package com.example.studentbroadcastsystem;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FacultyDashboardActivity extends AppCompatActivity {

    private TextView tvSelectedBranches, tvSelectedSemesters;
    private CheckBox cbIndividual;
    private EditText etIndividualEmail, etSubject, messageBox;
    private Button sendButton, btnViewMyRequests;

    private String[] branchesArray = {"BCA", "MCA"};
    private boolean[] selectedBranches;
    private ArrayList<Integer> branchList = new ArrayList<>();

    private String[] semestersArray = {"1", "2", "3", "4", "5", "6"};
    private boolean[] selectedSemesters;
    private ArrayList<Integer> semesterList = new ArrayList<>();

    private FirebaseManager firebaseManager;
    private String facultyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        firebaseManager = FirebaseManager.getInstance();
        facultyEmail = getIntent().getStringExtra("FACULTY_EMAIL");
        if (facultyEmail == null) {
            facultyEmail = "unknown_faculty@demo.com"; // Fallback
        }

        tvSelectedBranches = findViewById(R.id.tvSelectedBranches);
        tvSelectedSemesters = findViewById(R.id.tvSelectedSemesters);
        cbIndividual = findViewById(R.id.cbIndividual);
        etIndividualEmail = findViewById(R.id.etIndividualEmail);
        etSubject = findViewById(R.id.etSubject);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendButton);
        btnViewMyRequests = findViewById(R.id.btnViewMyRequests);

        Button btnLogout = findViewById(R.id.btnLogoutFaculty);
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("LoginStatus", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(FacultyDashboardActivity.this, MainActivity.class));
            finish();
        });

        btnViewMyRequests.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyDashboardActivity.this, FacultyRequestsActivity.class);
            intent.putExtra("FACULTY_EMAIL", facultyEmail);
            startActivity(intent);
        });

        selectedBranches = new boolean[branchesArray.length];
        tvSelectedBranches.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FacultyDashboardActivity.this);
            builder.setTitle("Select Branches");
            builder.setCancelable(false);

            builder.setMultiChoiceItems(branchesArray, selectedBranches, (dialog, which, isChecked) -> {
                if (isChecked) {
                    if (!branchList.contains(which)) branchList.add(which);
                } else {
                    branchList.remove(Integer.valueOf(which));
                }
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < branchList.size(); i++) {
                    stringBuilder.append(branchesArray[branchList.get(i)]);
                    if (i != branchList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                if (branchList.isEmpty()) {
                    tvSelectedBranches.setText("Tap to select branches");
                } else {
                    tvSelectedBranches.setText(stringBuilder.toString());
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.setNeutralButton("Clear All", (dialog, which) -> {
                for (int i = 0; i < selectedBranches.length; i++) {
                    selectedBranches[i] = false;
                }
                branchList.clear();
                tvSelectedBranches.setText("Tap to select branches");
            });

            builder.show();
        });

        selectedSemesters = new boolean[semestersArray.length];
        tvSelectedSemesters.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FacultyDashboardActivity.this);
            builder.setTitle("Select Semesters");
            builder.setCancelable(false);

            builder.setMultiChoiceItems(semestersArray, selectedSemesters, (dialog, which, isChecked) -> {
                if (isChecked) {
                    if (!semesterList.contains(which)) semesterList.add(which);
                } else {
                    semesterList.remove(Integer.valueOf(which));
                }
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < semesterList.size(); i++) {
                    stringBuilder.append(semestersArray[semesterList.get(i)]);
                    if (i != semesterList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                if (semesterList.isEmpty()) {
                    tvSelectedSemesters.setText("Tap to select semesters");
                } else {
                    tvSelectedSemesters.setText(stringBuilder.toString());
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.setNeutralButton("Clear All", (dialog, which) -> {
                for (int i = 0; i < selectedSemesters.length; i++) {
                    selectedSemesters[i] = false;
                }
                semesterList.clear();
                tvSelectedSemesters.setText("Tap to select semesters");
            });

            builder.show();
        });

        cbIndividual.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etIndividualEmail.setVisibility(View.VISIBLE);
                tvSelectedBranches.setEnabled(false);
                tvSelectedSemesters.setEnabled(false);
                tvSelectedBranches.setAlpha(0.5f);
                tvSelectedSemesters.setAlpha(0.5f);
            } else {
                etIndividualEmail.setVisibility(View.GONE);
                tvSelectedBranches.setEnabled(true);
                tvSelectedSemesters.setEnabled(true);
                tvSelectedBranches.setAlpha(1.0f);
                tvSelectedSemesters.setAlpha(1.0f);
                etIndividualEmail.setText("");
            }
        });

        sendButton.setOnClickListener(v -> {
            String msg = messageBox.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            if (msg.isEmpty()) {
                Toast.makeText(this, "Enter some message", Toast.LENGTH_SHORT).show();
                return;
            }
            if (subject.isEmpty()) {
                Toast.makeText(this, "Enter a subject", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isIndividual = cbIndividual.isChecked();
            String individualEmail = etIndividualEmail.getText().toString().trim();
            String finalBranch = "";
            String finalSemester = "";

            if (isIndividual) {
                if (individualEmail.isEmpty()) {
                    Toast.makeText(this, "Enter student email", Toast.LENGTH_SHORT).show();
                    return;
                }
                finalBranch = "N/A";
                finalSemester = "N/A";
            } else {
                if (branchList.isEmpty() || semesterList.isEmpty()) {
                    Toast.makeText(this, "Select at least one branch and semester", Toast.LENGTH_SHORT).show();
                    return;
                }
                finalBranch = tvSelectedBranches.getText().toString();
                finalSemester = tvSelectedSemesters.getText().toString();
            }

            sendButton.setEnabled(false);
            firebaseManager.addMessageRequest(facultyEmail, subject, msg, finalBranch, finalSemester, isIndividual, individualEmail, new FirebaseManager.AddMessageCallback() {
                @Override
                public void onResult(boolean success) {
                    runOnUiThread(() -> {
                        sendButton.setEnabled(true);
                        if (success) {
                            Toast.makeText(FacultyDashboardActivity.this, "Message Sent for Approval!", Toast.LENGTH_SHORT).show();
                            etSubject.setText("");
                            messageBox.setText("");
                            if (isIndividual) {
                                etIndividualEmail.setText("");
                            }
                        } else {
                            Toast.makeText(FacultyDashboardActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        sendButton.setEnabled(true);
                        Toast.makeText(FacultyDashboardActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}