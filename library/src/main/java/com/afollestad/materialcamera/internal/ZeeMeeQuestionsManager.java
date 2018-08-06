package com.afollestad.materialcamera.internal;

import java.util.ArrayList;

/**
 * Created by babs on 6/21/17.
 */

public class ZeeMeeQuestionsManager {
    public static ArrayList<PromptQuestion> zeeMeeQuestions;

    public static ArrayList<PromptQuestion> getZeemeeQuestions() {
        return zeeMeeQuestions;
    }

    public static void setZeemeeQuestions(ArrayList<PromptQuestion> zeemeeQuestions) {
        ZeeMeeQuestionsManager.zeeMeeQuestions = zeemeeQuestions;
    }
}
