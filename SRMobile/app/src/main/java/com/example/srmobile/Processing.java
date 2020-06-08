package com.example.srmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Processing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Processing extends Fragment implements IActiveView{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Processing() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Processing.
     */
    // TODO: Rename and change types and number of parameters
    public static Processing newInstance(String param1, String param2) {
        Processing fragment = new Processing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ImageGenerator generator;
    MainActivity mainActivity;
    FrameLayout processMain;
    ActiveView currentView;

    Button moveBtn=null;
    Button rotateBtn =null;
    Button freeCropBtn=null;
    Button rectCropBtn=null;

    List<ActiveView> activeViews=null;
    private void init(ViewGroup vg)
    {
        mainActivity=MainActivity.singletone;
        processMain=vg.findViewById(R.id.preprocessMainLayout);

        activeViews=new ArrayList<>();
    }
    private void setWidgets(ViewGroup vg)
    {
        moveBtn=vg.findViewById(R.id.objectMoveBtn);
        rotateBtn=vg.findViewById(R.id.objectRotateBtn);
        freeCropBtn = vg.findViewById(R.id.freeCropBtn);
        rectCropBtn=vg.findViewById(R.id.rectCropBtn);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup processingPage = (ViewGroup)inflater.inflate(R.layout.fragment_processing, container, false);
        init(processingPage);
        setWidgets(processingPage);
        ActiveView activeView = new ActiveView(getContext());
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
                mainActivity.volitileFragment(new FreeCropFragment());
            }
        });

        //process(mainActivity.getInputImg());
        //mainActivity.setFragment(5);
        return processingPage;
    }
    public void addActiveView(Bitmap image)
    {
        if(activeViews.size()<10)
        {
            ActiveView activeView = new ActiveView(getContext());
            activeView.setiActivity(this);
            activeView.setImage(image);
            activeViews.add(activeView);
            processMain.addView(activeView);
        }
        else
            Toast.makeText(getContext(),"Object가 너무 많습니다!",Toast.LENGTH_SHORT).show();
    }
    public void process(Bitmap input)
    {
        Bitmap result = mainActivity.generator.ImageProcess(input, mainActivity.width, mainActivity.height);
        mainActivity.setResultImg(result);
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
            Toast.makeText(getContext(),"오브젝트가 선택되지 않았습니다.",Toast.LENGTH_SHORT).show();
    }
}
