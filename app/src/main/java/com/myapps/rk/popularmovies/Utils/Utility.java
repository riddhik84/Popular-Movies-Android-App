package com.myapps.rk.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.myapps.rk.popularmovies.R;
import com.myapps.rk.popularmovies.sync.MoviesSyncAdapter;

/**
 * Created by RKs on 4/15/2016.
 */
public class Utility {

    public static String getPreferredSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
        return sortOrder;
    }

    public static String networkType(Context context) {
        boolean wifiConnection = false;
        boolean dataConnection = false;
        String networkType = "NO_NETWORK";

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
        //Network[] networks = cm.getAllNetworks();

        for (NetworkInfo ni : networkInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    wifiConnection = true;
                    networkType = "WIFI";
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
                    dataConnection = true;
                    networkType = "MOBILE";
                }
        }
       // return wifiConnection || dataConnection;
        return networkType;
    }

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @SuppressWarnings("ResourceType")
    static public @MoviesSyncAdapter.ServerStatus
    int getServerStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_server_status_key), MoviesSyncAdapter.STATUS_UNKNOWN);
    }
}
