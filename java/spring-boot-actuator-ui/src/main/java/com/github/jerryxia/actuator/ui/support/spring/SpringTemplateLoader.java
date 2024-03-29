package com.github.jerryxia.actuator.ui.support.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import freemarker.cache.TemplateLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * FreeMarker {@link TemplateLoader} adapter that loads via a Spring {@link ResourceLoader}.
 * Used by {@link FreeMarkerConfigurationFactory} for any resource loader path that cannot
 * be resolved to a {@link java.io.File}.
 *
 * @author Juergen Hoeller
 * @since 14.03.2004
 * @see FreeMarkerConfigurationFactory#setTemplateLoaderPath
 * @see freemarker.template.Configuration#setDirectoryForTemplateLoading
 */
public class SpringTemplateLoader implements TemplateLoader {
    private static final Logger logger = LoggerFactory.getLogger(SpringTemplateLoader.class);

    private final ResourceLoader resourceLoader;
    private final String templateLoaderPath;

    /**
     * Create a new SpringTemplateLoader.
     * @param resourceLoader the Spring ResourceLoader to use
     * @param templateLoaderPath the template loader path to use
     */
    public SpringTemplateLoader(ResourceLoader resourceLoader, String templateLoaderPath) {
        this.resourceLoader = resourceLoader;
        if (!templateLoaderPath.endsWith("/")) {
            templateLoaderPath += "/";
        }
        this.templateLoaderPath = templateLoaderPath;
        if (logger.isInfoEnabled()) {
            logger.info("SpringTemplateLoader for FreeMarker: using resource loader [" + this.resourceLoader +
                    "] and template loader path [" + this.templateLoaderPath + "]");
        }
    }


    @Override
    public Object findTemplateSource(String name) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for FreeMarker template with name [" + name + "]");
        }
        Resource resource = this.resourceLoader.getResource(this.templateLoaderPath + name);
        return (resource.exists() ? resource : null);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Resource resource = (Resource) templateSource;
        try {
            return new InputStreamReader(resource.getInputStream(), encoding);
        }
        catch (IOException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not find FreeMarker template: " + resource);
            }
            throw ex;
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        Resource resource = (Resource) templateSource;
        try {
            return resource.lastModified();
        }
        catch (IOException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not obtain last-modified timestamp for FreeMarker template in " +
                        resource + ": " + ex);
            }
            return -1;
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
    }

}
