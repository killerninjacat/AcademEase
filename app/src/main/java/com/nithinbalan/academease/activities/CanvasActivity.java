package com.nithinbalan.academease.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CanvasActivity extends View {
    private Path path1;
    private Paint paint1;
    private int paintColor = Color.BLACK;
    private int strokeWidth = 6;
    private List<PathInfo> paths = new ArrayList<>();
    public void setColor(int color) {
        paintColor = color;
        paint1.setColor(paintColor);
    }
    public void modifyStrokeWidth(int v)
    {
        if(strokeWidth+v>0)
        {
            strokeWidth+=v;
            paint1.setStrokeWidth(strokeWidth);
        }
    }
    public int getStrokeWidth()
    {
        return strokeWidth;
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        paint1.setStrokeWidth(strokeWidth);
    }

    public void clearCanvas() {
        paths.clear();
        invalidate();
    }

    public CanvasActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        paint1 = new Paint();
        paint1.setColor(paintColor);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(strokeWidth);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeJoin(Paint.Join.ROUND);
        paint1.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (PathInfo pathInfo : paths) {
            paint1.setColor(pathInfo.color);
            paint1.setStrokeWidth(pathInfo.stroke);
            canvas.drawPath(pathInfo.path, paint1);
        }

        if (path1 != null) {
            canvas.drawPath(path1, paint1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path1 = new Path();
                path1.moveTo(touchX, touchY);
                paths.add(new PathInfo(path1, paintColor, strokeWidth));
                break;
            case MotionEvent.ACTION_MOVE:
                path1.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                path1 = null;
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }


    private static class PathInfo {
        Path path;
        int color, stroke;

        PathInfo(Path path, int color, int stroke) {
            this.path = path;
            this.color = color;
            this.stroke=stroke;
        }
    }
}
