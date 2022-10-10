package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class CountriesController {

	@Autowired
	private CountriesService countriesService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/")
	public List<CountryDto> all() {
		return countriesService.getAll().stream()
				.map(country -> modelMapper.map(country, CountryDto.class))
				.collect(Collectors.toList());
	}

	@PostMapping("/")
	public CountryDto addOne(@RequestBody CountryDto country) {
		Country resCountry = countriesService.addOne(modelMapper.map(country, Country.class));
		return modelMapper.map(resCountry, CountryDto.class);
	}

	@DeleteMapping("/")
	public void deleteAll() {
		countriesService.deleteAll();
	}

	@GetMapping("/{countryID}")
	public CountryDto getOne(@PathVariable("countryID") String countryID) {
		Country country = countriesService.getOne(countryID);
		return modelMapper.map(country, CountryDto.class);
	}

	@DeleteMapping("/{countryID}")
	public CountryDto deleteOne(@PathVariable("countryID") String countryID) {
		Country country = countriesService.deleteOne(countryID);
		return modelMapper.map(country, CountryDto.class);
	}

	@PutMapping("/{countryID}")
	public CountryDto updateOne(@PathVariable("countryID") String countryID,
							 @RequestBody CountryDto country) {
		Country dbCountry = countriesService.updateOne(countryID, modelMapper.map(country, Country.class));
		return modelMapper.map(dbCountry, CountryDto.class);
	}

}
