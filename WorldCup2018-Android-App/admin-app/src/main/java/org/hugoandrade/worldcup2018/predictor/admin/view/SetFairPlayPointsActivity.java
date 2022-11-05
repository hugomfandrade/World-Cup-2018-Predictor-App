package org.hugoandrade.worldcup2018.predictor.admin.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.hugoandrade.worldcup2018.predictor.admin.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.admin.view.helper.CountryFilterWrapper;
import org.hugoandrade.worldcup2018.predictor.view.helper.FilterTheme;
import org.hugoandrade.worldcup2018.predictor.view.helper.FilterWrapper;

import java.util.ArrayList;
import java.util.List;

public class SetFairPlayPointsActivity extends AppCompatActivity {

    private static final String INTENT_EXTRA_COUNTRY = "Country";
    private static final String INTENT_EXTRA_COUNTRY_LIST = "CountryList";

    private List<Country> mCountryList;
    private Country mSelectedCountry;

    private EditText etFairPlayPoints;

    public static Intent makeIntent(Context context, ArrayList<Country> countryList, Country country) {
        return new Intent(context, SetFairPlayPointsActivity.class)
                .putExtra(INTENT_EXTRA_COUNTRY_LIST, countryList)
                .putExtra(INTENT_EXTRA_COUNTRY, country);
    }

    public static Country extractCountryFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_COUNTRY);
    }

    public static ArrayList<Country> extractCountryListFromIntent(Intent data) {
        return data.getParcelableArrayListExtra(INTENT_EXTRA_COUNTRY_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCountryList = extractCountryListFromIntent(getIntent());
        mSelectedCountry = extractCountryFromIntent(getIntent());

        initializeUI();

        setupUI();
    }

    private void initializeUI() {

        setContentView(R.layout.activity_set_fair_play_points);

        int initialFilter = 0;
        for (int i = 0 ; i < mCountryList.size() ; i++) {
            if (mCountryList.get(i).getID().equals(mSelectedCountry.getID())) {
                initialFilter = i;
                mSelectedCountry = mCountryList.get(i);
            }
        }

        CountryFilterWrapper.Builder.instance(this)
                .setTheme(FilterTheme.LIGHT)
                .setFilterText(findViewById(R.id.tv_filter_title))
                .setPreviousButton(findViewById(R.id.iv_filter_previous))
                .setNextButton(findViewById(R.id.iv_filter_next))
                .setCountryList(mCountryList)
                .setInitialFilter(initialFilter)
                .setListener(new FilterWrapper.OnFilterSelectedListener() {
                    @Override
                    public void onFilterSelected(int stage) {
                        mSelectedCountry = mCountryList.get(stage);
                        setupUI();
                    }
                })
                .build().setSelectedFilter(initialFilter);


        etFairPlayPoints = findViewById(R.id.et_fair_play_points);

        View vGoBack = findViewById(R.id.v_go_back);
        vGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        View tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        View tvSet = findViewById(R.id.tv_set);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    private void setupUI() {
        etFairPlayPoints.setText(String.valueOf(mSelectedCountry.getFairPlayPoints()));
    }

    private void confirm() {

        try {
            int fairPlayPoints = Integer.valueOf(etFairPlayPoints.getText().toString());
            mSelectedCountry.setFairPlayPoints(fairPlayPoints);

            setResult(RESULT_OK, new Intent().putExtra(INTENT_EXTRA_COUNTRY, mSelectedCountry));
            onBackPressed();
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            goBack();
        }
    }

    private void goBack() {
        setResult(RESULT_CANCELED);
        onBackPressed();
    }
}
