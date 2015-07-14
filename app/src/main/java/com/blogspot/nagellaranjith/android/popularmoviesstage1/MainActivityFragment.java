package com.blogspot.nagellaranjith.android.popularmoviesstage1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */


public class MainActivityFragment extends Fragment {

    final private String Log_TAG = "PM_STAGE1";

    private GridView gridView;
    final private String[] imageIDs = {};

    private final String API_KEY = "5ad1483b40fbedcac64c9ffcca680796";

    private JSONArray jsonArray1;


    public MainActivityFragment() {
    }

    private void updateMoviePosters() {
        FetchMovieTask fetchMoviePosters = new FetchMovieTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = sharedPreferences.getString(getString(R.string.pref_sort_order), "popularity.desc");

        fetchMoviePosters.execute(sort_order);
    }

    private String[] getMovieData(String data) throws JSONException {
        final String tmdb_base_url = "http://image.tmdb.org/t/p/w185/";
        final String tmdb_results = "results";
        final String tmdb_original_title = "original_title";
        final String tmdb_overview = "overview";
        final String tmdb_release_date = "release_date";
        final String tmdb_popularity = "popularity";
        final String tmdb_vote_average = "vote_average";
        final String tmdb_poster_path = "poster_path";

        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray(tmdb_results);


        String[] movieData = new String[jsonArray.length()];


        jsonArray1 = new JSONArray();


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movie = jsonArray.getJSONObject(i);

            String url = movie.getString(tmdb_poster_path);


            movieData[i] = tmdb_base_url + url;

            jsonArray1.put(new JSONObject()
                    .put("original_title", movie.getString(tmdb_original_title))
                    .put("overview", movie.getString(tmdb_overview))
                    .put("release_date", movie.getString(tmdb_release_date))
                    .put("popularity", movie.getString(tmdb_popularity))
                    .put("vote_average", movie.getString(tmdb_vote_average))
                    .put("poster_path", tmdb_base_url + movie.getString(tmdb_poster_path)));

        }
        return movieData;
    }

    @Override
    public void onStart() {

        super.onStart();
        updateMoviePosters();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);


//        ImageAdapter adapter1 = new ImageAdapter(getActivity(), imageIDs);


        gridView.setAdapter(new ImageAdapter(getActivity(), imageIDs));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                try {



                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, jsonArray1.getJSONObject(position).toString());
                    startActivity(intent);

                } catch (Exception e) {
                    Log.e("Log_TAG", "Error", e);

                }


            }
        });

        return rootView;
    }

    private class FetchMovieTask extends AsyncTask<String, Void, String[]> {


        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader br = null;
            String movieJsonStr;

            try {


                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("api.themoviedb.org");
                builder.appendPath("3");
                builder.appendPath("discover");
                builder.appendPath("movie");
                builder.appendQueryParameter("sort_by", params[0]);
                builder.appendQueryParameter("api_key", API_KEY);



                URL url = new URL(builder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();

                if (inputStream == null) {
                    // do nothing.
                    return null;
                }

                br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");

                }

                if (sb == null) {
                    return null;
                }


                movieJsonStr = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                        Log.e(Log_TAG, "Error closing stream", e);
                    }
                }

            }

            try {
                return getMovieData(movieJsonStr);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String[] result) {

            gridView.setAdapter(new ImageAdapter(getActivity(), result));
        }
    }


}
