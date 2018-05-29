package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.raw.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides some general utility League helper methods.
 */
public final class StageUtils {

    public final static int STAGE_ALL = 0;
    public final static int STAGE_GROUP_STAGE_MATCHDAY_1 = 1;
    public final static int STAGE_GROUP_STAGE_MATCHDAY_2 = 2;
    public final static int STAGE_GROUP_STAGE_MATCHDAY_3 = 3;
    public final static int STAGE_ROUND_OF_16 = 4;
    public final static int STAGE_QUARTER_FINALS = 5;
    public final static int STAGE_SEMI_FINALS = 6;
    public final static int STAGE_3RD_PLACE_PLAYOFF  = 7;
    public final static int STAGE_FINAL = 8;
    public final static int STAGE_UNKNOWN = 9;
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = StageUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private StageUtils() {
        throw new AssertionError();
    }


    public static StaticVariableUtils.SStage getStage(Match match) {

        if (match == null)
            return StaticVariableUtils.SStage.unknown;

        String stage = match.getStage();

        if (StaticVariableUtils.SStage.groupStage.name.equals(stage))
            return StaticVariableUtils.SStage.groupStage;
        if (StaticVariableUtils.SStage.roundOf16.name.equals(stage))
            return StaticVariableUtils.SStage.roundOf16;
        if (StaticVariableUtils.SStage.quarterFinals.name.equals(stage))
            return StaticVariableUtils.SStage.quarterFinals;
        if (StaticVariableUtils.SStage.semiFinals.name.equals(stage))
            return StaticVariableUtils.SStage.semiFinals;
        if (StaticVariableUtils.SStage.thirdPlacePlayOff.name.equals(stage))
            return StaticVariableUtils.SStage.thirdPlacePlayOff;
        if (StaticVariableUtils.SStage.finals.name.equals(stage))
            return StaticVariableUtils.SStage.finals;

        return StaticVariableUtils.SStage.unknown;
    }

    public static boolean isGroupStage(Match match) {
        return match != null && getStage(match).equals(StaticVariableUtils.SStage.groupStage);
    }

    public static StaticVariableUtils.SStage getStage(int stage) {

        switch (stage) {
            case STAGE_ALL: return StaticVariableUtils.SStage.all;
            case STAGE_GROUP_STAGE_MATCHDAY_1:
            case STAGE_GROUP_STAGE_MATCHDAY_2:
            case STAGE_GROUP_STAGE_MATCHDAY_3: return StaticVariableUtils.SStage.groupStage;
            case STAGE_ROUND_OF_16: return StaticVariableUtils.SStage.roundOf16;
            case STAGE_QUARTER_FINALS: return StaticVariableUtils.SStage.quarterFinals;
            case STAGE_SEMI_FINALS: return StaticVariableUtils.SStage.semiFinals;
            case STAGE_3RD_PLACE_PLAYOFF: return StaticVariableUtils.SStage.thirdPlacePlayOff;
            case STAGE_FINAL: return StaticVariableUtils.SStage.finals;
            default: return StaticVariableUtils.SStage.unknown;
        }
    }

    public static int getStageNumber(Match match) {
        if (match == null)
            return STAGE_ALL;

        int matchNumber = match.getMatchNumber();
        if (matchNumber >= 1 && matchNumber <= 16) return STAGE_GROUP_STAGE_MATCHDAY_1;
        if (matchNumber >= 17 && matchNumber <= 32) return STAGE_GROUP_STAGE_MATCHDAY_2;
        if (matchNumber >= 33 && matchNumber <= 48) return STAGE_GROUP_STAGE_MATCHDAY_3;
        if (matchNumber >= 49 && matchNumber <= 56) return STAGE_ROUND_OF_16;
        if (matchNumber >= 57 && matchNumber <= 60) return STAGE_QUARTER_FINALS;
        if (matchNumber >= 61 && matchNumber <= 62) return STAGE_SEMI_FINALS;
        if (matchNumber >= 63 && matchNumber <= 63) return STAGE_3RD_PLACE_PLAYOFF;
        if (matchNumber >= 64 && matchNumber <= 64) return STAGE_FINAL;

        return STAGE_ALL;
    }

    public static int getMinMatchNumber(int stage) {

        switch (stage) {
            case STAGE_ALL:
            case STAGE_GROUP_STAGE_MATCHDAY_1: return 1;
            case STAGE_GROUP_STAGE_MATCHDAY_2: return 17;
            case STAGE_GROUP_STAGE_MATCHDAY_3: return 33;
            case STAGE_ROUND_OF_16: return 49;
            case STAGE_QUARTER_FINALS: return 57;
            case STAGE_SEMI_FINALS: return 61;
            case STAGE_3RD_PLACE_PLAYOFF: return 63;
            case STAGE_FINAL: return 64;
            default: return 1;
        }
    }

    public static int getMaxMatchNumber(int stage) {

        switch (stage) {
            case STAGE_ALL: return 64;
            case STAGE_GROUP_STAGE_MATCHDAY_1: return 16;
            case STAGE_GROUP_STAGE_MATCHDAY_2: return 32;
            case STAGE_GROUP_STAGE_MATCHDAY_3: return 48;
            case STAGE_ROUND_OF_16: return 56;
            case STAGE_QUARTER_FINALS: return 60;
            case STAGE_SEMI_FINALS: return 62;
            case STAGE_3RD_PLACE_PLAYOFF: return 63;
            case STAGE_FINAL: return 64;
            default: return 64;
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

    public static String getAsString(Context context, Match match) {
        return String.format("%s%s",
                TranslationUtils.translateStage(context, match.getStage()),
                match.getGroup() == null ? "" : (" - " + match.getGroup()));
    }
}

