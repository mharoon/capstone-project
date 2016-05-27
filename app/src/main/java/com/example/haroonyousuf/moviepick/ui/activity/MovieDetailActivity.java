package com.example.haroonyousuf.moviepick.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.example.haroonyousuf.moviepick.R;
import com.example.haroonyousuf.moviepick.ui.fragment.MovieDetailActivityFragment;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE = "DetailActivity:image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null){

            Bundle extras = getIntent().getExtras();

            //for tablet layout only
            MovieDetailActivityFragment movieDetailActivityFragment = new MovieDetailActivityFragment();
            movieDetailActivityFragment.setArguments(extras);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_detail_container, movieDetailActivityFragment)
                    .commit();
        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_leave);
    }
}
