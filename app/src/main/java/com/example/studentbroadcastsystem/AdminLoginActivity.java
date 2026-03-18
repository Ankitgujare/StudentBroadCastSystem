package com.example.studentbroadcastsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        dbHelper = new DatabaseHelper(this);

        email = findViewById(R.id.adminEmail);
        password = findViewById(R.id.adminPassword);
        loginBtn = findViewById(R.id.adminLoginBtn);

        loginBtn.setOnClickListener(v -> {
            String adminEmail = email.getText().toString();
            String adminPassword = password.getText().toString();

            if (adminEmail.isEmpty() || adminPassword.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.checkUserLogin(adminEmail, adminPassword, "admin")) {
                SharedPreferences.Editor editor = getSharedPreferences("LoginStatus", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("role", "admin");
                editor.apply();

                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}