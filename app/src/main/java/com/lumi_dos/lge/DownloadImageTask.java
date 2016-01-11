package com.lumi_dos.lge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Colin on 27/12/2015.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    protected Bitmap doInBackground(String... url ) {
        return loadImageFromNetwork(url[0]);
    }

    private Bitmap loadImageFromNetwork(String url) {
        try {
            return BitmapFactory
                    .decodeStream((InputStream) new URL(url)
                            .getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPreExecute() { IntranetFragment.progressBar.setVisibility(View.VISIBLE); }

    protected void onPostExecute(Bitmap result) {
        IntranetFragment.imageView.setImageBitmap(result);
        IntranetFragment.progressBar.setVisibility(View.INVISIBLE);
    }
}
