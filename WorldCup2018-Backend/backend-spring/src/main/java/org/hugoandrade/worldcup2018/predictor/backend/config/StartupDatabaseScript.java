package org.hugoandrade.worldcup2018.predictor.backend.config;

import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.hugoandrade.worldcup2018.predictor.backend.config.StaticVariableUtils.SCountry;
import static org.hugoandrade.worldcup2018.predictor.backend.config.StaticVariableUtils.SGroup;

@Service
public class StartupDatabaseScript {

    @Autowired
    private CountryRepository countryRepository;

    public void startup() {
        startupCountries();
    }

    private void startupCountries() {

        final List<Country> countries = configCountries();

        final List<Country> dbCountries = StreamSupport
                .stream(countryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        final Comparator<Country> countrySorter = (o1, o2) -> {
            if (o1.getName() == null) return -1;
            if (o2.getName() == null) return 1;
            return o1.getName().compareTo(o2.getName());
        };
        final Comparator<Country> countryComparator = (o1, o2) -> {
            if (o1.getName() == null) return -1;
            if (o2.getName() == null) return 1;
            int name = o1.getName().compareTo(o2.getName());
            if (name != 0) return name;

            if (o1.getGroup() == null) return -1;
            if (o2.getGroup() == null) return 1;
            int group = o1.getGroup().compareTo(o1.getGroup());
            if (group != 0) return group;

            return o1.getDrawingOfLots() - o2.getDrawingOfLots();
        };

        countries.sort(countrySorter);
        dbCountries.sort(countrySorter);

        boolean areEqual = countries.size() == dbCountries.size() &&
                IntStream.range(0, countries.size())
                        .allMatch(i -> countryComparator.compare(countries.get(i), dbCountries.get(i)) == 0);

        if (!areEqual) {
            countryRepository.deleteAll();
            countryRepository.saveAll(countries);
        }
    }


    public static List<Country> configCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(SCountry.Russia.name, SGroup.A.name, 1));
        countries.add(new Country(SCountry.SaudiArabia.name, SGroup.A.name, 2));
        countries.add(new Country(SCountry.Egypt.name, SGroup.A.name, 3));
        countries.add(new Country(SCountry.Uruguay.name, SGroup.A.name, 4));

        countries.add(new Country(SCountry.Portugal.name, SGroup.B.name, 1));
        countries.add(new Country(SCountry.Spain.name, SGroup.B.name, 2));
        countries.add(new Country(SCountry.Morocco.name, SGroup.B.name, 3));
        countries.add(new Country(SCountry.Iran.name, SGroup.B.name, 4));

        countries.add(new Country(SCountry.France.name, SGroup.C.name, 1));
        countries.add(new Country(SCountry.Australia.name, SGroup.C.name,2 ));
        countries.add(new Country(SCountry.Peru.name, SGroup.C.name, 3));
        countries.add(new Country(SCountry.Denmark.name, SGroup.C.name, 4));

        countries.add(new Country(SCountry.Argentina.name, SGroup.D.name, 1));
        countries.add(new Country(SCountry.Iceland.name, SGroup.D.name, 2));
        countries.add(new Country(SCountry.Croatia.name, SGroup.D.name, 3));
        countries.add(new Country(SCountry.Nigeria.name, SGroup.D.name, 4));

        countries.add(new Country(SCountry.Brazil.name, SGroup.E.name, 1));
        countries.add(new Country(SCountry.Switzerland.name, SGroup.E.name, 2));
        countries.add(new Country(SCountry.CostaRica.name, SGroup.E.name, 3));
        countries.add(new Country(SCountry.Serbia.name, SGroup.E.name, 4));

        countries.add(new Country(SCountry.Germany.name, SGroup.F.name, 1));
        countries.add(new Country(SCountry.Mexico.name, SGroup.F.name, 2));
        countries.add(new Country(SCountry.Sweden.name, SGroup.F.name, 3));
        countries.add(new Country(SCountry.SouthKorea.name, SGroup.F.name, 4));

        countries.add(new Country(SCountry.Belgium.name, SGroup.G.name, 1));
        countries.add(new Country(SCountry.Panama.name, SGroup.G.name, 2));
        countries.add(new Country(SCountry.Tunisia.name, SGroup.G.name, 3));
        countries.add(new Country(SCountry.England.name, SGroup.G.name, 4));

        countries.add(new Country(SCountry.Poland.name, SGroup.H.name, 1));
        countries.add(new Country(SCountry.Senegal.name, SGroup.H.name, 2));
        countries.add(new Country(SCountry.Colombia.name, SGroup.H.name, 3));
        countries.add(new Country(SCountry.Japan.name, SGroup.H.name, 4));
        return countries;
    }
}
