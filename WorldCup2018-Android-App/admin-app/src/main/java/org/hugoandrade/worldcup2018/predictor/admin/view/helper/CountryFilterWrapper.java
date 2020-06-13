package org.hugoandrade.worldcup2018.predictor.admin.view.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.hugoandrade.worldcup2018.predictor.admin.data.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryFilterWrapper extends FilterWrapper {

    private List<Country> mCountryList = new ArrayList<>();

    CountryFilterWrapper(Context context) {
        super(context);
    }

    @Override
    protected List<String> buildFilter() {
        if (mCountryList == null) {
            return new ArrayList<>();
        }

        List<String> filterList = new ArrayList<>();
        for (Country country : mCountryList) {
            filterList.add(country.getName());
        }
        return filterList;
    }

    @Override
    FilterPopup onCreatePopup(View view) {
        FilterPopup filterPopup = super.onCreatePopup(view);
        filterPopup.setMaxRows(5);
        return filterPopup;
    }

    private void setCountryList(List<Country> countryList) {
        if (mCountryList != countryList) {
            mCountryList = countryList;

            rebuildFilter();
        }
    }

    public static class Builder extends AbstractBuilder<CountryFilterWrapper, Builder> {

        private List<Country> countryList = new ArrayList<>();

        Builder(Context context) {
            super(new CountryFilterWrapper(context));
        }

        public static Builder instance(Context context) {
            return new Builder(context);
        }

        public Builder setCountryList(List<Country> countryList) {
            this.countryList = countryList;
            return this;
        }

        @Override
        public CountryFilterWrapper build() {
            CountryFilterWrapper filterWrapper = super.build();
            filterWrapper.setCountryList(countryList);
            return filterWrapper;
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }
}
