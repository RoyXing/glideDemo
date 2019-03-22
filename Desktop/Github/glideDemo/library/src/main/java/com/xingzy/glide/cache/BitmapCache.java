package com.xingzy.glide.cache;

import android.graphics.Bitmap;

import com.xingzy.glide.request.BitmapRequest;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public interface BitmapCache {

    /**
     * 存入内存
     * @param request
     * @param bitmap
     */
    void put(BitmapRequest request, Bitmap bitmap);

    /**
     * 读取缓存的图片
     * @param request
     */
    Bitmap get(BitmapRequest request);

    /**
     * 清楚所有的图片
     * @param request
     */
    void remove(BitmapRequest request);

    /**
     * 清楚所属activity的Bitmap
     * @param activityCode
     */
    void remove(int activityCode);
}
