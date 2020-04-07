package com.nikitha.android.earthquakereport;

public class ListObjectsClass {

    double mag;
    String place;
    String date;
    String url;

    public ListObjectsClass(double mag, String place, String date,String url) {
        this.mag = mag;
        this.place = place;
        this.date = date;
        this.url=url;
    }


    public ListObjectsClass() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public double getMag() {
        return mag;
    }

    public void setMag(double mag) {
        this.mag = mag;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
