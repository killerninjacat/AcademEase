package com.example.studentcompanion.dayfragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.studentcompanion.adapters.AttendanceAdapter;
import com.example.studentcompanion.ClickListener;
import com.example.studentcompanion.R;
import com.example.studentcompanion.adapters.notesData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ThursdayFragment extends Fragment {
    List<notesData> displaylist;
    List<String> classes;
    List<Double> times;
    Button new_class;
    AttendanceAdapter attendanceAdapter;
    RecyclerView recyclerView;
    private SharedPreferences sp;
    Gson gson;
    ClickListener clickListener;
    public void editClassDialog(int h,int m,String name,int index)
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_class);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TimePicker timePicker;
        Button save;
        EditText newsubjectbox;
        newsubjectbox=dialog.findViewById(R.id.subjectnamebox);
        timePicker=dialog.findViewById(R.id.simpleTimePicker);
        TextView t1;
        t1=dialog.findViewById(R.id.class_header);
        t1.setText("Edit Class");
        timePicker.setHour(h);
        timePicker.setMinute(m);
        newsubjectbox.setText(name);
        SharedPreferences.Editor editor=sp.edit();
        save=dialog.findViewById(R.id.save_class);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classes.remove(index);
                times.remove(index);
                classes.add(newsubjectbox.getText().toString());
                int timeInMinutes=timePicker.getHour()*60+timePicker.getMinute();
                times.add(Double.parseDouble(timeInMinutes+""));
                for(int i=0;i<classes.size();i++) {
                    int min=i;
                    for (int j = i + 1; j < classes.size(); j++)
                        if (times.get(j) < times.get(min)) {
                            min=j;
                        }
                    Double temp = times.get(i);
                    String tm = classes.get(i);
                    times.set(i, times.get(min));
                    classes.set(i, classes.get(min));
                    times.set(min, temp);
                    classes.set(min, tm);
                }
                displaylist.clear();
                for(int k=0;k<classes.size();k++) {
                    int hour=(int)(times.get(k)/60);
                    int minutes=(int)(times.get(k)%60);
                    displaylist.add(new notesData(classes.get(k), ""+hour+":"+minutes));
                }
                attendanceAdapter.notifyDataSetChanged();
                String json=gson.toJson(classes);
                String json1=gson.toJson(times);
                editor.putString("thursdayClasses",json);
                editor.putString("thursdayTimes",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void newClassDialog()
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_class);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TimePicker timePicker;
        Button save;
        TextView newsubjectbox;
        newsubjectbox=dialog.findViewById(R.id.subjectnamebox);
        timePicker=dialog.findViewById(R.id.simpleTimePicker);
        SharedPreferences.Editor editor=sp.edit();
        save=dialog.findViewById(R.id.save_class);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classes.add(newsubjectbox.getText().toString());
                int timeInMinutes=timePicker.getHour()*60+timePicker.getMinute();
                times.add(Double.parseDouble(timeInMinutes+""));
                for(int i=0;i<classes.size();i++) {
                    int min=i;
                    for (int j = i + 1; j < classes.size(); j++)
                        if (times.get(j) < times.get(min)) {
                            min=j;
                        }
                    Double temp = times.get(i);
                    String tm = classes.get(i);
                    times.set(i, times.get(min));
                    classes.set(i, classes.get(min));
                    times.set(min, temp);
                    classes.set(min, tm);
                }
                displaylist.clear();
                for(int k=0;k<classes.size();k++) {
                    int hour=(int)(times.get(k)/60);
                    int minutes=(int)(times.get(k)%60);
                    displaylist.add(new notesData(classes.get(k), ""+hour+":"+minutes));
                }
                attendanceAdapter.notifyDataSetChanged();
                String json=gson.toJson(classes);
                String json1=gson.toJson(times);
                editor.putString("thursdayClasses",json);
                editor.putString("thursdayTimes",json1);
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public ThursdayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_tuesday, container, false);
        displaylist=new ArrayList<>();
        classes=new ArrayList<>();
        times=new ArrayList<>();
        recyclerView=view.findViewById(R.id.tue_view);
        new_class=view.findViewById(R.id.new_subject_tuesday);
        gson=new Gson();
        sp = getContext().getSharedPreferences("com.example.studentcompanion", 0);
        classes=gson.fromJson(sp.getString("thursdayClasses",null),ArrayList.class);
        times=gson.fromJson(sp.getString("thursdayTimes",null),ArrayList.class);
        if(classes==null)
            classes=new ArrayList<>();
        if(times==null)
            times=new ArrayList<>();
        for(int i=0;i<classes.size();i++) {
            int min=i;
            for (int j = i + 1; j < classes.size(); j++)
                if (times.get(j) < times.get(min)) {
                    min=j;
                }
            Double temp = times.get(i);
            String tm = classes.get(i);
            times.set(i, times.get(min));
            classes.set(i, classes.get(min));
            times.set(min, temp);
            classes.set(min, tm);
        }
        for(int k=0;k<classes.size();k++) {
            int hour=(int)(times.get(k)/60);
            int minutes=(int)(times.get(k)%60);
            displaylist.add(new notesData(classes.get(k), ""+hour+" : "+minutes));
        }
        clickListener = new ClickListener() {
            @Override
            public void click(int index){
                editClassDialog((int)(times.get(index)/60),(int)(times.get(index)%60),classes.get(index),index);
            }
        };
        attendanceAdapter=new AttendanceAdapter(displaylist,getContext(),clickListener);
        recyclerView.setAdapter(attendanceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newClassDialog();
            }
        });

        return view;
    }
}