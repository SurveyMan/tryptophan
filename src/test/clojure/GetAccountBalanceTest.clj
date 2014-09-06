(ns GetAccountBalanceTest
  (:use clojure.test)
  (:use TestNS)
  (:use mturk.core)
  )

(reset! secretKey (:secret credentials))
(reset! accessKey (:access credentials))

(deftest get-account-balance-test
  []
  (let [balance (read-string (:Amount (getBalance)))]
    (is (> balance 0))
    )
  )