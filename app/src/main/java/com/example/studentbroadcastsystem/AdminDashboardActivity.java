package com.example.studentbroadcastsystem;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PendingRequestAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        firebaseManager = FirebaseManager.getInstance();
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnLogout = findViewById(R.id.btnLogoutAdmin);
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("LoginStatus", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
            finish();
        });
        Button btnViewProcessedRequests = findViewById(R.id.btnViewProcessedRequests);
        btnViewProcessedRequests.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminRequestsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPendingRequests();
    }

    private void loadPendingRequests() {
        firebaseManager.getPendingMessages(new FirebaseManager.FetchMessagesCallback() {
            @Override
            public void onMessagesFetched(List<MessageModel> messages) {
                runOnUiThread(() -> {
                    adapter = new PendingRequestAdapter(AdminDashboardActivity.this, messages);
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // Handle error silently or show toast
                });
            }
        });
    }
}