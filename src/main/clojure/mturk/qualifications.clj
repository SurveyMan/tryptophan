(ns mturk.qualifications)

;; create a qualification class as a drop-in for the java sdk qualification class
;; this library should include instantiations of the qualification class for 
;; mturk's built-in quals

(defrecord QualificationTypeId [sandbox production])

(def Masters "2ARFPLSP75KLA8M8DH1HTEQVJT3SY6" "2F1QJWKUDD8XADTFD2Q0G6UTO95ALH")
(def CategorizationMasters "2F1KVCNHMVHV8E9PBUB2A4J79LU20F" "2NDP2L92HECWY8NS8H3CK0CP5L9GHO")
(def PhotoModerationMasters "2TGBB6BFMFFOM08IBMAFGGESC1UWJX" "21VZU98JHSTLZ5BPP4A9NOBJEK3DPG")
(def Worker_NumberHITsApproved "00000000000000000040" "00000000000000000040")
(def Worker_Locale "00000000000000000071" "00000000000000000071")
(def Worker_Adult "00000000000000000060" "00000000000000000060")
(def Worker_PercentAssignmentsApproved "000000000000000000L0" "000000000000000000L0")