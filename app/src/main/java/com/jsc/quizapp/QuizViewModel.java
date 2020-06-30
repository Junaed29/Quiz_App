package com.jsc.quizapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizViewModel extends ViewModel implements FirebaseRepository.OnFireStoreTaskComplete, FirebaseRepository.OnFireStoreCheckVisibility {


    private MutableLiveData <List<QuizListModel>> quizListLiveData = new MutableLiveData<>();

    private MutableLiveData<String> visibilityLiveDate = new MutableLiveData<>();

    private FirebaseRepository repository = new FirebaseRepository(this,this);

    public QuizViewModel() {
        repository.getQuizData();
    }

    public LiveData<List<QuizListModel>> getQuizListLiveData() {
        repository.getQuizData();
        return quizListLiveData;
    }

    public LiveData<String> getVisibility(String quizId){
        repository.checkVisibility(quizId);
        return visibilityLiveDate;
    }

    @Override
    public void quizListDataAdded(List<QuizListModel> quizModelList) {
        quizListLiveData.setValue(quizModelList);
    }

    @Override
    public void onQuizListException(Exception e) {

    }

    @Override
    public void visibilityChanged(String visibility) {
        visibilityLiveDate.setValue(visibility);
    }

    @Override
    public void visibilityChangedError(Exception e) {

    }
}
