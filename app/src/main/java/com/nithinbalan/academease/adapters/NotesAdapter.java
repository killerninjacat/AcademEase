package com.nithinbalan.academease.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.nithinbalan.academease.ClickListener;
import com.nithinbalan.academease.LongClickListener;
import com.example.academease.R;

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
        viewHolder.view1.setOnClickListener(view -> clickListener.click(index));
        viewHolder.view1.setOnLongClickListener(v -> {
            longClickListener.longclick(index);
            return true;
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
