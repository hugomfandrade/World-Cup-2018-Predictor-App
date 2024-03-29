package org.hugoandrade.worldcup2018.predictor.backend;

import org.hugoandrade.worldcup2018.predictor.backend.config.ApplicationListeners;
import org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript;
import org.hugoandrade.worldcup2018.predictor.backend.security.Pbkdf2PasswordEncoderImpl;
import org.hugoandrade.worldcup2018.predictor.backend.security.SecurityConstants;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Map;

import static org.springframework.context.annotation.FilterType.ASPECTJ;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(pattern = "org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb.*", type = ASPECTJ))
@EnableMongoRepositories(basePackages = "org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb")
public class Application {

	public static void main(String[] args) {

		SpringApplication application = new SpringApplication(Application.class);
		application.addListeners(
				new ApplicationListeners.ApplicationStartingListener(),
				new ApplicationListeners.ApplicationEnvironmentPreparedListener());
		application.run(args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

	@Autowired
	private StartupDatabaseScript startupScript;

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
		Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
				.getHandlerMethods();
		map.forEach((key, value) -> System.out.println("{} {} :: " + key + " , " + value));

		startupScript.startup();
	}

	@Autowired
	public SecurityConstants securityConstants;

	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new Pbkdf2PasswordEncoderImpl(securityConstants.iterations, securityConstants.bytes);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}
}
