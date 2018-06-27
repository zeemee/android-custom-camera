package com.afollestad.materialcamera.internal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialcamera.R;

import java.util.ArrayList;

public class VideoQuestionPromptsAdapter extends RecyclerView.Adapter<PromptViewHolder>{

    private ArrayList<ZeeMeeQuestion> questions = new ArrayList<ZeeMeeQuestion>();
    private Context mContext;
    private PromptAnswerCallback callback;

    public VideoQuestionPromptsAdapter(Context context, PromptAnswerCallback pCallback){
        this.mContext = context;
        this.callback = pCallback;
    }

    public void addItems(ArrayList<ZeeMeeQuestion> list){
        questions.clear();
        questions.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public PromptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.prompt_item_layout, parent, false);
        return new PromptViewHolder(view, callback);
    }

    @Override
    public void onBindViewHolder(PromptViewHolder holder, int position) {
        ZeeMeeQuestion question = questions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questions.size() ;
    }
}
