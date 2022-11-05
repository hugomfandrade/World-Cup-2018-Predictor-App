package org.hugoandrade.worldcup2018.predictor.backend.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

import java.util.Map;

public final class ApplicationListeners {

    private ApplicationListeners() {}

    public static class ApplicationStartingListener implements ApplicationListener<ApplicationStartingEvent> {

        @Override
        public void onApplicationEvent(@NonNull ApplicationStartingEvent event) {
            /*
            for (Map.Entry<String, String> env :  System.getenv().entrySet()) {
                System.err.println(env.getKey() + " ==> " + env.getValue());
            }
            for (Map.Entry<Object, Object> env :  System.getProperties().entrySet()) {
                System.err.println(env.getKey() + " --> " + env.getValue());
            }
            */
        }
    }


    public static class ApplicationEnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

            ConfigurableEnvironment env = event.getEnvironment();

            for (Map.Entry<String, Object> var :  env.getSystemEnvironment().entrySet()) {
                if (var.getKey().startsWith("CONFIG_")) {
                    event.getEnvironment().getSystemProperties()
                            .put(var.getKey().toLowerCase()
                                    .substring("CONFIG_".length())
                                    .replaceAll("___", "-")
                                    .replaceAll("__", "TEMPTEMP")
                                    .replaceAll("_", ".")
                                    .replaceAll("TEMPTEMP", "_"), var.getValue());
                }
            }
            for (String key : env.getSystemProperties().keySet().toArray(new String[0])) {
                if (key.startsWith("CONFIG_")) {
                    event.getEnvironment().getSystemProperties()
                            .put(key.toLowerCase()
                                    .substring("CONFIG_".length())
                                    .replaceAll("___", "-")
                                    .replaceAll("__", "TEMPTEMP")
                                    .replaceAll("_", ".")
                                    .replaceAll("TEMPTEMP", "_"), env.getSystemProperties().get(key));
                }
            }

            /*
            System.err.println("System Environment Variables: System.getenv()");
            for (Map.Entry<String, String> var :  System.getenv().entrySet()) {
                System.err.println("   " + var.getKey() + " ==> " + var.getValue());
            }
            System.err.println("System Environment Variables: System.getProperties()");
            for (Map.Entry<Object, Object> var :  System.getProperties().entrySet()) {
                System.err.println("   " + var.getKey() + " --> " + var.getValue());
            }
            System.err.println("System Environment Variables: ConfigurableEnvironment.getSystemProperties()");
            for (Map.Entry<String, Object> var :  env.getSystemProperties().entrySet()) {
                System.err.println("   " + var.getKey() + " ||> " + var.getValue());
            }
            System.err.println("System Environment Variables: ConfigurableEnvironment.getSystemEnvironment()");
            for (Map.Entry<String, Object> var :  env.getSystemEnvironment().entrySet()) {
                System.err.println("   " + var.getKey() + " ~~> " + var.getValue());
            }

            System.err.println("System Environment Variable (mongodb host): "
                    + event.getEnvironment().getProperty("spring.data.mongodb.host"));
            */
        }
    }
}
