package org.hugoandrade.worldcup2018.predictor.view.helper;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.R;

import java.util.ArrayList;
import java.util.List;

public class StageFilterWrapper extends FilterWrapper {

    StageFilterWrapper(Context context) {
        super(context);

        setDarkColor(context.getResources().getColor(R.color.colorMain));
    }

    @Override
    protected List<String> buildFilter() {
        return buildStringList(getContext());
    }

    public static class Builder extends AbstractBuilder<StageFilterWrapper, Builder> {

        public Builder(Context context) {
            super(new StageFilterWrapper(context));
        }

        public static Builder instance(Context context) {
            return new Builder(context);
        }

        @Override
        protected Builder getThis() {
            return this;
        }
    }

    public static List<String> buildStringList(Context context) {
        List<String> predictionFilter = new ArrayList<>();
        predictionFilter.add(context.getString(R.string.prediction_filter_all));
        predictionFilter.add(context.getString(R.string.prediction_matchday_1));
        predictionFilter.add(context.getString(R.string.prediction_matchday_2));
        predictionFilter.add(context.getString(R.string.prediction_matchday_3));
        predictionFilter.add(context.getString(R.string.prediction_round_of_16));
        predictionFilter.add(context.getString(R.string.prediction_quarter_finals));
        predictionFilter.add(context.getString(R.string.prediction_semi_finals));
        predictionFilter.add(context.getString(R.string.prediction_third_place_playoff));
        predictionFilter.add(context.getString(R.string.prediction_final));
        return predictionFilter;
    }
}
