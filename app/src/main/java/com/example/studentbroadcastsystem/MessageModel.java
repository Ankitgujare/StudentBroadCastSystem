package com.example.studentbroadcastsystem;

public class MessageModel {
    private int id;
    private String senderEmail;
    private String content;
    private String branch;
    private String semester;
    private String status;
    private boolean isIndividual;
    private String individualEmail;
    private String rejectionReason;

    public MessageModel(int id, String senderEmail, String content, String branch, String semester, String status, boolean isIndividual, String individualEmail, String rejectionReason) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.content = content;
        this.branch = branch;
        this.semester = semester;
        this.status = status;
        this.isIndividual = isIndividual;
        this.individualEmail = individualEmail;
        this.rejectionReason = rejectionReason;
    }

    public int getId() { return id; }
    public String getSenderEmail() { return senderEmail; }
    public String getContent() { return content; }
    public String getBranch() { return branch; }
    public String getSemester() { return semester; }
    public String getStatus() { return status; }
    public boolean isIndividual() { return isIndividual; }
    public String getIndividualEmail() { return individualEmail; }
    public String getRejectionReason() { return rejectionReason; }

    public void setStatus(String status) { this.status = status; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setContent(String content) { this.content = content; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setSemester(String semester) { this.semester = semester; }
}
