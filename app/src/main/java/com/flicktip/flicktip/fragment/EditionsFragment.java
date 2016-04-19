package com.flicktip.flicktip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flicktip.flicktip.R;
import com.flicktip.flicktip.adapter.EditionsAdapter;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.model.Edition;
import com.flicktip.flicktip.utils.Constants;

import java.util.List;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class EditionsFragment extends Fragment {

    private ListView lvEditions;
    private View view;
    private Award award;
    private List<Edition> editions;

    @Override
    public void onStart() {
        super.onStart();

        DBHelper sInstance = DBHelper.getInstance(view.getContext());

        award = (Award) getArguments().getSerializable("SelectedAward");
        editions = sInstance.getListEditions(award);
        award.setEditions(editions);

        EditionsAdapter adapter = new EditionsAdapter(getContext(), editions);

        lvEditions = (ListView) view.findViewById(R.id.list_editions);
        lvEditions.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_editions, container, false);

        Toolbar vToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        vToolbar.setTitle(R.string.fragment_editions);

        onStart();
        setFragmentContent();
        return view;
    }

    private void setFragmentContent() {

        lvEditions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Edition edition = editions.get(position);

                Bundle arguments = new Bundle();
                arguments.putSerializable(Constants.SELECTED_AWARD, award);
                arguments.putSerializable(Constants.SELECTED_EDITION, edition);

                CategoriesFragment fragment = new CategoriesFragment();
                fragment.setArguments(arguments);

                String tag = getString(R.string.fragment_categories);

                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

                transaction.replace(R.id.fragment_view, fragment, tag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
