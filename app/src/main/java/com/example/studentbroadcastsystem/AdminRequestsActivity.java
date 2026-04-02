package com.example.studentbroadcastsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProcessedRequestAdapter adapter;
    private FirebaseManager firebaseManager;
    private TextView tvFilterDate;
    private Button btnClearFilter;
    
    private List<MessageModel> allProcessedMessages = new ArrayList<>();
    private Calendar selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requests);

        firebaseManager = FirebaseManager.getInstance();

        recyclerView = findViewById(R.id.recyclerViewProcessedRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvFilterDate = findViewById(R.id.tvFilterDate);
        btnClearFilter = findViewById(R.id.btnClearFilter);

        adapter = new ProcessedRequestAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        tvFilterDate.setOnClickListener(v -> showDatePicker());

        btnClearFilter.setOnClickListener(v -> {
            selectedDate = null;
            tvFilterDate.setText("Tap to filter by Date");
            filterMessages();
        });

        loadProcessedRequests();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        if (selectedDate != null) {
            c.setTimeInMillis(selectedDate.getTimeInMillis());
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    if (selectedDate == null) {
                        selectedDate = Calendar.getInstance();
                    }
                    selectedDate.set(year1, monthOfYear, dayOfMonth, 0, 0, 0);
                    tvFilterDate.setText("Filtered by: " + String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1));
                    filterMessages();
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void loadProcessedRequests() {
        firebaseManager.getProcessedMessages(new FirebaseManager.FetchMessagesCallback() {
            @Override
            public void onMessagesFetched(List<MessageModel> messages) {
                runOnUiThread(() -> {
                    allProcessedMessages = messages;
                    filterMessages();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(AdminRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void filterMessages() {
        if (selectedDate == null) {
            adapter.updateList(allProcessedMessages);
            return;
        }

        List<MessageModel> filteredList = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String selectedDateStr = fmt.format(selectedDate.getTime());

        for (MessageModel msg : allProcessedMessages) {
            if (msg.getTimestamp() > 0) {
                String msgDateStr = fmt.format(new Date(msg.getTimestamp()));
                if (selectedDateStr.equals(msgDateStr)) {
                    filteredList.add(msg);
                }
            }
        }
        adapter.updateList(filteredList);
    }
}
