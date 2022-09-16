package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.model.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/predictions")
public class PredictionsController {

	private final static Date DATE_TIME = ISO8601Utils.parse("2018-06-27T12:00:00Z");

	@Autowired private MatchRepository matchRepository;
	@Autowired private PredictionRepository predictionRepository;

	@GetMapping("/enabled-matches")
	public List<Match> enabledMatches() {
		return matchRepository.findGreatThan(DATE_TIME);
	}

	public int[] enabledMatchNumbers() {
		return this.enabledMatches()
				.stream()
				.mapToInt(Match::getMatchNumber)
				.toArray();
	}

	@GetMapping("/{userID}")
	public List<Prediction> getAll(Principal principal,
								   @PathVariable("userID") String requestedUserID) {
		String userID = principal.getName();
		if (StringUtils.equals(userID, requestedUserID)) {
			return predictionRepository.findByUserID(userID);
		}
		else {
			int[] matchNumbers = enabledMatchNumbers();
			return predictionRepository.findByUserIDAndMatchNumbers(userID, matchNumbers);
		}
	}

	// insert or... update
	@PostMapping("/")
	public Prediction insert(Principal principal, @RequestBody Prediction prediction) {
		final String userID = principal.getName();
		final int predictionNumber = prediction.getMatchNumber();
		final int[] matchNumbers = enabledMatchNumbers();
		if (Arrays.stream(matchNumbers).noneMatch(value -> predictionNumber == value)) {
			throw new IllegalArgumentException("can not add prediction to this match number. past the date");
		}

		Prediction dbPrediction = predictionRepository.findByUserIDAndMatchNumber(userID, predictionNumber);

		// updating
		if (dbPrediction != null) {
			dbPrediction.setHomeTeamGoals(prediction.getHomeTeamGoals());
			dbPrediction.setAwayTeamGoals(prediction.getAwayTeamGoals());
			return predictionRepository.save(dbPrediction);
		}
		else {
			prediction.setUserID(userID);
			return predictionRepository.save(prediction);
		}
	}

	// update or... insert
	@PutMapping("/")
	public Prediction update(Principal principal, @RequestBody Prediction prediction) {
		return insert(principal, prediction);
	}
}
