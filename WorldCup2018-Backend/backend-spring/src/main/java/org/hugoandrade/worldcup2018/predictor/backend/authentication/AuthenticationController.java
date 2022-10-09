package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AccountService accountService;

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
	public String signUp(@RequestBody LoginData user) {
		Account account = new Account();
		account.setUsername(user.getUsername());
		String passwordAndSalt = bCryptPasswordEncoder.encode(user.getPassword());
		String salt = passwordAndSalt.substring(0, passwordAndSalt.length() / 2);
		String password = passwordAndSalt.substring(passwordAndSalt.length() / 2);
		account.setPassword(password);
		account.setSalt(salt);
		account = accountService.add(account);

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
		return accountService.getAccounts();
	}
}
