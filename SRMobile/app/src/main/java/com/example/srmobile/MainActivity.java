package com.example.srmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageDecoder;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    public static MainActivity singletone;
    int permissionRequestCode= 80;
    int cameraRequestCode = 100;
    int imageConvertRequestCode = 101;
    int galleryCode = 102;
    int result_ok = -1;
    int result_fail=0;

    public int screenWidth;
    public int screenHeight;
    public int width = 150;
    public int height =150;

    public ImageGenerator generator;
    ImageSelectionWay selectionWay;//id==1

    ProcessDecision decision;//id==3
    Processing processing;//id==4
    ResultPage resultPage;//id==5
    public IGetImage process=null;
    enum Screen{
        select,decision,processing,result
    }
    private Screen currentScreen;
    HashMap<Screen,Fragment> fragmentHashMap;

    Bitmap inputImg;
    Bitmap resultImg;

    private String imageFilePath;
    private Uri photoUri;

    public void setScreenSize()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth=size.x;
        screenHeight=size.y;
    }

    public void setPermission()
    {
        int cameraCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraCheck==PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},permissionRequestCode);
        int readCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readCheck==PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},permissionRequestCode);
        int writeCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeCheck==PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},permissionRequestCode);
    }

    public void init()
    {
        singletone=this;
        setScreenSize();
        Drawable drawable = getDrawable(R.drawable.bird_mid);
        inputImg = ((BitmapDrawable)drawable).getBitmap();
        inputImg = Bitmap.createScaledBitmap(inputImg, width, height, false);
        resultImg = ((BitmapDrawable)drawable).getBitmap();
        resultImg = Bitmap.createScaledBitmap(resultImg, width, height, false);
    }
    public void initFragment()
    {
        selectionWay = new ImageSelectionWay();
        processing = new Processing();
        resultPage = new ResultPage();
        fragmentHashMap=new HashMap<>();
        fragmentHashMap.put(Screen.select, selectionWay);
        fragmentHashMap.put(Screen.decision, decision);
        fragmentHashMap.put(Screen.processing, processing);
        process=processing;
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
        try{
            generator =new ImageGenerator(Utils.assetFilePath(this, "SRMobile_150_N.pt"));
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setFragmentNotStack(Screen.select);
    }
    public void requestFoundImage(int requestCode)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }
    public void setInputImg(Bitmap img)
    {
        inputImg = img;//Bitmap.createScaledBitmap(img, width, height, false);
    }
    public Bitmap getInputImg()
    {
        return inputImg;
    }
    public void setResultImg(Bitmap img)
    {
        resultImg = Bitmap.createScaledBitmap(img, width*2, height*2, false);
    }
    public Bitmap getResultImg()
    {
        return resultImg;
    }
    public void requestCameraActivity(int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!=null) {
                File photoFile = null;
                try {
                    photoFile = generateSampleImage();
                }
                catch(IOException e)
                {

                }
                if(photoFile!=null)
                {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                    startActivityForResult(intent, requestCode);
                }
            }
        }
    }
    public File generateSampleImage()throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="Test_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        imageFilePath= image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==permissionRequestCode)
        {

        }
    }

    /*code 101_1 : convert process complete.
    * code 101_2 : convert process failed
    * code 102_1 : get_image from gallery
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==cameraRequestCode)
        {
            if(resultCode==result_ok)
            {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
                    setInputImg(bitmap);
                    setFragment(Screen.processing);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }        if(requestCode==galleryCode)
        {
            if(resultCode==result_ok)
            {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    setInputImg(img);
                    //setFragment(Screen.processing);
                    Intent intent = new Intent(getApplicationContext(),ProcessActivity.class);
                    startActivity(intent);
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode==result_fail)
            {
                Toast.makeText(getApplicationContext(),"취소되었어요",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void volitileFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_layout,fragment).commit();
        fragmentTransaction.addToBackStack(null);
    }
    public void setFragmentNotStack(Screen fragment_id)
    {
        if(fragmentHashMap.containsKey(fragment_id)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment f = fragmentHashMap.get(fragment_id);
            transaction.replace(R.id.main_layout, f);
            transaction.commit();
            currentScreen=fragment_id;
        }
        else
            Toast.makeText(getApplicationContext(),"해당 화면을 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
    }
    public int setFragment(Screen fragment_id){
        if(fragmentHashMap.containsKey(fragment_id)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment f = fragmentHashMap.get(fragment_id);
            transaction.replace(R.id.main_layout, f);
            transaction.addToBackStack(null);
            if(currentScreen==Screen.processing) {
                getSupportFragmentManager().popBackStack();
            }
            transaction.commit();
            currentScreen=fragment_id;
        }
        else
            Toast.makeText(getApplicationContext(),"해당 화면을 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        return 0;
    }
}
