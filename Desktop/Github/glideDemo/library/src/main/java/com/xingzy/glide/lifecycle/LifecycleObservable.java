package com.xingzy.glide.lifecycle;

import com.xingzy.glide.cache.DoubleLruCache;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class LifecycleObservable {

    private static volatile LifecycleObservable instance;

    public static LifecycleObservable getInstance() {
        if (instance == null) {
            synchronized (LifecycleObservable.class) {
                if (instance == null) {
                    instance = new LifecycleObservable();
                }
            }
        }
        return instance;
    }

    void onDestroy(int activityCode) {
        DoubleLruCache.getInstance().remove(activityCode);
    }
}
