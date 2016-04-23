package com.myapps.rk.popularmovies.asynctask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by RKs on 4/13/2016.
 */
public class HttpRequestResponse {

    public OkHttpClient client = new OkHttpClient();

    public String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string().toString();  //user string() to get response
    }
}
