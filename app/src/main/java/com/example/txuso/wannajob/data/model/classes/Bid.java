package com.example.txuso.wannajob.data.model.classes;

/**
 * Created by Txuso on 26/05/16.
 */
public class Bid {

    private int number;
    private String jobId;
    private String userId;

    public Bid(int number, String jobId, String userId) {
        this.number = number;
        this.jobId = jobId;
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }


    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getNumber() {
        return number;
    }

    public String getUserId() {
        return userId;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
