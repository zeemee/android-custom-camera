package com.afollestad.materialcamerasample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.afollestad.materialcamera.internal.PromptQuestion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int CAMERA_RQ = 6969;
    private final static int PERMISSION_RQ = 84;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.launchCamera).setOnClickListener(this);
        findViewById(R.id.launchCameraStillshot).setOnClickListener(this);
        findViewById(R.id.launchFromFragment).setOnClickListener(this);
        findViewById(R.id.launchFromFragmentSupport).setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to save videos in external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_RQ);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.launchFromFragment) {
            Intent intent = new Intent(this, FragmentActivity.class);
            startActivity(intent);
            return;
        }
        if (view.getId() == R.id.launchFromFragmentSupport) {
            Intent intent = new Intent(this, FragmentActivity.class);
            intent.putExtra("support", true);
            startActivity(intent);
            return;
        }

        File saveDir = null;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
            saveDir.mkdirs();
        }

        String jsonString = "{\"data\":{\"me\":{\"promptQuestions\":{\"edges\":[{\"node\":{\"dbId\":999999,\"questionText\":\"What's on your mind?\",\"isGeneric\":true,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Adam Mack\"}}},{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}},{\"node\":{\"user\":{\"name\":\"Babs 2\"}}}]}}},{\"node\":{\"dbId\":41,\"questionText\":\"Do you have any hidden talents? What are they?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Luke Davis\"}}},{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"Adam Mack\"}}},{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}},{\"node\":{\"user\":{\"name\":\"ZeeMee Admin\"}}}]}}},{\"node\":{\"dbId\":40,\"questionText\":\"How do you keep yourself motivated even when things don't go your way? \",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}}]}}},{\"node\":{\"dbId\":39,\"questionText\":\"In what kind of environment do you learn best?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}}]}}},{\"node\":{\"dbId\":38,\"questionText\":\"How do you think you can impact the future?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}}]}}},{\"node\":{\"dbId\":37,\"questionText\":\"If tomorrow were your last day on Earth and you could do anything you wanted, how would you spend your day?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}}]}}},{\"node\":{\"dbId\":36,\"questionText\":\"Would you rather have 3 wishes in 10 years or 1 wish today? Why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}}]}}},{\"node\":{\"dbId\":35,\"questionText\":\"Can you describe a time you had to deal with an emergency?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":34,\"questionText\":\"What is one thing you think is important to help people succeed?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":33,\"questionText\":\"Can you tell us about a time that someone you know has done something nice for you?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":32,\"questionText\":\"You get to have a cup of coffee with any figure in history. Who is it and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":31,\"questionText\":\"Who in your life would you like to thank, and what would you like to say to them?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":30,\"questionText\":\"What are you looking for in a college?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Alícia Test\"}}}]}}},{\"node\":{\"dbId\":29,\"questionText\":\"How have you changed in the last year?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":28,\"questionText\":\"What does perseverence mean to you?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":27,\"questionText\":\"Who inspires you, and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":26,\"questionText\":\"If you had a song playing when you entered the room, what song would it be?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}}]}}},{\"node\":{\"dbId\":25,\"questionText\":\"Can you describe a time you've followed through on something challenging?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}}]}}},{\"node\":{\"dbId\":24,\"questionText\":\"What is one thing that isn't anywhere else in your college application or resume, but is very important to you?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":23,\"questionText\":\"Who is your favorite fictional character and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":22,\"questionText\":\"Would you rather be born with an elephant trunk or a giraffes neck?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":21,\"questionText\":\"What would the title of your autobiography be?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":20,\"questionText\":\"If you could change anything about your high school, what would you change and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":19,\"questionText\":\"Who has been most influential in your life?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":18,\"questionText\":\"You have a time machine. To what time period would you travel and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":17,\"questionText\":\"How would your friends describe you?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":16,\"questionText\":\"Would you like to live abroad? If so, where and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":15,\"questionText\":\"You get one superpower for one day. What is it?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":14,\"questionText\":\"What is the one possession you cannot live without?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"bbb\"}}}]}}},{\"node\":{\"dbId\":13,\"questionText\":\"What is a long-term goal you are passionate about?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"Test Alicia\"}}},{\"node\":{\"user\":{\"name\":\"Mr. Ken\"}}},{\"node\":{\"user\":{\"name\":\"babba\"}}}]}}},{\"node\":{\"dbId\":12,\"questionText\":\"What is a skill you'd like to develop and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}}]}}},{\"node\":{\"dbId\":11,\"questionText\":\"What is your most embarrasing moment in high school?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":10,\"questionText\":\"What is your biggest fear in life?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Mr. Ken\"}}}]}}},{\"node\":{\"dbId\":9,\"questionText\":\"You're stranded on a deserted island. You get one book, one movie, and one tool. Which are they?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}}]}}},{\"node\":{\"dbId\":8,\"questionText\":\"If you could meet any famous person, who would it be and why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":7,\"questionText\":\"Who is your best friend? Why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[]}}},{\"node\":{\"dbId\":6,\"questionText\":\"What is your spirit animal?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"ken kyger\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}}]}}},{\"node\":{\"dbId\":5,\"questionText\":\"Which app can you not live without?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"Dave woollard\"}}}]}}},{\"node\":{\"dbId\":4,\"questionText\":\"What is your favorite book or TV show? Why?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"Dave woollard\"}}}]}}},{\"node\":{\"dbId\":3,\"questionText\":\"What did you do today?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"Dave woollard\"}}},{\"node\":{\"user\":{\"name\":\"davewooll\"}}}]}}},{\"node\":{\"dbId\":2,\"questionText\":\"What's your name and where do you live?\",\"isGeneric\":false,\"isAnswered\":false,\"relatedAnswers\":{\"edges\":[{\"node\":{\"user\":{\"name\":\"ken kyger\"}}},{\"node\":{\"user\":{\"name\":\"Gilbert Test\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bbb\"}}},{\"node\":{\"user\":{\"name\":\"bot\"}}}]}}}]}}}}";
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MaterialCamera materialCamera = new MaterialCamera(this)
                .saveDir(saveDir)
                .showPortraitWarning(false)
                .allowRetry(true)
                .defaultToFrontFacing(true)
                .allowRetry(true)
                .autoSubmit(false)
                .iconRearCamera(R.drawable.picture_switch)
                .iconFrontCamera(R.drawable.picture_switch)
                .iconRecord(R.drawable.inactive_record_button)
                .iconStop(R.drawable.record)
                .videoQuestions(obj)
                .labelConfirm(R.string.mcam_use_video);

        if (view.getId() == R.id.launchCameraStillshot)
            materialCamera
                    .stillShot() // launches the Camera in stillshot mode
                    .labelConfirm(R.string.mcam_use_stillshot);
        materialCamera.start(CAMERA_RQ);
    }

    private String readableFileSize(long size) {
        if (size <= 0) return size + " B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String fileSize(File file) {
        return readableFileSize(file.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {
            if (resultCode == RESULT_OK) {
                final File file = new File(data.getData().getPath());
                Toast.makeText(this, String.format("Saved to: %s, size: %s",
                        file.getAbsolutePath(), fileSize(file)), Toast.LENGTH_LONG).show();
            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // Sample was denied WRITE_EXTERNAL_STORAGE permission
            Toast.makeText(this, "Videos will be saved in a cache directory instead of an external storage directory since permission was denied.", Toast.LENGTH_LONG).show();
        }
    }
}