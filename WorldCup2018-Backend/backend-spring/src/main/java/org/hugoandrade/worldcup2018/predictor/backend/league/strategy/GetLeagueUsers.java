package org.hugoandrade.worldcup2018.predictor.backend.league.strategy;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeaguesService;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class GetLeagueUsers {

    private final static LeagueUser EMPTY_USER = new LeagueUser(null, -1);

    protected final LeaguesService leaguesService;

    protected GetLeagueUsers(LeaguesService leaguesService) {
        this.leaguesService = leaguesService;
    }

    public List<Account> getLeagueUsers(String leagueID) {
        return this.getLeagueUsers(leagueID, Pageable.unpaged());
    }

    public abstract List<Account> getLeagueUsers(String leagueID, Pageable pageable);


    public static class SimpleRankedMongo extends GetLeagueUsers {

        public SimpleRankedMongo(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<Account> getLeagueUsers(String leagueID, Pageable pageable) {

            final Map<String, LeagueUser> leaguesUsersMap = leaguesService.leagueUserRepository.findAllByLeagueID(leagueID)
                    .stream()
                    .collect(Collectors.toMap(LeagueUser::getUserID, Function.identity()));

            List<Account> leagueUsers = leaguesService.accountRepository.findAllByIdIn(leaguesUsersMap.keySet(), pageable);

            return leagueUsers.stream()
                    .peek(account -> account.setRank(leaguesUsersMap.getOrDefault(account.getId(), EMPTY_USER).getRank()))
                    .filter(account -> account.getRank() != -1)
                    .collect(Collectors.toList());
        }
    }

    public static class SimpleRankedJpa extends GetLeagueUsers {

        public SimpleRankedJpa(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<Account> getLeagueUsers(String leagueID, Pageable pageable) {

            final List<Account> leagueUsers = leaguesService.accountRepository.findAllByLeagueID(leagueID, pageable);

            final Map<String, LeagueUser> leaguesUsersMap = leaguesService.leagueUserRepository.findAllByLeagueID(leagueID)
                    .stream()
                    .collect(Collectors.toMap(LeagueUser::getUserID, Function.identity()));

            return leagueUsers.stream()
                    .filter(account -> leaguesUsersMap.containsKey(account.getId()))
                    .peek(account -> account.setRank(leaguesUsersMap.get(account.getId()).getRank()))
                    .collect(Collectors.toList());
        }
    }

    @Deprecated
    public static class Simple extends GetLeagueUsers {

        public Simple(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<Account> getLeagueUsers(String leagueID, Pageable pageable) {

            List<LeagueUser> leagueUsers = leaguesService.leagueUserRepository.findAllByLeagueID(leagueID);

            List<String> accountIDs = leagueUsers.stream()
                    .map(LeagueUser::getUserID)
                    .collect(Collectors.toList());

            return leaguesService.accountRepository.findAllByIdInOrderByScoreDesc(accountIDs);
        }
    }

    @Deprecated
    public static class Query extends GetLeagueUsers {

        public Query(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<Account> getLeagueUsers(String leagueID, Pageable pageable) {
            return leaguesService.accountRepository.findAllByLeagueID(leagueID);
        }
    }

    @Deprecated
    public static class Relations extends GetLeagueUsers {

        public Relations(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<Account> getLeagueUsers(String leagueID, Pageable pageable) {

            List<LeagueUser> leagueUsers = leaguesService.leagueRepository.findById(leagueID).get().getLeagueUsers();

            return leagueUsers.stream()
                    .map(LeagueUser::getAccount)
                    .collect(Collectors.toList());
        }
    }
}
