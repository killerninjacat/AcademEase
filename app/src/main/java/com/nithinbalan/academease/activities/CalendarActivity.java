package com.nithinbalan.academease.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.nithinbalan.academease.AttendanceData;
import com.nithinbalan.academease.DBHandler;
import com.example.academease.R;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private DBHandler dbHandler;
    String currentSubject;
    private int attendedClasses,totalclasses;
    List<Calendar> highlightedDays;
    List<String> attendedDates, allDates, absentDates;
    int exists,c,current_index;
    Button home;
    CalendarView calendar;
    List<Double>targetsList;
    int targetValue;
    String att;
    Gson gson;
    List<AttendanceData> attendanceDataList;
    private SharedPreferences sp;
    TextView current_percentage,totalbox,presentbox,absentbox,subname1,target;
    public void showDates(List<String> dates1, int id)
    {
        List<String> temp = new ArrayList<>(dates1);
        for(int i=0;i<temp.size();i++)
        {
            String[] parts = temp.get(i).split("-");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            temp.set(i,year+"-"+month+"-"+day);
        }
        Collections.sort(temp);
        for(int i=0;i<temp.size();i++)
        {
            String[] parts = temp.get(i).split("-");
            String day = parts[2];
            String month = parts[1];
            String year = parts[0];
            temp.set(i,day+"-"+month+"-"+year);
        }
        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dates_view);
        dialog.getWindow().setLayout(750, 750);
        ListView listView=dialog.findViewById(R.id.listView);
        TextView textView = dialog.findViewById(R.id.textView);
        if(id==0)
            textView.setText(" ALL DATES ");
        else if(id==1)
            textView.setText(" ATTENDED DATES ");
        else if(id==2)
            textView.setText(" ABSENT DATES ");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(CalendarActivity.this,R.layout.each_date,R.id.eachdate1,temp);
        listView.setAdapter(arrayAdapter);
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
        dialog.getWindow().setLayout(750, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                        if(att.equals("false")) {
                            attendedClasses++;
                            attendedDates.add(date);
                            absentDates.remove(date);
                            Calendar highlightedDate = convertStringToCalendar(date);
                            highlightedDays.remove(highlightedDate);
                        }
                    }
                    else if(absent.isChecked()) {
                        dbHandler.updateAttended(currentSubject, date, "false");
                        if(att.equals("true")) {
                            attendedClasses--;
                            absentDates.add(date);
                            attendedDates.remove(date);
                        }
                        Calendar highlightedDate = convertStringToCalendar(date);
                        highlightedDays.add(highlightedDate);
                    }
                }
                else {
                    if (present.isChecked()) {
                        dbHandler.addNewCourse(currentSubject, date, "true");
                        attendedClasses++;
                        totalclasses++;
                        allDates.add(date);
                        attendedDates.add(date);
                    }
                    else if (absent.isChecked()) {
                        dbHandler.addNewCourse(currentSubject, date, "false");
                        totalclasses++;
                        Calendar highlightedDate = convertStringToCalendar(date);
                        highlightedDays.add(highlightedDate);
                        allDates.add(date);
                        absentDates.add(date);
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
                        target.setTextColor(Color.parseColor("#dd0000"));
                        target.setText("Attend the next " + (int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) + " classes to achieve " + targetValue + "% attendance.");
                    }
                    else {
                        target.setTextColor(Color.parseColor("#77dd77"));
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
                        target.setTextColor(Color.parseColor("#dd0000"));
                        target.setText("Attend the next " + (int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) + " classes to achieve " + targetValue + "% attendance.");
                    }
                    else {
                        target.setTextColor(Color.parseColor("#77dd77"));
                        target.setText("You have achieved your target of " + targetValue + "%! Keep up the good job!");
                    }
                    exists=0;
                    present.setChecked(false);
                    absent.setChecked(false);
                    allDates.remove(date);
                    if(attendedDates.contains(date))
                        attendedDates.remove(date);
                    else {
                        absentDates.remove(date);
                        Calendar highlightedDate1 = convertStringToCalendar(date);
                        highlightedDays.remove(highlightedDate1);
                        calendar.setHighlightedDays(highlightedDays);
                    }
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
        sp = getSharedPreferences("com.example.academease", 0);
        gson=new Gson();
        calendar=findViewById(R.id.calendarView);
        current_percentage=(TextView) findViewById(R.id.current_percentage);
        totalbox=(TextView) findViewById(R.id.totalclasses);
        presentbox=(TextView) findViewById(R.id.attendedclasses);
        absentbox=(TextView) findViewById(R.id.absentclasses);
        subname1=(TextView) findViewById(R.id.subname1);
        target=(TextView) findViewById(R.id.target);
        subname1.setText(" "+getIntent().getStringExtra("sub")+" ");
        targetValue=(int)getIntent().getDoubleExtra("target_percentage",0.0);
        attendanceDataList=new ArrayList<>();
        targetsList=new ArrayList<>();
        allDates = new ArrayList<>();
        absentDates = new ArrayList<>();
        attendedDates = new ArrayList<>();
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
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
            if(!attendanceDataList.get(i).getName().equals(currentSubject)) {
                attendanceDataList.remove(i);
                i--;
            }
        }
        for(int j=0;j<attendanceDataList.size();j++)
        {
            totalclasses++;
            allDates.add(attendanceDataList.get(j).getDate());
            if(attendanceDataList.get(j).getAttended().equals("true")) {
                attendedClasses++;
                attendedDates.add(attendanceDataList.get(j).getDate());
            }
            else {
                Calendar highlightedDate = convertStringToCalendar(attendanceDataList.get(j).getDate());
                highlightedDays.add(highlightedDate);
                absentDates.add(attendanceDataList.get(j).getDate());
            }
        }
        calendar.setHighlightedDays(highlightedDays);
        Log.d("targetslist","targetslist"+targetsList);
        totalbox.setText("Total Classes: "+totalclasses);
        presentbox.setText("Attended: "+attendedClasses);
        absentbox.setText("Absent: "+(totalclasses-attendedClasses));
        totalbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDates(allDates,0);
            }
        });
        presentbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDates(attendedDates,1);
            }
        });
        absentbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDates(absentDates,2);
            }
        });
        float f1=attendedClasses;
        double d1= (double) Math.round((f1 * 100 / totalclasses) * 100) / 100;
        current_percentage.setText(" " + d1 + "% ");
        if((int)Math.ceil((targetsList.get(current_index)/100*totalclasses-attendedClasses)/0.25)>0)
            target.setText("Attend the next "+(int)Math.ceil((targetsList.get(current_index)/100*totalclasses-attendedClasses)/0.25)+" classes to achieve "+targetValue+"% attendance.");
        else if(totalclasses!=0) {
            target.setTextColor(Color.parseColor("#77dd77"));
            target.setText("You have achieved your target of " + targetValue + "%! Keep up the good job!");
        }
        else target.setText("Attend the next 1 class to achieve " +targetValue+"% attendance.");
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CalendarActivity.this,AttendanceActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}