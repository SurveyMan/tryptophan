(ns TestNS)

;; a ".credentials" file is required to run the tests. It contains the secret key and the access key.
(def credentials (read-string (slurp ".credentials")))