package com.example.imageprocesssdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActiveView extends androidx.appcompat.widget.AppCompatImageView {

    private Bitmap mBitmap;
    private ScaleGestureDetector mScaleDetector;
    private Paint mBitmapPaint;
    private float mScaleFactor=1.f;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ActiveView extends View {

    private Bitmap mBitmap;

    private Paint mBitmapPaint;

    private int nAngle;
    private int previousAngle;
    private int currentAngle;
    private float oldXvalue;
    private float oldYvalue;
    private ViewGroup parent;
    private IActiveView iActivity;

    private List<Point> points;
    public enum state {Move, Rotate,Idle};
    public state currentState;

    protected void init()
    {
        parent = (ViewGroup)this.getParent();
        mScaleDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        points=new ArrayList<>();

    public enum state {Move, Rotate,Idle};
    public state currentState;
    protected void init()
    {
        parent = (ViewGroup)this.getParent();

        iActivity = (IActiveView)getContext();
        if(parent==null)
            Log.d("log_test","noParent");
        mBitmapPaint = new Paint();
        nAngle=0;
        currentState = state.Idle;
        this.setY(120);
        this.setX(120);
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
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(setSize(b.getWidth(),b.getHeight()),setSize(b.getWidth(),b.getHeight()));
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
        int parentWidth = ((ViewGroup)this.getParent()).getWidth();
        int parentHeight = ((ViewGroup)this.getParent()).getHeight();
        if(currentState==state.Idle){
            Toast.makeText(getContext(), "Selected",Toast.LENGTH_SHORT).show();
            iActivity.getTouchedView(this);
        }
        if(currentState==state.Move) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                oldXvalue = event.getX();
                oldYvalue = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                this.setX(this.getX() + (event.getX()) - (this.getWidth() / 2));
                this.setY(this.getY() + (event.getY()) - (this.getHeight() / 2));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (this.getX() < 0)
                    this.setX(0);
                else if ((this.getX() + this.getWidth()) > parentWidth) {
                    this.setX(parentWidth - this.getWidth());
                }
                if (this.getY() < 0)
                    this.setY(0);
                else if ((this.getY() + this.getHeight()) > parentHeight)
                    this.setY(parentHeight - this.getHeight());
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

                Log.e("angle","angle is...");
                invalidate();
            }
        }
        mScaleDetector.onTouchEvent(event);
        return true;
    }
    public void setCurrentBitmap(Bitmap resultingImage)
    {
        mBitmap = resultingImage;
        Integer aa = getWidth();
        Log.e("width",aa.toString());
        invalidate();
    }

    public void setBitmap(byte[] imgByte)
    {
        mBitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(mScaleFactor,mScaleFactor);
        canvas.drawColor(Color.BLUE);
        canvas.rotate(nAngle,canvas.getWidth()/2, canvas.getHeight()/2);
        canvas.drawBitmap(mBitmap,(canvas.getWidth()-mBitmap.getWidth())/2,(canvas.getHeight()-mBitmap.getHeight())/2,mBitmapPaint);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentState=state.Idle;
            mScaleFactor = detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor,5.0f));
            invalidate();
            return true;
        }
    }

                invalidate();
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(nAngle,canvas.getWidth()/2, canvas.getHeight()/2);
        canvas.drawBitmap(mBitmap,(canvas.getWidth()-mBitmap.getWidth())/2,(canvas.getHeight()-mBitmap.getHeight())/2,mBitmapPaint);
    }

}
