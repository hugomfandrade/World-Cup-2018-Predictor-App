package org.hugoandrade.worldcup2018.predictor;

import androidx.core.util.Pair;
import android.util.Log;
import android.util.SparseArray;

import org.hugoandrade.worldcup2018.predictor.data.LeagueWrapper;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.data.User;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StaticVariableUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalData {

    private static final String TAG = GlobalData.class.getSimpleName();

    private static GlobalData mInstance = null;

    public User user;
    public SystemData systemData;

    private Set<OnMatchesChangedListener> mOnMatchesChangedListenerSet = new HashSet<>();
    private Set<OnCountriesChangedListener> mOnCountriesChangedListenerSet = new HashSet<>();
    private Set<OnPredictionsChangedListener> mOnPredictionsChangedListenerSet = new HashSet<>();
    private Set<OnLeaguesChangedListener> mOnLeaguesChangedListenerSet = new HashSet<>();

    private List<Country> mCountryList = new ArrayList<>();
    private List<Match> mMatchList = new ArrayList<>();
    private List<Prediction> mPredictionList = new ArrayList<>();
    private List<LeagueWrapper> mLeagueWrapperList = new ArrayList<>();

    // LeagueID, Map Stage - LeagueWrapper
    private Map<String, SparseArray<LeagueWrapper>> mLeagueWrapperMap = new HashMap<>();
    // UserID, list of matches whose predictions where fetched
    //private Map<String, SparseIntArray> mPredictionOfUserMap = new HashMap<>();
    // UserID, Map MatchNumber - predictions
    private Map<String, SparseArray<Prediction>> mMatchPredictionMap = new HashMap<>();
    private boolean mHasFetchedInfo = false;
    private long pastCurrentTimeInMillis;

    /*public static GlobalData getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(TAG + " is not initialized");
        }
        return mInstance;
    }/**/

    public static GlobalData getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalData();
        }
        return mInstance;
    }

    public static void unInitialize() {
        if (mInstance == null) {
            return;
        }
        try {
            mInstance.user = null;
            mInstance.systemData = null;
            mInstance.mHasFetchedInfo = false;
            mInstance.pastCurrentTimeInMillis = 0L;

            clear(mInstance.mCountryList,
                    mInstance.mMatchList,
                    mInstance.mPredictionList,
                    mInstance.mLeagueWrapperList
            );

            clear(mInstance.mOnMatchesChangedListenerSet,
                    mInstance.mOnCountriesChangedListenerSet,
                    mInstance.mOnPredictionsChangedListenerSet,
                    mInstance.mOnLeaguesChangedListenerSet);

            clear(mInstance.mLeagueWrapperMap,
                    mInstance.mMatchPredictionMap);

        } catch (IllegalStateException e) {
            Log.e(TAG, "unInitialize error: " + e.getMessage());
        }
    }

    public void resetInfo() {
        mCountryList.clear();
        mMatchList.clear();
        mPredictionList.clear();
        mLeagueWrapperList.clear();
        mLeagueWrapperMap.clear();
        mMatchPredictionMap.clear();
        mHasFetchedInfo = false;
    }

    public Calendar getServerTime() {
        return systemData.getDate();
    }

    public void setSystemData(SystemData systemData) {
        this.systemData = systemData;
        this.pastCurrentTimeInMillis = System.currentTimeMillis();
    }

    public void updateServerTime() {
        if (pastCurrentTimeInMillis != 0L) {
            systemData.add(System.currentTimeMillis() - pastCurrentTimeInMillis);
            pastCurrentTimeInMillis = System.currentTimeMillis();
        }
    }

    public boolean wasLastFetchMoreThanFiveMinutesAgo() {
        return pastCurrentTimeInMillis == 0L || (System.currentTimeMillis() - pastCurrentTimeInMillis) > 5 * 60 * 1000;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLeagues(List<LeagueWrapper> leagueWrapperList) {

        mLeagueWrapperList = leagueWrapperList;

        if (mLeagueWrapperList == null)
            mLeagueWrapperList = new ArrayList<>();
        else {
            for (LeagueWrapper leagueWrapper : leagueWrapperList) {

                if (!mLeagueWrapperMap.containsKey(leagueWrapper.getLeague().getID())) {
                    mLeagueWrapperMap.put(leagueWrapper.getLeague().getID(), new SparseArray<LeagueWrapper>());
                }
                mLeagueWrapperMap.get(leagueWrapper.getLeague().getID()).put(0, leagueWrapper);
            }/**/
            //mLeagueWrapperList.add(LeagueWrapper.createOverall(mUserList));
        }

        for (OnLeaguesChangedListener listener : mOnLeaguesChangedListenerSet) {
            listener.onLeaguesChanged();
        }
    }

    public List<LeagueWrapper> getLeagues() {
        return mLeagueWrapperList;
    }

    public List<Prediction> getPredictionList() {
        return mPredictionList;
    }

    public void setPredictionList(List<Prediction> predictionList) {
        this.mPredictionList = predictionList;

        setPredictionsOfUser(user, mPredictionList, 1, 51);

        for (OnPredictionsChangedListener listener : mOnPredictionsChangedListenerSet) {
            listener.onPredictionsChanged();
        }
    }

    public void updatePrediction(Prediction prediction) {

        boolean isUpdated = false;
        for (int l = 0; l < mPredictionList.size() ; l++) {
            if (mPredictionList.get(l).getMatchNumber() == prediction.getMatchNumber()) {
                mPredictionList.set(l, prediction);
                isUpdated = true;
            }
        }

        if (!isUpdated) {
            mPredictionList.add(prediction);
        }

        updatePredictionOfUser(user, prediction);

        /*for (OnPredictionsChangedListener listener : mOnPredictionsChangedListenerSet) {
            listener.onPredictionsChanged();
        }/**/
    }

    public List<Country> getCountryList() {
        return mCountryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.mCountryList = countryList;

        for (OnCountriesChangedListener listener : mOnCountriesChangedListenerSet) {
            listener.onCountriesChanged();
        }
    }

    public List<Match> getMatchList() {
        return mMatchList;
    }

    public void setMatchList(List<Match> matchList) {
        this.mMatchList = matchList;

        Collections.sort(mMatchList, new Comparator<Match>() {
            @Override
            public int compare(Match o1, Match o2) {
                if ((o1 == null || o1.getDateAndTime() == null) &&
                        (o2 == null || o2.getDateAndTime() == null))
                    return 0;
                if (o1 == null || o1.getDateAndTime() == null)
                    return -1;
                if (o2 == null || o2.getDateAndTime() == null)
                    return 1;
                return o1.getDateAndTime().before(o2.getDateAndTime()) ? -1 : 1;
            }
        });

        for (OnMatchesChangedListener listener : mOnMatchesChangedListenerSet) {
            listener.onMatchesChanged();
        }
    }

    public Country getCountry(Country country) {
        if (country == null) return null;
        for (Country c : mCountryList) {
            if (c.getID().equals(country.getID())) {
                return c;
            }
        }
        return null;
    }

    public Match getMatch(int matchNumber) {
        for (Match m : mMatchList) {
            if (m.getMatchNumber() == matchNumber) {
                return m;
            }
        }
        return null;
    }

    public List<Match> getMatchList(Country country) {
        if (country == null || country.getName() == null) return new ArrayList<>();
        List<Match> matchList = new ArrayList<>();
        for (Match m : mMatchList) {
            if (country.getName().equals(m.getHomeTeamName())) {
                matchList.add(m);
            }
            if (country.getName().equals(m.getAwayTeamName())) {
                matchList.add(m);
            }
        }
        return matchList;
    }

    public List<Match> getMatchList(int minMatchNumber, int maxMatchNumber) {
        return MatchUtils.getMatchList(mMatchList, minMatchNumber, maxMatchNumber);
    }

    public List<Match> getPlayedMatchList() {
        return MatchUtils.getPlayedMatchList(mMatchList, getServerTime().getTime(), 1, 62);
    }

    public List<Match> getPlayedMatchList(int minMatchNumber, int maxMatchNumber) {
        return MatchUtils.getPlayedMatchList(mMatchList, getServerTime().getTime(), minMatchNumber, maxMatchNumber);
    }

    public List<Match> getMatchList(StaticVariableUtils.SStage stage) {
        return MatchUtils.getMatchList(mMatchList, stage);
    }

    public List<Match> getMatchList(StaticVariableUtils.SStage stage, int matchday) {
        return MatchUtils.getMatchList(mMatchList, stage, matchday);
    }

    public List<Country> getCountryList(Country country) {
        if (country == null || country.getGroup() == null) return new ArrayList<>();
        List<Country> countryList = new ArrayList<>();
        for (Country c : mCountryList) {
            if (country.getGroup().equals(c.getGroup())) {
                countryList.add(c);
            }
        }

        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                return o1.getPosition() - o2.getPosition();
            }
        });
        return countryList;
    }

    public void setPredictionsOfUsers(List<User> userList,
                                      List<Prediction> predictionList,
                                      int fromMatchNumber,
                                      int toMatchNumber) {
        for (User user : userList) {
            setPredictionsOfUser(user, predictionList, fromMatchNumber, toMatchNumber);
        }
    }

    public void setPredictionsOfUser(User user,
                                     List<Prediction> predictionList,
                                     int fromMatchNumber,
                                     int toMatchNumber) {
        for (int matchNumber = fromMatchNumber ; matchNumber <= toMatchNumber ; matchNumber++) {
            setPredictionsOfUser(matchNumber, user, predictionList);
        }
    }

    public void setPredictionsOfUsers(int matchNumber, List<User> userList, List<Prediction> predictionList) {
        for (User user : userList) {
            setPredictionsOfUser(matchNumber, user, predictionList);
        }
    }

    public void setPredictionsOfUser(int matchNumber, User user, List<Prediction> predictionList) {
        if (user == null) return;
        String userID = user.getID();

        Prediction prediction = null;
        for (Prediction p : predictionList) {
            if (p.getMatchNumber() == matchNumber && userID.equals(p.getUserID())) {
                prediction = p;
            }
        }
        if (prediction == null) {
            prediction = Prediction.emptyInstance(matchNumber, userID);
        }

        if (mMatchPredictionMap.containsKey(userID)) {
            mMatchPredictionMap.get(userID).put(matchNumber, prediction);
        } else {
            mMatchPredictionMap.put(userID, new SparseArray<Prediction>());
            mMatchPredictionMap.get(userID).put(matchNumber, prediction);
        }
    }

    public void updatePredictionOfUser(User user, Prediction prediction) {
        if (user == null || prediction == null || !user.getID().equals(prediction.getUserID())) return;

        String userID = user.getID();

        if (mMatchPredictionMap.containsKey(userID)) {
            mMatchPredictionMap.get(userID).put(prediction.getMatchNumber(), prediction);
        } else {
            mMatchPredictionMap.put(userID, new SparseArray<Prediction>());
            mMatchPredictionMap.get(userID).put(prediction.getMatchNumber(), prediction);
        }
    }

    public List<Prediction> getPredictionsOfUser(String userID) {
        if (mMatchPredictionMap.containsKey(userID)) {
            return toList(mMatchPredictionMap.get(userID));
        } else {
            return new ArrayList<>();
        }
    }

    private static <T> List<T> toList(SparseArray<T> predictionSparseArray) {
        if (predictionSparseArray == null)
            return new ArrayList<>();
        List<T> predictionList = new ArrayList<>(predictionSparseArray.size());
        for (int i = 0; i < predictionSparseArray.size(); i++)
            predictionList.add(predictionSparseArray.valueAt(i));
        return predictionList;
    }


    public List<Pair<User, Prediction>> getPredictionsOfUsers(int matchNumber, List<User> userList) {
        List<Pair<User, Prediction>> m = new ArrayList<>();

        for (User user : userList) {
            Prediction defaultPrediction = Prediction.emptyInstance(matchNumber, user.getID());
            Prediction p = !mMatchPredictionMap.containsKey(user.getID())?
                    defaultPrediction :
                    mMatchPredictionMap.get(user.getID()).get(matchNumber, defaultPrediction);

            m.add(new Pair<>(user, p));
        }
        return m;
    }

    public boolean wasPredictionFetched(User user, int matchNumber) {
        return mMatchPredictionMap.containsKey(user.getID()) && mMatchPredictionMap.get(user.getID()).get(matchNumber) != null;
    }

    public void addLeague(LeagueWrapper leagueWrapper) {

        mLeagueWrapperList.add(0, leagueWrapper);

        for (OnLeaguesChangedListener listener : mOnLeaguesChangedListenerSet) {
            listener.onLeaguesChanged();
        }

    }

    public void addUsersToLeague(String leagueID, List<LeagueUser> userList) {

        for (LeagueWrapper leagueWrapper : mLeagueWrapperList) {
            if (leagueWrapper.getLeague().getID().equals(leagueID)) {
                for (LeagueUser newUser : userList) {
                    if (leagueWrapper.getLeagueUserList().size() == 20)
                        continue;

                    boolean isUserOnList = false;
                    for (LeagueUser user : leagueWrapper.getLeagueUserList()) {
                        if (user.getUser().getID().equals(newUser.getUser().getID())) {
                            isUserOnList = true;
                            break;
                        }
                    }

                    if (!isUserOnList) {
                        leagueWrapper.getLeagueUserList().add(newUser);
                    }

                }

                Collections.sort(leagueWrapper.getLeagueUserList(), new Comparator<LeagueUser>() {
                    @Override
                    public int compare(LeagueUser o1, LeagueUser o2) {
                        return o1.getRank() - o2.getRank();
                    }
                });

                for (OnLeaguesChangedListener listener : mOnLeaguesChangedListenerSet) {
                    listener.onLeaguesChanged();
                }
            }
        }
    }

    public synchronized void removeLeague(LeagueWrapper leagueWrapper) {

        List<Integer> toRemoveIs = new ArrayList<>();
        for (int i = 0 ; i < mLeagueWrapperList.size() ; i++) {
            LeagueWrapper l = mLeagueWrapperList.get(i);
            if (l == null || l.getLeague() == null || l.getLeague().getID() == null) {
                toRemoveIs.add(i);
            }
            else if (leagueWrapper != null && leagueWrapper.getLeague() != null &&
                    l.getLeague().getID().equals(leagueWrapper.getLeague().getID())) {
                toRemoveIs.add(i);
            }
        }

        if (toRemoveIs.size() > 0) {
            for (Integer i : toRemoveIs) {
                mLeagueWrapperList.remove(i.intValue());
            }

            for (OnLeaguesChangedListener listener : mOnLeaguesChangedListenerSet) {
                listener.onLeaguesChanged();
            }
        }

    }

    public void setHasFetchedInfo(boolean hasFetchedInfo) {
        mHasFetchedInfo = hasFetchedInfo;
    }

    public boolean hasFetchedInfo() {
        return mHasFetchedInfo;
    }

    public void setLeagueWrapperByStage(LeagueWrapper leagueWrapper, int stage) {
        if (leagueWrapper == null
                || leagueWrapper.getLeague() == null
                || leagueWrapper.getLeague().getID() == null) {
            return;
        }
        String leagueID = leagueWrapper.getLeague().getID();
        if (!mLeagueWrapperMap.containsKey(leagueID)) {
            mLeagueWrapperMap.put(leagueID, new SparseArray<LeagueWrapper>());
        }
        mLeagueWrapperMap.get(leagueID).put(stage, leagueWrapper);
    }

    public void addUsersToLeagueByStage(String leagueID, List<LeagueUser> userList, int stage) {

        if (!mLeagueWrapperMap.containsKey(leagueID) ||
                mLeagueWrapperMap.get(leagueID).get(stage) == null) {
            return;
        }
        LeagueWrapper leagueWrapper = mLeagueWrapperMap.get(leagueID).get(stage);

        for (LeagueUser newUser : userList) {
            if (leagueWrapper.getLeagueUserList().size() == 20)
                continue;

            boolean isUserOnList = false;
            for (LeagueUser user : leagueWrapper.getLeagueUserList()) {
                if (user.getUser().getID().equals(newUser.getUser().getID())) {
                    isUserOnList = true;
                    break;
                }
            }

            if (!isUserOnList) {
                leagueWrapper.getLeagueUserList().add(newUser);
            }

        }

        Collections.sort(leagueWrapper.getLeagueUserList(), new Comparator<LeagueUser>() {
            @Override
            public int compare(LeagueUser o1, LeagueUser o2) {
                return o1.getRank() - o2.getRank();
            }
        });
    }

    public LeagueWrapper getLeagueByStage(String leagueID, int stage) {

        if (!mLeagueWrapperMap.containsKey(leagueID) ||
                mLeagueWrapperMap.get(leagueID).get(stage) == null) {
            return null;
        }

        return mLeagueWrapperMap.get(leagueID).get(stage);
    }

    public interface OnMatchesChangedListener {
        void onMatchesChanged();
    }

    public interface OnCountriesChangedListener {
        void onCountriesChanged();
    }

    public interface OnPredictionsChangedListener {
        void onPredictionsChanged();
    }

    public interface OnLeaguesChangedListener {
        void onLeaguesChanged();
    }

    public void addOnMatchesChangedListener(OnMatchesChangedListener listener) {
        if (!mOnMatchesChangedListenerSet.contains(listener))
            mOnMatchesChangedListenerSet.add(listener);
    }

    public void removeOnMatchesChangedListener(OnMatchesChangedListener listener) {
        mOnMatchesChangedListenerSet.remove(listener);
    }

    public void addOnCountriesChangedListener(OnCountriesChangedListener listener) {
        if (!mOnCountriesChangedListenerSet.contains(listener))
            mOnCountriesChangedListenerSet.add(listener);
    }

    public void removeOnCountriesChangedListener(OnCountriesChangedListener listener) {
        mOnCountriesChangedListenerSet.remove(listener);
    }

    public void addOnPredictionsChangedListener(OnPredictionsChangedListener listener) {
        if (!mOnPredictionsChangedListenerSet.contains(listener))
            mOnPredictionsChangedListenerSet.add(listener);
    }

    public void removeOnPredictionsChangedListener(OnPredictionsChangedListener listener) {
        mOnPredictionsChangedListenerSet.remove(listener);
    }

    public void addOnLeaguesChangedListener(OnLeaguesChangedListener listener) {
        if (!mOnLeaguesChangedListenerSet.contains(listener))
            mOnLeaguesChangedListenerSet.add(listener);
    }

    public void removeOnLeaguesChangedListener(OnLeaguesChangedListener listener) {
        mOnLeaguesChangedListenerSet.remove(listener);
    }

    private static void clear(List<?> ... lists) {
        for (List<?> list : lists) {
            list.clear();
        }
    }

    private static void clear(Set<?> ... sets) {
        for (Set<?> set : sets) {
            set.clear();
        }
    }

    private static void clear(Map<?,?> ... maps) {
        for (Map<?,?> map : maps) {
            map.clear();
        }
    }
}
