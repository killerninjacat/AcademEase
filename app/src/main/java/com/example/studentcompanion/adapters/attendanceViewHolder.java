package com.example.studentcompanion.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studentcompanion.R;

public class attendanceViewHolder extends RecyclerView.ViewHolder {
    TextView title,description;
    View view1;
    attendanceViewHolder(View view)
    {
        super(view);
        title=(TextView) view.findViewById(R.id.subject_title);
        description=(TextView) view.findViewById(R.id.percentage);
        view1=view;
    }
}
