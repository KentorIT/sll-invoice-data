/**
 * Copyright (c) 2013 SLL. <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice-Data is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Invoice-Data is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Invoice-Data.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */

package se.sll.invoicedata.core.security;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class InvoiceDataUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(InvoiceDataUserDetailsService.class);
    private static Collection<? extends GrantedAuthority> grants = Collections.singleton(new GrantedAuthority() {
        private static final long serialVersionUID = 1L;
        @Override
        public String getAuthority() {
            return "ROLE_USER";
        }
        
    });

    @Value("${acl.allow:*}")
    private String[] acl;

    public InvoiceDataUserDetailsService() {
        super();
        log.info("Created with acl: {}", acl);
    }
    
    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
            throws UsernameNotFoundException {
        log.info("Authenticate: {}", token);
        return loadUserByUsername(token.getName());
    }

    @Override
    public UserDetails loadUserByUsername(String name)
            throws UsernameNotFoundException {
        if (allow(name)) {
            log.info("Allow: {}", name);
            return new User(name);
        }
        log.info("Deny: {}", name);
        throw new UsernameNotFoundException(name);
    }
    
    //
    private boolean allow(final String name) {
        if (acl == null) {
            log.warn("acl is null");
            return true;
        }
        if ((acl.length == 1) && "*".equals(acl[0])) {
            return true;
        }
        for (final String a : acl) {
            if (a.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * User impl.
     */
    public static class User implements UserDetails {
        private static final long serialVersionUID = 1L;
        
        private String name;
        
        public User(final String name) {
            this.name = name;
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return grants;
        }

        @Override
        public String getPassword() {
            return "";
        }

        @Override
        public String getUsername() {
            return name;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
        
    }
}
