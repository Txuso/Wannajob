package com.example.txuso.wannajob.data.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.activities.DiscoveryPreferencesActivity;
import com.example.txuso.wannajob.data.model.classes.WannajobBidUser;
import com.example.txuso.wannajob.data.model.classes.WannajobUser;
import com.example.txuso.wannajob.misc.things.DialogUtils;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.Firebase;

import java.io.IOException;
import java.util.List;

/**
 * Created by Txuso on 09/02/16.
 */
public class RVUserAdapter extends RecyclerView.Adapter<RVUserAdapter.UserViewHolder> {


    List<WannajobBidUser> users;
    OnItemClickListener mItemClickListener;
    Context context;
    Firebase mFirebaseRef;

    public RVUserAdapter(List<WannajobBidUser> users, Context context){
        this.users = users;
        this.context = context;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        UserViewHolder pvh = new UserViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        holder.userName.setText(users.get(position).getName());
        holder.userRating.setRating((float) users.get(position).getRating());
        holder.userDescription.setText(users.get(position).getDescription());
        holder.userBid.setText(users.get(position).getBidNumber()+"€");
        holder.userId = users.get(position).getUserId();
        holder.jobId = users.get(position).getJobId();
        Glide.with(context).load(users.get(position).getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.jobPhoto) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.jobPhoto.setImageDrawable(circularBitmapDrawable);
            }
        });
        //Picasso.with(context).load(users.get(position).getImageUrl()).fit().centerCrop().placeholder(R.drawable.person_placeholder).into(holder.jobPhoto);
       // holder.jobPhoto.setBackground(users.get(position).getImageId());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView userName;
        RatingBar userRating;
        ImageView jobPhoto;
        TextView acceptButton;
        TextView userBid;
        TextView userDescription;
        String userId;
        String jobId;

        UserViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.user_item_name);
            userRating = (RatingBar) itemView.findViewById(R.id.user_item_rating);
            jobPhoto = (ImageView)itemView.findViewById(R.id.user_item_photo);
            userDescription = (TextView) itemView.findViewById(R.id.user_item_description);
            acceptButton = (TextView) itemView.findViewById(R.id.user_item_accept_button);
            userBid = (TextView) itemView.findViewById(R.id.user_item_bid);
            mFirebaseRef = new Firebase("https://wannajob.firebaseio.com/");

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.buildAlertDialog(context)
                            .setMessage(context.getString(R.string.user_adapter_dialog )
                                    + " " + userName.getText().toString()
                                    + " " + context.getString(R.string.user_adapter_dialog2))
                            .setCancelable(true)
                            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    mFirebaseRef.child("wannajobUsers").child(userId).child("newBidMessage").setValue(UserManager.getUserName(context) + "^" + UserManager.getUserId(context) + "^" + jobId);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }

        }
    }

    public interface OnItemClickListener {
         void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
