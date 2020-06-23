package com.jsc.quizapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {

    FirebaseFirestore firestore;

    private static final String TAG = "QuizFragment";

    String quiz_id;

    ImageButton closeImageButton;
    TextView titleTextView;
    TextView questionNumberTextView;
    TextView timerTextView;
    TextView questionTextView;
    TextView varyingTextView;

    Button option_aButton;
    Button option_bButton;
    Button option_cButton;
    Button nextQuesButton;

    ProgressBar progressBar;

    private List<QuestionModel> allQuestionModelList = new ArrayList<>();

    public QuizFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeImageButton  = view.findViewById(R.id.quiz_close_btn);

        titleTextView = view.findViewById(R.id.quez_title);
        questionNumberTextView = view.findViewById(R.id.quiz_question_number);
        timerTextView = view.findViewById(R.id.quiz_question_time);
        questionTextView = view.findViewById(R.id.quiz_question_text);
        varyingTextView = view.findViewById(R.id.quiz_question_feedback);

        option_aButton = view.findViewById(R.id.quiz_option_one);
        option_bButton = view.findViewById(R.id.quiz_option_two);
        option_cButton = view.findViewById(R.id.quiz_option_three);
        nextQuesButton = view.findViewById(R.id.quiz_next_btn);

        progressBar = view.findViewById(R.id.quiz_progressBar);

        quiz_id = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();

        firestore = FirebaseFirestore.getInstance();


        //Get all question
        firestore.collection("QuizList")
                .document(quiz_id)
                .collection("Questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            allQuestionModelList = task.getResult().toObjects(QuestionModel.class);

                            // shuffle the list
                            Collections.shuffle(allQuestionModelList);

                            Log.d(TAG, "onComplete: "+ allQuestionModelList.get(0).getQuestion());
                        }else {
                            titleTextView.setText("Error loading data");
                        }

                    }
                });
    }

    public void pickQuestions(){

    }
}