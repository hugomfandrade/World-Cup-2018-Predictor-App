package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
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
@RequestMapping("/leagues")
public class LeaguesController {

	@Autowired private LeaguesService leaguesService;

	@Autowired private ModelMapper modelMapper;

	@GetMapping("/")
	public List<LeagueDto> getMyLeagues(Principal principal) {
		String userID = principal.getName();
		return leaguesService.getMyLeagues(userID).stream()
				.map(league -> modelMapper.map(league, LeagueDto.class))
				.collect(Collectors.toList());
	}

	@PostMapping("/")
	public LeagueDto createLeague(Principal principal, @RequestBody LeagueDto league) {
		String userID = principal.getName();
		return modelMapper.map(leaguesService.createLeague(userID, modelMapper.map(league, League.class)),
				LeagueDto.class);
	}

	@GetMapping("/{leagueID}")
	public LeagueDto getLeague(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);

		if (!belongsToLeague) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		return modelMapper.map(leaguesService.getLeague(leagueID), LeagueDto.class);
	}

	@PutMapping("/{leagueID}")
	public LeagueDto updateLeague(Principal principal,
								  @PathVariable("leagueID") String leagueID,
								  @RequestBody LeagueDto league) {
		String userID = principal.getName();

		final boolean isAdmin = leaguesService.isAdminOfLeague(userID, leagueID);

		if (!isAdmin) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you can not update to this league");

		return modelMapper.map(leaguesService.updateLeague(userID, leagueID, modelMapper.map(league, League.class)),
				LeagueDto.class);
	}

	@GetMapping("/{leagueID}/profile")
	public AccountDto getLeagueUser(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);

		if (!belongsToLeague) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		return Optional.ofNullable(leaguesService.getLeagueUser(leagueID, userID))
				.map(account -> modelMapper.map(account, AccountDto.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league"));
	}

	@GetMapping("/{leagueID}/users")
	public List<AccountDto> getLeagueUsers(Principal principal, @PathVariable("leagueID") String leagueID) {
		String userID = principal.getName();

		boolean belongsToLeague = leaguesService.belongsToLeague(userID, leagueID);

		if (!belongsToLeague) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you do not belong to this league");

		return leaguesService.getLeagueUsers(leagueID)
				.stream()
				.map(account -> modelMapper.map(account, AccountDto.class))
				.collect(Collectors.toList());
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
	public LeagueDto joinLeague(Principal principal,
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
		return modelMapper.map(leaguesService.joinLeague(userID, leagueID, code), LeagueDto.class);
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
}
