package com.example.srmobile.sr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.srmobile.MainActivity;
import com.example.srmobile.R;
import com.example.srmobile.RectCrop;
import com.example.srmobile.ResultPage;
import com.zhihu.matisse.Matisse;


@SuppressLint({"Registered", "CheckResult"})
public class test_SRActivity extends AppCompatActivity {

    ImageGenerator imagegenerator;

    private static final int REQUEST_CODE_CHOOSE = 23;

    ImageView imageView2;
    Button submitBtn;
    Bitmap result;
    MainActivity mainActivity;
    com.example.srmobile.ImageGenerator generator;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_sr);

        mainActivity = MainActivity.singletone;
        generator = mainActivity.generator;
        imagegenerator = new ImageGenerator(Utils.assetFilePath(this, "generator_mobile_700.pt"));
        ImageView imageView = (ImageView) findViewById(R.id.sampleImageView);
        Button buttonLoadImage = (Button) findViewById(R.id.loadBtn);
        Button decisionSRBtn = (Button) findViewById(R.id.decisionSRBtn);


//        imageView2 = findViewById(R.id.convertimage);

        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras(); // bundle에서 꺼낼 때
        if (myBundle != null) {
            String path = myBundle.getString("dataID");
            Log.d("bundle", path);


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
            public void onClick(View arg0) {
//                TextView textView = findViewById(R.id.result_text);
//                textView.setText("");
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
                    Log.d("result", String.valueOf(result));
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

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This functions return the selected image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            Bundle myBundle = data.getExtras(); // bundle에서 꺼낼 때
            if (myBundle != null) {
                String val2 = myBundle.getString("dataID");
                Log.d("bundle", val2);
                ImageView imageView = (ImageView) findViewById(R.id.image);
                val2 = Matisse.obtainPathResult(data).get(0);
                Log.d("Text", Matisse.obtainPathResult(data).get(0));
                if (val2 != null) {
                    Glide.with(this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(val2)
                            .apply(new RequestOptions() {
                                {
                                    override(Target.SIZE_ORIGINAL);
                                }
                            })
                            .into(imageView);

                } else
                    Toast.makeText(this, "Uri is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("ttt", "wrong");
        }
    }
}
