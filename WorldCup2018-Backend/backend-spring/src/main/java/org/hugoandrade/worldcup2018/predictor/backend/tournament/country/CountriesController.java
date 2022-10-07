package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountriesController {

	@Autowired
	private CountryRepository countryRepository;

	@GetMapping("/")
	public List<Country> all() {
		return countryRepository.findAllAsList();
	}

	@PostMapping("/")
	public Country addOne(@RequestBody Country country) {
		Country resCountry = countryRepository.save(country);
		return resCountry;
	}

	@DeleteMapping("/")
	public void deleteAll() {
		countryRepository.deleteAll();
	}

	@GetMapping("/{countryID}")
	public Country getOne(@PathVariable("countryID") String countryID) {

		Country country = countryRepository.findCountryById(countryID);

		if (country == null) return null;

		return country;
	}

	@DeleteMapping("/{countryID}")
	public Country deleteOne(@PathVariable("countryID") String countryID) {

		Country country = countryRepository.findCountryById(countryID);
		if (country == null) return null;
		countryRepository.deleteById(countryID);
		return country;
	}

	@PutMapping("/{countryID}")
	public Country updateOne(@PathVariable("countryID") String countryID,
							 @RequestBody Country country) {

		Country dbCountry = countryRepository.findCountryById(countryID);
		if (dbCountry == null) return null;

		country.setID(countryID);

		Country resCountry = countryRepository.save(country);
		return resCountry;
	}

}
