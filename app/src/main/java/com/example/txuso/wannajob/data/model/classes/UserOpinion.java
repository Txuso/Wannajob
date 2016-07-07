package com.example.txuso.wannajob.data.model.classes;

/**
 * Created by Txuso on 17/02/16.
 */
public class UserOpinion {

    private String fromUser;
    private int stars;
    private String jobName;
    private String imageUrl;
    private String opinion;
    private String fromId;
    private String toID;

    public UserOpinion(String fromUser, String jobName, String opinion, int stars, String imageUrl, String toID, String fromId) {
        this.fromUser = fromUser;
        this.imageUrl = imageUrl;
        this.jobName = jobName;
        this.opinion = opinion;
        this.stars = stars;
        this.toID = toID;
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
        return fromUser;
    }

    public void setName(String fromUser) {
        this.fromUser = fromUser;
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
        return toID;
    }

    public void setUserID(String toID) {
        this.toID = toID;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
}
