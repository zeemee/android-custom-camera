package com.afollestad.materialcamera.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialcamera.CaptureActivity;
import com.afollestad.materialcamera.MaterialCamera;
import com.afollestad.materialcamera.R;
import com.afollestad.materialcamera.util.CameraUtil;
import com.afollestad.materialcamera.util.Degrees;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static com.afollestad.materialcamera.internal.BaseCaptureActivity.CAMERA_POSITION_BACK;
import static com.afollestad.materialcamera.internal.BaseCaptureActivity.FLASH_MODE_ALWAYS_ON;
import static com.afollestad.materialcamera.internal.BaseCaptureActivity.FLASH_MODE_AUTO;
import static com.afollestad.materialcamera.internal.BaseCaptureActivity.FLASH_MODE_OFF;

/**
 * @author Aidan Follestad (afollestad)
 */
abstract class BaseCameraFragment extends Fragment implements CameraUriInterface, View.OnClickListener {

    protected ImageButton mButtonClose;
    protected ImageButton mButtonVideo;
    protected ImageButton mButtonStillshot;
    protected ImageButton mButtonFacing;
    protected ImageButton mButtonFlash;
    protected TextView mRecordDuration;
    protected TextView mDelayStartCountdown;
    protected TextView mZeeMeeQuestion;
    protected TextView mPromptText;
    protected TextView mPickPromptText;
    protected RecyclerView mPromptRecyclerView;
    protected VideoQuestionPromptsAdapter mAdapter;
    protected RelativeLayout mControlsRelative, mPromptRelative, mTopPromptRelative;

    public static int videoId = -1;
    public String promptString = null;

    protected final int ANIMATION_DURATION = 400;
    protected boolean inSelectAPromptMode = true;

    private boolean mIsRecording;
    protected String mOutputUri;
    protected BaseCaptureInterface mInterface;
    protected Handler mPositionHandler;
    protected MediaRecorder mMediaRecorder;
    private int mIconTextColor;

    protected static void LOG(Object context, String message) {
        Log.d(context instanceof Class<?> ? ((Class<?>) context).getSimpleName() :
                context.getClass().getSimpleName(), message);
    }

    private final Runnable mPositionUpdater = new Runnable() {
        @Override
        public void run() {
            if (mInterface == null || mRecordDuration == null) return;
            final long mRecordStart = mInterface.getRecordingStart();
            final long mRecordEnd = mInterface.getRecordingEnd();
            if (mRecordStart == -1 && mRecordEnd == -1) return;
            final long now = System.currentTimeMillis();
            if (mRecordEnd != -1) {
                if (now >= mRecordEnd) {
                    stopRecordingVideo(true, videoId);
                } else {
                    final long diff = mRecordEnd - now;
                    mRecordDuration.setText(String.format("-%s", CameraUtil.getDurationString(diff)));
                }
            } else {
                mRecordDuration.setText(CameraUtil.getDurationString(now - mRecordStart));
            }
            if (mPositionHandler != null)
                mPositionHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mcam_fragment_videocapture, container, false);

        return v;
    }

    protected void setImageRes(ImageView iv, @DrawableRes int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && iv.getBackground() instanceof RippleDrawable) {
            RippleDrawable rd = (RippleDrawable) iv.getBackground();
            rd.setColor(ColorStateList.valueOf(CameraUtil.adjustAlpha(mIconTextColor, 0.3f)));
        }
        Drawable d = AppCompatResources.getDrawable(iv.getContext(), res);
        d = DrawableCompat.wrap(d.mutate());
        //DrawableCompat.setTint(d, mIconTextColor);
        iv.setImageDrawable(d);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mButtonClose = (ImageButton) view.findViewById(R.id.btnClose);
        mDelayStartCountdown = (TextView) view.findViewById(R.id.delayStartCountdown);
        mButtonVideo = (ImageButton) view.findViewById(R.id.video);
        mButtonStillshot = (ImageButton) view.findViewById(R.id.stillshot);
        mRecordDuration = (TextView) view.findViewById(R.id.recordDuration);
        mPickPromptText = (TextView) view.findViewById(R.id.tvPromptText);
        mPromptText = (TextView) view.findViewById(R.id.tvPrompt);
        mButtonFacing = (ImageButton) view.findViewById(R.id.facing);
        mPromptRecyclerView = (RecyclerView) view.findViewById(R.id.rvPrompts);
        mControlsRelative = (RelativeLayout) view.findViewById(R.id.controlsFrame);
        mPromptRelative = (RelativeLayout) view.findViewById(R.id.promptFrame);
        mTopPromptRelative = (RelativeLayout) view.findViewById(R.id.rlZeeMeeQuestion);

