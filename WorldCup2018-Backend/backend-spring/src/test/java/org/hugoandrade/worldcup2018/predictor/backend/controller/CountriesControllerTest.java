package org.hugoandrade.worldcup2018.predictor.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript;
import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.repository.CountryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountriesControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void all() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/countries/"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/countries/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Country> countries = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Country>>(){});

                    Assertions.assertTrue(areEqual(countries, StartupDatabaseScript.configCountries()));
                });

        mvc.perform(MockMvcRequestBuilders.get("/countries/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Country> countries = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Country>>(){});

                    Assertions.assertTrue(areEqual(countries, StartupDatabaseScript.configCountries()));
                });
    }

    @Test
    void addOne() throws Exception {

        Country country = new Country("Country", "I", 1);

        mvc.perform(MockMvcRequestBuilders.post("/countries/")
                        .content(format(country))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/countries/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(country))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/countries/")
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(country))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    country.setID(resCountry.getID());
                })
                .andDo(mvcResult -> {

                    // clean repo
                    countryRepository.deleteById(country.getID());
                });
    }

    @Test
    void deleteAll() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/countries/"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/countries/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/countries/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andDo(mvcResult -> Assertions.assertEquals(0, countryRepository.count()))
                .andDo(mvcResult -> startupScript.startup())
                .andDo(mvcResult -> Assertions.assertEquals(32, countryRepository.count()));
    }

    @Test
    void getOne() throws Exception {

        Country country = countryRepository.findAll().iterator().next();

        mvc.perform(MockMvcRequestBuilders.get("/countries/" + country.getID()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                });

        mvc.perform(MockMvcRequestBuilders.get("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                });
    }

    @Test
    void deleteOne() throws Exception {

        Country country = countryRepository.findAll().iterator().next();

        mvc.perform(MockMvcRequestBuilders.delete("/countries/" + country.getID()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);

                    Assertions.assertEquals(31, countryRepository.count());
                })
                .andDo(mvcResult -> startupScript.startup());
    }

    @Test
    void updateOne() throws Exception {

        Country country = countryRepository.findAll().iterator().next();
        country.setName("another name");

        mvc.perform(MockMvcRequestBuilders.put("/countries/" + country.getID())
                        .content(format(country))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(country))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(country))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                })
                // .andDo(mvcResult -> startupScript.startup())
        ;

        mvc.perform(MockMvcRequestBuilders.get("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                });

        mvc.perform(MockMvcRequestBuilders.get("/countries/" + country.getID())
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Country resCountry = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Country>(){});

                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                });

        startupScript.startup();
    }

    public static boolean areEqual(List<Country> countries1, List<Country> countries2) {

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

        countries1.sort(countrySorter);
        countries2.sort(countrySorter);

        boolean areEqual = countries1.size() == countries2.size() &&
                IntStream.range(0, countries1.size())
                        .allMatch(i -> countryComparator.compare(countries1.get(i), countries2.get(i)) == 0);

        return areEqual;
    }
}