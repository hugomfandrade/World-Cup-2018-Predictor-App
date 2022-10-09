package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class PredictionsService {

	@Autowired private SystemDataService systemDataService;

	@Autowired private MatchesService matchesService;

	@Autowired private PredictionRepository predictionRepository;

	public List<Match> enabledMatches() {
		Date date = systemDataService.getSystemData().getDate();
		return matchesService.getGreaterThan(date);
	}

	public int[] enabledMatchNumbers() {
		return this.enabledMatches()
				.stream()
				.mapToInt(Match::getMatchNumber)
				.toArray();
	}

	public List<Prediction> getAll() {
		return predictionRepository.findAllAsList();
	}

	public List<Prediction> getPredictions(String userID) {
		return predictionRepository.findByUserID(userID);
	}

	public List<Prediction> getPredictions(String userID, int[] matchNumbers) {
		return predictionRepository.findByUserIDAndMatchNumbers(userID, matchNumbers);
	}

	public List<Prediction> getPredictions(String userID, boolean enabledMatches) {
		if (enabledMatches) {
			return this.getPredictions(userID, enabledMatchNumbers());
		}
		else {
			return this.getPredictions(userID);
		}
	}

	// insert or... update
	public Prediction insert(String userID, Prediction prediction) {
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
	public Prediction update(String userID, Prediction prediction) {
		return insert(userID, prediction);
	}

}
