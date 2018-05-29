package org.hugoandrade.worldcup2018.predictor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.MVP;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.raw.Country;
import org.hugoandrade.worldcup2018.predictor.data.raw.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.data.raw.Match;
import org.hugoandrade.worldcup2018.predictor.data.raw.User;
import org.hugoandrade.worldcup2018.predictor.presenter.MatchPredictionPresenter;
import org.hugoandrade.worldcup2018.predictor.utils.BitmapUtils;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StageUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StringUtils;
import org.hugoandrade.worldcup2018.predictor.utils.TranslationUtils;
import org.hugoandrade.worldcup2018.predictor.utils.ViewUtils;
import org.hugoandrade.worldcup2018.predictor.view.helper.FilterTheme;
import org.hugoandrade.worldcup2018.predictor.view.helper.FilterWrapper;
import org.hugoandrade.worldcup2018.predictor.view.helper.MatchFilterWrapper;
import org.hugoandrade.worldcup2018.predictor.view.listadapter.MatchPredictionListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatchPredictionActivity extends MainActivityBase<MVP.RequiredMatchPredictionViewOps,
                                                              MVP.ProvidedMatchPredictionPresenterOps,
                                                              MatchPredictionPresenter>

        implements MVP.RequiredMatchPredictionViewOps {

    private static final String INTENT_EXTRA_USER_LIST = "intent_extra_user_list";
    private static final String INTENT_EXTRA_LEAGUE_NAME = "intent_extra_league_name";

    private static final String TIME_TEMPLATE = "d MMMM - HH:mm";

    private String mLeagueName;
    private List<LeagueUser> mUserList;

    private int mCurrentMatchNumber;

    private View progressBar;
    private MatchPredictionListAdapter mMatchPredictionsAdapter;

    private TextView tvHomeTeam;
    private TextView tvAwayTeam;
    private ImageView ivHomeTeam;
    private ImageView ivAwayTeam;
    private TextView etHomeTeamGoals;
    private TextView etAwayTeamGoals;
    private View detailsInfoContainer;
    private TextView tvMatchNumber;
    private TextView tvStadium;
    private TextView tvStage;
    private TextView tvDateAndTime;

    private MatchFilterWrapper mFilterWrapper;

    private TextView tvMatchText;

    public static Intent makeIntent(Context context, List<LeagueUser> userList, String leagueName) {
        return new Intent(context, MatchPredictionActivity.class)
                .putExtra(INTENT_EXTRA_USER_LIST, new ArrayList<>(userList))
                .putExtra(INTENT_EXTRA_LEAGUE_NAME, leagueName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         Match match = MatchUtils.getLastPlayedMatch(
                GlobalData.getInstance().getMatchList(),
                GlobalData.getInstance().getServerTime().getTime());


        if (getIntent() != null && getIntent() != null && match != null) {
            mLeagueName = getIntent().getStringExtra(INTENT_EXTRA_LEAGUE_NAME);
            mUserList = getIntent().getParcelableArrayListExtra(INTENT_EXTRA_USER_LIST);
            mCurrentMatchNumber = match.getMatchNumber();
        }
        else {
            finish();
            return;
        }

        initializeUI();

        super.onCreate(MatchPredictionPresenter.class, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeUI() {

        setContentView(R.layout.activity_match_predictions);

        setSupportActionBar((Toolbar) findViewById(R.id.anim_toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mLeagueName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressBar = findViewById(R.id.progressBar_waiting);

        tvMatchText = findViewById(R.id.tv_filter_title);

        mFilterWrapper = MatchFilterWrapper.Builder.instance(this)
                .setTheme(FilterTheme.LIGHT)
                .setFilterText(tvMatchText)
                .setPreviousButton(findViewById(R.id.iv_filter_previous))
                .setNextButton(findViewById(R.id.iv_filter_next))
                .setHoldEnabled(true)
                .setInitialFilter(mCurrentMatchNumber)
                .setOnMatchSelectedListener(new MatchFilterWrapper.OnMatchSelectedListener() {
                    @Override
                    public void onMatchSelected(Match match) {
                        filterSpecificMatchNumber(match.getMatchNumber());
                    }
                })
                .setMatchList(GlobalData.getInstance().getPlayedMatchList())
                .build();

        // Match info
        tvHomeTeam = findViewById(R.id.tv_match_home_team);
        tvAwayTeam = findViewById(R.id.tv_match_away_team);
        ivHomeTeam = findViewById(R.id.iv_match_home_team);
        ivAwayTeam = findViewById(R.id.iv_match_away_team);
        ivHomeTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country c = GlobalData.getInstance().getMatch(mCurrentMatchNumber).getHomeTeam();

                onCountryClicked(c);
            }
        });
        ivAwayTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country c = GlobalData.getInstance().getMatch(mCurrentMatchNumber).getAwayTeam();

                onCountryClicked(c);
            }
        });
        etHomeTeamGoals = findViewById(R.id.et_home_team_goals);
        etAwayTeamGoals = findViewById(R.id.et_away_team_goals);
        detailsInfoContainer = findViewById(R.id.viewGroup_info_details_container);
        tvDateAndTime = findViewById(R.id.tv_match_date_time);

        tvMatchNumber = findViewById(R.id.tv_match_number);
        tvStadium = findViewById(R.id.tv_match_stadium);
        tvStage = findViewById(R.id.tv_stage);
        detailsInfoContainer = findViewById(R.id.viewGroup_info_details_container);
        ImageView ivInfo = findViewById(R.id.iv_info);
        ivInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        detailsInfoContainer.setVisibility(View.VISIBLE);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        detailsInfoContainer.setVisibility(View.INVISIBLE);
                        break;
                }

                return true;
            }
        });


        // Prediction list
        RecyclerView rvPredictions = findViewById(R.id.rv_predictions_of_users);
        mMatchPredictionsAdapter = new MatchPredictionListAdapter();
        rvPredictions.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        rvPredictions.setAdapter(mMatchPredictionsAdapter);

    }

    @Override
    public void disableUI() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void enableUI() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected void logout() {
        getPresenter().logout();

        super.logout();
    }

    private void onCountryClicked(Country country) {
        startActivity(CountryDetailsActivity.makeIntent(this, country));
    }

    private void filterSpecificMatchNumber(int matchNumber) {
        getPresenter().getPredictions(mUserList, matchNumber);
    }

    @Override
    public List<LeagueUser> getUserList() {
        return mUserList;
    }

    @Override
    public void setMatchPredictionList(int matchNumber, List<User> userList) {
        mCurrentMatchNumber = matchNumber;

        mFilterWrapper.setSelectedMatchNumber(mCurrentMatchNumber);

        Match match = GlobalData.getInstance().getMatch(matchNumber);

        tvMatchText.setText(TextUtils.concat(getString(R.string.match_number), " ", String.valueOf(matchNumber)));
        mMatchPredictionsAdapter.setMatch(GlobalData.getInstance().getMatch(matchNumber));
        mMatchPredictionsAdapter.setPredictionList(GlobalData.getInstance().getPredictionsOfUsers(matchNumber, userList));
        mMatchPredictionsAdapter.notifyDataSetChanged();

        tvHomeTeam.setText(TranslationUtils.translateCountryName(this, match.getHomeTeamName()));
        tvAwayTeam.setText(TranslationUtils.translateCountryName(this, match.getAwayTeamName()));
        BitmapUtils.decodeSampledBitmapFromResourceAsync(this, ivHomeTeam, Country.getImageID(match.getHomeTeam()));
        BitmapUtils.decodeSampledBitmapFromResourceAsync(this, ivAwayTeam, Country.getImageID(match.getAwayTeam()));

        boolean hasHomeCountryFlag = Country.getImageID(match.getHomeTeam()) != 0;
        boolean hasAwayCountryFlag = Country.getImageID(match.getAwayTeam()) != 0;

        ((View) ivHomeTeam.getParent()).setVisibility(hasHomeCountryFlag ? View.VISIBLE : View.GONE);
        ((View) ivAwayTeam.getParent()).setVisibility(hasAwayCountryFlag ? View.VISIBLE : View.GONE);
        tvHomeTeam.setGravity(hasHomeCountryFlag ? Gravity.TOP | Gravity.CENTER_HORIZONTAL : Gravity.CENTER);
        tvAwayTeam.setGravity(hasAwayCountryFlag ? Gravity.TOP | Gravity.CENTER_HORIZONTAL : Gravity.CENTER);

        etHomeTeamGoals.setText(MatchUtils.getScoreOfHomeTeam(match));
        etAwayTeamGoals.setText(MatchUtils.getScoreOfAwayTeam(match));

        tvDateAndTime.setText(StringUtils.capitalize(DateFormat.format(TIME_TEMPLATE, match.getDateAndTime())));

        tvMatchNumber.setText(TextUtils.concat(getString(R.string.match_number), ": ", String.valueOf(match.getMatchNumber())));
        detailsInfoContainer.setVisibility(View.INVISIBLE);
        tvStage.setText(StageUtils.getAsString(this, match));
        tvStadium.setText(TranslationUtils.translateStadium(this, match.getStadium()));

    }

    @Override
    public void reportMessage(String message) {
        ViewUtils.showToast(this, message);
    }
}
