package com.xingzy.glide.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.xingzy.glide.request.BitmapRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class MemoryLruCache implements BitmapCache {

    private static volatile MemoryLruCache instance;
    private static final byte[] lock = new byte[0];
    private LruCache<String, Bitmap> lruCache;
    private HashMap<String, Integer> activityCodes;

    public static MemoryLruCache getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MemoryLruCache();
                }
            }
        }
        return instance;
    }

    private MemoryLruCache() {
        int maxMemorySize = 1024 * 1024 * 1024;
        lruCache = new LruCache<String, Bitmap>(maxMemorySize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //一张图的大小
                return value.getRowBytes() * value.getHeight();
            }
        };
        activityCodes = new HashMap<>();
    }

    @Override
    public void put(BitmapRequest request, Bitmap bitmap) {
        if (bitmap != null) {
            lruCache.put(request.getUriMD5(), bitmap);
            activityCodes.put(request.getUriMD5(), request.getContext().hashCode());
        }
    }

    @Override
    public Bitmap get(BitmapRequest request) {
        return lruCache.get(request.getUriMD5());
    }

    @Override
    public void remove(BitmapRequest request) {
        lruCache.remove(request.getUriMD5());
    }

    @Override
    public void remove(int activity) {
        List<String> tempList = new ArrayList<>();

        for (String urlMd5 : activityCodes.keySet()) {
            if (activityCodes.get(urlMd5).intValue() == activity) {
                tempList.add(urlMd5);
            }
        }

        for (String url : tempList) {
            activityCodes.remove(url);
            Bitmap bitmap = lruCache.get(url);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            lruCache.remove(url);
            bitmap = null;
        }

        if (!tempList.isEmpty()) {
            System.gc();
        }
    }
}
