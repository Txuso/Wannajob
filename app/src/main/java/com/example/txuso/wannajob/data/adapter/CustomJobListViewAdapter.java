package com.example.txuso.wannajob.data.adapter;

/**
 * Created by Txuso on 29/02/16.
 */
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.txuso.wannajob.R;
import com.example.txuso.wannajob.data.model.classes.WannajobEncounterItem;

/**
 * Created by josurubio on 08/04/15.
 * Custom adapter when listing found users
 */
public class CustomJobListViewAdapter extends ArrayAdapter<WannajobEncounterItem> {

    Context context;

    public CustomJobListViewAdapter(Context context, int resourceId,
                                       List<WannajobEncounterItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        CardView cv;
        TextView messageName;
        TextView messageDate;
        ImageView messagePhoto;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        WannajobEncounterItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.cv = (CardView)convertView.findViewById(R.id.cv2);
            holder.messageName = (TextView)convertView.findViewById(R.id.message_name);
            holder.messageDate = (TextView)convertView.findViewById(R.id.message_created_date);
            holder.messagePhoto = (ImageView)convertView.findViewById(R.id.message_photo);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.messageName.setText(rowItem.getReceptorName());
        holder.messageDate.setText(rowItem.getDate());
        holder.messagePhoto.setImageDrawable(rowItem.getImageId());
        //holder.imageView.setBackground(rowItem.getImageId());

        return convertView;
    }

}