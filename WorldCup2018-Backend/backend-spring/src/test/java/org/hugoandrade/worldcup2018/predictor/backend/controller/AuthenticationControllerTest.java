package org.hugoandrade.worldcup2018.predictor.backend.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.hugoandrade.worldcup2018.predictor.backend.model.LoginData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest {

	@Autowired
	private MockMvc mvc;

	@BeforeAll
	public void setUp() throws Exception {

		LoginData loginData = new LoginData("username", "password");

		mvc.perform(MockMvcRequestBuilders.post("/auth/sign-up/")
						.content(format(loginData))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				// .andExpect(jsonPath("$.id", Matchers.equalTo("1")))
				.andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())));
				// .andExpect(content().json(format(loginData)));
	}

	@Test
	public void getLogin() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/auth/login/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Authentication from Spring Boot!")));
	}

	@Test
	public void postLogin() throws Exception {

		LoginData loginData = new LoginData("username", "password");

		mvc.perform(MockMvcRequestBuilders.post("/auth/login")
						.content(format(loginData))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				// .andExpect(jsonPath("$.id", Matchers.equalTo("1")))
				// .andExpect(jsonPath("$.UserID", Matchers.equalTo("1")))
				.andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())))
				.andExpect(jsonPath("$.token", Matchers.notNullValue()))
				.andExpect(jsonPath("$.Token", Matchers.notNullValue()));
	}

	public static String format(Object loginData) {
		if (loginData == null) return null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		try {
			//Convert object to JSON string
			return mapper.writeValueAsString(loginData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T parse(String data, Class<T> clazz) {
		if (data == null) return null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			//Convert object to JSON string
			return mapper.readValue(data, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T parse(String data, TypeReference<T> clazz) {
		if (data == null) return null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			//Convert object to JSON string
			return mapper.readValue(data, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
