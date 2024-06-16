package com.nithinbalan.academease.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.academease.R;
import com.google.gson.Gson;
import com.nithinbalan.academease.Notification;
import com.nithinbalan.academease.adapters.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimetableActivity extends AppCompatActivity {
    List<String> classes;
    List<Double> times;
    Gson gson;
    private SharedPreferences sp;
    private void scheduleNotification(){
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        Calendar cal = Calendar.getInstance();
        int day=cal.get(Calendar.DAY_OF_WEEK);
        classes=new ArrayList<>();
        times=new ArrayList<>();
        switch(day){
            case Calendar.SUNDAY:
                classes=gson.fromJson(sp.getString("sundayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("sundayTimes",null),ArrayList.class);
                break;
            case Calendar.MONDAY:
                classes=gson.fromJson(sp.getString("mondayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("mondayTimes",null),ArrayList.class);
                break;
            case Calendar.TUESDAY:
                classes=gson.fromJson(sp.getString("tuesdayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("tuesdayTimes",null),ArrayList.class);
                break;
            case Calendar.WEDNESDAY:
                classes=gson.fromJson(sp.getString("wednesdayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("wednesdayTimes",null),ArrayList.class);
                break;
            case Calendar.THURSDAY:
                classes=gson.fromJson(sp.getString("thursdayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("thursdayTimes",null),ArrayList.class);
                break;
            case Calendar.FRIDAY:
                classes=gson.fromJson(sp.getString("fridayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("fridayTimes",null),ArrayList.class);
                break;
            case Calendar.SATURDAY:
                classes=gson.fromJson(sp.getString("saturdayClasses",null),ArrayList.class);
                times=gson.fromJson(sp.getString("saturdayTimes",null),ArrayList.class);
                break;
        }
        if(classes==null)
            classes=new ArrayList<>();
        if(times==null)
            times=new ArrayList<>();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        for(int i=0;i<classes.size();i++){
            int hour = (int) (times.get(i) / 60);
            int minutes = (int) (times.get(i) % 60);
            intent.putExtra("title", classes.get(i));
            intent.putExtra("msg", "Did you attend the class?");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 420+i, intent, PendingIntent.FLAG_IMMUTABLE);
            cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, hour, minutes, 0);
            long time = cal.getTimeInMillis();
            Calendar cal1=Calendar.getInstance();
            if(hour>=cal1.get(Calendar.HOUR_OF_DAY) && minutes>cal1.get(Calendar.MINUTE)) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, /*7 * AlarmManager.INTERVAL_DAY,*/ pendingIntent);
                Log.d("Notification","Notification scheduled for "+classes.get(i)+" at "+hour+":"+minutes);
            }
            else Log.d("Notification","Time has already passed!");
        }
    }
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Academease";
            String description = "Channel for Academease notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("academease", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        else Log.d("Notification","Notification channel not created!");
    }
    public boolean checkNotifPerm(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        boolean isEnabled=notificationManager.areNotificationsEnabled();
        if(!isEnabled){
            Toast.makeText(context, "Please enable notifications for Academease!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);
            return false;
        }
        else {
            boolean isEnabledCompat= NotificationManagerCompat.from(context).areNotificationsEnabled();
            if(!isEnabledCompat){
                Toast.makeText(context, "Please enable notifications for Academease!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        createNotificationChannel();
        gson=new Gson();
        sp = getSharedPreferences("com.example.academease", 0);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(this);
        viewPager.setAdapter(tabPagerAdapter);
        if(checkNotifPerm(this)) scheduleNotification();
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 1:
                            tab.setText("Mon");
                            break;
                        case 2:
                            tab.setText("Tue");
                            break;
                        case 3:
                            tab.setText("Wed");
                            break;
                        case 4:
                            tab.setText("Thu");
                            break;
                        case 5:
                            tab.setText("Fri");
                            break;
                        case 6:
                            tab.setText("Sat");
                            break;
                        case 0:
                            tab.setText("Sun");
                            break;
                    }
                }).attach();
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int initialTab = dayOfWeek - 1;
        viewPager.setCurrentItem(initialTab);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TimetableActivity.this,MainActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}