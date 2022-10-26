package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.league.strategy.GetLeagueUsers;
import org.hugoandrade.worldcup2018.predictor.backend.league.strategy.GetLeagues;
import org.hugoandrade.worldcup2018.predictor.backend.league.strategy.LeaguesStrategyFactory;
import org.hugoandrade.worldcup2018.predictor.backend.utils.UnpagedSorted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LeaguesService {

	@Autowired public AccountRepository accountRepository;
	@Autowired public LeagueRepository leagueRepository;
	@Autowired public LeagueUserRepository leagueUserRepository;

	private GetLeagues getLeaguesStrategy;
	private GetLeagueUsers getLeagueUsersStrategy;
	private LeaguesStrategyFactory strategyFactory;

	public List<League> getMyLeagues(String userID) {
		return this.getMyLeagues(userID, UnpagedSorted.of(Sort.by(Sort.Order.asc("id"))));
	}

	public List<League> getMyLeagues(String userID, int page, int size) {
		return this.getMyLeagues(userID, PageRequest.of(page, size, Sort.by(Sort.Order.asc("id"))));
	}

	public List<League> getMyLeagues(String userID, Pageable pageable) {

		if (getLeaguesStrategy != null) {
			return getLeaguesStrategy.getLeagues(userID, pageable);
		}

		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByUserID(userID, pageable);

		List<String> leagueIDs = leagueUsers.stream()
				.map(LeagueUser::getLeagueID)
				.collect(Collectors.toList());

		return StreamSupport.stream(leagueRepository.findAllById(leagueIDs).spliterator(), false)
				.collect(Collectors.toList());
	}

	public boolean isAdminOfLeague(String userID, String leagueID) {
		League dbLeague = leagueRepository.findByAdminID(leagueID, userID);
		return dbLeague != null;
	}

	public boolean belongsToLeague(String userID, String leagueID) {
		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);
		return leagueUser != null;
	}

	public League createLeague(String userID, League league) {

		String code = generateUniqueCode();

		league.setAdminID(userID);
		league.setNumberOfMembers(1);
		league.setCode(code);

		LeagueUser adminUser = new LeagueUser(userID, 1);
		league.addLeagueUser(adminUser); // add relations

		League dbLeague = leagueRepository.save(league);

		adminUser.setLeague(dbLeague); 			 // add league
		adminUser.setAccount(accountRepository.findById(userID).orElse(null));
		LeagueUser dbAdminUser = leagueUserRepository.save(adminUser);

		return dbLeague;
	}

	public League joinLeague(String userID, String leagueID, String code) {

		// check if league exists
		League league = leagueRepository.findById(leagueID).orElse(null);
		if (league == null) return null;

		// check if user already belongs to league
		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);
		if (leagueUser != null) return null;

		// check if code equals
		if (!StringUtils.equals(code, league.getCode())) return null;

		// join
		LeagueUser newLeagueUser = new LeagueUser(userID, 1);
		league.setNumberOfMembers(league.getNumberOfMembers() + 1);
		league.addLeagueUser(newLeagueUser);   // add relations

		League dbLeague = leagueRepository.save(league);

		newLeagueUser = dbLeague.getLeagueUsers().stream().filter(l -> l.getLeagueID() == null).findAny().orElse(newLeagueUser);
		newLeagueUser.setLeague(dbLeague);			  // add relations
		newLeagueUser.setAccount(accountRepository.findById(userID).orElse(null));
		LeagueUser dbLeagueUser = leagueUserRepository.save(newLeagueUser);

		return dbLeague;
	}

	public League getLeague(String leagueID) {
		return leagueRepository.findById(leagueID).orElse(null);
	}

	public League updateLeague(String userID, String leagueID, League league) {

		League dbLeague = leagueRepository.findByAdminID(leagueID, userID);

		if (dbLeague == null) return null;

		// do update, of fields
		dbLeague.setName(league.getName());

		return leagueRepository.save(dbLeague);
	}

	public List<Account> getLeagueUsers(String leagueID) {
		return this.getLeagueUsers(leagueID,
				UnpagedSorted.of(Sort.by(Sort.Order.desc("score"), Sort.Order.asc("id"))));
	}

	public List<Account> getLeagueUsers(String leagueID, int page, int size) {
		return this.getLeagueUsers(leagueID,
				PageRequest.of(page, size, Sort.by(
						Sort.Order.desc("score"),
						Sort.Order.asc("id"))));
	}

	public List<Account> getLeagueUsers(String leagueID, Pageable pageable) {

		if (getLeagueUsersStrategy != null) {
			return getLeagueUsersStrategy.getLeagueUsers(leagueID, pageable);
		}

		final Map<String, LeagueUser> leaguesUsersMap = leagueUserRepository.findAllByLeagueID(leagueID)
				.stream()
				.collect(Collectors.toMap(LeagueUser::getUserID, Function.identity()));

		List<Account> leagueUsers = accountRepository.findAllByIdIn(leaguesUsersMap.keySet(), pageable);

		return leagueUsers.stream()
				.filter(account -> leaguesUsersMap.containsKey(account.getId()))
				.peek(account -> account.setRank(leaguesUsersMap.get(account.getId()).getRank()))
				.collect(Collectors.toList());
	}

	public Account getLeagueUser(String leagueID, String userID) {

		final Account account = accountRepository.findById(userID).orElse(null);
		final LeagueUser leaguesUser = leagueUserRepository.findByUserID(leagueID, userID);

		if (account == null || leaguesUser == null) return null;

		account.setRank(leaguesUser.getRank());
		return account;
	}

	public void deleteLeague(String userID, String leagueID) {

		League league = leagueRepository.findByAdminID(leagueID, userID);

		if (league == null) return;
			// throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not delete this league");

		// delete league users
		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByLeagueID(leagueID);
		leagueUserRepository.deleteAll(leagueUsers);

		// delete league
		leagueRepository.delete(league);
	}

	public void removeLeagueUsers(String leagueID) {
		// delete league users
		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByLeagueID(leagueID);
		leagueUserRepository.deleteAll(leagueUsers);
	}

	public void removeLeagueUser(String userID, String leagueID) {
		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);
		League league = leagueRepository.findById(leagueID).orElse(null);

		if (league != null) {
			league.removeLeagueUser(userID);
			leagueRepository.save(league);
		}

		leagueUserRepository.delete(leagueUser);
	}

	public void deleteLeague(League league) {
		leagueRepository.delete(league);
	}

	public void deleteLeague(String leagueID) {
		leagueRepository.deleteById(leagueID);
	}

	public void leaveLeague(String userID, String leagueID) {

		// check if league exists
		League league = leagueRepository.findById(leagueID).orElse(null);
		if (league == null) return;

		// if is admin, delete league altogether
		if (StringUtils.equals(userID, league.getAdminID())) {
			this.deleteLeague(userID, leagueID);
		}
		else {

			// check if user does not belong to league
			if (!belongsToLeague(userID, leagueID)) return;

			this.removeLeagueUser(userID, leagueID);
		}
	}

	private final static int COUNT = 8;
	private final static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	private String generateUniqueCode() {

		while (true) {
			String code = generateCode(COUNT);

			League league = leagueRepository.findByCode(code);
			if (league == null) return code;
		}

		// throw new IllegalArgumentException("failed to generate code");
	}

	private static String generateCode(int count) {

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < count; i++) {
			str.append(CHARS.charAt((int) (Math.random() * (CHARS.length()))));
		}

		return str.toString();
	}

	public void setGetLeagueUsersStrategy(GetLeagueUsers getLeagueUsersStrategy) {
		this.getLeagueUsersStrategy = getLeagueUsersStrategy;
	}

	public void setGetLeaguesStrategy(GetLeagues getLeaguesStrategy) {
		this.getLeaguesStrategy = getLeaguesStrategy;
	}

	public void setStrategyFactory(LeaguesStrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
		this.setGetLeaguesStrategy(strategyFactory.getLeaguesStrategy(this));
		this.setGetLeagueUsersStrategy(strategyFactory.getLeagueUsersStrategy(this));
	}
}
