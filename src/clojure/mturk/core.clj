(ns mturk.core
  (:require [clojure.string :as s]
            [clj-http.client :as client])
  (:import [java.util Date SimpleTimeZone]
           [java.text SimpleDateFormat]
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
(def supported_operations '(ApproveAssignment))

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
  "Basic body of a mechanical turk request."
  ([op] (request op {}))
  ([op args]
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
       (client/get "https://mechanicalturk.amazonaws.com/?" {:query-params http-args}))))

;; (defn poll 
;;   "Wrapper for the request function that issues requests until we get a legit error or results"
;;   ([op] (poll op {}))
;;   ([op args] ))

;; ;; 
(defn getAssignment [aid] {})