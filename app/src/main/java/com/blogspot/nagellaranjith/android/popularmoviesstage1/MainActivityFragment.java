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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

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
    private Toast mAppToast;
    final String Log_TAG = "PM_STAGE1";
    private ArrayAdapter<String> adapter;
    GridView gridView;
    String[] imageIDs ={};
    String[] movieData;
//    String[] imageIDs =  {"http://image.tmdb.org/t/p/w185//kqjL17yufvn9OVLyXYpvtyrFfak.jpg",
//            "http://image.tmdb.org/t/p/w185//kqjL17yufvn9OVLyXYpvtyrFfak.jpg"};

    public MainActivityFragment() {
    }

    private void updateMoviePosters() {
        FetchMovieTask fetchMoviePosters = new FetchMovieTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = sharedPreferences.getString(getString(R.string.pref_sort_order), "popularity.desc");
//        Log.e("pref_sort_order_label", sharedPreferences.getString("pref_sort_order_values", "popularity.desc"));

                Log.e("sort_order_by", sort_order);
        //vote_average.desc
        fetchMoviePosters.execute(sort_order);
    }

    private String[] getMovieData(String data) throws JSONException {
        final String tmdb_base_url = "http://image.tmdb.org/t/p/w185/";
        final String tmdb_results = "results";
        final String tmdb_original_title = "original_title";
        final String tmdb_overview = "overview";
        final String tmdb_release_date = "release_date";
        final String tmdb_popularity = "popularity";
        final String tmdb_vote_average = "popularity";
        final String tmdb_poster_path = "poster_path";

        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray(tmdb_results);


        movieData = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movie = jsonArray.getJSONObject(i);

            String url = movie.getString(tmdb_poster_path);
            String original_title = "Juraci world";

//            if(url!=null) {
//                movieData.put("original_title", "original_title");

          movieData[i] = tmdb_base_url + url;
//            }
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
        Log.e("gridView", gridView.toString());

//        adapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_main, R.layout.poster_image_thumbnail, new ArrayList<>());


        ImageAdapter adapter1 = new ImageAdapter(getActivity(), imageIDs);


        gridView.setAdapter(new ImageAdapter(getActivity(), imageIDs));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                if (mAppToast != null) {
//                    mAppToast.cancel();
//                }
//                mAppToast.makeText(getActivity(), "" + position,
//                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, "hello_world");
                startActivity(intent);

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

//                URL url = new URL("http://api.themoviedb.org/3/discover/movie?" +
//                        "sort_by=popularity.desc&api_key=5ad1483b40fbedcac64c9ffcca680796");

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("api.themoviedb.org");
                builder.appendPath("3");
                builder.appendPath("discover");
                builder.appendPath("movie");
                builder.appendQueryParameter("sort_by", params[0]);
                builder.appendQueryParameter("api_key","5ad1483b40fbedcac64c9ffcca680796");

                Log.e("urlBuilder", builder.build().toString());

                URL url = new URL(builder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer sb = new StringBuffer();

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
