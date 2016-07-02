package com.example.txuso.wannajob.data.model.classes;

import android.media.Image;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Txuso on 01/02/16.
 */
public class WannajobBidUser {

    private String name;
    private String image;
    private String description;
    private Double rating;
    private long bidNumber;
    private String userId;
    private String jobId;

    public WannajobBidUser(String name, String description, String image, Double rating, long bidNumber, String userId, String jobId) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.rating = rating;
        this.bidNumber = bidNumber;
        this.userId = userId;
        this.jobId = jobId;
    }

    public void setImages(ArrayList<Image> images) {
        this.image = images.get(0).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return this.image;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public long getBidNumber() {
        return bidNumber;
    }

    public void setBidNumber(long bidNumber) {
        this.bidNumber = bidNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}

