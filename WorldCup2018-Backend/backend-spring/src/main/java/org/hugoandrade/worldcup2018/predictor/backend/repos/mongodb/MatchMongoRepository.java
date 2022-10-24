package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchRepository;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface MatchMongoRepository extends MatchRepository, MongoRepository<Match, String> {

    @Query("{ 'mMatchNo' : ?0 }")
    Match findByMatchNumber(int matchNumber);

    @DeleteQuery("{ 'mMatchNo' : ?0 }")
    void deleteByMatchNumber(int matchNumber);

    @Query("{ 'mDateAndTime' : { $gt : ?0} }")
    List<Match> findGreatThan(Date dateTime);
}