package com.example.txuso.wannajob.data.model.classes;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Txuso on 17/02/16.
 */
public class JobListItem{

    private String name;
    private int salary;
    private String jobID;
    private String jobDescription;
    private Drawable imageId;
    private String creatorID;
    private double distance;

    public JobListItem (String jobID, String name, int salary, Drawable imageId, String creatorID, String jobDescription) {
        this.name = name;
        this.salary = salary;
        this.jobID = jobID;
        this.imageId = imageId;
        this.distance = 0;
        this.creatorID = creatorID;
        this.jobDescription = jobDescription;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Drawable getImageId() {
        return imageId;
    }

    public int getSalary() {
        return salary;
    }

    public String getJobID() {
        return jobID;
    }

    public String getName() {
        return name;
    }

    public void setImageId(Drawable imageId) {
        this.imageId = imageId;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
