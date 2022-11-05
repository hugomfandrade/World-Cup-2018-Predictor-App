package org.hugoandrade.worldcup2018.predictor.admin.view.main;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.hugoandrade.worldcup2018.predictor.admin.GlobalData;
import org.hugoandrade.worldcup2018.predictor.admin.MVP;
import org.hugoandrade.worldcup2018.predictor.admin.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.Group;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.admin.presenter.MainPresenter;
import org.hugoandrade.worldcup2018.predictor.utils.UIUtils;
import org.hugoandrade.worldcup2018.predictor.view.ActivityBase;
import org.hugoandrade.worldcup2018.predictor.admin.view.EditSystemDataActivity;
import org.hugoandrade.worldcup2018.predictor.admin.view.SetFairPlayPointsActivity;
import org.hugoandrade.worldcup2018.predictor.admin.view.helper.SimpleDialog;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.matches.MatchesFragment;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.standings.StandingsFragment;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActivityBase<MVP.RequiredViewOps,
                                               MVP.ProvidedPresenterOps,
                                               MainPresenter>
        implements MVP.RequiredViewOps, MainFragComm.ProvidedMainActivityOps {

    private static final int EDIT_SYSTEM_DATA_REQUEST_CODE = 100;

    private final CharSequence[] mFragmentTitleArray = {"Set Results", "Groups"};
    private final Fragment[] mFragmentArray = {
            new MatchesFragment(),
            new StandingsFragment()};

    private View vProgressBar;

    public static Intent makeIntent(Context activityContext) {
        return new Intent(activityContext, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        enableUI();

        super.onCreate(MainPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_main);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_scores: {

                SimpleDialog simpleDialog = new SimpleDialog(this,
                        "Update Prediction Scores",
                        "Are you sure you want to update the scores of all Predictions? It may take a while");
                simpleDialog.setOnDialogResultListener(new SimpleDialog.OnDialogResult() {
                    @Override
                    public void onResult(DialogInterface dialog, int result) {
                        if (result == SimpleDialog.YES) {
                            getPresenter().updateScoresOfPredictions();
                        }
                    }
                });
                simpleDialog.show();
                return true;
            }
            case R.id.action_reset: {
                SimpleDialog simpleDialog = new SimpleDialog(this,
                        "Reset Cloud Database",
                        "Are you sure you want to reset the Cloud Database?");
                simpleDialog.setOnDialogResultListener(new SimpleDialog.OnDialogResult() {
                    @Override
                    public void onResult(DialogInterface dialog, int result) {
                        if (result == SimpleDialog.YES) {
                            getPresenter().reset();
                        }
                    }
                });
                simpleDialog.show();
                return true;
            }
            case R.id.action_edit_system_data: {

                Bundle options = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //noinspection unchecked
                    options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                }

                startActivityForResult(
                        EditSystemDataActivity.makeIntent(this, GlobalData.getSystemData()),
                        EDIT_SYSTEM_DATA_REQUEST_CODE,
                        options);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // Method accessed by Fragment
    @Override
    public void setMatch(Match match) {
        getPresenter().setMatch(match);
    }


    @Override
    public void displayMatches(List<Match> matchList) {
        for (Fragment fragment : mFragmentArray)
            if (fragment instanceof MainFragComm.ProvidedMatchesFragmentOps)
                ((MainFragComm.ProvidedMatchesFragmentOps) fragment).displayMatches(matchList);
    }

    @Override
    public void updateMatch(Match match) {
        for (Fragment fragment : mFragmentArray)
            if (fragment instanceof MainFragComm.ProvidedMatchesFragmentOps)
                ((MainFragComm.ProvidedMatchesFragmentOps) fragment).updateMatch(match);
    }

    @Override
    public void updateFailedMatch(Match match) {
        for (Fragment fragment : mFragmentArray)
            if (fragment instanceof MainFragComm.ProvidedMatchesFragmentOps)
                ((MainFragComm.ProvidedMatchesFragmentOps) fragment).updateFailedMatch(match);
    }

    @Override
    public void displayGroups(HashMap<String, Group> groupsMap) {
        for (Fragment fragment : mFragmentArray)
            if (fragment instanceof MainFragComm.ProvidedGroupsChildFragmentOps)
                ((MainFragComm.ProvidedGroupsChildFragmentOps) fragment).displayGroups(groupsMap);
    }

    @Override
    public void disableUI() {
        vProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableUI() {
        vProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_SYSTEM_DATA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                SystemData systemData = EditSystemDataActivity.extractSystemDataFromIntent(data);
                getPresenter().updateSystemData(systemData);
            }

            return;
        }
        if (requestCode == MatchesFragment.EDIT_FAIR_PLAY_POINTS) {
            Log.e(TAG, "onActivityResult::EDIT_FAIR_PLAY_POINTS" + resultCode);
            if (resultCode == RESULT_OK) {

                Country country = SetFairPlayPointsActivity.extractCountryFromIntent(data);
                getPresenter().updateCountry(country);
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentArray[position];//SetResultsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return mFragmentArray.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleArray[position];
        }
    }

}
