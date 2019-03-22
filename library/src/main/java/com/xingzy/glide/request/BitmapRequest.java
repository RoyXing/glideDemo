package com.xingzy.glide.request;

import android.content.Context;
import android.widget.ImageView;

import com.xingzy.glide.listener.BitmapRequestListener;
import com.xingzy.glide.utils.MD5Utils;

import java.lang.ref.SoftReference;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class BitmapRequest {

    private String url;
    private String md5Url;
    private SoftReference<ImageView> softReference;
    private int loading;
    private BitmapRequestListener listener;
    private Context context;

    public BitmapRequest(Context context) {
        this.context = context;
    }

    public BitmapRequest load(String url) {
        this.url = url;
        md5Url = MD5Utils.toMD5(url);
        return this;
    }

    public void into(ImageView imageView) {
        this.softReference = new SoftReference<>(imageView);
        imageView.setTag(md5Url);
        BitmapManager.getInstance().addBitmap(this);
    }

    public BitmapRequest loading(int loading) {
        this.loading = loading;
        return this;
    }

    public BitmapRequest listener(BitmapRequestListener bitmapRequestListener) {
        this.listener = bitmapRequestListener;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getUriMD5() {
        return md5Url;
    }

    public int getLoading() {
        return loading;
    }

    public BitmapRequestListener getListener() {
        return listener;
    }

    public Context getContext() {
        return context;
    }

    public ImageView getImageView() {
        return softReference.get();
    }
}
