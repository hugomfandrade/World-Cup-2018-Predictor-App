package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@PostMapping("/sign-up")
	public void signUp(@RequestBody LoginData user) {
		Account account = new Account();
		account.setUsername(user.getUsername());
		String passwordAndSalt = bCryptPasswordEncoder.encode(user.getPassword());
		String salt = passwordAndSalt.substring(0, passwordAndSalt.length() / 2);
		String password = passwordAndSalt.substring(0, passwordAndSalt.length() / 2);
		account.setPassword(password);
		account.setSalt(salt);
		accountRepository.save(account);
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String index() {
		return "Login from Spring Boot!";
	}

	@GetMapping("/accounts")
	public Iterable<Account> getAccounts() {
		return accountRepository.findAll();
	}
}
