package org.hugoandrade.worldcup2018.predictor.admin.processing;

import org.hugoandrade.worldcup2018.predictor.data.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupComp {

    private static final String TAG = GroupComp.class.getSimpleName();

    private final String mGroup;
    private final List<CountryComp> mCountryList;

    GroupComp(String group) {
        mGroup = group;
        mCountryList = new ArrayList<>();
    }

    public void add(CountryComp c) {
        mCountryList.add(c);
    }

    List<CountryComp> getCountryCompList() {
        return mCountryList;
    }

    public List<Country> getCountryList() {
        List<Country> cList = new ArrayList<>();
        for (CountryComp c : mCountryList)
            cList.add(c.getCountry());
        return cList;
    }

    public CountryComp get(int i) {
        return mCountryList.get(i);
    }

    public String getGroup() {
        return mGroup;
    }

    void orderGroup() {
        for (CountryComp country : mCountryList)
            country.compute();

        orderCountryList();
    }

    private void orderCountryList() {
        if (mCountryList.size() != 4) {
            Collections.sort(mCountryList, Collections.<CountryComp>reverseOrder());
            return;
        }

        // Sort Group
        Collections.sort(mCountryList, Collections.<CountryComp>reverseOrder());

        List<CountryComp> sortedGroup = new ArrayList<>();
        List<CountryComp> countriesWithEqualNumberOfPoints = new ArrayList<>();
        countriesWithEqualNumberOfPoints.add(mCountryList.get(0));

        for (int i = 1 ; i < 4 ; i++) {
            // The country "i" has equal number of points as the previous country. Store it in the
            // CountriesStillTied List
            if (mCountryList.get(i - 1).equalsRanking(mCountryList.get(i))) {
                countriesWithEqualNumberOfPoints.add(mCountryList.get(i));
            }
            // The country "i" does not have an equal number of points as the previous country.
            // Add the previous countries that were tied (which were stored in the
            // countriesWithEqualNumberOfPoints List) to the sortedGroup List after applying the
            // Tie-Breaking criteria to those countries; and clear and add country "i" to
            // countriesWithEqualNumberOfPoints List
            else {
                sortedGroup.addAll(computeTieBreaker(countriesWithEqualNumberOfPoints));
                countriesWithEqualNumberOfPoints.clear();
                countriesWithEqualNumberOfPoints.add(mCountryList.get(i));
            }
            if (i == 3) { // last iteration
                sortedGroup.addAll(computeTieBreaker(countriesWithEqualNumberOfPoints));
            }
        }

        mCountryList.clear();
        for (int i = 0 ; i < sortedGroup.size() ; i++) {
            sortedGroup.get(i).getCountry().setPosition(i + 1);
            mCountryList.add(sortedGroup.get(i));
        }

    }

    private List<CountryComp> computeTieBreaker(List<CountryComp> countriesTiedList) {

        // One Country only. Return it;
        if (countriesTiedList.size() == 1 || countriesTiedList.size() == 4) {
            return countriesTiedList;
        }
        // Two Countries that were tied. Compute tiebreaker between two teams;
        else if (countriesTiedList.size() == 2) {
            // Clone List (It is not necessary)
            List<CountryComp> cloneCountriesTiedList = new ArrayList<>(countriesTiedList);

            // Update Stats between the two teams. ie Compute Head to Head match-up (if it
            cloneCountriesTiedList.get(0).compute(
                    cloneCountriesTiedList.get(1).getCountry().getID());
            cloneCountriesTiedList.get(1).compute(
                    cloneCountriesTiedList.get(0).getCountry().getID());

            // Sort countries
            Collections.sort(cloneCountriesTiedList, Collections.<CountryComp>reverseOrder());
            for (CountryComp c : cloneCountriesTiedList)
                c.compute();
            return cloneCountriesTiedList;
        }
        else if (countriesTiedList.size() == 3) {
            // Clone List (It is not necessary)
            List<CountryComp> cloneCountriesTiedList = new ArrayList<>(countriesTiedList);

            // Update Stats between the three teams
            cloneCountriesTiedList.get(0).compute(
                    cloneCountriesTiedList.get(1).getCountry().getID(),
                    cloneCountriesTiedList.get(2).getCountry().getID());
            cloneCountriesTiedList.get(1).compute(
                    cloneCountriesTiedList.get(0).getCountry().getID(),
                    cloneCountriesTiedList.get(2).getCountry().getID());
            cloneCountriesTiedList.get(2).compute(
                    cloneCountriesTiedList.get(0).getCountry().getID(),
                    cloneCountriesTiedList.get(1).getCountry().getID());

            // Sort countries
            Collections.sort(cloneCountriesTiedList, Collections.<CountryComp>reverseOrder());
            for (CountryComp c : cloneCountriesTiedList)
                c.compute();
            return cloneCountriesTiedList;
        }
        else {
            return countriesTiedList;
        }
    }

    public boolean areAllMatchesPlayed() {

        for (CountryComp c : mCountryList)
            if (c.getCountry().getMatchesPlayed() != 3)
                return false;
        return true;
    }
}
