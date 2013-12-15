(ns mturk.core
  (:require [clojure.string :as s]
            [clj-http.client :as client]
            [clojure.data.xml :as xml]
            [mturk.response :as response]
             )
  (:import [java.util Date SimpleTimeZone UUID]
           [java.text SimpleDateFormat]
           [java.io StringReader]
           [javax.crypto Mac]
           [javax.crypto.spec SecretKeySpec]
           [org.apache.commons.codec.binary Base64]
           )
  (:gen-class
   :name mturk.core
   :methods [ [getAssignment [java.lang.String] java.util.Map]
             ]
  )
)

;; Put these into an object with the proper security settings
;; For now, they are just text strings
(def secretKey (atom nil))
(def accessKey (atom nil))
(def maxTimeOut (atom (* 1000 60 2)))

(def ^{:private true} service 'AWSMechanicalTurkRequester)
(def ^{:private true} timeFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")
(def ^{:private true} shaAlgo "HmacSHA1")
(def ^{:private true} version "2012-03-25")
(def supported-operations '(ApproveAssignment))

(defn ^{:private true} uuid 
  "Generates a uuid to be used for non-idempotent requests to mturk"
  [op]
  (if (contains? #{'CreateHIT 'GrantBonus 'ExtendHIT} op)
    (-> (UUID/randomUUID) (.toString))))

(defn load-keys-from-file [filename]
  (let [ [[_ access] [_ secret]] (map #(s/split % #"=") (s/split-lines (slurp filename)))]
    (reset! secretKey secret)
    (reset! accessKey access))
  'ok)

(defn UTCDateString []
  (let [dformat (SimpleDateFormat. timeFormat)
        tz (SimpleTimeZone/getTimeZone "UTC")
        date (Date.)]
    (.setTimeZone dformat tz)
    (.format dformat date)
    )
  )

(defn request
  "Basic body of a mechanical turk request. Returns false if secretKey or accessKey are not set. Returns the XML of a response request, which may be an error or response data."
  ([op] (request op {}))
  ([op args]
     (and (not (nil? @accessKey))
          (not (nil? @secretKey))
          (let [timestamp (UTCDateString)
                msg (str service op timestamp)
                signK (SecretKeySpec. (.getBytes @secretKey) shaAlgo)
               raw (-> (doto (Mac/getInstance shaAlgo) (.init signK))
                       (.doFinal (.getBytes msg)))
                sig (-> (Base64/encodeBase64 raw) (String. "UTF-8"))
                http-args (merge args
                                 {:Service service
                                  :AWSAccessKeyId @accessKey
                                  :Version version
                                  :Operation op
                                  :Signature sig
                                  :Timestamp timestamp})]
            (print http-args)
            (client/get "https://mechanicalturk.amazonaws.com/?" {:query-params http-args})))))

;; possible errors
;; java exceptions 
;; - java.net.UnknownHostException
;; xml responses
;; - AWS.NotAuthorized
;; - AWS.BadClaimsSupplied -- retry this (looks like this mostly happens in python when the times are off)
;; - AWS.ServiceUnavailable -- retry, but use unique token for CreateHIT, GrantBonus, and ExtendHIT


(defn poll 
  "Wrapper for the request function that issues requests until we get a legit error or results"
  ([op] (poll op {}))
  ([op args]
     (let [id (uuid op)]
       (loop [time 2]
         (let [resp-map (request op (merge args (if id {:UniqueRequestToken id})))
               resp-type (response/make-response-type resp-map)]
           (if-let [err (response/isError (:body resp-map))]
             (if (contains? response/retry-error-codes err)
               (if (< time maxTimeOut)
                 (recur (* time 2))
                 (throw (Exception. (str "Exceeded max timeout of " maxTimeOut))))
               (throw (Exception. err)))
             resp-type))))))
       

;; ;; 
(defn getAssignment [aid] {})