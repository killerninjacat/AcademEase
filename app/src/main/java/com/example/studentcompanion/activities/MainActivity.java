package com.example.studentcompanion.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentcompanion.R;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {
    Button timetable,attendance, notes, wb, gh;
    int fresh;
    private KonfettiView konfettiView;
    String name,name1;
    TextView welcome;
    private SharedPreferences sp;
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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        namebox=dialog.findViewById(R.id.namebox);
        ok=dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=namebox.getText().toString();
                name=name.trim();
                if(name.isEmpty())
                    Toast.makeText(MainActivity.this,"Enter a name.",Toast.LENGTH_SHORT).show();
                else {
                    editor.putString("username", name);
                    fresh=1;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if(current.getHour()>=5&&current.getHour()<=11)
                            welcome.setText("GOOD MORNING, "+name+"!");
                        else if(current.getHour()>=11&&current.getHour()<=15)
                            welcome.setText("GOOD AFTERNOON, "+name+"!");
                        else if(current.getHour()>15&&current.getHour()<=20)
                            welcome.setText("GOOD EVENING, "+name+"!");
                        else if(current.getHour()>20&&current.getHour()<=23) welcome.setText("GOOD NIGHT, "+name1+"!");
                        else welcome.setText("SLEEP WELL! DON'T STAY UP TOO LATE, "+name+"!");
                    }
                    editor.putInt("newUser",fresh);
                    editor.apply();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timetable=(Button) findViewById(R.id.button);
        attendance=(Button) findViewById(R.id.button2);
        notes=(Button) findViewById(R.id.button3);
        konfettiView = (KonfettiView) findViewById(R.id.konfettiView);
        wb=(Button) findViewById(R.id.button4);
        gh=(Button) findViewById(R.id.gh_link);
        TextView madeWith = (TextView) findViewById(R.id.madeWith);
        madeWith.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                explode();
                return true;
            }
        });
        gh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/killerninjacat/StudentCompanion");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);
            }
        });

        welcome=(TextView) findViewById(R.id.welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            current=LocalDateTime.now();
        }
        sp = getSharedPreferences("com.example.studentcompanion", 0);
        name1=sp.getString("username","User");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(current.getHour()>=5&&current.getHour()<=11)
                welcome.setText("GOOD MORNING, "+name1+"!");
            else if(current.getHour()>=11&&current.getHour()<=15)
                welcome.setText("GOOD AFTERNOON, "+name1+"!");
            else if(current.getHour()>15&&current.getHour()<=20)
                welcome.setText("GOOD EVENING, "+name1+"!");
            else if(current.getHour()>20&&current.getHour()<=23) welcome.setText("GOOD NIGHT, "+name1+"!");
            else welcome.setText("SLEEP WELL! DON'T STAY UP TOO LATE, "+name1+"!");
        }
        editor = sp.edit();
        fresh=sp.getInt("newUser",0);
        if(fresh==0)
        {
            enterName();
        }
        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, TimetableActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, AttendanceActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, NotesActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
        wb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, WhiteboardActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
        welcome.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enterName();
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
    }
}