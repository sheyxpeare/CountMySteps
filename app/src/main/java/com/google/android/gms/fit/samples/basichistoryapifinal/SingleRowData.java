package com.google.android.gms.fit.samples.basichistoryapifinal;

/*
Contains the Single Row information
 */
public class SingleRowData {

    private int rank;
    public void setRank(int val) {
        rank = val;
    }
    public int getRank() {
        return rank;
    }

    private String username;
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    private int stepCount;
    public void setStepCount(int val) {
        stepCount = val;
    }
    public int getStepCount() {
        return stepCount;
    }

    public SingleRowData(int rank, String username, int stepCount) {
        this.rank = rank;
        this.username = username;
        this.stepCount = stepCount;
    }

}
