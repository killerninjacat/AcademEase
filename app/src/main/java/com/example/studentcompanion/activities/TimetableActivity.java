package com.example.studentcompanion.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.studentcompanion.R;
import com.example.studentcompanion.adapters.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(this);
        viewPager.setAdapter(tabPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Mon");
                            break;
                        case 1:
                            tab.setText("Tue");
                            break;
                        case 2:
                            tab.setText("Wed");
                            break;
                        case 3:
                            tab.setText("Thu");
                            break;
                        case 4:
                            tab.setText("Fri");
                            break;
                        case 5:
                            tab.setText("Sat");
                            break;
                        case 6:
                            tab.setText("Sun");
                            break;
                    }
                }).attach();
    }
}