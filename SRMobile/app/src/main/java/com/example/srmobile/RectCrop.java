package com.example.srmobile;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

public class RectCrop extends androidx.appcompat.widget.AppCompatImageView {
    DotPoint leftTop;
    DotPoint rightBot;
    float left;
    float top;
    float scaleFactor;
    float imgCanvasRatio;
    float imgCanvasRatio_h;
    private float oldXValue;
    private float oldYValue;
    private float dx;
    private float dy;
    float margin;
    float currentWidth;
    float currentheight;
    float widthHeightRatio;
    Bitmap currentBitmap;
    Paint mBitmapPaint;
    Paint rectPaint;
    private float minRatio = 0.3f;
    int width;
    int height;

    enum State {
        Move, Scale
    }

    State currentState;
    private float mScaleFactor = 1.0f;

    private ScaleGestureDetector mScaleDetector;

    private void init() {
        //
        Resources res = getResources();
        currentBitmap = BitmapFactory.decodeResource(res, R.drawable.bird_mid);
        currentBitmap = Bitmap.createScaledBitmap(currentBitmap, 500, 500, true);
        widthHeightRatio = 1.0f;
        //
        height = (int) (MainActivity.singletone.screenHeight * widthHeightRatio);
        width = MainActivity.singletone.screenWidth;
        left = 0;
        top = 0;
        dx = 0f;
        dy = 0f;
        imgCanvasRatio = 1f;
        imgCanvasRatio_h = 1f;
        scaleFactor = 1f;
        margin = 200f;
        currentWidth = width;
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mBitmapPaint = new Paint();
        rectPaint = new Paint();
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(15f);
    }

    public RectCrop(Context c) {
        super(c);
        init();
    }

    public RectCrop(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    public void setBitmap(Bitmap bitmap) {
        currentBitmap = bitmap;
        widthHeightRatio = (float) currentBitmap.getHeight() / (float) currentBitmap.getWidth();
        Log.e("scale_set", Float.toString(widthHeightRatio));
        Log.d("scale", Integer.toString(currentBitmap.getWidth()));
        Log.d("scale", Integer.toString(currentBitmap.getHeight()));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);
        //Test
        Paint testPaint = new Paint();
        testPaint.setColor(Color.BLUE);
        //Test

        //canvas.scale(mScaleFactor,mScaleFactor);
        currentWidth = Math.max(mScaleFactor * width, width - (2 * margin));
        currentheight = currentWidth * widthHeightRatio;
        Log.e("scale", Float.toString(currentheight));//Math.max(mScaleFactor*height,width-(2*margin));
        Log.e("scale", Float.toString(width - (2 * margin)));
        imgCanvasRatio = currentBitmap.getWidth() / currentWidth;
        Rect rect = new Rect();
        rect.set((int) (left + dx), (int) (top + dy), (int) (currentWidth + (left + dx)), (int) (currentheight + (top + dy)));
        canvas.drawBitmap(currentBitmap, null, rect, mBitmapPaint);
        //canvas.scale(1/mScaleFactor,1/mScaleFactor);
        canvas.drawRect(margin, margin, width - margin, width - margin, rectPaint);
        Log.e("Scaler", Float.toString(imgCanvasRatio));
        //canvas.drawBitmap(currentBitmap,left, top,mBitmapPaint);
    }

    public Bitmap CropImage() {
        if (currentheight < (width - (2 * margin)) || currentWidth < (width - (2 * margin)))
            return null;
        Bitmap cropImage = Bitmap.createBitmap(currentBitmap, (int) ((-left + margin) * imgCanvasRatio), (int) ((-top + margin) * imgCanvasRatio), (int) ((width - 2 * margin) * imgCanvasRatio), (int) ((width - 2 * margin) * imgCanvasRatio));
        Log.e("left", Float.toString((-left + margin)));
        Log.e("left2", Float.toString(left));
        cropImage = Bitmap.createScaledBitmap(cropImage, 150, 150, true);
        return cropImage;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        Log.e("Log2", Integer.toString(pointerCount));
        if (pointerCount == 1)
            currentState = State.Move;
        if (currentState == State.Move) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.e("Log2", "Down");
                oldXValue = event.getX();
                oldYValue = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Log.e("Log2", "Move");
                dx = event.getX() - oldXValue;
                dy = event.getY() - oldYValue;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.e("Log2", "Up");
                left += dx;
                top += dy;
                left = Math.min(margin, left);
                top = Math.min(margin, top);
                if ((top + currentWidth) < width - margin)
                    top = ((width - margin) - currentWidth);
                if ((left + currentWidth) < (width - margin))
                    left = ((width - margin) - currentWidth);
                dx = 0f;
                dy = 0f;
            }
            invalidate();
        }
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            currentState = State.Scale;
            mScaleFactor *= detector.getScaleFactor();
            Float sf = mScaleFactor;
            Log.e("Scale", sf.toString());
            mScaleFactor = Math.max(minRatio, Math.min(mScaleFactor, 2.5f));
            invalidate();
            return true;
        }
    }
}
