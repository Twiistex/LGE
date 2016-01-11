package com.lumi_dos.lge;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Colin on 03/01/2015.
 */
public class GetTotalNumberOfSlidesTask extends AsyncTask<String, Void, int[]> {

    protected int[] doInBackground(String... Url) {
        URL url = null;
        String inputLine = "100";
        try {
            url = new URL(Url[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            assert url != null;
            in = new BufferedReader(
                    new InputStreamReader(
                            url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
            in = null;
        }

        try {
            if(in != null) inputLine = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int outcome[] = new int[1];

        outcome[0] = Integer.valueOf(inputLine);

        return outcome;
    }

    protected void onPreExecute() {}

    protected void onPostExecute(int[] result) {
        IntranetFragment.totalNumberOfSlides = result[0];
    }
}
