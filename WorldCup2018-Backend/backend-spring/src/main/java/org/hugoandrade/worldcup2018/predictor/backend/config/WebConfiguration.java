package org.hugoandrade.worldcup2018.predictor.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Configuration
@ComponentScan(basePackages="org.bk.webtestuuid")
public class WebConfiguration extends WebMvcConfigurationSupport {

    /*
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }
    */

    @Bean
    public PathMatcher pathMatcher(){
        return new CaseInsensitivePathMatcher();
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        // handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setPathMatcher(pathMatcher());
        return handlerMapping;
    }

    public static class CaseInsensitivePathMatcher extends AntPathMatcher {

        @Override
        protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables) {
            return super.doMatch(pattern.toLowerCase(), path == null ? null : path.toLowerCase(), fullMatch, uriTemplateVariables);
        }
    }
}