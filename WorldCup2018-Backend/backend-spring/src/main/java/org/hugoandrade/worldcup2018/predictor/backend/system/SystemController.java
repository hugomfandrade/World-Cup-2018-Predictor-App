package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SystemController {

	@Autowired private SystemDataService systemDataService;

	@RequestMapping("/")
	public String index() {
		return "Greetings from WorldCup 2018 (Spring Boot)!";
	}

	@GetMapping("/system-data")
	public SystemData getSystemData() {
		return systemDataService.getSystemData();
	}

	@PostMapping("/system-data")
	public SystemData postSystemData(@RequestBody SystemData systemData) {
		SystemData newSystemData = systemDataService.setSystemData(systemData);
		return newSystemData;
	}

	@PostMapping("/reset-all")
	public String hardReset() {
		systemDataService.hardReset();
		return "Tournament Updated, Scores of Predictions Updated !";
	}

}
