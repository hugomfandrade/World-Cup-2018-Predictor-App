package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchesController {

	@Autowired MatchesService matchesService;

	@GetMapping("/")
	public List<Match> all() {
		return matchesService.getAll();
	}

	@PostMapping("/")
	public Match addOne(@RequestBody Match match) {
		Match resMatch = matchesService.addOne(match);
		return resMatch;
	}

	@DeleteMapping("/")
	public void deleteAll() {
		matchesService.deleteAll();
	}

	@GetMapping("/{matchNumber}")
	public Match getOne(@PathVariable("matchNumber") int matchNumber) {
		Match match = matchesService.getOne(matchNumber);
		return match;
	}

	@DeleteMapping("/{matchNumber}")
	public Match deleteOne(@PathVariable("matchNumber") int matchNumber) {
		Match match = matchesService.deleteOne(matchNumber);
		return match;
	}

	@PutMapping("/{matchNumber}")
	public Match updateOne(@PathVariable("matchNumber") int matchNumber, @RequestBody Match match) {
		Match resMatch = matchesService.updateOne(matchNumber, match);
		return resMatch;
	}

}
