package com.flicktip.flicktip.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.fragment.EditionsFragment;
import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.utils.Constants;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class MinorActivity extends AppCompatActivity {

    private DBHelper sInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minor);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getString(R.string.fragment_editions));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sInstance = DBHelper.getInstance(this);
        sInstance.populateDB();

        Award award = (Award) getIntent().getSerializableExtra("SelectedAward");

        Bundle arguments = new Bundle();
        arguments.putSerializable(Constants.SELECTED_AWARD, award);

        EditionsFragment fragment = new EditionsFragment();
        fragment.setArguments(arguments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_view, fragment, getString(R.string.fragment_editions));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
