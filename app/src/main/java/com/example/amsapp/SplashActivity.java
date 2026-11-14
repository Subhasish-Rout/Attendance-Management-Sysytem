package com.example.amsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String role = doc.getString("role");
                                navigateByRole(role);
                            } else {
                                goToLogin();
                            }
                        })
                        .addOnFailureListener(e -> goToLogin());
            } else {
                goToLogin();
            }
        }, 2000);
    }

    private void navigateByRole(String role) {
        if (role == null) { goToLogin(); return; }
        switch (role) {
            case "admin":
                startActivity(new Intent(this, AdminDashboardActivity.class));
                break;
            case "teacher":
                startActivity(new Intent(this, TeacherDashboardActivity.class));
                break;
            case "student":
                startActivity(new Intent(this, StudentDashboardActivity.class));
                break;
            default:
                goToLogin();
        }
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}