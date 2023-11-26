package com.example.studentcompanion.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentcompanion.AttendanceData;
import com.example.studentcompanion.ClickListener;
import com.example.studentcompanion.DBHandler;
import com.example.studentcompanion.LongClickListener;
import com.example.studentcompanion.R;
import com.example.studentcompanion.adapters.AttendanceAdapter;
import com.example.studentcompanion.adapters.notesData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity{
    RecyclerView subject_view;
    Button new_subject;
    Button home;
    Gson gson;
    private DBHandler dbHandler;
    List<AttendanceData> attendanceDataList, attendanceDataList1;
    List<String> subjectsList;
    List<notesData> displaylist;
    List<Double> percentagesList;
    AttendanceAdapter attendanceAdapter;
    ClickListener clickListener;
    LongClickListener longClickListener;
    List<Double> targetsList;
    private SharedPreferences sp;
    public void edit_attendance_dialog(int index)
    {
        EditText subject_name,target_box;
        Button save;
        TextView title;
        final Dialog dialog = new Dialog(AttendanceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_attendance);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subject_name=dialog.findViewById(R.id.new_subject_box);
        target_box=dialog.findViewById(R.id.target_box);
        title=dialog.findViewById(R.id.newAtt);
        title.setText("Edit Course Details");
        subject_name.setText(subjectsList.get(index));
        target_box.setText(targetsList.get(index)+"");
        save=dialog.findViewById(R.id.save_new_subject);
        Gson gson=new Gson();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectsList.contains(subject_name.getText().toString())&&!(subject_name.getText().toString().equals(subjectsList.get(index))))
                    Toast.makeText(AttendanceActivity.this, "Subject \"" + subject_name.getText().toString() + "\" already exists!", Toast.LENGTH_SHORT).show();
                else {
                    dbHandler.updateName(subject_name.getText().toString(), subjectsList.get(index));
                    subjectsList.set(index, subject_name.getText().toString());
                    targetsList.set(index, Double.parseDouble(target_box.getText().toString()));
                    targetsList.set(index,Double.parseDouble(target_box.getText().toString()));
                    subjectsList.set(index,subject_name.getText().toString());
                    displaylist.set(index,new notesData(subject_name.getText().toString(),percentagesList.get(index)+""));
                    attendanceAdapter.notifyDataSetChanged();
                    String json = gson.toJson(subjectsList);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("subjects", json);
                    String json1 = gson.toJson(targetsList);
                    editor.putString("targets", json1);
                    editor.apply();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    public void deleteConfirmation(int ind)
    {
        final Dialog dialog = new Dialog(AttendanceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_confirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button yes,no;
        subjectsList=new ArrayList<>();
        String json2=sp.getString("subjects",null);
        subjectsList=gson.fromJson(json2, ArrayList.class);
        if(subjectsList==null)
            subjectsList=new ArrayList<>();
        yes=dialog.findViewById(R.id.yes_att);
        no=dialog.findViewById(R.id.no_att);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteSubject(subjectsList.get(ind));
                subjectsList.remove(ind);
                targetsList.remove(ind);
                String json=gson.toJson(subjectsList);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("subjects",json);
                String json1=gson.toJson(targetsList);
                editor.putString("targets",json1);
                editor.apply();
                displaylist.remove(ind);
                attendanceAdapter.notifyDataSetChanged();
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
    public void editdeletedialog(int ind)
    {
        final Dialog dialog = new Dialog(AttendanceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edit_delete_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button edit,delete;
        edit=dialog.findViewById(R.id.editAtt);
        delete=dialog.findViewById(R.id.deleteAtt);
        subjectsList=new ArrayList<>();
        String json2=sp.getString("subjects",null);
        subjectsList=gson.fromJson(json2, ArrayList.class);
        if(subjectsList==null)
            subjectsList=new ArrayList<>();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmation(ind);
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_attendance_dialog(ind);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
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
                if (subject_name.getText().toString().equals(""))
                    Toast.makeText(AttendanceActivity.this, "Enter a valid subject name", Toast.LENGTH_SHORT).show();
                else if (target.getText().toString().equals(""))
                    Toast.makeText(AttendanceActivity.this, "Enter a valid target attendance", Toast.LENGTH_SHORT).show();
                else
                {
                    double d = Double.parseDouble(target.getText().toString());
                if (d >= 0 && d <= 100) {
                    if (subjectsList.contains(subject_name.getText().toString()))
                        Toast.makeText(AttendanceActivity.this, "Subject \"" + subject_name.getText().toString() + "\" already exists!", Toast.LENGTH_SHORT).show();
                    else {
                        subjectsList.add(subject_name.getText().toString());
                        targetsList.add(d);
                        displaylist.add(new notesData(subject_name.getText().toString(), "0.0"));
                        attendanceAdapter.notifyDataSetChanged();
                        String json = gson.toJson(subjectsList);
                        String json1 = gson.toJson(targetsList);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("subjects", json);
                        editor.putString("targets", json1);
                        editor.apply();
                        dialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(AttendanceActivity.this, "Enter a valid target attendance", Toast.LENGTH_SHORT).show();
                }

            }
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
        attendanceDataList=new ArrayList<>();
        attendanceDataList1=new ArrayList<>();
        percentagesList=new ArrayList<>();
        displaylist=new ArrayList<>();
        gson=new Gson();
        sp = getSharedPreferences("com.example.studentcompanion", 0);
        String json=sp.getString("subjects",null);
        String json1=sp.getString("targets",null);
        subjectsList=gson.fromJson(json,ArrayList.class);
        targetsList=gson.fromJson(json1,ArrayList.class);
        home=(Button) findViewById(R.id.home_a);
        dbHandler=new DBHandler(AttendanceActivity.this);
        attendanceDataList=dbHandler.readData();
        if(subjectsList==null)
            subjectsList=new ArrayList<>();
        if(targetsList==null)
            targetsList=new ArrayList<>();
        for(int i=0;i<subjectsList.size();i++) {
            attendanceDataList1=new ArrayList<>();
            for (int j = 0; j < attendanceDataList.size(); j++) {
                if (subjectsList.get(i).equals(attendanceDataList.get(j).getName())) {
                    attendanceDataList1.add(attendanceDataList.get(j));
                }
            }
            int tc=0, ac=0;
            for(int k=0;k<attendanceDataList1.size();k++)
            {
                tc++;
                if(attendanceDataList1.get(k).getAttended().equals("true"))
                    ac++;
            }
            float ac1=ac;
            double percentage=(double) Math.round((ac1 * 100 / tc) * 100) / 100;
            percentagesList.add(percentage);
            displaylist.add(new notesData(subjectsList.get(i),String.valueOf(percentage)));
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendanceActivity.this,MainActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
        subject_view =(RecyclerView) findViewById(R.id.attendance_recycler_view);
         new_subject=(Button) findViewById(R.id.new_subject);
        new_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_attendance_dialog();
            }
        });
        clickListener = new ClickListener() {
            @Override
            public void click(int index){
                Intent i=new Intent(AttendanceActivity.this, CalendarActivity.class);
                i.putExtra("sub",subjectsList.get(index));
                i.putExtra("target_percentage",targetsList.get(index));
                i.putExtra("sub_index",index);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        };
        longClickListener=new LongClickListener() {
            @Override
            public void longclick(int index) {
                editdeletedialog(index);
            }
        };
        attendanceAdapter=new AttendanceAdapter(displaylist,this,clickListener,longClickListener);
        subject_view.setAdapter(attendanceAdapter);
        subject_view.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AttendanceActivity.this,MainActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}