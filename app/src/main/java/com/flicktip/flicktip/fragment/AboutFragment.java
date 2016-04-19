package com.flicktip.flicktip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flicktip.flicktip.R;

/**
 * Created by Cinthia on 24/02/2016.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setToolbarContent();

        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    private void setToolbarContent() {
        Toolbar vToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        vToolbar.setTitle(R.string.fragment_about_us);
    }

}
