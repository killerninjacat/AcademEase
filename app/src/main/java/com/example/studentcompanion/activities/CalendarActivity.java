package com.example.studentcompanion.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.studentcompanion.AttendanceData;
import com.example.studentcompanion.DBHandler;
import com.example.studentcompanion.R;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private DBHandler dbHandler;
    String currentSubject;
    private int attendedClasses,totalclasses;
    List<Calendar> highlightedDays;
    int exists,c,current_index;
    Button at_settings,home;
    List<String> subjectsList;
    CalendarView calendar;
    List<Double>targetsList;
    int targetValue;
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
                subjectsList.remove(current_index);
                targetsList.remove(current_index);
                String json=gson.toJson(subjectsList);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("subjects",json);
                String json1=gson.toJson(targetsList);
                editor.putString("targets",json1);
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
        EditText subject_name,target_box;
        Button save;
        TextView title;
        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_attendance);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subject_name=dialog.findViewById(R.id.new_subject_box);
        target_box=dialog.findViewById(R.id.target_box);
        title=dialog.findViewById(R.id.newAtt);
        title.setText("Edit Course Details");
        subject_name.setText(currentSubject);
        target_box.setText(targetsList.get(current_index)+"");
        save=dialog.findViewById(R.id.save_new_subject);
        Gson gson=new Gson();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectsList.contains(subject_name.getText().toString())&&!(subject_name.getText().toString().equals(currentSubject)))
                    Toast.makeText(CalendarActivity.this, "Subject \"" + subject_name.getText().toString() + "\" already exists!", Toast.LENGTH_SHORT).show();
                else {
                    subjectsList.set(current_index, subject_name.getText().toString());
                    targetsList.set(current_index, Double.parseDouble(target_box.getText().toString()));
                    targetValue=(int)Double.parseDouble(target_box.getText().toString());
                    currentSubject=subject_name.getText().toString();
                    dbHandler.updateName(subject_name.getText().toString(), currentSubject);
                    String json = gson.toJson(subjectsList);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("subjects", json);
                    String json1 = gson.toJson(targetsList);
                    editor.putString("targets", json1);
                    editor.apply();
                    subname1.setText(" " + subject_name.getText().toString() + " ");
                    if ((int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) > 0) {
                        target.setText("Attend the next " + (int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) + " classes to achieve " + targetValue + "% attendance.");
                        target.setTextColor(Color.parseColor("#cc0000"));
                    }
                    else {
                        target.setTextColor(Color.parseColor("#226622"));
                        target.setText("You have achieved your target of " + targetValue + "%! Keep up the good job!");
                    }
                    dialog.dismiss();
                }
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
        subjectsList=new ArrayList<>();
        String json2=sp.getString("subjects",null);
        subjectsList=gson.fromJson(json2, ArrayList.class);
        if(subjectsList==null)
            subjectsList=new ArrayList<>();
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
        TextView date_display;
        Button save,del;
        exists=0;
        c=0;
        att="";
        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.set_attendance);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        present=dialog.findViewById(R.id.presentButton);
        date_display=dialog.findViewById(R.id.date_display);
        del=dialog.findViewById(R.id.del_day);
        date_display.setText(date);
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
                        Calendar highlightedDate = convertStringToCalendar(date);
                        highlightedDays.add(highlightedDate);
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
                        Calendar highlightedDate = convertStringToCalendar(date);
                        highlightedDays.add(highlightedDate);
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
                    if((int)Math.ceil((targetsList.get(current_index)/100*totalclasses-attendedClasses)/0.25)>0) {
                        target.setTextColor(Color.parseColor("#cc0000"));
                        target.setText("Attend the next " + (int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) + " classes to achieve " + targetValue + "% attendance.");
                    }
                        else {
                        target.setTextColor(Color.parseColor("#226622"));
                        target.setText("You have achieved your target of " + targetValue + "%! Keep up the good job!");
                    }
                    dialog.dismiss();
                    calendar.setHighlightedDays(highlightedDays);
                }
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.deleteAttendance(currentSubject,date);
                if(exists==1)
                {
                    Toast.makeText(CalendarActivity.this,"Deleted!",Toast.LENGTH_SHORT).show();
                    if(present.isChecked())
                        attendedClasses--;
                    totalclasses--;
                    float f2 = attendedClasses;
                    double d1= (double) Math.round((f2 * 100 / totalclasses) * 100) / 100;
                    current_percentage.setText(" " + d1 + "% ");
                    totalbox.setText("Total Classes: " + totalclasses);
                    presentbox.setText("Attended: " + attendedClasses);
                    absentbox.setText("Absent: " + (totalclasses - attendedClasses));
                    attendanceDataList = dbHandler.readData();
                    if((int)Math.ceil((targetsList.get(current_index)/100*totalclasses-attendedClasses)/0.25)>0) {
                        target.setTextColor(Color.parseColor("#cc0000"));
                        target.setText("Attend the next " + (int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) + " classes to achieve " + targetValue + "% attendance.");
                    }
                    else {
                        target.setTextColor(Color.parseColor("#226622"));
                        target.setText("You have achieved your target of " + targetValue + "%! Keep up the good job!");
                    }
                    exists=0;
                    present.setChecked(false);
                    absent.setChecked(false);
                }
                else Toast.makeText(CalendarActivity.this,"Entry does not exist!",Toast.LENGTH_SHORT).show();
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
        sp = getSharedPreferences("com.example.studentcompanion", 0);
        gson=new Gson();
        calendar=findViewById(R.id.calendarView);
        current_percentage=(TextView) findViewById(R.id.current_percentage);
        totalbox=(TextView) findViewById(R.id.totalclasses);
        presentbox=(TextView) findViewById(R.id.attendedclasses);
        absentbox=(TextView) findViewById(R.id.absentclasses);
        subname1=(TextView) findViewById(R.id.subname1);
        target=(TextView) findViewById(R.id.target);
        at_settings=(Button) findViewById(R.id.attendance_settings);
        subname1.setText(" "+getIntent().getStringExtra("sub")+" ");
        targetValue=(int)getIntent().getDoubleExtra("target_percentage",0.0);
        attendanceDataList=new ArrayList<>();
        targetsList=new ArrayList<>();
        String json3=sp.getString("targets",null);
        targetsList=gson.fromJson(json3, ArrayList.class);
        if(targetsList==null)
            targetsList=new ArrayList<>();
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
        current_index=getIntent().getIntExtra("sub_index",0);
        dbHandler=new DBHandler(CalendarActivity.this);
        attendanceDataList=dbHandler.readData();
        highlightedDays = new ArrayList<>();
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
            else {
                Calendar highlightedDate = convertStringToCalendar(attendanceDataList.get(j).getDate());
                highlightedDays.add(highlightedDate);
            }
        }
        calendar.setHighlightedDays(highlightedDays);
        Log.d("targetslist","targetslist"+targetsList);
        totalbox.setText("Total Classes: "+totalclasses);
        presentbox.setText("Attended: "+attendedClasses);
        absentbox.setText("Absent: "+(totalclasses-attendedClasses));
        float f1=attendedClasses;
        double d1= (double) Math.round((f1 * 100 / totalclasses) * 100) / 100;
        current_percentage.setText(" " + d1 + "% ");
        if((int)Math.ceil((targetsList.get(current_index)/100*totalclasses-attendedClasses)/0.25)>0)
            target.setText("Attend the next "+(int)Math.ceil((targetsList.get(current_index)/100*totalclasses-attendedClasses)/0.25)+" classes to achieve "+targetValue+"% attendance.");
        else if(totalclasses!=0) {
            target.setTextColor(Color.parseColor("#226622"));
            target.setText("You have achieved your target of " + targetValue + "%! Keep up the good job!");
        }
        else target.setText("Attend the next 1 class to achieve " +targetValue+"% attendance.");
        at_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdeletedialog();
            }
        });
        calendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String selectedDate = formatDate(clickedDayCalendar.get(Calendar.YEAR),
                        clickedDayCalendar.get(Calendar.MONTH),
                        clickedDayCalendar.get(Calendar.DAY_OF_MONTH));
                setAttendance(selectedDate);
            }
        });
    }
    private Calendar convertStringToCalendar(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}