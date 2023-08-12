package com.example.studentcompanion.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentcompanion.AttendanceData;
import com.example.studentcompanion.DBHandler;
import com.example.studentcompanion.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private DBHandler dbHandler;
    String currentSubject;
    private int attendedClasses,totalclasses;
    int exists,c;
    Button at_settings,home;
    List<String> subjectsList;
    String att;
    Gson gson;
    List<AttendanceData> attendanceDataList;
    private SharedPreferences sp;
    TextView current_percentage,totalbox,presentbox,absentbox,subname1,target;
    public void deleteConfirmation()
    {
        final Dialog dialog = new Dialog(CalendarActivity.this);
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
                dbHandler.deleteSubject(currentSubject);
                subjectsList.remove(currentSubject);
                String json=gson.toJson(subjectsList);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("subjects",json);
                editor.apply();
                startActivity(new Intent(CalendarActivity.this,AttendanceActivity.class));
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
    public void edit_attendance_dialog()
    {
        EditText subject_name;
        Button save;
        TextView title;
        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_attendance);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subject_name=dialog.findViewById(R.id.new_subject_box);
        title=dialog.findViewById(R.id.newAtt);
        title.setText("Edit Course Name");
        subject_name.setText(currentSubject);
        save=dialog.findViewById(R.id.save_new_subject);
        Gson gson=new Gson();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subjectsList.set(subjectsList.indexOf(currentSubject),subject_name.getText().toString());
                dbHandler.updateName(subject_name.getText().toString(),currentSubject);
                String json=gson.toJson(subjectsList);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("subjects",json);
                editor.apply();
                subname1.setText(" "+subject_name.getText().toString()+" ");
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void editdeletedialog()
    {
        final Dialog dialog = new Dialog(CalendarActivity.this);
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
                deleteConfirmation();
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_attendance_dialog();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void setAttendance(String date)
    {
        RadioButton present,absent;
        Button save;
        exists=0;
        c=0;
        att="";
        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.set_attendance);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        present=dialog.findViewById(R.id.presentButton);
        absent=dialog.findViewById(R.id.absentButton);
        save=dialog.findViewById(R.id.save_attendance);
        for(int k=0;k<attendanceDataList.size();k++)
        {
            if(attendanceDataList.get(k).getName().equals(currentSubject)&&attendanceDataList.get(k).getDate().equals(date)) {
                exists=1;
                att=attendanceDataList.get(k).getAttended();
                if (attendanceDataList.get(k).getAttended().equals("true"))
                    present.setChecked(true);
                else if (attendanceDataList.get(k).getAttended().equals("false"))
                    absent.setChecked(true);
                Log.d("date","date: "+attendanceDataList.get(k).getAttended());
            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c=0;
                if(exists==1)
                {
                    if(present.isChecked()) {
                        dbHandler.updateAttended(currentSubject, date, "true");
                        if(att.equals("false"))
                         attendedClasses++;
                    }
                    else if(absent.isChecked()) {
                        dbHandler.updateAttended(currentSubject, date, "false");
                        if(att.equals("true"))
                            attendedClasses--;
                    }
                }
                else {
                    if (present.isChecked()) {
                        dbHandler.addNewCourse(currentSubject, date, "true");
                        attendedClasses++;
                        totalclasses++;
                    }
                    else if (absent.isChecked()) {
                        dbHandler.addNewCourse(currentSubject, date, "false");
                        totalclasses++;
                    }
                    else {
                        Toast.makeText(CalendarActivity.this, "Were you present or absent?", Toast.LENGTH_SHORT).show();
                        c=1;
                    }
                }
                if(c==0) {
                    float f2 = attendedClasses;
                    double d1= (double) Math.round((f2 * 100 / totalclasses) * 100) / 100;
                    current_percentage.setText(" " + d1 + "% ");
                    totalbox.setText("Total Classes: " + totalclasses);
                    presentbox.setText("Attended: " + attendedClasses);
                    absentbox.setText("Absent: " + (totalclasses - attendedClasses));
                    attendanceDataList = dbHandler.readData();
                    exists = 0;
                    if((int)Math.ceil((0.75*totalclasses-attendedClasses)/0.25)>0)
                        target.setText("Attend the next "+(int)Math.ceil((0.75*totalclasses-attendedClasses)/0.25)+" classes to achieve your target attendance.");
                    else target.setText("You have achieved your target attendance! Keep up the good job!");
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    private String formatDate(int year, int month, int dayOfMonth) {
        String formattedMonth = String.format("%02d", month + 1);
        String formattedDay = String.format("%02d", dayOfMonth);
        return formattedDay + "-" + formattedMonth + "-" + year;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        CalendarView calendar=findViewById(R.id.calendarView);
        current_percentage=(TextView) findViewById(R.id.current_percentage);
        totalbox=(TextView) findViewById(R.id.totalclasses);
        presentbox=(TextView) findViewById(R.id.attendedclasses);
        absentbox=(TextView) findViewById(R.id.absentclasses);
        subname1=(TextView) findViewById(R.id.subname1);
        target=(TextView) findViewById(R.id.target);
        at_settings=(Button) findViewById(R.id.attendance_settings);
        subname1.setText(" "+getIntent().getStringExtra("sub")+" ");
        attendanceDataList=new ArrayList<>();
        sp = getSharedPreferences("com.example.studentcompanion", 0);
        attendedClasses=0;
        totalclasses=0;
        home=(Button) findViewById(R.id.home_c);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalendarActivity.this,MainActivity.class));
            }
        });
        gson=new Gson();
        currentSubject=getIntent().getStringExtra("sub");
        dbHandler=new DBHandler(CalendarActivity.this);
        attendanceDataList=dbHandler.readData();
        for(int i=0;i<attendanceDataList.size();i++)
        {
            Log.d("total classes","total classes: "+attendanceDataList.size());
            if(!attendanceDataList.get(i).getName().equals(currentSubject)) {
                attendanceDataList.remove(i);
                i--;
            }
        }
        for(int j=0;j<attendanceDataList.size();j++)
        {
            totalclasses++;
            if(attendanceDataList.get(j).getAttended().equals("true"))
                attendedClasses++;

        }
        totalbox.setText("Total Classes: "+totalclasses);
        presentbox.setText("Attended: "+attendedClasses);
        absentbox.setText("Absent: "+(totalclasses-attendedClasses));
        float f1=attendedClasses;
        double d1= (double) Math.round((f1 * 100 / totalclasses) * 100) / 100;
        current_percentage.setText(" " + d1 + "% ");
        if((int)Math.ceil((0.75*totalclasses-attendedClasses)/0.25)>0)
        target.setText("Attend the next "+(int)Math.ceil((0.75*totalclasses-attendedClasses)/0.25)+" classes to achieve your target attendance.");
        else target.setText("You have achieved your target attendance! Keep up the good job!");
        at_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdeletedialog();
            }
        });
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate=formatDate(year, month, dayOfMonth);
                setAttendance(selectedDate);
            }
        });
    }
}