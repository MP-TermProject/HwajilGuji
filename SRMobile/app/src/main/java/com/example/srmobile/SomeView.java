package com.example.srmobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SomeView extends View implements View.OnTouchListener {
    private Paint paint;
    public List<DotPoint> dotPoints;
    int DIST = 2;
    boolean flgPathDraw = true;

    MainActivity mainActivity;
    DotPoint mfirstpoint = null;
    boolean bfirstpoint = false;

    DotPoint mlastpoint = null;

    Bitmap bitmap;
    byte[] byteArray;

    Context mContext;
    ISomeView returnPage;
    int height;
    int width;
    int originWidth;
    int originHeight;

    int pointX;
    int pointY;

    @SuppressLint("WrongThread")
    public SomeView(Context c, ISomeView parent) {
        super(c);
        returnPage = parent;
        mContext = c;
        mainActivity = MainActivity.singletone;
        Log.e("load", "createThis");
        width = mainActivity.screenWidth;
        height = mainActivity.screenHeight;

        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.bird_mid);//비트맵도 인풋으로 받기
        float scale = (float) ((width / (float) original.getWidth()));//이미지가 결국, 300, 300 size가 됨.
        originHeight = original.getHeight();
        originWidth = original.getWidth();

        int image_w = (int) (original.getWidth() * scale);
        int image_h = (int) (original.getHeight() * scale);

        bitmap = Bitmap.createScaledBitmap(original, image_w, image_h, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byteArray = stream.toByteArray();

        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        this.setOnTouchListener(this);
        dotPoints = new ArrayList<DotPoint>();

        bfirstpoint = false;
    }

    public SomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        this.setOnTouchListener(this);
        dotPoints = new ArrayList<DotPoint>();
        bfirstpoint = false;

    }

    @SuppressLint("WrongThread")
    public void setBitmap(Bitmap get_img) {
        Bitmap original = get_img;
        float scale = (float) ((width / (float) original.getWidth()));
        originHeight = original.getHeight();
        originWidth = original.getWidth();
        float ratio = (float) originHeight / originWidth;
        int image_w = (int) (original.getWidth() * scale);
        int image_h = (int) ((float) image_w * ratio);
        Log.e("width", Integer.toString(originWidth));
        Log.e("width", Integer.toString(originHeight));
        Log.e("width", Float.toString(ratio));
        bitmap = Bitmap.createScaledBitmap(original, image_w, image_h, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byteArray = stream.toByteArray();
        Log.e("setBitmap", Integer.toString(bitmap.getWidth()));
        setFocusable(true);
        setFocusableInTouchMode(true);
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);

        Path path = new Path();
        boolean first = true;

        for (int i = 0; i < dotPoints.size(); i += 2) {
            DotPoint dotPoint = dotPoints.get(i);
            if (first) {
                first = false;
                path.moveTo(dotPoint.x, dotPoint.y);
            } else if (i < dotPoints.size() - 1) {
                DotPoint next = dotPoints.get(i + 1);
                path.quadTo(dotPoint.x, dotPoint.y, next.x, next.y);
            } else {
                mlastpoint = dotPoints.get(i);
                path.lineTo(dotPoint.x, dotPoint.y);
            }
        }
        canvas.drawPath(path, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {

        DotPoint dotPoint = new DotPoint();
        dotPoint.x = (int) event.getX();
        dotPoint.y = (int) event.getY();

        if (flgPathDraw) {

            if (bfirstpoint) {

                if (comparepoint(mfirstpoint, dotPoint)) {
                    dotPoints.add(mfirstpoint);
                    flgPathDraw = false;
                    showcropdialog();
                } else {
                    dotPoints.add(dotPoint);
                }
            } else {
                dotPoints.add(dotPoint);
            }

            if (!(bfirstpoint)) {
                mfirstpoint = dotPoint;
                bfirstpoint = true;
            }
        }
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mlastpoint = dotPoint;
            if (flgPathDraw) {
                if (dotPoints.size() > 12) {
                    if (!comparepoint(mfirstpoint, mlastpoint)) {
                        flgPathDraw = false;
                        dotPoints.add(mfirstpoint);
                        showcropdialog();
                    }
                }
            }
        }

        return true;
    }

    private boolean comparepoint(DotPoint first, DotPoint current) {
        int left_range_x = (int) (current.x - 3);
        int left_range_y = (int) (current.y - 3);

        int right_range_x = (int) (current.x + 3);
        int right_range_y = (int) (current.y + 3);

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            if (dotPoints.size() < 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void fillinPartofPath() {
        DotPoint dotPoint = new DotPoint();
        dotPoint.x = dotPoints.get(0).x;
        dotPoint.y = dotPoints.get(0).y;

        dotPoints.add(dotPoint);
        invalidate();
    }

    public void resetView() {
        dotPoints.clear();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        flgPathDraw = true;
        invalidate();
    }

    private void showcropdialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Bitmap resultImg = makeBitmap(dotPoints, byteArray);
                        returnPage.getBitmap(resultImg);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        bfirstpoint = false;
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Crop Image?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
                .setCancelable(false);
    }

    public Bitmap makeBitmap(List<DotPoint> dotPoints, byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        DisplayMetrics dm = new DisplayMetrics();
        Log.e("windowSize", Integer.toString(width));
        int widthOfScreen = width;
        float ratio = (float) originHeight / originWidth;
        int heightOfScreen = (int) (width * (ratio));

        Bitmap resultingImage = Bitmap.createBitmap(widthOfScreen, heightOfScreen, bitmap.getConfig());
        Canvas canvas = new Canvas(resultingImage);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Path path = new Path();
        for (int i = 0; i < dotPoints.size(); i++) {
            path.lineTo(dotPoints.get(i).x, dotPoints.get(i).y);
        }
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        resultingImage = Bitmap.createScaledBitmap(resultingImage, originWidth, originHeight, true);
        return resultingImage;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        pointX = getWidth();
        pointY = getHeight();
        Integer Y = pointY;
    }
}


class DotPoint {

    public float dy;
    public float dx;
    float x, y;

    public DotPoint() {

    }

    public DotPoint(float _x, float _y) {
        x = _x;
        y = _y;
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}