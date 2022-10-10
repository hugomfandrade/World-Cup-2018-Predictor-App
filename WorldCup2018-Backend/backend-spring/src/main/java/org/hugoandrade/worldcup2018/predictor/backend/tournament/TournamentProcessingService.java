package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentProcessingService {

    @Autowired private MatchRepository matchRepository;
    @Autowired private CountryRepository countryRepository;

    @Autowired private ModelMapper modelMapper;

    private final TournamentProcessing tournamentProcessing = new TournamentProcessing();

    private List<MatchDto> getMatches() {
        List<Match> m1 = matchRepository.findAllAsList();
        List<MatchDto> m2 = matchRepository.findAllAsList()
                .stream()
                .map(match -> modelMapper.map(match, MatchDto.class))
                .collect(Collectors.toList());
        return matchRepository.findAllAsList()
                .stream()
                .map(match -> modelMapper.map(match, MatchDto.class))
                .collect(Collectors.toList());
    }

    public void resetOrder() {

        final List<MatchDto> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setResetListener();

        tournamentProcessing.startUpdateGroupsSync(countries, matches);
    }

    public void resetOrderAsync() {

        final List<MatchDto> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setResetListener();

        tournamentProcessing.startUpdateGroupsProcessing(countries, matches);
    }

    public void updateOrder() {

        final List<MatchDto> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setUpdateListener();

        tournamentProcessing.startUpdateGroupsSync(countries, matches);
    }

    public void updateOrderAsync() {

        final List<MatchDto> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setUpdateListener();

        tournamentProcessing.startUpdateGroupsProcessing(countries, matches);
    }


    private void setUpdateListener() {

        // first update, the positions
        tournamentProcessing.setListener(new TournamentProcessing.OnProcessingListener() {

            @Override public void onProcessingFinished(List<Country> countries, List<MatchDto> matches) {}

            @Override
            public void updateCountry(Country country) {
                Country dbCountry = countryRepository.findCountryById(country.getID());
                countryRepository.save(country);
            }

            @Override
            public void updateMatchUp(MatchDto match) {
                Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
                dbMatch.setHomeTeamID(match.getHomeTeamID());
                dbMatch.setAwayTeamID(match.getAwayTeamID());
                dbMatch.setScore(match.getHomeTeamGoals(), match.getAwayTeamGoals());
                dbMatch.setHomeTeamNotes(match.getHomeTeamNotes());
                dbMatch.setAwayTeamNotes(match.getHomeTeamNotes());
                matchRepository.save(dbMatch);
            }
        });
    }

    private void setResetListener() {

        // first update, the positions
        tournamentProcessing.setListener(new TournamentProcessing.OnProcessingListener() {
            @Override
            public void onProcessingFinished(List<Country> countries, List<MatchDto> matches) {

                for (Country country : countries) {
                    Country dbCountry = countryRepository.findCountryById(country.getID());
                    countryRepository.save(country);
                }

                for (MatchDto match : matches) {
                    Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
                    matchRepository.save(modelMapper.map(dbMatch, Match.class));
                }
            }

            @Override public void updateCountry(Country country) { }
            @Override public void updateMatchUp(MatchDto match) { }

        });
    }
}
