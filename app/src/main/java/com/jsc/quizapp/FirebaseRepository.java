package com.jsc.quizapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseRepository {
    private OnFireStoreTaskComplete onFireStoreTaskComplete;

    public FirebaseRepository(OnFireStoreTaskComplete onFireStoreTaskComplete) {
        this.onFireStoreTaskComplete = onFireStoreTaskComplete;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference quizRef = db.collection("QuizList");

    public void getQuizData(){
        quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    onFireStoreTaskComplete.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));
                }else {
                    onFireStoreTaskComplete.onQuizListException(task.getException());
                }
            }
        });
    }

    public interface OnFireStoreTaskComplete{
        void quizListDataAdded(List<QuizListModel> quizModelList);
        void onQuizListException(Exception e);
    }
}
