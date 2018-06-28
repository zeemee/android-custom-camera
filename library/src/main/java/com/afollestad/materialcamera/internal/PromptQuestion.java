package com.afollestad.materialcamera.internal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PromptQuestion {

        private int dbId;
        private String questionText;
        private boolean isGeneric;
        private boolean isAnswered;
        //The related answers will come later

        public PromptQuestion(){

        }

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
                if (responseObj.has("edges")) {
                    JSONArray edges = responseObj.getJSONArray("edges");
                    JSONObject obj;
                    int genericPointer = 0, nonGenericPointer = 0;
                    for(int i = 0; i < edges.length(); i++){
                        obj = edges.getJSONObject(i);
                        if(obj.has("node")){
                            JSONObject node = obj.getJSONObject("node");
                            PromptQuestion question = new PromptQuestion();
                            question.setDbId(node.getInt("dbId"));
                            question.setQuestionText(node.getString("questionText"));
                            question.setGeneric(node.getBoolean("isGeneric"));
                            question.setAnswered(node.getBoolean("isAnswered"));
                            if(questionList.size() > 0){
                                if(question.getGeneric()){
                                    if(genericPointer >= questionList.size()){
                                        questionList.add(question);
                                    }
                                    else{
                                        questionList.add(genericPointer, question);
                                        genericPointer++;
                                    }
                                    if(nonGenericPointer <= genericPointer){
                                        nonGenericPointer = genericPointer + 1;
                                    }
                                }
                                else if(question.getAnswered()){
                                    questionList.add(question);
                                }
                                //This is for a non-generic question that hasn't been answered
                                else{
                                    if(nonGenericPointer >= questionList.size()){
                                        questionList.add(question);
                                    }
                                    else{
                                        questionList.add(nonGenericPointer, question);
                                        nonGenericPointer++;
                                    }

                                }

                            }
                            else{
                                questionList.add(question);
                                if(question.getGeneric()){
                                    genericPointer++;
                                    if(nonGenericPointer <= genericPointer){
                                        nonGenericPointer = genericPointer + 1;
                                    }
                                }
                                else if(!question.getAnswered()){
                                    nonGenericPointer++;
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
    }

