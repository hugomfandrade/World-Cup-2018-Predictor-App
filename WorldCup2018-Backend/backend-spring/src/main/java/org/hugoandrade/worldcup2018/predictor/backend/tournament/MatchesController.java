package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/matches")
public class MatchesController {

	@Autowired MatchesService matchesService;

	@Autowired ModelMapper modelMapper;

	@GetMapping("/")
	public List<MatchDto> all() {
		return matchesService.getAll()
				.stream()
				.map(match -> modelMapper.map(match, MatchDto.class))
				.collect(Collectors.toList());
	}

	@PostMapping("/")
	public MatchDto addOne(@RequestBody MatchDto match) {
		Match resMatch = matchesService.addOne(modelMapper.map(match, Match.class));
		return modelMapper.map(resMatch, MatchDto.class);
	}

	@DeleteMapping("/")
	public void deleteAll() {
		matchesService.deleteAll();
	}

	@GetMapping("/{matchNumber}")
	public MatchDto getOne(@PathVariable("matchNumber") int matchNumber) {
		Match match = matchesService.getOne(matchNumber);
		return modelMapper.map(match, MatchDto.class);
	}

	@DeleteMapping("/{matchNumber}")
	public MatchDto deleteOne(@PathVariable("matchNumber") int matchNumber) {
		Match match = matchesService.deleteOne(matchNumber);
		return modelMapper.map(match, MatchDto.class);
	}

	@PutMapping("/{matchNumber}")
	public MatchDto updateOne(@PathVariable("matchNumber") int matchNumber, @RequestBody MatchDto match) {
		Match resMatch = matchesService.updateOne(matchNumber, modelMapper.map(match, Match.class));
		return modelMapper.map(resMatch, MatchDto.class);
	}

}
