package com.jsc.quizapp.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jsc.quizapp.R;

public class ResultFragment extends Fragment {

    private static final String TAG = "ResultFragment";

    private NavController navController;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String currentUserId;
    private String quiz_id;
    private String quiz_type;

    ConstraintLayout exerciseLayout, testLayout;

    private TextView correctTextView;
    private TextView wrongTextView;
    private TextView titleTextView;
    private TextView missedTextView;
    private TextView resultPercentTextView;

    private ProgressBar progressBar;

    private Button gotoHomeButton;


    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exerciseLayout = view.findViewById(R.id.exerciseLayout);
        exerciseLayout.setVisibility(View.GONE);
        testLayout = view.findViewById(R.id.tsetLayout);
        testLayout.setVisibility(View.GONE);

        navController = Navigation.findNavController(view);

        firebaseAuth = firebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            // Go back to Home
        } else {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }

        quiz_id = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();
        quiz_type = ResultFragmentArgs.fromBundle(getArguments()).getQuizType();

        if (quiz_type.contains("test")){
            testLayout.setVisibility(View.VISIBLE);
        }else {
            exerciseLayout.setVisibility(View.VISIBLE);
        }

        titleTextView = view.findViewById(R.id.result_title);
        correctTextView = view.findViewById(R.id.result_correct_text);
        wrongTextView = view.findViewById(R.id.result_wrong_text);
        missedTextView = view.findViewById(R.id.result_missed_text);
        resultPercentTextView = view.findViewById(R.id.result_percent);
        progressBar = view.findViewById(R.id.result_progressBar);
        gotoHomeButton = view.findViewById(R.id.result_home_btn);

        gotoHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        });

        //Get Results
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("AllAboutQuiz").document(quiz_id)
                .collection("Results").document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: " + task);

                    DocumentSnapshot results = task.getResult();

                    Long correct = results.getLong("correct");
                    Long wrong = results.getLong("wrong");
                    Long unanswered = results.getLong("unanswered");

                    correctTextView.setText(correct.toString());
                    missedTextView.setText(unanswered.toString());
                    wrongTextView.setText(wrong.toString());

                    //Calculate Progress
                    Long total = correct + wrong + unanswered;

                    Long percent = (correct * 100) / total;
                    resultPercentTextView.setText(percent.toString() + "%");

                    progressBar.setProgress(percent.intValue());

                } else {
                    titleTextView.setText(task.getException().getMessage());
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }
            }
        });
    }
}