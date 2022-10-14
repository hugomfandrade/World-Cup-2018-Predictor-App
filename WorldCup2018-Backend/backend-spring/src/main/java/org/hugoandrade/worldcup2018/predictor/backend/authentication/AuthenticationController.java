package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/")
	public String home() {
		return "Authentication from Spring Boot!";
	}

	@GetMapping("/login/")
	public String login() {
		return home();
	}

	@PostMapping("/sign-up")
	public LoginData signUp(@RequestBody LoginData user) {
		String passwordAndSalt = bCryptPasswordEncoder.encode(user.getPassword());
		String salt = passwordAndSalt.substring(0, passwordAndSalt.length() / 2);
		String password = passwordAndSalt.substring(passwordAndSalt.length() / 2);
		Account account = new Account();
		account.setUsername(user.getUsername());
		account.setPassword(password);
		account.setSalt(salt);
		account = accountService.add(account);

		// output value
		LoginData loginData = new LoginData();
		loginData.setUserID(account.getId());
		loginData.setUsername(account.getUsername());
		return loginData;
	}
}
