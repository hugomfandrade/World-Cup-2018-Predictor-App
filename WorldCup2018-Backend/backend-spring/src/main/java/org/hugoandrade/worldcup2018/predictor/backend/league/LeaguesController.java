package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/leagues")
public class LeaguesController {

	@Autowired private LeaguesService leaguesService;

	@GetMapping("/")
	public List<League> getMyLeagues(Principal principal) {
		String userID = principal.getName();
		return leaguesService.getMyLeagues(userID);
	}

	@PostMapping("/")
	public League createLeague(Principal principal, @RequestBody League league) {
		String userID = principal.getName();
		return leaguesService.createLeague(userID, league);
	}

	@GetMapping("/{leagueID}")
	public League getLeague(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);

		if (!belongsToLeague) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		return leaguesService.getLeague(leagueID);
	}

	@PutMapping("/{leagueID}")
	public League updateLeague(Principal principal,
							   @PathVariable("leagueID") String leagueID,
							   @RequestBody League league) {
		String userID = principal.getName();

		final boolean isAdmin = leaguesService.isAdminOfLeague(userID, leagueID);

		if (!isAdmin) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not update to this league");

		return leaguesService.updateLeague(userID, leagueID, league);
	}

	@GetMapping("/{leagueID}/users")
	public List<Account> getLeagueUsers(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);

		if (!belongsToLeague) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		return leaguesService.getLeagueUsers(leagueID);
	}

	@DeleteMapping("/{leagueID}")
	public void deleteLeague(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		boolean isAdmin = leaguesService.isAdminOfLeague(userID, leagueID);

		if (!isAdmin) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not delete to this league");

		// delete league users, and league
		leaguesService.removeLeagueUsers(leagueID);
		leaguesService.deleteLeague(leagueID);
	}

	@PostMapping("/{leagueID}/join")
	public League joinLeague(Principal principal,
							 @PathVariable("leagueID") String leagueID,
							 @RequestBody JoinRequestBody requestBody) {
		String userID = principal.getName();
		String code = requestBody.code;

		// check if league exists
		League league = leaguesService.getLeague(leagueID);
		if (league == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this league does not exist anymore");

		// check if user already belongs to league
		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);
		if (belongsToLeague) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you already belong to this league");

		// check if code equals
		if (!StringUtils.equals(code, league.getCode())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong code");

		// join
		return leaguesService.joinLeague(userID, leagueID, code);
	}

	@DeleteMapping("/{leagueID}/users")
	public void leaveLeague(Principal principal,
							@PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		// check if league exists
		League league = leaguesService.getLeague(leagueID);
		if (league == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this league does not exist anymore");

		// if is admin, delete league altogether
		if (StringUtils.equals(userID, league.getAdminID())) {
			this.deleteLeague(principal, leagueID);
			return;
		}

		// check if user does not belong to league
		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);
		if (!belongsToLeague) return;

		leaguesService.removeLeagueUser(userID, leagueID);
	}

	public static class JoinRequestBody {

		public String code;

		public JoinRequestBody() { }

		public JoinRequestBody(String code) {
			this.code = code;
		}
	}
}
