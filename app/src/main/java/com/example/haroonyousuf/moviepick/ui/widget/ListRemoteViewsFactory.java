package com.example.haroonyousuf.moviepick.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.haroonyousuf.moviepick.R;
import com.example.haroonyousuf.moviepick.constants.Constants;
import com.example.haroonyousuf.moviepick.data.TMDBContract;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = ListRemoteViewsFactory.class.getSimpleName();

    List mCollection = new ArrayList();
    Context mContext = null;
    private Cursor data = null;

    private static final String[] MOVIE_COLUMNS = {
            TMDBContract.MovieEntry.TABLE_NAME + "." + TMDBContract.MovieEntry._ID,
            TMDBContract.MovieEntry.COLUMN_POSTER_PATH,
            TMDBContract.MovieEntry.COLUMN_TITLE,
            TMDBContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    //column indices
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER_PATH = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_VOTE_AVERAGE = 3;

    enum SortType {
        MOST_POPULAR,
        HIGHEST_RATED
    }


    public ListRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
    }

    @Override
    public void onCreate() {
        //initData();
    }

    @Override
    public void onDataSetChanged() {
        //initData();
        if (data != null) {
            data.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri movieUri = TMDBContract.MovieEntry.CONTENT_URI;

        /*String selection = TMDBContract.MovieEntry.COLUMN_POPULARITY  + " > ?";
        String[] selectionArgs = new String[]{Integer.toString(20)};*/
        String sortOrder = TMDBContract.MovieEntry.COLUMN_POPULARITY + " DESC LIMIT " + Constants.WIDGET_ITEM_LIMIT;

        data = mContext.getContentResolver().query(movieUri, MOVIE_COLUMNS, null, null, sortOrder);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        data.moveToPosition(position);

        String moviePoster = data.getString(COL_POSTER_PATH);
        String movieTitle = data.getString(COL_TITLE);
        String movieAvg = data.getString(COL_VOTE_AVERAGE);


        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        remoteViews.setImageViewBitmap(R.id.movie_poster, getImageBitmap(moviePoster));
        remoteViews.setTextViewText(R.id.movie_title, movieTitle);
        remoteViews.setTextViewText(R.id.vote_avg, movieAvg);

        //remoteViews.setTextColor(R.id.text1, Color.BLACK);

        final Intent fillInIntent = new Intent();


        Uri movieUri = TMDBContract.MovieEntry.CONTENT_URI;
        fillInIntent.setData(movieUri);
        remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting bitmap", e);
        }
        return bm;
    }
}
