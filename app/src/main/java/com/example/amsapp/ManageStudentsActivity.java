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

public class ManageStudentsActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etRoll, etClass;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Student> studentList = new ArrayList<>();
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        db          = FirebaseFirestore.getInstance();
        mAuth       = FirebaseAuth.getInstance();
        etName      = findViewById(R.id.etStudentName);
        etEmail     = findViewById(R.id.etStudentEmail);
        etPassword  = findViewById(R.id.etStudentPassword);
        etRoll      = findViewById(R.id.etRollNumber);
        etClass     = findViewById(R.id.etClassName);
        progressBar = findViewById(R.id.progressBar);
        Button btnAdd = findViewById(R.id.btnAddStudent);
        RecyclerView rv = findViewById(R.id.rvStudents);

        adapter = new StudentAdapter(studentList, this::onStudentAction);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadStudents();
        btnAdd.setOnClickListener(v -> addStudent());
    }

    private void loadStudents() {
        db.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    studentList.clear();
                    for (var doc : querySnapshot) {
                        Student u = doc.toObject(Student.class);
                        u.setUid(doc.getId());
                        studentList.add(u);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void addStudent() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();
        String roll  = etRoll.getText().toString().trim();
        String cls   = etClass.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()
                || roll.isEmpty() || cls.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
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
                    data.put("role", "student");
                    data.put("classname", cls);
                    data.put("rollNumber", roll);

                    db.collection("users").document(uid).set(data)
                            .addOnSuccessListener(unused -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Student added",
                                        Toast.LENGTH_SHORT).show();
                                etName.setText(""); etEmail.setText("");
                                etPassword.setText(""); etRoll.setText("");
                                etClass.setText("");
                                loadStudents();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // Fixed: added String action parameter to match StudentAdapter.OnActionListener
    private void onStudentAction(Student user, String action) {
        if ("delete".equals(action)) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Student")
                    .setMessage("Delete " + user.getName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.collection("users").document(user.getUid()).delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Student deleted",
                                            Toast.LENGTH_SHORT).show();
                                    loadStudents();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        // "qr" action — ManageStudentsActivity mein QR ki zaroorat nahi
        // agar chahiye toh batao, add kar dunga
    }
}