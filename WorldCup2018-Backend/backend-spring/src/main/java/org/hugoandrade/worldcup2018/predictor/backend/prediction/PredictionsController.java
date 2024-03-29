package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/predictions")
public class PredictionsController {

	@Autowired private PredictionsService predictionsService;

	@Autowired private ModelMapper modelMapper;

	@GetMapping("/enabled-matches")
	public List<MatchDto> enabledMatches() {
		return predictionsService.enabledMatches().stream()
				.map(match -> modelMapper.map(match, MatchDto.class))
				.collect(Collectors.toList());
	}

	@GetMapping("/{userID}")
	public List<PredictionDto> getAll(Principal principal,
								   @PathVariable("userID") String requestedUserID) {
		String userID = principal.getName();
		return predictionsService.getPredictions(userID, !StringUtils.equals(userID, requestedUserID))
				.stream()
				.map(prediction -> modelMapper.map(prediction, PredictionDto.class))
				.collect(Collectors.toList());
	}

	// insert or... update
	@PostMapping("/")
	public PredictionDto insert(Principal principal, @RequestBody PredictionDto prediction) {
		final String userID = principal.getName();
		final int predictionNumber = prediction.getMatchNumber();
		final int[] matchNumbers = predictionsService.enabledMatchNumbers();

		if (Arrays.stream(matchNumbers).noneMatch(value -> predictionNumber == value)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can not add prediction to this match number. past the date");
		}

		Prediction dbPrediction = predictionsService.insert(userID, modelMapper.map(prediction, Prediction.class));
		return modelMapper.map(dbPrediction, PredictionDto.class);
	}

	// update or... insert
	@PutMapping("/")
	public PredictionDto update(Principal principal, @RequestBody PredictionDto prediction) {
		return insert(principal, prediction);
	}
}
