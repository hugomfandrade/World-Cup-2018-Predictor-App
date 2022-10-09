package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/predictions")
public class PredictionsController {

	@Autowired private PredictionsService predictionsService;

	@GetMapping("/enabled-matches")
	public List<Match> enabledMatches() {
		return predictionsService.enabledMatches();
	}

	@GetMapping("/{userID}")
	public List<Prediction> getAll(Principal principal,
								   @PathVariable("userID") String requestedUserID) {
		String userID = principal.getName();
		return predictionsService.getPredictions(userID, !StringUtils.equals(userID, requestedUserID));
	}

	// insert or... update
	@PostMapping("/")
	public Prediction insert(Principal principal, @RequestBody Prediction prediction) {
		final String userID = principal.getName();
		final int predictionNumber = prediction.getMatchNumber();
		final int[] matchNumbers = predictionsService.enabledMatchNumbers();

		if (Arrays.stream(matchNumbers).noneMatch(value -> predictionNumber == value)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can not add prediction to this match number. past the date");
		}

		Prediction dbPrediction = predictionsService.insert(userID, prediction);
		return dbPrediction;
	}

	// update or... insert
	@PutMapping("/")
	public Prediction update(Principal principal, @RequestBody Prediction prediction) {
		return insert(principal, prediction);
	}
}
