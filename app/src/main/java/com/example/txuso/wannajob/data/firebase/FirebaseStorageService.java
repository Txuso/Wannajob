package com.example.txuso.wannajob.data.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Created by Txuso on 10/06/16.
 */
public class FirebaseStorageService {
    StorageReference storageRef;
    Firebase mFirebaseRef;
    String imageURL = "";


    FirebaseStorage storage = FirebaseStorage.getInstance();

    public FirebaseStorageService() {
        storageRef =  storage.getReferenceFromUrl("gs://project-6871569626797643888.appspot.com");
        mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/wannajobUsers");

    }

    public String  uploadUserOrTaskImage(final String userID, Bitmap bitmap, final Context context) {
        StorageReference mountainImagesRef = storageRef.child("images/"+userID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data2 = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data2);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl.toString() != null) {
                    imageURL = downloadUrl.toString();
                    UserManager.setUserPhoto(context, imageURL);
                    mFirebaseRef.child(userID).child("image").setValue(UserManager.getUserPhoto(context));
                }
            }
        });
        return imageURL;
    }

}
