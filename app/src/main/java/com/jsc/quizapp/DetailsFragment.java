package com.jsc.quizapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DetailsFragment";

    private QuizViewModel viewModel;
    private NavController navController;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView descTextView;
    private TextView difficultyTextView;
    private TextView totalQuesTextView;
    private TextView lastScoreTextView;
    private Button startQuizButton;

    private String quiz_id;

    private int position;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();
        navController = Navigation.findNavController(view);

        imageView = view.findViewById(R.id.details_image);
        titleTextView = view.findViewById(R.id.details_title);
        descTextView = view.findViewById(R.id.details_disc);
        difficultyTextView = view.findViewById(R.id.details_diffucilty_text);
        totalQuesTextView = view.findViewById(R.id.details_questions_text);
        lastScoreTextView = view.findViewById(R.id.details_score_text);
        startQuizButton = view.findViewById(R.id.details_start_btn);

        startQuizButton.setOnClickListener(this);

        Log.d(TAG, "Position "+position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);


        viewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

                Glide.with(getContext())
                        .load(quizListModels.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(imageView);

                titleTextView.setText(quizListModels.get(position).getName());
                descTextView.setText(quizListModels.get(position).getDesc());
                descTextView.setMovementMethod(new ScrollingMovementMethod());
                difficultyTextView.setText(quizListModels.get(position).getLevel());
                totalQuesTextView.setText(quizListModels.get(position).getQuestions()+"");

                quiz_id = quizListModels.get(position).getQuiz_id();

            }
        });
    }

    @Override
    public void onClick(View v) {
        DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
        action.setPosition(position);
        action.setQuizId(quiz_id);
        navController.navigate(action);
    }
}