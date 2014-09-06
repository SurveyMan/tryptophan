package mturk;

//import mturk.core;
import java.util.Date;

public class Assignment {

    public enum AssignmentStatus { Submitted, Approved, Rejected; };
    
    private String assignmentId;
    private String workerId;
    private String HITId;
    private AssignmentStatus assignmentStatus;
    private Date autoApprovalTime;
    private Date acceptTime;
    private Date submitTime;
    private Date approvalTime;
    private Date rejectionTime;
    private Date deadline;
    private String answer;
    private String requesterFeedback;

    protected Assignment(String assignmentId,
		       String workerId,
		       String HITId,
		       AssignmentStatus assignmentStatus,
		       Date autoApprovalTime,
		       Date acceptTime,
		       Date submitTime,
		       Date approvalTime,
		       Date rejectionTime,
		       Date deadline,
		       String answer,
		       String requesterFeedback){
	this.assignmentId = assignmentId;
	this.workerId = workerId;
	this.HITId = HITId;
	this.assignmentStatus = assignmentStatus;
	this.autoApprovalTime = autoApprovalTime;
	this.acceptTime = acceptTime;
	this.submitTime = submitTime;
	this.approvalTime = approvalTime;
	this.rejectionTime = rejectionTime;
	this.deadline = deadline;
	this.answer = answer;
	this.requesterFeedback = requesterFeedback;
    }


    public static Assignment getAssignment(String assignmentId){
    	return null;
    }
    
}
