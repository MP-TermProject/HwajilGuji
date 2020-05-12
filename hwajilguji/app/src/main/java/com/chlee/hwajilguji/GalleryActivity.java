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
import com.chlee.hwajilguji.db.SQLiteControl;
import com.chlee.hwajilguji.db.SQLiteHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;


public class GalleryActivity extends AppCompatActivity{
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static ImageView gallery_image;

    SQLiteHelper helper; // 헬퍼 선언
    SQLiteControl sqlite; // 실제로 SQLite를 활용할 Class를 선언합니다.

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

//        helper = new SQLiteHelper(
//                GalleryActivity.this, // context
//                "ImageDB.db", // DB 파일 이름을 적어주시면 됩니다.
//                null, // Factory
//                1 // 현재 생성하는 DB의 버전을 설정합니다.
//        );
//
//        sqlite = new SQLiteControl(helper);

//        dbTest();

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

//    private void dbTest(){
//        // DB Insert를 하고 Select로 확인합니다.
//        dbInsert(String data);
//        dbSelect();
//
//        // DB Update를 하고 Select로 확인합니다.
////        dbUpdate();
//        dbSelect();
//
//        // DB Delete를 하고 닫아줍니다. (DB를 사용 후에는 꼭 닫는 습관을 들이시는게 좋습니다!)
//        dbDelete();
//        sqlite.db_close();
//    }

    private void dbInsert(String data){
        // Insert 하려는 정보를 파라미터로 넘겨주기만 하면 Insert 됩니다.
        sqlite.insert(2, data);
    }

    private void dbSelect(){
        // 아까 SQLiteControl에서 배열로 넘겨주었기 때문에 sqlite.select()를 배열로 받아줍니다.
        String[] selectData = sqlite.select();

        // 배열로 받은 정보를 로그로 확인합니다.
        for(int i=0; i>selectData.length; i++){
            Log.i("@@ Select DB : ", selectData[i]);
        }
    }

//    private void dbUpdate(String ID, String IMAGE){
//        // update 해야 할 column명, 변경할 값, where조건 이지만 간단 예제이기때문에 primary Key를 사용했습니다.
//        sqlite.update(helper.IMAGE, IMAGE);
//    }

    private void dbDelete(){
        // delete 해야 할 정보 primary Key를 파라미터로 전달
//        sqlite.delete("010-0000-0000");
    }


    @SuppressLint("CheckResult")
    @Override //Receive the returned address
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            String path = Matisse.obtainPathResult(data).get(0);
            Log.d("Text",Matisse.obtainPathResult(data).get(0));
            if (path != null) {
                Glide.with(this)
                        .asBitmap() // some .jpeg files are actually gif
                        .load(path)
                        .apply(new RequestOptions() { {
                            override(Target.SIZE_ORIGINAL);
                        }})
                        .into(gallery_image);
                dbInsert(path);
            } else
                Toast.makeText(this, "Uri is null", Toast.LENGTH_SHORT).show();
        }

    }
}

