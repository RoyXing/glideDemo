package com.xingzy.glide.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.xingzy.glide.cache.DoubleLruCache;
import com.xingzy.glide.cache.disk.IOUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class BitmapDispatcher extends Thread {

    private LinkedBlockingQueue<BitmapRequest> blockingQueue;
    private DoubleLruCache doubleLruCache;
    private Handler handler;

    public BitmapDispatcher(LinkedBlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
        doubleLruCache = DoubleLruCache.getInstance();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            try {
                BitmapRequest request = blockingQueue.take();
                loading(request);
                Bitmap bitmap = findBitmap(request);
                showUI(request, bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showUI(final BitmapRequest request, final Bitmap bitmap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = request.getImageView();
                if (imageView != null && imageView.getTag().equals(request.getUriMD5()) && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });

        if (request.getListener() != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        request.getListener().onBitmapResourceReady(bitmap);
                    } else {
                        request.getListener().onException();
                    }
                }
            });
        }
    }

    private Bitmap findBitmap(BitmapRequest request) {
        Bitmap bitmap = doubleLruCache.get(request);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = downloadBitmap(request);
        if (bitmap != null) {
            doubleLruCache.put(request, bitmap);
        }
        return bitmap;
    }

    private Bitmap downloadBitmap(BitmapRequest request) {
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(inputStream);
        }
        return bitmap;
    }

    private void loading(final BitmapRequest request) {
        if (request.getImageView() != null && request.getLoading() > 0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    request.getImageView().setImageResource(request.getLoading());
                }
            });
        }
    }
}