        System.out.println("DENSITY - " + getResources().getDisplayMetrics().density);

        //Set up Toolbar
        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inSelectAPromptMode) {
                    ((CaptureActivity) getActivity()).onBackPressed();
                }
                else{
                    onCloseClicked();
                }
            }
        });


        // Display the question
        mZeeMeeQuestion = (TextView) view.findViewById(R.id.etZeeMeeQuestion);
        //mZeeMeeQuestion.setText(ZeeMeeQuestion.getZeemeeQuestion());

        mPromptRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new VideoQuestionPromptsAdapter(getActivity(), new PromptAnswerCallback() {
            @Override
            public void onAnswerButtonClicked(String question, int dbId) {
                videoId = dbId;
                onPromptSelected(question);
            }
        });
        mAdapter.addItems(ZeeMeeQuestionsManager.getZeemeeQuestions());
        mPromptRecyclerView.setAdapter(mAdapter);

        //Set the height for Relative Layout displaying video question
        RelativeLayout questionsRelativelayout = (RelativeLayout) view.findViewById(R.id.rlZeeMeeQuestion);
        questionsRelativelayout.getLayoutParams().height = setLayoutHeight();

        if (mInterface.shouldHideCameraFacing() || CameraUtil.isChromium()) {
            mButtonFacing.setVisibility(View.GONE);
        } else {
            setImageRes(mButtonFacing, mInterface.getCurrentCameraPosition() == CAMERA_POSITION_BACK ?
                    mInterface.iconFrontCamera() : mInterface.iconRearCamera());
        }

        mButtonFlash = (ImageButton) view.findViewById(R.id.flash);
        setupFlashMode();

        mButtonVideo.setOnClickListener(this);
        mButtonStillshot.setOnClickListener(this);
        mButtonFacing.setOnClickListener(this);
        mButtonFlash.setOnClickListener(this);

        int primaryColor = getArguments().getInt(CameraIntentKey.PRIMARY_COLOR);
        if (CameraUtil.isColorDark(primaryColor)) {
            mIconTextColor = ContextCompat.getColor(getActivity(), R.color.mcam_color_light);
            primaryColor = CameraUtil.darkenColor(primaryColor);
        } else {
            mIconTextColor = ContextCompat.getColor(getActivity(), R.color.mcam_color_dark);
        }

        // Set the height and background color for controls Relative Layout
        RelativeLayout controlsRelativeLayout = (RelativeLayout) view.findViewById(R.id.controlsFrame);
        controlsRelativeLayout.setBackgroundColor(primaryColor);
        controlsRelativeLayout.getLayoutParams().height = setLayoutHeight() + 60;

        mRecordDuration.setTextColor(mIconTextColor);

        if (mMediaRecorder != null && mIsRecording) {
            setImageRes(mButtonVideo, mInterface.iconStop());
        } else {
            setImageRes(mButtonVideo, mInterface.iconRecord());
            mInterface.setDidRecord(false);
        }

        if (mInterface.useStillshot()) {
            mButtonVideo.setVisibility(View.GONE);
            mRecordDuration.setVisibility(View.GONE);
            mButtonStillshot.setVisibility(View.VISIBLE);
            setImageRes(mButtonStillshot, mInterface.iconStillshot());
            mButtonFlash.setVisibility(View.VISIBLE);
        }

        if (savedInstanceState != null) {
            openCamera();
            mOutputUri = savedInstanceState.getString("output_uri");
            promptString = savedInstanceState.getString("prompt");
            videoId = savedInstanceState.getInt("video_id");
            onPromptSelected(promptString);
        }

        if (mInterface.autoRecordDelay() < 1000) {
            mDelayStartCountdown.setVisibility(View.GONE);
        } else {
            mDelayStartCountdown.setText(Long.toString(mInterface.autoRecordDelay() / 1000));
        }
    }

    private int setLayoutHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int newHeight = height;

        if(height > width){
            newHeight = (height - width) / 2;
        }

        return newHeight;
    }

    private void onPromptSelected(String prompt){
        mZeeMeeQuestion.setText(prompt);
        promptString = prompt;
        mPromptText.setAlpha(0f);
        mPromptText.setVisibility(View.VISIBLE);
        mPromptText.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(null);
        mZeeMeeQuestion.setAlpha(0f);
        mZeeMeeQuestion.setVisibility(View.VISIBLE);
        mZeeMeeQuestion.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(null);
        mPromptRelative.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPromptRelative.setVisibility(View.GONE);
            }
        });
        mControlsRelative.setAlpha(0f);
        mControlsRelative.setVisibility(View.VISIBLE);
        mControlsRelative.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(null);
        inSelectAPromptMode = false;
    }

    private void onCloseClicked(){
        mPromptText.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPromptText.setVisibility(View.GONE);
            }
        });
        mZeeMeeQuestion.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mZeeMeeQuestion.setVisibility(View.GONE);
            }
        });
        mPromptRelative.setAlpha(0f);
        mPromptRelative.setVisibility(View.VISIBLE);
        mPromptRelative.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(null);
        mControlsRelative.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mControlsRelative.setVisibility(View.GONE);
            }
        });

        inSelectAPromptMode = true;
    }

    protected void onFlashModesLoaded() {
        if (getCurrentCameraPosition() != BaseCaptureActivity.CAMERA_POSITION_FRONT) {
            invalidateFlash(false);
        }
    }

    private boolean mDidAutoRecord = false;
    private Handler mDelayHandler;
    private int mDelayCurrentSecond = -1;

    protected void onCameraOpened() {
        if (mDidAutoRecord || mInterface == null || mInterface.useStillshot() || mInterface.autoRecordDelay() < 0 || getActivity() == null) {
            mDelayStartCountdown.setVisibility(View.GONE);
            mDelayHandler = null;
            return;
        }
        mDidAutoRecord = true;
        mButtonFacing.setVisibility(View.GONE);

        if (mInterface.autoRecordDelay() == 0) {
            mDelayStartCountdown.setVisibility(View.GONE);
            mIsRecording = startRecordingVideo();
            mDelayHandler = null;
            return;
        }

        mDelayHandler = new Handler();
        mButtonVideo.setEnabled(false);

        if (mInterface.autoRecordDelay() < 1000) {
            // Less than a second delay
            mDelayStartCountdown.setVisibility(View.GONE);
            mDelayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isAdded() || getActivity() == null || mIsRecording) return;
                    mButtonVideo.setEnabled(true);
                    mIsRecording = startRecordingVideo();
                    mDelayHandler = null;
                }
            }, mInterface.autoRecordDelay());
            return;
        }

        mDelayStartCountdown.setVisibility(View.VISIBLE);
        mDelayCurrentSecond = (int) mInterface.autoRecordDelay() / 1000;
        mDelayHandler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (!isAdded() || getActivity() == null || mIsRecording) return;
                mDelayCurrentSecond -= 1;
                mDelayStartCountdown.setText(Integer.toString(mDelayCurrentSecond));

                if (mDelayCurrentSecond == 0) {
                    mDelayStartCountdown.setVisibility(View.GONE);
                    mButtonVideo.setEnabled(true);
                    mIsRecording = startRecordingVideo();
                    mDelayHandler = null;
                    return;
                }

                mDelayHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private ObjectAnimator createFadeAnimation(View v, String propertyName){
        ObjectAnimator animator = new ObjectAnimator();
        animator.setDuration(200);
        animator.setPropertyName(propertyName);
        animator.setTarget(v);
        return animator;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mButtonVideo = null;
        mButtonStillshot = null;
        mButtonFacing = null;
        mButtonFlash = null;
        mRecordDuration = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        openCamera();
        if (mInterface != null && mInterface.hasLengthLimit()) {
            if (mInterface.countdownImmediately() || mInterface.getRecordingStart() > -1) {
                if (mInterface.getRecordingStart() == -1)
                    mInterface.setRecordingStart(System.currentTimeMillis());
                startCounter();
            } else {
                mRecordDuration.setText(String.format("-%s", CameraUtil.getDurationString(mInterface.getLengthLimit())));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (BaseCaptureInterface) activity;
    }

    @NonNull
    protected final File getOutputMediaFile() {
        return CameraUtil.makeTempFile(getActivity(), getArguments().getString(CameraIntentKey.SAVE_DIR), "VID_", ".mp4");
    }

    @NonNull
    protected final File getOutputPictureFile() {
        return CameraUtil.makeTempFile(getActivity(), getArguments().getString(CameraIntentKey.SAVE_DIR), "IMG_", ".jpg");
    }

    public abstract void openCamera();

    public abstract void closeCamera();

    public void cleanup() {
        closeCamera();
        releaseRecorder();
        stopCounter();
    }

    public abstract void takeStillshot();

    public abstract void onPreferencesUpdated();

    @Override
    public void onPause() {
        super.onPause();
        cleanup();
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    public final void startCounter() {
        if (mPositionHandler == null)
            mPositionHandler = new Handler();
        else mPositionHandler.removeCallbacks(mPositionUpdater);
        mPositionHandler.post(mPositionUpdater);
    }

    @BaseCaptureActivity.CameraPosition
    public final int getCurrentCameraPosition() {
        if (mInterface == null) return BaseCaptureActivity.CAMERA_POSITION_UNKNOWN;
        return mInterface.getCurrentCameraPosition();
    }

    public final int getCurrentCameraId() {
        if (mInterface.getCurrentCameraPosition() == BaseCaptureActivity.CAMERA_POSITION_BACK)
            return (Integer) mInterface.getBackCamera();
        else return (Integer) mInterface.getFrontCamera();
    }

    public final void stopCounter() {
        if (mPositionHandler != null) {
            mPositionHandler.removeCallbacks(mPositionUpdater);
            mPositionHandler = null;
        }
    }

    public final void releaseRecorder() {
        if (mMediaRecorder != null) {
            if (mIsRecording) {
                try {
                    mMediaRecorder.stop();
                } catch (Throwable t) {
                    //noinspection ResultOfMethodCallIgnored
                    new File(mOutputUri).delete();
                    t.printStackTrace();
                }
                mIsRecording = false;
            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public boolean startRecordingVideo() {
        if (mInterface != null && mInterface.hasLengthLimit() && !mInterface.countdownImmediately()) {
            // Countdown wasn't started in onResume, start it now
            if (mInterface.getRecordingStart() == -1)
                mInterface.setRecordingStart(System.currentTimeMillis());
            startCounter();
        }

        final int orientation = Degrees.getActivityOrientation(getActivity());
        //noinspection ResourceType
        getActivity().setRequestedOrientation(orientation);
        mInterface.setDidRecord(true);
        return true;
    }

    public void stopRecordingVideo(boolean reachedZero, int videoId) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("output_uri", mOutputUri);
        outState.putString("prompt", promptString);
        outState.putInt("video_id",videoId);
    }

    @Override
    public final String getOutputUri() {
        return mOutputUri;
    }

    protected final void throwError(Exception e) {
        Activity act = getActivity();
        if (act != null) {
            act.setResult(RESULT_CANCELED, new Intent().putExtra(MaterialCamera.ERROR_EXTRA, e));
            act.finish();
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.facing) {
            mInterface.toggleCameraPosition();
            setImageRes(mButtonFacing, mInterface.getCurrentCameraPosition() == BaseCaptureActivity.CAMERA_POSITION_BACK ?
                    mInterface.iconFrontCamera() : mInterface.iconRearCamera());
            closeCamera();
            openCamera();
            setupFlashMode();
        } else if (id == R.id.video) {
            if (mIsRecording) {
                stopRecordingVideo(false, videoId);
                mIsRecording = false;
            } else {
                if (getArguments().getBoolean(CameraIntentKey.SHOW_PORTRAIT_WARNING, true) &&
                        Degrees.isPortrait(getActivity())) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.mcam_portrait)
                            .content(R.string.mcam_portrait_warning)
                            .positiveText(R.string.mcam_yes)
                            .negativeText(android.R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    mIsRecording = startRecordingVideo();
                                }
                            })
                            .show();
                } else {
                    mIsRecording = startRecordingVideo();
                }
            }
        } else if (id == R.id.stillshot) {
            takeStillshot();
        } else if (id == R.id.flash) {
            invalidateFlash(true);
        }
    }

    private void invalidateFlash(boolean toggle) {
        if (toggle) mInterface.toggleFlashMode();
        setupFlashMode();
        onPreferencesUpdated();
    }

    private void setupFlashMode() {
        if (mInterface.shouldHideFlash()) {
            mButtonFlash.setVisibility(View.GONE);
            return;
        } else {
            mButtonFlash.setVisibility(View.VISIBLE);
        }

        final int res;
        switch (mInterface.getFlashMode()) {
            case FLASH_MODE_AUTO:
                res = mInterface.iconFlashAuto();
                break;
            case FLASH_MODE_ALWAYS_ON:
                res = mInterface.iconFlashOn();
                break;
            case FLASH_MODE_OFF:
            default:
                res = mInterface.iconFlashOff();
        }

        setImageRes(mButtonFlash, res);
    }

    @Override
    public void onStop() {
        onSaveInstanceState(getArguments());
        super.onStop();
    }


}