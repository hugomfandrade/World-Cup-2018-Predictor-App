package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoRepositoryBean
public interface MatchRepository extends CrudRepository<Match, String> {

    Match findByMatchNumber(int matchNumber);

    List<Match> findAllByCountryID(String countryID);

    void deleteByMatchNumber(int matchNumber);

    List<Match> findGreatThan(Date dateTime);

    default List<Match> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}