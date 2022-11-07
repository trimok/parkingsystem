package com.parkit.parkingsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.service.InteractiveShell;

/**
 * 
 * @author trimok
 *
 *         Entry point of the application
 *
 */
public class App {
	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("App");
	/**
	 * The entry method in the application
	 * 
	 * @param args
	 *            : the line command arguments
	 */
	public static void main(String args[]) throws Exception {
		logger.info("Initializing Parking System");
		try {
			InteractiveShell.loadInterface();
		} catch (Exception e) {
			throw e;
		}
	}
}
