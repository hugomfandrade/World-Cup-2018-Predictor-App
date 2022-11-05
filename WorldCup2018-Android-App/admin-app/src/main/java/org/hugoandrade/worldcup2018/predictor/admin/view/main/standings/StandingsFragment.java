package org.hugoandrade.worldcup2018.predictor.admin.view.main.standings;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.worldcup2018.predictor.admin.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.Group;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.MainFragComm;
import org.hugoandrade.worldcup2018.predictor.view.FragComm;
import org.hugoandrade.worldcup2018.predictor.view.FragmentBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StandingsFragment extends FragmentBase<FragComm.RequiredActivityBaseOps>

        implements MainFragComm.ProvidedGroupsChildFragmentOps {

    private HashMap<String, ViewStruct> mGroupViewMap = buildViewStructMap();

    private HashMap<String, ViewStruct> buildViewStructMap() {
        HashMap<String, ViewStruct> viewStructMap = new HashMap<>();
        viewStructMap.put("A", new ViewStruct(R.string.group_a));
        viewStructMap.put("B", new ViewStruct(R.string.group_b));
        viewStructMap.put("C", new ViewStruct(R.string.group_c));
        viewStructMap.put("D", new ViewStruct(R.string.group_d));
        viewStructMap.put("E", new ViewStruct(R.string.group_e));
        viewStructMap.put("F", new ViewStruct(R.string.group_f));
        viewStructMap.put("G", new ViewStruct(R.string.group_g));
        viewStructMap.put("H", new ViewStruct(R.string.group_h));
        return viewStructMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initializeUI(view);
    }

    private void initializeUI(View view) {
        setupGroupLayout(view.findViewById(R.id.layout_group_a), mGroupViewMap.get("A"));
        setupGroupLayout(view.findViewById(R.id.layout_group_b), mGroupViewMap.get("B"));
        setupGroupLayout(view.findViewById(R.id.layout_group_c), mGroupViewMap.get("C"));
        setupGroupLayout(view.findViewById(R.id.layout_group_d), mGroupViewMap.get("D"));
        setupGroupLayout(view.findViewById(R.id.layout_group_e), mGroupViewMap.get("E"));
        setupGroupLayout(view.findViewById(R.id.layout_group_f), mGroupViewMap.get("F"));
        setupGroupLayout(view.findViewById(R.id.layout_group_g), mGroupViewMap.get("G"));
        setupGroupLayout(view.findViewById(R.id.layout_group_h), mGroupViewMap.get("H"));
    }

    @Override
    public void displayGroups(HashMap<String, Group> groupsMap) {
        List<Country> e = new ArrayList<>();
        updateViewStruct(mGroupViewMap.get("A"), groupsMap.get("A") == null? e : groupsMap.get("A").getCountryList());
        updateViewStruct(mGroupViewMap.get("B"), groupsMap.get("B") == null? e : groupsMap.get("B").getCountryList());
        updateViewStruct(mGroupViewMap.get("C"), groupsMap.get("C") == null? e : groupsMap.get("C").getCountryList());
        updateViewStruct(mGroupViewMap.get("D"), groupsMap.get("D") == null? e : groupsMap.get("D").getCountryList());
        updateViewStruct(mGroupViewMap.get("E"), groupsMap.get("E") == null? e : groupsMap.get("E").getCountryList());
        updateViewStruct(mGroupViewMap.get("F"), groupsMap.get("F") == null? e : groupsMap.get("F").getCountryList());
        updateViewStruct(mGroupViewMap.get("G"), groupsMap.get("G") == null? e : groupsMap.get("G").getCountryList());
        updateViewStruct(mGroupViewMap.get("H"), groupsMap.get("H") == null? e : groupsMap.get("H").getCountryList());
    }

    private void setupGroupLayout(View view, ViewStruct viewStruct) {
        // Setup title
        TextView tvGroupTitle = (TextView) view.findViewById(R.id.tv_group);
        tvGroupTitle.setText(getString(viewStruct.getTitleResource()));

        // Setup recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_group);
        recyclerView.setAdapter(viewStruct.getGroupAdapter());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void updateViewStruct(ViewStruct viewStruct, List<Country> countryList) {
        viewStruct.setAll(countryList);
    }

    private class ViewStruct {
        private final int mTitleResID;
        private final GroupListAdapter mGroupAdapter;
        private List<Country> mCountryList;

        ViewStruct(int titleResID) {
            mTitleResID = titleResID;
            mCountryList = new ArrayList<>();
            mGroupAdapter = new GroupListAdapter(mCountryList);
        }


        void setAll(List<Country> countryList) {
            mCountryList = countryList;
            mGroupAdapter.set(mCountryList);
        }

        RecyclerView.Adapter getGroupAdapter() {
            return mGroupAdapter;
        }

        int getTitleResource() {
            return mTitleResID;
        }
    }
}
