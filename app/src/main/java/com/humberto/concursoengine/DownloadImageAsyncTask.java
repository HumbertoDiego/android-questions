package com.humberto.concursoengine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by humberto on 05/05/17.
 */

public class DownloadImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    String urldisplay = "http://humbertoalves.pythonanywhere.com/static/images/stylish.jpg";

    public DownloadImageAsyncTask() {
    }

    protected Bitmap doInBackground(Void... urls) {

        Bitmap bit2 = null;
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bit2 = BitmapFactory.decodeStream(in);
            mIcon11= Bitmap.createScaledBitmap(bit2,132,132,false);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {

    }

}