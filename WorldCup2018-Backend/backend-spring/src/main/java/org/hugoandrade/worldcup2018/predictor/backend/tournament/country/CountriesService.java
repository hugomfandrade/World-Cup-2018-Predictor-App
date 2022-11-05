package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountriesService {

	@Autowired
	private CountryRepository countryRepository;

	public List<Country> getAll() {
		return countryRepository.findAllAsList();
	}

	public Country addOne(Country country) {
		Country resCountry = countryRepository.save(country);
		return resCountry;
	}

	public void deleteAll() {
		countryRepository.deleteAll();
	}

	public Country getOne(String countryID) {

		Country country = countryRepository.findCountryById(countryID);

		if (country == null) return null;

		return country;
	}

	public Country deleteOne(String countryID) {
		Country country = countryRepository.findCountryById(countryID);
		if (country == null) return null;

		countryRepository.deleteById(countryID);
		return country;
	}

	public Country updateOne(String countryID, Country country) {

		Country dbCountry = countryRepository.findCountryById(countryID);
		if (dbCountry == null) return null;

		country.setID(countryID);

		Country resCountry = countryRepository.save(country);
		return resCountry;
	}

}
