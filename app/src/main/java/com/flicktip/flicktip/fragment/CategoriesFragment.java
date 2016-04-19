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
import com.flicktip.flicktip.adapter.CategoriesAdapter;
import com.flicktip.flicktip.dao.DBHelper;
import com.flicktip.flicktip.model.Award;
import com.flicktip.flicktip.model.Category;
import com.flicktip.flicktip.model.Edition;
import com.flicktip.flicktip.utils.Constants;

import java.util.List;

/**
 * Created by Cinthia on 21/02/2016.
 */
public class CategoriesFragment extends Fragment {

    private Award award;
    private Edition edition;
    private List<Category> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        award = (Award) getArguments().getSerializable("SelectedAward");
        edition = (Edition) getArguments().getSerializable("SelectedEdition");

        setToolbarContent(getString(R.string.fragment_categories));

        View layout = inflater.inflate(R.layout.fragment_categories, container, false);
        setFragmentContent(layout);
        return layout;
    }

    private void setFragmentContent(View layout) {

        DBHelper sInstance = DBHelper.getInstance(layout.getContext());

        categories = sInstance.getListCategories(edition);
        edition.setCategories(categories);

        CategoriesAdapter adapter = new CategoriesAdapter(getContext(), categories);

        ListView lvCategories = (ListView) layout.findViewById(R.id.list_categories);
        lvCategories.setAdapter(adapter);

        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Category category = categories.get(position);

                Bundle arguments = new Bundle();
                arguments.putSerializable(Constants.SELECTED_AWARD, award);
                arguments.putSerializable(Constants.SELECTED_EDITION, edition);
                arguments.putSerializable(Constants.SELECTED_CATEGORY, category);

                NominationsFragment fragment = new NominationsFragment();
                fragment.setArguments(arguments);

                String tag = getString(R.string.fragment_nominations);

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

    private void setToolbarContent(String title) {

        Toolbar vToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        vToolbar.setTitle(title);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
//
//        vToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });


    }
}
