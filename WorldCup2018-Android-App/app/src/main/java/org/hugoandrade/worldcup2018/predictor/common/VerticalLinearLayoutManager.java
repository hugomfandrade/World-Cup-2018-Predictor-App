package org.hugoandrade.worldcup2018.predictor.common;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class VerticalLinearLayoutManager extends LinearLayoutManager {

    public VerticalLinearLayoutManager(Context context) {
        super(context, LinearLayoutManager.VERTICAL, false);
    }
}
