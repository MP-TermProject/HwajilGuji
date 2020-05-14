package com.chlee.hwajilguji.sr;
import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, this.mean, this.std);
    }

    public Bitmap ImageProcess(Bitmap bitmap, int width, int height){
        Tensor tensor = preprocess(bitmap, width, height);

        IValue inputs = IValue.from(tensor);
        Tensor output = model.forward(inputs).toTensor();

        float []result= output.getDataAsFloatArray();
        List<Float> RArray = new ArrayList<>();
        List<Float> GArray = new ArrayList<>();
        List<Float> BArray  = new ArrayList<>();
        int index=0;
        for (int i=0;i<3;i++){
            for(int j=0;j<width*height;j++){
                if(i==0)
                    RArray.add(result[index]);
                else if(i==1) GArray.add(result[index]);
                else BArray.add(result[index]);
                index++;
            }
        }
        Log.d("result", "ImageProcess: convert ok");
        return arrayToBitmap(RArray, GArray, BArray,width, height);
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
