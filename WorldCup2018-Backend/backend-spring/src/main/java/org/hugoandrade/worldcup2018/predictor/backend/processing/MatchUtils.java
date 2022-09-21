package org.hugoandrade.worldcup2018.predictor.backend.processing;

import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.model.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.model.Stage;

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

    public static boolean isMatchValid(Match match) {
        return match != null;
    }

    public static boolean isMatchPlayed(Match match) {
        return isMatchValid(match) && match.getHomeTeamGoals() != -1 && match.getAwayTeamGoals() != -1;
    }

    public static boolean didHomeTeamWin(Match match) {
        return didHomeTeamWinRegularTime(match) || didHomeTeamWinByPenaltyShootout(match);
    }

    public static boolean didAwayTeamWin(Match match) {
        return didAwayTeamWinRegularTime(match) || didAwayTeamWinByPenaltyShootout(match);
    }

    public static boolean didHomeTeamWinRegularTime(Match match) {
        return match.getHomeTeamGoals() > match.getAwayTeamGoals();
    }

    public static boolean didAwayTeamWinRegularTime(Match match) {
        return match.getAwayTeamGoals() > match.getHomeTeamGoals();
    }

    public static boolean didTeamsTied(Match match) {
        return isMatchValid(match) && match.getHomeTeamGoals() == match.getAwayTeamGoals();
    }

    public static boolean didHomeTeamWinByPenaltyShootout(Match match) {
        return isMatchValid(match) && "p".equals(match.getHomeTeamNotes());
    }

    public static boolean didAwayTeamWinByPenaltyShootout(Match match) {
        return isMatchValid(match) && "p".equals(match.getAwayTeamNotes());
    }

    public static boolean wasThereAPenaltyShootout(Match match) {
        return isMatchValid(match) &&
                ("p".equals(match.getHomeTeamNotes()) || "p".equals(match.getAwayTeamNotes()));
    }

    public static String getShortDescription(Match match) {
        if (!MatchUtils.isMatchPlayed(match))
            return "";
        return getAsString(match.getHomeTeamNotes()) +
                Integer.toString(match.getHomeTeamGoals()) +
                " - " +
                Integer.toString(match.getAwayTeamGoals()) +
                getAsString(match.getAwayTeamNotes());
    }

    public static String getAsString(String value) {
        return value == null ? "": value;
    }

    public static String getAsString(int value) {
        return value == -1 ? "": Integer.toString(value);
    }

    public static String getString(String value) {
        return value.equals("") ? null: value;
    }

    public static int getInt(String value) {
        return getInt(value, -1);
    }

    public static int getInt(String value, int defaultValue) {
        if (value == null)
            return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Match getFirstNotPlayedMatch(List<Match> matchList, Date serverTime) {
        if (matchList != null) {
            for (Match match : matchList) {
                if (match.getDateAndTime().after(serverTime)) {
                    return match;
                }
            }
        }
        return null;
    }

    public static boolean isPastAllMatches(List<Match> matchList, Date serverTime) {
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i++) {
                if (matchList.get(i).getDateAndTime().after(serverTime)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static int getMatchNumberOfFirstNotPlayedMatch(List<Match> matchList, Date serverTime) {
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i++) {
                if (matchList.get(i).getDateAndTime().after(serverTime)) {
                    return matchList.get(i).getMatchNumber();
                }
            }
            return matchList.size() + 1;
        }
        return 0;
    }

    public static int getPositionOfFirstNotPlayedMatch(List<Match> matchList, Date serverTime) {
        return getPositionOfFirstNotPlayedMatch(matchList, serverTime, 0);
    }

    public static int getPositionOfFirstNotPlayedMatch(List<Match> matchList, Date serverTime, int offset) {
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i++) {
                if (matchList.get(i).getDateAndTime().after(serverTime)) {
                    return (i < offset)? 0 : (i - offset);
                }
            }
            return (matchList.size() < offset)? 0 : (matchList.size() - offset);
        }
        return 0;
    }

    public static int getPositionOfFirstNotPlayedMatchOfPreviousTwoHours(List<Match> matchList, Date serverTime) {
        Calendar tomorrow = previousTwoHours(toCalendar(serverTime));
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i++) {
                if (matchList.get(i).getDateAndTime().after(tomorrow.getTime())) {
                    return i;
                }
            }
            return matchList.size();
        }
        return 0;
    }

    public static int getPositionOfFirstNotPlayedMatchOfPreviousThreeHours(List<Match> matchList, Date serverTime) {
        Calendar tomorrow = previousThreeHours(toCalendar(serverTime));
        if (matchList != null) {
            for (int i = 0; i < matchList.size(); i++) {
                if (matchList.get(i).getDateAndTime().after(tomorrow.getTime())) {
                    return i;
                }
            }
            return matchList.size();
        }
        return 0;
    }

    public static int getPositionOfLastPlayedPlayedMatch(List<Match> matchList, Date serverTime) {
        return getPositionOfLastPlayedPlayedMatch(matchList, serverTime, 0);
    }

    public static int getPositionOfLastPlayedPlayedMatch(List<Match> matchList, Date serverTime, int offset) {
        if (matchList != null) {
            int lastMatchPosition = 0;
            for (int i = 0; i < matchList.size(); i++) {
                Match match = matchList.get(i);
                if (match.getDateAndTime().after(serverTime)) {
                    return (lastMatchPosition < offset)? 0 : (lastMatchPosition - offset);
                }
                else {
                    lastMatchPosition = i;
                }
            }
            return (matchList.size() < offset)? 0 : (matchList.size() - offset);
        }
        return 0;
    }

    public static int getPositionOfLastPlayedPlayedMatchOfPreviousThreeHours(List<Match> matchList, Date serverTime) {
        Calendar tomorrow = previousThreeHours(toCalendar(serverTime));
        if (matchList != null) {
            int lastMatchPosition = 0;
            for (int i = 0; i < matchList.size(); i++) {
                Match match = matchList.get(i);
                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return lastMatchPosition;
                }
                else {
                    lastMatchPosition = i;
                }
            }
            return matchList.size();
        }
        return 0;
    }

    public static int getPositionOfLastPlayedPlayedMatchOfPreviousTwoHours(List<Match> matchList, Date serverTime) {
        Calendar tomorrow = previousTwoHours(toCalendar(serverTime));
        if (matchList != null) {
            int lastMatchPosition = 0;
            for (int i = 0; i < matchList.size(); i++) {
                Match match = matchList.get(i);
                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return lastMatchPosition;
                }
                else {
                    lastMatchPosition = i;
                }
            }
            return matchList.size();
        }
        return 0;
    }

    public static Match getLastPlayedMatch(List<Match> matchList, Date serverTime) {
        if (matchList != null && matchList.size() != 0) {
            Match lastMatch = null;
            for (Match match : matchList) {
                if (match.getDateAndTime().after(serverTime)) {
                    return lastMatch;
                }
                else {
                    lastMatch = match;
                }
            }
            return lastMatch;
        }
        return null;
    }

    public static Match getFirstMatchOfTomorrow(List<Match> matchList, Date time) {
        Calendar tomorrow = toTomorrow(toCalendar(time));
        if (matchList != null && matchList.size() != 0) {
            for (Match match : matchList) {

                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return match;
                }
            }
            return null;//matchList.get(matchList.size() - 1);
        }
        return null;
    }

    public static Match getFirstMatchOfYesterday(List<Match> matchList, Date time) {
        Calendar tomorrow = toYesterday(toCalendar(time));
        if (matchList != null && matchList.size() != 0) {
            for (Match match : matchList) {

                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return match;
                }
            }
            return null;//matchList.get(matchList.size() - 1);
        }
        return null;
    }

    public static Match getFirstMatchOfPreviousTwoHours(List<Match> matchList, Date time) {
        Calendar tomorrow = previousTwoHours(toCalendar(time));
        if (matchList != null && matchList.size() != 0) {
            for (Match match : matchList) {

                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return match;
                }
            }
            return null;//matchList.get(matchList.size() - 1);
        }
        return null;
    }

    public static Match getFirstMatchOfPrevious24Hours(List<Match> matchList, Date time) {
        Calendar tomorrow = previous24Hours(toCalendar(time));
        if (matchList != null && matchList.size() != 0) {
            for (Match match : matchList) {

                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return match;
                }
            }
            return null;//matchList.get(matchList.size() - 1);
        }
        return null;
    }

    public static Match getFirstMatchOfPreviousThreeHours(List<Match> matchList, Date time) {
        Calendar tomorrow = previousThreeHours(toCalendar(time));
        if (matchList != null && matchList.size() != 0) {
            for (Match match : matchList) {

                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return match;
                }
            }
            return null;//matchList.get(matchList.size() - 1);
        }
        return null;
    }

    public static Match getFirstMatchOfPreviousSixHours(List<Match> matchList, Date time) {
        Calendar tomorrow = previousSixHours(toCalendar(time));
        if (matchList != null && matchList.size() != 0) {
            for (Match match : matchList) {

                if (match.getDateAndTime().after(tomorrow.getTime())) {
                    return match;
                }
            }
            return null;//matchList.get(matchList.size() - 1);
        }
        return null;
    }

    private static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private static Calendar toTomorrow(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    private static Calendar toYesterday(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar;
    }

    public static Calendar previousTwoHours(Calendar calendar) {
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        return calendar;
    }

    private static Calendar previous24Hours(Calendar calendar) {
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        return calendar;
    }

    public static Calendar previousThreeHours(Calendar calendar) {
        calendar.add(Calendar.HOUR_OF_DAY, -3);
        return calendar;
    }

    private static Calendar previousSixHours(Calendar calendar) {
        calendar.add(Calendar.HOUR_OF_DAY, -6);
        return calendar;
    }

    public static List<Match> getMatchList(List<Match> matchList, Stage stage, int matchday) {

        List<Match> mList = new ArrayList<>();
        for (Match m : matchList) {
            if (stage.name.equals(m.getStage())) {
                int maxMatchUmber = StageUtils.getMaxMatchNumber(matchday);
                int minMatchUmber = StageUtils.getMinMatchNumber(matchday);
                if (m.getMatchNumber() >= minMatchUmber && m.getMatchNumber() <= maxMatchUmber) {
                    mList.add(m);
                }
            }
        }
        return mList;
    }

    public static List<Match> getMatchList(List<Match> matchList, int minMatchNumber, int maxMatchNumber) {

        List<Match> mList = new ArrayList<>();
        for (Match m : matchList) {
            if (m.getMatchNumber() >= minMatchNumber && m.getMatchNumber() <= maxMatchNumber) {
                mList.add(m);
            }
        }
        return mList;
    }


    public static List<Match> getPlayedMatchList(List<Match> matchList, Date serverTime, int minMatchNumber, int maxMatchNumber) {

        List<Match> mList = new ArrayList<>();
        for (Match m : matchList) {
            if (m.getDateAndTime().before(serverTime) &&
                    m.getMatchNumber() >= minMatchNumber &&
                    m.getMatchNumber() <= maxMatchNumber) {
                mList.add(m);
            }
        }
        return mList;
    }

    public static List<Match> getMatchList(List<Match> matchList, Stage stage) {
        List<Match> mList = new ArrayList<>();
        for (Match m : matchList) {
            if (stage.name.equals(m.getStage())) {
                mList.add(m);
            }
        }
        return mList;
    }

    public static boolean isMatchupSetUp(Match match) {
        return match != null && isCountry(match.getHomeTeamName()) && isCountry(match.getAwayTeamName());

    }

    public static boolean hasAtLeastOneOfTheMatchupSetUp(Match match) {
        return match != null && (isCountry(match.getHomeTeamName()) || isCountry(match.getAwayTeamName()));

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

    public static boolean isPredictionCorrect(Match match, Prediction prediction) {

        return prediction.getHomeTeamGoals() == match.getHomeTeamGoals()
                && prediction.getAwayTeamGoals() == match.getAwayTeamGoals();
    }

    public static boolean isMarginOfVictoryCorrect(Match match, Prediction prediction) {

        return prediction.getHomeTeamGoals() - prediction.getAwayTeamGoals() ==
                match.getHomeTeamGoals() - match.getAwayTeamGoals();
    }

    public static boolean didPredictTie(Prediction prediction) {
        return prediction.getHomeTeamGoals() == prediction.getAwayTeamGoals();
    }
}

