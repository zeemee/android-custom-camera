package com.afollestad.materialcamera.internal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialcamera.R;
import com.bumptech.glide.Glide;

public class PromptViewHolder extends RecyclerView.ViewHolder {

    TextView mQuestionText, mOverFlowText;
    Button mAnswerBtn;
    ImageView avatarOne, avatarTwo, avatarThree, avatarFour, avatarFive;
    PromptAnswerCallback callback;

    public PromptViewHolder(View itemView, PromptAnswerCallback pCallback) {
        super(itemView);
        callback = pCallback;
        setupViews(itemView);
    }

    private void setupViews(View view){
        mQuestionText = (TextView) view.findViewById(R.id.tvPromptItem);
        mOverFlowText = (TextView) view.findViewById(R.id.tv_overflow);
        mAnswerBtn = (Button) view.findViewById(R.id.btnAnswerItem);
        avatarOne = (ImageView) view.findViewById(R.id.riv_one);
        avatarTwo = (ImageView) view.findViewById(R.id.riv_two);
        avatarThree = (ImageView) view.findViewById(R.id.riv_three);
        avatarFour = (ImageView) view.findViewById(R.id.riv_four);
        avatarFive = (ImageView) view.findViewById(R.id.riv_five);
    }

    public void bind(final PromptQuestion question, Context context){
        mQuestionText.setText(question.getQuestionText());
        UserPrompt[] users = question.getUserArr();
        avatarOne.setVisibility(View.GONE);
        avatarTwo.setVisibility(View.GONE);
        avatarThree.setVisibility(View.GONE);
        avatarFour.setVisibility(View.GONE);
        avatarFive.setVisibility(View.GONE);
        if(users != null) {
            for (int i = 0; i < users.length; i++) {
                if (users[i] != null) {
                    showAvatar(i, users[i], context);
                }
            }

            if (question.isMoreThanFiveUsers()) {
                avatarFive.setVisibility(View.GONE);
                mOverFlowText.setVisibility(View.VISIBLE);
                mOverFlowText.setText("+" + Integer.toString(question.getOverFlowUsersNum()));
            } else {
                mOverFlowText.setVisibility(View.GONE);
            }
        }
        if(!question.getGeneric() && question.getAnswered()){
            mAnswerBtn.setText("Answer Again");
        }
        else{
            mAnswerBtn.setText("Answer");
        }
        mAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onAnswerButtonClicked(question.getQuestionText(), question.getDbId());
            }
        });
    }

    public void showAvatar(int index, UserPrompt user, Context context){

        if(index == 0){
            avatarOne.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).into(avatarOne);
        }
        if(index == 1){
            avatarTwo.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).into(avatarTwo);
        }

        if(index == 2){
            avatarThree.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).into(avatarThree);
        }

        if(index == 3){
            avatarFour.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).into(avatarFour);
        }

        if(index == 4){
            avatarFive.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).into(avatarFive);
        }
    }
}
