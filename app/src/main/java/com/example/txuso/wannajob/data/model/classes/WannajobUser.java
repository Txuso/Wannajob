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
public class WannajobUser {

    private String name;
    private String age;
    private String image;
    private String description;
    private double longitude;
    private double latitude;
    private int distance;
    private String registeredDate;
    private Double rating;
    public Map<String, Boolean> likes = new HashMap<>();


    public WannajobUser(String name, String age, String image) {
        this.name = name;
        this.image = image;
        this.latitude = 0;
        this.longitude = 0;
        this.age = age;
        this.distance = 50;
        this.description = "";
        this.registeredDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        rating = 0.0;
    }

    public WannajobUser(String name, String description, String image, Double rating) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.rating = rating;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getAge() {
        return age;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}

