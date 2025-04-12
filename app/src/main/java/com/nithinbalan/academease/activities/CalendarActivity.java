package com.nithinbalan.academease.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.applandeo.materialcalendarview.CalendarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.nithinbalan.academease.AttendanceData;
import com.nithinbalan.academease.DBHandler;
import com.example.academease.R;
import com.google.gson.Gson;
import com.nithinbalan.academease.DayData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CalendarActivity extends AppCompatActivity {
    private DBHandler dbHandler;
    String currentSubject;
    private int attendedClasses, totalclasses;
    List<Calendar> highlightedDays;
    List<DayData> attendedDates, allDates, absentDates;
    int exists, c, current_index, cnt;
    CalendarView calendar;
    List<Double> targetsList;
    int screenWidth, screenHeight;
    DisplayMetrics displayMetrics;
    EditText numberOfClasses;
    int targetValue;
    String att;
    Gson gson;
    List<AttendanceData> attendanceDataList;
    private SharedPreferences sp;
    TextView current_percentage, totalClassesText, attendedClassesText, absentClassesText, subjectName, targetText;
    TextView totalClassesLabel, attendedClassesLabel, absentClassesLabel;
    MaterialCardView statsCard;
    LinearProgressIndicator attendanceProgressBar;

    @SuppressLint("SetTextI18n")
    public void showDates(List<DayData> dates1, int id) {
        if(dates1.isEmpty()){
            Toast.makeText(CalendarActivity.this, "No dates available", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < dates1.size(); i++) {
            temp.add(dates1.get(i).getDay() + "-" + dates1.get(i).getCnt());
        }
        for (int i = 0; i < temp.size(); i++) {
            String[] parts = temp.get(i).split("-");
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            String cnt = parts[3];
            temp.set(i, year + "-" + month + "-" + day + "-" + cnt);
        }
        Collections.sort(temp);
        for (int i = 0; i < temp.size(); i++) {
            String[] parts = temp.get(i).split("-");
            String day = parts[2];
            String month = parts[1];
            String year = parts[0];
            String cnt = parts[3];
            temp.set(i, day + "-" + month + "-" + year + ": " + cnt);
        }
        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dates_view);
        int h= (int) (screenHeight*0.5);
        Objects.requireNonNull(dialog.getWindow()).setLayout(screenWidth-100,h);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dark_layout_rounded);
        ListView listView=dialog.findViewById(R.id.listView);
        TextView textView = dialog.findViewById(R.id.textView);

        String title;
        switch (id) {
            case 0:
                title = " All Classes ";
                break;
            case 1:
                title = " Attended Classes ";
                break;
            case 2:
                title = " Missed Classes ";
                break;
            default:
                title = "Class Dates";
                break;
        }

        textView.setText(title);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CalendarActivity.this, R.layout.each_date, R.id.eachdate1, temp);
        listView.setAdapter(arrayAdapter);
        Log.d("DatesDialog", "Showing " + temp.size() + " dates: " + temp);
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void setAttendance(String date) {
        RadioButton present, absent;
        TextView date_display;
        Button save, del;
        exists = 0;
        c = 0;
        att = "";

        final Dialog dialog = new Dialog(CalendarActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.set_attendance);
        Objects.requireNonNull(dialog.getWindow()).setLayout(screenWidth - 200, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dark_layout_rounded);

        present = dialog.findViewById(R.id.presentButton);
        date_display = dialog.findViewById(R.id.date_display);
        del = dialog.findViewById(R.id.del_day);
        date_display.setText(date);
        absent = dialog.findViewById(R.id.absentButton);
        save = dialog.findViewById(R.id.save_attendance);
        numberOfClasses = dialog.findViewById(R.id.classes);
        numberOfClasses.setText("1");

        for (int k = 0; k < attendanceDataList.size(); k++) {
            if (attendanceDataList.get(k).getName().equals(currentSubject) && attendanceDataList.get(k).getDate().equals(date)) {
                exists = 1;
                cnt = attendanceDataList.get(k).getCnt();
                numberOfClasses.setText("" + cnt);
                att = attendanceDataList.get(k).getAttended();
                if (attendanceDataList.get(k).getAttended().equals("true"))
                    present.setChecked(true);
                else if (attendanceDataList.get(k).getAttended().equals("false"))
                    absent.setChecked(true);
            }
        }

        save.setOnClickListener(v -> {
            c = 0;
            cnt = Integer.parseInt(numberOfClasses.getText().toString());
            if (exists == 1) {
                if (present.isChecked()) {
                    dbHandler.updateAttended(currentSubject, date, "true", cnt);
                    if (att.equals("false")) {
                        attendedClasses += cnt;
                        attendedDates.add(new DayData(date, cnt));
                        absentDates.remove(new DayData(date, cnt));
                        Calendar highlightedDate = convertStringToCalendar(date);
                        highlightedDays.remove(highlightedDate);
                    }
                } else if (absent.isChecked()) {
                    dbHandler.updateAttended(currentSubject, date, "false", cnt);
                    if (att.equals("true")) {
                        attendedClasses -= cnt;
                        absentDates.add(new DayData(date, cnt));
                        attendedDates.remove(new DayData(date, cnt));
                    }
                    Calendar highlightedDate = convertStringToCalendar(date);
                    highlightedDays.add(highlightedDate);
                }
            } else {
                if (present.isChecked()) {
                    dbHandler.addNewCourse(currentSubject, date, "true", cnt);
                    attendedClasses += cnt;
                    totalclasses += cnt;
                    allDates.add(new DayData(date, cnt));
                    attendedDates.add(new DayData(date, cnt));
                } else if (absent.isChecked()) {
                    dbHandler.addNewCourse(currentSubject, date, "false", cnt);
                    totalclasses += cnt;
                    Calendar highlightedDate = convertStringToCalendar(date);
                    highlightedDays.add(highlightedDate);
                    allDates.add(new DayData(date, cnt));
                    absentDates.add(new DayData(date, cnt));
                } else {
                    Toast.makeText(CalendarActivity.this, "Were you present or absent?", Toast.LENGTH_SHORT).show();
                    c = 1;
                }
            }

            if (c == 0) {
                updateAttendanceDisplay();
                attendanceDataList = dbHandler.readData();
                exists = 0;
                dialog.dismiss();
                calendar.setHighlightedDays(highlightedDays);
            }
        });

        del.setOnClickListener(v -> {
            dbHandler.deleteAttendance(currentSubject, date);
            if (exists == 1) {
                Toast.makeText(CalendarActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                if (att.equals("true"))
                    attendedClasses -= cnt;
                totalclasses -= cnt;

                updateAttendanceDisplay();
                attendanceDataList = dbHandler.readData();
                exists = 0;
                present.setChecked(false);
                absent.setChecked(false);

                // Remove dates from respective lists
                for (int i = 0; i < allDates.size(); i++) {
                    if (allDates.get(i).getDay().trim().equals(date.trim())) {
                        allDates.remove(i);
                        break;
                    }
                }

                if (att.equals("true")) {
                    for (int i = 0; i < attendedDates.size(); i++) {
                        if (attendedDates.get(i).getDay().trim().equals(date.trim())) {
                            attendedDates.remove(i);
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < absentDates.size(); i++) {
                        if (absentDates.get(i).getDay().trim().equals(date.trim())) {
                            absentDates.remove(i);
                            break;
                        }
                    }
                    Calendar highlightedDate = convertStringToCalendar(date);
                    highlightedDays.remove(highlightedDate);
                    calendar.setHighlightedDays(highlightedDays);
                }
            } else {
                Toast.makeText(CalendarActivity.this, "Entry does not exist!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        String formattedMonth = String.format("%02d", month + 1);
        String formattedDay = String.format("%02d", dayOfMonth);
        return formattedDay + "-" + formattedMonth + "-" + year;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_attendance) {
                return true;
            } else if (itemId == R.id.nav_timetable) {
                startActivity(new Intent(CalendarActivity.this, TimetableActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (itemId == R.id.nav_notes) {
                startActivity(new Intent(CalendarActivity.this, NotesActivity.class));
                overridePendingTransition(0,0);
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(CalendarActivity.this, MainActivity.class));
                overridePendingTransition(0,0);
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_attendance);

        initializeViews();
        setupData();
        setupListeners();
        updateAttendanceDisplay();
    }

    private void initializeViews() {
        // Initialize views
        calendar = findViewById(R.id.calendarView);
        current_percentage = findViewById(R.id.current_percentage);

        // Find the stats card views - these are the labels and values in the stats card
        totalClassesText = findViewById(R.id.totalclasses);
        totalClassesLabel = findViewById(R.id.totalclasses_label);

        attendedClassesText = findViewById(R.id.attendedclasses);
        attendedClassesLabel = findViewById(R.id.attendedclasses_label);

        absentClassesText = findViewById(R.id.absentclasses);
        absentClassesLabel = findViewById(R.id.absentclasses_label);

        statsCard = findViewById(R.id.statsCard);

        subjectName = findViewById(R.id.subname1);
        targetText = findViewById(R.id.target);
        attendanceProgressBar = findViewById(R.id.attendanceProgressBar);
    }

    private void setupData() {
        // Initialize data
        sp = getSharedPreferences("com.example.academease", 0);
        gson = new Gson();

        subjectName.setText(getIntent().getStringExtra("sub"));
        targetValue = (int) getIntent().getDoubleExtra("target_percentage", 0.0);

        attendanceDataList = new ArrayList<>();
        targetsList = new ArrayList<>();
        allDates = new ArrayList<>();
        absentDates = new ArrayList<>();
        attendedDates = new ArrayList<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        String json3 = sp.getString("targets", null);
        targetsList = gson.fromJson(json3, ArrayList.class);
        if (targetsList == null)
            targetsList = new ArrayList<>();

        attendedClasses = 0;
        totalclasses = 0;

        currentSubject = getIntent().getStringExtra("sub");
        current_index = getIntent().getIntExtra("sub_index", 0);

        dbHandler = new DBHandler(CalendarActivity.this);
        attendanceDataList = dbHandler.readData();
        highlightedDays = new ArrayList<>();

        // Filter for current subject and calculate statistics
        for (int i = 0; i < attendanceDataList.size(); i++) {
            if (!attendanceDataList.get(i).getName().equals(currentSubject)) {
                attendanceDataList.remove(i);
                i--;
            }
        }

        for (int j = 0; j < attendanceDataList.size(); j++) {
            int cnt = attendanceDataList.get(j).getCnt();
            totalclasses += cnt;
            allDates.add(new DayData(attendanceDataList.get(j).getDate(), cnt));

            if (attendanceDataList.get(j).getAttended().equals("true")) {
                attendedClasses += cnt;
                attendedDates.add(new DayData(attendanceDataList.get(j).getDate(), cnt));
            } else {
                Calendar highlightedDate = convertStringToCalendar(attendanceDataList.get(j).getDate());
                highlightedDays.add(highlightedDate);
                absentDates.add(new DayData(attendanceDataList.get(j).getDate(), cnt));
            }
        }

        calendar.setHighlightedDays(highlightedDays);
    }

    private void setupListeners() {

        // IMPORTANT: Show date lists when clicking on stats rows
        // These are the core listeners that were requested
        totalClassesText.setOnClickListener(v -> showDates(allDates, 0));
        totalClassesLabel.setOnClickListener(v -> showDates(allDates, 0));

        attendedClassesText.setOnClickListener(v -> showDates(attendedDates, 1));
        attendedClassesLabel.setOnClickListener(v -> showDates(attendedDates, 1));

        absentClassesText.setOnClickListener(v -> showDates(absentDates, 2));
        absentClassesLabel.setOnClickListener(v -> showDates(absentDates, 2));

        calendar.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            String selectedDate = formatDate(
                    clickedDayCalendar.get(Calendar.YEAR),
                    clickedDayCalendar.get(Calendar.MONTH),
                    clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
            );
            setAttendance(" " + selectedDate + " ");
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateAttendanceDisplay() {
        // Update UI with current attendance stats
        double attendancePercentage = totalclasses > 0 ?
                (double) Math.round((attendedClasses * 100.0 / totalclasses) * 100) / 100 : 0;

        current_percentage.setText(attendancePercentage + "%");
        attendanceProgressBar.setProgress((int) attendancePercentage);

        // Update the text values for stats
        totalClassesText.setText(String.valueOf(totalclasses));
        attendedClassesText.setText(String.valueOf(attendedClasses));
        absentClassesText.setText(String.valueOf(totalclasses - attendedClasses));

        // Update target text
        if (totalclasses > 0) {
            if ((int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) > 0) {
                targetText.setTextColor(getResources().getColor(R.color.colorError));
                targetText.setText("Target: " + targetValue + "% (Need " +
                        (int) Math.ceil((targetsList.get(current_index) / 100 * totalclasses - attendedClasses) / 0.25) +
                        " more classes)");
            } else {
                targetText.setTextColor(getResources().getColor(R.color.colorSuccess));
                targetText.setText("Target: " + targetValue + "% (Achieved!)");
            }
        } else {
            targetText.setTextColor(getResources().getColor(R.color.colorWarning));
            targetText.setText("Target: " + targetValue + "% (Attend 1 class)");
        }
    }

    private Calendar convertStringToCalendar(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = sdf.parse(dateString.trim());
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
            }
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CalendarActivity.this, AttendanceActivity.class));
    }
}