package org.hugoandrade.worldcup2018.predictor.backend.league.strategy;

import org.hugoandrade.worldcup2018.predictor.backend.league.League;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeaguesService;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class GetLeagues {

    protected final LeaguesService leaguesService;

    protected GetLeagues(LeaguesService leaguesService) {
        this.leaguesService = leaguesService;
    }

    public List<League> getLeagues(String userID) {
        return this.getLeagues(userID, Pageable.unpaged());
    }

    public abstract List<League> getLeagues(String userID, Pageable pageable);


    public static class SimpleRankedMongo extends GetLeagues {

        public SimpleRankedMongo(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<League> getLeagues(String userID, Pageable pageable) {

            List<LeagueUser> leagueUsers = leaguesService.leagueUserRepository.findAllByUserID(userID, pageable);

            List<String> leagueIDs = leagueUsers.stream()
                    .map(LeagueUser::getLeagueID)
                    .collect(Collectors.toList());

            return StreamSupport.stream(leaguesService.leagueRepository.findAllById(leagueIDs).spliterator(), false)
                    .collect(Collectors.toList());
        }
    }

    public static class SimpleRankedJpa extends GetLeagues {

        public SimpleRankedJpa(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<League> getLeagues(String userID, Pageable pageable) {

            List<LeagueUser> leagueUsers = leaguesService.leagueUserRepository.findAllByUserID(userID, pageable);

            List<String> leagueIDs = leagueUsers.stream()
                    .map(LeagueUser::getLeagueID)
                    .collect(Collectors.toList());

            return StreamSupport.stream(leaguesService.leagueRepository.findAllById(leagueIDs).spliterator(), false)
                    .collect(Collectors.toList());
        }
    }

    @Deprecated
    public static class Simple extends GetLeagues {

        public Simple(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<League> getLeagues(String userID, Pageable pageable) {
            List<LeagueUser> leagueUsers = leaguesService.leagueUserRepository.findAllByUserID(userID);

            List<String> leagueIDs = leagueUsers.stream()
                    .map(LeagueUser::getLeagueID)
                    .collect(Collectors.toList());

            return StreamSupport.stream(leaguesService.leagueRepository.findAllById(leagueIDs).spliterator(), false)
                    .collect(Collectors.toList());
        }
    }

    @Deprecated
    public static class Query extends GetLeagues {

        public Query(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<League> getLeagues(String userID, Pageable pageable) {
            return leaguesService.leagueRepository.findAllByUserID(userID);
        }
    }

    @Deprecated
    public static class Relations extends GetLeagues {

        public Relations(LeaguesService leaguesService) {
            super(leaguesService);
        }

        @Override
        public List<League> getLeagues(String userID, Pageable pageable) {
            return leaguesService.leagueUserRepository.findAllByUserID(userID)
                    .stream()
                    .map(LeagueUser::getLeague)
                    .collect(Collectors.toList());
        }
    }
}
