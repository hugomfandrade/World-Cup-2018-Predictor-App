package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SystemController {

	@Autowired private SystemDataService systemDataService;

	@Autowired private ModelMapper modelMapper;

	@RequestMapping("/")
	public String index() {
		return "Greetings from WorldCup 2018 (Spring Boot)!";
	}

	@GetMapping("/system-data")
	public SystemDataDto getSystemData() {
		return modelMapper.map(systemDataService.getSystemData(), SystemDataDto.class);
	}

	@PostMapping("/system-data")
	public SystemDataDto postSystemData(@RequestBody SystemDataDto systemData) {
		SystemData newSystemData = systemDataService.setSystemData(modelMapper.map(systemData, SystemData.class));
		return modelMapper.map(newSystemData, SystemDataDto.class);
	}

	@PostMapping("/reset-all")
	public String hardReset() {
		systemDataService.hardReset();
		return "Tournament Updated, Scores of Predictions Updated !";
	}

}
