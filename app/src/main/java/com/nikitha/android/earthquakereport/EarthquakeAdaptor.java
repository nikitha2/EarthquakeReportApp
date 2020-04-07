package com.nikitha.android.earthquakereport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class EarthquakeAdaptor extends ArrayAdapter<ListObjectsClass> {
    ListObjectsClass currentword;
    Context contextActivity;
    ArrayList earthquakeData=new ArrayList<ListObjectsClass>();

    public EarthquakeAdaptor(MainActivity context, ArrayList<ListObjectsClass> listObjects) {
        super(context, 0, listObjects);
        contextActivity=context;
        earthquakeData=listObjects;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_eachlistitem, parent, false);
        }
        final String[] parts = (parent.toString()).split("app:id/",2);
        currentword = getItem(position);

        TextView mag=(TextView) listItemView.findViewById(R.id.mag);
        TextView place=(TextView) listItemView.findViewById(R.id.place);
        TextView place1=(TextView) listItemView.findViewById(R.id.place1);

        TextView date=(TextView) listItemView.findViewById(R.id.date);

//        DecimalFormat formatter = new DecimalFormat("0.00");
//        String output = formatter.format(2.3234);
        mag.setText(Double.toString(currentword.getMag()));

       // String[] splitPlace= currentword.getPlace().split("of ",2);
       // String place11=(splitPlace[0]).toUpperCase();

       // place1.setText(place11+"OF");
       // System.out.println("----------------------------------------1.1"+splitPlace[1]);
        place.setText(currentword.getPlace());
        System.out.println("----------------------------------------1.2"+currentword.getDate());
        date.setText(currentword.getDate());

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) mag.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentword.getMag());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        listItemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentword.getUrl()));
                contextActivity.startActivity(i);
            }
        });
        return listItemView;

    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    public void setData(ArrayList<ListObjectsClass> data) {
        earthquakeData.addAll(data);
        notifyDataSetChanged();
    }
}
