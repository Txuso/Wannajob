package wannajob.classes;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.txuso.wannajob.JobListItem;
import com.example.txuso.wannajob.R;

import java.util.List;

import wannajob.chat.Chat;
import wannajob.chat.WannajobEncounter;
import wannajob.chat.WannajobEncounterItem;

/**
 * Created by Txuso on 09/02/16.
 */
public class RVMessageAdapter extends RecyclerView.Adapter<RVMessageAdapter.MessageViewHolder> {


    List<WannajobEncounterItem> messages;
    OnItemClickListener mItemClickListener;

    public RVMessageAdapter(List<WannajobEncounterItem> messages){
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        MessageViewHolder pvh = new MessageViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.messageName.setText(messages.get(position).getAuthor());
        holder.messageDate.setText(messages.get(position).getDate());
        holder.messagePhoto.setBackground(messages.get(position).getImageId());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void clearContent() {
        this.messages.clear();
        int size = getItemCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.messages.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView messageName;
        TextView messageDate;
        ImageView messagePhoto;

        MessageViewHolder (View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv2);
            messageName = (TextView)itemView.findViewById(R.id.message_name);
            messageDate = (TextView)itemView.findViewById(R.id.message_created_date);
            messagePhoto = (ImageView)itemView.findViewById(R.id.message_photo);
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
