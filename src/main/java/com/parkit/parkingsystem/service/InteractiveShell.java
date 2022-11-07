package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * The interactive shell (console mode) for the application
 * 
 * @author trimok
 *
 */
public class InteractiveShell {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("InteractiveShell");

	/**
	 * Launching the console mode
	 */
	public static void loadInterface() throws Exception {
		logger.info("App initialized!!!");
		System.out.println("Welcome to Parking System!");

		boolean continueApp = true;
		InputReaderUtil inputReaderUtil = new InputReaderUtil();
		ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
		TicketDAO ticketDAO = new TicketDAO();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		while (continueApp) {
			loadMenu();
			int option = inputReaderUtil.readSelection();
			switch (option) {
				case 1 : {
					try {
						parkingService.processIncomingVehicle();
					} catch (Exception e) {
						throw e;
					}

					break;
				}
				case 2 : {
					parkingService.processExitingVehicle();
					break;
				}
				case 3 : {
					System.out.println("Exiting from the system!");
					continueApp = false;
					break;
				}
				default :
					System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
			}
		}
	}

	/**
	 * Loading the main menu
	 */
	private static void loadMenu() {
		System.out.println("Please select an option. Simply enter the number to choose an action");
		System.out.println("1 New Vehicle Entering - Allocate Parking Space");
		System.out.println("2 Vehicle Exiting - Generate Ticket Price");
		System.out.println("3 Shutdown System");
	}

}
