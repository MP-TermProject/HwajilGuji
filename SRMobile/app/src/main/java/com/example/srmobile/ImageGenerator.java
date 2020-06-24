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
    float[] mean = {0f, 0f, 0f};//TensorImageUtils.TORCHVISION_NORM_MEAN_RGB;//{0.5f, 0.5f, 0.5f};
    float[] std = {1f, 1f, 1f};//TensorImageUtils.TORCHVISION_NORM_STD_RGB;//{0.5f, 0.5f, 0.5f};

    public ImageGenerator(String modelpath) {
        model = Module.load(modelpath);
    }

    public Tensor preprocess(Bitmap bitmap, int width, int height) {
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, this.mean, this.std);
    }

    public Bitmap ImageProcess(Bitmap bitmap, int width, int height) {
        Log.e("process", "processStarted");
        Tensor tensor = preprocess(bitmap, width, height);
        float[] input = tensor.getDataAsFloatArray();
        IValue inputs = IValue.from(tensor);
        Tensor output = model.forward(inputs).toTensor();
        float[] result = output.getDataAsFloatArray();
        float m = 0f;
        for (int i = 0; i < input.length; i++)
            m += input[i];
        Float me = m / input.length;
        Log.e("mean", me.toString());
        List<Float> RArray = new ArrayList<>();
        List<Float> GArray = new ArrayList<>();
        List<Float> BArray = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < width * height * 4 * 4; j++) {
                float value = result[index];//(result[index]*std[i])+mean[i];
                if (value < 0)
                    value = 0f;

                if (index < 400) {
                    Float temp = value;
                    Integer id = index;
                    Log.e("value_result", id.toString() + "---" + temp.toString());
                }

                if (i == 0)
                    RArray.add(value);
                else if (i == 1) GArray.add(value);
                else BArray.add(value);
                index++;
            }
        }
        Log.d("test", "test_2");
        return arrayToBitmap(RArray, GArray, BArray, width * 4, height * 4);
    }

    private Bitmap arrayToBitmap(List<Float> R, List<Float> G, List<Float> B, int width, int height) {
        byte alpha = (byte) 255;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteBuffer byteBuffer = ByteBuffer.allocate(width * height * 4);

        for (int index = 0; index < width * height; index++) {
            byte r_value = (byte) (R.get(index) * 255);
            byte g_value = (byte) (G.get(index) * 255);
            byte b_value = (byte) (B.get(index) * 255);
            byteBuffer.put(4 * index, r_value);
            byteBuffer.put(4 * index + 1, g_value);
            byteBuffer.put(4 * index + 2, b_value);
            byteBuffer.put(4 * index + 3, alpha);
        }
        bmp.copyPixelsFromBuffer(byteBuffer);
        return bmp;
    }
}
