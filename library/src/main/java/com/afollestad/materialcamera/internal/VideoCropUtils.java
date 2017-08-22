package com.afollestad.materialcamera.internal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    final static String TAG = "ZEEMEE";
    public static void loadLibrary(Context context) {
        FFmpeg ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "loadBinary:onStart()");
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "loadBinary:onFailure()");
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "loadBinary:onSuccess()");
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "loadBinary:onFinish()");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            Log.d(TAG, "loadBinary:error: " + e);
        }
    }

    public static String cropVideo(final Context context, final File file, final BaseCaptureInterface mBaseCaptureListener) {
        String croppedUrl = "";
        FFmpeg ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
        try {
            croppedUrl = file.getParent() + "/Cropped_" + file.getName();
//            String[] cmds = new String[]{"-i", file.getAbsolutePath(), "-filter:v", "crop=out_h=in_w", croppedUrl};
//            String[] cmds = {"-i", file.getAbsolutePath(), "-vf", "crop=480:480", "-threads", "5", "-preset" ,"ultrafast","-strict","-2",croppedUrl};
            String[] cmds = {"-i", file.getAbsolutePath(), "-vf", "crop=out_h=in_w", "-threads", "5", "-preset" ,"ultrafast","-strict","-2",croppedUrl};


            ffmpeg.execute(cmds, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "cropVideo:onStart()");
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "cropVideo:onProgress(): " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "cropVideo:onFailure(): " + message);
                    Toast.makeText(context, "Failed to Crop the video", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String message) {
                    file.delete();
                    mBaseCaptureListener.videoCropStatus(true);
                    Log.d(TAG, "cropVideo:onSuccess(): " + message);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "cropVideo:onFinish()");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d(TAG, "cropVideo:error: " + e);
        }
        return croppedUrl;
    }
}
