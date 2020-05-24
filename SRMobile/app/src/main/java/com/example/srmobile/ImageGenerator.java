package com.example.srmobile;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageGenerator {
    Module model;
    float[] mean = {0f, 0f, 0f};
    float[] std = {1f, 1f, 1f};
    public ImageGenerator(String modelpath){
        model = Module.load(modelpath);
    }

    public Tensor preprocess(Bitmap bitmap, int width, int height){
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Log.d("test","test_22");
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, this.mean, this.std);
    }

    public Bitmap ImageProcess(Bitmap bitmap, int width, int height){

        Tensor tensor = preprocess(bitmap, width, height);
        IValue inputs = IValue.from(tensor);
        Log.d("test","test_0");
        Tensor output = model.forward(inputs).toTensor();
        Log.d("test","test_1");
        float []result= output.getDataAsFloatArray();
        List<Float> RArray = new ArrayList<>();
        List<Float> GArray = new ArrayList<>();
        List<Float> BArray  = new ArrayList<>();
        int index=0;
        for (int i=0;i<3;i++){
            for(int j=0;j<width*height*4;j++){
                if(i==0)
                    RArray.add(result[index]);
                else if(i==1) GArray.add(result[index]);
                else BArray.add(result[index]);
                index++;
            }
        }
        Log.d("test","test_2");
        return arrayToBitmap(RArray, GArray, BArray,width*2, height*2);
    }

    private Bitmap arrayToBitmap(List<Float> R,List<Float> G, List<Float> B, int width, int height){
        byte alpha = (byte) 255;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteBuffer byteBuffer = ByteBuffer.allocate(width*height*4);

        for (int index=0; index<width*height; index++){
            byte r_value  = (byte) (R.get(index)*255);
            byte g_value  = (byte) (G.get(index)*255);
            byte b_value  = (byte) (B.get(index)*255);
            byteBuffer.put(4*index, r_value);
            byteBuffer.put(4*index+1,g_value);
            byteBuffer.put(4*index+2,b_value);
            byteBuffer.put(4*index+3,alpha);
        }
        bmp.copyPixelsFromBuffer(byteBuffer);
        return bmp;
    }
}
