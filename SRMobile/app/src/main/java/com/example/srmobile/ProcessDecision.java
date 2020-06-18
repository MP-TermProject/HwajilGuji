package com.example.srmobile;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProcessDecision#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcessDecision extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProcessDecision() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProcessDecision.
     */
    // TODO: Rename and change types and number of parameters
    public static ProcessDecision newInstance(String param1, String param2) {
        ProcessDecision fragment = new ProcessDecision();
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
    MainActivity mainActivity;
    Button processBtn;
    Button srBtn;
    ImageView preview;

    public void init()
    {
        mainActivity = MainActivity.singletone;
    }

    public void widget_init(ViewGroup vg)
    {
        preview = vg.findViewById(R.id.sampleImageView);
        processBtn = vg.findViewById(R.id.decisionOtherBtn);
        srBtn = vg.findViewById(R.id.decisionSRBtn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg= (ViewGroup) inflater.inflate(R.layout.fragment_process_decision, container, false);
        init();
        widget_init(vg);
        preview.setImageBitmap(mainActivity.getInputImg());
        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        srBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return vg;
    }
}
