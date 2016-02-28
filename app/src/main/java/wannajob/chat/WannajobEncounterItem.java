package wannajob.chat;

/**
 * Created by Txuso on 24/02/16.
 */
import android.graphics.drawable.Drawable;

/**
 * Created by josurubio on 15/04/15.
 */
public class WannajobEncounterItem {

    private String encounterID;
    private String author;
    private String receptor;
    private String date;
    private String receptorName;
    private Drawable imageId;


    public WannajobEncounterItem(String author, String receptor, String receptorName, String date, Drawable imageId, String encounterID) {
        this.author = author;
        this.receptor = receptor;
        this.receptorName = receptorName;
        this.date = date;
        this.imageId = imageId;
        this.encounterID = encounterID;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public void setReceptorName(String receptorName) {
        this.receptorName = receptorName;
    }

    public String getReceptorName() {
        return receptorName;
    }

    public void setImageId(Drawable imageId) {
        this.imageId = imageId;
    }

    public Drawable getImageId() {
        return imageId;
    }

    public String getEncounterID() {
        return encounterID;
    }

    public void setEncounterID(String encounterID) {
        this.encounterID = encounterID;
    }
}