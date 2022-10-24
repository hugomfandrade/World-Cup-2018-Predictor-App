package org.hugoandrade.worldcup2018.predictor.backend.repos;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.*;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueRepository;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUserJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUserRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb.*;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataJpaRepository;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataRepository;
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

    @Autowired AccountJpaRepository accountJpaRepository;
    @Autowired AccountMongoRepository accountMongoRepository;

    @Bean
    public AccountRepository accountRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return accountMongoRepository;
        }
        return accountJpaRepository;
    }

    @Autowired AdminJpaRepository adminJpaRepository;
    @Autowired AdminMongoRepository adminMongoRepository;

    @Bean
    public AdminRepository adminRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return adminMongoRepository;
        }
        return adminJpaRepository;
    }

    @Autowired LeagueUserJpaRepository leagueUserJpaRepository;
    @Autowired LeagueUserMongoRepository leagueUserMongoRepository;

    @Bean
    public LeagueUserRepository leagueUserRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return leagueUserMongoRepository;
        }
        return leagueUserJpaRepository;
    }

    @Autowired LeagueJpaRepository leagueJpaRepository;
    @Autowired LeagueMongoRepository leagueMongoRepository;

    @Bean
    public LeagueRepository leagueRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return leagueMongoRepository;
        }
        return leagueJpaRepository;
    }

    @Autowired SystemDataJpaRepository systemDataJpaRepository;
    @Autowired SystemDataMongoRepository systemDataMongoRepository;

    @Bean
    public SystemDataRepository systemDataRepository(@Value("${spring.data.repository.type}") String repositoryType) {
        if ("mongodb".equalsIgnoreCase(repositoryType)) {
            return systemDataMongoRepository;
        }
        return systemDataJpaRepository;
    }
}
