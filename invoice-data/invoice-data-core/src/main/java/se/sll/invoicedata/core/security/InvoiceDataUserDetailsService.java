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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Security policy.
 * 
 * @author Peter
 *
 */
public class InvoiceDataUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(InvoiceDataUserDetailsService.class);

    @Value("${security.acl}") 
    private String[] aclAllow;

    public InvoiceDataUserDetailsService() {
        super();
    }
    
    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
            throws UsernameNotFoundException {
        log.debug("Authenticate: {}", token);
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
    
    private boolean allow(final String name) {
        if ((aclAllow.length == 1) && "*".equals(aclAllow[0])) {
            return true;
        }
        for (final String a : aclAllow) {
            if (a.equals(name)) {
                return true;
            }
        }
        return false;
    }

    
}
