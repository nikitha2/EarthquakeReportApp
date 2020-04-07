package com.nikitha.android.earthquakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<ListObjectsClass>> {
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    EarthquakeAdaptor earthquakesAdapter;
    Bundle input=new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView) findViewById(R.id.list);
        earthquakesAdapter = new EarthquakeAdaptor(this,new ArrayList<ListObjectsClass>());
        listView.setAdapter(earthquakesAdapter);

        input.putString("url",USGS_REQUEST_URL);
        Log.i("initLoader started","TEST:initLoader started");
        LoaderManager.getInstance(this).initLoader(1, input, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<ArrayList<ListObjectsClass>> onCreateLoader(int id, @Nullable Bundle args) {
       // args.putString("url",USGS_REQUEST_URL);
        Log.i("onCreateLoader started","TEST: onCreateLoader started");
        ProgressBar spinnerProgressBar=(ProgressBar) findViewById(R.id.spinner);
        spinnerProgressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("starttime", "2012-01-01");
        uriBuilder.appendQueryParameter("endtime", "2012-12-01");
        uriBuilder.appendQueryParameter("minmagnitude", minMagnitude);
        return new EarthquakesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<ListObjectsClass>> loader, ArrayList<ListObjectsClass> data) {
        Log.i("onLoadFinished started","TEST:onLoadFinished started");
        if(data!=null){
            earthquakesAdapter.setData(data);
        }
        else{
            TextView emptyTextView=(TextView) findViewById(R.id.emptyView);
            ListView listView=(ListView) findViewById(R.id.list);
            emptyTextView.setVisibility(View.VISIBLE);
            listView.setEmptyView(emptyTextView);

            ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
           if(networkInfo==null){
               emptyTextView.setText(getResources().getString(R.string.noDatabczNoInternet));
           }
        }
        ProgressBar spinnerProgressBar=(ProgressBar) findViewById(R.id.spinner);
        spinnerProgressBar.setVisibility(View.INVISIBLE);
        Log.i("onLoadFinished complete","TEST:onLoadFinished completed");
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<ListObjectsClass>> loader) {
        Log.i("onLoaderReset started","TEST:onLoaderReset started");
        earthquakesAdapter.setData(new ArrayList<ListObjectsClass>());

        TextView emptyTextView=(TextView) findViewById(R.id.emptyView);
        ListView listView=(ListView) findViewById(R.id.list);
        emptyTextView.setVisibility(View.VISIBLE);
        listView.setEmptyView(emptyTextView);
        View loadingIndicator = findViewById(R.id.spinner);
        loadingIndicator.setVisibility(View.GONE);
        Log.i("onLoaderReset completed","TEST:onLoaderReset completed");
        //or earthquakesAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

