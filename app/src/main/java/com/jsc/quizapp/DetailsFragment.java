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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DetailsFragment";

    private QuizViewModel viewModel;
    private NavController navController;

    Boolean visibility = false;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView descTextView;
    private TextView difficultyTextView;
    private TextView totalQuesTextView;
    private TextView lastScoreTextView;
    private Button startQuizButton;

    private String quiz_id;
    private String quiz_name;

    private int position;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private String currentUserId;

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


        firebaseAuth = firebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            // Go back to Home
        }else {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }

        viewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);


        viewModel.getQuizListLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

//                Glide.with(getContext())
//                        .load(quizListModels.get(position).getImage())
//                        .centerCrop()
//                        .placeholder(R.drawable.placeholder_image)
//                        .into(imageView);

                titleTextView.setText(quizListModels.get(position).getName());
                descTextView.setText(quizListModels.get(position).getDesc());
                descTextView.setMovementMethod(new ScrollingMovementMethod());
                difficultyTextView.setText(quizListModels.get(position).getLevel());
                totalQuesTextView.setText(quizListModels.get(position).getQuestions()+"");

                quiz_id = quizListModels.get(position).getQuiz_id();
                quiz_name = quizListModels.get(position).getName();

                //Load Results Data
                loadResultsData();

            }
        });



    }

    private void loadResultsData() {

        viewModel.getVisibility(quiz_id).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: "+s);
                visibility = s.equals("public");
                if (visibility){
                    startQuizButton.setText("Start Quiz");
                    startQuizButton.setBackground(getResources().getDrawable(R.drawable.correct_ans_btn_bg,null));
                }else {
                    startQuizButton.setText("Private");
                    startQuizButton.setBackground(getResources().getDrawable(R.drawable.private_btn_bg,null));
                }
            }
        });

        //Get Results
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("QuizList").document(quiz_id)
                .collection("Results").document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: "+task);

                    DocumentSnapshot results = task.getResult();

                   if (results != null && results.exists()){
                       Long correct = results.getLong("correct");
                       Long wrong = results.getLong("wrong");
                       Long unanswered = results.getLong("unanswered");

//                       Log.d(TAG, "correct: "+correct);
//                       Log.d(TAG, "wrong: "+wrong);
//                       Log.d(TAG, "unanswered: "+unanswered);

                       //Calculate Progress
                       Long total = correct + wrong + unanswered;

                       Long percent = (correct*100)/total;
                       lastScoreTextView.setText(percent.toString()+"%");

                   }
                }else {
                    lastScoreTextView.setText("NA");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (visibility) {
            DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
            action.setPosition(position);
            action.setQuizId(quiz_id);
            action.setQuizName(quiz_name);
            navController.navigate(action);
        }else {
            Toast.makeText(getActivity(), "You can't participate\nIt's private", Toast.LENGTH_LONG).show();
        }
    }
}