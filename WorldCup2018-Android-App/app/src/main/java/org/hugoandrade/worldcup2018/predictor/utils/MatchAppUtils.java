package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseArray;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Provides some general utility Match helper methods.
 */
public final class MatchAppUtils {
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = MatchAppUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private MatchAppUtils() {
        throw new AssertionError();
    }


    public static boolean isMatchupSetUp(Match match) {
        return match != null && isCountry(match.getHomeTeamName()) && isCountry(match.getAwayTeamName());

    }

    public static boolean isCountry(String countryName) {
        if (countryName == null) return false;

        if (countryName.equals(StaticVariableUtils.SCountry.Argentina.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Australia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Belgium.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Brazil.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Colombia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.CostaRica.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Croatia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Denmark.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Egypt.name) ||
                countryName.equals(StaticVariableUtils.SCountry.England.name) ||
                countryName.equals(StaticVariableUtils.SCountry.France.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Germany.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Iceland.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Iran.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Japan.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Mexico.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Morocco.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Nigeria.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Panama.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Peru.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Poland.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Portugal.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Russia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.SaudiArabia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Senegal.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Serbia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.SouthKorea.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Spain.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Sweden.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Switzerland.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Tunisia.name) ||
                countryName.equals(StaticVariableUtils.SCountry.Uruguay.name))
                return true;

        return false;
    }

    public static boolean isValidToGetPreviousMatches(SparseArray<Match> matchSet, Match match) {

        String stage = match.getStage();

        if (stage.equals(StaticVariableUtils.SStage.groupStage.name) ||
                stage.equals(StaticVariableUtils.SStage.roundOf16.name))
            return false;

        if (stage.equals(StaticVariableUtils.SStage.quarterFinals.name) ||
                stage.equals(StaticVariableUtils.SStage.semiFinals.name) ||
                stage.equals(StaticVariableUtils.SStage.thirdPlacePlayOff.name) ||
                stage.equals(StaticVariableUtils.SStage.finals.name)) {

            if (!MatchAppUtils.havePreviousMatchesBeenSetUpStrict(matchSet, match)) {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }

    public static String tryGetTemporaryAwayTeam(Context context, SparseArray<Match> matchSet, Match match) {

        if (MatchUtils.isMatchPlayed(getParentMatch(matchSet, match, false)) ||
                !isValidToGetPreviousMatches(matchSet, match)) {
            return TranslationUtils.translateCountryName(context, match.getAwayTeamName());
        }

        return getTemporaryTeam(context, matchSet, match, false);
    }

    public static String tryGetTemporaryHomeTeam(Context context, SparseArray<Match> matchSet, Match match) {

        if (MatchUtils.isMatchPlayed(getParentMatch(matchSet, match, true)) ||
                !isValidToGetPreviousMatches(matchSet, match)) {
            return TranslationUtils.translateCountryName(context, match.getHomeTeamName());
        }

        return getTemporaryTeam(context, matchSet, match, true);
    }

    public static Match getParentMatch(SparseArray<Match> matchSet, Match match, boolean forHomeTeam) {

        switch (match.getMatchNumber()) {
            case 64: return forHomeTeam? matchSet.get(61) : matchSet.get(62);
            case 63: return forHomeTeam? matchSet.get(61) : matchSet.get(62);
            case 62: return forHomeTeam? matchSet.get(59) : matchSet.get(60);
            case 61: return forHomeTeam? matchSet.get(57) : matchSet.get(58);
            case 60: return forHomeTeam? matchSet.get(55) : matchSet.get(56);
            case 59: return forHomeTeam? matchSet.get(51) : matchSet.get(52);
            case 58: return forHomeTeam? matchSet.get(53) : matchSet.get(54);
            case 57: return forHomeTeam? matchSet.get(49) : matchSet.get(50);

        }
        return null;
    }

    public static String getTemporaryTeam(Context context, SparseArray<Match> matchSet, Match match, boolean forHomeTeam) {

        Match parentMatch = getParentMatch(matchSet, match, forHomeTeam);

        if (parentMatch == null) {
            return TranslationUtils.translateCountryName(context, forHomeTeam?
                            match.getHomeTeamName() :
                            match.getAwayTeamName());
        }
        String firstName = parentMatch.getHomeTeamName();
        String secondName = parentMatch.getAwayTeamName();

        return TextUtils.concat(
                TranslationUtils.translateCountryName(context, firstName),
                "\n",
                context.getString(R.string.or),
                "\n",
                TranslationUtils.translateCountryName(context, secondName)
        ).toString();
    }

    public static boolean havePreviousMatchesBeenSetUpStrict(SparseArray<Match> matchSet, Match match) {

        Match parentMatchHome = getParentMatch(matchSet, match, true);
        Match parentMatchAway = getParentMatch(matchSet, match, false);

        return isMatchupSetUp(parentMatchHome) && isMatchupSetUp(parentMatchAway);
    }

    public static String getScoreOfHomeTeam(Match match) {
        if (match == null || match.getHomeTeamGoals() == -1) return "";

        return (match.getHomeTeamNotes() == null ? "" : match.getHomeTeamNotes())
                    + String.valueOf(match.getHomeTeamGoals());
    }

    public static String getScoreOfAwayTeam(Match match) {
        if (match == null || match.getAwayTeamGoals() == -1) return "";

        return String.valueOf(match.getAwayTeamGoals()) +
                (match.getAwayTeamNotes() == null ? "" : match.getAwayTeamNotes());
    }

    public static String getShortMatchUp(Context context, Match match) {
        if (match == null) return "";

        return TextUtils.concat(
                TranslationUtils.translateCountryName(context, match.getHomeTeamName()),
                " - ",
                TranslationUtils.translateCountryName(context, match.getAwayTeamName())).toString();
    }

    private static final int COLOR_DEFAULT = Color.parseColor("#aaffffff");
    private static final int COLOR_INCORRECT_PREDICTION = Color.parseColor("#aaff0000");
    private static final int COLOR_CORRECT_OUTCOME = Color.parseColor("#aaaa7d00");
    private static final int COLOR_CORRECT_MARGIN_OF_VICTORY = Color.parseColor("#aaAAAA00");
    private static final int COLOR_CORRECT_PREDICTION = Color.parseColor("#aa00AA00");

    public static int getCardColor(Match match, Prediction prediction) {
        if (match == null || !match.getDateAndTime().before(GlobalData.getInstance().getServerTime().getTime()))
            return COLOR_DEFAULT;


        if (prediction == null) {
            return COLOR_INCORRECT_PREDICTION;
        }
        else {
            if (prediction.getScore() == GlobalData.getInstance().systemData.getRules().getRuleCorrectMarginOfVictory()) {
                return COLOR_CORRECT_MARGIN_OF_VICTORY;
            }
            else if (prediction.getScore() == GlobalData.getInstance().systemData.getRules().getRuleCorrectOutcome()) {
                return COLOR_CORRECT_OUTCOME;
            }
            else if (prediction.getScore() == GlobalData.getInstance().systemData.getRules().getRuleCorrectPrediction()) {
                return COLOR_CORRECT_PREDICTION;
            }
            else {
                return COLOR_INCORRECT_PREDICTION;
            }
        }
    }
}

