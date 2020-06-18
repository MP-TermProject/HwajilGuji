package com.example.srmobile;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RectCropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RectCropFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RectCropFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RectCropFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RectCropFragment newInstance(String param1, String param2) {
        RectCropFragment fragment = new RectCropFragment();
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
    ProcessActivity processActivity;
    Bitmap cropBitmap=null;
    Button cropBtn;
    RectCrop rc;
    ImageView resultImg;
    public void init()
    {
        processActivity=ProcessActivity.singletone;
    }
    public void initWidget(ViewGroup vg)
    {
        rc = vg.findViewById(R.id.rectCropScreen);
        resultImg = vg.findViewById(R.id.resultImg);
        cropBtn = vg.findViewById(R.id.submitResult);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_rect_crop, container, false);
        init();
        initWidget(vg);
        rc.setBitmap(cropBitmap);
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rc.CropImage()!=null) {
                    resultImg.setImageBitmap(rc.CropImage());
                    Bitmap output = rc.CropImage();
                    processActivity.setProcessedBitmap(output);
                    endTask();
                }
                else
                    Toast.makeText(getContext(),"사이즈가 작습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        return vg;
    }
    public void setCropBitmap(Bitmap bitmap)
    {
        cropBitmap=bitmap;
    }
    public void endTask()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(RectCropFragment.this).commit();
        fragmentManager.popBackStack();
    }
}
