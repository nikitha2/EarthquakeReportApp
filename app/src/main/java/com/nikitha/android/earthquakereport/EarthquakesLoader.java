package com.nikitha.android.earthquakereport;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

class EarthquakesLoader extends AsyncTaskLoader<ArrayList<ListObjectsClass>> {
    Bundle args;
    String uri;
    public static final String LOG_TAG = MainActivity.class.getName();

    public EarthquakesLoader(Context context, Bundle args) {
            super(context);
            this.args=args;
            }

    public EarthquakesLoader(MainActivity context, String toString) {
        super(context);
        uri=toString;
    }
//"https://earthquake.usgs.gov/fdsnws/event/1/query"?format=geojson&starttime=2012-01-01&endtime=2012-12-01&minmagnitude=6";
    @Nullable
    @Override
    public ArrayList<ListObjectsClass> loadInBackground() {
        ArrayList<ListObjectsClass> resultFromHttpCall= new ArrayList<>();
      //  String stringUrl= args.getString("url");
        URL url=null;

            try {
                if(uri!=null) {
                    url=createURL(uri); }
                else{
                    url=null; } }
            catch (MalformedURLException e) {
                e.printStackTrace(); }

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }
        resultFromHttpCall = extractFeatureFromJson(jsonResponse);
        return resultFromHttpCall;
    }

    public URL createURL(String stringUrl) throws MalformedURLException {
      URL url=null;
        try{
          url = new URL(stringUrl);
        return url;
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
        return null;
        }
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            int responseStatus = urlConnection.getResponseCode();
            Log.v("responseStatus= ", Integer.toString(responseStatus));

            if(responseStatus==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                jsonResponse="";
            }
        } catch (IOException e) {
            throw new IOException("invalid URL? - exception is "+e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link ArrayList<ListObjectsClass>} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    private ArrayList<ListObjectsClass> extractFeatureFromJson(String earthquakeJSON) {
        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray featureArray = baseJsonResponse.getJSONArray("features");
            ArrayList<ListObjectsClass> arrayList=new ArrayList<ListObjectsClass>();
            JSONObject firstFeature;JSONObject properties;

            // If there are results in the features array
            for (int i=0;i< featureArray.length() ;i++) {
                // Extract out the first feature (which is an earthquake)
                firstFeature = featureArray.getJSONObject(i);
                properties = firstFeature.getJSONObject("properties");

                // Extract out the title, time, and tsunami values
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                String date = properties.getString("time");
                String dateLong= getDateString(Long. parseLong(date));
                String url = properties.getString("url");

                arrayList.add(new ListObjectsClass(mag, place, dateLong,url));
                // Create a new {@link Event} object ArrayList<ListObjectsClass>
            }
            return arrayList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }
    private String getDateString(long timeInMilliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy \n HH:mm aa");
        return formatter.format(timeInMilliseconds);
    }

}
