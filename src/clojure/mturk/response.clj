(ns mturk.response
  (:require [clojure.data.xml :as xml])
  (:import [java.io StringReader]
           [java.text SimpleDateFormat])
  (:gen-class))

(defprotocol ServiceResponse
  (request-time [this] "GET request time")
  (response-code [this] "HTTP response code")
  (date [this] "Request date")
  (server [this] "Server pinged")
  (operation [this] "AWS Operation")
  (request-id [this] "Request id")
  (aws-error-code [this] "AWS-issued error code, if this request is in error")
  (aws-error-msg [this] "AWS-issued error message, if this request is in error")
  (response-content [this] "XML response"))

(deftype ResponseError [time status datetime servername elts errors]
  ServiceResponse
  (request-time [this] time)
  (response-code [this] (int status))
  (date [this] datetime) 
  (server [this] servername)
  (operation [this] (:tag elts))
  (request-id [this] (nth (iterate #(first (:content %)) elts) 3))
  (aws-error-code [this] (nth (iterate #(first (:content %)) (first errors))))
  (aws-error-msg [this] (-> (first errors)
                            (#(second (:content %)))
                            (#(first (:conetnt %)))))
  (response-content [this] nil))

(deftype ResponseData [time status datetime servername content]
  ServiceResponse
  (request-time [this] time)
  (response-code [this] (int status))
  (date [this] datetime)
  (server [this] servername)
  (operation [this] (:tag content))
  (request-id [this] (nth (iterate #(first (:content %)) content) 3))
  (aws-error-code [this] nil)
  (aws-error-msg [this] nil)
  (response-content [this] content))

(def fatal-error-codes 
  "These errors should halt computation and throw an error"
  {:not-authorized "AWS.NotAuthorized"
   })

(def retry-error-codes 
  "These errors should retry until we time out."
  {:bad-claims-supplied "AWS.BadClaimsSupplied"
   :service-unavailable "AWS.ServiceUnavailable"
   })

(defn assert-one-error [errors]
  (assert (< (count errors) 2) 
          "AWS response returned more than one error. (Not currently supported)."))

(defn assert-supported-errors [txt]
  (assert (not (.contains txt "Error"))
          "The error response is not contained in fatal-error-codes or retry-error-codes"))

(defn isError [body]
  "If the response is in error, returns the error type. Otherwise return nil."
  (let [merged-errors (seq (merge fatal-error-codes retry-error-codes))
        aws-codes (map second merged-errors)
        inverted-map (zipmap aws-codes (map first merged-errors))
        errors (map #(and (.contains body %) %) aws-codes)
        error-types (filter #(not (false? %)) errors)]
    (assert-one-error error-types)
    (if (empty? error-types) (assert-supported-errors body))
    (first error-types)))

(defn make-response-type
  "Takes in a request response and returns an error. If the content is not an error, returns nil."
  [{time :request-time 
    status :status 
    headers :headers
    body :body}]
  (let [elts (xml/parse (StringReader. body))
        date (-> (SimpleDateFormat. "EEE, dd MMM yyyy HH:mm:ss zzz") 
                 (.parse (get headers "date")))
        server (get headers "server")]
    (if (isError body)
      (let [errors (-> (first (:content elts))
                       (#(second (:content %)))
                       (:content))]
        (assert-one-error errors)
        (ResponseError. time status date server elts errors))
      (ResponseData. time status date server elts))))
