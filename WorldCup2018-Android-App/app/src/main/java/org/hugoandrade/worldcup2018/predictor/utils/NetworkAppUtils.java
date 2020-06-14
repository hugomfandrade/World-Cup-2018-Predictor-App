package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.R;

public final class NetworkAppUtils {

    public static boolean isNetworkUnavailableError(Context context, String message) {

        return message != null
                && context != null
                && message.equals(context.getString(R.string.no_network_connection));

    }
}
