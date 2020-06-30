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
import android.widget.LinearLayout;
import android.widget.SeekBar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EraserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EraserFragment extends Fragment implements IGetImage {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EraserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EraserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EraserFragment newInstance(String param1, String param2) {
        EraserFragment fragment = new EraserFragment();
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

    Bitmap resultBitmap;
    LinearLayout eraserMainLayout = null;
    Button backBtn = null;
    Button widthBtn = null;
    Button blurBtn = null;
    EraserView eView;
    SeekBar statusBar;
    ProcessActivity processActivity;

    enum Var {
        idle, wid, blur
    }

    Var currentVar = Var.idle;

    private void init() {
        processActivity = ProcessActivity.singletone;
    }

    private void initWidget(ViewGroup vg) {
        eraserMainLayout = vg.findViewById(R.id.eraserMainLayout);
        backBtn = vg.findViewById(R.id.eraser_backBtn);
        blurBtn = vg.findViewById(R.id.setBlur);
        widthBtn = vg.findViewById(R.id.setWidth);
        statusBar = vg.findViewById(R.id.eraserSeekBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_eraser, container, false);
        init();
        initWidget(vg);
        EraserView eView = new EraserView(getContext());
        eView.setBitmap(resultBitmap);
        eraserMainLayout.addView(eView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processActivity.setProcessedBitmap(eView.getResult());
                endTask();
            }
        });
        blurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVar = Var.blur;
                if (eView != null) {
                    statusBar.setProgress((int) (eView.getPaintBlur()));
                }
            }
        });
        widthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVar = Var.wid;
                if (eView != null) {
                    statusBar.setProgress((int) (eView.getPaintWidth() / 3));
                }
            }
        });
        statusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (eView != null) {
                    if (currentVar == Var.wid) {
                        seekBar.setProgress((int) (eView.getPaintWidth() / 3));
                    } else if (currentVar == Var.blur) {
                        seekBar.setProgress((int) (eView.getPaintBlur()));
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (eView != null) {
                    if (currentVar == Var.wid) {
                        eView.setPaintWidth(seekBar.getProgress() * 3);
                    } else if (currentVar == Var.blur) {
                        eView.setBlur(seekBar.getProgress());
                    }
                }
            }
        });

        return vg;
    }

    @Override
    public IGetImage returnPlace() {
        return this;
    }

    @Override
    public void setProcessedBitmap(Bitmap bitmap) {
        resultBitmap = bitmap;
    }

    public void endTask() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(EraserFragment.this).commit();
        fragmentManager.popBackStack();
    }
}
