package org.hugoandrade.worldcup2018.predictor.backend.repository;

import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface MatchRepository extends CrudRepository<Match, String> {

    @Query("FROM Match m WHERE m.mMatchNo = :matchNumber")
    Match findByMatchNumber(int matchNumber);

    @Transactional
    @Modifying
    @Query("DELETE FROM Match m WHERE m.mMatchNo = :matchNumber")
    void deleteByMatchNumber(int matchNumber);
}