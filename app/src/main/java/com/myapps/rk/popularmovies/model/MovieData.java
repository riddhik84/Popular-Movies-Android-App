package com.myapps.rk.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by RKs on 4/16/2016.
 */
public class MovieData implements Parcelable {

    public String id;
    public String poster_path;
    public String overview;
    public String release_date;
    public String movie_id;
    public String original_title;
    public String vote_average;
    public String sort_order;

    public final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    public MovieData (String _id, String _poster_path, String _overview, String _release_date, String _movie_id,
                      String _original_title, String _vote_average, String _sort_order){
        id = _id;
        poster_path = _poster_path;
        overview = _overview;
        release_date = _release_date;
        movie_id = _movie_id;
        original_title = _original_title;
        vote_average = _vote_average;
        sort_order = _sort_order;
    }

    private MovieData (Parcel in){
        id = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        movie_id = in.readString();
        original_title = in.readString();
        vote_average = in.readString();
        sort_order = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(id);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(movie_id);
        parcel.writeString(original_title);
        parcel.writeString(vote_average);
        parcel.writeString(sort_order);
    }
}
