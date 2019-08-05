/**
 * 
 */
package com.github.jerryxia.devutil.springblock.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.core.userdetails.UserCache;

/**
 * @author guqk
 *
 */
public class DaoAuthenticationProviderPostProcess implements ObjectPostProcessor<AbstractUserDetailsAuthenticationProvider> {
    private static final Logger log = LoggerFactory.getLogger(DaoAuthenticationProviderPostProcess.class);

    private final UserCache userCache;

    public DaoAuthenticationProviderPostProcess(UserCache userCache) {
        log.debug("DaoAuthenticationProviderPostProcess userCache: {}", userCache.getClass().toString());
        this.userCache = userCache;
    }

    @Override
    public <O extends AbstractUserDetailsAuthenticationProvider> O postProcess(O object) {
        log.debug("DaoAuthenticationProviderPostProcess postProcess object: {}", object.getClass().toString());
        object.setUserCache(this.userCache);
        return object;
    }
}
