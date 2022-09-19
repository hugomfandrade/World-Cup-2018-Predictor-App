package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.hugoandrade.worldcup2018.predictor.backend.model.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.repository.SystemDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.stream.StreamSupport;

@RestController
public class SystemController {

	@Autowired private SystemDataRepository systemDataRepository;

	@RequestMapping("/")
	public String index() {
		return "Greetings from WorldCup 2018 (Spring Boot)!";
	}

	@GetMapping("/system-data")
	public SystemData getSystemData() {

		SystemData systemData = StreamSupport.stream(systemDataRepository.findAll().spliterator(), false)
				.findFirst()
				.orElse(null);

		if (systemData == null) {
			systemData = new SystemData(null, "0,1,2,4", true, new Date(), new Date());
			systemData = systemDataRepository.save(systemData);
		}

		return systemData;
	}

	@PostMapping("/system-data")
	public SystemData postSystemData(@RequestBody SystemData systemData) {

		systemDataRepository.deleteAll();
		SystemData dbSystemData = systemDataRepository.save(systemData);
		return dbSystemData;
	}

}
