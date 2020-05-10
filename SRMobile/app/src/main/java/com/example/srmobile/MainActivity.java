package com.example.srmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int cameraRequestCode = 100;
    int imageConvertRequestCode = 101;
    ImageSelectionWay selectionWay;//id==1
    CameraAction cameraAction;//id==2
    Preprocess preprocess;//id==3
    Processing processing;//id==4
    ResultPage resultPage;//id==5
    HashMap<Integer,Fragment> fragmentHashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectionWay = new ImageSelectionWay();
        cameraAction = new CameraAction();
        fragmentHashMap=new HashMap<>();
        fragmentHashMap.put(1, selectionWay);
        fragmentHashMap.put(2, cameraAction);
        //fragmentHashMap.put(3, preprocess);
        //fragmentHashMap.put(4, processing);
        //fragmentHashMap.put(5, resultPage);
        getSupportFragmentManager().beginTransaction().add(R.id.main_layout,selectionWay).commit();
    }
    /*code 101_1 : convert process complete.
    * code 101_2 : convert process failed
    *
    *
    *
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    public int setFragment(int fragment_id){
        if(fragmentHashMap.containsKey(fragment_id)) {
            Fragment f = fragmentHashMap.get(fragment_id);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, f).commit();
        }
        Toast.makeText(getApplicationContext(),"해당 화면을 찾을 수 없습니다.",Toast.LENGTH_SHORT).show();
        return 0;
    }
}
