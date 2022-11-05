package org.hugoandrade.worldcup2018.predictor.backend.runtime;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.LoginData;
import org.junit.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;

public class WebServerTest {

    private final static String URL = "https://world-cup-2018-predictor-spring-app.azurewebsites.net/";

    private static final Properties properties;
    static {
        properties = new Properties();

        try {
            ClassLoader classLoader = WebServerTest.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application-local.properties");
            properties.load(applicationPropertiesStream);
        } catch (Exception e) {
            // process the exception
        }
    }

    public static void main(String... args) {
        new WebServerTest().run(properties.getProperty("web.server.url"));
    }

    private final static LoginData loginData = new LoginData("reserved-username", "password");

    private void run(String url) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(url));

        // ping
        Assert.assertEquals("Authentication from Spring Boot!", restTemplate.getForObject("auth/", String.class));

        // sign-up // login
        restTemplate.postForObject("/auth/sign-up/", loginData, LoginData.class);
        String user = restTemplate.postForObject("/auth/login", loginData, String.class);

        final String token = parse(user, LoginData.class).getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set(properties.getProperty("spring.jwt.header_string"), properties.getProperty("spring.jwt.token_prefix") + "::" + token);

        // get countries
        Assert.assertEquals(32, restTemplate
                .exchange("countries/", HttpMethod.GET, new HttpEntity<>(headers), List.class).getBody().size());
        Assert.assertEquals(64, restTemplate
                .exchange("matches/", HttpMethod.GET, new HttpEntity<>(headers), List.class).getBody().size());
    }
}
