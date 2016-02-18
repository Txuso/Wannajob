package wannajob.classes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.txuso.wannajob.JobListItem;
import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.ShowJob;

import java.util.List;

/**
 * Created by Txuso on 09/02/16.
 */
public class RVUserAdapter extends RecyclerView.Adapter<RVUserAdapter.JobViewHolder> {


    List<JobListItem> jobs;
    OnItemClickListener mItemClickListener;

    public RVUserAdapter(List<JobListItem> jobs){

        this.jobs = jobs;
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
        JobViewHolder pvh = new JobViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        holder.jobName.setText(jobs.get(position).getName());
        holder.jobSalary.setText(jobs.get(position).getSalary() + " €");
        //holder.jobPhoto.setImageResource(jobs.get(position).photoId);
        holder.jobPhoto.setBackground(jobs.get(position).getImageId());

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void clearContent() {
        this.jobs.clear();
        int size = getItemCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.jobs.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView jobName;
        TextView jobSalary;
        ImageView jobPhoto;

        JobViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            jobName = (TextView)itemView.findViewById(R.id.job_name);
            jobSalary = (TextView)itemView.findViewById(R.id.job_salary);
            jobPhoto = (ImageView)itemView.findViewById(R.id.job_photo);
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
         void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}
