/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.haroonyousuf.moviepick.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.haroonyousuf.moviepick.R;
import com.example.haroonyousuf.moviepick.api.TMDBService;
import com.example.haroonyousuf.moviepick.constants.Constants;
import com.example.haroonyousuf.moviepick.data.TMDBContract;
import com.example.haroonyousuf.moviepick.model.TMDBMovie;
import com.example.haroonyousuf.moviepick.model.TMDB_Movie_Feeds;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MoviePickSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = MoviePickSyncAdapter.class.getSimpleName();

    public static final int PAGE_INDEX = 1;
    public static final int SYNC_INTERVAL = 60 * 60 * 10; // 10 hours

    public MoviePickSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int sortOption = Integer.parseInt(prefs.getString(Constants.PREF_SORT_LIST_KEY, "0"));

        String sortBy = (sortOption == 0) ? Constants.SORT_BY_POPULARITY : Constants.SORT_BY_VOTE_AVG;

        TMDBService.getTMDBApiClient().getMovieFeeds(sortBy, PAGE_INDEX, Constants.API_KEY, new Callback<TMDB_Movie_Feeds>() {
            @Override
            public void success(TMDB_Movie_Feeds tmdbMovieFeeds, Response response) {
                //bind adapter with new data
                bindGrid(tmdbMovieFeeds.getResults());
            }

            @Override
            public void failure(RetrofitError error) {
                bindGrid(null);
                Log.d("mhyousuf.moviestreamer", "MainActivityFragment:" + error.getMessage());
            }
        });

    }

    private void bindGrid(List<TMDBMovie> results) {
        if (results != null) {
            Vector<ContentValues> cVVector = new Vector<>(results.size());

            for (TMDBMovie movie : results) {

                ContentValues movieValues = new ContentValues();
                movieValues.put(TMDBContract.MovieEntry._ID, movie.getId());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                movieValues.put(TMDBContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

                cVVector.add(movieValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int rowsInserted = getContext().getContentResolver().bulkInsert(TMDBContract.MovieEntry.CONTENT_URI, cvArray);

                Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of movies data");
            }

        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            //boolean isCreated = accountManager.addAccountExplicitly(newAccount, "", null);
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            if (ContentResolver.isSyncPending(newAccount, context.getString(R.string.content_authority)) ||
                    ContentResolver.isSyncActive(newAccount, context.getString(R.string.content_authority))) {
                Log.i("ContentResolver", "SyncPending, canceling");

                ContentResolver.cancelSync(newAccount,
                        context.getString(R.string.content_authority));
            }

            ContentResolver.setIsSyncable(newAccount,
                    context.getString(R.string.content_authority), 1);

            ContentResolver.setSyncAutomatically(newAccount,
                    context.getString(R.string.content_authority),
                    true);

            // schedule the sync adapter
            ContentResolver.addPeriodicSync(newAccount,
                    context.getString(R.string.content_authority),
                    Bundle.EMPTY,
                    SYNC_INTERVAL);


            syncImmediately(context);

        }
        return newAccount;
    }
}