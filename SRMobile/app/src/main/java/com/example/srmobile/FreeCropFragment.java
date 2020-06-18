package com.example.srmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FreeCropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeCropFragment extends Fragment implements ISomeView{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FreeCropFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FreeCropFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeCropFragment newInstance(String param1, String param2) {
        FreeCropFragment fragment = new FreeCropFragment();
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

    private LinearLayout mainLayout=null;
    private MainActivity mainActivity=null;
    private ProcessActivity processActivity=null;
    private SomeView someView=null;
    private Bitmap currentBitmap=null;
    private Bitmap resultBitmap = null;
    //test
    Button loadBtn;
    ImageView testImageView;
    //
    private void init(ViewGroup vg)
    {
        mainLayout=vg.findViewById(R.id.freeCropMainLayout);
        mainActivity=MainActivity.singletone;
        processActivity=ProcessActivity.singletone;
        someView = new SomeView(getContext(),this);
        someView.setBitmap(currentBitmap);
        mainLayout.addView(someView);
        //test
        loadBtn=vg.findViewById(R.id.loadBeforeBtn);
        testImageView=vg.findViewById(R.id.testImgView);
        //
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("isCalled","OCV Called");
        ViewGroup vg= (ViewGroup) inflater.inflate(R.layout.fragment_free_crop, container, false);

        init(vg);

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTask();
            }
        });

        return vg;
    }
    public void setBitmap(Bitmap bitmap)
    {
        Log.e("setBitmap","access");
        if(bitmap!=null) {
            Log.e("setBitmap", Integer.toString(bitmap.getWidth()));
            currentBitmap = bitmap;
        }
    }

    @Override
    public void getBitmap(Bitmap bitmap) {
        resultBitmap=bitmap;
        processActivity.setProcessedBitmap(bitmap);
        testImageView.setImageBitmap(resultBitmap);
        //endTask();
    }

    public void endTask()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(FreeCropFragment.this).commit();
        fragmentManager.popBackStack();
    }
}
