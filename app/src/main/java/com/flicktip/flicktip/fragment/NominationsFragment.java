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

import com.flicktip.flicktip.activity.MovieActivity;
import com.flicktip.flicktip.R;
import com.flicktip.flicktip.adapter.NominationsAdapter;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.model.Category;
import com.flicktip.flicktip.model.Edition;
import com.flicktip.flicktip.model.Nomination;
import com.flicktip.flicktip.utils.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class NominationsFragment extends Fragment {

    private Award award;
    private Edition edition;
    private Category category;
    private View view;
    private List<Nomination> nominations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("Script", "onCreateView()");

        view = inflater.inflate(R.layout.fragment_nominations, container, false);

        award = (Award) getArguments().getSerializable("SelectedAward");
        edition = (Edition) getArguments().getSerializable("SelectedEdition");
        category = (Category) getArguments().getSerializable("SelectedCategory");

        if (category != null) {
            setToolbarContent(WordUtils.capitalizeFully(category.getName()));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        DBHelper sInstance = DBHelper.getInstance(getContext());
        nominations = sInstance.getListNominations(award, edition, category);
        category.setNominations(nominations);

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

        NominationsAdapter adapter = new NominationsAdapter(getContext(), nominations, mImageLoader);

        ListView lvNominations = (ListView) view.findViewById(R.id.list_nominations);
        lvNominations.setOnScrollListener(mPauseOnScrollListener);
        lvNominations.setAdapter(adapter);

        lvNominations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Intent activity = new Intent(getActivity(), MovieActivity.class);
                activity.putExtra(Constants.SELECTED_MOVIE, nominations.get(position).getMovie());
                startActivity(activity);
            }
        });
    }

    private void setToolbarContent(String title) {

        Toolbar vToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        vToolbar.setTitle(title + " (" + edition.getName() + ")");
    }
}