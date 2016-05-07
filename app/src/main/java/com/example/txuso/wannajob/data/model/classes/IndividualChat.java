package com.example.txuso.wannajob.data.model.classes;

/**
 * Created by Txuso on 24/02/16.
 */
public class IndividualChat extends Chat {

    private String receptorID;
    private String receptor;
    private boolean position;

    public IndividualChat (){
        super();
        this.receptorID = "";
        this.receptor = "";
        this.position = false;

    }
    public IndividualChat(String message, String author, String receptor, String authorID, String receptorID, boolean position, String date) {
        super(message,author, authorID, date);
        this.receptorID = receptorID;
        this.receptor = receptor;
        this.position = position;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getReceptorID() {
        return receptorID;
    }

    public void setReceptorID(String receptorID) {
        this.receptorID = receptorID;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }

    public boolean getPosition(){
        return this.position;
    }

}