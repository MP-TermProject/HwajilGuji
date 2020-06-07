package com.example.imageprocesssdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RectCropActivity extends AppCompatActivity {

    Button cropBtn;
    RectCrop rc;
    ImageView resultImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rect_crop);
        resultImg=findViewById(R.id.resultImg);
        cropBtn = findViewById(R.id.submitResult);
        rc=findViewById(R.id.rectCropScreen);
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultImg.setImageBitmap(rc.CropImage());
            }
        });
    }
}
