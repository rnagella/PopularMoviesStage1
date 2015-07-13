package com.blogspot.nagellaranjith.android.popularmoviesstage1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
        Log.e("fragmentIntent", intent.toString());

        if (intent!=null&& intent.hasExtra(Intent.EXTRA_TEXT)){
            String msg = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.e("msg", msg);
        }

        return rootView;
    }
}
