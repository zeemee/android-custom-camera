package com.afollestad.materialcamera.internal;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

/**
 * Created by babs on 6/19/17.
 */

public class VideoCropUtils {

    public static void loadLibrary(Context context) {
        FFmpeg ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("###", "loadBinary:onStart()");
                }

                @Override
                public void onFailure() {
                    Log.d("###", "loadBinary:onFailure()");
                }

                @Override
                public void onSuccess() {
                    Log.d("###", "loadBinary:onSuccess()");
                }

                @Override
                public void onFinish() {
                    Log.d("###", "loadBinary:onFinish()");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            Log.d("###", "loadBinary:error: " + e);
        }
    }

    public static String cropVideo(Context context, final File file, final BaseCaptureInterface mBaseCaptureListener) {
        String croppedUrl = "";
        FFmpeg ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
        try {
            croppedUrl = file.getParent() + "/Cropped_" + file.getName();
            String[] cmds = new String[]{"-i", file.getAbsolutePath(), "-filter:v", "crop=out_h=in_w", croppedUrl};


            ffmpeg.execute(cmds, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("###", "cropVideo:onStart()");
                }

                @Override
                public void onProgress(String message) {
                    Log.d("###", "cropVideo:onProgress(): " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("###", "cropVideo:onFailure(): " + message);
                }

                @Override
                public void onSuccess(String message) {
                    file.delete();
                    Log.d("###", "cropVideo:onSuccess(): " + message);
                }

                @Override
                public void onFinish() {
                    Log.d("###", "cropVideo:onFinish()");
                    mBaseCaptureListener.videoCropStatus(true);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("###", "cropVideo:error: " + e);
        }
        return croppedUrl;
    }
}
