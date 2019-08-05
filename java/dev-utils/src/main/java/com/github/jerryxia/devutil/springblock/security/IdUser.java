/**
 * 
 */
package com.github.jerryxia.devutil.springblock.security;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 携带用户Id的User对象
 * 
 * @author guqk
 *
 */
public class IdUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String id;
    private final Date   createDate;

    public IdUser(String id, String userName, String encodedPassword, int status, Date createDate,
            Collection<? extends GrantedAuthority> authorities) {
        super(userName, encodedPassword, status == 1, true, true, true, authorities);
        this.id = id;
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public Date getCreateDate() {
        return createDate;
    }
}
