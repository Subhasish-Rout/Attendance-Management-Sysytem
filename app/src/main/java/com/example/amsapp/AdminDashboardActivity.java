package com.example.amsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvWelcome.setText("Welcome, " + doc.getString("name"));
                    }
                });

        findViewById(R.id.btnManageTeachers).setOnClickListener(v ->
                startActivity(new Intent(this, ManageTeachersActivity.class)));

        findViewById(R.id.btnManageStudents).setOnClickListener(v ->
                startActivity(new Intent(this, ManageStudentsActivity.class)));

        findViewById(R.id.btnViewReports).setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}