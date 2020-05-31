package com.example.imageprocesssdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Display;
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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SomeView extends View implements View.OnTouchListener {
    private Paint paint;
    public List<Point> points;
    int DIST = 2;
    boolean flgPathDraw = true;

    MainActivity mainActivity;
    CropActivity cropActivity;
    Point mfirstpoint = null;
    boolean bfirstpoint = false;

    Point mlastpoint = null;

    Bitmap bitmap;
    byte[] byteArray;

    Context mContext;
    int window_w;
    int window_h;
    int height;
    int width;

    @SuppressLint("WrongThread")
    public SomeView(Context c) {
        super(c);

        mContext = c;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //height = 600;//출력될 사이즈.
        //width = 600;//출력될 사이즈. width, height받아서 처리.

        height= displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        Float size = getScaleY();
        Log.e("width_2",size.toString());

        float cc = window_h/height;
        Float d = cc;
        Log.e("scale",d.toString());
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.bird);//비트맵도 인풋으로 받기
        float scale = (float) ((width/(float)original.getWidth()));//이미지가 결국, 300, 300 size가 됨.

        int image_w = (int) (original.getWidth()*scale);
        int image_h = (int) (original.getHeight()*scale);

        bitmap = Bitmap.createScaledBitmap(original, image_w, image_h, true);
        Integer aa = bitmap.getHeight();
        Log.e("height",aa.toString());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byteArray = stream.toByteArray();

        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] { 10, 10 }, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        this.setOnTouchListener(this);
        points = new ArrayList<Point>();

        bfirstpoint = false;
    }
    public void setCropActivity(CropActivity c)
    {
        cropActivity=c;
    }
    public SomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLUE);

        this.setOnTouchListener(this);
        points = new ArrayList<Point>();
        bfirstpoint = false;

    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);

        Path path = new Path();
        boolean first = true;

        for (int i = 0; i < points.size(); i += 2) {
            Point point = points.get(i);
            if (first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else if (i < points.size() - 1) {
                Point next = points.get(i + 1);
                path.quadTo(point.x, point.y, next.x, next.y);
            } else {
                mlastpoint = points.get(i);
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, paint);
    }

    public boolean onTouch(View view, MotionEvent event) {

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        if (flgPathDraw) {

            if (bfirstpoint) {

                if (comparepoint(mfirstpoint, point)) {
                    // points.add(point);
                    points.add(mfirstpoint);
                    flgPathDraw = false;
                    showcropdialog();
                } else {
                    points.add(point);
                }
            } else {
                points.add(point);
            }

            if (!(bfirstpoint)) {
                mfirstpoint = point;
                bfirstpoint = true;
            }
        }

        invalidate();
        Log.e("Hi  ==>", "Size: " + point.x + " " + point.y);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("Action up*******~~~>>>>", "called");
            mlastpoint = point;
            if (flgPathDraw) {
                if (points.size() > 12) {
                    if (!comparepoint(mfirstpoint, mlastpoint)) {
                        flgPathDraw = false;
                        points.add(mfirstpoint);
                        showcropdialog();
                    }
                }
            }
        }

        return true;
    }

    private boolean comparepoint(Point first, Point current) {
        int left_range_x = (int) (current.x - 3);
        int left_range_y = (int) (current.y - 3);

        int right_range_x = (int) (current.x + 3);
        int right_range_y = (int) (current.y + 3);

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            if (points.size() < 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void fillinPartofPath() {
        Point point = new Point();
        point.x = points.get(0).x;
        point.y = points.get(0).y;

        points.add(point);
        invalidate();
    }

    public void resetView() {
        points.clear();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        flgPathDraw = true;
        invalidate();
    }
    public Bitmap getBitmap()
    {
        return BitmapFactory.decodeByteArray(byteArray,0, byteArray.length );
    }

    private void showcropdialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        MainActivity.singletone.setPath(points,byteArray);
                        cropActivity.setTitle(points,byteArray);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        bfirstpoint = false;
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you Want to save Crop or Non-crop image?")
                .setPositiveButton("Crop", dialogClickListener)
                .setNegativeButton("Non-crop", dialogClickListener).show()
                .setCancelable(false);
    }
}


class Point {

    public float dy;
    public float dx;
    float x, y;

    @Override
    public String toString() {
        return x + ", " + y;
    }
}