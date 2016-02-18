package com.example.txuso.wannajob;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Txuso on 17/02/16.
 */
public class JobListItem{

    private String name;
    private int salary;
    private String jobID;
    private Drawable imageId;

    public JobListItem (String jobID, String name, int salary, Drawable imageId) {
        this.name = name;
        this.salary = salary;
        this.jobID = jobID;
        this.imageId = imageId;
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
}
