package org.hugoandrade.worldcup2018.predictor.backend.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hugoandrade.worldcup2018.predictor.backend.controller.AuthenticationControllerTest.format;
import static org.hugoandrade.worldcup2018.predictor.backend.controller.AuthenticationControllerTest.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript;
import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.model.SystemData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class SystemControllerTest extends BaseControllerTest {

	@Autowired private MockMvc mvc;

	@Test
	public void getHello() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from WorldCup 2018 (Spring Boot)!")));
	}

	@Test
	void getSystemData() throws Exception {

		final SystemData expectedSystemData = new SystemData(null, "0,1,2,4", true, new Date(), new Date());

		mvc.perform(MockMvcRequestBuilders.get("/system-data/"))
				.andExpect(status().isOk())
				.andExpect(mvcResult -> {
					SystemData systemData = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<SystemData>(){});

					Assertions.assertEquals(expectedSystemData.getAppState(), systemData.getAppState());
					Assertions.assertEquals(expectedSystemData.getRawRules(), systemData.getRawRules());
					Assertions.assertEquals(expectedSystemData.getDate(), systemData.getDate());
				});

		mvc.perform(MockMvcRequestBuilders.get("/system-data/")
						.header(securityConstants.HEADER_STRING, user.getToken()))
				.andExpect(status().isOk())
				.andExpect(mvcResult -> {
					SystemData systemData = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<SystemData>(){});

					Assertions.assertEquals(expectedSystemData.getAppState(), systemData.getAppState());
					Assertions.assertEquals(expectedSystemData.getRawRules(), systemData.getRawRules());
					Assertions.assertEquals(expectedSystemData.getDate(), systemData.getDate());
				});

		mvc.perform(MockMvcRequestBuilders.get("/system-data/")
						.header(securityConstants.HEADER_STRING, admin.getToken()))
				.andExpect(status().isOk())
				.andExpect(mvcResult -> {
					SystemData systemData = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<SystemData>(){});

					Assertions.assertEquals(expectedSystemData.getAppState(), systemData.getAppState());
					Assertions.assertEquals(expectedSystemData.getRawRules(), systemData.getRawRules());
					Assertions.assertEquals(expectedSystemData.getDate(), systemData.getDate());
				});

		startupScript.startup();
	}

	@Test
	void postSystemData() throws Exception {

		final Date date = ISO8601Utils.parse("2018-06-27T12:00:00Z");

		final SystemData expectedSystemData = new SystemData(null, "0,1,2,4", true, date);

		mvc.perform(MockMvcRequestBuilders.post("/system-data/")
						.content(format(expectedSystemData))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());

		mvc.perform(MockMvcRequestBuilders.post("/system-data/")
						.header(securityConstants.HEADER_STRING, user.getToken())
						.content(format(expectedSystemData))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());

		mvc.perform(MockMvcRequestBuilders.post("/system-data/")
						.header(securityConstants.HEADER_STRING, admin.getToken())
						.content(format(expectedSystemData))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(mvcResult -> {
					SystemData systemData = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<SystemData>(){});

					Assertions.assertEquals(expectedSystemData.getAppState(), systemData.getAppState());
					Assertions.assertEquals(expectedSystemData.getRawRules(), systemData.getRawRules());
					Assertions.assertEquals(expectedSystemData.getDate(), systemData.getDate());
				});


		startupScript.startup();
	}
}
