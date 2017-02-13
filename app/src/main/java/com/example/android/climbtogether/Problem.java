package com.example.android.climbtogether;

import java.util.Date;

/**
 * Created by MD on 2017-01-28.
 */

public class Problem {
    private  String problemName;
    private Date problemDate;
    private int problemLevel;

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public Date getProblemDate() {
        return problemDate;
    }

    public void setProblemDate(Date problemDate) {
        this.problemDate = problemDate;
    }

    public int getProblemLevel() {
        return problemLevel;
    }

    public void setProblemLevel(int problemLevel) {
        this.problemLevel = problemLevel;
    }
}
