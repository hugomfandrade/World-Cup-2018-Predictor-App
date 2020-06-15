package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

	@RequestMapping(value = "/Login", method = RequestMethod.GET)
	public String index() {
		return "Login from Spring Boot!";
	}

	@RequestMapping(value = "/Login", method = RequestMethod.POST)
	@ResponseBody
	public LoginData login(@RequestBody LoginData v) {
		return v;
	}

}
