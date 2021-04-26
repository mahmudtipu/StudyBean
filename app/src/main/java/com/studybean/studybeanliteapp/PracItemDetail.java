package com.studybean.studybeanliteapp;

/**
 * Created by Tipu on 3/14/2018.
 */

public class PracItemDetail {
    private String Question,AnswerA,AnswerB,AnswerC,AnswerD,CorrectAnswer,CategoryId,IsImageQuestion,Des,Answer,Date,CategoryId2;

    public PracItemDetail() {
    }

    public PracItemDetail(String question, String answerA, String answerB, String answerC, String answerD, String correctAnswer, String categoryId, String isImageQuestion, String des, String answer,String date, String categoryId2) {
        Question = question;
        AnswerA = answerA;
        AnswerB = answerB;
        AnswerC = answerC;
        AnswerD = answerD;
        CorrectAnswer = correctAnswer;
        CategoryId = categoryId;
        IsImageQuestion = isImageQuestion;
        Des = des;
        Answer = answer;
        Date = date;
        CategoryId2 = categoryId2;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswerA() {
        return AnswerA;
    }

    public void setAnswerA(String answerA) {
        AnswerA = answerA;
    }

    public String getAnswerB() {
        return AnswerB;
    }

    public void setAnswerB(String answerB) {
        AnswerB = answerB;
    }

    public String getAnswerC() {
        return AnswerC;
    }

    public void setAnswerC(String answerC) {
        AnswerC = answerC;
    }

    public String getAnswerD() {
        return AnswerD;
    }

    public void setAnswerD(String answerD) {
        AnswerD = answerD;
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        CorrectAnswer = correctAnswer;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getIsImageQuestion() {
        return IsImageQuestion;
    }

    public void setIsImageQuestion(String isImageQuestion) {
        IsImageQuestion = isImageQuestion;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCategoryId2() {
        return CategoryId2;
    }

    public void setCategoryId2(String categoryId2) {
        CategoryId2 = categoryId2;
    }
}
