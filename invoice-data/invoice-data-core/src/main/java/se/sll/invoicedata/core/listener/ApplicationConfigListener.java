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

/**
 * 
 */
package se.sll.invoicedata.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * @author muqkha
 *
 */
public class ApplicationConfigListener implements ApplicationContextAware {
	
	private static final Logger log = LoggerFactory.getLogger(ApplicationConfigListener.class);
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {		
		logConfiguration(appContext);
	}

	private void logConfiguration(ApplicationContext appContext) {
		Environment env = appContext.getEnvironment();
		if (env.getActiveProfiles().length == 0) {
			String errorMsg = "@setApplicationContext(): There are no ACTIVE Spring profiles available to run the application";			
			throw new IllegalStateException(errorMsg);
		} else {
			for (String profile : env.getActiveProfiles()) {
				log.info(String.format("@createWebApplicationContext(): Spring profile:%s is ACTIVE", profile));
			}
		}
	}
}
