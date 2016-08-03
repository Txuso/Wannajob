package com.example.txuso.wannajob.data.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.activities.DiscoveryPreferencesActivity;
import com.example.txuso.wannajob.activities.ShowJobActivity;
import com.example.txuso.wannajob.data.model.classes.Bid;
import com.example.txuso.wannajob.data.model.classes.WannajobBidUser;
import com.example.txuso.wannajob.data.model.classes.WannajobUser;
import com.example.txuso.wannajob.misc.things.DialogUtils;
import com.example.txuso.wannajob.misc.things.UserManager;
import com.firebase.client.Firebase;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

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

                    final Dialog myDialog = new Dialog(context);
                    myDialog.getWindow();
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    myDialog.setContentView(R.layout.confirm_wannajober_dialog);
                    final TextInputLayout userNumber = (TextInputLayout)myDialog.findViewById(R.id.confirm_wannajober_dialog_number);

                    myDialog.findViewById(R.id.bid_dialog_bid_text).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!userNumber.getEditText().getText().toString().equals("") &&
                                    isValidPhoneNumber(userNumber.getEditText().getText().toString())) {
                                mFirebaseRef.child("wannaJobs").child(jobId).child("selectedUserNumber").setValue(userNumber.getEditText().getText().toString());
                                mFirebaseRef.child("wannaJobs").child(jobId).child("selectedUserID").setValue(userId);
                                mFirebaseRef.child("wannajobUsers").child(userId).child("newBidMessage").setValue(UserManager.getUserName(context) + "^" + UserManager.getUserId(context) + "^" + jobId);
                                myDialog.dismiss();
                                Toast.makeText(context,
                                        "Contacto enviado. Tu Wannajober se pondrá en contacto en seguida. No te olvides de evaluar su servicio!!", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(context,
                                        "Please, introduce a valid phone number", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    myDialog.show();
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


    /**
     * Validation of Phone Number
     */
    public final static boolean isValidPhoneNumber(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            if (target.length() < 6 || target.length() > 13) {
                return false;
            } else {
                return android.util.Patterns.PHONE.matcher(target).matches();
            }
        }
    }
}

