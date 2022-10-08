package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import javafx.util.Pair;
import org.apache.commons.lang3.SerializationUtils;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TournamentProcessing {

    private final static String TAG = TournamentProcessing.class.getSimpleName();

    private enum Place { HOME, AWAY }
    private static final Map<String, int[]> ROUND_OF_16_MATCH_UPS = new HashMap<>();
    static {
        ROUND_OF_16_MATCH_UPS.put("A", new int[]{49, 51});
        ROUND_OF_16_MATCH_UPS.put("B", new int[]{51, 49});
        ROUND_OF_16_MATCH_UPS.put("C", new int[]{50, 52});
        ROUND_OF_16_MATCH_UPS.put("D", new int[]{52, 50});
        ROUND_OF_16_MATCH_UPS.put("E", new int[]{53, 55});
        ROUND_OF_16_MATCH_UPS.put("F", new int[]{55, 53});
        ROUND_OF_16_MATCH_UPS.put("G", new int[]{54, 56});
        ROUND_OF_16_MATCH_UPS.put("H", new int[]{56, 54});
    }
    private static final Map<Integer, Pair<Integer, Place>> QUARTER_FINALS_MATCH_UPS = new HashMap<>();
    static {
        QUARTER_FINALS_MATCH_UPS.put(49, new Pair<>(57, Place.HOME));
        QUARTER_FINALS_MATCH_UPS.put(50, new Pair<>(57, Place.AWAY));
        QUARTER_FINALS_MATCH_UPS.put(53, new Pair<>(58, Place.HOME));
        QUARTER_FINALS_MATCH_UPS.put(54, new Pair<>(58, Place.AWAY));
        QUARTER_FINALS_MATCH_UPS.put(51, new Pair<>(59, Place.HOME));
        QUARTER_FINALS_MATCH_UPS.put(52, new Pair<>(59, Place.AWAY));
        QUARTER_FINALS_MATCH_UPS.put(55, new Pair<>(60, Place.HOME));
        QUARTER_FINALS_MATCH_UPS.put(56, new Pair<>(60, Place.AWAY));
    }
    private static final Map<Integer, Pair<Integer, Place>> SEMI_FINALS_MATCH_UPS = new HashMap<>();
    static {
        SEMI_FINALS_MATCH_UPS.put(57, new Pair<>(61, Place.HOME));
        SEMI_FINALS_MATCH_UPS.put(58, new Pair<>(61, Place.AWAY));
        SEMI_FINALS_MATCH_UPS.put(59, new Pair<>(62, Place.HOME));
        SEMI_FINALS_MATCH_UPS.put(60, new Pair<>(62, Place.AWAY));
    }
    private static final Map<Integer, Pair<Integer, Place>> THIRD_PLACE_MATCH_UPS = new HashMap<>();
    static {
        THIRD_PLACE_MATCH_UPS.put(61, new Pair<>(63, Place.HOME));
        THIRD_PLACE_MATCH_UPS.put(62, new Pair<>(63, Place.AWAY));
    }
    private static final Map<Integer, Pair<Integer, Place>> FINAL_MATCH_UPS = new HashMap<>();
    static {
        FINAL_MATCH_UPS.put(61, new Pair<>(64, Place.HOME));
        FINAL_MATCH_UPS.put(62, new Pair<>(64, Place.AWAY));
    }

    private OnProcessingListener mOnProcessingFinished;

    private GroupProcessing mTask;

    private ExecutorService mExecutors;

    public void setListener(OnProcessingListener onProcessingListener) {
        mOnProcessingFinished = onProcessingListener;
    }

    public void startUpdateGroupsProcessing(List<Country> countries, List<Match> matches) {
        // Do processing asynchronously
        mTask = new GroupProcessing(mOnProcessingFinished, countries, matches);
        mExecutors = Executors.newCachedThreadPool();
        mExecutors.submit(mTask);
    }

    public void cancel() {
        if (mExecutors != null) mExecutors.shutdownNow();
        mExecutors = null;
        mTask = null;
    }

    public void startUpdateGroupsSync(List<Country> countries, List<Match> matches) {
        // Do processing synchronously
        mTask = new GroupProcessing(mOnProcessingFinished, countries, matches);
        mTask.run();
    }

    public static class GroupProcessing implements Runnable {

        private final WeakReference<OnProcessingListener> mOnProcessingListener;
        private final Map<Integer, Match> mMatchMap;
        private final Map<String, Country> mCountryMap;

        GroupProcessing(OnProcessingListener onProcessingListener, List<Country> countries, List<Match> matches) {
            mOnProcessingListener = new WeakReference<>(onProcessingListener);
            mCountryMap = countries.stream()
                    .collect(Collectors.toMap(Country::getID, Function.identity()));
            mMatchMap = matches.stream()
                    .collect(Collectors.toMap(Match::getMatchNumber, Function.identity()));
        }

        @Override
        public void run() {

            // Create List of countries object, and group them by Group
            final Map<String, Group> groups = setupGroups();
            final Map<String, Country> originalCountries = mCountryMap.values().stream()
                    // serialize, clone
                    .map(SerializationUtils::clone)
                    .collect(Collectors.toMap(Country::getID, Function.identity()));

            // Order each group
            final List<Country> updatedCountries = groups.values().stream()
                    // order
                    .peek(Group::orderGroup)
                    // and collect
                    .map(Group::getCountries)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            // Find countries whose info changed from the original one.
            for (Country updatedCountry : updatedCountries) {
                Country originalCountry = originalCountries.get(updatedCountry.getID());
                if (!updatedCountry.equals(originalCountry))
                    Optional.ofNullable(mOnProcessingListener.get())
                            .ifPresent(l -> l.updateCountry(updatedCountry));
            }

            // Put in database only the matches whose info was modified
            // Check if all matches of each group have been played. If yes, update the matches
            // of the knockout stage appropriately (The first- and second-place teams in each group)
            for (Group group : groups.values()) {
                for (Match match : updateRoundOf16WhenGroupWasPlayed(group))
                    Optional.ofNullable(mOnProcessingListener.get())
                            .ifPresent(l -> l.updateMatchUp(match));
            }

            for (Match match : updateRemainingKnockOutMatchUps())
                Optional.ofNullable(mOnProcessingListener.get())
                        .ifPresent(l -> l.updateMatchUp(match));

            Optional.ofNullable(mOnProcessingListener.get())
                    .ifPresent(l -> l.onProcessingFinished(toList(mCountryMap), toList(mMatchMap)));
        }

        private Map<String, Group> setupGroups() {

            final List<Match> groupStageMatches = Stage.GROUP_STAGE.filter(mMatchMap.values());

            final HashMap<String, Group> groups = new HashMap<>();

            // Iterate over all 32 countries. We gonna make a Country object for each one.
            for (Map.Entry<String, Country> countryEntry : mCountryMap.entrySet()) {

                final String countryID = countryEntry.getKey();
                final Country country = countryEntry.getValue();
                final String groupLetter = country.getGroup();

                final Group group;
                if (groups.containsKey(groupLetter)) {
                    group = groups.get(groupLetter);
                } else {
                    group = new Group(groupLetter);
                    groups.put(groupLetter, group);
                }

                group.add(country);

                for (Match match : groupStageMatches) {
                    if (match.getHomeTeamID().equals(countryID) || match.getAwayTeamID().equals(countryID)) {
                        group.addMatch(match);
                    }
                }
            }

            return groups;
        }

        private List<Match> updateRoundOf16WhenGroupWasPlayed(Group group) {
            String groupLetter = group.getGroupLetter();

            // Update knockout mStage matches only if necessary
            final int[] matchUps = ROUND_OF_16_MATCH_UPS.getOrDefault(groupLetter, new int[]{-1, -1});
            final int winnerMatchUp = matchUps[0];
            final int runnerUpMatchUp = matchUps[1];

            // Check if all countries have played 3 matches
            final boolean areAllMatchesPlayed = group.areAllMatchesPlayed();

            final String winnerTeamID = areAllMatchesPlayed ? group.getCountries().get(0).getID() :
                    "Winner Group " + groupLetter;
            final String runnerUpTeamID = areAllMatchesPlayed ? group.getCountries().get(1).getID() :
                    "Runner-up Group " + groupLetter;

            final List<Match> roundOf16Matches = new NullAvoidList<>();
            roundOf16Matches.add(updateRoundOf16MatchUp(winnerMatchUp, winnerTeamID, Place.HOME));
            roundOf16Matches.add(updateRoundOf16MatchUp(runnerUpMatchUp, runnerUpTeamID, Place.AWAY));
            return roundOf16Matches;
        }

        private Match updateRoundOf16MatchUp(int matchNumber, String teamID, Place place) {

            // Check if there is any need to update match-up
            final Match match = mMatchMap.get(matchNumber);
            if (match == null) return null;
            if (place == Place.HOME && match.getHomeTeamID().equals(teamID)) return null;
            if (place == Place.AWAY && match.getAwayTeamID().equals(teamID)) return null;

            // Update match-up accordingly
            if (place == Place.HOME) {
                match.setHomeTeamID(teamID);
            }
            else if (place == Place.AWAY) {
                match.setAwayTeamID(teamID);
            }
            else {
                System.err.println(TAG + "::" + "MatchUp position (home or away) not recognized");
            }

            return match;
        }

        private List<Match> updateRemainingKnockOutMatchUps() {

            List<Match> knockOutMatchUps = new NullAvoidList<>();
            // Quarter Finals
            for (Map.Entry<Integer, Pair<Integer, Place>> knockOutMatchUpEntry : QUARTER_FINALS_MATCH_UPS.entrySet()) {
                final int matchUp = knockOutMatchUpEntry.getKey();
                final int quarterFinalMatchUp = knockOutMatchUpEntry.getValue().getKey();
                final Place quarterFinalPlace = knockOutMatchUpEntry.getValue().getValue();
                final Match match = updateKnockOutMatchUp(matchUp, quarterFinalMatchUp, quarterFinalPlace);
                knockOutMatchUps.add(match);
            }

            // Semi Finals
            for (Map.Entry<Integer, Pair<Integer, Place>> knockOutMatchUpEntry : SEMI_FINALS_MATCH_UPS.entrySet()) {
                final int matchUp = knockOutMatchUpEntry.getKey();
                final int semiFinalMatchUp = knockOutMatchUpEntry.getValue().getKey();
                final Place semiFinalPlace = knockOutMatchUpEntry.getValue().getValue();
                final Match match = updateKnockOutMatchUp(matchUp, semiFinalMatchUp, semiFinalPlace);
                knockOutMatchUps.add(match);
            }

            // 3rd Playoff
            for (Map.Entry<Integer, Pair<Integer, Place>> knockOutMatchUpEntry : THIRD_PLACE_MATCH_UPS.entrySet()) {
                final int matchUp = knockOutMatchUpEntry.getKey();
                final int thirdPlaceMatchUp = knockOutMatchUpEntry.getValue().getKey();
                final Place thirdPlacePlace = knockOutMatchUpEntry.getValue().getValue();
                final Match match = updateKnockOutMatchUpFor3rdPlace(matchUp, thirdPlaceMatchUp, thirdPlacePlace);
                knockOutMatchUps.add(match);
            }

            // Final
            for (Map.Entry<Integer, Pair<Integer, Place>> knockOutMatchUpEntry : FINAL_MATCH_UPS.entrySet()) {
                final int matchUp = knockOutMatchUpEntry.getKey();
                final int finalMatchUp = knockOutMatchUpEntry.getValue().getKey();
                final Place finalPlace = knockOutMatchUpEntry.getValue().getValue();
                final Match match = updateKnockOutMatchUp(matchUp, finalMatchUp, finalPlace);
                knockOutMatchUps.add(match);
            }

            return knockOutMatchUps;
        }

        private Match updateKnockOutMatchUp(int matchUp, int matchUpToUpdate, Place place) {

            final Match match = mMatchMap.get(matchUp);

            String teamName;
            if (!MatchUtils.isMatchPlayed(match) && MatchUtils.didTeamsTied(match)) {
                teamName = "Winner Match " + Integer.toString(matchUp);
            }
            else {
                if (MatchUtils.didHomeTeamWinRegularTime(match) || MatchUtils.didHomeTeamWinByPenaltyShootout(match))
                    teamName = match.getHomeTeamID();
                else if (MatchUtils.didAwayTeamWinRegularTime(match) || MatchUtils.didAwayTeamWinByPenaltyShootout(match))
                    teamName = match.getAwayTeamID();
                else
                    teamName = "Winner Match " + Integer.toString(matchUp);
            }

            Match matchToUpdate = mMatchMap.get(matchUpToUpdate);

            if (matchToUpdate == null) return null;

            if (place == Place.AWAY) {
                if (!matchToUpdate.getAwayTeamID().equals(teamName)) {
                    matchToUpdate.setAwayTeamID(teamName);
                    return matchToUpdate;
                }
            }
            else if (place == Place.HOME) {
                if (!matchToUpdate.getHomeTeamID().equals(teamName)) {
                    matchToUpdate.setHomeTeamID(teamName);
                    return matchToUpdate;
                }
            }
            return null;
        }

        private Match updateKnockOutMatchUpFor3rdPlace(int matchUp, int matchUpToUpdate, Place place) {

            final Match match = mMatchMap.get(matchUp);

            String teamName;
            if (!MatchUtils.isMatchPlayed(match) && MatchUtils.didTeamsTied(match)) {
                teamName = "Loser Match " + Integer.toString(matchUp);
            }
            else {
                if (MatchUtils.didHomeTeamWinRegularTime(match) || MatchUtils.didHomeTeamWinByPenaltyShootout(match))
                    teamName = match.getAwayTeamID();
                else if (MatchUtils.didAwayTeamWinRegularTime(match) || MatchUtils.didAwayTeamWinByPenaltyShootout(match))
                    teamName = match.getHomeTeamID();
                else
                    teamName = "Loser Match " + Integer.toString(matchUp);
            }

            Match matchToUpdate = mMatchMap.get(matchUpToUpdate);

            if (matchToUpdate == null)
                return null;

            if (place == Place.AWAY) {
                if (!matchToUpdate.getAwayTeamID().equals(teamName)) {
                    matchToUpdate.setAwayTeamID(teamName);
                    return matchToUpdate;
                }
            }
            else if (place == Place.HOME) {
                if (!matchToUpdate.getHomeTeamID().equals(teamName)) {
                    matchToUpdate.setHomeTeamID(teamName);
                    return matchToUpdate;
                }
            }
            return null;
        }

    }

    public interface OnProcessingListener {
        void onProcessingFinished(List<Country> countries, List<Match> matches);
        void updateCountry(Country country);
        void updateMatchUp(Match match);
    }

    private static <T> List<T> toList(Map<?, T> tMap) {
        return new ArrayList<>(tMap.values());
    }

    private static class NullAvoidList<T> extends ArrayList<T> {

        @Override
        public boolean add(T t) {
            if (t == null) return false;
            return super.add(t);
        }
    }
}
