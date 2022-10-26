package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LeagueUserMongoRepository extends LeagueUserRepository, MongoRepository<LeagueUser, String> {

    @Query("{ 'mLeagueID' : ?0, 'mUserID' : ?1 }")
    LeagueUser findByUserID(String leagueID, String userID);

    @Query("{ 'mUserID' : ?0 }")
    List<LeagueUser> findAllByUserID(String userID);

    @Query("{ 'mUserID' : ?0 }")
    List<LeagueUser> findAllByUserID(String userID, Pageable pageable);

    @Query("{ 'mLeagueID' : ?0 }")
    List<LeagueUser> findAllByLeagueID(String leagueID);
}