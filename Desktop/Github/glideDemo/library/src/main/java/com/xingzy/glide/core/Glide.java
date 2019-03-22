package com.xingzy.glide.core;

import android.app.Activity;
import android.app.FragmentManager;

import com.xingzy.glide.lifecycle.RequestFragment;
import com.xingzy.glide.request.BitmapRequest;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class Glide {

    public static BitmapRequest with(Activity activity) {

        FragmentManager fragmentManager = activity.getFragmentManager();
        RequestFragment fragment = (RequestFragment) fragmentManager.findFragmentByTag("glide");
        if (fragment == null) {
            fragment = new RequestFragment();
            fragmentManager.beginTransaction().add(fragment, "glide").commitAllowingStateLoss();
        }

        return new BitmapRequest(activity);
    }
}
