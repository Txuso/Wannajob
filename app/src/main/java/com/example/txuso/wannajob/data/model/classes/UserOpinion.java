package com.example.txuso.wannajob.data.model.classes;

/**
 * Created by Txuso on 17/02/16.
 */
public class UserOpinion {

    private String name;
    private int stars;
    private String jobName;
    private String imageUrl;
    private String opinion;
    private String fromId;
    private String userID;
    public UserOpinion(String name, String jobName, String opinion, int stars, String imageUrl, String userID) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.jobName = jobName;
        this.opinion = opinion;
        this.stars = stars;
        this.userID = userID;
    }

    public UserOpinion(String name, String jobName, String opinion, int stars, String imageUrl, String userID, String fromId) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.jobName = jobName;
        this.opinion = opinion;
        this.stars = stars;
        this.userID = userID;
        this.fromId = fromId;
    }

    public String getJobName() {
        return jobName;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
}
