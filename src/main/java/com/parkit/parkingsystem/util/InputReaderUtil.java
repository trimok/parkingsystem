package com.parkit.parkingsystem.util;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The interactive shell
 * 
 * @author trimok
 *
 */
public class InputReaderUtil {

	/**
	 * The scanner (console object)
	 */
	private static Scanner scan = new Scanner(System.in);
	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("InputReaderUtil");

	/**
	 * Reading the choice of the user (an integer)
	 * 
	 * @return : an integer corresponding to the selection
	 */
	public int readSelection() {
		try {
			int input = Integer.parseInt(scan.nextLine());
			return input;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter valid number for proceeding further");
			return -1;
		}
	}

	/**
	 * Reading the vehicle number
	 * 
	 * @return : the vehicle number
	 * @throws Exception
	 *             : invalid input
	 */
	public String readVehicleRegistrationNumber() throws Exception {
		try {
			String vehicleRegNumber = scan.nextLine();
			if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
				throw new IllegalArgumentException("Invalid input provided");
			}
			return vehicleRegNumber;
		} catch (Exception e) {
			logger.error("Error while reading user input from Shell", e);
			System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
			throw e;
		}
	}
}
