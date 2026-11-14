package com.example.amsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {

    private EditText etName, etRoll, etClass;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        db = FirebaseFirestore.getInstance();

        etName  = findViewById(R.id.etStudentName);
        etRoll  = findViewById(R.id.etRollNumber);
        etClass = findViewById(R.id.etClassName);
        RecyclerView rvStudents = findViewById(R.id.rvStudents);
        Button btnAdd = findViewById(R.id.btnAddStudent);

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList, this::onStudentAction);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);

        loadStudents();

        btnAdd.setOnClickListener(v -> addStudent());
    }

    private void loadStudents() {
        db.collection("students")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    studentList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        Student s = doc.toObject(Student.class);
                        s.setUid(doc.getId());
                        studentList.add(s);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load students: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void addStudent() {
        String name = etName.getText().toString().trim();
        String roll = etRoll.getText().toString().trim();
        String cls  = etClass.getText().toString().trim();

        if (name.isEmpty() || roll.isEmpty() || cls.isEmpty()) {
            Toast.makeText(this, "All fields are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if roll number already exists
        db.collection("students")
                .whereEqualTo("rollNumber", roll)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    if (!querySnapshots.isEmpty()) {
                        Toast.makeText(this, "Roll number already exists",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Create new student object
                        Student newStudent = new Student("", name, "", "student", cls, roll);

                        db.collection("students")
                                .add(newStudent)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(this, "Student added successfully",
                                            Toast.LENGTH_SHORT).show();
                                    etName.setText("");
                                    etRoll.setText("");
                                    etClass.setText("");
                                    loadStudents();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to add student: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking roll number: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void onStudentAction(Student student, String action) {
        if ("qr".equals(action)) {
            Intent intent = new Intent(this, QRGenerateActivity.class);
            intent.putExtra("roll",  student.getRollNumber());
            intent.putExtra("name",  student.getName());
            intent.putExtra("class", student.getClassname()); // fixed: getClassname()
            startActivity(intent);

        } else if ("delete".equals(action)) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Student")
                    .setMessage("Delete " + student.getName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.collection("students")
                                .document(student.getUid())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    loadStudents();
                                    Toast.makeText(this, "Student deleted",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to delete: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}