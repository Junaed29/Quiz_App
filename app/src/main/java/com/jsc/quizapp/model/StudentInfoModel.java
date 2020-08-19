package com.jsc.quizapp.model;

public class StudentInfoModel {

    String studentName;
    String studentId;
    String studentBatch;
    String studentDept;

    public StudentInfoModel() {
    }

    public StudentInfoModel(String studentName, String studentId, String studentBatch, String studentDept) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.studentBatch = studentBatch;
        this.studentDept = studentDept;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentBatch() {
        return studentBatch;
    }

    public void setStudentBatch(String studentBatch) {
        this.studentBatch = studentBatch;
    }

    public String getStudentDept() {
        return studentDept;
    }

    public void setStudentDept(String studentDept) {
        this.studentDept = studentDept;
    }
}
