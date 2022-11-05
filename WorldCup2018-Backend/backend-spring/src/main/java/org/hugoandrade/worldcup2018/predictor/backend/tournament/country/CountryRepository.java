package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoRepositoryBean
public interface CountryRepository extends CrudRepository<Country, String> {

    Country findCountryById(String id);

    default List<Country> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}