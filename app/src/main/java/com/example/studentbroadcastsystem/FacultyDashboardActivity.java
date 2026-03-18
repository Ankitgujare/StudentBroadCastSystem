package com.example.studentbroadcastsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacultyDashboardActivity extends AppCompatActivity {

    private Spinner spinnerBranch, spinnerSemester;
    private EditText messageBox;
    private Button sendButton;
    private DatabaseHelper dbHelper;
    private String facultyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        dbHelper = new DatabaseHelper(this);
        facultyEmail = getIntent().getStringExtra("FACULTY_EMAIL");
        if (facultyEmail == null) {
            facultyEmail = "unknown_faculty@demo.com"; // Fallback
        }

        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendButton);

        Button btnLogout = findViewById(R.id.btnLogoutFaculty);
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("LoginStatus", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(FacultyDashboardActivity.this, MainActivity.class));
            finish();
        });

        // Setup Branch Spinner
        List<String> branches = Arrays.asList("BCA", "MCA");
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branches);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branchAdapter);

        // Setup Semester Spinner
        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBranch = branches.get(position);
                List<String> semesters = new ArrayList<>();
                if (selectedBranch.equals("BCA")) {
                    semesters.addAll(Arrays.asList("1", "2", "3", "4", "5", "6"));
                } else if (selectedBranch.equals("MCA")) {
                    semesters.addAll(Arrays.asList("1", "2", "3", "4"));
                }
                ArrayAdapter<String> semAdapter = new ArrayAdapter<>(FacultyDashboardActivity.this, android.R.layout.simple_spinner_item, semesters);
                semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSemester.setAdapter(semAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        sendButton.setOnClickListener(v -> {
            String msg = messageBox.getText().toString().trim();
            if (msg.isEmpty()) {
                Toast.makeText(this, "Enter some message", Toast.LENGTH_SHORT).show();
                return;
            }

            String branch = (String) spinnerBranch.getSelectedItem();
            String semester = (String) spinnerSemester.getSelectedItem();

            boolean success = dbHelper.addMessageRequest(facultyEmail, msg, branch, semester);

            if (success) {
                Toast.makeText(this, "Message Sent for Approval!", Toast.LENGTH_SHORT).show();
                messageBox.setText("");
            } else {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }
}