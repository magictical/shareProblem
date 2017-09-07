package com.example.android.climbtogether;

/**
 * Created by MD on 2017-01-28.
 */

public class Problem /*extends Gym <=나중에 고려해보자*/ {
    private String problemName;
    private String problemPhotoUri;
    private String problemLevel;
    private String problemCreator;
    private String problemFinisher;
    private String problemChallenger;
    private String problemExpireDay;

    public Problem() {
    }

    public Problem(String name, String problemPhotoUri, String level, String creator, String finisher,
                   String challenger, String expireDay) {
        problemName = name;
        this.problemPhotoUri = problemPhotoUri;
        problemLevel = level;
        problemCreator = creator;
        problemFinisher = finisher;
        problemChallenger = challenger;
        problemExpireDay = expireDay;
    }


    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemPhotoUri() {
        return problemPhotoUri;
    }

    public void setProblemPhotoUri(String problemPhotoUri) {
        this.problemPhotoUri = problemPhotoUri;
    }

    public String getProblemLevel() {
        return problemLevel;
    }

    public void setProblemLevel(String problemLevel) {
        this.problemLevel = problemLevel;
    }

    public String getProblemCreator() {
        return problemCreator;
    }

    public void setProblemCreator(String problemCreator) {
        this.problemCreator = problemCreator;
    }

    public String getProblemFinisher() {
        return problemFinisher;
    }

    public void setProblemFinisher(String problemFinisher) {
        this.problemFinisher = problemFinisher;
    }

    public String getProblemChallenger() {
        return problemChallenger;
    }

    public void setProblemChallenger(String problemChallenger) {
        this.problemChallenger = problemChallenger;
    }

    public String getProblemExpireDay() {
        return problemExpireDay;
    }

    public void setProblemExpireDay(String problemExpireDay) {
        this.problemExpireDay = problemExpireDay;
    }
}