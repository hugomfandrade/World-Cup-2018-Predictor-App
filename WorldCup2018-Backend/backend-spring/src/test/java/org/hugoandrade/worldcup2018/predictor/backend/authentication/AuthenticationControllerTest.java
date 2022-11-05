package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.hamcrest.Matchers;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ListResultMatchers.list;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ObjResultMatchers.obj;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest extends BaseControllerTest {

	@Autowired
	private MockMvc mvc;

	private final static LoginData loginData = new LoginData("username-login", "password");

	@BeforeAll
	public void setUp() throws Exception {
		super.setUp();

		doOn(mvc).post("/auth/sign-up/", loginData)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())));
	}

	@Test
	public void getLogin() throws Exception {
		doOn(mvc).get("/auth/login/")
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Authentication from Spring Boot!")));
	}

	@Test
	public void postLogin() throws Exception {

		final AccountDto accountDto = new AccountDto(loginData.getUsername());

		// login
		doOn(mvc).post("/auth/login", loginData)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())))
				.andExpect(jsonPath("$.token", Matchers.notNullValue()))
				.andExpect(obj(LoginData.class)
						.addDo(account -> loginData.setToken(securityConstants.TOKEN_PREFIX + "::" + account.getToken())));

		// get profile
		doOn(mvc).get("/users/profile/")
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(loginData.getToken()).get("/users/profile/")
				.andExpect(status().isOk())
				.andExpect(obj(AccountDto.class).addDo(account -> {
					accountDto.setId(account.getId());
					accountDto.setScore(account.getScore());
					accountDto.setRank(account.getRank());
				}));

		// get users
		doOn(mvc).get("/users/")
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(loginData.getToken()).get("/users/")
				.andExpect(status().isOk())
				.andExpect(list(AccountDto.class).assertSize(4))
				.andExpect(list(AccountDto.class)
						.addDo(accountDtos -> Assertions.assertTrue(accountDtos.contains(accountDto))));

		doOn(mvc).withHeader(loginData.getToken()).paging(0,2).get("/users/")
				.andExpect(status().isOk())
				.andExpect(list(AccountDto.class).assertSize(2));

		doOn(mvc).withHeader(loginData.getToken()).paging(1,2).get("/users/")
				.andExpect(status().isOk())
				.andExpect(list(AccountDto.class).assertSize(2));

		doOn(mvc).withHeader(loginData.getToken()).paging(2,2).get("/users/")
				.andExpect(status().isOk())
				.andExpect(list(AccountDto.class).assertSize(0));

		// get single user
		doOn(mvc).get("/users/" + loginData.getUsername() + "-INVALID")
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(loginData.getToken()).get("/users/" + loginData.getUsername() + "-INVALID")
				.andExpect(status().is4xxClientError());

		doOn(mvc).withHeader(loginData.getToken()).get("/users/" + loginData.getUsername())
				.andExpect(status().isOk())
				.andExpect(obj(AccountDto.class).assertEquals(accountDto));

	}
}
