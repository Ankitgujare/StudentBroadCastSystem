package com.example.studentbroadcastsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FacultyLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);

        firebaseManager = FirebaseManager.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            firebaseManager.checkUserLogin(email, password, "faculty", new FirebaseManager.LoginCallback() {
                @Override
                public void onResult(boolean success) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        if (success) {
                            SharedPreferences.Editor editor = getSharedPreferences("LoginStatus", MODE_PRIVATE).edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("role", "faculty");
                            editor.putString("email", email);
                            editor.apply();

                            Toast.makeText(FacultyLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FacultyLoginActivity.this, FacultyDashboardActivity.class);
                            intent.putExtra("FACULTY_EMAIL", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(FacultyLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        Toast.makeText(FacultyLoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(FacultyLoginActivity.this, FacultyRegisterActivity.class));
        });
    }
}