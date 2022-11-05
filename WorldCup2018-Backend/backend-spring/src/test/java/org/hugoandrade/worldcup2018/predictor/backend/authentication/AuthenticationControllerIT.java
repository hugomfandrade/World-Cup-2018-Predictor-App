package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerIT {

	@LocalServerPort
	private int port;

	private URL base;

	@Autowired
	private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port);
    }

    @Test
    public void pingAuthService() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/auth/", String.class);
        assertThat(response.getBody()).isEqualTo("Authentication from Spring Boot!");

        response = template.getForEntity(base.toString() + "/auth/login/", String.class);
        assertThat(response.getBody()).isEqualTo("Authentication from Spring Boot!");
    }
}
