package wannajob.classes;

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

    public Job (String name, String description, int salary, String category, String creatorID, String createdDate){
        this.name = name;
        this.description = description;
        this.salary = salary;
        this.category = category;
        this.creatorID = creatorID;
        this.createdDate = createdDate;
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

}
