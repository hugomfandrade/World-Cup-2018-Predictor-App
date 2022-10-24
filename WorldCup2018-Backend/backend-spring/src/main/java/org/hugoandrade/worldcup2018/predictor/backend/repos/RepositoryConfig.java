package org.hugoandrade.worldcup2018.predictor.backend.repos;

import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb.*;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Autowired CountryJpaRepository countryJpaRepository;
    @Autowired CountryMongoRepository countryMongoRepository;

    @Bean
    public CountryRepository countryRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return countryMongoRepository;
        }
        return countryJpaRepository;
    }

    @Autowired MatchJpaRepository matchJpaRepository;
    @Autowired MatchMongoRepository matchMongoRepository;

    @Bean
    public MatchRepository matchRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return matchMongoRepository;
        }
        return matchJpaRepository;
    }

    @Autowired PredictionJpaRepository predictionJpaRepository;
    @Autowired PredictionMongoRepository predictionMongoRepository;

    @Bean
    public PredictionRepository predictionRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return predictionMongoRepository;
        }
        return predictionJpaRepository;
    }
}
