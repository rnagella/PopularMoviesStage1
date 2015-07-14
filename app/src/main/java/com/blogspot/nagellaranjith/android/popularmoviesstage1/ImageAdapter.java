package com.blogspot.nagellaranjith.android.popularmoviesstage1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by conrnagella on 7/11/15.
 */
public class ImageAdapter extends BaseAdapter
{
    final private String[] imageIDs;
    final private Context context;

    public ImageAdapter(Context c, String[] i)
    {
        context = c;
        imageIDs = i;
    }

    //---returns the number of images---
    public int getCount() {
        return imageIDs.length;
    }

    //---returns the ID of an item---
    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //---returns an ImageView view---
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
//            imageView.setLayoutParams(new GridView.LayoutParams(185, 275));
            imageView.setAdjustViewBounds(true);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }

        String url = imageIDs[position];
        Picasso.with(context).load(url)
                .placeholder(R.drawable.noimage)
//                .error(R.drawable.errorimage)
                .into(imageView);
        return imageView;
    }
}

