package org.hugoandrade.worldcup2018.predictor.view.helper;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.utils.StageUtils;

import java.util.ArrayList;
import java.util.List;

public class StageFilterWrapper extends FilterWrapper {

    StageFilterWrapper(Context context) {
        super(context);
    }

    @Override
    protected List<String> buildFilter() {
        return StageUtils.buildStringList(getContext());
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
}
