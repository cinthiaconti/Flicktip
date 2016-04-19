package com.flicktip.flicktip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.model.Category;

import java.util.List;

/**
 * Created by Cinthia on 14/03/2016.
 */
public class CategoriesAdapter extends ArrayAdapter<Category> {

    public CategoriesAdapter(Context context, List<Category> categories) {
        super(context, R.layout.fragment_categories, categories);
    }

    private static class CategoryHolder {
        public TextView itemName;
        public TextView itemProgressText;
        public ProgressBar itemProgressBar;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Category category = getItem(position);
        CategoryHolder holder = new CategoryHolder();

        if (view == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_categories_item, parent, false);

            holder.itemName = (TextView) view.findViewById(R.id.list_categories_name);
            holder.itemProgressText = (TextView) view.findViewById(R.id.list_categories_progress_text);
            holder.itemProgressBar = (ProgressBar) view.findViewById(R.id.list_categories_progressbar);
            view.setTag(holder);
        } else {
            holder = (CategoryHolder) view.getTag();
        }

        int progress = category.getProgress().get(0);
        int max = category.getProgress().get(1);
        String total = category.getProgress().get(0) + "/" + category.getProgress().get(1);

        holder.itemProgressBar.setMax(max);
        holder.itemProgressBar.setProgress(progress);
        holder.itemProgressText.setText(total);
        holder.itemName.setText(category.getName());

        return view;
    }
}
