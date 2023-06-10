package com.github.jerryxia.actuator.ui.support.spring.autoconfigure;

import com.github.jerryxia.actuator.ui.support.web.servlet.DispatchWebRequestServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Administrator
 * @date 2023/06/07
 */
@Configuration
@EnableConfigurationProperties(ActuatorUIWebServletProperties.class)
@ConditionalOnWebApplication
public class ActuatorUIrAutoConfiguration {
    public static final String ACTUATOR_UI_WEB_SERVLET_REGISTRATION_BEAN_NAME = "devhelper-actuatorUIWebServlet-registration";

    @Autowired
    private ActuatorUIWebServletProperties properties;
    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = ACTUATOR_UI_WEB_SERVLET_REGISTRATION_BEAN_NAME)
    @ConditionalOnMissingBean(name = ACTUATOR_UI_WEB_SERVLET_REGISTRATION_BEAN_NAME)
    @ConditionalOnProperty(name = "devhelper.actuator-ui-servlet.enabled", havingValue = "true")
    public ServletRegistrationBean actuatorUIWebServlet(ServletContext servletContext) {
        String servletName = "actuatorUIWebServlet";
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();

        DispatchWebRequestServlet servlet = new DispatchWebRequestServlet();
        registrationBean.setServlet(servlet);
        registrationBean.setName(servletName);
        registrationBean.setLoadOnStartup(1);

        if (StringUtils.hasText(properties.getUrlPattern())) {
            registrationBean.addUrlMappings(properties.getUrlPattern());
        } else {
            registrationBean.addUrlMappings(ActuatorUIWebServletProperties.DEFAULT_URL_PATTERN);
        }
        if(properties.getInitParameters() != null) {
            Iterator<Map.Entry<String, String>> iterator = properties.getInitParameters().entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                registrationBean.addInitParameter(entry.getKey(), entry.getValue());
            }
        }

        ServletRegistration servletRegistration = servletContext.getServletRegistration(servletName);
        if (servletRegistration != null) {
            // if webapp deployed as war in a container with MonitoringFilter and SessionListener already added by
            // web-fragment.xml,
            // do not add again
            registrationBean.setEnabled(false);
            servletRegistration.setInitParameters(registrationBean.getInitParameters());
        }
        return registrationBean;
    }


}
