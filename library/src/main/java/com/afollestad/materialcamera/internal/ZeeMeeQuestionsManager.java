package com.afollestad.materialcamera.internal;

import java.util.ArrayList;

/**
 * Created by babs on 6/21/17.
 */

public class ZeeMeeQuestionsManager {
    public static ArrayList<ZeeMeeQuestion> zeeMeeQuestions;

    public static ArrayList<ZeeMeeQuestion> getZeemeeQuestions() {
        return zeeMeeQuestions;
    }

    public static void setZeemeeQuestions(ArrayList<ZeeMeeQuestion> zeemeeQuestions) {
        ZeeMeeQuestionsManager.zeeMeeQuestions = zeemeeQuestions;
    }
}
