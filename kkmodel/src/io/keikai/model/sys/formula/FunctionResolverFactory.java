/* FunctionResolverFactory.java

	Purpose:
		
	Description:
		
	History:
		Dec 26, 2013 Created by Pao Wang

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package io.keikai.model.sys.formula;


import io.keikai.model.impl.sys.formula.FunctionResolverImpl;
import org.zkoss.lang.Library;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory of formula function resolver.
 * @author Pao
 */
public class FunctionResolverFactory {
	private static final Logger logger = Logger.getLogger(FunctionResolverFactory.class.getName());

	private static Class<?> functionResolverClazz;
	static {
		String clz = Library.getProperty("io.keikai.model.FunctionResolver.class");
		if(clz!=null){
			try {
				functionResolverClazz = Class.forName(clz);
			} catch(Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}			
		}
	}

	public static FunctionResolver createFunctionResolver() {
		try {
			if(functionResolverClazz != null) {
				return (FunctionResolver)functionResolverClazz.newInstance();
			}
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			functionResolverClazz = null;
		}
		return new FunctionResolverImpl();
	}
}
