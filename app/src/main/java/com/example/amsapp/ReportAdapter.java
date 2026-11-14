package com.example.amsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final List<AttendanceRecord> list;

    public ReportAdapter(List<AttendanceRecord> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AttendanceRecord r = list.get(position);
        h.tvName.setText(r.getStudentName());
        h.tvRoll.setText("Roll: " + r.getRollNumber()
                + "  |  Class: " + r.getClassname());
        h.tvDateTime.setText(r.getDate() + "   " + r.getTime());
        h.tvStatus.setText(r.getStatus());
        int color = "Present".equals(r.getStatus())
                ? R.color.green : R.color.red;
        h.tvStatus.setTextColor(
                ContextCompat.getColor(h.tvStatus.getContext(), color));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoll, tvDateTime, tvStatus;
        ViewHolder(View v) {
            super(v);
            tvName     = v.findViewById(R.id.tvAttName);
            tvRoll     = v.findViewById(R.id.tvAttRoll);
            tvDateTime = v.findViewById(R.id.tvAttDateTime);
            tvStatus   = v.findViewById(R.id.tvAttStatus);
        }
    }
}