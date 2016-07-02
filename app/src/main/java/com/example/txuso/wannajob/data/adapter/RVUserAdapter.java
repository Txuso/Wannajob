package com.example.txuso.wannajob.data.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.example.txuso.wannajob.data.model.classes.WannajobUser;
import com.example.txuso.wannajob.misc.things.DialogUtils;

import java.util.List;

/**
 * Created by Txuso on 09/02/16.
 */
public class RVUserAdapter extends RecyclerView.Adapter<RVUserAdapter.UserViewHolder> {


    List<WannajobUser> users;
    OnItemClickListener mItemClickListener;
    Context context;

    public RVUserAdapter(List<WannajobUser> users, Context context){
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
        TextView userDescription;

        UserViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.user_item_name);
            userRating = (RatingBar) itemView.findViewById(R.id.user_item_rating);
            jobPhoto = (ImageView)itemView.findViewById(R.id.user_item_photo);
            userDescription = (TextView) itemView.findViewById(R.id.user_item_description);
            acceptButton = (TextView) itemView.findViewById(R.id.user_item_accept_button);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtils.buildAlertDialog(context)
                            .setMessage("Do you want to accept " + userName.getText() + " to do your job?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
