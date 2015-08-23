package com.blogspot.nagellaranjith.android.popularmoviesstage1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by conrnagella on 8/18/15.
 */
public class MovieDataParcable implements Parcelable {

    public final String mOriginalTitle, mOverview, mReleaseDate, mPopularity, mVoteAverage ,mPosterPath;

    public int describeContents() {
        return 0;
    }

    public MovieDataParcable(String mOriginalTitle, String mOverview, String mReleaseDate,
                             String mPopularity, String mVoteAverage, String mPosterPath) {
        this.mOriginalTitle = mOriginalTitle;
        this.mOverview = mOverview;
        this.mReleaseDate = mReleaseDate;
        this.mPopularity = mPopularity;
        this.mVoteAverage = mVoteAverage;
        this.mPosterPath = mPosterPath;
    }

    private MovieDataParcable(Parcel in) {
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mPopularity = in.readString();
        mVoteAverage = in.readString();
        mPosterPath = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mOriginalTitle);
        out.writeString(mOverview);
        out.writeString(mReleaseDate);
        out.writeString(mPopularity);
        out.writeString(mVoteAverage);
        out.writeString(mPosterPath);
    }

    public static final Parcelable.Creator<MovieDataParcable> CREATOR
            = new Parcelable.Creator<MovieDataParcable>() {
        public MovieDataParcable createFromParcel(Parcel in) {
            return new MovieDataParcable(in);
        }

        public MovieDataParcable[] newArray(int size) {
            return new MovieDataParcable[size];
        }
    };
}

