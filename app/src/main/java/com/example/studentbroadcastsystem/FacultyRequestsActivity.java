package com.example.studentbroadcastsystem;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FacultyRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FacultyRequestAdapter adapter;
    private FirebaseManager firebaseManager;
    private String facultyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_requests);

        firebaseManager = FirebaseManager.getInstance();
        facultyEmail = getIntent().getStringExtra("FACULTY_EMAIL");

        recyclerView = findViewById(R.id.recyclerViewMyRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRequests();
    }

    private void loadRequests() {
        firebaseManager.getMessagesBySender(facultyEmail, new FirebaseManager.FetchMessagesCallback() {
            @Override
            public void onMessagesFetched(List<MessageModel> messages) {
                runOnUiThread(() -> {
                    adapter = new FacultyRequestAdapter(FacultyRequestsActivity.this, messages);
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(FacultyRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
