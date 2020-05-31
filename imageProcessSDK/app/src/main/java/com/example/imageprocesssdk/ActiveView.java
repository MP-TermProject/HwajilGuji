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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActiveView extends androidx.appcompat.widget.AppCompatImageView {

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
    private List<Point> points;
    public enum state {Move, Rotate,Idle,UpScale};
    public state currentState;

    protected void init()
    {
        parent = (ViewGroup)this.getParent();
        mScaleDetector = new ScaleGestureDetector(getContext(),new ScaleListener());
        points=new ArrayList<>();
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
        currentBitmapHeight=(int)(mBitmap.getHeight()*mScaleFactor);
        currentBitmapWidth=(int)(mBitmap.getWidth()*mScaleFactor);
        mBitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(setSize(b.getWidth(),b.getHeight()),setSize(b.getWidth(),b.getHeight()));
        defaultSize = setSize(b.getWidth(),b.getHeight());

        setScale(setSize(b.getWidth(),b.getHeight()),setSize(b.getWidth(),b.getHeight()));
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
    public void setCurrentBitmap(Bitmap resultingImage)
    {
        mBitmap = resultingImage;
        Integer aa = mBitmap.getWidth();
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
        canvas.rotate(nAngle,canvas.getWidth()/2, canvas.getHeight()/2);

        canvas.drawBitmap(mBitmap,(canvas.getWidth()-currentBitmapWidth)/2,(canvas.getHeight()-currentBitmapHeight)/2,mBitmapPaint);
        Integer cSize = canvas.getWidth();
        Integer BSize = currentBitmapWidth;
        Log.e("ScaleCanvas",cSize.toString());
        Log.e("ScaleImg",BSize.toString());
        Integer left=(canvas.getWidth()-currentBitmapWidth)/2;
        Log.e("Scale_left",left.toString());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentState=state.UpScale;
            mScaleFactor *= detector.getScaleFactor();
            Float sf = mScaleFactor;
            Log.e("Scale",sf.toString());
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor,2.0f));
            currentBitmapWidth = (int)(mBitmap.getWidth()*mScaleFactor);
            currentBitmapHeight = (int)(mBitmap.getHeight()*mScaleFactor);
            float size = defaultSize*mScaleFactor;
            setScale((int)size+100,(int)size+100);
            invalidate();
            Integer Scale = (int)size;
            Log.e("Scale_width",Scale.toString());
            return true;
        }
    }
}
