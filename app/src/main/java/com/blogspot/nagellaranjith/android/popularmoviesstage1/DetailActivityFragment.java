package com.blogspot.nagellaranjith.android.popularmoviesstage1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String msg = intent.getStringExtra(Intent.EXTRA_TEXT);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(msg);

                ImageView imageView = (ImageView) rootView.findViewById(R.id.poster__image_view);
                Picasso.with(getActivity().getApplicationContext()).load(jsonObject.getString("poster_path"))
                        .placeholder(R.mipmap.noimage)
//                .error(R.drawable.errorimage)
                        .into(imageView);

                String overview = jsonObject.getString("overview");


                String original_title = TextUtils.isEmpty(jsonObject.getString("original_title")) ? "No overview available": jsonObject.getString("original_title");
                String release_date = TextUtils.isEmpty(jsonObject.getString("release_date")) ? "Unknow release date":jsonObject.getString("release_date");
                String vote_average = TextUtils.isEmpty(jsonObject.getString("vote_average")) ? "-/10" : (jsonObject.getString("vote_average") + "/10");


                ((TextView) rootView.findViewById(R.id.overview_text_view)).setText(overview);
                ((TextView) rootView.findViewById(R.id.original_title_text_view)).setText(original_title.isEmpty() ? "Unknown title" : original_title);
                ((TextView) rootView.findViewById(R.id.release_date_text_view)).setText(release_date);
                ((TextView) rootView.findViewById(R.id.rating_text_view)).setText(vote_average);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return rootView;
    }
}
