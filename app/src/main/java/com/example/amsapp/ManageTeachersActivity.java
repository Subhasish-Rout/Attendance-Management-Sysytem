package com.example.amsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTeachersActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Student> teacherList = new ArrayList<>();
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teachers);

        db          = FirebaseFirestore.getInstance();
        mAuth       = FirebaseAuth.getInstance();
        etName      = findViewById(R.id.etTeacherName);
        etEmail     = findViewById(R.id.etTeacherEmail);
        etPassword  = findViewById(R.id.etTeacherPassword);
        progressBar = findViewById(R.id.progressBar);
        Button btnAdd = findViewById(R.id.btnAddTeacher);
        RecyclerView rv = findViewById(R.id.rvTeachers);

        adapter = new StudentAdapter(teacherList, this::onTeacherAction);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadTeachers();
        btnAdd.setOnClickListener(v -> addTeacher());
    }

    private void loadTeachers() {
        db.collection("users")
                .whereEqualTo("role", "teacher")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    teacherList.clear();
                    for (var doc : querySnapshot) {
                        Student u = doc.toObject(Student.class);
                        u.setUid(doc.getId());
                        teacherList.add(u);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void addTeacher() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be 6+ characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", uid);
                    data.put("name", name);
                    data.put("email", email);
                    data.put("role", "teacher");
                    data.put("classname", "");
                    data.put("rollNumber", "");

                    db.collection("users").document(uid).set(data)
                            .addOnSuccessListener(unused -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Teacher added",
                                        Toast.LENGTH_SHORT).show();
                                etName.setText(""); etEmail.setText("");
                                etPassword.setText("");
                                loadTeachers();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // Fixed: added String action parameter to match StudentAdapter.OnActionListener
    private void onTeacherAction(Student user, String action) {
        if ("delete".equals(action)) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Teacher")
                    .setMessage("Delete " + user.getName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.collection("users").document(user.getUid()).delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Teacher deleted",
                                            Toast.LENGTH_SHORT).show();
                                    loadTeachers();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}