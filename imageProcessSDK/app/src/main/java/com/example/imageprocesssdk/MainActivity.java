package com.example.imageprocesssdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements IActiveView {

    Bitmap img;
    FrameLayout frameLayout;
    Button move;
    Button rotate;
    protected ActiveView currentProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = findViewById(R.id.frame);
        move = findViewById(R.id.moveBtn);
        rotate  = findViewById(R.id.rotBtn);
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
    }

    @Override
    public void getTouchedView(ActiveView v) {
        if(currentProcess!=null&&v != currentProcess)
            setState(ActiveView.state.Idle);
        currentProcess = v;
    }

    @Override
    public void setState(ActiveView.state s) {
        if(currentProcess!=null)
            currentProcess.setCurrentState(s);
        else
            Toast.makeText(getApplicationContext(), "오브젝트가 선택되지 않았습니다",Toast.LENGTH_SHORT).show();
    }
}
