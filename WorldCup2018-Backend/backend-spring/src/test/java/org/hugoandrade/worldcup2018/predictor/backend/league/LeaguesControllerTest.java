package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.util.Lists;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemController;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeaguesControllerTest extends BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired private SystemDataService systemDataService;
    @Autowired private LeagueUserRepository leagueUserRepository;
    @Autowired private LeagueRepository leagueRepository;

    @BeforeEach
    void beforeEach() {
        SystemData systemData = new SystemData(null, "0,1,2,4", true, ISO8601Utils.parse("2018-06-27T12:00:00Z"));
        systemDataService.setSystemData(systemData);

        leagueRepository.deleteAll();
        leagueUserRepository.deleteAll();
    }

    @Test
    void getLeagues_Empty() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/leagues/"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<League> leagues = parse(mvcResult, new TypeReference<List<League>>(){});
                    Assertions.assertEquals(0, leagues.size());
                });

        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<League> leagues = parse(mvcResult, new TypeReference<List<League>>(){});
                    Assertions.assertEquals(0, leagues.size());
                });
    }

    @Test
    void createLeagues() throws Exception {

        League newLeague = new League("League Name", null);

        mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    League league = parse(mvcResult, League.class);

                    Assertions.assertEquals(newLeague.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                });

        mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    League league = parse(mvcResult, League.class);

                    Assertions.assertEquals(newLeague.getName(), league.getName());
                    Assertions.assertEquals(admin.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                });

        // get route
        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<League> leagues = parse(mvcResult, new TypeReference<List<League>>(){});
                    Assertions.assertEquals(1, leagues.size());

                    League league = leagues.get(0);

                    Assertions.assertEquals(newLeague.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                });
    }

    @Test
    void createLeagues_Multiple() throws Exception {

        League newLeague01 = new League("League Name 01", null);
        League newLeague02 = new League("League Name 02", null);


        mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague01))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    League league = parse(mvcResult, League.class);

                    Assertions.assertEquals(newLeague01.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                });

        mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague02))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    League league = parse(mvcResult, League.class);

                    Assertions.assertEquals(newLeague02.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                });


        // get route
        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<League> leagues = parse(mvcResult, new TypeReference<List<League>>(){});
                    Assertions.assertEquals(2, leagues.size());

                    List<League> expectedLeagues = Lists.newArrayList(newLeague01, newLeague02);
                    leagues.sort(Comparator.comparing(League::getName));
                    expectedLeagues.sort(Comparator.comparing(League::getName));

                    for (int i = 0 ; i < 2 ; i++) {
                        League league = leagues.get(i);
                        League expectedLeague = expectedLeagues.get(i);
                        Assertions.assertEquals(expectedLeague.getName(), league.getName());
                        Assertions.assertEquals(user.getUserID(), league.getAdminID());
                        Assertions.assertEquals(1, league.getNumberOfMembers());
                        Assertions.assertNotNull(league.getCode());
                    }
                });
    }

    @Test
    void leagueLifecycle() throws Exception {

        League newLeague = new League("League Name", null);

        MvcResult postLeaguesRes = mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        League league = parse(postLeaguesRes, League.class);

        LeaguesController.JoinRequestBody joinRequest = new LeaguesController.JoinRequestBody();
        joinRequest.code = league.getCode();
        LeaguesController.JoinRequestBody wrongCodeJoinRequest = new LeaguesController.JoinRequestBody();
        wrongCodeJoinRequest.code = league.getCode() + "-WRONG";

        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "/join")
                        .content(format(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "/join")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "/join")
                        .header(securityConstants.HEADER_STRING, userOther.getToken())
                        .content(format(wrongCodeJoinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "-INVALID" + "/join")
                        .header(securityConstants.HEADER_STRING, userOther.getToken())
                        .content(format(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        // join, successful
        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "/join")
                        .header(securityConstants.HEADER_STRING, userOther.getToken())
                        .content(format(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    League joinLeague = parse(mvcResult, League.class);

                    Assertions.assertEquals(league.getID(), joinLeague.getID());
                    Assertions.assertEquals(league.getName(), joinLeague.getName());
                    Assertions.assertEquals(user.getUserID(), joinLeague.getAdminID());
                    Assertions.assertEquals(2, joinLeague.getNumberOfMembers());
                    Assertions.assertNotNull(joinLeague.getCode());
                });


        // check two members
        final ResultMatcher checkEqualAccounts = mvcResult -> {
            List<Account> accounts = parse(mvcResult, new TypeReference<List<Account>>() {});
            List<Account> expectedAccounts = Stream.of(user, userOther)
                    .map(account -> new Account(account.getUserID(), account.getUsername(), null, null))
                    .collect(Collectors.toList());

            accounts.sort(Comparator.comparing(Account::getUsername));
            expectedAccounts.sort(Comparator.comparing(Account::getUsername));

            Assertions.assertEquals(2, accounts.size());

            for (int i = 0 ; i < accounts.size() ; i++) {
                Assertions.assertEquals(expectedAccounts.get(i).getId(), accounts.get(i).getId());
                Assertions.assertEquals(expectedAccounts.get(i).getUsername(), accounts.get(i).getUsername());
            }
        };

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().isOk())
                .andExpect(checkEqualAccounts);

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(checkEqualAccounts);

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "-INVALID" + "/users")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        //
        // update name
        league.setName("ANOTHER NAME");

        mvc.perform(MockMvcRequestBuilders.put("/leagues/" + league.getID() + "-INVALID")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(league))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, userOther.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/leagues/" + league.getID())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(league))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    League updatedLeague = parse(mvcResult, League.class);

                    Assertions.assertEquals(league.getName(), updatedLeague.getName());
                    Assertions.assertEquals(league.getAdminID(), updatedLeague.getAdminID());
                    Assertions.assertEquals(user.getUserID(), updatedLeague.getAdminID());
                    Assertions.assertEquals(2, updatedLeague.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode(), updatedLeague.getCode());
                    Assertions.assertNotNull(league.getCode());
                });


        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<League> leagues = parse(mvcResult, new TypeReference<List<League>>(){});
                    Assertions.assertEquals(1, leagues.size());

                    List<League> expectedLeagues = Lists.newArrayList(league);
                    leagues.sort(Comparator.comparing(League::getName));
                    expectedLeagues.sort(Comparator.comparing(League::getName));

                    for (int i = 0 ; i < leagues.size() ; i++) {
                        League updatedLeague = leagues.get(i);
                        League expectedLeague = expectedLeagues.get(i);
                        Assertions.assertEquals(expectedLeague.getName(), updatedLeague.getName());
                        Assertions.assertEquals(user.getUserID(), updatedLeague.getAdminID());
                        Assertions.assertEquals(2, updatedLeague.getNumberOfMembers());
                        Assertions.assertNotNull(updatedLeague.getCode());
                    }
                });

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(checkEqualAccounts);


        //
        // delete league name

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID() + "-INVALID")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk());


        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<League> leagues = parse(mvcResult, new TypeReference<List<League>>(){});
                    Assertions.assertEquals(0, leagues.size());
                });

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void leaveLeague() throws Exception {

        League newLeague = new League("League Name", null);

        MvcResult postLeaguesRes = mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        League league = parse(postLeaguesRes, League.class);

        LeaguesController.JoinRequestBody joinRequest = new LeaguesController.JoinRequestBody();
        joinRequest.code = league.getCode();

        // join, successful
        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "/join")
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // leave before joining, should error
        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().isOk());
                // .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID() + "/users"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID() + "-INVALID" + "/users")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().is4xxClientError());

        // join another
        mvc.perform(MockMvcRequestBuilders.post("/leagues/" + league.getID() + "/join")
                        .header(securityConstants.HEADER_STRING, userOther.getToken())
                        .content(format(joinRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Assertions.assertEquals(3, parse(mvcResult, new TypeReference<List<Account>>() {}).size());
                });

        // leave as member
        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Assertions.assertEquals(2, parse(mvcResult, new TypeReference<List<Account>>() {}).size());
                });

        // leave as admin, should delete league, no longer available
        mvc.perform(MockMvcRequestBuilders.delete("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID() + "/users")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/leagues/" + league.getID())
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().is4xxClientError());
    }
}