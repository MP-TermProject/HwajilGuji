package com.example.srmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class EraserView extends View {
    Bitmap input;
    Bitmap output;
    Paint tPaint;
    float width=15f;
    float blur=10f;
    float mScaleFactor = 1.0f;
    List<List> lines;
    List<DotPoint> currentPoint;
    Rect bitmapRect;
    ScaleGestureDetector mScaleDetector;
    enum State
    {
        erase, scale
    }
    private State state;
    public void init()
    {
        bitmapRect = new Rect();
        mScaleDetector = new ScaleGestureDetector(getContext(),new EraserView.ScaleListener());
        lines = new ArrayList<>();
        tPaint=new Paint();
        tPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        tPaint.setStyle(Paint.Style.STROKE);
        tPaint.setStrokeWidth(width);
        BlurMaskFilter filter = new BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL);
        tPaint.setMaskFilter(filter);
        tPaint.setAntiAlias(true);
    }
    public void setBitmap(Bitmap _input)
    {
        input = _input;
        output = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.ARGB_8888);
        int canvwidth = input.getWidth();//MainActivity.singletone.screenWidth;
        float ratio = _input.getHeight()/_input.getWidth();
        int canvheight = input.getHeight();//(int) (canvwidth*ratio);
        bitmapRect = new Rect(0,0,canvwidth,canvheight);
        invalidate();
    }
    public EraserView(Context c)
    {
        super(c);
        init();
    }
    public EraserView(Context c, AttributeSet a)
    {
        super(c,a);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        Canvas c = new Canvas(output);
        canvas.scale(mScaleFactor,mScaleFactor);
        c.drawBitmap(input,null,bitmapRect,new Paint());
        for (int i = 0;i<lines.size();i++)
        {
            List<DotPoint> points =  lines.get(i);
            Path path = new Path();
            path.moveTo(points.get(0).x,points.get(0).y);
            for(int j = 0; j<points.size();j++)
            {
                DotPoint currentPoint = points.get(j);
                if(j==0)
                {
                    path.moveTo(currentPoint.x,currentPoint.y);
                }
                else if(j <points.size()-1)
                {
                    DotPoint nextPoint = points.get(j+1);
                    path.quadTo(currentPoint.x,currentPoint.y, nextPoint.x,nextPoint.y);
                }
                else
                {
                    path.lineTo(currentPoint.x,currentPoint.y);
                }
            }
            c.drawPath(path,tPaint);
        }
        if(currentPoint!=null)
        {
            Path path = new Path();
            for(int i=0;i<currentPoint.size();i++)
            {
                DotPoint point = currentPoint.get(i);
                if(i==0)
                    path.moveTo(point.x,point.y);
                else if(i<currentPoint.size()-1)
                {
                    DotPoint nextPoint = currentPoint.get(i+1);
                    path.quadTo(point.x,point.y,nextPoint.x,nextPoint.y);
                }
                else
                {
                    path.lineTo(point.x,point.y);
                }
            }
            c.drawPath(path,tPaint);
        }
        Log.e("canvas",Integer.toString(canvas.getWidth()));
        Log.e("canvas",Integer.toString(c.getWidth()));
        canvas.drawBitmap(output,0,0,new Paint());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int count = event.getPointerCount();
        if(count==1) {
            float x = event.getX()/mScaleFactor;
            float y = event.getY()/mScaleFactor;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                currentPoint = new ArrayList<>();
                DotPoint dotPoint = new DotPoint((int) x, (int) y);
                currentPoint.add(dotPoint);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                currentPoint.add(new DotPoint((int) x, (int) y));
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                lines.add(currentPoint);
            }
        }
        else
            mScaleDetector.onTouchEvent(event);
        return true;
    }

    public Bitmap getResult()
    {
        return output;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor,2.5f));
            invalidate();
            return true;
        }
    }
    public void setPaintWidth(float w)
    {
        width=w;
        tPaint.setStrokeWidth(w);
    }
    public void setBlur(float b) {
        blur = b;
        BlurMaskFilter filter = new BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL);
        tPaint.setMaskFilter(filter);
    }

    public float getPaintWidth()
    {
        return width;
    }
    public float getPaintBlur()
    {
        return blur;
    }
}
