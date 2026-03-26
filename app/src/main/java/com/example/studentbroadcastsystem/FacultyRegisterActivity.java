package com.example.studentbroadcastsystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FacultyRegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_register);

        firebaseManager = FirebaseManager.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegister.setEnabled(false);
            firebaseManager.registerFaculty(email, password, new FirebaseManager.RegisterCallback() {
                @Override
                public void onResult(boolean success) {
                    runOnUiThread(() -> {
                        btnRegister.setEnabled(true);
                        if (success) {
                            Toast.makeText(FacultyRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to login
                        } else {
                            Toast.makeText(FacultyRegisterActivity.this, "Registration Failed (Email might exist)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        btnRegister.setEnabled(true);
                        Toast.makeText(FacultyRegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
