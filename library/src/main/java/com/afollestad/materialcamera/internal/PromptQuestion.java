package com.afollestad.materialcamera.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PromptQuestion implements Parcelable{

    private int dbId;
    private String questionText;
    private boolean isGeneric;
    private boolean isAnswered;
    //The related answers will come later

    public PromptQuestion(int dbId, String question, boolean generic, boolean answer){
        this.dbId = dbId;
        this.questionText = question;
        this.isGeneric = generic;
        this.isAnswered = answer;
    }

    protected PromptQuestion(Parcel in) {
        dbId = in.readInt();
        questionText = in.readString();
        isGeneric = in.readByte() != 0;
        isAnswered = in.readByte() != 0;
    }

    public static final Creator<PromptQuestion> CREATOR = new Creator<PromptQuestion>() {
        @Override
        public PromptQuestion createFromParcel(Parcel in) {
            return new PromptQuestion(in);
        }

        @Override
        public PromptQuestion[] newArray(int size) {
            return new PromptQuestion[size];
        }
    };

    public void setDbId(int db){
        this.dbId = db;
    }

    public int getDbId(){
        return dbId;
    }

    public void setQuestionText(String question){
        this.questionText = question;
    }

    public String getQuestionText(){
        return questionText;
    }

    public void setGeneric(boolean pGeneric){
        this.isGeneric = pGeneric;
    }

    public boolean getGeneric(){
        return isGeneric;
    }

    public void setAnswered(boolean pAnswered){
        this.isAnswered = pAnswered;
    }

    public boolean getAnswered(){
        return isAnswered;
    }

    public static ArrayList<PromptQuestion> parse(JSONObject responseObj){
        ArrayList<PromptQuestion> questionList = new ArrayList<PromptQuestion>();
        try {
            if (responseObj.has("data")) {
                JSONObject user_json = responseObj.getJSONObject("data");
                if (user_json.has("me")) {
                    JSONObject meObject = user_json.getJSONObject("me");
                    if (meObject.has("promptQuestions")) {
                        JSONObject prompts = meObject.getJSONObject("promptQuestions");
                        if (prompts.has("edges")) {
                            JSONArray edges = prompts.getJSONArray("edges");
                            JSONObject obj;
                            int genericPointer = 0, nonGenericPointer = 0;
                            for (int i = 0; i < edges.length(); i++) {
                                obj = edges.getJSONObject(i);
                                if (obj.has("node")) {
                                    JSONObject node = obj.getJSONObject("node");
                                    int db = node.getInt("dbId");
                                    String questText = node.getString("questionText");
                                    boolean generic = node.getBoolean("isGeneric");
                                    boolean answered = node.getBoolean("isAnswered");
                                    PromptQuestion question = new PromptQuestion(db, questText, generic, answered);
                                    if (questionList.size() > 0) {
                                        if (question.getGeneric()) {
                                            if (genericPointer >= questionList.size()) {
                                                questionList.add(question);
                                            } else {
                                                questionList.add(genericPointer, question);
                                                genericPointer++;
                                            }
                                            if (nonGenericPointer <= genericPointer) {
                                                nonGenericPointer = genericPointer + 1;
                                            }
                                        } else if (question.getAnswered()) {
                                            questionList.add(question);
                                        }
                                        //This is for a non-generic question that hasn't been answered
                                        else {
                                            if (nonGenericPointer >= questionList.size()) {
                                                questionList.add(question);
                                            } else {
                                                questionList.add(nonGenericPointer, question);
                                                nonGenericPointer++;
                                            }

                                        }

                                    } else {
                                        questionList.add(question);
                                        if (question.getGeneric()) {
                                            genericPointer++;
                                            if (nonGenericPointer <= genericPointer) {
                                                nonGenericPointer = genericPointer + 1;
                                            }
                                        } else if (!question.getAnswered()) {
                                            nonGenericPointer++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(JSONException e){
            Log.e("Tag", "Prompt::parse(JSONObject): ERROR: " + e);
        }

        return questionList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(dbId);
        parcel.writeString(questionText);
        parcel.writeByte((byte) (isGeneric ? 1 : 0));
        parcel.writeByte((byte) (isAnswered ? 1 : 0));
    }
}

