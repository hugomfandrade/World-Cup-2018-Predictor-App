package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.util.Lists;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
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
        SystemData systemData = new SystemData("0,1,2,4", true, ISO8601Utils.parse("2018-06-27T12:00:00Z"));
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
                    List<LeagueDto> leagues = parse(mvcResult, new TypeReference<List<LeagueDto>>(){});
                    Assertions.assertEquals(0, leagues.size());
                });

        mvc.perform(MockMvcRequestBuilders.get("/leagues/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<LeagueDto> leagues = parse(mvcResult, new TypeReference<List<LeagueDto>>(){});
                    Assertions.assertEquals(0, leagues.size());
                });
    }

    @Test
    void createLeagues() throws Exception {

        LeagueDto newLeague = new LeagueDto("League Name");

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
                    LeagueDto league = parse(mvcResult, LeagueDto.class);

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
                    LeagueDto league = parse(mvcResult, LeagueDto.class);

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
                    List<LeagueDto> leagues = parse(mvcResult, new TypeReference<List<LeagueDto>>(){});
                    Assertions.assertEquals(1, leagues.size());

                    LeagueDto league = leagues.get(0);

                    Assertions.assertEquals(newLeague.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                });
    }

    @Test
    void createLeagues_Multiple() throws Exception {

        LeagueDto newLeague01 = new LeagueDto("League Name 01");
        LeagueDto newLeague02 = new LeagueDto("League Name 02");


        mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague01))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    LeagueDto league = parse(mvcResult, LeagueDto.class);

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
                    LeagueDto league = parse(mvcResult, LeagueDto.class);

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
                    List<LeagueDto> leagues = parse(mvcResult, new TypeReference<List<LeagueDto>>(){});
                    Assertions.assertEquals(2, leagues.size());

                    List<LeagueDto> expectedLeagues = Lists.newArrayList(newLeague01, newLeague02);
                    leagues.sort(Comparator.comparing(LeagueDto::getName));
                    expectedLeagues.sort(Comparator.comparing(LeagueDto::getName));

                    for (int i = 0 ; i < 2 ; i++) {
                        LeagueDto league = leagues.get(i);
                        LeagueDto expectedLeague = expectedLeagues.get(i);
                        Assertions.assertEquals(expectedLeague.getName(), league.getName());
                        Assertions.assertEquals(user.getUserID(), league.getAdminID());
                        Assertions.assertEquals(1, league.getNumberOfMembers());
                        Assertions.assertNotNull(league.getCode());
                    }
                });
    }

    @Test
    void leagueLifecycle() throws Exception {

        LeagueDto newLeague = new LeagueDto("League Name");

        final String leaguesUrl = "/leagues/";

        MvcResult postLeaguesRes = doOn(mvc).withHeader(user.getToken())
                .post(leaguesUrl, newLeague)
                .andExpect(status().isOk())
                .andReturn();

        LeagueDto league = parse(postLeaguesRes, LeagueDto.class);

        final String joinUrl = "/leagues/" + league.getID() + "/join";
        final JoinRequestBody joinRequest = new JoinRequestBody(league.getCode());

        final String wrongJoinUrl = "/leagues/" + league.getID() + "-INVALID" + "/join";
        final JoinRequestBody wrongCodeJoinRequest = new JoinRequestBody(league.getCode() + "-WRONG");

        doOn(mvc).post(joinUrl, joinRequest)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .post(joinUrl, joinRequest)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(userOther.getToken())
                .post(joinUrl, wrongCodeJoinRequest)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(userOther.getToken())
                .post(wrongJoinUrl, joinRequest)
                .andExpect(status().is4xxClientError());

        // join, successful
        doOn(mvc).withHeader(userOther.getToken())
                .post(joinUrl, joinRequest)
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    LeagueDto joinLeague = parse(mvcResult, LeagueDto.class);

                    Assertions.assertEquals(league.getID(), joinLeague.getID());
                    Assertions.assertEquals(league.getName(), joinLeague.getName());
                    Assertions.assertEquals(user.getUserID(), joinLeague.getAdminID());
                    Assertions.assertEquals(2, joinLeague.getNumberOfMembers());
                    Assertions.assertNotNull(joinLeague.getCode());
                });


        // check two members
        final ResultMatcher checkEqualAccounts = mvcResult -> {
            List<AccountDto> accounts = parse(mvcResult, new TypeReference<List<AccountDto>>() {});
            List<AccountDto> expectedAccounts = Stream.of(user, userOther)
                    .map(account -> new AccountDto(account.getUserID(), account.getUsername()))
                    .collect(Collectors.toList());

            accounts.sort(Comparator.comparing(AccountDto::getUsername));
            expectedAccounts.sort(Comparator.comparing(AccountDto::getUsername));

            Assertions.assertEquals(2, accounts.size());

            for (int i = 0 ; i < accounts.size() ; i++) {
                Assertions.assertEquals(expectedAccounts.get(i).getId(), accounts.get(i).getId());
                Assertions.assertEquals(expectedAccounts.get(i).getUsername(), accounts.get(i).getUsername());
            }
        };

        // Get Users
        final String usersUrl = "/leagues/" + league.getID() + "/users";
        final String wrongUsersUrl = "/leagues/" + league.getID() + "-INVALID" + "/users";

        doOn(mvc).withHeader(userOther.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(checkEqualAccounts);

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(checkEqualAccounts);

        doOn(mvc).get(usersUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .get(usersUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .get(wrongUsersUrl)
                .andExpect(status().is4xxClientError());

        //
        // update name
        league.setName("ANOTHER NAME");

        final String leagueUrl = "/leagues/" + league.getID();
        final String wrongLeagueUrl = "/leagues/" + league.getID() + "-INVALID";
        doOn(mvc).withHeader(user.getToken())
                .put(wrongLeagueUrl, league)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(userOther.getToken())
                .put(leagueUrl, league)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .put(leagueUrl, league)
                .andExpect(status().is4xxClientError());

        doOn(mvc).put(leagueUrl, league)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .put(leagueUrl, league)
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    LeagueDto updatedLeague = parse(mvcResult, LeagueDto.class);

                    Assertions.assertEquals(league.getName(), updatedLeague.getName());
                    Assertions.assertEquals(league.getAdminID(), updatedLeague.getAdminID());
                    Assertions.assertEquals(user.getUserID(), updatedLeague.getAdminID());
                    Assertions.assertEquals(2, updatedLeague.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode(), updatedLeague.getCode());
                    Assertions.assertNotNull(league.getCode());
                });

        // Get leagues

        doOn(mvc).withHeader(user.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<LeagueDto> leagues = parse(mvcResult, new TypeReference<List<LeagueDto>>(){});
                    Assertions.assertEquals(1, leagues.size());

                    List<LeagueDto> expectedLeagues = Lists.newArrayList(league);
                    leagues.sort(Comparator.comparing(LeagueDto::getName));
                    expectedLeagues.sort(Comparator.comparing(LeagueDto::getName));

                    for (int i = 0 ; i < leagues.size() ; i++) {
                        LeagueDto updatedLeague = leagues.get(i);
                        LeagueDto expectedLeague = expectedLeagues.get(i);
                        Assertions.assertEquals(expectedLeague.getName(), updatedLeague.getName());
                        Assertions.assertEquals(user.getUserID(), updatedLeague.getAdminID());
                        Assertions.assertEquals(2, updatedLeague.getNumberOfMembers());
                        Assertions.assertNotNull(updatedLeague.getCode());
                    }
                });

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(checkEqualAccounts);


        //
        // delete league name
        doOn(mvc).withHeader(user.getToken())
                .delete(wrongLeagueUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(userOther.getToken())
                .delete(leagueUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .delete(leagueUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).delete(leagueUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .delete(leagueUrl)
                .andExpect(status().isOk());

        doOn(mvc).withHeader(user.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<LeagueDto> leagues = parse(mvcResult, new TypeReference<List<LeagueDto>>(){});
                    Assertions.assertEquals(0, leagues.size());
                });

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().is4xxClientError());

    }

    @Test
    void leaveLeague() throws Exception {

        LeagueDto newLeague = new LeagueDto("League Name");

        MvcResult postLeaguesRes = mvc.perform(MockMvcRequestBuilders.post("/leagues/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(newLeague))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        LeagueDto league = parse(postLeaguesRes, LeagueDto.class);

        JoinRequestBody joinRequest = new JoinRequestBody(league.getCode());

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
                    Assertions.assertEquals(3, parse(mvcResult, new TypeReference<List<AccountDto>>() {}).size());
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
                    Assertions.assertEquals(2, parse(mvcResult, new TypeReference<List<AccountDto>>() {}).size());
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