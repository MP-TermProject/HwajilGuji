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
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import com.example.srmobile.sr.ImageGenerator;
import com.example.srmobile.sr.SRActivity;


import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

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

    int srRequestCode = 180;
    int defaultGalleryCode;
    int result_ok = -1;
    int result_fail = 0;

    public int screenWidth;
    public int screenHeight;
    public int width = 150;
    public int height = 150;

    public ImageGenerator generator;
    ImageSelectionWay selectionWay;
    ProcessDecision decision;
    Processing processing;
    ResultPage resultPage;

    public IGetImage process = null;

    enum Screen {
        select, decision, processing, result
    }

    private Screen currentScreen;
    HashMap<Screen, Fragment> fragmentHashMap;


    Bitmap inputImg;
    Bitmap resultImg;

    private String imageFilePath;
    private Uri photoUri;

    String path;
    Button cameraBtn;
    Button galleryBtn;
    Button configure;
    @SuppressLint("StaticFieldLeak")
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
        setScreenSize();
        Drawable drawable = getDrawable(R.drawable.bird_mid);
        inputImg = ((BitmapDrawable) drawable).getBitmap();
        inputImg = Bitmap.createScaledBitmap(inputImg, width, height, false);
        resultImg = ((BitmapDrawable) drawable).getBitmap();
        resultImg = Bitmap.createScaledBitmap(resultImg, width, height, false);
    }

    public void initFragment() {
        selectionWay = new ImageSelectionWay();
        processing = new Processing();
        decision = new ProcessDecision();
        resultPage = new ResultPage();
        fragmentHashMap = new HashMap<>();
        fragmentHashMap.put(Screen.select, selectionWay);
        fragmentHashMap.put(Screen.decision, decision);
        fragmentHashMap.put(Screen.processing, processing);
        process = processing;
        fragmentHashMap.put(Screen.result, resultPage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPermission();
        setScreenSize();

        init();
        initFragment();

        //SRMobile_150_N.pt
        try {
            generator = new ImageGenerator(com.example.srmobile.Utils.assetFilePath(this, "SRMobile_150_N.pt"));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setFragmentNotStack(Screen.select);


//        Intent intent = getIntent();
//        String action = intent.getAction();
//        String type = intent.getType();
//
//        if (Intent.ACTION_SEND.equals(action) && type != null) {
//            if (type.startsWith("image/")) {
//                handleSendImage(intent); // Handle single image being sent
//            }
//        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
//            if (type.startsWith("image/")) {
//                handleSendMultipleImages(intent); // Handle multiple images being sent
//            }
//        } else {
//            // Handle other intents, such as being started from the home screen
//        }

    }


//    void handleSendImage(Intent intent) {
//        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//        if (imageUri != null) {
//            // Update UI to reflect image being shared
//        }
//    }
//
//    void handleSendMultipleImages(Intent intent) {
//        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//        if (imageUris != null) {
//            // Update UI to reflect multiple images being shared
//        }
//    }

    public void requestFoundImage(int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ImagePicker(requestCode);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        permissionRequestCode);
            }
        } else {    // Permission has already been granted
            ImagePicker(requestCode);
        }
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

    public void requestCameraActivity(int requestCode) {
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
                    startActivityForResult(intent, requestCode);
                }
            }
        }
    }

    public File generateSampleImage() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    public void ImagePicker(int requestcode) {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
//                .theme(R.style.Matisse_Dracula)
                .countable(true)
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
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(requestcode);
    }

    /*code 101_1 : convert process complete.
     * code 101_2 : convert process failed
     * code 102_1 : get_image from gallery
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
                    //Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);
                    //startActivity(intent);
                    ProcessDecision decisionfragment = new ProcessDecision();
                    volitileFragment(decisionfragment);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (resultCode == result_ok) {
            Log.d("code:", String.valueOf(requestCode));
            if (requestCode == galleryCode) {
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
                                    ProcessDecision decisionfragment = new ProcessDecision();
                                    volitileFragment(decisionfragment);
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
                    Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT).show();
            } else if (requestCode == srRequestCode) {
                path = Matisse.obtainPathResult(data).get(0);
                if (path != null) {
                    Intent intent = new Intent(this, SRActivity.class);
                    intent.putExtra("dataID", path);
                    startActivityForResult(intent, 1);

                } else
                    Toast.makeText(this, "Request code error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void volitileFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, fragment).commit();
        fragmentTransaction.addToBackStack(null);
    }

    public void setFragmentNotStack(Screen fragment_id) {
        if (fragmentHashMap.containsKey(fragment_id)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment f = fragmentHashMap.get(fragment_id);
            transaction.replace(R.id.main_layout, f);
            transaction.commit();
            currentScreen = fragment_id;
        } else
            Toast.makeText(getApplicationContext(), "해당 화면을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
    }

    public int setFragment(Screen fragment_id) {
        if (fragmentHashMap.containsKey(fragment_id)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment f = fragmentHashMap.get(fragment_id);
            transaction.replace(R.id.main_layout, f);
            transaction.addToBackStack(null);
            if (currentScreen == Screen.processing) {
                getSupportFragmentManager().popBackStack();
            }
            transaction.commit();
            currentScreen = fragment_id;
        } else
            Toast.makeText(getApplicationContext(), "해당 화면을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        return 0;
    }
}