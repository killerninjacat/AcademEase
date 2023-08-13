package com.example.studentcompanion.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.studentcompanion.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ArrayAdapter<String> arr;
    ListView subject_view;
    Button new_subject;
    Button home;
    List<String> subjectsList;
    List<Double> targetsList;
    private SharedPreferences sp;
    public void new_attendance_dialog()
    {
        EditText subject_name,target;
        Button save;
        final Dialog dialog = new Dialog(AttendanceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_attendance);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subject_name=dialog.findViewById(R.id.new_subject_box);
        target=dialog.findViewById(R.id.target_box);
        save=dialog.findViewById(R.id.save_new_subject);
        Gson gson=new Gson();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double d=Double.parseDouble(target.getText().toString());
                if(d>=0&&d<=100) {
                    if (subjectsList.contains(subject_name.getText().toString()))
                        Toast.makeText(AttendanceActivity.this, "Subject \"" + subject_name.getText().toString() + "\" already exists!", Toast.LENGTH_SHORT).show();
                    else
                    {
                        subjectsList.add(subject_name.getText().toString());
                    targetsList.add(d);
                    arr.notifyDataSetChanged();
                    String json = gson.toJson(subjectsList);
                    String json1 = gson.toJson(targetsList);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("subjects", json);
                    editor.putString("targets", json1);
                    editor.apply();
                    dialog.dismiss();
                }
                }
                else Toast.makeText(AttendanceActivity.this,"Enter a valid value (0 to 100) for target attendance",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        subjectsList=new ArrayList<>();
        targetsList=new ArrayList<>();
        Gson gson=new Gson();
        sp = getSharedPreferences("com.example.studentcompanion", 0);
        String json=sp.getString("subjects",null);
        String json1=sp.getString("targets",null);
        subjectsList=gson.fromJson(json,ArrayList.class);
        targetsList=gson.fromJson(json1,ArrayList.class);
        home=(Button) findViewById(R.id.home_a);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendanceActivity.this,MainActivity.class));
            }
        });
        if(subjectsList==null)
            subjectsList=new ArrayList<>();
        if(targetsList==null)
            targetsList=new ArrayList<>();
        subject_view =(ListView) findViewById(R.id.attendancelistview);
         new_subject=(Button) findViewById(R.id.new_subject);
        arr=new ArrayAdapter<String>(this,R.layout.eachsubject,R.id.name,subjectsList);
        subject_view.setAdapter(arr);
        subject_view.setOnItemClickListener(this);
        new_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_attendance_dialog();
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i=new Intent(AttendanceActivity.this, CalendarActivity.class);
        i.putExtra("sub",subjectsList.get(position));
        i.putExtra("target_percentage",targetsList.get(position));
        i.putExtra("sub_index",position);
        startActivity(i);
    }
}