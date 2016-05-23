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
    List<Item> mItems;
    private final LayoutInflater mInflater;

    public CategoryGridAdapter(Context context, ArrayList<Item> mItems) {
        this.mItems = mItems;
        mInflater = LayoutInflater.from(context);

        mItems.add(new Item("Manitas, Reparaciones y Obras",       R.drawable.electricista, 0));
        mItems.add(new Item("Transportes/envíos",   R.drawable.paquetes, 1));
        mItems.add(new Item("Tecnología", R.drawable.taking_out_the_trash, 2));
        mItems.add(new Item("Clases",      R.drawable.clases, 3));
        mItems.add(new Item("Servicisos Domésticos",     R.drawable.fregar_platos, 4));
        mItems.add(new Item("Cocina",      R.drawable.limpieza, 5));
        mItems.add(new Item("Ayuda en General",      R.drawable.hacer_cola, 6));
        mItems.add(new Item("Otros",      R.drawable.paseo_perro, 7));
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


    public static class Item {
        public final String name;
        public final int drawableId;
        public final int categoryId;


        Item(String name, int drawableId, int categoryId) {
            this.name = name;
            this.drawableId = drawableId;
            this.categoryId = categoryId;
        }
    }
}
