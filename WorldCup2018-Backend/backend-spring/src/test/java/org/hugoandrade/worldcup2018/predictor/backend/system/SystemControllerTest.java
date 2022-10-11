package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryDto;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BiFunctionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ObjResultMatchers.obj;
import static org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country.Tournament.*;
import static org.hugoandrade.worldcup2018.predictor.backend.tournament.TournamentProcessingTest.standingsDetails;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SystemControllerTest extends BaseControllerTest {

	@Autowired private MockMvc mvc;

	@Autowired private ModelMapper modelMapper;

	@Test
	public void getHello() throws Exception {
		doOn(mvc).get("/")
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from WorldCup 2018 (Spring Boot)!")));
	}

	@Test
	void getSystemData() throws Exception {

		final SystemDataDto expectedSystemData = new SystemDataDto("0,1,2,4", true, Date.from(Instant.now().truncatedTo(ChronoUnit.SECONDS)));

		doOn(mvc).withHeader(admin.getToken())
				.post("/system-data/", expectedSystemData)
				.andExpect(status().isOk());

		doOn(mvc).get("/system-data/")
				.andExpect(status().isOk())
				.andExpect(obj(SystemDataDto.class).assertEquals(expectedSystemData));

		doOn(mvc).withHeader(user.getToken()).get("/system-data/")
				.andExpect(status().isOk())
				.andExpect(obj(SystemDataDto.class).assertEquals(expectedSystemData));

		doOn(mvc).withHeader(admin.getToken()).get("/system-data/")
				.andExpect(status().isOk())
				.andExpect(obj(SystemDataDto.class).assertEquals(expectedSystemData));

		startupScript.startup();
	}

	@Test
	void postSystemData() throws Exception {

		final Date date = ISO8601Utils.parse("2018-06-27T12:00:00Z");

		final SystemDataDto expectedSystemData = new SystemDataDto("0,1,2,4", true, date);

		doOn(mvc).post("/system-data/", expectedSystemData)
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(user.getToken())
				.post("/system-data/", expectedSystemData)
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(admin.getToken())
				.post("/system-data/", expectedSystemData)
				.andExpect(status().isOk())
				.andExpect(obj(SystemDataDto.class).assertEquals(expectedSystemData));

		startupScript.startup();
	}

	private final static Map<Integer, Integer[]> SCORES_GROUP_B = new HashMap<>();
	static {
		SCORES_GROUP_B.put(4, new Integer[]{0, 1});
		SCORES_GROUP_B.put(3, new Integer[]{3, 3});
		SCORES_GROUP_B.put(19, new Integer[]{1, 0});
		SCORES_GROUP_B.put(20, new Integer[]{0, 1});
		SCORES_GROUP_B.put(35, new Integer[]{1, 1});
		SCORES_GROUP_B.put(36, new Integer[]{2, 2});
	}

	@Autowired SystemController systemController;
	@Autowired PredictionRepository predictionRepository;
	@Autowired MatchRepository matchRepository;

	@Test
	void hardReset_GroupB() throws Exception {

		final List<MatchDto> matches = getMatches();
		final Map<Integer, MatchDto> matchMap = matches.stream()
				.collect(Collectors.toMap(MatchDto::getMatchNumber, Function.identity()));

		// put predictions, in repository
		final BiConsumer<LoginData, Prediction> putPrediction = (loginData, prediction) -> {
			prediction.setUserID(loginData.getUserID());
			predictionRepository.save(prediction);
		};

		//
		// correct prediction
		putPrediction.accept(user, new Prediction(0, 1, 4));
		// correct margin of victory
		putPrediction.accept(user, new Prediction(2, 2, 3));
		// correct outcome
		putPrediction.accept(user, new Prediction(2, 0, 19));
		// incorrect
		putPrediction.accept(user, new Prediction(2, 1, 20));
		// incomplete
		putPrediction.accept(user, new Prediction(-1, 1, 35));

		// insert matches, in repository
		for (Map.Entry<Integer, Integer[]> scoreEntry : SCORES_GROUP_B.entrySet()) {

			final MatchDto match = matchMap.get(scoreEntry.getKey());

			match.setScore(scoreEntry.getValue()[0], scoreEntry.getValue()[1]);

			matchRepository.save(modelMapper.map(match, Match.class));
		}

		// hard-reset
		doOn(mvc).post("/reset-all")
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(admin.getToken())
				.post("/reset-all")
				.andExpect(status().isOk());

		// verify

		// get again from rest api
		final List<CountryDto> countries = getCountries();
		final Map<String, CountryDto> countryMap = countries.stream()
				.collect(Collectors.toMap(CountryDto::getName, Function.identity()));

		matches.clear();
		matches.addAll(getMatches());
		matchMap.clear();
		matchMap.putAll(matches.stream()
				.collect(Collectors.toMap(MatchDto::getMatchNumber, Function.identity())));


		Assertions.assertEquals(1, countryMap.get(Spain.name).getPosition());
		Assertions.assertEquals(2, countryMap.get(Portugal.name).getPosition());
		Assertions.assertEquals(3, countryMap.get(Iran.name).getPosition());
		Assertions.assertEquals(4, countryMap.get(Morocco.name).getPosition());

		Assertions.assertArrayEquals(new int[]{3, 1, 2, 0, 6, 5, 1, 5}, standingsDetails(countryMap.get(Spain.name)));
		Assertions.assertArrayEquals(new int[]{3, 1, 2, 0, 5, 4, 1, 5}, standingsDetails(countryMap.get(Portugal.name)));
		Assertions.assertArrayEquals(new int[]{3, 1, 1, 1, 2, 2, 0, 4}, standingsDetails(countryMap.get(Iran.name)));
		Assertions.assertArrayEquals(new int[]{3, 0, 1, 2, 2, 4, -2, 1}, standingsDetails(countryMap.get(Morocco.name)));

		Assertions.assertEquals(matchMap.get(49).getAwayTeamID(), countryMap.get(Portugal.name).getID());
		Assertions.assertEquals(matchMap.get(51).getHomeTeamID(), countryMap.get(Spain.name).getID());


		final SystemDataDto systemData = systemController.getSystemData();
		final Rules rules = systemData.getRules();

		int incorrectPrediction = rules.getRuleIncorrectPrediction();
		int correctOutcome = rules.getRuleCorrectOutcome();
		int correctMarginOfVictory = rules.getRuleCorrectMarginOfVictory();
		int correctPrediction = rules.getRuleCorrectPrediction();

		final BiFunctionException<Integer, LoginData, Prediction> getPrediction = (matchNumber, loginData) -> {

			return parseList(doOn(mvc).withHeader(loginData.getToken())
						.get("/predictions/" + loginData.getUserID())
						.andExpect(status().isOk())
						.andReturn(), Prediction.class)
					.stream()
					.filter(prediction -> prediction.getMatchNumber() == matchNumber)
					.findAny()
					.orElse(null);
		};

		Assertions.assertEquals(correctPrediction, getPrediction.apply(4, user).getScore());
		Assertions.assertEquals(correctMarginOfVictory, getPrediction.apply(3, user).getScore());
		Assertions.assertEquals(correctOutcome, getPrediction.apply(19, user).getScore());
		Assertions.assertEquals(incorrectPrediction, getPrediction.apply(20, user).getScore());
		Assertions.assertEquals(0, getPrediction.apply(35, user).getScore());
		
		int expectedScore = incorrectPrediction + correctOutcome + correctMarginOfVictory + correctPrediction;

		Assertions.assertEquals(0, getAccount(admin.getUsername()).getScore());
		Assertions.assertEquals(expectedScore, getAccount(user.getUsername()).getScore());
		Assertions.assertEquals(0, getAccount(userOther.getUsername()).getScore());
	}

	private AccountDto getAccount(String username) throws Exception {

		MvcResult mvcResult = doOn(mvc).withHeader(admin.getToken())
				.get("/auth/accounts")
				.andExpect(status().isOk())
				.andReturn();

		return parseList(mvcResult, AccountDto.class)
				.stream()
				.filter(account -> username.equals(account.getUsername()))
				.findAny()
				.orElse(null);
	}

	private List<MatchDto> getMatches() throws Exception {

		MvcResult mvcResult = doOn(mvc).withHeader(admin.getToken())
				.get("/matches/")
				.andExpect(status().isOk())
				.andReturn();

		return parseList(mvcResult, MatchDto.class);
	}

	private List<CountryDto> getCountries() throws Exception {

		MvcResult mvcResult = doOn(mvc).withHeader(admin.getToken())
				.get("/countries/")
				.andExpect(status().isOk())
				.andReturn();

		return parseList(mvcResult, CountryDto.class);
	}
}
