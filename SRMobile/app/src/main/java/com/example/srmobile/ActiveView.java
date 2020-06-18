package com.example.srmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ActiveView extends androidx.appcompat.widget.AppCompatImageView{

    public boolean lock=false;
    public int alpha = 255;
    private Bitmap mBitmap;
    private ScaleGestureDetector mScaleDetector;
    private Paint mBitmapPaint;
    private float mScaleFactor=1.f;
    private int currentBitmapWidth;
    private int currentBitmapHeight;


    private int nAngle;
    private int previousAngle;
    private int currentAngle;
    private int defaultSize;
    private float oldXvalue;
    private float oldYvalue;
    private ViewGroup parent;
    private IActiveView iActivity;
    private List<DotPoint> dotPoints;
    public enum state {Move, Rotate,Idle,UpScale};
    public state currentState;

    protected void init()
    {
        parent = (ViewGroup)this.getParent();
        mScaleDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        dotPoints =new ArrayList<>();
        mBitmapPaint = new Paint();
        nAngle=0;
        currentState = state.Idle;
        this.setY(120);
        this.setX(120);
    }
    public void setiActivity(IActiveView inter)
    {
        iActivity=inter;
    }
    public  ActiveView(Context c)
    {
        super(c);
        init();
    }
    public ActiveView(Context c, AttributeSet a)
    {
        super(c, a);
        init();
    }
    public ActiveView(Context c, AttributeSet a, int d)
    {
        super(c,a,d);
        init();
    }
    public ActiveView(Context c, AttributeSet a, Bitmap b)
    {
        super(c,a);
        setImage(b);
        init();
    }
    public void setCurrentState(state s)
    {
        currentState =s;
    }
    public void setImage(Bitmap b)
    {
        mBitmap = b;

        Log.e("isCalled","changeBitmap");
        currentBitmapHeight=(int)(mBitmap.getHeight()*mScaleFactor);
        currentBitmapWidth=(int)(mBitmap.getWidth()*mScaleFactor);
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        defaultSize = setSize(b.getWidth(),b.getHeight());
        setScale(setSize(b.getWidth(),b.getHeight()),setSize(b.getWidth(),b.getHeight()));
        invalidate();
    }
    public void setScale(int w, int h)
    {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        Log.e("debug","isComplete");
        this.setLayoutParams(params);
    }

    protected int setSize(int w, int h)
    {
        w = (w+1)/2;
        h = (h+1)/2;
        int newSize = (int)Math.pow(w,2)+(int)Math.pow(h,2);
        return (int)Math.sqrt(newSize)*2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(lock)
            return false;
        int parentWidth = ((ViewGroup)this.getParent()).getWidth();
        int parentHeight = ((ViewGroup)this.getParent()).getHeight();

        iActivity.getTouchedView(this);

        if(currentState==state.Move) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                oldXvalue = event.getX();
                oldYvalue = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                this.setX(this.getX() + (event.getX()) - (this.getWidth() / 2));
                this.setY(this.getY() + (event.getY()) - (this.getHeight() / 2));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (this.getX()+(mBitmap.getWidth()*2/3) < 0)
                    this.setX(-(mBitmap.getWidth()*2/3));
                else if ((this.getX() + this.getWidth()-(mBitmap.getWidth()*2/3)) > parentWidth) {
                    this.setX(parentWidth - (mBitmap.getWidth()*2/3));
                }
                if (this.getY()+(mBitmap.getHeight()*2/3) < 0)
                    this.setY(-(mBitmap.getHeight()*2/3));
                else if ((this.getY() + this.getHeight()-(mBitmap.getHeight()*2/3)) > parentHeight)
                    this.setY(parentHeight - (mBitmap.getHeight()*2/3));
            }
        }
        if(currentState==state.Rotate) {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                previousAngle=nAngle;
                currentAngle=0;
                oldXvalue = event.getX();
                oldYvalue = event.getY();
            }else if(event.getAction()==MotionEvent.ACTION_MOVE){
                double dx = event.getX()-oldXvalue;//this.getX() + (event.getX()) - (this.getWidth() / 2)-event.getX();
                double dy = event.getY()-oldYvalue;//(this.getY() + (event.getY()) - (this.getHeight() / 2))-event.getY();
                currentAngle = (int)Math.toDegrees(Math.atan2(dy,dx));
                nAngle=(previousAngle+currentAngle)%360;
                invalidate();
            }
        }
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    public Bitmap getBitmap()
    {
        return mBitmap;
    }

    public void setCurrentBitmap(Bitmap resultingImage)
    {
        mBitmap = resultingImage;
    }
    /*
    public void setBitmap(byte[] imgByte)
    {
        mBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(nAngle,canvas.getWidth()/2, canvas.getHeight()/2);
        int left=(canvas.getWidth()-currentBitmapWidth);
        int left_margin = (canvas.getWidth()-currentBitmapWidth)/2;
        int top_margin = (canvas.getHeight()-currentBitmapWidth)/2;
        canvas.drawBitmap(mBitmap, null,new Rect(left_margin, top_margin,(canvas.getWidth()-left_margin),(canvas.getHeight()-top_margin)),mBitmapPaint);
    }
    public void setTransparent(int transparent)
    {
        mBitmapPaint.setAlpha(transparent);
        alpha = transparent;
        invalidate();
    }
    /*
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putFloat("X",this.getX());
        bundle.putFloat("Y",this.getY());
        bundle.putParcelable("Bitmap",mBitmap);
        bundle.putFloat("ScaleFactor",mScaleFactor);
        return super.onSaveInstanceState();
    }
    */

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentState=state.UpScale;
            mScaleFactor *= detector.getScaleFactor();
            Float sf = mScaleFactor;
            Log.e("Scale",sf.toString());

            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor,2.5f));
            currentBitmapWidth = (int)(mBitmap.getWidth()*mScaleFactor);
            currentBitmapHeight = (int)(mBitmap.getHeight()*mScaleFactor);
            float size = defaultSize*mScaleFactor;
            setScale((int)size,(int)size);

            invalidate();
            Integer Scale = (int)size;
            Log.e("Scale_width",Scale.toString());
            return true;
        }
    }
}
