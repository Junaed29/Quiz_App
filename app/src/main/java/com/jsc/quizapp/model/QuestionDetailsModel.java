package com.jsc.quizapp.model;

import com.google.firebase.firestore.DocumentId;

public class QuestionDetailsModel {

    @DocumentId
    String documentId;

    String questionTitle;
    String courseId;
    String batch;
    String dept;
    String visibility;
    String userId;
    String quizType;




    public QuestionDetailsModel() {
    }

    public QuestionDetailsModel(String questionTitle, String courseId, String batch, String dept, String visibility, String userId, String quizType) {
        this.questionTitle = questionTitle;
        this.courseId = courseId;
        this.batch = batch;
        this.dept = dept;
        this.visibility = visibility;
        this.userId = userId;
        this.quizType = quizType;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }


    @Override
    public String toString() {
        return "QuestionDetailsModel{" +
                "documentId='" + documentId + '\'' +
                ", questionTitle='" + questionTitle + '\'' +
                ", courseId='" + courseId + '\'' +
                ", batch='" + batch + '\'' +
                ", dept='" + dept + '\'' +
                ", visibility='" + visibility + '\'' +
                ", userId='" + userId + '\'' +
                ", quizType='" + quizType + '\'' +
                '}';
    }
}
