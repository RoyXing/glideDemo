package com.xingzy.glide.request;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class BitmapManager {

    private static volatile BitmapManager instance;
    private LinkedBlockingQueue<BitmapRequest> bitmapRequests;
    private BitmapDispatcher[] dispatchers;

    private BitmapManager() {
        bitmapRequests = new LinkedBlockingQueue<>();
        start();
    }

    public static BitmapManager getInstance() {
        if (instance == null) {
            synchronized (BitmapManager.class) {
                if (instance == null) {
                    instance = new BitmapManager();
                }
            }
        }
        return instance;
    }

    public void addBitmap(BitmapRequest request) {
        if (!bitmapRequests.contains(request)) {
            bitmapRequests.add(request);
        }
    }

    private void start() {
        stop();
        int threadCount = Runtime.getRuntime().availableProcessors();
        dispatchers = new BitmapDispatcher[threadCount];
        for (int i = 0; i < dispatchers.length; i++) {
            BitmapDispatcher bitmapDispatcher = new BitmapDispatcher(bitmapRequests);
            bitmapDispatcher.start();
            dispatchers[i] = bitmapDispatcher;
        }
    }

    private void stop() {
        if (dispatchers != null && dispatchers.length > 0) {
            for (int i = 0; i < dispatchers.length; i++) {
                if (!dispatchers[i].isInterrupted()) {
                    dispatchers[i].interrupt();
                }
            }
        }
    }
}
