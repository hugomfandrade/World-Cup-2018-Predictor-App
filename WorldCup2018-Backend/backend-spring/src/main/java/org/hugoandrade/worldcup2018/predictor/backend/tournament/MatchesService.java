package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.prediction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MatchesService {

	@Autowired private MatchRepository matchRepository;

	@Autowired private PredictionScoresProcessingService predictionScoresProcessing;
	@Autowired private TournamentProcessingService tournamentProcessing;
	@Autowired private UsersScoreProcessingService usersScoreProcessing;
	@Autowired private LeagueUsersScoreProcessingService leagueUsersScoreProcessing;

	public List<Match> getAll() {
		return matchRepository.findAllAsList();
	}

	public Match addOne(Match match) {
		Match resMatch = matchRepository.save(match);
		return resMatch;
	}

	public void deleteAll() {
		matchRepository.deleteAll();
	}

	public Match getOne(int matchNumber) {

		Match Match = matchRepository.findByMatchNumber(matchNumber);

		if (Match == null) return null;

		return Match;
	}

	public List<Match> getGreaterThan(Date date) {
		return matchRepository.findGreatThan(date);
	}

	public Match deleteOne(int matchNumber) {

		Match match = matchRepository.findByMatchNumber(matchNumber);
		if (match == null) return null;
		matchRepository.deleteByMatchNumber(matchNumber);
		return match;
	}

	public Match updateOne(int matchNumber, Match match) {

		Match dbMatch = matchRepository.findByMatchNumber(matchNumber);
		if (dbMatch == null) return null;

		match.setMatchNumber(matchNumber);

		Match resMatch = matchRepository.save(match);

		// update tournament
		tournamentProcessing.updateOrder();

		// update predictions
		predictionScoresProcessing.resetScores(matchNumber);

		// update accounts
		usersScoreProcessing.updateScores();

		// update leagues
		leagueUsersScoreProcessing.updateScores();

		return resMatch;
	}
}
