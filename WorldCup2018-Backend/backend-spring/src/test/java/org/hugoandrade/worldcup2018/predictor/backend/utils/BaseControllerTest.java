package org.hugoandrade.worldcup2018.predictor.backend.utils;

import org.hamcrest.Matchers;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.*;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueRepository;
import org.hugoandrade.worldcup2018.predictor.backend.security.SecurityConstants;
import org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(loader = CustomSpringBootContextLoader.class)
public abstract class BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired AdminRepository adminRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired LeagueRepository leagueRepository;
    @Autowired PredictionRepository predictionRepository;
    @Autowired protected SecurityConstants securityConstants;
    @Autowired protected StartupDatabaseScript startupScript;

    protected final LoginData admin = new LoginData("admin", "password");
    protected final LoginData user = new LoginData("username", "password");
    protected final LoginData userOther = new LoginData("username-other", "password");

    @BeforeAll
    public void setUp() throws Exception {

        // clear repo
        accountRepository.deleteAll();
        adminRepository.deleteAll();
        predictionRepository.deleteAll();

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
        leagueRepository.deleteAll();
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



    protected RequestBuilder doOn(MockMvc mvc) {
        return new RequestBuilder(mvc);
    }

    protected class RequestBuilder {

        private final MockMvc mvc;

        private String token;

        private Integer page;
        private Integer size;

        protected RequestBuilder(MockMvc mvc) {
            this.mvc = mvc;
        }

        public RequestBuilder withHeader(String token) {
            this.token = token;
            return this;
        }

        public RequestBuilder paging(int page, int size) {
            this.page = page;
            this.size = size;
            return this;
        }

        public ResultActions post(String url) throws Exception {
            return post(url, null);
        }

        public ResultActions post(String url, Object body) throws Exception {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(url);

            if (token != null) {
                builder.header(securityConstants.HEADER_STRING, token);
            }

            if (body != null) {
                builder.content(format(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);
            }

            return mvc.perform(builder);
        }

        public ResultActions get(String url) throws Exception {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url);

            if (token != null) {
                builder.header(securityConstants.HEADER_STRING, token);
            }
            if (page != null) {
                builder.param("page", page.toString());
            }
            if (size != null) {
                builder.param("size", size.toString());
            }

            return mvc.perform(builder);
        }

        public ResultActions put(String url, Object body) throws Exception {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url);

            if (token != null) {
                builder.header(securityConstants.HEADER_STRING, token);
            }

            builder.content(format(body))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            return mvc.perform(builder);
        }

        public ResultActions delete(String url) throws Exception {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(url);

            if (token != null) {
                builder.header(securityConstants.HEADER_STRING, token);
            }

            return mvc.perform(builder);
        }
    }
}