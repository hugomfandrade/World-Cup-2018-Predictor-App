package org.hugoandrade.worldcup2018.predictor.view.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.common.VerticalLinearLayoutManager;
import org.hugoandrade.worldcup2018.predictor.data.raw.Country;
import org.hugoandrade.worldcup2018.predictor.data.raw.Group;
import org.hugoandrade.worldcup2018.predictor.data.raw.Match;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StageUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StaticVariableUtils.SGroup;
import org.hugoandrade.worldcup2018.predictor.utils.StaticVariableUtils.SStage;
import org.hugoandrade.worldcup2018.predictor.utils.ViewUtils;
import org.hugoandrade.worldcup2018.predictor.view.CountryDetailsActivity;
import org.hugoandrade.worldcup2018.predictor.view.listadapter.GroupListAdapter;
import org.hugoandrade.worldcup2018.predictor.view.listadapter.KnockoutListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StandingsFragment extends FragmentBase<FragComm.RequiredActivityBaseOps> {

    private NestedScrollView nestedScrollView;
    private View tvGroupStageTitle;
    private HashMap<SGroup, GroupViewStruct> mGroupViewStructMap = buildGroupViewStructMap();
    private HashMap<SStage, KnockOutViewStruct> mKnockOutViewStructMap = buildKnockOutViewStructMap();


    private HashMap<SGroup, GroupViewStruct> buildGroupViewStructMap() {
        HashMap<SGroup, GroupViewStruct> viewStructMap = new HashMap<>();
        viewStructMap.put(SGroup.A, new GroupViewStruct(R.string.group_a));
        viewStructMap.put(SGroup.B, new GroupViewStruct(R.string.group_b));
        viewStructMap.put(SGroup.C, new GroupViewStruct(R.string.group_c));
        viewStructMap.put(SGroup.D, new GroupViewStruct(R.string.group_d));
        viewStructMap.put(SGroup.E, new GroupViewStruct(R.string.group_e));
        viewStructMap.put(SGroup.F, new GroupViewStruct(R.string.group_f));
        viewStructMap.put(SGroup.G, new GroupViewStruct(R.string.group_g));
        viewStructMap.put(SGroup.H, new GroupViewStruct(R.string.group_h));
        return viewStructMap;
    }

    private HashMap<SStage, KnockOutViewStruct> buildKnockOutViewStructMap() {
        HashMap<SStage, KnockOutViewStruct> viewStructMap = new HashMap<>();
        viewStructMap.put(SStage.roundOf16, new KnockOutViewStruct(R.string.round_of_16));
        viewStructMap.put(SStage.quarterFinals, new KnockOutViewStruct(R.string.quarter_finals));
        viewStructMap.put(SStage.semiFinals, new KnockOutViewStruct(R.string.semi_finals));
        viewStructMap.put(SStage.thirdPlacePlayOff, new KnockOutViewStruct(R.string.third_place_playoff));
        viewStructMap.put(SStage.finals, new KnockOutViewStruct(R.string.finals));
        return viewStructMap;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GlobalData.getInstance().addOnMatchesChangedListener(mOnMatchesChangedListener);
        GlobalData.getInstance().addOnCountriesChangedListener(mOnCountriesChangedListener);

        return inflater.inflate(R.layout.fragment_standings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nestedScrollView = view.findViewById(R.id.nsv_standings);

        tvGroupStageTitle = view.findViewById(R.id.tv_group_stage_title);

        setupGroupLayout(view.findViewById(R.id.layout_group_a),
                mGroupViewStructMap.get(SGroup.A));
        setupGroupLayout(view.findViewById(R.id.layout_group_b),
                mGroupViewStructMap.get(SGroup.B));
        setupGroupLayout(view.findViewById(R.id.layout_group_c),
                mGroupViewStructMap.get(SGroup.C));
        setupGroupLayout(view.findViewById(R.id.layout_group_d),
                mGroupViewStructMap.get(SGroup.D));
        setupGroupLayout(view.findViewById(R.id.layout_group_e),
                mGroupViewStructMap.get(SGroup.E));
        setupGroupLayout(view.findViewById(R.id.layout_group_f),
                mGroupViewStructMap.get(SGroup.F));
        setupGroupLayout(view.findViewById(R.id.layout_group_g),
                mGroupViewStructMap.get(SGroup.G));
        setupGroupLayout(view.findViewById(R.id.layout_group_h),
                mGroupViewStructMap.get(SGroup.H));

        setupKnockOutLayout(view.findViewById(R.id.layout_round_of_16),
                mKnockOutViewStructMap.get(SStage.roundOf16));
        setupKnockOutLayout(view.findViewById(R.id.layout_quarter_finals),
                mKnockOutViewStructMap.get(SStage.quarterFinals));
        setupKnockOutLayout(view.findViewById(R.id.layout_semi_finals),
                mKnockOutViewStructMap.get(SStage.semiFinals));
        setupKnockOutLayout(view.findViewById(R.id.layout_third_place_playoff),
                mKnockOutViewStructMap.get(SStage.thirdPlacePlayOff));
        setupKnockOutLayout(view.findViewById(R.id.layout_final),
                mKnockOutViewStructMap.get(SStage.finals));

        updateKnockOutView();
        updateGroupView();

        setupInitialScrollPosition();
    }

    private void setupInitialScrollPosition() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                List<Match> matchList = GlobalData.getInstance().getMatchList();
                Date serverTime = GlobalData.getInstance().getServerTime().getTime();

                if (MatchUtils.isPastAllMatches(matchList, serverTime)) {
                    requestFocusOn(SStage.finals);
                }
                else {
                    Match match = MatchUtils.getFirstNotPlayedMatch(matchList, serverTime);

                    SStage sStage = StageUtils.getStage(match);

                    if (sStage == SStage.unknown || sStage == SStage.all)
                        requestFocusOn(SStage.groupStage);
                    else
                        requestFocusOn(sStage);
                }
            }
        }, 10L);
    }

    private void requestFocusOn(SStage stage) {
        if (nestedScrollView != null) {
            scrollToView(nestedScrollView, tvGroupStageTitle);
            if (stage.name.equals(SStage.groupStage.name)) {
                if (tvGroupStageTitle != null) {
                    scrollToView(nestedScrollView, tvGroupStageTitle);
                }
            } else {
                if (mKnockOutViewStructMap != null && mKnockOutViewStructMap.get(stage) != null) {
                    View v = mKnockOutViewStructMap.get(stage).tvTitle;
                    scrollToView(nestedScrollView, v);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        GlobalData.getInstance().removeOnMatchesChangedListener(mOnMatchesChangedListener);
        GlobalData.getInstance().removeOnCountriesChangedListener(mOnCountriesChangedListener);
    }

    private GlobalData.OnMatchesChangedListener mOnMatchesChangedListener
            = new GlobalData.OnMatchesChangedListener() {

        @Override
        public void onMatchesChanged() {
            updateKnockOutView();

            setupInitialScrollPosition();
        }
    };

    private GlobalData.OnCountriesChangedListener mOnCountriesChangedListener
            = new GlobalData.OnCountriesChangedListener() {

        @Override
        public void onCountriesChanged() {
            updateGroupView();

            setupInitialScrollPosition();
        }
    };

    /**
     * Used to scroll to the given view.
     *
     * @param scrollViewParent Parent ScrollView
     * @param view View to which we need to scroll.
     */
    private void scrollToView(final NestedScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumulated Offset.
     */
    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    private void updateGroupView() {
        HashMap<SGroup, Group> groupsMap = setupGroups(GlobalData.getInstance().getCountryList());

        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.A), groupsMap.get(SGroup.A));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.B), groupsMap.get(SGroup.B));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.C), groupsMap.get(SGroup.C));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.D), groupsMap.get(SGroup.D));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.E), groupsMap.get(SGroup.E));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.F), groupsMap.get(SGroup.F));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.G), groupsMap.get(SGroup.G));
        updateGroupViewStruct(mGroupViewStructMap.get(SGroup.H), groupsMap.get(SGroup.H));
    }

    private void updateKnockOutView() {

        HashMap<SStage, List<Match>> matchMap = setupMatches(GlobalData.getInstance().getMatchList());

        updateKnockOutViewStruct(mKnockOutViewStructMap.get(SStage.roundOf16), matchMap.get(SStage.roundOf16));
        updateKnockOutViewStruct(mKnockOutViewStructMap.get(SStage.quarterFinals), matchMap.get(SStage.quarterFinals));
        updateKnockOutViewStruct(mKnockOutViewStructMap.get(SStage.semiFinals), matchMap.get(SStage.semiFinals));
        updateKnockOutViewStruct(mKnockOutViewStructMap.get(SStage.thirdPlacePlayOff), matchMap.get(SStage.thirdPlacePlayOff));
        updateKnockOutViewStruct(mKnockOutViewStructMap.get(SStage.finals), matchMap.get(SStage.finals));
    }

    private void setupGroupLayout(View view, GroupViewStruct groupViewStruct) {
        // Setup title
        TextView tvGroupTitle = view.findViewById(R.id.tv_group);
        tvGroupTitle.setText(getString(groupViewStruct.getTitleResource()));

        // Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.rv_group);
        recyclerView.setAdapter(groupViewStruct.getAdapter());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new VerticalLinearLayoutManager(getActivity()));
    }

    private void setupKnockOutLayout(View view, KnockOutViewStruct knockOutViewStruct) {
        // Setup title
        TextView tvKnockOutTitle = view.findViewById(R.id.tv_knockout_name);
        tvKnockOutTitle.setText(getString(knockOutViewStruct.getTitleResource()));
        knockOutViewStruct.tvTitle = tvKnockOutTitle;

        // Setup recycler view
        RecyclerView recyclerView = view.findViewById(R.id.rv_knockout);
        recyclerView.setAdapter(knockOutViewStruct.getAdapter());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new VerticalLinearLayoutManager(getActivity()));
    }

    private void updateGroupViewStruct(GroupViewStruct groupViewStruct, Group group) {
        if (group != null) {
            groupViewStruct.set(group.getCountryList());
        }
    }

    private void updateKnockOutViewStruct(KnockOutViewStruct knockOutViewStruct, List<Match> matchList) {
        if (matchList != null) {
            knockOutViewStruct.set(matchList);
        }
    }

    /**
     * Utility method to group countries according to the group stage.
     *
     * @param countryList List of countries.
     *
     * @return HashMap of the countries grouped together according to group stage
     */
    private HashMap<SGroup, Group> setupGroups(List<Country> countryList) {
        // Set groups
        HashMap<SGroup, Group> groupsMap = new HashMap<>();
        for (Country c : countryList) {
            SGroup group = SGroup.get(c.getGroup());

            if (groupsMap.containsKey(group)) {
                groupsMap.get(group).add(c);
            } else {
                groupsMap.put(group, new Group(group == null? null : group.name));
                groupsMap.get(group).add(c);
            }
        }
        for (Group group : groupsMap.values())
            Collections.sort(group.getCountryList(), new Comparator<Country>() {
                @Override
                public int compare(Country lhs, Country rhs) {
                    return lhs.getPosition() - rhs.getPosition();
                }
            });

        return groupsMap;
    }

    /**
     * Utility method to group matches according to stage.
     *
     * @param matchList List of matches.
     *
     * @return HashMap of the matches grouped together according to stage
     */
    private HashMap<SStage, List<Match>> setupMatches(List<Match> matchList) {
        // Set groups
        HashMap<SStage, List<Match>> matchesMap = new HashMap<>();
        for (Match m : matchList) {
            SStage stage = SStage.get(m.getStage());

            if (matchesMap.containsKey(stage)) {
                matchesMap.get(stage).add(m);
            } else {
                matchesMap.put(stage, new ArrayList<Match>());
                matchesMap.get(stage).add(m);
            }
        }
        for (List<Match> matches : matchesMap.values())
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match lhs, Match rhs) {
                    return lhs.getMatchNumber() - rhs.getMatchNumber();
                }
            });

        return matchesMap;
    }

    private class GroupViewStruct {
        private final int mTitleResID;
        private final GroupListAdapter mGroupAdapter;
        private List<Country> mCountryList;

        GroupViewStruct(int titleResID) {
            mTitleResID = titleResID;
            mCountryList = new ArrayList<>();
            mGroupAdapter = new GroupListAdapter(mCountryList);
        }


        void set(List<Country> countryList) {
            mCountryList = countryList;
            mGroupAdapter.set(mCountryList);
            mGroupAdapter.notifyDataSetChanged();
        }

        RecyclerView.Adapter getAdapter() {
            return mGroupAdapter;
        }

        int getTitleResource() {
            return mTitleResID;
        }
    }

    private class KnockOutViewStruct {
        private final int mTitleResID;
        private final KnockoutListAdapter mKnockoutAdapter;
        private List<Match> mMatchList;
        private TextView tvTitle;

        KnockOutViewStruct(int titleResID) {
            mTitleResID = titleResID;
            mMatchList = new ArrayList<>();
            mKnockoutAdapter = new KnockoutListAdapter(mMatchList);
            mKnockoutAdapter.setOnKnockoutListAdapterListener(new KnockoutListAdapter.OnKnockoutListAdapterListener() {
                @Override
                public void onCountryClicked(Country country) {

                    startActivity(CountryDetailsActivity.makeIntent(getActivity(), country));

                }
            });
        }


        void set(List<Match> matchList) {
            mMatchList = matchList;
            mKnockoutAdapter.set(mMatchList);
            mKnockoutAdapter.notifyDataSetChanged();
        }

        RecyclerView.Adapter getAdapter() {
            return mKnockoutAdapter;
        }

        int getTitleResource() {
            return mTitleResID;
        }
    }
}