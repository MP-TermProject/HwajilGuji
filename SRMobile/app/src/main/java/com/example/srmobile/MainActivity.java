package com.example.srmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    public static MainActivity singletone;
    int permissionRequestCode = 80;
    int cameraRequestCode = 100;
    int imageConvertRequestCode = 101;
    int galleryCode = 102;
    int result_ok = -1;
    int result_fail = 0;

    public int screenWidth;
    public int screenHeight;
    public int width = 150;
    public int height = 150;

    public ImageGenerator generator;
    ImageSelectionWay selectionWay;//id==1
    CameraAction cameraAction;//id==2
    Processing preprocess;//id==3
    Processing processing;//id==4
    ResultPage resultPage;//id==5
    HashMap<Integer, Fragment> fragmentHashMap;

    Bitmap inputImg;
    Bitmap resultImg;

    private String imageFilePath;
    private Uri photoUri;
    String path;
    Button cameraBtn;
    Button galleryBtn;
    Button configure;
    private static ImageView gallery_image;

    public void setScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    public void setPermission() {
        int cameraCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraCheck == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, permissionRequestCode);

        int readCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readCheck == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissionRequestCode);

        int writeCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeCheck == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCode);
    }

    public void init() {
        singletone = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setScreenSize();

        Log.e("screen", Integer.toString(screenWidth));

        Log.e("screen", Integer.toString(screenHeight));
        Drawable drawable = getDrawable(R.drawable.bird_mid);
        init();
        setPermission();
        setScreenSize();

        inputImg = ((BitmapDrawable) drawable).getBitmap();
        inputImg = Bitmap.createScaledBitmap(inputImg, width, height, false);
        resultImg = ((BitmapDrawable) drawable).getBitmap();
        resultImg = Bitmap.createScaledBitmap(resultImg, width, height, false);
        selectionWay = new ImageSelectionWay();
        cameraAction = new CameraAction();
        processing = new Processing();
        resultPage = new ResultPage();

        fragmentHashMap = new HashMap<>();
        fragmentHashMap.put(1, selectionWay);
        fragmentHashMap.put(2, cameraAction);
        fragmentHashMap.put(3, preprocess);
        fragmentHashMap.put(4, processing);
        fragmentHashMap.put(5, resultPage);


        //SRMobile_150_N.pt
        try {
            generator = new ImageGenerator(Utils.assetFilePath(this, "SRMobile_150_N.pt"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setFragment(1);
    }

    public void requestFoundImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ImagePicker();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        permissionRequestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            ImagePicker();
            // Permission has already been granted
        }
//        ImagePicker();
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, galleryCode);
    }

    public void setInputImg(Bitmap img) {
        inputImg = img;//Bitmap.createScaledBitmap(img, width, height, false);
    }

    public Bitmap getInputImg() {
        return inputImg;
    }

    public void setResultImg(Bitmap img) {
        resultImg = Bitmap.createScaledBitmap(img, width * 2, height * 2, false);
    }

    public Bitmap getResultImg() {
        return resultImg;
    }

    public void requestCameraActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = generateSampleImage();
                } catch (IOException e) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, cameraRequestCode);
                }
            }
        }
    }

    public File generateSampleImage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Test_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode) {

        }
    }


    public void volitileFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, fragment).commit();
        fragmentTransaction.addToBackStack(null);
    }

    public void setFragment(int fragment_id) {
        if (fragmentHashMap.containsKey(fragment_id)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment f = fragmentHashMap.get(fragment_id);
            transaction.replace(R.id.main_layout, f);
            transaction.addToBackStack(null);
            if (fragment_id == 5) {
                getSupportFragmentManager().popBackStack();

            }
            transaction.commit();
        } else
            Toast.makeText(getApplicationContext(), "해당 화면을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
    }

    private void ImagePicker() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                .maxSelectable(9)
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(galleryCode);
    }

    /*code 101_1 : convert process complete.
     * code 101_2 : convert process failed
     * code 102_1 : get_image from gallery
     *
     *
     * */
    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cameraRequestCode) {
            if (resultCode == result_ok) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    setInputImg(bitmap);
                    setFragment(4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
//        if (requestCode == galleryCode) {
//            if (resultCode == result_ok) {
//                try {
//                    InputStream in = getContentResolver().openInputStream(data.getData());
//                    Log.d("InputStream", String.valueOf(in));
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    Log.d("Bitmap", String.valueOf(img));
//                    in.close();
//                    setInputImg(img);
//                    setFragment(4);
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            } else if (resultCode == result_fail) {
//                Toast.makeText(getApplicationContext(), "취소되었어요", Toast.LENGTH_SHORT).show();
//            }
//        }
        if (requestCode == galleryCode && resultCode == result_ok) {
            path = Matisse.obtainPathResult(data).get(0);
            if (path != null) {
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(path)
                        .apply(new RequestOptions() {
                            {
                                override(Target.SIZE_ORIGINAL);
                            }
                        })
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Log.d("Bitmap", String.valueOf(resource));
                                setInputImg(resource);
                                setFragment(4);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                Log.d("glideError", String.valueOf(errorDrawable));

                            }
                        });


            } else
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Request code error", Toast.LENGTH_SHORT).
                    show();

    }
}