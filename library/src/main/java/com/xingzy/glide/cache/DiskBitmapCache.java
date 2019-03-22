package com.xingzy.glide.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import com.xingzy.glide.cache.disk.DiskLruCache;
import com.xingzy.glide.cache.disk.IOUtil;
import com.xingzy.glide.request.BitmapRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class DiskBitmapCache implements BitmapCache {

    private static volatile DiskBitmapCache instance;
    private DiskLruCache diskLruCache;
    private static final byte[] lock = new byte[0];

    public static DiskBitmapCache getInstance(Context context) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DiskBitmapCache(context);
                }
            }
        }
        return instance;
    }

    private DiskBitmapCache(Context context) {
        String imageCachePath = "Image";
        File cacheFile = getImageCacheFile(context, imageCachePath);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }

        try {
            int maxDiskSize = 50 * 1024 * 1024;
            diskLruCache = DiskLruCache.open(cacheFile, getAppVersion(context), 1, maxDiskSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getImageCacheFile(Context context, String imageCachePath) {
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = context.getExternalCacheDir().getPath();
        } else {
            path = context.getCacheDir().getPath();
        }
        return new File(path + File.separator + imageCachePath);
    }

    private int getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) packageInfo.getLongVersionCode();
            } else {
                return packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public void put(BitmapRequest request, Bitmap bitmap) {
        DiskLruCache.Editor editor;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(request.getUriMD5());
            outputStream = editor.newOutputStream(0);
            if (presetBitmap2Disk(outputStream, bitmap)) {
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(outputStream);
        }
    }

    private boolean presetBitmap2Disk(OutputStream outputStream, Bitmap bitmap) {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(bufferedOutputStream);
        }
        return false;
    }

    @Override
    public Bitmap get(BitmapRequest request) {
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(request.getUriMD5());
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(inputStream);
        }
        return null;
    }

    @Override
    public void remove(BitmapRequest request) {
        try {
            diskLruCache.remove(request.getUriMD5());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(int activityCode) {

    }
}
