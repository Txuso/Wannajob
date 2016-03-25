package wannajob.classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Txuso on 03/02/16.
 */
public class Job {

    private String name;
    private String description;
    private int salary;
    private String category;
    private String creatorID;
    private String createdDate;
    private String jobDuration;
    private String jobImage;
    private double latitude;
    private double longitude;
    private ArrayList<String> wannajobers;

    public Job (String name, String description, int salary, String category, String creatorID, String createdDate, String jobImage, String jobDuration, double latitude, double longitude){
        this.name = name;
        this.description = description;
        this.salary = salary;
        this.category = category;
        this.creatorID = creatorID;
        this.createdDate = createdDate;
        this.jobImage = jobImage;
        this.jobDuration = jobDuration;
        this.latitude = latitude;
        this.longitude = longitude;
        wannajobers = new ArrayList<>();
    }

    public Job (String name, int salary, String jobImage){
        this.name = name;
        this.description = description;
        this.salary = salary;
        this.category = category;
        this.creatorID = creatorID;
        this.createdDate = createdDate;
        this.jobImage = jobImage;
    }

    public Job () {

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getJobDuration() {
        return jobDuration;
    }

    public void setJobDuration(String jobDuration) {
        this.jobDuration = jobDuration;
    }

    public String getJobImage() {
        return jobImage;
    }

    public void setJobImage(String jobImage) {
        this.jobImage = jobImage;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public int getSalary() {
        return salary;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public ArrayList<String> getWannajobers() {
        return wannajobers;
    }

    public void setWannajobers(ArrayList<String> wannajobers) {
        this.wannajobers = wannajobers;
    }
}
