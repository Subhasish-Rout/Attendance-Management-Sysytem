package com.example.amsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    public interface OnActionListener {
        void onAction(Student student, String action); // "qr" or "delete"
    }

    private final List<Student> list;
    private final OnActionListener listener;

    public StudentAdapter(List<Student> list, OnActionListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Student u = list.get(position);
        h.tvName.setText(u.getName());
        h.tvEmail.setText(u.getEmail());
        h.tvExtra.setText("Roll: " + u.getRollNumber() + "  Class: " + u.getClassname());
        h.btnDelete.setOnClickListener(v -> listener.onAction(u, "delete"));
        h.btnQr.setOnClickListener(v -> listener.onAction(u, "qr"));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvExtra;
        Button btnDelete, btnQr;
        ViewHolder(View v) {
            super(v);
            tvName    = v.findViewById(R.id.tvName);
            tvEmail   = v.findViewById(R.id.tvEmail);
            tvExtra   = v.findViewById(R.id.tvExtra);
            btnDelete = v.findViewById(R.id.btnDelete);
            btnQr     = v.findViewById(R.id.btnQr);
        }
    }
}