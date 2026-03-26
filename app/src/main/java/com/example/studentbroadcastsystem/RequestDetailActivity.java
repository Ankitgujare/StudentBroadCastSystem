package com.example.studentbroadcastsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RequestDetailActivity extends AppCompatActivity {

    private TextView tvFacultyEmail;
    private EditText etBranch, etSemester, etMessageContent, etRejectionReason;
    private Button btnApproveSend, btnReject;
    private FirebaseManager firebaseManager;
    private MessageModel currentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        firebaseManager = FirebaseManager.getInstance();

        tvFacultyEmail = findViewById(R.id.tvFacultyEmail);
        etBranch = findViewById(R.id.etBranch);
        etSemester = findViewById(R.id.etSemester);
        etMessageContent = findViewById(R.id.etMessageContent);
        etRejectionReason = findViewById(R.id.etRejectionReason);
        btnApproveSend = findViewById(R.id.btnApproveSend);
        btnReject = findViewById(R.id.btnReject);

        int messageId = getIntent().getIntExtra("MESSAGE_ID", -1);
        if (messageId == -1) {
            Toast.makeText(this, "Invalid Message ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseManager.getMessageById(messageId, new FirebaseManager.FetchMessagesCallback() {
            @Override
            public void onMessagesFetched(List<MessageModel> messages) {
                runOnUiThread(() -> {
                    if (messages.isEmpty()) {
                        Toast.makeText(RequestDetailActivity.this, "Message not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    
                    currentMessage = messages.get(0);
                    tvFacultyEmail.setText("Faculty: " + currentMessage.getSenderEmail());
                    
                    if (currentMessage.isIndividual()) {
                        etBranch.setText("Individual Student");
                        etSemester.setText(currentMessage.getIndividualEmail());
                        etBranch.setEnabled(false);
                        etSemester.setEnabled(false);
                    } else {
                        etBranch.setText(currentMessage.getBranch());
                        etSemester.setText(currentMessage.getSemester());
                    }
                    
                    etMessageContent.setText(currentMessage.getContent());
                    
                    btnApproveSend.setOnClickListener(v -> handleApproveAndSend());
                    btnReject.setOnClickListener(v -> handleReject());
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(RequestDetailActivity.this, "Error fetching message", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void handleReject() {
        String reason = etRejectionReason.getText().toString().trim();
        if (reason.isEmpty()) {
            Toast.makeText(this, "Please enter a reason for rejection", Toast.LENGTH_SHORT).show();
            return;
        }
        btnReject.setEnabled(false);
        btnApproveSend.setEnabled(false);
        
        firebaseManager.rejectMessage(currentMessage.getId(), reason, new FirebaseManager.MessageActionCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(RequestDetailActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    btnReject.setEnabled(true);
                    btnApproveSend.setEnabled(true);
                    Toast.makeText(RequestDetailActivity.this, "Failed to reject: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handleApproveAndSend() {
        btnApproveSend.setEnabled(false);
        btnReject.setEnabled(false);
        Toast.makeText(this, "Approving message...", Toast.LENGTH_SHORT).show();
        
        // Save edits
        currentMessage.setContent(etMessageContent.getText().toString().trim());
        if (!currentMessage.isIndividual()) {
            currentMessage.setBranch(etBranch.getText().toString().trim());
            currentMessage.setSemester(etSemester.getText().toString().trim());
        }

        firebaseManager.updateMessage(currentMessage, new FirebaseManager.MessageActionCallback() {
            @Override
            public void onSuccess() {
                firebaseManager.approveMessage(currentMessage.getId(), new FirebaseManager.MessageActionCallback() {
                    @Override
                    public void onSuccess() {
                        sendEmailExecution();
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            btnApproveSend.setEnabled(true);
                            btnReject.setEnabled(true);
                            Toast.makeText(RequestDetailActivity.this, "Approval failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    btnApproveSend.setEnabled(true);
                    btnReject.setEnabled(true);
                    Toast.makeText(RequestDetailActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendEmailExecution() {
        if (currentMessage.isIndividual()) {
            String[] bccArray = {currentMessage.getIndividualEmail()};
            sendActualEmail(bccArray);
        } else {
            firebaseManager.getStudentEmails(currentMessage.getBranch(), currentMessage.getSemester(), new FirebaseManager.FetchStudentsCallback() {
                @Override
                public void onStudentsFetched(List<String> emails) {
                    runOnUiThread(() -> {
                        if (emails.isEmpty()) {
                            Toast.makeText(RequestDetailActivity.this, "No students found for this Branch and Semester", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String[] bccArray = emails.toArray(new String[0]);
                            sendActualEmail(bccArray);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(RequestDetailActivity.this, "Approved but failed to fetch students.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }

    private void sendActualEmail(String[] bccArray) {
        String formattedMessage = "Hello Student(s),\n\n" +
                "You have a new broadcast message.\n\n" +
                "From: " + currentMessage.getSenderEmail() + "\n" +
                "Message:\n" + currentMessage.getContent() + "\n\n" +
                "Best Regards";

        String subject = "New Broadcast Alert";
        if (!currentMessage.isIndividual()) {
            subject = "New Broadcast Alert: " + currentMessage.getBranch() + " Sem " + currentMessage.getSemester();
        }

        JavaMailAPI javaMailAPI = new JavaMailAPI(
                getApplicationContext(), 
                bccArray, 
                subject, 
                formattedMessage
        );
        javaMailAPI.execute();
        Toast.makeText(getApplicationContext(), "Message Approved and Email Scheduled", Toast.LENGTH_SHORT).show();
        finish();
    }
}
