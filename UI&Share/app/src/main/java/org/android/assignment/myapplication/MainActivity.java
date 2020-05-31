package org.android.assignment.myapplication;
import androidx.appcompat.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.security.MessageDigest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;

    private FloatingActionButton fab_main, fab_sub1, fab_sub2;

    private Animation fab_open, fab_close;

    private boolean isFabOpen = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAppKeyHash(); // hash key 생성~

        mContext = getApplicationContext();

        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);
        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fab_sub2);



        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
    }


    private void getAppKeyHash(){
        /* 카카오톡 연계를 위해서 해시키 값 가져오기 위한 변수
         * */
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(),0));
                Log.e("Hash key : ", something);
            }
        }catch(Exception e){
            //TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main:
                toggleFab();
                break;

            case R.id.fab_sub1:
                toggleFab();
                Toast.makeText(this, "카카오톡", Toast.LENGTH_SHORT).show();



                break;

            case R.id.fab_sub2:
                toggleFab();
                Toast.makeText(this, "인스타그램", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    /** fab 버튼 클릭 시에 바로 toggleFab() 메서드를 실행시켜 애니메이션 효과를 시작하도록 정의
     *
     * **/
    private void toggleFab() {
        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.outline_add_circle_outline_black_18dp);
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.outline_cancel_black_24dp);
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            isFabOpen = true;
        }
    }
    private void sendKakao(Uri uri){
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setPackage("com.kakao.talk");
            startActivityForResult(intent,REQUEST_IMG_SEND);

        } catch (ActivityNotFoundException e) {
            Uri uriMarket = Uri.parse("market://deatils?id=com.kakao.talk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriMarket);
            startActivity(intent);
        }
    }

}
