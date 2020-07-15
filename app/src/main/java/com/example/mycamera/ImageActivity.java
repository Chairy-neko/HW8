package com.example.mycamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CAMERA_CODE = 123;
    private final static int REQUEST_CODE_TAKE_PHOTO = 123;

    ImageView imageView;
    private String takeImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.iv);
        ActivityCompat.requestPermissions(ImageActivity.this,permissions,PERMISSION_REQUEST_CAMERA_CODE);
        openSystemCamera();
    }

    private void openSystemCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeImagePath = getOutputMediaPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(ImageActivity.this, takeImagePath));
        if(intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    String[] permissions = new String[]{Manifest.permission.CAMERA};

    private String getOutputMediaPath(){
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir,"IMG_"+timestamp+".jpg");
        if(!mediaFile.exists()){
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    public static Uri getUriForFile(Context context, String path) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK){
            //获取ImageView控件宽高
            int targetWidth = imageView.getWidth();
            int targetHeight = imageView.getHeight();
            //创建Options，设置inJustDecodeBounds为true，只解码图片宽高信息
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(takeImagePath, options);
            int photoWidth = options.outWidth;
            int photoHeight = options.outHeight;
            //计算图片和控件的缩放比例，并设置给Options，然后inJustDecodeBounds置为false，解码真正的图片信息
            int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            Bitmap bitmap = BitmapFactory.decodeFile(takeImagePath, options);
            imageView.setImageBitmap(bitmap);
        }
    }
}
