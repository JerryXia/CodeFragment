package com.github.jerryxia.healthcheck;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.jerryxia.healthcheck.util.RecordLogViewStatusMessagesServlet;

import lombok.val;
import no.api.freemarker.java8.Java8ObjectWrapper;

@Configuration
// public class WebConfiguration extends WebMvcConfigurerAdapter {
public class WebConfiguration {
    @Bean
    public freemarker.template.Configuration freeMarkerConfigurationEx(
            freemarker.template.Configuration configuration) {
        val java8ObjectWrapper = new Java8ObjectWrapper(freemarker.template.Configuration.VERSION_2_3_26);
        configuration.setObjectWrapper(java8ObjectWrapper);
        // String staticFileUrlPrefix = env.getProperty("app.staticfile.urlprefix", "");
        // configuration.setSharedVariable("renderCss", new RenderCssFtlEx(staticFileUrlPrefix, env));
        // configuration.setSharedVariable("renderJs", new RenderJsFtlEx(staticFileUrlPrefix, env));
        //
        // configuration.setSharedVariable("ex_pathbuild", new TechReadPostPathBuildFtlEx());
        return configuration;
    }

    @Bean
    public ServletRegistrationBean recordLogViewStatusMessagesServlet() {
        val recordLogViewStatusMessagesServlet = new RecordLogViewStatusMessagesServlet();
        String urlMapping = "/healthcheck/lbClassicStatus";
        val servletRegistrationBean = new ServletRegistrationBean(recordLogViewStatusMessagesServlet, urlMapping);

        return servletRegistrationBean;
    }
}