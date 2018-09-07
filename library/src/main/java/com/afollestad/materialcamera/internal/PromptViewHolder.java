package com.afollestad.materialcamera.internal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialcamera.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PromptViewHolder extends RecyclerView.ViewHolder {

    TextView mQuestionText, mOverFlowText;
    Button mAnswerBtn, mSomeDumbButton;
    ImageView avatarOne, avatarTwo, avatarThree, avatarFour, avatarFive;
    //RelativeLayout avatarsRelative;
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
        //avatarsRelative = (RelativeLayout) view.findViewById(R.id.rlAvatarAnswers);
        avatarOne = (ImageView) view.findViewById(R.id.iv_avatar_one);
        avatarTwo = (ImageView) view.findViewById(R.id.iv_avatar_two);
        avatarThree = (ImageView) view.findViewById(R.id.iv_avatar_three);
        avatarFour = (ImageView) view.findViewById(R.id.iv_avatar_four);
        avatarFive = (ImageView) view.findViewById(R.id.iv_avatar_five);
    }

    public void bind(final PromptQuestion question, Context context){
        mOverFlowText.setVisibility(View.GONE);
        avatarOne.setVisibility(View.GONE);
        avatarTwo.setVisibility(View.GONE);
        avatarThree.setVisibility(View.GONE);
        avatarFour.setVisibility(View.GONE);
        avatarFive.setVisibility(View.GONE);
        mQuestionText.setText(question.getQuestionText());
        UserPrompt[] users = question.getUserArr();
        if(users != null) {
            for (int i = 0; i < users.length; i++) {
                if (users[i] != null) {
                    showAvatar(i, users[i], context);
                }
            }
            if(mOverFlowText != null) {
                if (question.isMoreThanFiveUsers() && avatarFive != null) {
                    mOverFlowText.setVisibility(View.VISIBLE);
                    mOverFlowText.setText("+" + Integer.toString(question.getOverFlowUsersNum()));
                }
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
            Glide.with(context).load(user.getThumb()).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_placeholder_large)).into(avatarOne);
        }
        if(index == 1){
            avatarTwo.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_placeholder_large)).into(avatarTwo);
        }

        if(index == 2){
            avatarThree.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_placeholder_large)).into(avatarThree);
        }

        if(index == 3){
            avatarFour.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_placeholder_large)).into(avatarFour);
        }

        if(index == 4){
            avatarFive.setVisibility(View.VISIBLE);
            Glide.with(context).load(user.getThumb()).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_placeholder_large)).into(avatarFive);
        }
    }
}
