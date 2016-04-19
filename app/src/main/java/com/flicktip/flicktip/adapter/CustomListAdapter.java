package com.flicktip.flicktip.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.model.Movie;
import com.flicktip.flicktip.utils.Constants;
import com.flicktip.flicktip.utils.Functions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cinthia on 25/03/2016.
 */
public class CustomListAdapter extends ArrayAdapter<Movie> {

    private final ImageLoader mImageLoader;
    private final LayoutInflater inflater;
    private final Animation animation;

    public CustomListAdapter(Context context, List<Movie> movie, ImageLoader mImageLoader) {
        super(context, R.layout.fragment_custom_list, movie);
        this.mImageLoader = mImageLoader;
        this.inflater = LayoutInflater.from(getContext());
        this.animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);

    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        Movie movie = getItem(position);
        Holder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.list_custom_list_item, parent, false);

            holder = new Holder();

            holder.vLayout = (RelativeLayout) view.findViewById(R.id.list_movies_item);
            holder.vTitle = (TextView) view.findViewById(R.id.list_movies_title);
            holder.vSubtitle = (TextView) view.findViewById(R.id.list_movies_subtitle);
            holder.vPoster = (ImageView) view.findViewById(R.id.list_movies_poster);
            holder.vLoading = (ProgressBar) view.findViewById(R.id.list_movies_progressbar);
            holder.vYear = (TextView) view.findViewById(R.id.list_movies_year);

            view.setTag(holder);

        } else {
            holder = (Holder) view.getTag();
        }

        holder.vLayout.setVisibility(View.INVISIBLE);
        holder.vPoster.setImageResource(android.R.color.transparent);

        holder.vYear.setText(movie.getYear());
        holder.vSubtitle.setText(movie.getDbTitle());

        String posterPath = movie.getPosterPath();
        NetworkInfo info = Functions.checkNetworkConnection(getContext());

        if (info != null && info.isConnected()) {
            if (posterPath != null) {
                holder.vTitle.setText(movie.getTitle());
                holder.vLayout.setAnimation(animation);
                holder.vLayout.setVisibility(View.VISIBLE);
                loadImage(holder.vLoading, holder.vPoster, movie.getPosterPath());

            } else {
                new DownloadMovieInfo(holder, position).execute(movie.getDbTitle());
            }
        }
        return view;
    }

    private static class Holder {
        public TextView vTitle;
        public ImageView vPoster;
        public ProgressBar vLoading;
        public TextView vYear;
        public TextView vSubtitle;
        public RelativeLayout vLayout;
    }

    private void loadImage(final ProgressBar vLoading, ImageView vPoster, String url) {

        mImageLoader.displayImage(url, vPoster, null, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingCancelled(String uri, View view) {
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                vLoading.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String uri, View view, FailReason fail) {
                Log.i("Script", "onLoadingFailed(" + fail + ")");
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingStarted(String uri, View view) {
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

        private final ImageView vPoster;
        private final TextView vTitle;
        private final RelativeLayout vLayout;
        private final ProgressBar vLoading;
        private final int position;

        public DownloadMovieInfo(Holder holder, int position) {
            this.position = position;
            this.vPoster = holder.vPoster;
            this.vTitle = holder.vTitle;
            this.vLoading = holder.vLoading;
            this.vLayout = holder.vLayout;
        }

        @Override
        protected Movie doInBackground(Object[] params) {
            return requestMovieBySearch(params[0]);
        }

        @Override
        protected void onPostExecute(Object result) {

            Movie movie = (Movie) result;

            if (movie != null) {
                getItem(position).setBackdropPath(movie.getBackdropPath());
                getItem(position).setPosterPath(movie.getPosterPath());
                getItem(position).setTmdbId(movie.getTmdbId());
                getItem(position).setTitle(movie.getTitle());
                getItem(position).setOriginalTitle(movie.getOriginalTitle());
                getItem(position).setOverview(movie.getOverview());

                vTitle.setText(movie.getTitle());

                loadImage(vLoading, vPoster, movie.getPosterPath());

                vLayout.setAnimation(animation);
                vLayout.setVisibility(View.VISIBLE);
            }
        }

        private Movie requestMovieBySearch(Object param) {

            String url = (String) param;
            url = url.replace(" ", "%20");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://api.themoviedb.org/3/search/movie");
            stringBuilder.append("?api_key=" + Constants.TMDB_API_KEY);
            stringBuilder.append("&query=").append(url);
            stringBuilder.append("&year=").append(getItem(position).getYear());
            stringBuilder.append("&language=").append(Locale.getDefault().toString().replace("_", "-"));

            InputStream stream;

            try {
                HttpURLConnection connection = Functions.createConnection(stringBuilder);
                stream = connection.getInputStream();
                return parseResult(Functions.stringify(stream));

            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        private Movie parseResult(String stringResult) {

            Movie movie = new Movie();

            try {
                JSONObject jsonObject = new JSONObject(stringResult);
                JSONArray array = (JSONArray) jsonObject.get("results");
                JSONObject jsonMovieObject = array.getJSONObject(0);

                movie.setTmdbId(Long.valueOf(jsonMovieObject.getString("id")));
                movie.setTitle(jsonMovieObject.getString("title"));
                movie.setOriginalTitle(jsonMovieObject.getString("original_title"));
                movie.setBackdropPath("http://image.tmdb.org/t/p/w500" + jsonMovieObject.getString("backdrop_path"));
                movie.setPosterPath("http://image.tmdb.org/t/p/w500" + jsonMovieObject.getString("poster_path"));
                movie.setOverview(jsonMovieObject.getString("overview"));

                return movie;

            } catch (JSONException e) {
                Log.d(Constants.DEBUG_TAG, "Error parsing JSON. String was: " + stringResult);
            }
            return null;
        }
    }
}
