package com.example.imageprocesssdk;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IActiveView {

    Bitmap img;
    FrameLayout frameLayout;
    Button move;
    Button rotate;
    Button newObj;
    ArrayList<ActiveView> BitmapList;
    protected ActiveView currentProcess;
    public static MainActivity singletone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singletone=this;
        setContentView(R.layout.activity_main);
        BitmapList = new ArrayList<>();

        frameLayout = findViewById(R.id.frame);
        move = findViewById(R.id.moveBtn);
        rotate  = findViewById(R.id.rotBtn);
        newObj = findViewById(R.id.newObj);
        Resources res =getResources();
        img = BitmapFactory.decodeResource(res, R.drawable.bird);
        img = Bitmap.createScaledBitmap(img, 300, 300, true);
        ActiveView activeView = new ActiveView(this);
        activeView.setImage(img);
        frameLayout.addView(activeView);

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(ActiveView.state.Move);
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(ActiveView.state.Rotate);
            }
        });
        newObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CropActivity.class);
                startActivityForResult(intent,102);
            }
        });

    }

    @Override
    public void getTouchedView(ActiveView v) {
        if(currentProcess!=null&&v != currentProcess)
            setState(ActiveView.state.Idle);
        currentProcess = v;
    }

    public void addNewActiveView(Bitmap b)
    {
        ActiveView activeView = new ActiveView(this);
        activeView.setImage(b);
        frameLayout.addView(activeView);
        BitmapList.add(activeView);
    }

    public void setPath(List<Point> points,byte[] byteArray)
    {
        if(currentProcess!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            DisplayMetrics dm = new DisplayMetrics();

            int widthOfScreen = 0;
            int heightOfScreen = 0;

            try {
                getWindowManager().getDefaultDisplay().getMetrics(dm);
            }catch(Exception ex){}
            widthOfScreen = dm.widthPixels;
            heightOfScreen = dm.heightPixels;

            Bitmap resultingImage = Bitmap.createBitmap(widthOfScreen, heightOfScreen, bitmap.getConfig());
            Canvas canvas = new Canvas(resultingImage);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Path path = new Path();
            for (int i = 0; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);//offset만큼 더해주면 ok
            }
            canvas.drawPath(path, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, 0, 0, paint);

            resultingImage = Bitmap.createScaledBitmap(resultingImage,300,400,true);

            currentProcess.setCurrentBitmap(resultingImage);
        }
        else
            Toast.makeText(this,"오브젝트를 선택해주세요",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void setState(ActiveView.state s) {
        if(currentProcess!=null)
            currentProcess.setCurrentState(s);
        else
            Toast.makeText(getApplicationContext(), "오브젝트가 선택되지 않았습니다",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==102)
        {
            Toast.makeText(this, "qwert",Toast.LENGTH_SHORT).show();
        }
    }
}
