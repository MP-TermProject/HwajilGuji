package com.example.srmobile;

import android.graphics.Bitmap;

//이미지를 저장할 클래스 반환, 저장을 수행하는 인터페이스 제공
public interface IGetImage {
    public IGetImage returnPlace();
    public void setProcessedBitmap(Bitmap bitmap);
}
