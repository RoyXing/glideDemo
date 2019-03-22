package com.xingzy.glide.lifecycle;

import android.app.Fragment;
import android.content.Context;

/**
 * @author roy.xing
 * @date 2019/3/22
 */
public class RequestFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LifecycleObservable.getInstance().onDestroy(context.hashCode());
    }
}
