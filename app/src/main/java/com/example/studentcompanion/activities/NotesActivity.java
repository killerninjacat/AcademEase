package com.example.studentcompanion.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.studentcompanion.ClickListener;
import com.example.studentcompanion.LongClickListener;
import com.example.studentcompanion.R;
import com.example.studentcompanion.adapters.NotesAdapter;
import com.example.studentcompanion.adapters.notesData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    Button new_note,home;
    String t,d;
    List<String> titles, descriptions;
    List<notesData> displaylist;
    private SharedPreferences sp;
    NotesAdapter notesAdapter;
    RecyclerView recyclerView;
    ClickListener clickListener;
    LongClickListener longClickListener;
    public void deleteConfirmation(int index)
    {
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_confirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button yes,no;
        TextView textView;
        textView=(TextView) findViewById(R.id.textView);
        textView.setText("Do you really want to delete this note?");
        yes=dialog.findViewById(R.id.yes_att);
        no=dialog.findViewById(R.id.no_att);
        Gson gson=new Gson();
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titles.remove(index);
                descriptions.remove(index);
                displaylist.remove(index);
                notesAdapter.notifyDataSetChanged();
                String json=gson.toJson(titles);
                String json1=gson.toJson(descriptions);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("titles",json);
                editor.putString("descriptions",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void editdeletedialog(String title,String description,int index)
    {
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_delete_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button edit,delete;
        edit=dialog.findViewById(R.id.editAtt);
        delete=dialog.findViewById(R.id.deleteAtt);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmation(index);
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNotedialog(title,description,index);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void eachNote(String title,String description,int index)
    {
        Button close;
        TextView ti,de;
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.each_note_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        close=dialog.findViewById(R.id.close1);
        ti=dialog.findViewById(R.id.titleindialog);
        de=dialog.findViewById(R.id.descriptionindialog);
        ti.setText(title);
        de.setText(description);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void editNotedialog(String title,String description,int index)
    {
        EditText titlebox,contentbox;
        TextView newNotebox;
        Button close,save;
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.new_note_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titlebox=dialog.findViewById(R.id.titlebox);
        contentbox=dialog.findViewById(R.id.contentbox);
        newNotebox=dialog.findViewById(R.id.newNotebox);
        newNotebox.setText("Edit Note");
        titlebox.setText(title);
        contentbox.setText(description);
        close=dialog.findViewById(R.id.close);
        save=dialog.findViewById(R.id.save_note);
        Gson gson=new Gson();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t=titlebox.getText().toString();
                d=contentbox.getText().toString();
                titles.remove(index);
                descriptions.remove(index);
                titles.add(t);
                descriptions.add(d);
                for (notesData note : displaylist) {
                    if (note.getTitle().equals(title) && note.getDescription().equals(description)) {
                        displaylist.remove(note);
                        break;
                    }
                }
                displaylist.add(new notesData(t,d));
                notesAdapter.notifyDataSetChanged();
                String json=gson.toJson(titles);
                String json1=gson.toJson(descriptions);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("titles",json);
                editor.putString("descriptions",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void newNote()
    {
        EditText titlebox,contentbox;
        Button close,save;
        final Dialog dialog = new Dialog(NotesActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.new_note_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titlebox=dialog.findViewById(R.id.titlebox);
        contentbox=dialog.findViewById(R.id.contentbox);
        close=dialog.findViewById(R.id.close);
        save=dialog.findViewById(R.id.save_note);
        Gson gson=new Gson();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t=titlebox.getText().toString();
                d=contentbox.getText().toString();
                titles.add(t);
                descriptions.add(d);
                displaylist.add(new notesData(t,d));
                notesAdapter.notifyDataSetChanged();
                String json=gson.toJson(titles);
                String json1=gson.toJson(descriptions);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("titles",json);
                editor.putString("descriptions",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        new_note=(Button) findViewById(R.id.new_note);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        titles=new ArrayList<>();
        descriptions=new ArrayList<>();
        Gson gson=new Gson();
       sp = getSharedPreferences("com.example.studentcompanion", 0);
        displaylist=new ArrayList<>();
       String json=sp.getString("titles",null);
       String json1=sp.getString("descriptions",null);
       Log.d("json", "json: "+json);
       titles=gson.fromJson(json,ArrayList.class);
       descriptions=gson.fromJson(json1,ArrayList.class);
       if(titles==null)
           titles=new ArrayList<>();
       if(descriptions==null)
           descriptions=new ArrayList<>();
       if(titles!=null)
        for(int i=0;i<titles.size();i++)
       {
           displaylist.add(new notesData(titles.get(i),descriptions.get(i)));
       }
        clickListener = new ClickListener() {
            @Override
            public void click(int index){
                eachNote(titles.get(index),descriptions.get(index),index);
            }
        };
       longClickListener=new LongClickListener() {
           @Override
           public void longclick(int index) {
               editdeletedialog(titles.get(index),descriptions.get(index),index);
           }
       };
        home=(Button) findViewById(R.id.home_n);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,MainActivity.class));
            }
        });
       notesAdapter =new NotesAdapter(displaylist,getApplication(),clickListener,longClickListener);
       recyclerView.setAdapter(notesAdapter);
       recyclerView.setLayoutManager(new LinearLayoutManager(NotesActivity.this));
        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNote();
            }
        });
    }
}