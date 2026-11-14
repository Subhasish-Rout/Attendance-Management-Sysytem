package com.example.amsapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class StudentQRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qr);

        TextView tvInfo = findViewById(R.id.tvQRInfo);
        ImageView ivQR  = findViewById(R.id.ivQRCode);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String roll = doc.getString("rollNumber");
                        String cls  = doc.getString("classname");
                        tvInfo.setText(name + "  |  Roll: " + roll
                                + "  |  Class: " + cls);
                        generateQR(ivQR, uid);
                    }
                });
    }

    private void generateQR(ImageView ivQR, String uid) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(uid,
                    BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            ivQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "QR generation error",
                    Toast.LENGTH_SHORT).show();
        }
    }
}