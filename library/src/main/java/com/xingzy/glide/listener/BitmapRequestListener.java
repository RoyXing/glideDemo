package com.xingzy.glide.listener;

import android.graphics.Bitmap;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public interface BitmapRequestListener {

    boolean onException();

    boolean onBitmapResourceReady(Bitmap bitmap);
}
