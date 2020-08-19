package com.jsc.quizapp.repository;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jsc.quizapp.model.QuestionDetailsModel;
import com.jsc.quizapp.model.QuizListModel;

import java.util.List;

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";
    private OnFireStoreTaskComplete onFireStoreTaskComplete;

    private OnFireStoreCheckVisibility checkVisibility;

    private FirebaseFirestore db;
    private Query quizRef;

    public FirebaseRepository(OnFireStoreTaskComplete onFireStoreTaskComplete, OnFireStoreCheckVisibility checkVisibility) {
        this.onFireStoreTaskComplete = onFireStoreTaskComplete;
        this.checkVisibility = checkVisibility;

    }


    public void getQuizData(String dept, String batch) {
        db = FirebaseFirestore.getInstance();
//        quizRef = db.collection("QuizList").whereEqualTo("visibility","public");
//        Log.d(TAG, "getQuizData: ");
//        quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    onFireStoreTaskComplete.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));
//                }else {
//                    onFireStoreTaskComplete.onQuizListException(task.getException());
//                }
//            }
//        });

        db.collection("AllAboutQuiz")
                .whereEqualTo("batch",batch)
                .whereEqualTo("dept",dept)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            onFireStoreTaskComplete.onQuizListException(e);
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            onFireStoreTaskComplete.quizListDataAdded(queryDocumentSnapshots.toObjects(QuestionDetailsModel.class));
                        }
                    }
                });

    }


    public void checkVisibility(String quizId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AllAboutQuiz")
                .document(quizId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            checkVisibility.visibilityChangedError(e);
                            return;
                        }
                        if (documentSnapshot != null) {
                            String v = documentSnapshot.getString("visibility");
                            checkVisibility.visibilityChanged(v);
                        } else {
                            checkVisibility.visibilityChanged("null");
                        }
                    }
                });
    }

    public interface OnFireStoreTaskComplete {
        void quizListDataAdded(List<QuestionDetailsModel> quizModelList);

        void onQuizListException(Exception e);
    }

    public interface OnFireStoreCheckVisibility {
        void visibilityChanged(String visibility);

        void visibilityChangedError(Exception e);
    }
}
