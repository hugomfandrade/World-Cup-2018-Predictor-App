package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.model.Account;
import org.hugoandrade.worldcup2018.predictor.backend.model.League;
import org.hugoandrade.worldcup2018.predictor.backend.model.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.backend.repository.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.LeagueRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.LeagueUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/leagues")
public class LeaguesController {

	@Autowired private AccountRepository accountRepository;

	@Autowired private LeagueRepository leagueRepository;
	@Autowired private LeagueUserRepository leagueUserRepository;

	@GetMapping("/")
	public List<League> getMyLeagues(Principal principal) {
		String userID = principal.getName();

		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByUserID(userID);

		List<String> leagueIDs = leagueUsers.stream()
				.map(LeagueUser::getLeagueID)
				.collect(Collectors.toList());

		return StreamSupport.stream(leagueRepository.findAllById(leagueIDs).spliterator(), false)
				.collect(Collectors.toList());
	}

	@PostMapping("/")
	public League createLeague(Principal principal, @RequestBody League league) {
		String userID = principal.getName();

		String code = generateUniqueCode();

		league.setAdminID(userID);
		league.setNumberOfMembers(1);
		league.setCode(code);

		League dbLeague = leagueRepository.save(league);

		LeagueUser adminUser = new LeagueUser(dbLeague.getID(), userID, 1);

		LeagueUser dbAdminUser = leagueUserRepository.save(adminUser);

		return dbLeague;
	}

	@GetMapping("/{leagueID}")
	public League getLeague(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);

		if (leagueUser == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		return leagueRepository.findById(leagueID).orElse(null);
	}

	@PutMapping("/{leagueID}")
	public League updateLeague(Principal principal,
							   @PathVariable("leagueID") String leagueID,
							   @RequestBody League league) {
		String userID = principal.getName();

		League dbLeague = leagueRepository.findByAdminID(leagueID, userID);

		if (dbLeague == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not update to this league");

		// do update, of fields
		dbLeague.setName(league.getName());

		return leagueRepository.save(dbLeague);
	}

	@GetMapping("/{leagueID}/users")
	public List<Account> getLeagueUsers(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);

		if (leagueUser == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByLeagueID(leagueID);

		List<String> accountIDs = leagueUsers.stream()
				.map(LeagueUser::getUserID)
				.collect(Collectors.toList());

		return StreamSupport.stream(accountRepository.findAllById(accountIDs).spliterator(), false)
				.collect(Collectors.toList());
	}

	@DeleteMapping("/{leagueID}")
	public void deleteLeague(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		League league = leagueRepository.findByAdminID(leagueID, userID);

		if (league == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not delete to this league");

		// delete league users
		List<LeagueUser> leagueUsers = leagueUserRepository.findAllByLeagueID(leagueID);
		leagueUserRepository.deleteAll(leagueUsers);

		// delete league
		leagueRepository.delete(league);
	}

	@PostMapping("/{leagueID}/join")
	public League joinLeague(Principal principal,
							 @PathVariable("leagueID") String leagueID,
							 @RequestBody JoinRequestBody requestBody) {
		String userID = principal.getName();
		String code = requestBody.code;

		// check if league exists
		League league = leagueRepository.findById(leagueID).orElse(null);
		if (league == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this league does not exist anymore");

		// check if user already belongs to league
		LeagueUser leagueUser = leagueUserRepository.findByUserID(leagueID, userID);
		if (leagueUser != null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you already belong to this league");

		// check if code equals
		if (!StringUtils.equals(code, league.getCode())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong code");

		// join
		league.setNumberOfMembers(league.getNumberOfMembers() + 1);

		League dbLeague = leagueRepository.save(league);

		LeagueUser newLeagueUser = new LeagueUser(dbLeague.getID(), userID, 1);
		LeagueUser dbLeagueUser = leagueUserRepository.save(newLeagueUser);

		return dbLeague;
	}

	@DeleteMapping("/{leagueID}/users")
	public void leaveLeague(Principal principal,
							@PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		// check if league exists
		League league = leagueRepository.findById(leagueID).orElse(null);
		if (league == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this league does not exist anymore");

		// if is admin, delete league altogether
		if (StringUtils.equals(userID, league.getAdminID())) {
			deleteLeague(principal, leagueID);
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

	public static class JoinRequestBody {

		public String code;
	}
}
