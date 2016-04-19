package com.flicktip.flicktip.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cinthia on 06/06/2016.
 */
public class Functions {

    public static NetworkInfo checkNetworkConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    public static HttpURLConnection createConnection(StringBuilder stringBuilder) throws Exception {

        URL url = new URL(stringBuilder.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000 /* milliseconds */);
        connection.setConnectTimeout(15000 /* milliseconds */);
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.connect();

        return connection;
    }

    public static String stringify(InputStream stream) throws IOException {

        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        return bufferedReader.readLine();
    }

}
