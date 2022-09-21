package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.hugoandrade.worldcup2018.predictor.backend.model.Account;
import org.hugoandrade.worldcup2018.predictor.backend.model.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "Authentication from Spring Boot!";
	}

	@RequestMapping(value = "/login/", method = RequestMethod.GET)
	public String login() {
		return home();
	}

	@PostMapping("/sign-up")
	public String signUp(@RequestBody LoginData user) {
		Account account = new Account();
		account.setUsername(user.getUsername());
		String passwordAndSalt = bCryptPasswordEncoder.encode(user.getPassword());
		String salt = passwordAndSalt.substring(0, passwordAndSalt.length() / 2);
		String password = passwordAndSalt.substring(passwordAndSalt.length() / 2);
		account.setPassword(password);
		account.setSalt(salt);
		accountRepository.save(account);

		// output value
		// ObjectNode o = new ObjectNode(JsonNodeFactory.instance);
		ObjectNode o = new ObjectMapper().createObjectNode();
		o.put("id", account.getId());
		o.put("UserID", account.getId());
		o.put("username", account.getUsername());
		return o.toString();
	}

	@GetMapping("/accounts")
	public Iterable<Account> getAccounts() {
		return accountRepository.findAll();
	}
}
