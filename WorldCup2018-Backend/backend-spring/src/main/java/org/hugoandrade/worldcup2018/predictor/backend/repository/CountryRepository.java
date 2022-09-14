package org.hugoandrade.worldcup2018.predictor.backend.repository;

import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepository extends CrudRepository<Country, String> {

    @Query("FROM Country c WHERE c.mID = :id")
    Country findCountryById(String id);
}