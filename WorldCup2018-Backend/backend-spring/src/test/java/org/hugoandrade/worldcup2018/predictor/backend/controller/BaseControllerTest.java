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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired AdminRepository adminRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired protected SecurityConstants securityConstants;
    @Autowired protected StartupDatabaseScript startupScript;

    protected final LoginData admin = new LoginData("admin", "password");
    protected final LoginData user = new LoginData("username", "password");
    protected final LoginData userOther = new LoginData("username-other", "password");

    @BeforeAll
    public void setUp() throws Exception {

        doSignUp(admin).andDo(mvcResult -> {
            LoginData loginData = parse(mvcResult.getResponse().getContentAsString(), LoginData.class);
            Admin admin = new Admin();
            admin.setUserID(loginData.getUserID());
            adminRepository.save(admin);
        });
        doSignUp(user);
        doSignUp(userOther);

        doLogin(admin);
        doLogin(user);
        doLogin(userOther);
    }

    @AfterAll
    void tearDown() {
        adminRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private ResultActions doSignUp(LoginData loginData) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/auth/sign-up/")
                        .content(format(loginData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())));
    }

    private ResultActions doLogin(LoginData loginData) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(format(loginData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.equalTo(loginData.getUsername())))
                .andDo(mvcResult -> {
                    LoginData resLoginData = parse(mvcResult.getResponse().getContentAsString(), LoginData.class);
                    loginData.setToken(securityConstants.TOKEN_PREFIX + "::" + resLoginData.getToken());
                    loginData.setUserID(resLoginData.getUserID());
                });
    }
}