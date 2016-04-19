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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Movie;
import com.flicktip.flicktip.model.Nomination;
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
 * Created by Cinthia on 06/02/2016.
 */
public class NominationsAdapter extends ArrayAdapter<Nomination> {

    private final ImageLoader mImageLoader;
    private final LayoutInflater inflater;
    private final Animation animation;

    public NominationsAdapter(Context context, List<Nomination> nominations, ImageLoader mImageLoader) {
        super(context, R.layout.fragment_nominations, nominations);
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        Nomination nomination = getItem(position);
        Holder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_nominations_item, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        RelativeLayout vLayout = (RelativeLayout) convertView.findViewById(R.id.list_nominations_item);

        holder.vNomineeName.setText(nomination.getName());
        holder.vCheckBox.setChecked(nomination.getMovie().getViewed());

        if (nomination.getWinner()) {
            holder.vWinner.setVisibility(View.VISIBLE);
        } else {
            holder.vWinner.setVisibility(View.GONE);
        }

        String posterPath = nomination.getMovie().getPosterPath();
        NetworkInfo info = Functions.checkNetworkConnection(getContext());

        if (info != null && info.isConnected()) {
            if (posterPath != null) {
                holder.vMovieTitle.setText(nomination.getMovie().getTitle());
                vLayout.setAnimation(animation);
                vLayout.setVisibility(View.VISIBLE);

                loadImage(holder.vLoading, holder.vPoster, nomination.getMovie().getPosterPath());
            } else {
                new DownloadMovieInfo(vLayout, holder, position).execute(nomination.getMovie().getDbTitle());
            }
        }

        holder.vCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isClickable()) {
                    if (position != ListView.INVALID_POSITION) {
                        getItem(position).getMovie().setViewed(buttonView.isChecked());
                        Nomination n = getItem(position);
                        n.getMovie().setViewed(isChecked);
                        DBHelper sInstance = DBHelper.getInstance(getContext());
                        sInstance.updateMovie(n.getMovie());
                    }
                }
            }
        });

        return convertView;
    }

    private void loadImage(final ProgressBar vLoading, ImageView vPoster, String url) {

        mImageLoader.displayImage(url, vPoster, null, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingCancelled(String uri, View view) {
                //Log.i("Script", "onLoadingCancelled()");
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String uri, View view, Bitmap bmp) {
                //Log.i("Script", "onLoadingComplete() " + vLoading.getVisibility());
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String uri, View view, FailReason fail) {
                //Log.i("Script", "onLoadingFailed(" + fail + ")");
                vLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingStarted(String uri, View view) {
                //Log.i("Script", "onLoadingStarted()");
                vLoading.setVisibility(View.VISIBLE);
            }

        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String uri, View view, int current, int total) {
               //Log.i("Script", "onProgressUpdate(" + uri + " : " + total + " : " + current + ")");
            }
        });
    }

    private static class Holder {
        public TextView vMovieTitle;
        public TextView vNomineeName;
        public CheckBox vCheckBox;
        public ImageView vPoster;
        public ProgressBar vLoading;
        public TextView vWinner;

        public Holder(View view){
            vMovieTitle = (TextView) view.findViewById(R.id.list_nominations_title);
            vNomineeName = (TextView) view.findViewById(R.id.list_nominations_nominee);
            vCheckBox = (CheckBox) view.findViewById(R.id.list_nominations_checkbox);
            vPoster = (ImageView) view.findViewById(R.id.list_nominations_poster);
            vLoading = (ProgressBar) view.findViewById(R.id.list_nominations_progressbar);
            vWinner = (TextView) view.findViewById(R.id.list_nominations_winner);
        }

    }

    private class DownloadMovieInfo extends AsyncTask {

        private final ImageView vPoster;
        private final TextView vMovieTitle;
        private final ProgressBar vLoading;
        private final RelativeLayout vLayout;
        private final int position;

        public DownloadMovieInfo(RelativeLayout vLayout, Holder holder, int position) {
            this.vPoster = holder.vPoster;
            this.vLoading = holder.vLoading;
            this.position = position;
            this.vMovieTitle = holder.vMovieTitle;
            this.vLayout = vLayout;
        }


        @Override
        protected Movie doInBackground(Object[] params) {
            return requestMovieBySearch(params[0]);
        }

        @Override
        protected void onPostExecute(Object result) {

            Movie movie = (Movie) result;
            Movie currentMovie = getItem(position).getMovie();

            if (movie != null) {
                currentMovie.setBackdropPath(movie.getBackdropPath());
                currentMovie.setPosterPath(movie.getPosterPath());
                currentMovie.setTmdbId(movie.getTmdbId());
                currentMovie.setTitle(movie.getTitle());
                currentMovie.setOriginalTitle(movie.getOriginalTitle());
                currentMovie.setOverview(movie.getOverview());

                vMovieTitle.setText(movie.getTitle());
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
            stringBuilder.append("&year=").append(getItem(position).getMovie().getYear());
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

                if(jsonMovieObject.getString("backdrop_path") != "null") {
                    movie.setBackdropPath("http://image.tmdb.org/t/p/w500" + jsonMovieObject.getString("backdrop_path"));
                }else{
                    movie.setBackdropPath(null);
                }

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