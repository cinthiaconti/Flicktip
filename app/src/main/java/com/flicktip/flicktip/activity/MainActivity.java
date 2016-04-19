package com.flicktip.flicktip.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.fragment.AwardsFragment;
import com.flicktip.flicktip.fragment.SideMenuFragment;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class MainActivity extends AppCompatActivity {

    private DBHelper sInstance;
    private SideMenuFragment drawerFragment;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sInstance = DBHelper.getInstance(this);
        sInstance.populateDB();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        drawerFragment = (SideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_view, new AwardsFragment(), getString(R.string.fragment_awards));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private boolean isTablet(){
        return getResources().getBoolean(R.bool.isTablet);
    }

}
