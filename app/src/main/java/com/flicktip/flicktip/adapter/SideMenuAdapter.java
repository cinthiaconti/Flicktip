package com.flicktip.flicktip.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.model.SideMenuItem;

import java.util.List;

/**
 * Created by Cinthia on 16/02/2016.
 */
public class SideMenuAdapter extends BaseAdapter {

    private final Context context;
    private final List<SideMenuItem> menuItems;

    public SideMenuAdapter(Context context, List<SideMenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_sidemenu_item, parent, false);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCounter = (TextView) convertView.findViewById(R.id.counter);

        imgIcon.setImageResource(menuItems.get(position).getIcon());
        txtTitle.setText(menuItems.get(position).getTitle());

        if (menuItems.get(position).getCounterVisibility()) {
            txtCounter.setText(menuItems.get(position).getCount());
        } else {
            txtCounter.setVisibility(View.GONE);
        }
        return convertView;
    }
}
