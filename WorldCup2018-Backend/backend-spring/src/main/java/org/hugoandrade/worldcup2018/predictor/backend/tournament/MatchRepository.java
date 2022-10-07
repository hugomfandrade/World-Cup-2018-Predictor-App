package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface MatchRepository extends CrudRepository<Match, String> {

    @Query("FROM Match m WHERE m.mMatchNo = :matchNumber")
    Match findByMatchNumber(int matchNumber);

    @Transactional
    @Modifying
    @Query("DELETE FROM Match m WHERE m.mMatchNo = :matchNumber")
    void deleteByMatchNumber(int matchNumber);

    @Query("FROM Match m WHERE m.mDateAndTime > :dateTime")
    List<Match> findGreatThan(Date dateTime);

    default List<Match> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}