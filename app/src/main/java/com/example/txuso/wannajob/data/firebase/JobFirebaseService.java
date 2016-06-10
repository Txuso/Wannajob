package com.example.txuso.wannajob.data.firebase;

import com.example.txuso.wannajob.data.model.classes.Job;
import com.firebase.client.Firebase;

/**
 * Created by Txuso on 10/06/16.
 */
public class JobFirebaseService {

    Firebase mFirebaseRef;
    Firebase newTandRef;

    public JobFirebaseService () {
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannaJobs/");
    }

    public String getNewJobKey () {
        newTandRef = mFirebaseRef.push();
        return newTandRef.getKey();
    }

    public void createJob(String jobID, Job job) {
        mFirebaseRef.child(jobID).setValue(job);
    }
}
