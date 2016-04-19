package com.flicktip.flicktip.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.fragment.MovieFragment;
import com.flicktip.flicktip.model.Movie;
import com.flicktip.flicktip.utils.Constants;

/**
 * Created by Cinthia on 28/02/2016.
 */
public class MovieActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Movie movie = (Movie) getIntent().getSerializableExtra("SelectedMovie");

        Bundle arguments = new Bundle();
        arguments.putSerializable(Constants.SELECTED_MOVIE, movie);

        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(arguments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, fragment, getString(R.string.fragment_movie));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
