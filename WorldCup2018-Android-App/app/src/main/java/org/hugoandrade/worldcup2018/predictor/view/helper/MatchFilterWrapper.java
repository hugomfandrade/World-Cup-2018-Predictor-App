package org.hugoandrade.worldcup2018.predictor.view.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.utils.MatchAppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchFilterWrapper extends FilterWrapper {

    private List<Match> mMatchList = new ArrayList<>();
    private OnMatchSelectedListener mListener;

    MatchFilterWrapper(Context context) {
        super(context);

        setDarkColor(context.getResources().getColor(R.color.colorMain));
    }

    @Override
    protected List<String> buildFilter() {
        if (mMatchList == null) {
            return new ArrayList<>();
        }
        List<String> filterList = new ArrayList<>();
        for (Match match : mMatchList) {

            filterList.add(TextUtils.concat(
                    String.valueOf(match.getMatchNumber()),
                    ": ",
                    MatchAppUtils.getShortMatchUp(getContext(), match)).toString());
        }
        return filterList;
    }

    public void setSelectedMatchNumber(int matchNumber) {

        for (int i = 0; i < mMatchList.size() ; i++) {
            if (matchNumber == mMatchList.get(i).getMatchNumber())
                setSelectedFilter(i);
        }
    }

    @Override
    protected FilterPopup onCreatePopup(View view) {
        FilterPopup filterPopup = super.onCreatePopup(view);
        filterPopup.setMaxRows(5);
        return filterPopup;
    }

    private void setMatchList(List<Match> matchList) {
        if (mMatchList != matchList) {
            //mMatchList = matchList;
            mMatchList.clear();
            mMatchList.addAll(matchList);

            Collections.sort(mMatchList, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return o1.getMatchNumber() - o2.getMatchNumber();
                }
            });

            rebuildFilter();
        }
    }

    public void setOnMatchSelectedListener(OnMatchSelectedListener listener) {
        mListener = listener;

        setListener(new OnFilterSelectedListener() {
            @Override
            public void onFilterSelected(int stage) {
                if (mListener != null) {
                    mListener.onMatchSelected(mMatchList.get(stage));
                }
            }
        });
    }

    public interface OnMatchSelectedListener {
        void onMatchSelected(Match match);
    }

    public static class Builder extends AbstractBuilder<MatchFilterWrapper, Builder> {

        private List<Match> matchList;
        private OnMatchSelectedListener listener;

        public Builder(Context context) {
            super(new MatchFilterWrapper(context));
        }

        public static Builder instance(Context context) {
            return new Builder(context);
        }

        public Builder setMatchList(List<Match> matchList) {
            this.matchList = matchList;
            return this;
        }

        public Builder setOnMatchSelectedListener(OnMatchSelectedListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public MatchFilterWrapper build() {
            MatchFilterWrapper filterWrapper = super.build();
            filterWrapper.setMatchList(matchList);
            filterWrapper.setOnMatchSelectedListener(listener);
            return filterWrapper;
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
