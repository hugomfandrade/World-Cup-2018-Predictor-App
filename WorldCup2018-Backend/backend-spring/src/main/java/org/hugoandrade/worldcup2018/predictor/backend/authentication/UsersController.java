package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/")
	public List<AccountDto> getAccounts(@RequestParam(name = "page", defaultValue = "0") int page,
										@RequestParam(name = "size", defaultValue = "50") int size) {
		return accountService.getAccounts(page, size)
				.stream()
				.map(account -> modelMapper.map(account, AccountDto.class))
				.collect(Collectors.toList());
	}

	@GetMapping("/profile/")
	public AccountDto getAccount(Principal principal) {
		String userID = principal.getName();

		return Optional.ofNullable(accountService.getByID(userID))
				.map(account -> modelMapper.map(account, AccountDto.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "profile not found"));
	}

	@GetMapping("/{username}")
	public AccountDto getAccount(@PathVariable("username") String username) {
		return Optional.ofNullable(accountService.getByUsername(username))
				.map(account -> modelMapper.map(account, AccountDto.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "profile not found"));
	}
}
