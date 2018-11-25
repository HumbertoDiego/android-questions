package com.humberto.concursoengine;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by humberto on 05/05/17.
 */

public class PutAsyncTask extends AsyncTask<Void, Void, String> {

    private final String Rest_URL;

    //humberto-xingu13@live.com/102/xp33417401.json
    // Usar o construtor é uma forma dos parametros entrarem nessa subclasse
    PutAsyncTask(String url) {
        this.Rest_URL =url;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return syncXP(Rest_URL);
        } catch (IOException e) {
            return "Unable to retrieve data. URL may be invalid.";
        }
    }

    // Entra no conexão http em uma REst API para receber dados
    private String syncXP(String myurl) throws IOException {
        InputStream is = null;
        int length = 5000; // tamanho do texto q se espera receber

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("PUT");
        int response = conn.getResponseCode();
        Log.d("TA", "The response is: " + response);
        is = conn.getInputStream();

        // Convert the InputStream into a string
        String contentAsString = convertInputStreamToString(is, length);
        return contentAsString;
    }

    // Convert the InputStream into a string
    public String convertInputStreamToString(InputStream stream, int length) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }
}
