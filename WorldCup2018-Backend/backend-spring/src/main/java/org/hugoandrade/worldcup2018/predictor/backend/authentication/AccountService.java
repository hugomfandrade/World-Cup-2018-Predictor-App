package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.hugoandrade.worldcup2018.predictor.backend.utils.UnpagedSorted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

	@Autowired private AccountRepository accountRepository;
	@Autowired private AdminRepository adminRepository;

	public Account add(Account user) {
		return accountRepository.save(user);
	}

	public List<Account> getAccounts() {
		// return accountRepository.findAllByOrderByScoreDesc();
		return this.getAccounts(UnpagedSorted.of(Sort.by(
				Sort.Order.desc("score"),
				Sort.Order.asc("id"))));
	}

	public List<Account> getAccounts(int page, int size) {
		return this.getAccounts(PageRequest.of(page, size,
				Sort.by(Sort.Order.desc("score"),
						Sort.Order.asc("id"))));
	}

	public List<Account> getAccounts(Pageable pageable) {
		return accountRepository.findAll(pageable).toList();
	}

	public List<Account> getAccounts(List<String> accountIDs) {
		return accountRepository.findAllByIdInOrderByScoreDesc(accountIDs);
	}

	public Account getByID(String userID) {
		return accountRepository.findById(userID).orElse(null);
	}

	public Account getByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	public boolean isAdmin(String accountId) {
		return adminRepository.findByUserID(accountId) != null;
	}
}
