package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.hamcrest.Matchers;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest extends BaseControllerTest {

	@Autowired
	private MockMvc mvc;

	@BeforeAll
	public void setUp() throws Exception {

		LoginData loginData = new LoginData("username", "password");

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

		LoginData loginData = new LoginData("username", "password");

		doOn(mvc).post("/auth/login", loginData)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())))
				.andExpect(jsonPath("$.token", Matchers.notNullValue()));
	}
}
