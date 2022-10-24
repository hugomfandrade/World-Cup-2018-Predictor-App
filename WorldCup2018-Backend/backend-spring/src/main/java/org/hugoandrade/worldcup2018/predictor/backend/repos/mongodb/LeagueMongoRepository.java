package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.league.League;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LeagueMongoRepository extends LeagueRepository, MongoRepository<League, String> {

    @Query("{ '_id' : ?0 , 'mAdminID' : ?1 }")
    League findByAdminID(String leagueID, String adminID);

    @Query("{ 'mCode' : ?0 }")
    League findByCode(String code);

    @Deprecated
    @Query("{ 'mUserID' : ?0, $lookup: { from: LeagueUser, localField: _id, foreignField: mUserID, as: LeagueUsers } }")
    List<League> findAllByUserID(String userID);
}