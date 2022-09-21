package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.model.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.model.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.processing.PredictionScoresProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.processing.TournamentProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.repository.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.SystemDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class SystemController {

	@Autowired private SystemDataRepository systemDataRepository;

	@RequestMapping("/")
	public String index() {
		return "Greetings from WorldCup 2018 (Spring Boot)!";
	}

	@GetMapping("/system-data")
	public SystemData getSystemData() {

		SystemData systemData = systemDataRepository.findAllAsList().stream()
				.findFirst()
				.orElse(null);

		if (systemData == null) {
			systemData = new SystemData(null, "0,1,2,4", true, new Date(), new Date());
			systemData = systemDataRepository.save(systemData);
		}

		return systemData;
	}

	@PostMapping("/system-data")
	public SystemData postSystemData(@RequestBody SystemData systemData) {

		systemDataRepository.deleteAll();
		SystemData dbSystemData = systemDataRepository.save(systemData);
		return dbSystemData;
	}


	@Autowired private PredictionRepository predictionRepository;
	@Autowired private CountryRepository countryRepository;
	@Autowired private MatchRepository matchRepository;

	private final TournamentProcessing tournamentProcessing = new TournamentProcessing(new TournamentProcessing.OnProcessingListener() {

		@Override public void onProcessingFinished(List<Country> countries, List<Match> matches) {}

		@Override
		public void updateCountry(Country country) {
			Country dbCountry = countryRepository.findCountryById(country.getID());
			countryRepository.save(country);
		}

		@Override
		public void updateMatchUp(Match match) {
			Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
			matchRepository.save(match);
		}
	});

	private final PredictionScoresProcessing predictionScoresProcessing = new PredictionScoresProcessing(new PredictionScoresProcessing.OnProcessingListener() {

		@Override public void onProcessingFinished(List<Prediction> predictions) {}

		@Override
		public void updatePrediction(Prediction prediction) {
			Prediction dbPrediction = predictionRepository.findById(prediction.getID()).get();
			predictionRepository.save(prediction);
		}
	});


	@PostMapping("/reset-all")
	public String hardReset() {

		final SystemData systemData = this.getSystemData();
		final List<Match> matches = matchRepository.findAllAsList();

		// update tournament
		tournamentProcessing.startUpdateGroupsSync(countryRepository.findAllAsList(), matches);

		for (Match match : matches) {

			// update predictions
			predictionScoresProcessing.startUpdatePredictionScoresSync(
					systemData,
					matchRepository.findByMatchNumber(match.getMatchNumber()),
					predictionRepository.findByMatchNumber(match.getMatchNumber()));

		}

		return "Tournament Updated, Scores of Predictions Updated !";
	}

}
