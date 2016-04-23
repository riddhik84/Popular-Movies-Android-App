package com.myapps.rk.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.myapps.rk.popularmovies.R;

/**
 * Created by RKs on 4/15/2016.
 */
public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
        return sortOrder;
    }

    public static boolean isNetworkConnected(Context context){
        boolean wifiConnection = false;
        boolean dataConnection = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    wifiConnection = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    dataConnection = true;
        }
        return wifiConnection || dataConnection;
    }
}
