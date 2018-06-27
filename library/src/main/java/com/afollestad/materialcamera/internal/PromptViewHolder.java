package com.afollestad.materialcamera.internal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialcamera.R;

public class PromptViewHolder extends RecyclerView.ViewHolder {

    TextView mQuestionText;
    Button mAnswerBtn;
    PromptAnswerCallback callback;

    public PromptViewHolder(View itemView, PromptAnswerCallback pCallback) {
        super(itemView);
        callback = pCallback;
        setupViews(itemView);
    }

    private void setupViews(View view){
        mQuestionText = (TextView) view.findViewById(R.id.tvPromptItem);
        mAnswerBtn = (Button) view.findViewById(R.id.btnAnswerItem);

    }

    public void bind(final ZeeMeeQuestion question){
        mQuestionText.setText(question.getZeemeeQuestion());
        mAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onAnswerButtonClicked(question.getZeemeeQuestion());
            }
        });
    }
}
