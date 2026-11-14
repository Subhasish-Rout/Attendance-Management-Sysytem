package com.example.amsapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private FirebaseFirestore db;
    private String teacherName = "Teacher";
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        db          = FirebaseFirestore.getInstance();
        barcodeView = findViewById(R.id.barcodeView);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) teacherName = doc.getString("name");
                });

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (isProcessing) return;
                isProcessing = true;
                processQR(result.getText());
                barcodeView.postDelayed(() -> isProcessing = false, 3000);
            }
            @Override
            public void possibleResultPoints(
                    List<com.google.zxing.ResultPoint> r) {}
        });
    }

    private void processQR(String studentUid) {
        db.collection("users").document(studentUid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        runOnUiThread(() -> Toast.makeText(this,
                                "Student not found", Toast.LENGTH_LONG).show());
                        return;
                    }
                    String name  = doc.getString("name");
                    String roll  = doc.getString("rollNumber");
                    String cls   = doc.getString("classname");
                    String date  = new SimpleDateFormat("yyyy-MM-dd",
                            Locale.getDefault()).format(new Date());
                    String time  = new SimpleDateFormat("HH:mm:ss",
                            Locale.getDefault()).format(new Date());

                    String docId = studentUid + "_" + date;

                    db.collection("attendance").document(docId).get()
                            .addOnSuccessListener(attDoc -> {
                                if (attDoc.exists()) {
                                    runOnUiThread(() -> Toast.makeText(this,
                                            "Already marked: " + name,
                                            Toast.LENGTH_LONG).show());
                                    return;
                                }
                                Map<String, Object> data = new HashMap<>();
                                data.put("studentUid",  studentUid);
                                data.put("studentName", name);
                                data.put("rollNumber",  roll);
                                data.put("classname",   cls);
                                data.put("date",        date);
                                data.put("time",        time);
                                data.put("status",      "Present");
                                data.put("markedBy",    teacherName);

                                db.collection("attendance").document(docId)
                                        .set(data)
                                        .addOnSuccessListener(unused ->
                                                runOnUiThread(() ->
                                                        Toast.makeText(this,
                                                                "Present: " + name,
                                                                Toast.LENGTH_LONG).show()));
                            });
                })
                .addOnFailureListener(e -> runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()));
    }

    @Override protected void onResume() { super.onResume(); barcodeView.resume(); }
    @Override protected void onPause()  { super.onPause();  barcodeView.pause(); }
}