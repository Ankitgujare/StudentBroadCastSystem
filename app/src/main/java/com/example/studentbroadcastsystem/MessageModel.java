package com.example.studentbroadcastsystem;

public class MessageModel {
    private int id;
    private String senderEmail;
    private String subject;
    private String content;
    private String branch;
    private String semester;
    private String status;
    private boolean isIndividual;
    private String individualEmail;
    private String rejectionReason;
    private long timestamp;

    public MessageModel(int id, String senderEmail, String subject, String content, String branch, String semester, String status, boolean isIndividual, String individualEmail, String rejectionReason, long timestamp) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.content = content;
        this.branch = branch;
        this.semester = semester;
        this.status = status;
        this.isIndividual = isIndividual;
        this.individualEmail = individualEmail;
        this.rejectionReason = rejectionReason;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getSenderEmail() { return senderEmail; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public String getBranch() { return branch; }
    public String getSemester() { return semester; }
    public String getStatus() { return status; }
    public boolean isIndividual() { return isIndividual; }
    public String getIndividualEmail() { return individualEmail; }
    public String getRejectionReason() { return rejectionReason; }
    public long getTimestamp() { return timestamp; }

    public void setStatus(String status) { this.status = status; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setContent(String content) { this.content = content; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
