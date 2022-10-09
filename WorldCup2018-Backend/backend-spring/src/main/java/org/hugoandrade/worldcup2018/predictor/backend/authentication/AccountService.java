package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccountService {

	@Autowired private AccountRepository accountRepository;
	@Autowired private AdminRepository adminRepository;

	public Account add(Account user) {
		return accountRepository.save(user);
	}

	public List<Account> getAccounts() {
		return StreamSupport.stream(accountRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());
	}

	public List<Account> getAccounts(List<String> accountIDs) {
		return StreamSupport.stream(accountRepository.findAllById(accountIDs).spliterator(), false)
				.collect(Collectors.toList());
	}

	public Account getByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	public boolean isAdmin(String accountId) {
		return adminRepository.findByUserID(accountId) != null;
	}
}
