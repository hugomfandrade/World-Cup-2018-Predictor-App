package org.hugoandrade.worldcup2018.predictor.backend.controller;

import org.hamcrest.Matchers;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.jwt.SecurityConstants;
import org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript;
import org.hugoandrade.worldcup2018.predictor.backend.model.Admin;
import org.hugoandrade.worldcup2018.predictor.backend.model.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.repository.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.AdminRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hugoandrade.worldcup2018.predictor.backend.controller.AuthenticationControllerTest.format;
import static org.hugoandrade.worldcup2018.predictor.backend.controller.AuthenticationControllerTest.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseControllerTest {

    @Autowired MockMvc mvc;

    @Autowired AdminRepository adminRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired SecurityConstants securityConstants;
    @Autowired StartupDatabaseScript startupScript;

    final LoginData admin = new LoginData("admin", "password");
    final LoginData user = new LoginData("username", "password");

    @BeforeAll
    public void setUp() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/auth/sign-up/")
                        .content(format(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.id", Matchers.equalTo("1")))
                .andExpect(jsonPath("$.username", Matchers.equalTo(admin.getUsername())))
                .andDo(mvcResult -> {
                    LoginData loginData = parse(mvcResult.getResponse().getContentAsString(), LoginData.class);
                    Admin admin = new Admin();
                    admin.setUserID(loginData.getUserID());
                    adminRepository.save(admin);
                });

        mvc.perform(MockMvcRequestBuilders.post("/auth/sign-up/")
                        .content(format(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.id", Matchers.equalTo("2")))
                .andExpect(jsonPath("$.username", Matchers.equalTo(user.getUsername())));

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(format(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.id", Matchers.equalTo("1")))
                .andExpect(jsonPath("$.username", Matchers.equalTo(admin.getUsername())))
                .andDo(mvcResult -> {
                    LoginData loginData = parse(mvcResult.getResponse().getContentAsString(), LoginData.class);
                    admin.setToken(securityConstants.TOKEN_PREFIX + "::" + loginData.getToken());
                    admin.setUserID(loginData.getUserID());
                });

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(format(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$.id", Matchers.equalTo("2")))
                .andExpect(jsonPath("$.username", Matchers.equalTo(user.getUsername())))
                .andDo(mvcResult -> {
                    LoginData loginData = parse(mvcResult.getResponse().getContentAsString(), LoginData.class);
                    user.setToken(securityConstants.TOKEN_PREFIX + "::" + loginData.getToken());
                    user.setUserID(loginData.getUserID());
                });
    }

    @AfterAll
    void tearDown() {
        adminRepository.deleteAll();
        accountRepository.deleteAll();
    }
}