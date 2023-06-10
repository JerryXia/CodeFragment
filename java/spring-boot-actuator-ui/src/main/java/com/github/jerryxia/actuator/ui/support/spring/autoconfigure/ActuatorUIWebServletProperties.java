package com.github.jerryxia.actuator.ui.support.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author Administrator
 * @date 2023/06/08
 */
@ConfigurationProperties(prefix = "devhelper.actuator-ui-servlet")
public class ActuatorUIWebServletProperties {
    public static final String DEFAULT_URL_PATTERN = "/admin/management/actuator-ui/*";

    private boolean enabled;
    private String urlPattern;
    private Map<String, String> initParameters;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }
}
