package org.hugoandrade.worldcup2018.predictor.admin.view.main.matches;

import android.app.ActivityOptions;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hugoandrade.worldcup2018.predictor.admin.GlobalData;
import org.hugoandrade.worldcup2018.predictor.admin.R;
import org.hugoandrade.worldcup2018.predictor.admin.data.Country;
import org.hugoandrade.worldcup2018.predictor.admin.data.Match;
import org.hugoandrade.worldcup2018.predictor.admin.view.SetFairPlayPointsActivity;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.FragmentBase;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.MainFragComm;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment
        extends FragmentBase<MainFragComm.ProvidedMainActivityOps>
        implements MainFragComm.ProvidedMatchesFragmentOps {

    public static final int EDIT_FAIR_PLAY_POINTS = 200;

    private RecyclerView rvAllMatches;
    private MatchListAdapter mAdapter;
    private List<Match> mMatchList = new ArrayList<>();
    private int selection = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_set_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvAllMatches = view.findViewById(R.id.rv_all_matches);
        rvAllMatches.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new MatchListAdapter(mMatchList);
        mAdapter.setOnSetButtonClickListener(new MatchListAdapter.OnSetButtonClickListener() {
            @Override
            public void onClick(Match match) {
                getParentActivity().setMatch(match);
            }

            @Override
            public void onCountryLongClicked(Country country) {
                Bundle options = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //noinspection unchecked
                    options = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                }

                getActivity().startActivityForResult(SetFairPlayPointsActivity.makeIntent(
                        getActivity(),
                        GlobalData.getCountryList(),
                        country), EDIT_FAIR_PLAY_POINTS, options);
            }
        });

        rvAllMatches.setAdapter(mAdapter);
        rvAllMatches.scrollToPosition(selection);
    }

    @Override
    public void displayMatches(List<Match> matchList) {
        mMatchList = matchList;
        if (mAdapter != null) {
            mAdapter.set(mMatchList);
            mAdapter.notifyDataSetChanged();
            rvAllMatches.scrollToPosition(getStartingItemPosition());
        }
    }

    @Override
    public void updateMatch(Match match) {
        if (mAdapter != null)
            mAdapter.updateMatch(match);
    }

    @Override
    public void updateFailedMatch(Match match) {
        if (mAdapter != null)
            mAdapter.updateFailedMatch(match);
    }

    public int getStartingItemPosition() {
        selection = 0;
        if (mMatchList != null) {
            selection = 0;
            for (int i = 0; i < mMatchList.size(); i++) {
                if (mMatchList.get(i).getHomeTeamGoals() == -1 && mMatchList.get(i).getAwayTeamGoals() == -1) {
                    selection = i;
                    break;
                }
            }
            selection = (selection - 5) < 0? 0 : (selection - 5);
        }
        return selection;
    }
}
