package com.afollestad.materialcamera.internal;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class UserPrompt {

    private String id;
    private String uuid;
    private String thumb;

    public static UserPrompt parse(JSONObject user_json) {
        UserPrompt ret = new UserPrompt();
        try {
            if (user_json.has("id")) {
                ret.setId(user_json.getString("id"));
            }

            if (user_json.has("profilePhoto") && !user_json.isNull("profilePhoto")) {
                JSONObject photo_json = user_json.getJSONObject("profilePhoto");
                if (photo_json.has("url")) {
                    ret.setThumb(photo_json.getString("url"));
                }
            }

            if (user_json.has("uuid")) {
                ret.setUuid(user_json.getString("uuid"));
            }

        } catch (JSONException e) {
            Log.e("UserPromptModel", "UserPrompt::parse(JSONObject): ERROR: " + e);
        }
        return ret;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb1) {
        thumb = thumb1;
    }

    public String getId() {
        return id;
    }

    public void setId(String user_id) {
        id = user_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
