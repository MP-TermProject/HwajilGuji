package com.chlee.hwajilguji.vision;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chlee.hwajilguji.R;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.chlee.hwajilguji.vision.PytorchTestActivity.fetchModelFile;

@SuppressLint("Registered")
public class testActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;
    Module model;
    float[] mean = {0f, 0f, 0f};
    float[] std = {1f, 1f, 1f};

    ImageView imageView2;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pytorch_test);

        Button buttonLoadImage = (Button) findViewById(R.id.button);
        Button detectButton = (Button) findViewById(R.id.detect);

        imageView2=findViewById(R.id.convertimage);

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

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                //Getting the image from the image view
                ImageView imageView = (ImageView) findViewById(R.id.image);

                try {
                    bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    model = Module.load(fetchModelFile(testActivity.this, "generator_mobile.pt"));
                } catch (Exception e) {
                    finish();
                }

                ImageProcess(bitmap, 720, 1280);

            }
        });
    }

    public Tensor preprocess(Bitmap bitmap, int width, int height) {
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, this.mean, this.std);
    }

    public Bitmap ImageProcess(Bitmap bitmap, int width, int height) {
        Tensor tensor = preprocess(bitmap, width, height);

        IValue inputs = IValue.from(tensor);
        Tensor output = model.forward(inputs).toTensor();

        float[] result = output.getDataAsFloatArray();
        List<Float> RArray = new ArrayList<>();
        List<Float> GArray = new ArrayList<>();
        List<Float> BArray = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < width * height; j++) {
                if (i == 0)
                    RArray.add(result[index]);
                else if (i == 1) GArray.add(result[index]);
                else BArray.add(result[index]);
                index++;
            }
        }
        Log.d("result", "ImageProcess: convert ok");
        return arrayToBitmap(RArray, GArray, BArray, width, height);
    }

    private Bitmap arrayToBitmap(List<Float> R, List<Float> G, List<Float> B, int width, int height) {
        byte alpha = (byte) 255;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteBuffer byteBuffer = ByteBuffer.allocate(width * height * 4);

        for (int index = 0; index < width * height; index++) {
            byte r_value = (byte) (R.get(index) * 255);
            byte g_value = (byte) (G.get(index) * 255);
            byte b_value = (byte) (B.get(index) * 255);
            byteBuffer.put(4 * index, r_value);
            byteBuffer.put(4 * index + 1, g_value);
            byteBuffer.put(4 * index + 2, b_value);
            byteBuffer.put(4 * index + 3, alpha);
        }
        bmp.copyPixelsFromBuffer(byteBuffer);


        //Setting the URI so we can read the Bitmap from the image
        imageView2.setImageURI(null);
        imageView2.setImageBitmap(bmp);
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //This functions return the selected image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //Setting the URI so we can read the Bitmap from the image
            imageView.setImageURI(null);
            imageView.setImageURI(selectedImage);


        }


    }

}
