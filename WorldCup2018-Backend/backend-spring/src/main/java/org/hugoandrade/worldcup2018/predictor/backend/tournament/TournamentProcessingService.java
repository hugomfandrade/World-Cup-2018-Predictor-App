package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentProcessingService {

    @Autowired private MatchRepository matchRepository;
    @Autowired private CountryRepository countryRepository;

    @Autowired private ModelMapper modelMapper;

    private final TournamentProcessing tournamentProcessing = new TournamentProcessing();

    private List<Match> getMatches() {
        return matchRepository.findAllAsList();
    }

    public void resetOrder() {

        final List<Match> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setResetListener();

        tournamentProcessing.startUpdateGroupsSync(countries, matches);
    }

    public void resetOrderAsync() {

        final List<Match> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setResetListener();

        tournamentProcessing.startUpdateGroupsProcessing(countries, matches);
    }

    public void updateOrder() {

        final List<Match> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setUpdateListener();

        tournamentProcessing.startUpdateGroupsSync(countries, matches);
    }

    public void updateOrderAsync() {

        final List<Match> matches = getMatches();
        final List<Country> countries = countryRepository.findAllAsList();

        this.setUpdateListener();

        tournamentProcessing.startUpdateGroupsProcessing(countries, matches);
    }


    private void setUpdateListener() {

        // first update, the positions
        tournamentProcessing.setListener(new TournamentProcessing.OnProcessingListener() {

            @Override public void onProcessingFinished(List<Country> countries, List<Match> matches) {}

            @Override
            public void updateCountry(Country country) {
                Country dbCountry = countryRepository.findCountryById(country.getID());
                countryRepository.save(country);
            }

            @Override
            public void updateMatchUp(Match match) {
                Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
                matchRepository.save(match);
            }
        });
    }

    private void setResetListener() {

        // first update, the positions
        tournamentProcessing.setListener(new TournamentProcessing.OnProcessingListener() {
            @Override
            public void onProcessingFinished(List<Country> countries, List<Match> matches) {

                for (Country country : countries) {
                    Country dbCountry = countryRepository.findCountryById(country.getID());
                    countryRepository.save(country);
                }

                for (Match match : matches) {
                    Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
                    matchRepository.save(match);
                }
            }

            @Override public void updateCountry(Country country) { }
            @Override public void updateMatchUp(Match match) { }

        });
    }
}
