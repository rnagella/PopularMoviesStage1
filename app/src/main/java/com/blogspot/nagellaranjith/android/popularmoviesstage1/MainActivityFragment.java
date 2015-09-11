package com.blogspot.nagellaranjith.android.popularmoviesstage1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Button;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */


public class MainActivityFragment extends Fragment {


    final private String Log_TAG = "PM_STAGE1";

    private GridView gridView;
    final private String[] imageIDs = {};

    private JSONArray jsonArray1;
    private ArrayList<MovieDataParcable> list;
    private String[] movieData = {};
    private String sort_order;


    public MainActivityFragment() {

    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void updateMoviePosters() {
            FetchMovieTask fetchMoviePosters = new FetchMovieTask();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sort_order = sharedPreferences.getString(getString(R.string.pref_sort_order), "popularity.desc");

            fetchMoviePosters.execute(sort_order);
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("key") != null) {
            list = savedInstanceState.getParcelableArrayList("key");

            if (list != null) {
                movieData = new String[list.size()];
                jsonArray1 = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    movieData[i] = list.get(i).mPosterPath;
                    try {

                        jsonArray1.put(new JSONObject()
                                .put("original_title", list.get(i).mOriginalTitle)
                                .put("overview", list.get(i).mOverview)
                                .put("release_date", list.get(i).mReleaseDate)
                                .put("popularity", list.get(i).mPopularity)
                                .put("vote_average", list.get(i).mVoteAverage)
                                .put("poster_path", list.get(i).mPosterPath));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                gridView.setAdapter(new ImageAdapter(getActivity(), movieData));
                // storing sort_order locally - so to update grid view if any changes to preferences.
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sort_order = sharedPreferences.getString(getString(R.string.pref_sort_order), "popularity.desc");
            }

        } else {
            updateMoviePosters();
        }

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

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//    }

    @Override
    public void onStart() {
        super.onStart();
        // if preference changed
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String shared_sort_preference = sharedPreferences.getString(getString(R.string.pref_sort_order), "popularity.desc");
        Log.i(Log_TAG, "sort_order" + sort_order);
        Log.i(Log_TAG, "shared_sort_prefe" + shared_sort_preference);
        if (!sort_order.equals(shared_sort_preference)) {
            updateMoviePosters();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
         if (!isOnline()) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.network_fragment);
//            dialog.setTitle("Network issue");
            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonTryAgain);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()) {
                        dialog.dismiss();
                        updateMoviePosters();
                    } else {
                        dialog.show();
                    }
                }
            });
            dialog.show();
        }
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saved jsonArray data
        list = new ArrayList<MovieDataParcable>();
        if (jsonArray1!= null) {
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject movie;
                try {
                    movie = jsonArray1.getJSONObject(i);

                    list.add(new MovieDataParcable(movie.getString("original_title"), movie.getString("overview"), movie.getString("release_date"),
                            movie.getString("popularity"), movie.getString("vote_average"), movie.getString("poster_path")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        outState.putParcelableArrayList("key", list);
        super.onSaveInstanceState(outState);
    }



    private class FetchMovieTask extends AsyncTask<String, Void, String[]> {
        private String[] getMovieData(String data) throws JSONException {
            final String tmdb_base_url = "http://image.tmdb.org/t/p/w342/";
            final String tmdb_results = "results";
            final String tmdb_original_title = "original_title";
            final String tmdb_overview = "overview";
            final String tmdb_release_date = "release_date";
            final String tmdb_popularity = "popularity";
            final String tmdb_vote_average = "vote_average";
            final String tmdb_poster_path = "poster_path";

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray(tmdb_results);


            movieData = new String[jsonArray.length()];
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


        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader br = null;
            String movieJsonStr;
//            final String API_KEY = R.string.api_key;

            if (isOnline()) {
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http");
                    builder.authority("api.themoviedb.org");
                    builder.appendPath("3");
                    builder.appendPath("discover");
                    builder.appendPath("movie");
                    builder.appendQueryParameter("sort_by", params[0]);
                    builder.appendQueryParameter("api_key", getString(R.string.api_key));

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
            } else {
                return null;
            }

            return null;
        }

        @Override
        public void onPostExecute(String[] result) {
            gridView.setAdapter(new ImageAdapter(getActivity(), result));
        }
    }
}
