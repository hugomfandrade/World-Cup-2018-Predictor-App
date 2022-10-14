package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import javafx.util.Pair;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.league.League;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUser;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeagueUsersScoreProcessing {

    private final static String TAG = LeagueUsersScoreProcessing.class.getSimpleName();

    private OnProcessingListener mOnProcessingFinished;

    private UpdateScoreProcessing mTask;

    private ExecutorService mExecutors;

    public void setListener(OnProcessingListener onProcessingListener) {
        mOnProcessingFinished = onProcessingListener;
    }

    public void startUpdateUsersScoresSync(League[] leagues, LeagueUser[] leagueUsers, AccountDto[] accounts) {
        // Do processing synchronously
        mTask = new UpdateScoreProcessing(leagues, leagueUsers, accounts, mOnProcessingFinished);
        mTask.run();
    }

    public void startUpdateUsersScoresAsync(League[] leagues, LeagueUser[] leagueUsers, AccountDto[] accounts) {
        // Do processing asynchronously
        mTask = new UpdateScoreProcessing(leagues, leagueUsers, accounts, mOnProcessingFinished);
        mExecutors = Executors.newCachedThreadPool();
        mExecutors.submit(mTask);
    }

    public void cancel() {
        if (mExecutors != null) mExecutors.shutdownNow();
        mExecutors = null;
        mTask = null;
    }

    public static class UpdateScoreProcessing implements Runnable {

        private final WeakReference<OnProcessingListener> mOnProcessingListener;
        private final League[] mLeagues;
        private final LeagueUser[] mLeagueUsers;
        private final AccountDto[] mAccounts;

        UpdateScoreProcessing(League[] leagues, LeagueUser[] leagueUsers, AccountDto[] accounts, OnProcessingListener onProcessingListener) {
            mOnProcessingListener = new WeakReference<>(onProcessingListener);
            mLeagues = leagues;
            mLeagueUsers = leagueUsers;
            mAccounts = accounts;
        }

        @Override
        public void run() {

            final Map<String, League> leagues = Stream.of(mLeagues)
                    .collect(Collectors.toMap(League::getID, league -> league));
            final Map<String, AccountDto> accounts = Stream.of(mAccounts)
                    .collect(Collectors.toMap(AccountDto::getId, account -> account));

            final Map<League, List<Pair<LeagueUser, AccountDto>>> leagueUsersByLeague = Arrays.stream(mLeagueUsers)
                    // check if league exists
                    .filter(leagueUser -> leagues.containsKey(leagueUser.getLeagueID()))
                    .map(leagueUser -> new Pair<>(leagueUser, accounts.getOrDefault(leagueUser.getUserID(), null)))
                    // check if account exists
                    .filter(leagueUser -> leagueUser.getValue() != null)
                    // group by league
                    .collect(Collectors.groupingBy(leagueUser -> leagues.get(leagueUser.getKey().getLeagueID())));


            for (Map.Entry<League, List<Pair<LeagueUser, AccountDto>>> leagueEntry : leagueUsersByLeague.entrySet()) {

                final League league = leagueEntry.getKey();
                final List<Pair<LeagueUser, AccountDto>> leagueUsers = leagueEntry.getValue();

                leagueUsers.sort(Comparator.comparingInt((Pair<LeagueUser, AccountDto> o) -> o.getValue().getScore()).reversed()
                        .thenComparing(o -> o.getValue().getId(), Comparator.reverseOrder()));

                for (int i = 0 ; i < leagueUsers.size() ; i++) {

                    final LeagueUser leagueUser = leagueUsers.get(i).getKey();
                    final AccountDto account = leagueUsers.get(i).getValue();
                    final int previousRank = leagueUser.getRank();
                    final int rank = i + 1;

                    if (i == 0 || account.getScore() != leagueUsers.get(i - 1).getValue().getScore()) {
                        leagueUser.setRank(rank);
                    }
                    else {
                        leagueUser.setRank(leagueUsers.get(i - 1).getValue().getRank());
                    }

                    // notify if different
                    if (previousRank != leagueUser.getRank()) {
                        Optional.ofNullable(mOnProcessingListener.get())
                                .ifPresent(l -> l.updateLeagueUser(leagueUser));
                    }
                }

            }

            Optional.ofNullable(mOnProcessingListener.get())
                    .ifPresent(l -> l.onProcessingFinished(Arrays.asList(mLeagueUsers)));
        }
    }

    public interface OnProcessingListener {
        void onProcessingFinished(List<LeagueUser> leagueUsers);
        void updateLeagueUser(LeagueUser leagueUser);
    }
}
