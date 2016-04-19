package com.flicktip.flicktip.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.adapter.SideMenuAdapter;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.SideMenuItem;
import com.flicktip.flicktip.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SideMenuFragment extends Fragment {

    private DBHelper sInstance;

    public List<SideMenuItem> vSideMenuList;
    private static String[] mMenuTitles;
    private TypedArray mMenuIcons;

    private SideMenuAdapter adapter;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView lvSideMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_sidemenu, container, false);
        vSideMenuList = getMenuList();
        adapter = new SideMenuAdapter(getContext(), vSideMenuList);

        lvSideMenu = (ListView) layout.findViewById(R.id.drawerList);
        lvSideMenu.setAdapter(adapter);
        lvSideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayView(position);
            }
        });

        return layout;
    }

    public List<SideMenuItem> getMenuList() {

        sInstance = DBHelper.getInstance(getActivity());
        List<Integer> status = sInstance.getListSize();

        mMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        List<SideMenuItem> items = new ArrayList<>();
        items.add(new SideMenuItem(mMenuTitles[0], mMenuIcons.getResourceId(0, -1)));
        items.add(1, new SideMenuItem(mMenuTitles[1], mMenuIcons.getResourceId(1, -1), status.get(0).toString(), true));
        items.add(2, new SideMenuItem(mMenuTitles[2], mMenuIcons.getResourceId(2, -1), status.get(1).toString(), true));
        items.add(3, new SideMenuItem(mMenuTitles[3], mMenuIcons.getResourceId(3, -1), status.get(2).toString(), true));
        items.add(new SideMenuItem(mMenuTitles[5], mMenuIcons.getResourceId(5, -1)));
        //items.add(new SideMenuItem(mMenuTitles[4], mMenuIcons.getResourceId(4, -1)));

        return items;
    }

    public void setUp(DrawerLayout drawerLayout, final Toolbar toolbar) {

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();

                sInstance = DBHelper.getInstance(getActivity());
                List<Integer> listSize = sInstance.getListSize();

                vSideMenuList.get(1).setCount(listSize.get(0).toString());
                vSideMenuList.get(2).setCount(listSize.get(1).toString());
                vSideMenuList.get(3).setCount(listSize.get(2).toString());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    private void displayView(int position) {

        SideMenuItem item = vSideMenuList.get(position);

        Fragment fragment = null;
        String tag = "";
        String title = getString(R.string.app_name);

        switch (position) {
            case 0:
                AwardsFragment fragmentHome = (AwardsFragment) getFragmentManager().findFragmentByTag(getString(R.string.fragment_awards));
                if (!fragmentHome.isVisible()) {
                    getActivity().onBackPressed();
                }
                mDrawerLayout.closeDrawers();
                return;

            case 1:
                if (item.getCount().equals("0")) {
                    Toast.makeText(getContext(), R.string.empty_list, Toast.LENGTH_SHORT).show();
                    return;
                }
                fragment = new CustomListFragment();
                tag = DBHelper.KEY_VIEWED;
                break;

            case 2:
                if (item.getCount().equals("0")) {
                    Toast.makeText(getContext(), R.string.empty_list, Toast.LENGTH_SHORT).show();
                    return;
                }
                fragment = new CustomListFragment();
                tag = DBHelper.KEY_BOOKMARK;
                break;

            case 3:
                if (item.getCount().equals("0")) {
                    Toast.makeText(getContext(), R.string.empty_list, Toast.LENGTH_SHORT).show();
                    return;
                }
                fragment = new CustomListFragment();
                tag = DBHelper.KEY_FAVORITE;
                break;

            case 4:
                fragment = new AboutFragment();
                title = getString(R.string.fragment_to_watch);
                tag = title;
                break;

            default:
                break;
        }

        if (fragment != null) {

            Bundle arguments = new Bundle();
            arguments.putSerializable(Constants.SELECTED_LIST, tag);
            arguments.putSerializable("toolbar_title", title);
            fragment.setArguments(arguments);

            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.fragment_view, fragment);
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            getFragmentManager().popBackStack();
            transaction.addToBackStack(null);
            transaction.commit();

            mDrawerLayout.closeDrawers();
        }
    }
}