package com.afollestad.materialcamera;

import android.app.Fragment;
import android.support.annotation.NonNull;

import com.afollestad.materialcamera.internal.BaseCaptureActivity;
import com.afollestad.materialcamera.internal.CameraFragment;

public class CaptureActivity extends BaseCaptureActivity {

    static CameraFragment fragmentInstance;

    @Override
    @NonNull
    public Fragment getFragment() {
        if (fragmentInstance == null){
            fragmentInstance = CameraFragment.newInstance();
        }
        return fragmentInstance;
    }
}