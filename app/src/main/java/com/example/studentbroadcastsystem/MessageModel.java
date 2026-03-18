package com.example.studentbroadcastsystem;

public class MessageModel {
    private int id;
    private String senderEmail;
    private String content;
    private String branch;
    private String semester;
    private String status;

    public MessageModel(int id, String senderEmail, String content, String branch, String semester, String status) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.content = content;
        this.branch = branch;
        this.semester = semester;
        this.status = status;
    }

    public int getId() { return id; }
    public String getSenderEmail() { return senderEmail; }
    public String getContent() { return content; }
    public String getBranch() { return branch; }
    public String getSemester() { return semester; }
    public String getStatus() { return status; }
}
