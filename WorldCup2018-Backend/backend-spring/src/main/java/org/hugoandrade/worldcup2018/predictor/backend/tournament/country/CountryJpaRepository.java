package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CountryJpaRepository extends CountryRepository, JpaRepository<Country, String> {

    @Query("FROM Country c WHERE c.mID = :id")
    Country findCountryById(String id);
}