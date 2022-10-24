package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CountryRepository {

    Country findCountryById(String id);

    Iterable<Country> findAll();

    default List<Country> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    Country save(Country country);

    <S extends Country> Iterable<S> saveAll(Iterable<S> entities);

    void deleteById(String countryID);

    void deleteAll();

    long count();
}