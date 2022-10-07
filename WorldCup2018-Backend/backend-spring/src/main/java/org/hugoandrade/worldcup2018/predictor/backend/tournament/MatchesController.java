package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionScoresProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.UsersScoreProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchesController {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired private SystemController systemController;
	@Autowired private AccountRepository accountRepository;
	@Autowired private PredictionRepository predictionRepository;
	@Autowired private CountryRepository countryRepository;

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

		@Override public void onProcessingFinished(List<Prediction> predictions) { }

		@Override
		public void updatePrediction(Prediction prediction) {
			Prediction dbPrediction = predictionRepository.findById(prediction.getID()).get();
			predictionRepository.save(prediction);
		}
	});

	private final UsersScoreProcessing usersScoreProcessing = new UsersScoreProcessing(new UsersScoreProcessing.OnProcessingListener() {

		@Override public void onProcessingFinished(List<Account> accounts) {}

		@Override
		public void updateAccount(Account account) {
			Account dbAccount = accountRepository.findByUsername(account.getUsername());
			accountRepository.save(account);
		}
	});

	@GetMapping("/")
	public List<Match> all() {
		return matchRepository.findAllAsList();
	}

	@PostMapping("/")
	public Match addOne(@RequestBody Match Match) {
		Match resMatch = matchRepository.save(Match);
		return resMatch;
	}

	@DeleteMapping("/")
	public void deleteAll() {
		matchRepository.deleteAll();
	}

	@GetMapping("/{matchNumber}")
	public Match getOne(@PathVariable("matchNumber") int matchNumber) {

		Match Match = matchRepository.findByMatchNumber(matchNumber);

		if (Match == null) return null;

		return Match;
	}

	@DeleteMapping("/{matchNumber}")
	public Match deleteOne(@PathVariable("matchNumber") int matchNumber) {

		Match match = matchRepository.findByMatchNumber(matchNumber);
		if (match == null) return null;
		matchRepository.deleteByMatchNumber(matchNumber);
		return match;
	}

	@PutMapping("/{matchNumber}")
	public Match updateOne(@PathVariable("matchNumber") int matchNumber,
						   @RequestBody Match match) {

		Match dbMatch = matchRepository.findByMatchNumber(matchNumber);
		if (dbMatch == null) return null;

		match.setMatchNumber(matchNumber);

		Match resMatch = matchRepository.save(match);

		// update tournament
		tournamentProcessing.startUpdateGroupsSync(
				countryRepository.findAllAsList(),
				matchRepository.findAllAsList());

		// update predictions
		predictionScoresProcessing.startUpdatePredictionScoresSync(
				systemController.getSystemData(),
				matchRepository.findByMatchNumber(matchNumber),
				predictionRepository.findByMatchNumber(matchNumber));

		// update accounts
		usersScoreProcessing.startUpdateUsersScoresSync(
				predictionRepository.findAllAsList(),
				accountRepository.findAllAsList().toArray(new Account[0]));

		return resMatch;
	}

}
