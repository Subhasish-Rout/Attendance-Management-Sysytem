package com.example.amsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManualAttendanceActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String teacherName = "Teacher";
    private List<Student> studentList = new ArrayList<>();
    private ManualAttendanceAdapter adapter;
    private ProgressBar progressBar;
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_attendance);

        db        = FirebaseFirestore.getInstance();
        todayDate = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(new Date());
        progressBar = findViewById(R.id.progressBar);
        RecyclerView rv = findViewById(R.id.rvStudents);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) teacherName = doc.getString("name");
                });

        adapter = new ManualAttendanceAdapter(studentList,
                (student, status) -> markAttendance(student, status));
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadStudents();
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(qs -> {
                    studentList.clear();
                    for (var doc : qs) {
                        Student u = doc.toObject(Student.class);
                        u.setUid(doc.getId());
                        studentList.add(u);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void markAttendance(Student student, String status) {
        String time  = new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault()).format(new Date());
        String docId = student.getUid() + "_" + todayDate;

        Map<String, Object> data = new HashMap<>();
        data.put("studentUid",  student.getUid());
        data.put("studentName", student.getName());
        data.put("rollNumber",  student.getRollNumber());
        data.put("classname",   student.getClassname());
        data.put("date",        todayDate);
        data.put("time",        time);
        data.put("status",      status);
        data.put("markedBy",    teacherName);

        db.collection("attendance").document(docId).set(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                student.getName() + " marked " + status,
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}