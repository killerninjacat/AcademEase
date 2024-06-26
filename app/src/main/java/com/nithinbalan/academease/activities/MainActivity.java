package com.nithinbalan.academease.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.academease.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {
    Button timetable,attendance, notes, wb, gh;
    int fresh;
    private KonfettiView konfettiView;
    List<String> tips;
    String name,name1;
    TextView welcome;
    SharedPreferences.Editor editor;
    LocalDateTime current;
    public void explode() {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
        EmitterConfig emitterConfig1 = new Emitter(100L, TimeUnit.MILLISECONDS).max(50);
        konfettiView.start(
                new PartyFactory(emitterConfig1)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
    }
    public void enterName()
    {
        EditText namebox;
        Button ok;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.enter_name);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        namebox=dialog.findViewById(R.id.namebox);
        ok=dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                name=namebox.getText().toString();
                name=name.trim();
                if(name.isEmpty())
                    Toast.makeText(MainActivity.this,"Enter a name.",Toast.LENGTH_SHORT).show();
                else {
                    editor.putString("username", name);
                    fresh=1;
                    if (current.getHour() >= 5 && current.getHour() <= 11)
                        welcome.setText("GOOD MORNING, " + name + "!");
                    else if (current.getHour() >= 11 && current.getHour() <= 15)
                        welcome.setText("GOOD AFTERNOON, " + name + "!");
                    else if (current.getHour() > 15 && current.getHour() <= 20)
                        welcome.setText("GOOD EVENING, " + name + "!");
                    else if (current.getHour() > 20 && current.getHour() <= 23)
                        welcome.setText("GOOD NIGHT, " + name + "!");
                    else welcome.setText("SLEEP WELL! DON'T STAY UP TOO LATE, " + name + "!");
                    Log.d("name",name);
                    editor.putInt("newUser",fresh);
                    editor.apply();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        timetable= findViewById(R.id.button);
        attendance= findViewById(R.id.button2);
        notes= findViewById(R.id.button3);
        konfettiView = findViewById(R.id.konfettiView);
        wb= findViewById(R.id.button4);
        gh= findViewById(R.id.gh_link);
        TextView madeWith = findViewById(R.id.madeWith);
        tips = new ArrayList<>();
        tips.add("Report bugs and share your thoughts at nithin.appdev01@gmail.com.");
        tips.add("Long press the welcome message to edit your name.");
        tips.add("Feel free to share the app with your friends!");
        tips.add("Have you tried out the widget yet?");
        tips.add("Tap the number of classes in the calendar page to view your dates.");
        tips.add("Most list items and text items are long-clickable.");
        tips.add("Heads up! Uninstalling erases all your local data.");
        tips.add("Organize PDFs scanned in the app by creating sub-folders.");
        tips.add("Tip: You can save your doodle by clicking on the save icon.");
        tips.add("Long press a subject in the attendance page to edit.");
        tips.add("Fork the project on GitHub to contribute!");
        tips.add("Long press a subject in the timetable page to edit.");
        tips.add("Long press a note in the notes page to edit the content.");
        tips.add("Have you tried the PDF scanner?");
        madeWith.setOnLongClickListener(v -> {
            explode();
            Toast.makeText(MainActivity.this,tips.get((int)(Math.random()*tips.size())),Toast.LENGTH_SHORT).show();
            return true;
        });
//        gh.setOnClickListener(v -> {
//            Uri uri = Uri.parse("https://github.com/killerninjacat/StudentCompanion");
//            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent1);
//        });

        welcome= findViewById(R.id.welcome);
        current = LocalDateTime.now();
        SharedPreferences sp = getSharedPreferences("com.example.academease", 0);
        fresh= sp.getInt("newUser",0);
        if(fresh==0)
        {
            enterName();
        }
        else {
            name1 = sp.getString("username", "User");
            if (current.getHour() >= 5 && current.getHour() <= 11)
                welcome.setText("GOOD MORNING, " + name1 + "!");
            else if (current.getHour() >= 11 && current.getHour() <= 15)
                welcome.setText("GOOD AFTERNOON, " + name1 + "!");
            else if (current.getHour() > 15 && current.getHour() <= 20)
                welcome.setText("GOOD EVENING, " + name1 + "!");
            else if (current.getHour() > 20 && current.getHour() <= 23)
                welcome.setText("GOOD NIGHT, " + name1 + "!");
            else welcome.setText("SLEEP WELL! DON'T STAY UP TOO LATE, " + name1 + "!");
            Log.d("name1", name1);
        }
        editor = sp.edit();
        timetable.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, TimetableActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        attendance.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, AttendanceActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        notes.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, NotesActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        wb.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, WhiteboardActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        });
        welcome.setOnLongClickListener(v -> {
            enterName();
            return true;
        });
    }
    @Override
    public void onBackPressed() {
    }
}