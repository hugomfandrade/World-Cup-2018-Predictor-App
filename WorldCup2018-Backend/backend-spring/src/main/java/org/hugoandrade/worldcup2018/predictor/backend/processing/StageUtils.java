package org.hugoandrade.worldcup2018.predictor.backend.processing;

import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.model.Stage;

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
        throw new RuntimeException("this is a utility class");
    }

    public static Stage getStage(Match match) {

        for (Stage stage : Stage.values()) {
            if (stage.is(match)) return stage;
        }

        return Stage.UNKNOWN;
    }

    public static boolean isGroupStage(Match match) {
        return Stage.GROUP_STAGE.is(match);
    }

    public static Stage getStage(int stage) {

        switch (stage) {
            case STAGE_ALL: return Stage.ANY;
            case STAGE_GROUP_STAGE_MATCHDAY_1:
            case STAGE_GROUP_STAGE_MATCHDAY_2:
            case STAGE_GROUP_STAGE_MATCHDAY_3: return Stage.GROUP_STAGE;
            case STAGE_ROUND_OF_16: return Stage.ROUND_OF_16;
            case STAGE_QUARTER_FINALS: return Stage.QUARTER_FINALS;
            case STAGE_SEMI_FINALS: return Stage.SEMI_FINALS;
            case STAGE_3RD_PLACE_PLAYOFF: return Stage.THIRD_PLACE_PLAY_OFF;
            case STAGE_FINAL: return Stage.FINAL;
            default: return Stage.UNKNOWN;
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
}

