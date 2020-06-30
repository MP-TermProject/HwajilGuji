package com.example.srmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.srmobile.sr.ImageGenerator;

public class RectSRActivity extends AppCompatActivity {
    Button submitBtn;
    Bitmap result;
    MainActivity mainActivity;
    RectCrop cropView;
    ImageGenerator generator;
    public static RectSRActivity rectSRActivity;

    private void init() {
        submitBtn = findViewById(R.id.rcsrSubmitBtn);
        mainActivity = MainActivity.singletone;
        cropView = findViewById(R.id.rcSrRectCrop);
        generator = mainActivity.generator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rectSRActivity = this;
        setContentView(R.layout.activity_rect_s_r);
        init();
        cropView.setBitmap(mainActivity.inputImg);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = cropView.CropImage();
                if (result != null) {
                    result = generator.ImageProcess(result, mainActivity.width, mainActivity.height);
                    ResultPage resultPage = new ResultPage();
                    resultPage.setResultImage(result);
                    loadFragment(resultPage);
                }
            }
        });
        /*
        RectCropFragment rectCropFragment = new RectCropFragment();
        Bitmap input = getCurrentActiveBitmap();
        if(input!=null)
        {
            rectCropFragment.setCropBitmap(input);
            volitileFragment(rectCropFragment);
        }
        */
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rcsrMainLayout, fragment).commit();
        transaction.addToBackStack(null);
    }
}
