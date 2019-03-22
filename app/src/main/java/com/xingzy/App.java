package com.xingzy;

import android.app.Application;

import com.xingzy.glide.cache.DoubleLruCache;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DoubleLruCache.getInstance(this);
    }
}
