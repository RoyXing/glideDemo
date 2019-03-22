package com.xingzy;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xingzy.glide.core.Glide;
import com.xingzy.glide.listener.BitmapRequestListener;

public class MainActivity extends AppCompatActivity {

    private LinearLayout scrooll_line;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrooll_line = findViewById(R.id.scrooll_line);
        verifyStoragePermissions(this);

    }

    private void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void single(View view) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        scrooll_line.addView(imageView);
        Glide.with(this).load("http://pic24.nipic.com/20120906/2786001_082828452000_2.jpg").loading(R.drawable.loading).listener(new BitmapRequestListener() {
            @Override
            public boolean onException() {
                return false;
            }

            @Override
            public boolean onBitmapResourceReady(Bitmap resource) {
                Toast.makeText(MainActivity.this, "自定义处理图片（比如设置圆角）"
                        , Toast.LENGTH_SHORT).show();
                return false;
            }
        }).into(imageView);
    }

    public void more(View view) {
        for (int i = 0; i < 100; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            scrooll_line.addView(imageView);
            //设置占位图片
            Glide.with(this)
                    .loading(R.drawable.loading).load("http://pic24.nipic.com/20120906/2786001_082828452000_2.jpg").loading(R.drawable.loading)
                    .into(imageView);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            Toast.makeText(this, "onRequestPermissionsResult", Toast.LENGTH_SHORT).show();
        }
    }
}
