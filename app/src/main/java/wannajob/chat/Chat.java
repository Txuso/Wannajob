package wannajob.chat;

/**
 * @author greg
 * @since 6/21/13
 */
public class Chat {

    private String message;
    private String author;
    private String authorID;
    private String date;

    public Chat(){
        this.message = "";
        this.author = "";
        this.authorID = "";
        this.date = "";
    }

    public Chat(String message, String author, String authorID, String date) {
        this.message = message;
        this.author = author;
        this.authorID = authorID;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}