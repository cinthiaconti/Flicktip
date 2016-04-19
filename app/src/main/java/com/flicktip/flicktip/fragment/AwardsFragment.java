package com.flicktip.flicktip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flicktip.flicktip.activity.MinorActivity;
import com.flicktip.flicktip.R;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.utils.Constants;

import java.util.List;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class AwardsFragment extends Fragment {

    private List<Award> awards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Toolbar vToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        vToolbar.setTitle(R.string.app_name);
        vToolbar.invalidate();

        View layout = inflater.inflate(R.layout.fragment_awards, container, false);
        setFragmentContent(layout);
        setRetainInstance(true);
        return layout;
    }

    private void setFragmentContent(View layout) {

        DBHelper sInstance = DBHelper.getInstance(layout.getContext());
        awards = sInstance.getListAwards();

        ImageView oscars = (ImageView) layout.findViewById(R.id.img_oscar);

        oscars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Award award = awards.get(0);

                Intent activity = new Intent(getActivity(), MinorActivity.class);
                activity.putExtra(Constants.SELECTED_AWARD, award);
                startActivity(activity);


            }
        });
    }
}
