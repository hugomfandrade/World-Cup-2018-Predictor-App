package org.hugoandrade.worldcup2018.predictor.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.User;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StageUtils;
import org.hugoandrade.worldcup2018.predictor.view.helper.FilterWrapper;
import org.hugoandrade.worldcup2018.predictor.view.helper.FilterTheme;
import org.hugoandrade.worldcup2018.predictor.view.helper.StageFilterWrapper;
import org.hugoandrade.worldcup2018.predictor.view.listadapter.PredictionListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UsersPredictionsActivity extends SimpleActivityBase

        implements FilterWrapper.OnFilterSelectedListener {

    private static final String INTENT_EXTRA_USER = "intent_extra_user";
    private static final String INTENT_EXTRA_PREDICTION_LIST = "intent_extra_prediction_list";

    private User mUser;
    private List<Prediction> mPredictionList;

    private RecyclerView rvPredictions;
    private PredictionListAdapter mPredictionsAdapter;
    private StageFilterWrapper mFilterWrapper;

    public static Intent makeIntent(Context context,
                                    User selectedUser,
                                    List<Prediction> predictionList) {

        return new Intent(context, UsersPredictionsActivity.class)
                .putExtra(INTENT_EXTRA_USER, selectedUser)
                .putParcelableArrayListExtra(INTENT_EXTRA_PREDICTION_LIST, new ArrayList<>(predictionList));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mUser = getIntent().getExtras().getParcelable(INTENT_EXTRA_USER);
            mPredictionList = getIntent().getExtras().getParcelableArrayList(INTENT_EXTRA_PREDICTION_LIST);
        }
        else {
            finish();
            return;
        }

        initializeUI();
    }

    private void initializeUI() {

        setContentView(R.layout.activity_user_predictions);

        setSupportActionBar((Toolbar) findViewById(R.id.anim_toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mUser.getUsername());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int maxStage = StageUtils.getStageNumber(MatchUtils.getLastPlayedMatch(
                GlobalData.getInstance().getMatchList(),
                GlobalData.getInstance().getServerTime().getTime()
        ));
        mFilterWrapper = StageFilterWrapper.Builder.instance(this)
                .setTheme(FilterTheme.LIGHT)
                .setFilterText(findViewById(R.id.tv_filter_title))
                .setPreviousButton(findViewById(R.id.iv_filter_previous))
                .setNextButton(findViewById(R.id.iv_filter_next))
                .setMaxFilter(maxStage)
                .setListener(this)
                .build();

        View mWCNotStartedMessageContainer = findViewById(R.id.wc_not_started_message_container);
        mWCNotStartedMessageContainer.setVisibility(hasAnyMatchBeenPlayed()? View.GONE : View.VISIBLE);

        rvPredictions = findViewById(R.id.rv_predictions);
        mPredictionsAdapter = new PredictionListAdapter(GlobalData.getInstance().getMatchList(),
                                                        mPredictionList,
                                                        PredictionListAdapter.VIEW_TYPE_DISPLAY_ONLY);
        mPredictionsAdapter.setOnPredictionSetListener(new PredictionListAdapter.OnPredictionSetListener() {
            @Override
            public void onPredictionSet(Prediction prediction) {
                // No-ops
            }

            @Override
            public void onCountryClicked(Country country) {
                startActivity(CountryDetailsActivity.makeIntent(UsersPredictionsActivity.this, country));
            }
        });
        rvPredictions.setLayoutManager(new LinearLayoutManager(this));
        rvPredictions.setAdapter(mPredictionsAdapter);

        updateUI();
    }

    private boolean hasAnyMatchBeenPlayed() {
        Match lastPlayedMatch = MatchUtils.getLastPlayedMatch(
                GlobalData.getInstance().getMatchList(),
                GlobalData.getInstance().getServerTime().getTime());
        return lastPlayedMatch != null;
    }

    private void updateUI() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Match match = MatchUtils.getLastPlayedMatch(GlobalData.getInstance().getMatchList(),
                                                            GlobalData.getInstance().getServerTime().getTime());

                if (match == null) {// || nextMatchWithDelay == null) {
                    onFilterSelected(mFilterWrapper.getSelectedFilter());
                }
                else {
                    int stageNumber = StageUtils.getStageNumber(match);

                    mFilterWrapper.setSelectedFilter(stageNumber);
                    onFilterSelected(stageNumber);
                }
            }
        }, 100L);
    }

    @Override
    public void onFilterSelected(final int stage) {

        int minMatchNumber = StageUtils.getMinMatchNumber(stage);
        int maxMatchNumber = StageUtils.getMaxMatchNumber(stage);

        List<Match> playedMatchList = GlobalData.getInstance().getPlayedMatchList(minMatchNumber, maxMatchNumber);
        List<Match> matchList = GlobalData.getInstance().getMatchList(minMatchNumber, maxMatchNumber);

        Match lastPlayedMatchAll = MatchUtils.getLastPlayedMatch(
                GlobalData.getInstance().getMatchList(),
                GlobalData.getInstance().getServerTime().getTime());

        Match lastPlayedMatch = MatchUtils.getLastPlayedMatch(
                playedMatchList,
                GlobalData.getInstance().getServerTime().getTime());

        Calendar serverTimeWithDelay;
        if (StageUtils.isGroupStage(lastPlayedMatch)) {
            serverTimeWithDelay = MatchUtils.previousTwoHours((Calendar) GlobalData.getInstance().getServerTime().clone());
        }
        else {
            serverTimeWithDelay = MatchUtils.previousThreeHours((Calendar) GlobalData.getInstance().getServerTime().clone());
        }

        int startingPosition = MatchUtils.getPositionOfLastPlayedPlayedMatch(
                playedMatchList,
                GlobalData.getInstance().getServerTime().getTime());

        if (lastPlayedMatch != null &&
                lastPlayedMatchAll != null &&
                playedMatchList.size() > 0 &&
                lastPlayedMatch.getID() != null &&
                lastPlayedMatch.getDateAndTime() != null &&
                lastPlayedMatch.getID().equals(playedMatchList.get(playedMatchList.size() - 1).getID()) &&
                lastPlayedMatch.getID().equals(lastPlayedMatchAll.getID()) &&
                lastPlayedMatch.getDateAndTime().after(serverTimeWithDelay.getTime())) {
            startingPosition = playedMatchList.size() - 1;
        }
        else {
            startingPosition = startingPosition == matchList.size() ? 0 : (startingPosition - 1);
        }
        final int finalStartingPosition = startingPosition;
        //ViewUtils.showToast(this, "position::" + finalStartingPosition);

        if (mPredictionsAdapter != null) {
            mPredictionsAdapter.setMatchList(playedMatchList);
            mPredictionsAdapter.notifyDataSetChanged();
        }
        if (rvPredictions != null) {
            rvPredictions.setLayoutManager(new LinearLayoutManager(this));
            //rvPredictions.scrollToPosition(0);
            //rvPredictions.scrollToPosition(finalStartingPosition);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (rvPredictions != null) {
                    rvPredictions.scrollToPosition(finalStartingPosition);
                }
            }
        }, 10L);/**/
    }
}
