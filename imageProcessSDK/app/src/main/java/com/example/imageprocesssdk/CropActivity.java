package com.example.imageprocesssdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class CropActivity extends AppCompatActivity {
    LinearLayout someLayout;
    ImageView compositeImageView;
    SomeView someView;
    boolean crop;
    List<DotPoint> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        someLayout = findViewById(R.id.show);
        someView = new SomeView(this);
        someView.setCropActivity(this);
        someLayout.addView(someView);
    }
    public void setTitle(List<DotPoint> arr, byte[] byteArray)
    {
        arrayList=arr;
        MainActivity.singletone.setPath(arr, byteArray);
        setResult(102);
        finish();
    }
    /*
    public void Result()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            crop = extras.getBoolean("crop");
        }
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        int widthOfscreen = 0;
        int heightOfScreen = 0;

        DisplayMetrics dm = new DisplayMetrics();
        try {
            getWindowManager().getDefaultDisplay().getMetrics(dm);
        } catch (Exception ex) {
        }
        widthOfscreen = dm.widthPixels;
        heightOfScreen = dm.heightPixels;

        //compositeImageView = (ImageView) findViewById(R.id.imageview);


        Bitmap resultingImage = Bitmap.createBitmap(widthOfscreen, heightOfScreen, bitmap.getConfig());

        Canvas canvas = new Canvas(resultingImage);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Path path = new Path();
        for (int i = 0; i < SomeView.points.size(); i++) {
            path.lineTo(SomeView.points.get(i).x, SomeView.points.get(i).y);
        }
        canvas.drawPath(path, paint);
        if (crop) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);
        compositeImageView.setImageBitmap(resultingImage);

        /*
        Intent intent = new Intent();
        intent.putExtra("img",resultingImage);
        setResult(102, intent);
    }*/
}
