package com.jsc.quizapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QuizFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String currentUserId;
    private String quizTitle;

    private Dialog dialog;

    private static final String TAG = "QuizFragment";

    String quiz_id;

    ImageButton closeImageButton;
    TextView titleTextView;
    TextView questionNumberTextView;
    TextView timerTextView;
    TextView questionTextView;
    TextView feedbackTextView;

    Button option_aButton;
    Button option_bButton;
    Button option_cButton;
    Button nextQuesButton;

    ProgressBar progressBar;

    //Check user can answer or not
    boolean canAnswer = false;

    //Current Question Number
    int currentQuestionNumber = 0;

    //Total Selected correct and wrong answer
    int totalSelectedCorrectAnswer = 0;
    int totalSelectedWrongAnswer = 0;

    //Total Missed Questions
    int totalNotAnswered = 0;

    CountDownTimer countDownTimer;

    private List<QuestionModel> allQuestionModelList = new ArrayList<>();

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openQuitDialog();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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


        navController = Navigation.findNavController(view);

        firebaseAuth = firebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            // Go back to Home
        }else {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }

        closeImageButton = view.findViewById(R.id.quiz_close_btn);

        titleTextView = view.findViewById(R.id.quez_title);
        questionNumberTextView = view.findViewById(R.id.quiz_question_number);
        timerTextView = view.findViewById(R.id.quiz_question_time);
        questionTextView = view.findViewById(R.id.quiz_question_text);
        feedbackTextView = view.findViewById(R.id.quiz_question_feedback);


        option_aButton = view.findViewById(R.id.quiz_option_one);
        option_bButton = view.findViewById(R.id.quiz_option_two);
        option_cButton = view.findViewById(R.id.quiz_option_three);
        nextQuesButton = view.findViewById(R.id.quiz_next_btn);

        progressBar = view.findViewById(R.id.quiz_progressBar);

        quiz_id = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        quizTitle = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();





        firestore = FirebaseFirestore.getInstance();


        //Get all question
        firestore.collection("QuizList")
                .document(quiz_id)
                .collection("Questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            allQuestionModelList = task.getResult().toObjects(QuestionModel.class);

                            // shuffle the list
                            Collections.shuffle(allQuestionModelList);

                            loadUI();
                            Log.d(TAG, "onComplete: " + allQuestionModelList.get(0).getQuestion());
                        } else {
                            titleTextView.setText("Error loading data");
                        }

                    }
                });

        option_aButton.setOnClickListener(this);
        option_bButton.setOnClickListener(this);
        option_cButton.setOnClickListener(this);
        nextQuesButton.setOnClickListener(this);
        closeImageButton.setOnClickListener(this);
    }

    private void loadUI() {
        // Load quiz data
        // Load the UI

        titleTextView.setText(quizTitle);

        questionTextView.setText("Load first question");

        // Enable Options
        enableOptions();

        //Load first Question
        loadQuestion(0);
    }

    private void loadQuestion(int questionNumber) {
        //Load Question Text
        questionTextView.setText(allQuestionModelList.get(questionNumber).getQuestion());

        //Load Options
        option_aButton.setText(allQuestionModelList.get(questionNumber).getOption_a());
        option_bButton.setText(allQuestionModelList.get(questionNumber).getOption_b());
        option_cButton.setText(allQuestionModelList.get(questionNumber).getOption_c());

        //Set canAnswer 'true'
        canAnswer = true;

        //Set the QuestionNumber Globally
        currentQuestionNumber = questionNumber;

        startTimer(questionNumber);
    }

    private void startTimer(int questionNumber) {

        questionNumberTextView.setText(String.valueOf((questionNumber+1)));

        final Long timeToAnswer = allQuestionModelList.get(questionNumber).getTimer();

        timerTextView.setText(timeToAnswer.toString());

        //Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        //Start CountDown
        countDownTimer = new CountDownTimer(timeToAnswer * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {

                // Update Timer
                timerTextView.setText(millisUntilFinished / 1000 + "");

                //Update ProgressBar
                Long percent = millisUntilFinished / (timeToAnswer * 10);
                progressBar.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                //Set canAnswer 'true'
                canAnswer = false;

                feedbackTextView.setText("Time Up! No Answer was Submitted");
                feedbackTextView.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                totalNotAnswered++;

                showNextButton();
            }
        };
        countDownTimer.start();
    }

    private void enableOptions() {
        // Showing the option Button
        option_aButton.setVisibility(View.VISIBLE);
        option_bButton.setVisibility(View.VISIBLE);
        option_cButton.setVisibility(View.VISIBLE);

        // Enable the buttons
        option_aButton.setEnabled(true);
        option_bButton.setEnabled(true);
        option_cButton.setEnabled(true);

        // Hide Feedback and nextButton
        feedbackTextView.setVisibility(View.INVISIBLE);
        nextQuesButton.setVisibility(View.INVISIBLE);
        nextQuesButton.setEnabled(false);
    }



    private void submitResults() {
        HashMap<String, Object> resultsMap = new HashMap<>();
        resultsMap.put("correct",totalSelectedCorrectAnswer);
        resultsMap.put("wrong",totalSelectedWrongAnswer);
        resultsMap.put("unanswered",totalNotAnswered);

        firestore.collection("QuizList")
                .document(quiz_id).collection("Results")
                .document(currentUserId).set(resultsMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            QuizFragmentDirections.ActionQuizFragmentToResultFragment action = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                            action.setQuizId(quiz_id);
                            navController.navigate(action);
                        }else {
                            titleTextView.setText(task.getException().getMessage());
                        }
                    }
                });
    }

    private void resetOptions() {

        option_aButton.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg,null));
        option_bButton.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg,null));
        option_cButton.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg,null));

        option_aButton.setTextColor(getResources().getColor(R.color.colorLightText,null));
        option_bButton.setTextColor(getResources().getColor(R.color.colorLightText,null));
        option_cButton.setTextColor(getResources().getColor(R.color.colorLightText,null));

        feedbackTextView.setVisibility(View.INVISIBLE);
        nextQuesButton.setVisibility(View.INVISIBLE);
        nextQuesButton.setEnabled(false);
    }

    private void selectedAnswer(Button selectedButton) {


        String itemSelected = (String) selectedButton.getText();
        String actualAnswer = allQuestionModelList.get(currentQuestionNumber).getAnswer();
        if (canAnswer){
            if (itemSelected.equals(actualAnswer)|itemSelected.contains(actualAnswer)|actualAnswer.contains(itemSelected)){
                //Correct Answer
                totalSelectedCorrectAnswer++;
                selectedButton.setBackground(getResources().getDrawable(R.drawable.correct_ans_btn_bg,null));

                //Set Feedback Text
                feedbackTextView.setText("Correct Answer");
                feedbackTextView.setTextColor(getResources().getColor(R.color.colorPrimary,null));
            }else {
                //Wrong Answer
                totalSelectedWrongAnswer++;
                selectedButton.setBackground(getResources().getDrawable(R.drawable.wrong_ans_btn_bg,null));

                //Set Feedback Text
                feedbackTextView.setText("Wrong Answer \nCorrect Answer : "+actualAnswer);
                feedbackTextView.setTextColor(getResources().getColor(R.color.colorAccent,null));
            }
            feedbackTextView.setMovementMethod(new ScrollingMovementMethod());

            // Set answer button color to black
            selectedButton.setTextColor(getResources().getColor(R.color.colorDark,null));

            //Set canAnswer false
            canAnswer = false;

            //Stop the timer
            countDownTimer.cancel();

            //Showing Next Button
            showNextButton();
        }
    }

    private void showNextButton() {
        if ((currentQuestionNumber+1) == allQuestionModelList.size()){
            nextQuesButton.setText("Submit Results");
        }
        feedbackTextView.setVisibility(View.VISIBLE);
        nextQuesButton.setVisibility(View.VISIBLE);
        nextQuesButton.setEnabled(true);
    }


    public void openQuitDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.exit_dialog_sample);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button cancelButton = dialog.findViewById(R.id.cancel_btn);
        Button confirmButton = dialog.findViewById(R.id.confirm_btn);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quiz_option_one:
                selectedAnswer(option_aButton);
                break;
            case R.id.quiz_option_two:
                selectedAnswer(option_bButton);
                break;
            case R.id.quiz_option_three:
                selectedAnswer(option_cButton);
                break;
            case R.id.quiz_next_btn:
                if ((currentQuestionNumber+1) == allQuestionModelList.size()){
                    submitResults();
                }else {
                    currentQuestionNumber++;
                    loadQuestion(currentQuestionNumber);
                    resetOptions();
                }
                break;
            case R.id.cancel_btn:
                dialog.dismiss();
                break;
            case R.id.confirm_btn:
                dialog.dismiss();
                //Set canAnswer false
                canAnswer = false;

                //Stop the timer
                countDownTimer.cancel();
                navController.popBackStack();
                break;

            case R.id.quiz_close_btn :
                openQuitDialog();
                break;
        }
    }
}