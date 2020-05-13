package com.example.srmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    int cameraRequestCode = 100;
    int imageConvertRequestCode = 101;
    int galleryCode = 102;
    int result_ok = -1;
    int result_fail=0;

    int width = 540;
    int height =540;

    ImageSelectionWay selectionWay;//id==1
    CameraAction cameraAction;//id==2
    Preprocess preprocess;//id==3
    Processing processing;//id==4
    ResultPage resultPage;//id==5
    HashMap<Integer,Fragment> fragmentHashMap;

    Bitmap inputImg;
    Bitmap resultImg;

    Button cameraBtn;
    Button galleryBtn;
    Button configure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable drawable = getDrawable(R.drawable.bird_mid);
        inputImg = ((BitmapDrawable)drawable).getBitmap();
        inputImg = Bitmap.createScaledBitmap(inputImg, width, height, false);
        resultImg = ((BitmapDrawable)drawable).getBitmap();
        resultImg = Bitmap.createScaledBitmap(resultImg, width, height, false);
        selectionWay = new ImageSelectionWay();
        cameraAction = new CameraAction();
        resultPage = new ResultPage();
        fragmentHashMap=new HashMap<>();
        fragmentHashMap.put(1, selectionWay);
        fragmentHashMap.put(2, cameraAction);
        fragmentHashMap.put(3, preprocess);
        fragmentHashMap.put(4, processing);
        fragmentHashMap.put(5, resultPage);
        getSupportFragmentManager().beginTransaction().add(R.id.main_layout,selectionWay).commit();
    }
    public void requestFoundImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, galleryCode);
    }
    public void setInputImg(Bitmap img)
    {
        inputImg = Bitmap.createScaledBitmap(img, width, height, false);
    }
    public Bitmap getInputImg()
    {
        return inputImg;
    }
    public void setResultImg(Bitmap img)
    {
        resultImg = Bitmap.createScaledBitmap(img, width, height, false);
    }
    public Bitmap getResultImg()
    {
        return resultImg;
    }

    /*code 101_1 : convert process complete.
    * code 101_2 : convert process failed
    * code 102_1 : get_image from gallery
    *
    *
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryCode)
        {
            if(resultCode==result_ok)
            {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    setInputImg(img);
                    setFragment(5);
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
    public int setFragment(int fragment_id){
        if(fragmentHashMap.containsKey(fragment_id)) {
            Fragment f = fragmentHashMap.get(fragment_id);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, f);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else
            Toast.makeText(getApplicationContext(),"해당 화면을 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        return 0;
    }
}
