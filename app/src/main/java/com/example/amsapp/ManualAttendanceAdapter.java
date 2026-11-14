package com.example.amsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ManualAttendanceAdapter
        extends RecyclerView.Adapter<ManualAttendanceAdapter.ViewHolder> {

    public interface OnMarkListener {
        void onMark(Student student, String status);
    }

    private final List<Student> list;
    private final OnMarkListener listener;

    public ManualAttendanceAdapter(List<Student> list, OnMarkListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manual_attendance, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Student s = list.get(position);
        h.tvName.setText(s.getName());
        h.tvRoll.setText("Roll: " + s.getRollNumber()
                + "  |  Class: " + s.getClassname());
        h.btnPresent.setOnClickListener(v -> listener.onMark(s, "Present"));
        h.btnAbsent.setOnClickListener(v  -> listener.onMark(s, "Absent"));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoll;
        Button btnPresent, btnAbsent;
        ViewHolder(View v) {
            super(v);
            tvName     = v.findViewById(R.id.tvStudentName);
            tvRoll     = v.findViewById(R.id.tvRollClass);
            btnPresent = v.findViewById(R.id.btnPresent);
            btnAbsent  = v.findViewById(R.id.btnAbsent);
        }
    }
}