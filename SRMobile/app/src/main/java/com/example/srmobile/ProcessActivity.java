package com.example.srmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity implements IActiveView, IGetImage {

    public static ProcessActivity singletone;
    ImageGenerator generator;
    MainActivity mainActivity;
    FrameLayout processMain;
    ActiveView currentView;
    Bitmap defaultImage;
    Button newObjBtn = null;
    Button moveBtn=null;
    Button rotateBtn =null;
    Button freeCropBtn=null;
    Button rectCropBtn=null;
    Button removeBtn = null;
    Button resolutionBtn = null;
    Button captureBtn = null;
    SeekBar transparentBar = null;
    boolean transparentVisible;

    int galleryCode =102;
    //test
    ImageView tIV;
    //test
    List<ActiveView> activeViews=null;

    private void init()
    {
        singletone=this;
        transparentVisible=false;
        mainActivity=MainActivity.singletone;
        activeViews=new ArrayList<>();
        defaultImage=mainActivity.inputImg;
    }
    private void setWidgets()
    {
        //test
        tIV = findViewById(R.id.testImgView2);
        //test
        newObjBtn=findViewById(R.id.addObjectBtn);
        processMain=findViewById(R.id.preprocessMainLayout);
        moveBtn=findViewById(R.id.objectMoveBtn);
        rotateBtn=findViewById(R.id.objectRotateBtn);
        freeCropBtn = findViewById(R.id.freeCropBtn);
        rectCropBtn=findViewById(R.id.rectCropBtn);
        removeBtn=findViewById(R.id.removeActiveView);
        resolutionBtn=findViewById(R.id.superResolutionBtn);
        transparentBar=findViewById(R.id.transparentSeekBar);

        captureBtn=findViewById(R.id.btn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        init();
        setWidgets();
        addActiveView(mainActivity.inputImg);


        newObjBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFoundImage(galleryCode);
            }
        });
        moveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(ActiveView.state.Move);
            }
        });
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(ActiveView.state.Rotate);
            }
        });
        freeCropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeCropFragment freeCropFragment=new FreeCropFragment();
                Bitmap input = getCurrentActiveBitmap();
                if(input!=null) {
                    freeCropFragment.setBitmap(input);
                    volitileFragment(freeCropFragment);
                }
            }
        });
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("capture","Capture");
                Bitmap result = capture();
                //tIV.setImageBitmap(result);
                ResultPage resultPage = new ResultPage();
                resultPage.setResultImage(result);
                volitileFragment(resultPage);
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentView();
                transparentVisible=false;
                setTransparentVisible();
            }
        });
        rectCropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RectCropFragment rectCropFragment = new RectCropFragment();
                Bitmap input = getCurrentActiveBitmap();
                if(input!=null)
                {
                    rectCropFragment.setCropBitmap(input);
                    volitileFragment(rectCropFragment);
                }
            }
        });

        transparentBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(currentView!=null&&transparentBar.getVisibility()==View.VISIBLE)
                    currentView.setTransparent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        resolutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process(currentView.getBitmap());
            }
        });
    }

    public void requestFoundImage(int requestCode)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    public void addActiveView(Bitmap image)
    {
        if(activeViews.size()<10)
        {
            ActiveView activeView = new ActiveView(this);
            activeView.setiActivity(this);
            activeView.setImage(image);
            activeViews.add(activeView);
            processMain.addView(activeView);
        }
        else
            Toast.makeText(this,"Object가 너무 많습니다!",Toast.LENGTH_SHORT).show();
    }
    public void process(Bitmap input)
    {
        if(currentView!=null){
            Bitmap result = mainActivity.generator.ImageProcess(input, mainActivity.width, mainActivity.height);
            //mainActivity.setResultImg(result);
            currentView.setImage(result);
        }
    }

    public Bitmap capture()
    {
        Bitmap bitmap = Bitmap.createBitmap(mainActivity.screenWidth,processMain.getHeight(),Bitmap.Config.ARGB_8888);
        int[] windowSize = new int[2];
        processMain.getLocationInWindow(windowSize);
        try{
            PixelCopy.request(this.getWindow(),new Rect(windowSize[0],windowSize[1],windowSize[0]+processMain.getWidth(),processMain.getHeight()+62),bitmap, copyResult -> {
                if(copyResult==PixelCopy.SUCCESS){

                }
            },new Handler());
        } catch(Exception e){

        }

        Log.e("bitmapWidth",Integer.toString(bitmap.getWidth()));
        Log.e("bitmapHeight",Integer.toString(bitmap.getHeight()));
        return bitmap;
    }

    public Bitmap getCurrentActiveBitmap()
    {
        if(currentView!=null)
            return currentView.getBitmap();
        else
            Log.e("setBitmap","null");
        return null;
    }

    public boolean removeCurrentView()
    {
        if(currentView==null)
            return false;
        processMain.removeView(currentView);
        activeViews.remove(currentView);
        currentView=null;
        return true;
    }

    public void setTransparentVisible()
    {
        if(transparentVisible)
            transparentBar.setVisibility(View.VISIBLE);
        else
            transparentBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void getTouchedView(ActiveView a) {
        currentView=a;
        transparentVisible=true;
        setTransparentVisible();
    }

    @Override
    public void setState(ActiveView.state s) {
        if(currentView!=null)
            currentView.setCurrentState(s);
        else
            Toast.makeText(this,"오브젝트가 선택되지 않았습니다.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public IGetImage returnPlace() {
        return this;
    }

    @Override
    public void setProcessedBitmap(Bitmap bitmap) {
        Log.e("isCalled","called");
        if(currentView==null)
            Log.e("isCalled","isnull");
        else{
            currentView.setImage(bitmap);
            tIV.setImageBitmap(bitmap);
        }
    }

    public void volitileFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentField,fragment).commit();
        fragmentTransaction.addToBackStack(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("isCalled","Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("paused","Paused");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryCode)
        {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                addActiveView(img);
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        else
        {

        }
    }


}
