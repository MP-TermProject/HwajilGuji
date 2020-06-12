package com.example.srmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity implements IActiveView, IGetImage {

    public static ProcessActivity singletone;
    ImageGenerator generator;
    MainActivity mainActivity;
    FrameLayout processMain;
    ActiveView currentView;
    Bitmap defaultImage;
    Button moveBtn=null;
    Button rotateBtn =null;
    Button freeCropBtn=null;
    Button rectCropBtn=null;
    Button removeBtn = null;
    Button resolutionBtn = null;

    //test
    ImageView tIV;
    //test
    List<ActiveView> activeViews=null;

    private void init()
    {
        singletone=this;
        mainActivity=MainActivity.singletone;
        activeViews=new ArrayList<>();
        defaultImage=mainActivity.inputImg;
    }
    private void setWidgets()
    {
        //test
        tIV = findViewById(R.id.testImgView2);
        //test
        processMain=findViewById(R.id.preprocessMainLayout);
        moveBtn=findViewById(R.id.objectMoveBtn);
        rotateBtn=findViewById(R.id.objectRotateBtn);
        freeCropBtn = findViewById(R.id.freeCropBtn);
        rectCropBtn=findViewById(R.id.rectCropBtn);
        removeBtn=findViewById(R.id.removeActiveView);
        resolutionBtn=findViewById(R.id.superResolutionBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        init();
        setWidgets();
        ActiveView activeView = new ActiveView(this);
        activeView.setiActivity(this);
        activeView.setImage(mainActivity.inputImg);
        processMain.addView(activeView);

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
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentView();
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
        resolutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process(currentView.getBitmap());
            }
        });
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

    @Override
    public void getTouchedView(ActiveView a) {
        currentView=a;
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
}
