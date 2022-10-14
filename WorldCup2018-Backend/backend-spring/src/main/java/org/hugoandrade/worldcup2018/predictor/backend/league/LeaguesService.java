package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LeaguesService {

	@Autowired private AccountService accountService;

	@Autowired private AccountRepository accountRepository;
	@Autowired private LeagueRepository leagueRepository;
	@Autowired private LeagueUserRepository leagueUserRepository;

	public List<League> getMyLeagues(String userID) {

		List<League> leagueUsers01 = GET_MY_LEAGUES_STRATEGIES.get(SIMPLE).apply(userID);
		List<League> leagueUsers02 = GET_MY_LEAGUES_STRATEGIES.get(QUERY).apply(userID);
		List<League> leagueUsers03 = GET_MY_LEAGUES_STRATEGIES.get(RELATIONS).apply(userID);

		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByUserID(userID);

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

		newLeagueUser = dbLeague.getLeagueUsers().stream().filter(l -> l.getLeagueID() == null).findAny().get();
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

		List<Account> leagueUsers01 = GET_LEAGUE_USERS_STRATEGIES.get(SIMPLE).apply(leagueID);
		List<Account> leagueUsers02 = GET_LEAGUE_USERS_STRATEGIES.get(QUERY).apply(leagueID);
		List<Account> leagueUsers03 = GET_LEAGUE_USERS_STRATEGIES.get(RELATIONS).apply(leagueID);

		List<Account> leagueUsers = accountRepository.findAllByLeagueID(leagueID);

		final Map<String, LeagueUser> leaguesUsersMap = leagueUserRepository.findAllByLeagueID(leagueID)
				.stream()
				.collect(Collectors.toMap(LeagueUser::getUserID, Function.identity()));
		final LeagueUser EMPTY_USER = new LeagueUser(null, -1);

		return leagueUsers.stream()
				.peek(account -> account.setRank(leaguesUsersMap.getOrDefault(account.getId(), EMPTY_USER).getRank()))
				.filter(account -> account.getRank() != -1)
				.collect(Collectors.toList());
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

	private final static int SIMPLE = 1;
	private final static int QUERY = 2;
	private final static int RELATIONS = 3;

	private final Map<Integer, Function<String, List<League>>> GET_MY_LEAGUES_STRATEGIES = new HashMap<>();
	{
		GET_MY_LEAGUES_STRATEGIES.put(SIMPLE, userID -> {

			List<LeagueUser> leagueUsers = leagueUserRepository.findAllByUserID(userID);

			List<String> leagueIDs = leagueUsers.stream()
					.map(LeagueUser::getLeagueID)
					.collect(Collectors.toList());

			return StreamSupport.stream(leagueRepository.findAllById(leagueIDs).spliterator(), false)
					.collect(Collectors.toList());
		});
		GET_MY_LEAGUES_STRATEGIES.put(QUERY, userID -> leagueRepository.findAllByUserID(userID));
		GET_MY_LEAGUES_STRATEGIES.put(RELATIONS, userID -> leagueUserRepository.findAllByUserID(userID)
				.stream()
				.map(LeagueUser::getLeague)
				.collect(Collectors.toList())
		);
	}

	private final Map<Integer, Function<String, List<Account>>> GET_LEAGUE_USERS_STRATEGIES = new HashMap<>();
	{
		GET_LEAGUE_USERS_STRATEGIES.put(SIMPLE, leagueID -> {

			List<LeagueUser> leagueUsers = leagueUserRepository.findAllByLeagueID(leagueID);

			List<String> accountIDs = leagueUsers.stream()
					.map(LeagueUser::getUserID)
					.collect(Collectors.toList());

			return accountService.getAccounts(accountIDs);
		});
		GET_LEAGUE_USERS_STRATEGIES.put(QUERY, leagueID -> accountRepository.findAllByLeagueID(leagueID));
		GET_LEAGUE_USERS_STRATEGIES.put(RELATIONS, leagueID -> {

			List<LeagueUser> leagueUsers = leagueRepository.findById(leagueID).get().getLeagueUsers();

			return leagueUsers.stream()
					.map(LeagueUser::getAccount)
					.collect(Collectors.toList());
		});
	}
}
