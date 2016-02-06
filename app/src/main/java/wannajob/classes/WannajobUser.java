package wannajob.classes;

import android.media.Image;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Txuso on 01/02/16.
 */
public class WannajobUser {

    private String name;
    private String age;
    private String image;
    private String longitude;
    private String latitude;
    private int distance;
    private String registeredDate;
    private int publishedJobs;

    public WannajobUser(String name, String age, String image) {
        this.name = name;
        this.image = image;
        this.age = age;
        this.latitude = "0";
        this.longitude = "0";
        this.distance = 50;
        this.registeredDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        this.publishedJobs = 0;
    }

    public int getPublishedJobs() {
        return publishedJobs;
    }

    public void setPublishedJobs(int publishedJobs) {
        this.publishedJobs = publishedJobs;
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

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }


}
