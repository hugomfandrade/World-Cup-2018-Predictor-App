package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CountryMongoRepository extends CountryRepository, MongoRepository<Country, String> {

    @Query("{ '_id' : ?0 }")
    Country findCountryById(String id);
}