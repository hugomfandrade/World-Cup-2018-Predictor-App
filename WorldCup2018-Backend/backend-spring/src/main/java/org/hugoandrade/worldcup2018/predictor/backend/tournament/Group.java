package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Group {

    private final String mGroupLetter;
    private List<Country> mCountries = new ArrayList<>();
    private List<Match> mMatches = new ArrayList<>();

    public Group(String groupLetter) {
        mGroupLetter = groupLetter;
        mCountries = new ArrayList<>();
        mMatches = new ArrayList<>();
    }

    public void setCountryList(ArrayList<Country> countryList) {
        mCountries = countryList;
    }

    public List<Country> getCountries() {
        return mCountries;
    }

    public void add(Country country) {
        mCountries.add(country);
    }

    public void addMatch(Match match) {
        if (!mMatches.contains(match))
            mMatches.add(match);
    }

    public void addMatch(MatchDto match) {
        this.addMatch(new ModelMapper().map(match, Match.class));
    }

    public String getGroupLetter() {
        return mGroupLetter;
    }

    public boolean areAllMatchesPlayed() {

        for (Country country : mCountries)
            if (country.getMatchesPlayed() != 3)
                return false;
        return true;
    }

    //
    //


    public void orderGroup() {
        for (Country country : mCountries)
            compute(country);

        orderCountryList();
    }

    private void orderCountryList() {
        if (mCountries.size() != 4) {
            mCountries.sort(Collections.reverseOrder());
            return;
        }

        // Sort Group
        mCountries.sort(Collections.reverseOrder());

        List<Country> sortedGroup = new ArrayList<>();
        List<Country> countriesWithEqualNumberOfPoints = new ArrayList<>();
        countriesWithEqualNumberOfPoints.add(mCountries.get(0));

        for (int i = 1 ; i < 4 ; i++) {
            // The country "i" has equal number of points as the previous country. Store it in the
            // CountriesStillTied List
            if (equalsRanking(mCountries.get(i - 1), mCountries.get(i))) {
                countriesWithEqualNumberOfPoints.add(mCountries.get(i));
            }
            // The country "i" does not have an equal number of points as the previous country.
            // Add the previous countries that were tied (which were stored in the
            // countriesWithEqualNumberOfPoints List) to the sortedGroup List after applying the
            // Tie-Breaking criteria to those countries; and clear and add country "i" to
            // countriesWithEqualNumberOfPoints List
            else {
                sortedGroup.addAll(computeTieBreaker(countriesWithEqualNumberOfPoints));
                countriesWithEqualNumberOfPoints.clear();
                countriesWithEqualNumberOfPoints.add(mCountries.get(i));
            }
            if (i == 3) { // last iteration
                sortedGroup.addAll(computeTieBreaker(countriesWithEqualNumberOfPoints));
            }
        }

        mCountries.clear();
        for (int i = 0 ; i < sortedGroup.size() ; i++) {
            sortedGroup.get(i).setPosition(i + 1);
            mCountries.add(sortedGroup.get(i));
        }

    }

    private static boolean equalsRanking(Country o1, Country o2) {
        if (o1.getPoints() != o1.getPoints()) return false;
        if (o1.getGoalsDifference() != o2.getGoalsDifference()) return false;
        if (o1.getGoalsFor() != o2.getGoalsFor()) return false;
        return true;
    }

    private void compute(Country country, String... opposingCountryIDs) {

        // for tie-breaker
        List<String> opposingCountries = Arrays.asList(opposingCountryIDs);

        int mMatchesPlayed = 0;
        int mVictories = 0;
        int mDefeats = 0;
        int mDraws = 0;
        int mGoalsFor = 0;
        int mGoalsAgainst = 0;
        int mGoalsDifference = 0;
        int mPoints = 0;

        // Set \"Matches Played\"
        for (Match mmatch : mMatches) {
            MatchDto match = new ModelMapper().map(mmatch, MatchDto.class);
            if (!MatchUtils.isMatchPlayed(match)) continue;

            if (match.getHomeTeamID().equals(country.getID())) {
                // check if opposing country is in list, when verifying tie-breakers
                if (!opposingCountries.isEmpty() && !opposingCountries.contains(match.getAwayTeamID())) continue;

                mMatchesPlayed++;
                mGoalsFor += match.getHomeTeamGoals();
                mGoalsAgainst += match.getAwayTeamGoals();

                if (MatchUtils.didHomeTeamWinRegularTime(match)) {
                    mPoints = mPoints + 3;
                    mVictories += 1;
                }
                else if (MatchUtils.didAwayTeamWinRegularTime(match)) {
                    mDefeats += 1;
                }
                else if (MatchUtils.didTeamsTied(match)) {
                    mPoints = mPoints + 1;
                    mDraws += 1;
                }

            }
            else if (match.getAwayTeamID().equals(country.getID())) {
                // check if opposing country is in list, when verifying tie-breakers
                if (!opposingCountries.isEmpty() && !opposingCountries.contains(match.getAwayTeamID())) continue;

                mMatchesPlayed++;
                mGoalsFor += match.getAwayTeamGoals();
                mGoalsAgainst += match.getHomeTeamGoals();

                if (MatchUtils.didHomeTeamWinRegularTime(match)) {
                    mDefeats += 1;
                }
                else if (MatchUtils.didAwayTeamWinRegularTime(match)) {
                    mPoints = mPoints + 3;
                    mVictories += 1;
                }
                else if (MatchUtils.didTeamsTied(match)) {
                    mPoints = mPoints + 1;
                    mDraws += 1;
                }
            }
        }

        mGoalsDifference = mGoalsFor - mGoalsAgainst;

        country.setMatchesPlayed(mMatchesPlayed);
        country.setVictories(mVictories);
        country.setDefeats(mDefeats);
        country.setDraws(mDraws);
        country.setGoalsFor(mGoalsFor);
        country.setGoalsAgainst(mGoalsAgainst);
        country.setGoalsDifference(mGoalsDifference);
        country.setPoints(mPoints);
    }

    private List<Country> computeTieBreaker(List<Country> countriesTiedList) {

        // One Country only. Return it;
        if (countriesTiedList.size() == 1 || countriesTiedList.size() == 4) {
            return countriesTiedList;
        }
        // Two Countries that were tied. Compute tiebreaker between two teams;
        else if (countriesTiedList.size() == 2) {
            // Clone List (It is not necessary)
            List<Country> cloneCountriesTiedList = new ArrayList<>(countriesTiedList);

            // Update Stats between the two teams. ie Compute Head to Head match-up (if it
            compute(cloneCountriesTiedList.get(0),
                    cloneCountriesTiedList.get(1).getID());
            compute(cloneCountriesTiedList.get(1),
                    cloneCountriesTiedList.get(0).getID());

            // Sort countries
            cloneCountriesTiedList.sort(Collections.reverseOrder());
            for (Country country : cloneCountriesTiedList)
                compute(country);
            return cloneCountriesTiedList;
        }
        else if (countriesTiedList.size() == 3) {
            // Clone List (It is not necessary)
            List<Country> cloneCountriesTiedList = new ArrayList<>(countriesTiedList);

            // Update Stats between the three teams
            compute(cloneCountriesTiedList.get(0),
                    cloneCountriesTiedList.get(1).getID(),
                    cloneCountriesTiedList.get(2).getID());
            compute(cloneCountriesTiedList.get(1),
                    cloneCountriesTiedList.get(0).getID(),
                    cloneCountriesTiedList.get(2).getID());
            compute(cloneCountriesTiedList.get(2),
                    cloneCountriesTiedList.get(0).getID(),
                    cloneCountriesTiedList.get(1).getID());

            // Sort countries
            cloneCountriesTiedList.sort(Collections.reverseOrder());
            for (Country country : cloneCountriesTiedList)
                compute(country);

            return cloneCountriesTiedList;
        }
        else {
            return countriesTiedList;
        }
    }

    //
    //

    public enum Tournament {

        A("A"),
        B("B"),
        C("C"),
        D("D"),
        E("E"),
        F("F"),
        G("G"),
        H("H");

        public final String name;

        Tournament(String group) {
            name = group;
        }

        public static Tournament get(String group) {
            for (Tournament s : Tournament.values()) {
                if (s.name.equalsIgnoreCase(group)) {
                    return s;
                }
            }
            return null;
        }
    }
}
