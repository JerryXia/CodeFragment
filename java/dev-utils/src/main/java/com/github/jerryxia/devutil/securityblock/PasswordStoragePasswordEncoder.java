/**
 * 
 */
package com.github.jerryxia.devutil.securityblock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.jerryxia.devutil.securityblock.PasswordStorage.CannotPerformOperationException;
import com.github.jerryxia.devutil.securityblock.PasswordStorage.InvalidHashException;

/**
 * @author Administrator
 *
 */
public class PasswordStoragePasswordEncoder implements PasswordEncoder {
    private static final Logger log = LoggerFactory.getLogger(PasswordStoragePasswordEncoder.class);

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return PasswordStorage.createHash(rawPassword.toString());
        } catch (CannotPerformOperationException e) {
            log.error("PasswordStoragePasswordEncoder encode fail", e);
        }
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            throw new InternalAuthenticationServiceException("encodedPassword is null");
        }
        try {
            return PasswordStorage.verifyPassword(rawPassword.toString(), encodedPassword);
        } catch (CannotPerformOperationException e) {
            log.error("PasswordStoragePasswordEncoder matches fail", e);
        } catch (InvalidHashException e) {
            log.error("PasswordStoragePasswordEncoder matches fail", e);
        }
        return false;
    }

}
