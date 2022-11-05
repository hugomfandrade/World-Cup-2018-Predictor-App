package org.hugoandrade.worldcup2018.predictor.backend.tournament.group;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryDto;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript.configCountries;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ListResultMatchers.list;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ObjResultMatchers.obj;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountriesControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void all() throws Exception {

        final List<CountryDto> expectedCountries = configCountries()
                .stream().map(country -> modelMapper.map(country, CountryDto.class))
                .collect(Collectors.toList());

        doOn(mvc).get("/countries/")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).get("/countries/")
                .andExpect(status().isOk())
                .andExpect(list(CountryDto.class)
                        .assertEquals(expectedCountries, comparing(CountryDto::getName), COUNTRY_COMPARATOR));

        mvc.perform(MockMvcRequestBuilders.get("/countries/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(list(CountryDto.class)
                        .assertEquals(expectedCountries, comparing(CountryDto::getName), COUNTRY_COMPARATOR));
    }

    @Test
    void addOne() throws Exception {

        CountryDto country = new CountryDto("Country", "I", 1);

        doOn(mvc).post("/countries/", country)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .post("/countries/", country)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .post("/countries/", country)
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    country.setID(resCountry.getID());
                }))
                .andDo(mvcResult -> {
                    // clean repo
                    countryRepository.deleteById(country.getID());
                });
    }

    @Test
    void deleteAll() throws Exception {

        doOn(mvc).delete("/countries/")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).delete("/countries/")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).delete("/countries/")
                .andExpect(status().isOk())
                .andDo(mvcResult -> Assertions.assertEquals(0, countryRepository.count()))
                .andDo(mvcResult -> startupScript.startup())
                .andDo(mvcResult -> Assertions.assertEquals(32, countryRepository.count()));
    }

    @Test
    void getOne() throws Exception {

        CountryDto country = modelMapper.map(countryRepository.findAll().iterator().next(), CountryDto.class);

        doOn(mvc).get("/countries/" + country.getID())
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).get("/countries/" + country.getID())
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                }));

        doOn(mvc).withHeader(user.getToken()).get("/countries/" + country.getID())
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                }));
    }

    @Test
    void getMatches() throws Exception {

        CountryDto country = modelMapper.map(countryRepository.findAll().iterator().next(), CountryDto.class);

        doOn(mvc).get("/countries/" + country.getID() + "/matches")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).get("/countries/" + country.getID() + "/matches")
                .andExpect(status().isOk())
                .andExpect(list(MatchDto.class).assertSize(3))
                .andExpect(list(MatchDto.class).addDo((matchDtos) -> {
                    for (MatchDto matchDto : matchDtos) {
                        Assertions.assertTrue(
                                country.getID().equals(matchDto.getAwayTeamID()) |
                                        country.getID().equals(matchDto.getHomeTeamID()));
                    }
                }));

        doOn(mvc).withHeader(user.getToken()).get("/countries/" + country.getID() + "/matches")
                .andExpect(status().isOk())
                .andExpect(list(MatchDto.class).assertSize(3))
                .andExpect(list(MatchDto.class).addDo((matchDtos) -> {
                    for (MatchDto matchDto : matchDtos) {
                        Assertions.assertTrue(
                                country.getID().equals(matchDto.getAwayTeamID()) |
                                        country.getID().equals(matchDto.getHomeTeamID()));
                    }
                }));
    }

    @Test
    void deleteOne() throws Exception {

        CountryDto country = modelMapper.map(countryRepository.findAll().iterator().next(), CountryDto.class);

        doOn(mvc).delete("/countries/" + country.getID())
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).delete("/countries/" + country.getID())
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).delete("/countries/" + country.getID())
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);

                    Assertions.assertEquals(31, countryRepository.count());
                }))
                .andDo(mvcResult -> startupScript.startup());
    }

    @Test
    void updateOne() throws Exception {

        CountryDto country = modelMapper.map(countryRepository.findAll().iterator().next(), CountryDto.class);
        country.setName("another name");

        doOn(mvc).put("/countries/" + country.getID(), country)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).put("/countries/" + country.getID(), country)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).put("/countries/" + country.getID(), country)
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                }))
        ;

        doOn(mvc).withHeader(user.getToken()).get("/countries/" + country.getID())
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                }));

        doOn(mvc).withHeader(admin.getToken()).get("/countries/" + country.getID())
                .andExpect(status().isOk())
                .andExpect(obj(CountryDto.class).addDo((resCountry) -> {
                    Assertions.assertEquals(country.getName(), resCountry.getName());
                    Assertions.assertEquals(country.getGroup(), resCountry.getGroup());
                    Assertions.assertEquals(country.getDrawingOfLots(), resCountry.getDrawingOfLots());
                    Assertions.assertEquals(country, resCountry);
                }));

        startupScript.startup();
    }

    public static Comparator<CountryDto> COUNTRY_COMPARATOR = (o1, o2) -> {
        if (o1.getName() == null) return -1;
        if (o2.getName() == null) return 1;
        int name = o1.getName().compareTo(o2.getName());
        if (name != 0) return name;

        if (o1.getGroup() == null) return -1;
        if (o2.getGroup() == null) return 1;
        int group = o1.getGroup().compareTo(o2.getGroup());
        if (group != 0) return group;

        return o1.getDrawingOfLots() - o2.getDrawingOfLots();
    };
}