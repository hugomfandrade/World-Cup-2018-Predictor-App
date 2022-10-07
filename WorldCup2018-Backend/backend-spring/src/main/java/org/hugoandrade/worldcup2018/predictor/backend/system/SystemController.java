package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionScoresProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.TournamentProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.UsersScoreProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchRepository;
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


	@Autowired private AccountRepository accountRepository;
	@Autowired private PredictionRepository predictionRepository;
	@Autowired private CountryRepository countryRepository;
	@Autowired private MatchRepository matchRepository;

	private final TournamentProcessing tournamentProcessing = new TournamentProcessing(new TournamentProcessing.OnProcessingListener() {

		@Override
		public void onProcessingFinished(List<Country> countries, List<Match> matches) {
			for (Country country : countries) {
				Country dbCountry = countryRepository.findCountryById(country.getID());
				countryRepository.save(country);
			}
			for (Match match : matches) {
				Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
				matchRepository.save(match);
			}
		}

		@Override public void updateCountry(Country country) { }
		@Override public void updateMatchUp(Match match) { }
	});

	private final PredictionScoresProcessing predictionScoresProcessing = new PredictionScoresProcessing(new PredictionScoresProcessing.OnProcessingListener() {

		@Override
		public void onProcessingFinished(List<Prediction> predictions) {
			for (Prediction prediction : predictions) {
				Prediction dbPrediction = predictionRepository.findById(prediction.getID()).get();
				predictionRepository.save(prediction);
			}
		}

		@Override public void updatePrediction(Prediction prediction) {}
	});

	private final UsersScoreProcessing usersScoreProcessing = new UsersScoreProcessing(new UsersScoreProcessing.OnProcessingListener() {

		@Override
		public void onProcessingFinished(List<Account> accounts) {
			for (Account account : accounts) {
				Account dbAccount = accountRepository.findByUsername(account.getUsername());
				accountRepository.save(account);
			}
		}

		@Override public void updateAccount(Account account) { }
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

		// update users' scores
		usersScoreProcessing.startUpdateUsersScoresSync(
				predictionRepository.findAllAsList(),
				accountRepository.findAllAsList().toArray(new Account[0]));

		return "Tournament Updated, Scores of Predictions Updated !";
	}

}
