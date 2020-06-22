package com.jsc.quizapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizViewModel extends ViewModel implements FirebaseRepository.OnFireStoreTaskComplete {


    private MutableLiveData <List<QuizListModel>> quizListLiveData = new MutableLiveData<>();

    private FirebaseRepository repository = new FirebaseRepository(this);

    public QuizViewModel() {
        repository.getQuizData();
    }
    public MutableLiveData<List<QuizListModel>> getQuizListLiveData() {
        return quizListLiveData;
    }

    @Override
    public void quizListDataAdded(List<QuizListModel> quizModelList) {
        quizListLiveData.setValue(quizModelList);
    }

    @Override
    public void onQuizListException(Exception e) {

    }
}
