package com.example.txuso.wannajob.data.firebase;

import android.content.Context;
import android.widget.TextView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.Job;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Created by Txuso on 10/06/16.
 */
public class JobFirebaseService {

    Firebase mFirebaseRef;
    Firebase newTandRef;
    long bidNumber;
    long viewNumber;

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
    /*
    public void showJobUserInfo(Context context,
                                String jobID,
                                final android.support.design.widget.CollapsingToolbarLayout collapsingToolbarLayout,
                                final int color,
                                final TextView jobName,
                                final TextView jobDescription,
                                final TextView jobBids,
                                final TextView jobViews,
                                final TextView jobMoney) {

        mFirebaseRef.child(jobID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> job = (Map<String, Object>) dataSnapshot.getValue();
                collapsingToolbarLayout.setTitle(job.get("name").toString());

                // Bitmap pic = ImageManager.getResizedBitmap(ImageManager.decodeBase64(job.get("jobImage").toString()),100,100);

                //  Picasso.with(getApplicationContext()).load()
                // BitmapDrawable ob = new BitmapDrawable(getResources(), pic);

                if (job.get("bidNumber")  != null)
                    bidNumber = (long) job.get("bidNumber");
                else
                    bidNumber = 0;
                if (job.get("viewNumber") != null)
                    viewNumber = (long) job.get("viewNumber");
                else
                    viewNumber = 0;

                // Bitmap image =ImageManager.fromURLToBitmap(getApplicationContext(), job.get("jobImage").toString());
//                setPalette(image);

//                Picasso.with(getApplicationContext()).load(job.get("jobImage").toString()).fit().placeholder(R.drawable.photo_placeholder).into(jobImage);
                collapsingToolbarLayout.setStatusBarScrimColor(color);

                jobName.setText(job.get("name").toString());
                jobDescription.setText(job.get("description").toString());
                jobBids.setText(bidNumber + "");

                jobViews.setText(viewNumber + "");
                jobMoney.setText(job.get("salary").toString() + "€");
                setUpMapIfNeeded((double)job.get("latitude"), (double)job.get("longitude"));
                Picasso
                        .with(getApplicationContext())
                        .load(job.get("jobImage").toString())
                        .placeholder(R.drawable.photo_placeholder)
                        .fit()
                        .into(jobImage);

                //jobDuration.getEditText().setText(job.get("jobDuration").toString());
                //jobCategory.getEditText().setText(job.get("category").toString());
                mFirebaseRef.child("wannajobUsers").child(job.get("creatorID").toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Map<String, Object> user = (Map<String, Object>) dataSnapshot.getValue();
                        userName.setText(user.get("name").toString());
                        Picasso.with(getApplicationContext()).load(user.get("image").toString()).centerCrop().placeholder(R.drawable.photo_placeholder).fit().into(userPhoto);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    */
}
