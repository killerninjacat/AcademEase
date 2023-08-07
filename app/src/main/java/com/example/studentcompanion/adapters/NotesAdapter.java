package com.example.studentcompanion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.studentcompanion.ClickListener;
import com.example.studentcompanion.LongClickListener;
import com.example.studentcompanion.R;
import com.example.studentcompanion.adapters.notesData;
import com.example.studentcompanion.adapters.notesViewHolder;

import java.util.Collections;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<notesViewHolder> {
    List<notesData> list= Collections.emptyList();
    Context context;
    ClickListener clickListener;
    LongClickListener longClickListener;
    public NotesAdapter(List<notesData> list, Context context, ClickListener clickListener, LongClickListener longClickListener)
    {
        this.list=list;
        this.context=context;
        this.clickListener=clickListener;
        this.longClickListener=longClickListener;
    }

    @Override
    public notesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.notes_card, parent, false);
        notesViewHolder viewHolder = new notesViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final notesViewHolder viewHolder,
                     final int position)
    {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.title.setText(list.get(position).title);
        viewHolder.description.setText(list.get(position).description);
        viewHolder.view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                clickListener.click(index);
            }
        });
        viewHolder.view1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.longclick(index);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
