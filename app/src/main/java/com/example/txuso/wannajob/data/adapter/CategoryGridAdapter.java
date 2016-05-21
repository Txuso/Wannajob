package com.example.txuso.wannajob.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.txuso.wannajob.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Txuso on 21/05/16.
 */
public class CategoryGridAdapter extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;

    public CategoryGridAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        mItems.add(new Item("Manitas, Reparaciones y Obras",       R.drawable.electricista));
        mItems.add(new Item("Transportes/envíos",   R.drawable.paquetes));
        mItems.add(new Item("Tecnología", R.drawable.taking_out_the_trash));
        mItems.add(new Item("Clases",      R.drawable.clases));
        mItems.add(new Item("Servicisos Domésticos",     R.drawable.fregar_platos));
        mItems.add(new Item("Cocina",      R.drawable.limpieza));
        mItems.add(new Item("Ayuda en General",      R.drawable.hacer_cola));
        mItems.add(new Item("Otros",      R.drawable.paseo_perro));
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).drawableId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_category_item, viewGroup, false);
            v.setTag(R.id.grid_category_item_image, v.findViewById(R.id.grid_category_item_image));
            v.setTag(R.id.grid_category_text, v.findViewById(R.id.grid_category_text));
        }

        picture = (ImageView) v.getTag(R.id.grid_category_item_image);
        name = (TextView) v.getTag(R.id.grid_category_text);

        Item item = getItem(i);

        picture.setImageResource(item.drawableId);
        name.setText(item.name);

        return v;
    }


    private static class Item {
        public final String name;
        public final int drawableId;

        Item(String name, int drawableId) {
            this.name = name;
            this.drawableId = drawableId;
        }
    }
}
