package com.chlee.hwajilguji

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.picker.gallery.model.GalleryData
import com.picker.gallery.model.interactor.GalleryPicker
import com.picker.gallery.utils.MLog
import kotlinx.android.synthetic.main.activity_main.*
import com.picker.gallery.view.PickerActivity
import kotlinx.android.synthetic.main.activity_gallery.*

class Gallery : AppCompatActivity() {

    private val PERMISSIONS_READ_WRITE = 123

    val REQUEST_RESULT_CODE = 101

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val i = Intent(this@Gallery, PickerActivity::class.java)
        i.putExtra("IMAGES_LIMIT", 4)
        i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
        startActivityForResult(i, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            MLog.e("SELECTED MEDIA", mediaList.size.toString())
        }
    }


    private fun isReadWritePermitted(): Boolean = (checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
}
