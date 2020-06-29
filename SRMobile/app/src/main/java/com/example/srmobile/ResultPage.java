package com.example.srmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


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

    private MainActivity mainActivity;
    private ProcessActivity processActivity;
    private ImageView result_img;
    Boolean isInput = false;
    TextView state;
    Bitmap resultImage;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup resultPage = (ViewGroup) inflater.inflate(R.layout.fragment_result_page, container, false);
        context = container.getContext();
        mainActivity=MainActivity.singletone;
        processActivity = ProcessActivity.singletone;
        result_img = resultPage.findViewById(R.id.result_imgBtn);
        result_img.setImageBitmap(resultImage);
        Button restartBtn = resultPage.findViewById(R.id.restartBtn);
        Button shareBtn = resultPage.findViewById(R.id.shareBtn);
        Button saveBtn = resultPage.findViewById(R.id.saveBtn);

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(processActivity==null)
                {
                    RectSRActivity temp = (RectSRActivity) getContext();
                    temp.finish();
                }
                else
                    processActivity.finish();
            }
        });

//        shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent shareIntent = new Intent();
////                shareIntent.setAction(Intent.ACTION_SEND);
////                shareIntent.putExtra(Intent.EXTRA_STREAM, "https://t1.daumcdn.net/cfile/tistory/994BEF355CD0313D05");
////                shareIntent.setType("image/jpeg");
////                startActivity(Intent.createChooser(shareIntent, "Share images to.."));
//
//
//                File imageFileToShare = new File(resultImage);
//                // Get the URI of the image 'pic1.png' exists under pgguru right under SD card
//                Uri uri = Uri.fromFile(imageFileToShare);
//                // Set the action to be performed i.e 'Send Data'
//                Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                // Add the URI holding a stream of data
//                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                // Set the type of data i.e 'image/* which means image/png, image/jpg, image/jpeg etc.,'
//                sendIntent.setType("image/*");
//                // Launches the activity; Open 'Gallery' if you set it as default app to handle Image
//                startActivity(Intent.createChooser(sendIntent, "Share images to.."));
//            }
//        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageLocally(result_img);
            }
        });

        result_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return resultPage;
    }

    public void setResultImage(Bitmap bitmap) {
        resultImage = bitmap;
    }

    private void convertImage() {
        isInput = !isInput;
        if (isInput) {
            state.setText("is input");
            result_img.setImageBitmap(mainActivity.getInputImg());
        } else {
            state.setText("is output");
            result_img.setImageBitmap(mainActivity.getResultImg());
        }

    }

    private void saveImageLocally(ImageView iv) {
        //iv.buildDrawingCache();
        Bitmap bmp = resultImage;//iv.getDrawingCache();
        String date = getDate();


        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //context.getExternalFilesDir(null);
        File file = new File(storageLoc, "SRmobile" + date + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            scanFile(Objects.requireNonNull(getActivity()), Uri.fromFile(file));
            Toast.makeText(context, "Save Image Complete!", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("bitmap", "not found , " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("bitmap", "io ex , " + e.getMessage());
        }

    }


    private static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);
    }

    private static String getDate() {
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow = new SimpleDateFormat("YY.MM.dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        return formatDate;
    }
}
