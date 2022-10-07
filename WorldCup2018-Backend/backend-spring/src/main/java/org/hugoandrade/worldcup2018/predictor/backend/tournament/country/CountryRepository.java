package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CountryRepository extends CrudRepository<Country, String> {

    @Query("FROM Country c WHERE c.mID = :id")
    Country findCountryById(String id);

    default List<Country> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}