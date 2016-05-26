package com.example.haroonyousuf.moviepick;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.logging.Level;

import retrofit.RestAdapter;

public class MoviePickApplication extends Application {

    public Tracker mTracker;

    public void startTracking(){

        if(mTracker == null) {
            //get google analytics instance
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);

            //create a new tracker
            mTracker = ga.newTracker(R.xml.track_moviepick);

            //enable automatic reports
            ga.enableAutoActivityReports(this);

        }
    }

    public Tracker getTracker(){

        startTracking();

        return mTracker;

    }
}
