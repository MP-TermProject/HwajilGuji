package com.example.srmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CropView extends View {
    Bitmap background;
    MainActivity mainActivity;
    ProcessActivity processActivity;

    int width = 0;
    int height = 0;
    DotPoint firstPoint;
    DotPoint secondPoint;
    int originWidth = 0;
    int originHeight = 0;
    int stdW;
    int stdH;
    float ratio = 1.0f;
    Paint redPaint;

    public void init() {
        mainActivity = MainActivity.singletone;
        processActivity = ProcessActivity.singletone;
        width = mainActivity.screenWidth;
        height = mainActivity.screenHeight;
        firstPoint = new DotPoint();
        secondPoint = new DotPoint();
        firstPoint.x = firstPoint.y = secondPoint.x = secondPoint.y = 0;

        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(3);
        redPaint.setStyle(Paint.Style.STROKE);
    }

    public CropView(Context c) {
        super(c);
        init();
    }

    public CropView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    public void setBitmap(Bitmap bg) {
        background = bg;
        originWidth = background.getWidth();
        originHeight = background.getHeight();
        ratio = (float) width / originWidth;
        stdW = width;
        stdH = (int) (originHeight * ratio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (background != null) {
            Rect rect = new Rect();
            rect.set(0, 0, width, (int) (originHeight * ratio));
            canvas.drawBitmap(background, null, rect, new Paint());
        }
        Rect cropR = new Rect((int) firstPoint.x, (int) firstPoint.y, (int) secondPoint.x, (int) secondPoint.y);
        canvas.drawRect(cropR, redPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            firstPoint = secondPoint = valid(event.getX(), event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            secondPoint = valid(event.getX(), event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ResultPage resultPage = new ResultPage();
            resultPage.setResultImage(CropImage());
            processActivity.volitileFragment(resultPage);
        }
        invalidate();
        return true;
    }

    public Bitmap CropImage() {
        float cropW = Math.abs((firstPoint.x - secondPoint.x) / ratio);
        float cropH = Math.abs((firstPoint.y - secondPoint.y) / ratio);
        Bitmap cropImage = Bitmap.createBitmap(background, (int) (firstPoint.x / ratio), (int) (firstPoint.y / ratio), (int) cropW, (int) cropH);
        return cropImage;
    }

    public DotPoint valid(float x, float y) {
        if (x < 0)
            x = 0;
        if (x > stdW)
            x = stdW;
        if (y < 0)
            y = 0;
        if (y > stdH)
            y = stdH;
        Log.e("std", Float.toString(stdH));
        Log.e("std", Float.toString(y));
        return new DotPoint(x, y);
    }
}
