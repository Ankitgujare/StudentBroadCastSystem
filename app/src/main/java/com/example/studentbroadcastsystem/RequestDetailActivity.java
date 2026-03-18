package com.example.studentbroadcastsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RequestDetailActivity extends AppCompatActivity {

    private TextView tvFacultyEmail, tvBranch, tvSemester, tvMessageContent;
    private Button btnApproveSend;
    private DatabaseHelper dbHelper;
    private MessageModel currentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        dbHelper = new DatabaseHelper(this);

        tvFacultyEmail = findViewById(R.id.tvFacultyEmail);
        tvBranch = findViewById(R.id.tvBranch);
        tvSemester = findViewById(R.id.tvSemester);
        tvMessageContent = findViewById(R.id.tvMessageContent);
        btnApproveSend = findViewById(R.id.btnApproveSend);

        int messageId = getIntent().getIntExtra("MESSAGE_ID", -1);
        if (messageId == -1) {
            Toast.makeText(this, "Invalid Message ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentMessage = dbHelper.getMessageById(messageId);
        if (currentMessage == null) {
            Toast.makeText(this, "Message not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvFacultyEmail.setText("Faculty: " + currentMessage.getSenderEmail());
        tvBranch.setText("Branch: " + currentMessage.getBranch());
        tvSemester.setText("Semester: " + currentMessage.getSemester());
        tvMessageContent.setText(currentMessage.getContent());

        btnApproveSend.setOnClickListener(v -> {
            List<String> studentEmails = dbHelper.getStudentEmails(currentMessage.getBranch(), currentMessage.getSemester());

            if (studentEmails.isEmpty()) {
                Toast.makeText(this, "No students found for this Branch and Semester", Toast.LENGTH_SHORT).show();
            }

            dbHelper.approveMessage(currentMessage.getId());
            Toast.makeText(this, "Message Approved", Toast.LENGTH_SHORT).show();

            if (!studentEmails.isEmpty()) {
                String[] bccArray = studentEmails.toArray(new String[0]);
                
                String formattedMessage = "Hello Students,\n" +
                        "You have a new broadcast message.\n" +
                        "From: " + currentMessage.getSenderEmail() + "\n" +
                        "Branch: " + currentMessage.getBranch() + "\n" +
                        "Semester: " + currentMessage.getSemester() + "\n" +
                        "Message:\n" + currentMessage.getContent() + "\n" +
                        "Best Regards";

                JavaMailAPI javaMailAPI = new JavaMailAPI(
                        RequestDetailActivity.this, 
                        bccArray, 
                        "New Broadcast Alert: " + currentMessage.getBranch() + " Sem " + currentMessage.getSemester(), 
                        formattedMessage
                );
                javaMailAPI.execute();
            }
            
            finish();
        });
    }
}
