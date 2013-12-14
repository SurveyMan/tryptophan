(ns mturk.assignment
  (:require [date-time])
  (:import [java.util Date])
  (:gen-class)
)

(def ^{:private true} assignmentStatusList '(:Submitted :Approved :Rejected))

(defreford Assignment [^String AssignmentId
                       ^String WorkerId
                       ^String HITId
                       ^Keyword AssignmentStatus
                       ^Date AutoApprovalTime 
                       ^Date AcceptTime
                       ^Date SubmitTime
                       ^Date ApprovalTime
                       ^Date RejectionTime 
                       ^Date Deadline
                       ^String Answer
                       ^String RequesterFeedback])


  