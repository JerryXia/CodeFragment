/**
 * 
 */
package com.github.jerryxia.devutil.securityblock;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.jerryxia.devutil.securityblock.PasswordStorage.CannotPerformOperationException;
import com.github.jerryxia.devutil.securityblock.PasswordStorage.InvalidHashException;

/**
 * @author Administrator
 *
 */
public class PasswordStoragePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            return PasswordStorage.createHash(rawPassword.toString());
        } catch (CannotPerformOperationException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (InvalidHashException e) {
            e.printStackTrace();
        }
        return false;
    }

}
