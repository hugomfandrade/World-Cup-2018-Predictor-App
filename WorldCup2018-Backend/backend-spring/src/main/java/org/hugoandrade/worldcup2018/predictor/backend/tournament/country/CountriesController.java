package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountriesController {

	@Autowired
	private CountriesService countriesService;

	@GetMapping("/")
	public List<Country> all() {
		return countriesService.getAll();
	}

	@PostMapping("/")
	public Country addOne(@RequestBody Country country) {
		Country resCountry = countriesService.addOne(country);
		return resCountry;
	}

	@DeleteMapping("/")
	public void deleteAll() {
		countriesService.deleteAll();
	}

	@GetMapping("/{countryID}")
	public Country getOne(@PathVariable("countryID") String countryID) {
		Country country = countriesService.getOne(countryID);
		return country;
	}

	@DeleteMapping("/{countryID}")
	public Country deleteOne(@PathVariable("countryID") String countryID) {
		Country country = countriesService.deleteOne(countryID);
		return country;
	}

	@PutMapping("/{countryID}")
	public Country updateOne(@PathVariable("countryID") String countryID,
							 @RequestBody Country country) {
		Country dbCountry = countriesService.updateOne(countryID, country);
		return dbCountry;
	}

}
