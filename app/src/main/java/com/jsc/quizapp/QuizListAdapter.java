package com.jsc.quizapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {


    private List<QuizListModel> quizListModels;

    private OnClickQuizButton clickQuizButton;

    public QuizListAdapter(OnClickQuizButton clickQuizButton) {
        this.clickQuizButton = clickQuizButton;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_list_item, parent, false);

        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        holder.titleTextView.setText(quizListModels.get(position).getName());

        String imageUrl = quizListModels.get(position).getImage();
//        Glide.with(holder.itemView.getContext())
//                .load(imageUrl)
//                .centerCrop()
//                .placeholder(R.drawable.placeholder_image)
//                .into(holder.placeHolderImageView);

        String desc_text = quizListModels.get(position).getDesc();
        if (desc_text.length()>150){
            desc_text = desc_text.substring(0,150);
        }

        holder.descTextView.setText(desc_text+"...");
        holder.difficultyTextView.setText(quizListModels.get(position).getLevel());

    }

    @Override
    public int getItemCount() {
        if (quizListModels == null) {
            return 0;
        } else {
            return quizListModels.size();
        }
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView placeHolderImageView;
        TextView titleTextView;
        TextView descTextView;
        TextView difficultyTextView;
        Button startQuizButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            placeHolderImageView = itemView.findViewById(R.id.list_image);
            titleTextView = itemView.findViewById(R.id.list_title);
            descTextView = itemView.findViewById(R.id.list_disc);
            difficultyTextView = itemView.findViewById(R.id.list_diffucilty);
            startQuizButton = itemView.findViewById(R.id.list_btn);

            startQuizButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickQuizButton.onItemClick(getAdapterPosition());
        }
    }

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    public interface OnClickQuizButton{
        void onItemClick(int position);
    }
}
