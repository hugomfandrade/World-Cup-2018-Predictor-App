package org.hugoandrade.worldcup2018.predictor.backend.league;

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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.ListResultMatchers.list;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ObjResultMatchers.obj;
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

        final String leaguesUrl = "/leagues/";
        doOn(mvc).get(leaguesUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(list(LeagueDto.class).assertSize(0));

        doOn(mvc).withHeader(admin.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(list(LeagueDto.class).assertSize(0));
    }

    @Test
    void createLeagues() throws Exception {

        final String leaguesUrl = "/leagues/";
        final LeagueDto newLeague = new LeagueDto("League Name");

        doOn(mvc).post(leaguesUrl, newLeague)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .post(leaguesUrl, newLeague)
                .andExpect(status().isOk())
                .andExpect(obj(LeagueDto.class).equals(newLeague, LeagueDto::getName))
                .andExpect(obj(LeagueDto.class).equalsValue(user.getUserID(), LeagueDto::getAdminID))
                .andExpect(obj(LeagueDto.class).equalsValue(1, LeagueDto::getNumberOfMembers))
                .andExpect(obj(LeagueDto.class).notNull(LeagueDto::getCode));

        doOn(mvc).withHeader(admin.getToken())
                .post(leaguesUrl, newLeague)
                .andExpect(status().isOk())
                .andExpect(obj(LeagueDto.class).addDo((league) -> {
                    Assertions.assertEquals(newLeague.getName(), league.getName());
                    Assertions.assertEquals(admin.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                }));

        // get route
        doOn(mvc).withHeader(user.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(list(LeagueDto.class).addDo((leagues) -> {
                    Assertions.assertEquals(1, leagues.size());

                    LeagueDto league = leagues.get(0);
                    Assertions.assertEquals(newLeague.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                }));
    }

    @Test
    void createLeagues_Multiple() throws Exception {

        final String leaguesUrl = "/leagues/";
        LeagueDto newLeague01 = new LeagueDto("League Name 01");
        LeagueDto newLeague02 = new LeagueDto("League Name 02");

        doOn(mvc).withHeader(user.getToken())
                .post(leaguesUrl, newLeague01)
                .andExpect(status().isOk())
                .andExpect(obj(LeagueDto.class).addDo((league) -> {
                    Assertions.assertEquals(newLeague01.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                }));

        doOn(mvc).withHeader(user.getToken())
                .post(leaguesUrl, newLeague02)
                .andExpect(status().isOk())
                .andExpect(obj(LeagueDto.class).addDo((league) -> {
                    Assertions.assertEquals(newLeague02.getName(), league.getName());
                    Assertions.assertEquals(user.getUserID(), league.getAdminID());
                    Assertions.assertEquals(1, league.getNumberOfMembers());
                    Assertions.assertNotNull(league.getCode());
                }));


        // get route
        doOn(mvc).withHeader(user.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(list(LeagueDto.class).addDo((leagues) -> {
                    List<LeagueDto> expectedLeagues = Lists.newArrayList(newLeague01, newLeague02);

                    leagues.sort(Comparator.comparing(LeagueDto::getName));
                    expectedLeagues.sort(Comparator.comparing(LeagueDto::getName));

                    Assertions.assertEquals(2, leagues.size());

                    for (int i = 0 ; i < 2 ; i++) {
                        LeagueDto league = leagues.get(i);
                        LeagueDto expectedLeague = expectedLeagues.get(i);
                        Assertions.assertEquals(expectedLeague.getName(), league.getName());
                        Assertions.assertEquals(user.getUserID(), league.getAdminID());
                        Assertions.assertEquals(1, league.getNumberOfMembers());
                        Assertions.assertNotNull(league.getCode());
                    }
                }));
    }

    @Test
    void leagueLifecycle() throws Exception {

        LeagueDto newLeague = new LeagueDto("League Name");

        final String leaguesUrl = "/leagues/";

        LeagueDto league = parse(doOn(mvc).withHeader(user.getToken())
                .post(leaguesUrl, newLeague)
                .andExpect(status().isOk())
                .andReturn(), LeagueDto.class);

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
                .andExpect(obj(LeagueDto.class).addDo((joinLeague) -> {

                    Assertions.assertEquals(league.getID(), joinLeague.getID());
                    Assertions.assertEquals(league.getName(), joinLeague.getName());
                    Assertions.assertEquals(user.getUserID(), joinLeague.getAdminID());
                    Assertions.assertEquals(2, joinLeague.getNumberOfMembers());
                    Assertions.assertNotNull(joinLeague.getCode());
                }));


        // check two members
        final List<AccountDto> expectedAccounts = Stream.of(user, userOther)
                .map(account -> new AccountDto(account.getUserID(), account.getUsername(), 0, 1))
                .collect(Collectors.toList());

        // Get Users
        final String usersUrl = "/leagues/" + league.getID() + "/users";
        final String wrongUsersUrl = "/leagues/" + league.getID() + "-INVALID" + "/users";

        doOn(mvc).withHeader(userOther.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(list(AccountDto.class)
                        .assertEquals(expectedAccounts, Comparator.comparing(AccountDto::getUsername)));

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(list(AccountDto.class)
                        .assertEquals(expectedAccounts, Comparator.comparing(AccountDto::getUsername)));

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
                .andExpect(obj(LeagueDto.class).addDo((updatedLeague) -> {
                    Assertions.assertEquals(league.getName(), updatedLeague.getName());
                    Assertions.assertEquals(league.getAdminID(), updatedLeague.getAdminID());
                    Assertions.assertEquals(user.getUserID(), updatedLeague.getAdminID());
                    Assertions.assertEquals(2, updatedLeague.getNumberOfMembers());
                    Assertions.assertEquals(league.getCode(), updatedLeague.getCode());
                }));

        // Get leagues
        doOn(mvc).withHeader(user.getToken())
                .get(leaguesUrl)
                .andExpect(status().isOk())
                .andExpect(list(LeagueDto.class).addDo((leagues) -> {
                    List<LeagueDto> expectedLeagues = Lists.newArrayList(league);

                    leagues.sort(Comparator.comparing(LeagueDto::getName));
                    expectedLeagues.sort(Comparator.comparing(LeagueDto::getName));

                    Assertions.assertEquals(1, leagues.size());

                    for (int i = 0 ; i < leagues.size() ; i++) {
                        LeagueDto updatedLeague = leagues.get(i);
                        LeagueDto expectedLeague = expectedLeagues.get(i);
                        Assertions.assertEquals(expectedLeague.getName(), updatedLeague.getName());
                        Assertions.assertEquals(user.getUserID(), updatedLeague.getAdminID());
                        Assertions.assertEquals(2, updatedLeague.getNumberOfMembers());
                        Assertions.assertNotNull(updatedLeague.getCode());
                    }
                }));

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(list(AccountDto.class)
                        .assertEquals(expectedAccounts, Comparator.comparing(AccountDto::getUsername)));


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
                .andExpect(list(LeagueDto.class).assertSize(0));

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().is4xxClientError());

    }

    @Test
    void leaveLeague() throws Exception {

        final String leaguesUrl = "/leagues/";

        LeagueDto newLeague = new LeagueDto("League Name");
        LeagueDto league = parse(doOn(mvc).withHeader(user.getToken())
                .post(leaguesUrl, newLeague)
                .andExpect(status().isOk())
                .andReturn(), LeagueDto.class);

        JoinRequestBody joinRequest = new JoinRequestBody(league.getCode());

        // join, successful
        final String leagueUrl = "/leagues/" + league.getID();
        final String joinUrl = "/leagues/" + league.getID() + "/join";
        final String usersUrl = "/leagues/" + league.getID() + "/users";
        final String usersUrlInvalid = "/leagues/" + league.getID() + "-INVALID" + "/users";

        doOn(mvc).withHeader(admin.getToken())
                .post(joinUrl, joinRequest)
                .andExpect(status().isOk());

        // leave before joining, should error
        doOn(mvc).withHeader(userOther.getToken())
                .delete(usersUrl)
                .andExpect(status().isOk());

        doOn(mvc).delete(usersUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(userOther.getToken())
                .delete(usersUrlInvalid)
                .andExpect(status().is4xxClientError());

        // join another
        doOn(mvc).withHeader(userOther.getToken())
                .post(joinUrl, joinRequest)
                .andExpect(status().isOk());

        doOn(mvc).withHeader(userOther.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(list(AccountDto.class).assertSize(3));

        // leave as member
        doOn(mvc).withHeader(userOther.getToken())
                .delete(usersUrl)
                .andExpect(status().isOk());

        doOn(mvc).withHeader(userOther.getToken())
                .get(usersUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .get(usersUrl)
                .andExpect(status().isOk())
                .andExpect(list(AccountDto.class).assertSize(2));

        // leave as admin, should delete league, no longer available
        doOn(mvc).withHeader(user.getToken())
                .delete(usersUrl)
                .andExpect(status().isOk());

        doOn(mvc).withHeader(user.getToken())
                .get(usersUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .delete(usersUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .delete(leagueUrl)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken())
                .get(leagueUrl)
                .andExpect(status().is4xxClientError());
    }
}