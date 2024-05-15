package com.example.bouqetflowershop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CatalogAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInFlater;
    ArrayList<BouqetCard> objects;

    CatalogAdapter(Context context, ArrayList<BouqetCard> products) {
        ctx = context;
        objects = products;
        lInFlater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void removeItem(BouqetCard product) {
        objects.remove(product);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInFlater.inflate(R.layout.item_catalog, parent, false);
        }
        BouqetCard p = getProduct(position);
        ((TextView) view.findViewById(R.id.textViewName)).setText(p.name);
        ((TextView) view.findViewById(R.id.textViewPrice)).setText(p.price + "₽");
        ((ImageView) view.findViewById(R.id.imageViewImage)).setImageResource(p.image);
        return view;
    }

    BouqetCard getProduct(int position) {
        return ((BouqetCard) getItem(position));
    }

    ArrayList<BouqetCard> getBox() {
        ArrayList<BouqetCard> box = new ArrayList<>();
        for (BouqetCard p : objects) {
            if (p.box)
                box.add(p);
        }
        return box;
    }
}
