(ns mturk
  (:import '(java.util Date SimpleTimeZone))
  (:import '(java.text SimpleDateFormat))
  (:import '(javax.crypto Mac))
  (:import '(javax.crypto.spec SecretKeySpec))
  (:import '(org.apache.commons.codec.binary Base64))
  )

(def secretKey (atom nil))
(def accessKey (atom nil))
(def- service 'AWSMechanicalTurkRequester)
(def- timeFormat "%Y-%m-%dT%H:%M:%SZ")
(def- shaAlgo "HmacSH1")
(def- version "2012-03-25")
(def supported_operations '(ApproveAssignment))

(def UTCDateString []
  (let [dformat (SimpleDateFormat. timeFormat)
        tz (SimpleTimeZone/getTimeZone "UTC")
        date (Date.)]
    (.setTimeZone dformat tz)
    (.format dformat date)
    )
  )

(defmacro request
  "Basic body of a mechanical turk request."
  [op args]
  (let [timestamp UTCDateString()
        msg (str service op timestamp)
        sha (SecretKeySpec. (.getBytes @secretKey) shaAlgo)
        raw (doto (Mac/getInstance shaAlgo) (init sha) (.doFinal (.getBytes msg)))
        sig (doto (Base64. true) (.encodeBase64 raw))]
    (client/get "https://mechanicalturk.amazonaws.com"
                (merge args
                       {:Service service
                        :AWSAccessKeyId @accessKey
                        :Version version
                        :Operation op
                        :Signature sig
                        :Timestamp timestamp}))))
