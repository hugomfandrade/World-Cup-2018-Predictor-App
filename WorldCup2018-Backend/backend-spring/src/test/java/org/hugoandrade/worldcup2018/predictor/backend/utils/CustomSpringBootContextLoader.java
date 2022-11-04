package org.hugoandrade.worldcup2018.predictor.backend.utils;

import org.hugoandrade.worldcup2018.predictor.backend.config.ApplicationListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootContextLoader;

public class CustomSpringBootContextLoader extends SpringBootContextLoader {

	@Override
	protected SpringApplication getSpringApplication() {
		SpringApplication app = super.getSpringApplication();
		app.addListeners(new ApplicationListeners.AzureApplicationPropertiesListener());
		app.addListeners(new ApplicationListeners.AzureApplicationPropertiesListener2());
		return app;
	}
}