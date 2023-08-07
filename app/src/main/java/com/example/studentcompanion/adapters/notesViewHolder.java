package com.example.studentcompanion.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studentcompanion.R;

public class notesViewHolder extends RecyclerView.ViewHolder {
    TextView title,description;
    View view1;
    notesViewHolder(View view)
    {
        super(view);
        title=(TextView) view.findViewById(R.id.note_title);
        description=(TextView) view.findViewById(R.id.note_description);
        view1=view;
    }
}
