package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LeaguesService {

	@Autowired private AccountService accountService;

	@Autowired private ModelMapper modelMapper;

	@Autowired private LeagueRepository leagueRepository;
	@Autowired private LeagueUserRepository leagueUserRepository;

	public List<League> getMyLeagues(String userID) {

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

		League dbLeague = leagueRepository.save(league);

		LeagueUser adminUser = new LeagueUser(dbLeague.getID(), userID, 1);

		LeagueUser dbAdminUser = leagueUserRepository.save(adminUser);

		return dbLeague;
	}

	@Deprecated
	public League getLeague(String userID, String leagueID) {

		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);

		if (leagueUser == null) return null;

		return this.getLeague(leagueID);
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

	@Deprecated
	public List<AccountDto> getLeagueUsers(String userID, String leagueID) {

		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);

		if (leagueUser == null) return null;

		return this.getLeagueUsers(leagueID);
	}

	public List<AccountDto> getLeagueUsers(String leagueID) {

		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByLeagueID(leagueID);

		List<String> accountIDs = leagueUsers.stream()
				.map(LeagueUser::getUserID)
				.collect(Collectors.toList());

		return accountService.getAccounts(accountIDs)
				.stream()
				.map(account -> modelMapper.map(account, AccountDto.class))
				.collect(Collectors.toList());
	}

	public void deleteLeague(String userID, String leagueID) {

		League league = leagueRepository.findByAdminID(leagueID, userID);

		if (league == null) return;
			// throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not delete to this league");

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
		leagueUserRepository.delete(leagueUser);
	}

	public void deleteLeague(League league) {
		leagueRepository.delete(league);
	}

	public void deleteLeague(String leagueID) {
		leagueRepository.deleteById(leagueID);
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
		league.setNumberOfMembers(league.getNumberOfMembers() + 1);

		League dbLeague = leagueRepository.save(league);

		LeagueUser newLeagueUser = new LeagueUser(dbLeague.getID(), userID, 1);
		LeagueUser dbLeagueUser = leagueUserRepository.save(newLeagueUser);

		return dbLeague;
	}

	public void leaveLeague(String userID, String leagueID) {

		// check if league exists
		League league = leagueRepository.findById(leagueID).orElse(null);
		if (league == null) return;

		// if is admin, delete league altogether
		if (StringUtils.equals(userID, league.getAdminID())) {
			deleteLeague(userID, leagueID);
			return;
		}

		// check if user does not belong to league
		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);
		if (leagueUser == null) return;

		leagueUserRepository.delete(leagueUser);
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
}
