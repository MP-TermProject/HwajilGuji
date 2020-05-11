package com.chlee.hwajilguji.vision;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chlee.hwajilguji.R;

import org.pytorch.Module;

import static com.chlee.hwajilguji.vision.PytorchTestActivity.fetchModelFile;

@SuppressLint("Registered")
public class testActivity extends AppCompatActivity {
        private static final int REQUEST_CODE_CHOOSE = 23;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pytorch_test);

        Button buttonLoadImage = (Button) findViewById(R.id.button);
        Button detectButton = (Button) findViewById(R.id.detect);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

        detectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bitmap bitmap = null;
                Module module = null;

                //Getting the image from the image view
                ImageView imageView = (ImageView) findViewById(R.id.image);
                try{
                    bitmap=BitmapFactory.decodeStream(getAssets().open("image.jpg"));
                    module = Module.load(fetchModelFile(testActivity.this, "generator_mobile.pt"));
                } catch (Exception e) {
                    finish();
                }
            }
        });
    }
}
