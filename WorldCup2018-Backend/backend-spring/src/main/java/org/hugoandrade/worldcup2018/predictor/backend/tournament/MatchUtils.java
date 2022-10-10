package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.Prediction;

import java.util.*;

/**
 * Provides some general utility Match helper methods.
 */
public final class MatchUtils {
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = MatchUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private MatchUtils() {
        throw new RuntimeException("this is a utility class");
    }

    public static boolean isMatchValid(MatchDto match) {
        return match != null;
    }

    public static boolean isMatchPlayed(MatchDto match) {
        return isMatchValid(match) && match.getHomeTeamGoals() != -1 && match.getAwayTeamGoals() != -1;
    }

    public static boolean didHomeTeamWin(MatchDto match) {
        return didHomeTeamWinRegularTime(match) || didHomeTeamWinByPenaltyShootout(match);
    }

    public static boolean didAwayTeamWin(MatchDto match) {
        return didAwayTeamWinRegularTime(match) || didAwayTeamWinByPenaltyShootout(match);
    }

    public static boolean didHomeTeamWinRegularTime(MatchDto match) {
        return match.getHomeTeamGoals() > match.getAwayTeamGoals();
    }

    public static boolean didAwayTeamWinRegularTime(MatchDto match) {
        return match.getAwayTeamGoals() > match.getHomeTeamGoals();
    }

    public static boolean didTeamsTied(MatchDto match) {
        return isMatchValid(match) && match.getHomeTeamGoals() == match.getAwayTeamGoals();
    }

    public static boolean didHomeTeamWinByPenaltyShootout(MatchDto match) {
        return isMatchValid(match) && "p".equals(match.getHomeTeamNotes());
    }

    public static boolean didAwayTeamWinByPenaltyShootout(MatchDto match) {
        return isMatchValid(match) && "p".equals(match.getAwayTeamNotes());
    }

    public static boolean wasThereAPenaltyShootout(MatchDto match) {
        return isMatchValid(match) &&
                ("p".equals(match.getHomeTeamNotes()) || "p".equals(match.getAwayTeamNotes()));
    }

    public static boolean isMatchupSetUp(Match match) {
        return match != null && isCountry(match.getHomeTeamID()) && isCountry(match.getAwayTeamID());
    }

    public static boolean hasAtLeastOneOfTheMatchupSetUp(Match match) {
        return match != null && (isCountry(match.getHomeTeamID()) || isCountry(match.getAwayTeamID()));

    }

    public static boolean isCountry(String countryName) {
        if (countryName == null) return false;

        for (Country.Tournament country : Country.Tournament.values()) {
            if (country.name.equals(countryName)) return true;
        }

        return false;
    }

    public static boolean isValidToGetPreviousMatches(Map<Integer, Match> matchSet, Match match) {

        String stage = match.getStage();

        if (stage.equals(Stage.GROUP_STAGE.name) || stage.equals(Stage.ROUND_OF_16.name))
            return false;

        if (stage.equals(Stage.QUARTER_FINALS.name) ||
                stage.equals(Stage.SEMI_FINALS.name) ||
                stage.equals(Stage.THIRD_PLACE_PLAY_OFF.name) ||
                stage.equals(Stage.FINAL.name)) {

            if (!MatchUtils.havePreviousMatchesBeenSetUpStrict(matchSet, match)) {
                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }

    public static Match getParentMatch(Map<Integer, Match> matchSet, Match match, boolean forHomeTeam) {

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

    public static boolean havePreviousMatchesBeenSetUpStrict(Map<Integer, Match> matchSet, Match match) {

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

    public static boolean isPredictionSet(Prediction prediction) {

        return Integer.valueOf(prediction.getHomeTeamGoals()) != null &&
                Integer.valueOf(prediction.getAwayTeamGoals()) != null &&
                prediction.getHomeTeamGoals() != -1 &&
                prediction.getAwayTeamGoals() != -1;
    }

    public static boolean didPredictHomeTeamWin(Prediction prediction) {
        return prediction.getHomeTeamGoals() > prediction.getAwayTeamGoals();
    }

    public static boolean didPredictAwayTeamWin(Prediction prediction) {
        return prediction.getAwayTeamGoals() > prediction.getHomeTeamGoals();
    }

    public static boolean isPredictionCorrect(MatchDto match, Prediction prediction) {

        return prediction.getHomeTeamGoals() == match.getHomeTeamGoals()
                && prediction.getAwayTeamGoals() == match.getAwayTeamGoals();
    }

    public static boolean isMarginOfVictoryCorrect(MatchDto match, Prediction prediction) {

        return prediction.getHomeTeamGoals() - prediction.getAwayTeamGoals() ==
                match.getHomeTeamGoals() - match.getAwayTeamGoals();
    }

    public static boolean didPredictTie(Prediction prediction) {
        return prediction.getHomeTeamGoals() == prediction.getAwayTeamGoals();
    }
}

