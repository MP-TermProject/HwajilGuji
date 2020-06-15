package com.example.srmobile;

import android.graphics.Bitmap;
import android.icu.util.VersionInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultPage.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultPage newInstance(String param1, String param2) {
        ResultPage fragment = new ResultPage();
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
    ImageView result_img;
    Button restartBtn;
    Boolean isInput=false;
    TextView state;
    Bitmap resultImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup resultPage= (ViewGroup)inflater.inflate(R.layout.fragment_result_page, container, false);
        result_img = resultPage.findViewById(R.id.result_imgBtn);
        state = resultPage.findViewById(R.id.state);
        result_img.setImageBitmap(resultImage);

        result_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return resultPage;
    }
    public void setResultImage(Bitmap bitmap)
    {
        resultImage=bitmap;
    }
    private void convertImage()
    {
        isInput=!isInput;
        if(isInput) {
            state.setText("is input");
            result_img.setImageBitmap(mainActivity.getInputImg());
        }
        else {
            state.setText("is output");
            result_img.setImageBitmap(mainActivity.getResultImg());
        }
    }
}
