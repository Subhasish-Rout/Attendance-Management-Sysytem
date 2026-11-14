package com.example.amsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists())
                        tvWelcome.setText("Welcome, " + doc.getString("name"));
                });

        findViewById(R.id.btnScanQR).setOnClickListener(v ->
                startActivity(new Intent(this, ScannerActivity.class)));

        findViewById(R.id.btnManualAttendance).setOnClickListener(v ->
                startActivity(new Intent(this, ManualAttendanceActivity.class)));

        findViewById(R.id.btnViewReports).setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}