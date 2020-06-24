package com.example.srmobile.sr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.srmobile.MainActivity;
import com.example.srmobile.ProcessDecision;
import com.example.srmobile.R;
import com.example.srmobile.ResultPage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.IOException;


@SuppressLint({"Registered", "CheckResult"})
public class SRActivity extends AppCompatActivity {
    static final int requestcode = 180;
    Bitmap result;
    MainActivity mainActivity;
    ImageGenerator generator;
    String path;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr);

        mainActivity = MainActivity.singletone;
        generator = mainActivity.generator;

        ImageView imageView = (ImageView) findViewById(R.id.sampleImageView);
        Button buttonLoadImage = (Button) findViewById(R.id.loadBtn);
        Button decisionSRBtn = (Button) findViewById(R.id.decisionSRBtn);


        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras(); // bundle에서 꺼낼 때
        if (myBundle != null) {
            String path = myBundle.getString("dataID");
//            Log.d("bundle", path);

            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(path)
                    .apply(new RequestOptions() {
                        {
                            override(Target.SIZE_ORIGINAL);
                        }
                    })
                    .into(imageView);
        }

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                mainActivity.ImagePicker(requestcode);
                
            }
        });

        decisionSRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                try {
                    bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    result = generator.ImageProcess(bitmap, mainActivity.width, mainActivity.height);
                    ResultPage resultPage = new ResultPage();
                    resultPage.setResultImage(result);
//                    Log.d("result", String.valueOf(result));
                    loadFragment(resultPage);
                } catch (Exception e) {
                    finish();
                }
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.srLayout, fragment).commit();
        transaction.addToBackStack(null);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 180) {
            path = Matisse.obtainPathResult(data).get(0);
            Log.d("result", String.valueOf(path));
            if (path != null) {
                ImageView imageView = (ImageView) findViewById(R.id.sampleImageView);
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(path)
                        .apply(new RequestOptions() {
                            {
                                override(Target.SIZE_ORIGINAL);
                            }
                        })
                        .into(imageView);

            } else
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
    }
}
