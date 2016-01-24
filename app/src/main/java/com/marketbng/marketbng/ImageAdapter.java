package com.marketbng.marketbng;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.net.PasswordAuthentication;
import java.util.ArrayList;

/**
 * Created by sdhond on 2016-01-24.
 */
public class ImageAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<Survey> imageUrls;

    public ImageAdapter(Context context, ArrayList<Survey> imageUrls) {
        super(context, R.layout.listview_item_image, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
        }

        Picasso
                .with(context)
                .load(imageUrls.get(position).getString("logo_url"))
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }

    @Override
    public int getCount(){
        return imageUrls.size();
    }
}