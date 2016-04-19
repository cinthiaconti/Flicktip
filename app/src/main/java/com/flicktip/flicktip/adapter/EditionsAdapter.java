package com.flicktip.flicktip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.model.Edition;

import java.util.List;

/**
 * Created by Cinthia on 29/02/2016.
 */
public class EditionsAdapter extends ArrayAdapter<Edition> {

    public EditionsAdapter(Context context, List<Edition> editions) {
        super(context, R.layout.fragment_editions, editions);
    }

    private static class EditionHolder {
        public TextView itemName;
        public TextView itemProgressText;
        public ProgressBar itemProgressBar;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Edition edition = getItem(position);
        EditionHolder holder = new EditionHolder();

        if (view == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_editions_item, parent, false);

            holder.itemName = (TextView) view.findViewById(R.id.list_editions_name);
            holder.itemProgressText = (TextView) view.findViewById(R.id.editions_progress_text);
            holder.itemProgressBar = (ProgressBar) view.findViewById(R.id.list_editions_progressbar);

            view.setTag(holder);

        } else {
            holder = (EditionHolder) view.getTag();
        }

        int progress = edition.getProgress().get(0);
        int max = edition.getProgress().get(1);
        String total = edition.getProgress().get(0) + "/" + edition.getProgress().get(1);

        holder.itemName.setText(edition.getName());
        holder.itemProgressText.setText(total);
        holder.itemProgressBar.setMax(max);
        holder.itemProgressBar.setProgress(progress);

        return view;
    }
}
