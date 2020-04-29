package com.chlee.hwajilguji;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;


public class GalleryActivity extends AppCompatActivity{
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static ImageView gallery_image;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"CheckResult", "ShowToast"})
            @Override
            public void onClick(View view) {
                RxPermissions rxPermissions = new RxPermissions(GalleryActivity.this);
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(permission -> {
                            if (permission) {
                                ImagePicker();
                            } else {
                                Toast.makeText(GalleryActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG);
                            }
                        });
            }
        });

        gallery_image = (ImageView) findViewById(R.id.gallery_imageview);
    }

    private void ImagePicker() {
        Matisse.from(GalleryActivity.this)
                .choose(MimeType.ofImage(), false)
                .theme(R.style.Matisse_Dracula)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "test"))
                .maxSelectable(9)
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(REQUEST_CODE_CHOOSE);

    }

    @SuppressLint("CheckResult")
    @Override //Receive the returned address
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            String path = Matisse.obtainPathResult(data).get(0);
            if (path != null) {
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(path)
                        .apply(new RequestOptions() { {
                            override(Target.SIZE_ORIGINAL);
                        }})
                        .into(gallery_image);
            } else
                Toast.makeText(this, "Uri is null", Toast.LENGTH_SHORT).show();
        }

    }
}

