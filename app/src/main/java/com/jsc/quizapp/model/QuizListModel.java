package com.jsc.quizapp.model;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {

    @DocumentId
    private String quiz_id;
    private String name, desc, level, image, visibility;
    private long questions;


    public QuizListModel() {
    }

    public QuizListModel(String quiz_id, String name, String desc, String level, String image, String visibility, long questions) {
        this.quiz_id = quiz_id;
        this.name = name;
        this.desc = desc;
        this.level = level;
        this.image = image;
        this.visibility = visibility;
        this.questions = questions;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public long getQuestions() {
        return questions;
    }

    public void setQuestions(long questions) {
        this.questions = questions;
    }


}
