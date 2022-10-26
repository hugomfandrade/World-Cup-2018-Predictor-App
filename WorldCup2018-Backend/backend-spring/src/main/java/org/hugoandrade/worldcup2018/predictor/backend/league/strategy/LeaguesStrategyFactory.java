package org.hugoandrade.worldcup2018.predictor.backend.league.strategy;

import org.hugoandrade.worldcup2018.predictor.backend.league.LeaguesService;

public interface LeaguesStrategyFactory {

    GetLeagues getLeaguesStrategy(LeaguesService leaguesService);

    GetLeagueUsers getLeagueUsersStrategy(LeaguesService leaguesService);

    public static class Mongo implements LeaguesStrategyFactory {

        @Override
        public GetLeagues getLeaguesStrategy(LeaguesService leaguesService) {
            return new GetLeagues.SimpleRankedMongo(leaguesService);
        }

        @Override
        public GetLeagueUsers getLeagueUsersStrategy(LeaguesService leaguesService) {
            return new GetLeagueUsers.SimpleRankedMongo(leaguesService);
        }
    }

    public static class Jpa implements LeaguesStrategyFactory {

        @Override
        public GetLeagues getLeaguesStrategy(LeaguesService leaguesService) {
            return new GetLeagues.SimpleRankedJpa(leaguesService);
        }

        @Override
        public GetLeagueUsers getLeagueUsersStrategy(LeaguesService leaguesService) {
            return new GetLeagueUsers.SimpleRankedJpa(leaguesService);
        }
    }
}
