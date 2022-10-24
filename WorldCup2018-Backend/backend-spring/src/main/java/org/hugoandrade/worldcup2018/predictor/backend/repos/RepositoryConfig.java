package org.hugoandrade.worldcup2018.predictor.backend.repos;

import org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb.CountryMongoRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryCrudRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Autowired CountryCrudRepository countryCrudRepository;
    @Autowired CountryMongoRepository countryMongoRepository;

    @Bean
    public CountryRepository countryRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return countryMongoRepository;
        }
        return countryCrudRepository;
    }
}
