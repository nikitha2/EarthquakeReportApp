package com.nikitha.android.earthquakereport;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class QueryUtilsAsyncTask extends AsyncTask<URL, Void, ArrayList<ListObjectsClass>> {
    Context contextActivity;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2012-01-01&endtime=2012-12-01&minmagnitude=6";

    private static final String SAMPLE_JSON_RESPONSE = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2012-01-01&endtime=2012-12-01&minmagnitude=6";
    ArrayList<ListObjectsClass> finalData;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtilsAsyncTask} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtilsAsyncTask (and an object instance of QueryUtilsAsyncTask is not needed).
     */
    public QueryUtilsAsyncTask() {
    }

    @Override
    protected ArrayList<ListObjectsClass> doInBackground(URL... urls) {
        // Create URL object
        URL url = createUrl(USGS_REQUEST_URL);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            new IOException(e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<ListObjectsClass> earthquake = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
        return earthquake;
    }

    @Override
    protected void onPostExecute(ArrayList<ListObjectsClass> listObjectsClasses) {
        super.onPostExecute(listObjectsClasses);
    }


    private ArrayList<ListObjectsClass> extractFeatureFromJson(String jsonResponse) {
        JSONObject featuresPos;
        JSONObject properties; double mag;String place;  int time;String url;
        JSONObject SAMPLE_JSON_RESPONSE_JSON;
        JSONArray features = null;
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<ListObjectsClass> earthquakes = new ArrayList<>();

        try{
            SAMPLE_JSON_RESPONSE_JSON=new JSONObject(jsonResponse);
            features = SAMPLE_JSON_RESPONSE_JSON.getJSONArray("features");

            // build up a list of Earthquake objects with the corresponding data.
            for(int i=0;i<features.length();i++) {
                featuresPos = features.getJSONObject(i);
                properties = featuresPos.getJSONObject("properties");
                mag = properties.getDouble("mag");
                place = properties.getString("place");
                time = properties.getInt("time");
                Date date = new Date(time);
                url = properties.getString("url");
                DateFormat dateFormat = new SimpleDateFormat("MMMM dd,yyyy \n hh:mm a");
                String strDate = dateFormat.format(date);

                earthquakes.add(new ListObjectsClass(mag, place, strDate, url));
            }
        } catch (Exception e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtilsAsyncTask", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return earthquakes;
        }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
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

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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
    
}
