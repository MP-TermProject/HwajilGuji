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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.srmobile.R;
import com.zhihu.matisse.Matisse;


@SuppressLint({"Registered","CheckResult"})
public class test_SRActivity extends AppCompatActivity {

    ImageGenerator imagegenerator;

    private static final int REQUEST_CODE_CHOOSE = 23;

    ImageView imageView2;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_sr);


        imagegenerator = new ImageGenerator(Utils.assetFilePath(this, "generator_mobile_700.pt"));

        Button buttonLoadImage = (Button) findViewById(R.id.button);
        Button detectButton = (Button) findViewById(R.id.detect);


        imageView2 = findViewById(R.id.convertimage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent myCallerIntent = getIntent();
        Bundle myBundle = myCallerIntent.getExtras(); // bundle에서 꺼낼 때
        if (myBundle != null) {
            String path = myBundle.getString("dataID");
            Log.d("bundle", path);
            ImageView imageView = (ImageView) findViewById(R.id.image);

            Glide.with(this)
                    .asBitmap() // some .jpeg files are actually gif
                    .load(path)
                    .apply(new RequestOptions() {{
                            override(Target.SIZE_ORIGINAL);
                        }
                    })
                    .into(imageView);
        }

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TextView textView = findViewById(R.id.result_text);
                textView.setText("");
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                //Getting the image from the image view
                ImageView imageView = (ImageView) findViewById(R.id.image);

                try {
                    bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                } catch (Exception e) {
                    finish();
                }

                Bitmap bmp = imagegenerator.ImageProcess(bitmap, 400, 400);
                imageView2.setImageBitmap(bmp);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This functions return the selected image from gallery
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            ImageView imageView = (ImageView) findViewById(R.id.image);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//
//            //Setting the URI so we can read the Bitmap from the image
//            imageView.setImageURI(null);
//            imageView.setImageURI(selectedImage);
//
//        }

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
