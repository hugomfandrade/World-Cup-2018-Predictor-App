package org.hugoandrade.worldcup2018.predictor.admin.processing;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import org.hugoandrade.worldcup2018.predictor.admin.data.Country;
import org.hugoandrade.worldcup2018.predictor.admin.data.Match;
import org.hugoandrade.worldcup2018.predictor.admin.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.admin.utils.StaticVariableUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackEndProcessing {

    private final static String TAG = BackEndProcessing.class.getSimpleName();

    private final WeakReference<OnProcessingFinished> mOnProcessingFinished;
    private final List<Country> mCountryList;

    private GroupProcessing mTask;

    public BackEndProcessing(OnProcessingFinished onProcessingFinished, List<Country> allCountryList) {
        mOnProcessingFinished = new WeakReference<>(onProcessingFinished);
        mCountryList = allCountryList;
    }

    public void startUpdateGroupsProcessing(List<Match> matchList) {
        // Do processing asynchronously
        mTask = new GroupProcessing(this, mCountryList, matchList);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    static class GroupProcessing extends AsyncTask<Void, ProgressContainer, ResultContainer> {

        private final WeakReference<BackEndProcessing> mBackEndProcessing;
        private final SparseArray<Match> mMatchMap = new SparseArray<>();
        private final HashMap<String, Country> mCountryMap = new HashMap<>();

        GroupProcessing(BackEndProcessing backEndProcessing, List<Country> countryList, List<Match> matchList) {
            mBackEndProcessing = new WeakReference<>(backEndProcessing);
            for (Country c : countryList)
                mCountryMap.put(c.getID(), c);
            for (Match match : matchList)
                mMatchMap.append(match.getMatchNumber(), match);
        }

        @Override
        protected ResultContainer doInBackground(Void... aVoid) {

            // Create List of countries object, and group them by Group
            HashMap<String, GroupComp> allGroups = setupGroupsMap(mMatchMap, mCountryMap);

            // Order each group
            List<Country> updatedCountryList =  new ArrayList<>();
            for (GroupComp group : allGroups.values()) {
                group.orderGroup();
                updatedCountryList.addAll(group.getCountryList());
            }

            // Find countries whose info changed from the original one.
            for (Country updatedCountry : updatedCountryList) {
                Country originalCountry = mCountryMap.get(updatedCountry.getID());
                if (!updatedCountry.equals(originalCountry))
                    publishProgress(new ProgressContainer(updatedCountry));
            }

            // Put in database only the countries that were whose info was modified
            // Check if all matches of each group have been played. If yes, update the matches
            // of the knockout stage appropriately (The first- and second-place teams in each group)
            for (GroupComp group : allGroups.values()) {
                for (Match match : updateRoundOf16WhenGroupWasPlayed(mMatchMap, group))
                    publishProgress(new ProgressContainer(match));
            }

            for (Match match : updateRemainingKnockOutMatchUps(mMatchMap))
                publishProgress(new ProgressContainer(match));

            return new ResultContainer(toList(mCountryMap), toList(mMatchMap));
        }

        private List<Match> toList(SparseArray<Match> mMatchMap) {
            List<Match> tList = new ArrayList<>();
            for (int i = 0 ; i < mMatchMap.size() ; i++)
                tList.add(mMatchMap.valueAt(i));
            return tList;
        }

        private List<Country> toList(HashMap<String, Country> mCountryMap) {
            return new ArrayList<>(mCountryMap.values());
        }

        @Override
        protected void onProgressUpdate(ProgressContainer... progressContainers) {
            if (mBackEndProcessing.get() == null)
                return;

            for (ProgressContainer progressContainer : progressContainers) {
                if (progressContainer.mCountry != null)
                    mBackEndProcessing.get().onUpdateCountry(progressContainer.mCountry);
                if (progressContainer.mMatch != null)
                    mBackEndProcessing.get().onUpdateMatchUp(progressContainer.mMatch);
            }
        }

        @Override
        protected void onPostExecute(ResultContainer resultContainer) {
            super.onPostExecute(resultContainer);

            if (mBackEndProcessing.get() != null)
                mBackEndProcessing.get().onProcessingFinished(resultContainer.mCountryList,
                                                              resultContainer.mMatchList);
        }

        private HashMap<String, GroupComp> setupGroupsMap(SparseArray<Match> matchMap,
                                                          HashMap<String, Country> countryMap) {

            HashMap<String, GroupComp> allGroups = new HashMap<>();
            // Iterate over all 32 countries. We gonna make a Country object for each one.
            for (Map.Entry<String, Country> c : countryMap.entrySet()) {
                CountryComp countryComp = new CountryComp(c.getValue());

                for (Match m : getGroupStageMatches(matchMap)) {
                    if (m.getHomeTeamID().equals(countryComp.getID())
                            || m.getAwayTeamID().equals(countryComp.getID())) {
                        countryComp.add(m);
                    }
                }

                if (allGroups.containsKey(countryComp.getGroup())) {
                    allGroups.get(countryComp.getGroup()).add(countryComp);
                } else {
                    allGroups.put(countryComp.getGroup(), new GroupComp(countryComp.getGroup()));
                    allGroups.get(countryComp.getGroup()).add(countryComp);
                }
            }

            return allGroups;
        }

        private List<Match> updateRoundOf16WhenGroupWasPlayed(SparseArray<Match> allMatches,
                                                              GroupComp groupComp) {
            String group = groupComp.getGroup();
            // Check if all countries have played 3 matches
            boolean areAllMatchesPlayed =
                    groupComp.areAllMatchesPlayed();

            int winnerMatchUp = -1;
            int runnerUpMatchUp = -1;
            ListContainer<Match> matchListContainer = new ListContainer<>();
            // Update knockout mStage matches only if necessary
            switch (group) {
                case "A": {
                    winnerMatchUp = 49;
                    runnerUpMatchUp = 51;
                    break;
                }
                case "B": {
                    winnerMatchUp = 51;
                    runnerUpMatchUp = 49;
                    break;
                }
                case "C": {
                    winnerMatchUp = 50;
                    runnerUpMatchUp = 52;
                    break;
                }
                case "D": {
                    winnerMatchUp = 52;
                    runnerUpMatchUp = 50;
                    break;
                }
                case "E": {
                    winnerMatchUp = 53;
                    runnerUpMatchUp = 55;
                    break;
                }
                case "F": {
                    winnerMatchUp = 55;
                    runnerUpMatchUp = 53;
                    break;
                }
                case "G": {
                    winnerMatchUp = 54;
                    runnerUpMatchUp = 56;
                    break;
                }
                case "H": {
                    winnerMatchUp = 56;
                    runnerUpMatchUp = 54;
                    break;
                }
            }
            if (areAllMatchesPlayed) {
                matchListContainer.add(
                        updateRoundOf16MatchUp(allMatches.get(winnerMatchUp), groupComp.get(0).getID(), "HOME"));
                matchListContainer.add(
                        updateRoundOf16MatchUp(allMatches.get(runnerUpMatchUp), groupComp.get(1).getID(), "AWAY"));
            } else {
                matchListContainer.add(
                        updateRoundOf16MatchUp(allMatches.get(winnerMatchUp), "Winner Group " + group, "HOME"));
                matchListContainer.add(
                        updateRoundOf16MatchUp(allMatches.get(runnerUpMatchUp), "Runner-up Group " + group, "AWAY"));
            }

            return matchListContainer.getList();
        }

        private Match updateRoundOf16MatchUp(Match match, String teamID, String matchUpPosition) {
            if (match == null)
                return null;
            // Check if there is any need to update match-up
            if (matchUpPosition.equals("HOME") && match.getHomeTeamID().equals(teamID))
                return null;
            if (matchUpPosition.equals("AWAY") && match.getAwayTeamID().equals(teamID))
                return null;

            // Update match-up accordingly
            switch (matchUpPosition) {
                case "HOME":
                    match.setHomeTeamID(teamID);
                    break;
                case "AWAY":
                    match.setAwayTeamID(teamID);
                    break;
                default:
                    Log.e(TAG, "MatchUp position (home or away) not recognized");
                    return null;
            }

            return match;
        }

        private List<Match> updateRemainingKnockOutMatchUps(SparseArray<Match> allMatches) {

            ListContainer<Match> matchListContainer = new ListContainer<>();
            // Quarter Finals
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 49, 57, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 50, 57, "AWAY"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 53, 58, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 54, 58, "AWAY"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 51, 59, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 52, 59, "AWAY"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 55, 60, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 56, 60, "AWAY"));

            // Semi Finals
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 57, 61, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 58, 61, "AWAY"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 59, 62, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 60, 62, "AWAY"));

            // 3rd Playoff
            matchListContainer.add(updateKnockOutMatchUpFor3rdPlace(allMatches, 61, 63, "HOME"));
            matchListContainer.add(updateKnockOutMatchUpFor3rdPlace(allMatches, 62, 63, "AWAY"));

            // Final
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 61, 64, "HOME"));
            matchListContainer.add(updateKnockOutMatchUp(allMatches, 62, 64, "AWAY"));

            return matchListContainer.getList();
        }

        private Match updateKnockOutMatchUp(SparseArray<Match> allMatches,
                                            int matchUpNumber,
                                            int matchUpToUpdate,
                                            String matchUpToUpdatePosition) {
            Match match = allMatches.get(matchUpNumber);

            String teamName;
            if (!MatchUtils.isMatchPlayed(match) && MatchUtils.didTeamsTied(match)) {
                teamName = "Winner Match " + Integer.toString(matchUpNumber);
            }
            else {
                if (MatchUtils.didHomeTeamWin(match) || MatchUtils.didHomeTeamWinByPenaltyShootout(match))
                    teamName = match.getHomeTeamID();
                else if (MatchUtils.didAwayTeamWin(match) || MatchUtils.didAwayTeamWinByPenaltyShootout(match))
                    teamName = match.getAwayTeamID();
                else
                    teamName = "Winner Match " + Integer.toString(matchUpNumber);
            }

            Match matchToUpdate = allMatches.get(matchUpToUpdate);

            if (matchToUpdate == null)
                return null;

            if (matchUpToUpdatePosition.equals("AWAY")) {
                if (!matchToUpdate.getAwayTeamID().equals(teamName)) {
                    matchToUpdate.setAwayTeamID(teamName);
                    return matchToUpdate;
                }
            } else if (matchUpToUpdatePosition.equals("HOME")) {
                if (!matchToUpdate.getHomeTeamID().equals(teamName)) {
                    matchToUpdate.setHomeTeamID(teamName);
                    return matchToUpdate;
                }
            }
            return null;
        }

        private Match updateKnockOutMatchUpFor3rdPlace(SparseArray<Match> allMatches,
                                                       int matchUpNumber,
                                                       @SuppressWarnings("SameParameterValue") int matchUpToUpdate,
                                                       String matchUpToUpdatePosition) {
            Match match = allMatches.get(matchUpNumber);

            String teamName;
            if (!MatchUtils.isMatchPlayed(match) && MatchUtils.didTeamsTied(match)) {
                teamName = "Loser Match " + Integer.toString(matchUpNumber);
            }
            else {
                if (MatchUtils.didHomeTeamWin(match) || MatchUtils.didHomeTeamWinByPenaltyShootout(match))
                    teamName = match.getAwayTeamID();
                else if (MatchUtils.didAwayTeamWin(match) || MatchUtils.didAwayTeamWinByPenaltyShootout(match))
                    teamName = match.getHomeTeamID();
                else
                    teamName = "Loser Match " + Integer.toString(matchUpNumber);
            }

            Match matchToUpdate = allMatches.get(matchUpToUpdate);

            if (matchToUpdate == null)
                return null;

            if (matchUpToUpdatePosition.equals("AWAY")) {
                if (!matchToUpdate.getAwayTeamID().equals(teamName)) {
                    matchToUpdate.setAwayTeamID(teamName);
                    return matchToUpdate;
                }
            } else if (matchUpToUpdatePosition.equals("HOME")) {
                if (!matchToUpdate.getHomeTeamID().equals(teamName)) {
                    matchToUpdate.setHomeTeamID(teamName);
                    return matchToUpdate;
                }
            }
            return null;
        }

        private static List<Match> getGroupStageMatches(SparseArray<Match> matchMap) {
            List<Match> allGroupStageMatches = new ArrayList<>();
            for (int i = 0 ; i < matchMap.size() ; i++) {
                Match match = matchMap.valueAt(i);
                if (match.getStage().equals(StaticVariableUtils.SStage.groupStage.name))
                    allGroupStageMatches.add(match);
            }
            return allGroupStageMatches;
        }
    }

    private void onUpdateMatchUp(Match match) {
        if (mOnProcessingFinished.get() != null)
            mOnProcessingFinished.get().updateMatchUp(match);
    }

    private void onUpdateCountry(Country country) {
        if (mOnProcessingFinished.get() != null)
            mOnProcessingFinished.get().updateCountry(country);
    }

    private void onProcessingFinished(List<Country> countryList, List<Match> matchList) {
        if (mOnProcessingFinished.get() != null)
            mOnProcessingFinished.get().onProcessingFinished(countryList, matchList);
    }

    public void cancel() {
        mTask.cancel(true);
        mTask = null;
    }

    public interface OnProcessingFinished {
        void onProcessingFinished(List<Country> countryList, List<Match> matchList);
        void updateCountry(Country country);
        void updateMatchUp(Match match);
    }
}
