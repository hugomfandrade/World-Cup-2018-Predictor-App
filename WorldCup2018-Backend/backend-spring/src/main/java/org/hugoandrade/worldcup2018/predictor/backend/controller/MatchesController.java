package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchesController {

	@Autowired
	private MatchRepository matchRepository;

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
		return resMatch;
	}

}
