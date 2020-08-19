package com.jsc.quizapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jsc.quizapp.model.QuestionDetailsModel;
import com.jsc.quizapp.repository.FirebaseRepository;

import java.util.List;

public class QuizViewModel extends ViewModel implements FirebaseRepository.OnFireStoreTaskComplete, FirebaseRepository.OnFireStoreCheckVisibility {


    private MutableLiveData<List<QuestionDetailsModel>> quizListLiveData = new MutableLiveData<>();

    private MutableLiveData<String> visibilityLiveDate = new MutableLiveData<>();

    private FirebaseRepository repository = new FirebaseRepository(this, this);

    public QuizViewModel() {
    }

    public LiveData<List<QuestionDetailsModel>> getQuizListLiveData(String dept, String batch) {
        repository.getQuizData(dept, batch);
        return quizListLiveData;
    }

    public LiveData<String> getVisibility(String quizId) {
        repository.checkVisibility(quizId);
        return visibilityLiveDate;
    }

    @Override
    public void quizListDataAdded(List<QuestionDetailsModel> quizModelList) {
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
