package com.flicktip.flicktip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.flicktip.flicktip.activity.MovieActivity;
import com.flicktip.flicktip.R;
import com.flicktip.flicktip.adapter.CustomListAdapter;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Movie;
import com.flicktip.flicktip.utils.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.List;

/**
 * Created by Cinthia on 24/02/2016.
 */
public class CustomListFragment extends Fragment {
    private List<Movie> movies;
    private CustomListAdapter adapter;
    private View view;
    private String title;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("Script", "onCreateView()");
        view = inflater.inflate(R.layout.fragment_custom_list, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        DBHelper sInstance = DBHelper.getInstance(view.getContext());
        String list = (String) getArguments().getSerializable("SelectedList");

        assert list != null;
        if (list.equals(DBHelper.KEY_VIEWED)) {
            title = getString(R.string.fragment_watched);
            movies = sInstance.getListMovies(DBHelper.KEY_VIEWED);
        }
        if (list.equals(DBHelper.KEY_BOOKMARK)) {
            title = getString(R.string.fragment_to_watch);
            movies = sInstance.getListMovies(DBHelper.KEY_BOOKMARK);
        }
        if (list.equals(DBHelper.KEY_FAVORITE)) {
            title = getString(R.string.fragment_favorites);
            movies = sInstance.getListMovies(DBHelper.KEY_FAVORITE);
        }

        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mToolbar.setTitle(title);

        if(movies.size() < 1){
            Toast.makeText(getContext(), R.string.empty_list, Toast.LENGTH_SHORT).show();
            adapter.clear();
            return;
        }

        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1000))
                .build();

        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(5)
                .writeDebugLogs()
                .build();

        ImageLoader mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

        PauseOnScrollListener mPauseOnScrollListener = new PauseOnScrollListener(mImageLoader, true, true);
        adapter = new CustomListAdapter(view.getContext(), movies, mImageLoader);

        ListView lvMovies = (ListView) view.findViewById(R.id.list_movies);

        lvMovies.setAdapter(adapter);
        lvMovies.setOnScrollListener(mPauseOnScrollListener);
        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Intent activity = new Intent(getActivity(), MovieActivity.class);
                activity.putExtra(Constants.SELECTED_MOVIE, movies.get(position));
                startActivity(activity);
            }
        });
    }
}
