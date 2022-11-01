package com.parkit.parkingsystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesUtil
 * 
 * @author trimok
 *
 */
public class PropertiesUtil {

	/**
	 * Get a Properties object from a properties files
	 * 
	 * @param context
	 *            : the context object
	 * @param propertiesFile
	 *            : the properties file
	 * @return : a Properties object
	 */
	public final static Properties getDatabaseProperties(Object context, String propertiesFile) {
		InputStream is = context.getClass().getClassLoader().getResourceAsStream(propertiesFile);
		Properties properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return properties;
	}
}
