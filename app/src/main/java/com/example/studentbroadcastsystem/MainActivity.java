package com.example.studentbroadcastsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button adminBtn, facultyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("LoginStatus", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");

        if (isLoggedIn) {
            if ("admin".equals(role)) {
                startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                finish();
                return;
            } else if ("faculty".equals(role)) {
                Intent intent = new Intent(MainActivity.this, FacultyDashboardActivity.class);
                intent.putExtra("FACULTY_EMAIL", prefs.getString("email", ""));
                startActivity(intent);
                finish();
                return;
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Database to ensure tables are created for App Inspector
        new DatabaseHelper(this).getWritableDatabase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect buttons with XML
        adminBtn = findViewById(R.id.adminBtn);
        facultyBtn = findViewById(R.id.facultyBtn);

        // Admin button click
        adminBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        // Faculty button click
        facultyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FacultyLoginActivity.class);
            startActivity(intent);
        });

    }
}