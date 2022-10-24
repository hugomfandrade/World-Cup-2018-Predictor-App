package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface MatchJpaRepository extends MatchRepository, JpaRepository<Match, String> {

    @Query("FROM Match m WHERE m.mMatchNo = :matchNumber")
    Match findByMatchNumber(int matchNumber);

    @Transactional
    @Modifying
    @Query("DELETE FROM Match m WHERE m.mMatchNo = :matchNumber")
    void deleteByMatchNumber(int matchNumber);

    @Query("FROM Match m WHERE m.mDateAndTime > :dateTime")
    List<Match> findGreatThan(Date dateTime);
}