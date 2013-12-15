(ns mturk.Error
  (:require [clojure.data.xml :as xml])
  (:import [java.io StringReader])
  (:gen-class))

(def error-codes {:not-authorized "AWS.NotAuthorized"
                  })

(defprotocol ServiceError
  (request-time [time] "GET request time")
  (response-code [num] "HTTP response code")
  (date [datetime] "Request date")
  (server [s] "Server pinged")
  (operation [op] "AWS Operation")
  (request-id [id] "Request id")
  (aws-error-code [code] "AWS-issued error code")
  (aws-error-msg [msg] "AWS-issued error message"))

(defn parse-errors [{request-time :request-time
                    response-code :status
                    headers :headers
                    body :body}]
  (let [server (get headers "server")
        (xml/parse (StringReader. body))