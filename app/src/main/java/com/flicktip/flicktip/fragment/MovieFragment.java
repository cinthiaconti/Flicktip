package com.flicktip.flicktip.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.adapter.ExpandableListAdapter;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.model.Movie;
import com.flicktip.flicktip.model.Nomination;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class MovieFragment extends Fragment {

    private Movie movie;
    private ImageLoader mImageLoader;

    private NetworkInfo checkNetworkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        movie = (Movie) getArguments().getSerializable("SelectedMovie");
        View layout = inflater.inflate(R.layout.fragment_movie, container, false);

        NetworkInfo info = checkNetworkConnection();

        if (info != null && info.isConnected()) {

            new DownloadMovieInfo().execute(movie.getTmdbId());
            setFragmentContent(layout);

        } else {

            movie.setTitle(movie.getDbTitle());
            movie.setOriginalTitle(movie.getDbTitle());
            setToolbarContent(movie);

            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            getActivity().findViewById(R.id.loading_panel).setVisibility(View.GONE);
        }
        return layout;
    }

    private void setFragmentContent(View layout) {

        setViewedListener(layout);
        setFavoriteListener(layout);
        setBookmarkListener(layout);

        DBHelper sInstance = DBHelper.getInstance(layout.getContext());
        List<Award> awards = sInstance.getListAwardsByMovie(movie);

        ExpandableListView lvAwards = (ExpandableListView) layout.findViewById(R.id.list_awards);
        List<String> listAwardsGroup = new ArrayList<>();
        HashMap<String, List<Nomination>> listAwardsItem = new HashMap<>();

        int position = 0;

        for (Award award : awards) {

            List<Nomination> nominations = sInstance.getListNominationsByMovie(award, movie);

            String nominationNumber;

            if (nominations.size() > 1) {
                nominationNumber = nominations.size() + " " + getString(R.string.nominations);
            } else {
                nominationNumber = nominations.size() + " " + getString(R.string.nomination);
            }

            listAwardsGroup.add(award.getName() + ": " + nominationNumber);
            listAwardsItem.put(listAwardsGroup.get(position), nominations);
            position++;
        }
        ExpandableListAdapter adapter = new ExpandableListAdapter(layout.getContext(), listAwardsGroup, listAwardsItem);
        lvAwards.setAdapter(adapter);
        lvAwards.expandGroup(0);
        setListViewHeightBasedOnItems(lvAwards);
    }

    private void setToolbarContent(Movie movie) {

        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mToolbar.setTitle(movie.getTitle());
        mToolbar.setSubtitle(movie.getOriginalTitle());
    }

    private void setViewedListener(View layout) {

        CheckBox movieViewed = (CheckBox) layout.findViewById(R.id.movie_viewed);
        movieViewed.setChecked(movie.getViewed());

        movieViewed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                movie.setViewed(isChecked);
                DBHelper sInstance = DBHelper.getInstance(getActivity());
                sInstance.updateMovie(movie);
            }
        });
    }

    private void setFavoriteListener(View layout) {

        CheckBox favorite = (CheckBox) layout.findViewById(R.id.movie_favorite);
        favorite.setChecked(movie.getFavorite());

        favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                movie.setFavorite(isChecked);
                DBHelper sInstance = DBHelper.getInstance(getActivity());
                sInstance.updateMovie(movie);
            }
        });
    }

    private void setBookmarkListener(View layout) {

        CheckBox bookmark = (CheckBox) layout.findViewById(R.id.movie_bookmark);
        bookmark.setChecked(movie.getBookmark());

        bookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                movie.setBookmark(isChecked);
                DBHelper sInstance = DBHelper.getInstance(getActivity());
                sInstance.updateMovie(movie);
            }
        });
    }

    private static void setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    private void loadImage(final RelativeLayout vLoading, ImageView vPoster, String url) {

        mImageLoader.displayImage(url, vPoster, null, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingCancelled(String uri, View view) {
                Log.i("Script", "onLoadingCancelled()");
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                Log.i("Script", "onLoadingComplete() " + vLoading.getVisibility());
                vLoading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String uri, View view, FailReason fail) {
                Log.i("Script", "onLoadingFailed(" + fail + ")");
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingStarted(String uri, View view) {
                Log.i("Script", "onLoadingStarted()" + vLoading.getVisibility());
                vLoading.setVisibility(View.VISIBLE);
            }

        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String uri, View view, int current, int total) {
                Log.i("Script", "onProgressUpdate(" + uri + " : " + total + " : " + current + ")");
            }
        });
    }

    private class DownloadMovieInfo extends AsyncTask {

        private final String TMDB_API_KEY = "ef68bfed72780ce7ae801b9daba23069";

        private HttpURLConnection createConnection(StringBuilder stringBuilder) throws Exception {

            URL url = new URL(stringBuilder.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Accept", "application/json");
            connection.setDoInput(true);
            connection.connect();

            return connection;
        }

        @Override
        protected Movie doInBackground(Object[] params) {
            return requestMovieById((Long) params[0]);
        }

        @Override
        protected void onPostExecute(Object result) {

            RelativeLayout vLoading = (RelativeLayout) getActivity().findViewById(R.id.loading_panel);

            TextView runtime = (TextView) getActivity().findViewById(R.id.movie_runtime);
            runtime.setText(runtimeToHours(movie.getRuntime()));

            TextView overview = (TextView) getActivity().findViewById(R.id.movie_overview);
            overview.setText(movie.getOverview());

            if (movie.getBackdropPath() != null) {

                ImageView backdrop = (ImageView) getActivity().findViewById(R.id.movie_backdrop);

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

                mImageLoader = ImageLoader.getInstance();
                mImageLoader.init(conf);

                loadImage(vLoading, backdrop, movie.getBackdropPath());
            }else{
                vLoading.setVisibility(View.GONE);
            }

            setToolbarContent(movie);
        }

        private Movie requestMovieById(Long id) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/movie/");
            stringBuilder.append(id);
            stringBuilder.append("?api_key=" + TMDB_API_KEY);

            InputStream stream;

            try {
                HttpURLConnection connection = createConnection(stringBuilder);
                stream = connection.getInputStream();
                JSONObject jsonObject = new JSONObject(stringify(stream));
                movie.setReleaseDate((String) jsonObject.get("release_date"));
                movie.setRuntime((Integer) jsonObject.get("runtime"));
                return movie;

            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        private String stringify(InputStream stream) throws IOException {

            Reader reader = new InputStreamReader(stream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.readLine();
        }

        private String runtimeToHours(Integer runtime) {

            Integer hours = runtime / 60;
            Integer minutes = runtime % 60;

            String min = minutes.toString();

            if (minutes < 10) {
                min = "0" + minutes;
            }
            return hours + "h" + min;
        }

    }

}