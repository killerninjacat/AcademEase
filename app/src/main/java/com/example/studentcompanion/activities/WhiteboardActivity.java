package com.example.studentcompanion.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.studentcompanion.R;

public class WhiteboardActivity extends AppCompatActivity {
    private Button btnStrokeWidth,btnPaintColor,btnClearCanvas;
    CanvasActivity canvasActivity;
    public void strokedialog()
    {
        Button inc, dec, reset;
        final Dialog dialog = new Dialog(WhiteboardActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.stroke_selection);
        dialog.getWindow().setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        inc=dialog.findViewById(R.id.add);
        dec=dialog.findViewById(R.id.subtract);
        reset=dialog.findViewById(R.id.reset_stroke);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.modifyStrokeWidth(2);
                Toast.makeText(WhiteboardActivity.this,"StrokeWidth: "+canvasActivity.getStrokeWidth(),Toast.LENGTH_SHORT).show();
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.modifyStrokeWidth(-2);
                Toast.makeText(WhiteboardActivity.this,"StrokeWidth: "+canvasActivity.getStrokeWidth(),Toast.LENGTH_SHORT).show();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.setStrokeWidth(6);
                Toast.makeText(WhiteboardActivity.this,"StrokeWidth has been reset.",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    public void colourselectiondialog()
    {
        Button red,blue,black,green,violet;
        final Dialog dialog = new Dialog(WhiteboardActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.colour_selection);
        red=dialog.findViewById(R.id.red);
        blue=dialog.findViewById(R.id.blue);
        black=dialog.findViewById(R.id.black);
        green=dialog.findViewById(R.id.green);
        violet=dialog.findViewById(R.id.violet);
        violet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.setColor(Color.parseColor("#9900ff"));
                dialog.dismiss();
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.setColor(Color.RED);
                dialog.dismiss();
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.setColor(Color.BLUE);
                dialog.dismiss();
            }
        });
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.setColor(Color.BLACK);
                dialog.dismiss();
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.setColor(Color.GREEN);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard);
        canvasActivity = new CanvasActivity(this, null);
        canvasActivity.setBackgroundColor(Color.WHITE);

        FrameLayout drawingContainer = findViewById(R.id.drawing_container);
        drawingContainer.addView(canvasActivity);
        btnStrokeWidth = findViewById(R.id.btn_stroke_width);
        btnPaintColor = findViewById(R.id.btn_paint_color);
        btnClearCanvas = findViewById(R.id.btn_clear_canvas);
        btnStrokeWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokedialog();
            }
        });

        btnPaintColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colourselectiondialog();
            }
        });

        btnClearCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasActivity.clearCanvas();
            }
        });
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (canvasActivity.getParent() == null) {
            setContentView(R.layout.activity_whiteboard);

            FrameLayout drawingContainer = findViewById(R.id.drawing_container);
            drawingContainer.addView(canvasActivity);
                }
        }
    }