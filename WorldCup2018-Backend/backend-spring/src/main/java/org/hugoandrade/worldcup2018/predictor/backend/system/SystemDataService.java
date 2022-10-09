package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.hugoandrade.worldcup2018.predictor.backend.prediction.*;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.TournamentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SystemDataService {

	@Autowired private SystemDataRepository systemDataRepository;

	@Autowired private PredictionScoresProcessingService predictionScoresProcessing;
	@Autowired private TournamentProcessingService tournamentProcessing;
	@Autowired private UsersScoreProcessingService usersScoreProcessing;

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

	public SystemData setSystemData(SystemData systemData) {

		systemDataRepository.deleteAll();
		SystemData dbSystemData = systemDataRepository.save(systemData);
		return dbSystemData;
	}

	public void hardReset() {

		// update tournament
		tournamentProcessing.updateOrder();

		// reset predictions' scores
		predictionScoresProcessing.resetScores();

		// update users' scores
		usersScoreProcessing.resetScores();
	}
}
