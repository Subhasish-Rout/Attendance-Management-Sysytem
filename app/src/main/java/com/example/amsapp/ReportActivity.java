package com.example.amsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ReportAdapter adapter;
    private List<AttendanceRecord> recordList = new ArrayList<>();
    private ProgressBar progressBar;
    private EditText etStartDate, etEndDate;
    private TextView tvSummary;
    private String currentUserRole = "";
    private String currentUserUid  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db          = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate   = findViewById(R.id.etEndDate);
        tvSummary   = findViewById(R.id.tvSummary);
        Button btnFilter  = findViewById(R.id.btnFilter);
        Button btnWeekly  = findViewById(R.id.btnWeekly);
        Button btnMonthly = findViewById(R.id.btnMonthly);
        RecyclerView rv   = findViewById(R.id.rvReport);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new ReportAdapter(recordList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        db.collection("users").document(currentUserUid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentUserRole = doc.getString("role");
                        loadAllAttendance();
                    }
                });

        btnFilter.setOnClickListener(v -> {
            String start = etStartDate.getText().toString().trim();
            String end   = etEndDate.getText().toString().trim();
            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Enter start and end date",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            loadAttendanceByRange(start, end);
        });

        btnWeekly.setOnClickListener(v  -> loadWeekly());
        btnMonthly.setOnClickListener(v -> loadMonthly());
    }

    private void loadAllAttendance() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = db.collection("attendance")
                .orderBy("date", Query.Direction.DESCENDING);

        if ("student".equals(currentUserRole)) {
            query = query.whereEqualTo("studentUid", currentUserUid);
        }

        query.get().addOnSuccessListener(qs -> {
            recordList.clear();
            for (var doc : qs) {
                recordList.add(doc.toObject(AttendanceRecord.class));
            }
            adapter.notifyDataSetChanged();
            updateSummary();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void loadAttendanceByRange(String start, String end) {
        progressBar.setVisibility(View.VISIBLE);
        Query query = db.collection("attendance")
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .orderBy("date", Query.Direction.DESCENDING);

        if ("student".equals(currentUserRole)) {
            query = db.collection("attendance")
                    .whereEqualTo("studentUid", currentUserUid)
                    .whereGreaterThanOrEqualTo("date", start)
                    .whereLessThanOrEqualTo("date", end);
        }

        query.get().addOnSuccessListener(qs -> {
            recordList.clear();
            for (var doc : qs) {
                recordList.add(doc.toObject(AttendanceRecord.class));
            }
            adapter.notifyDataSetChanged();
            updateSummary();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void loadWeekly() {
        Calendar cal = Calendar.getInstance();
        String end   = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String start = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(cal.getTime());
        etStartDate.setText(start);
        etEndDate.setText(end);
        loadAttendanceByRange(start, end);
    }

    private void loadMonthly() {
        Calendar cal = Calendar.getInstance();
        String end   = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        String start = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(cal.getTime());
        etStartDate.setText(start);
        etEndDate.setText(end);
        loadAttendanceByRange(start, end);
    }

    private void updateSummary() {
        int total   = recordList.size();
        int present = 0;
        for (AttendanceRecord r : recordList) {
            if ("Present".equals(r.getStatus())) present++;
        }
        int absent = total - present;
        tvSummary.setText("Total: " + total
                + "  |  Present: " + present
                + "  |  Absent: " + absent);
    }
}