package com.example.txuso.wannajob.data.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.JobListItem;
import com.example.txuso.wannajob.data.model.classes.UserOpinionListItem;
import com.example.txuso.wannajob.misc.RoundedImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Txuso on 09/02/16.
 */
public class RVUserOpinionAdapter extends RecyclerView.Adapter<RVUserOpinionAdapter.JobViewHolder> {


    List<UserOpinionListItem> userOpinionListItems;
    OnItemClickListener mItemClickListener;
    Context context;

    public RVUserOpinionAdapter(List<UserOpinionListItem> userOpinionListItems, Context context){
        this.userOpinionListItems = userOpinionListItems;
        this.context = context;
    }


    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_opinion_item, parent, false);
        JobViewHolder pvh = new JobViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(final JobViewHolder holder, int position) {
        holder.jobName.setText(userOpinionListItems.get(position).getJobName());
        holder.userName.setText(userOpinionListItems.get(position).getName());
        holder.opinionText.setText(userOpinionListItems.get(position).getOpinion());
        holder.jobName.setText(userOpinionListItems.get(position).getJobName());
        holder.stars.setRating(userOpinionListItems.get(position).getStars());
        Glide.with(context).load(userOpinionListItems.get(position).getImageUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.jobPhoto) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.jobPhoto.setImageDrawable(circularBitmapDrawable);
            }
        });
        //holder.jobPhoto.setImageResource(jobs.get(position).photoId);
        //Picasso.with(context).load(jobs.get(position).getImageUrl()).fit().centerCrop().placeholder(R.drawable.person_placeholder).into(holder.jobPhoto);
       // holder.jobPhoto.setBackground(jobs.get(position).getImageId());

    }

    @Override
    public int getItemCount() {
        return userOpinionListItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        RatingBar stars;
        TextView userName;
        TextView jobName;
        TextView opinionText;
        ImageView jobPhoto;

        JobViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.user_opinion_cv);
            jobName = (TextView)itemView.findViewById(R.id.user_opinion_job_name);
            stars = (RatingBar) itemView.findViewById(R.id.user_opinion_stars);
            jobPhoto = (ImageView)itemView.findViewById(R.id.user_opinion_photo);
            opinionText = (TextView) itemView.findViewById(R.id.user_opinion_text);
            userName = (TextView) itemView.findViewById(R.id.user_opinion_name);
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
