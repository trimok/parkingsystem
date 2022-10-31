package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * Parking Service
 * 
 * @author trimok
 *
 */
public class ParkingService {

	/**
	 * The calculator service (except for mocking)
	 */
	private static final FareCalculatorService globalFareCalculatorService = new FareCalculatorService();

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("ParkingService");

	/**
	 * The interactive shell
	 */
	private InputReaderUtil inputReaderUtil;
	/**
	 * The ParkingSpot Dao
	 */
	private ParkingSpotDAO parkingSpotDAO;
	/**
	 * The Ticket Dao
	 */
	private TicketDAO ticketDAO;
	// TM 31/10/22 For testing calculatorService as a mock
	/**
	 * CalculatorService for mocking
	 */
	private FareCalculatorService fareCalculatorService;

	// TM 31/10/22 standard constructor
	/**
	 * Standard constructor
	 * 
	 * @param inputReaderUtil
	 *            : the interactive shell
	 * @param parkingSpotDAO
	 *            : the parkingSpot DAO
	 * @param ticketDAO
	 *            : the ticket DAO
	 */
	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
		this.fareCalculatorService = globalFareCalculatorService;
	}

	// TM 31/10/22 For testing calculatorService as a mock
	/**
	 * Constructor for mocking the calculator service
	 * 
	 * @param inputReaderUtil
	 *            : the interactive shell
	 * @param parkingSpotDAO
	 *            : the parkingSpot DAO
	 * @param ticketDAO
	 *            : the ticket DAO
	 * @param fareCalculatorService
	 *            : the calculator service
	 */
	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO,
			FareCalculatorService fareCalculatorService) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
		this.fareCalculatorService = fareCalculatorService;
	}

	/**
	 * Get the calculator service
	 * 
	 * @return : the calculator service
	 */
	public FareCalculatorService getFareCalculatorService() {
		return fareCalculatorService;
	}

	/**
	 * Setting the calculator service
	 * 
	 * @param fareCalculatorService
	 *            : the calculator service
	 */
	public void setFareCalculatorService(FareCalculatorService fareCalculatorService) {
		this.fareCalculatorService = fareCalculatorService;
	}

	/**
	 * Incoming process
	 */
	public void processIncomingVehicle() {
		try {

			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehicleRegNumber();
				// TM 28/10/22 Look if it exists already a ticket
				boolean old_client = (getLastTicketAndParkingSpotFromVehicleNumber(vehicleRegNumber) != null)
						? true
						: false;

				parkingSpot.setAvailable(false);
				parkingSpotDAO.updateParking(parkingSpot);// allot this parking
															// space and mark
															// it's availability
															// as
															// false
				LocalDateTime inTime = LocalDateTime.now();
				Ticket ticket = new Ticket();
				// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME,
				// OUT_TIME)
				// ticket.setId(ticketID);
				ticket.setParkingSpot(parkingSpot);
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(0);
				ticket.setInTime(inTime);
				ticket.setOutTime(null);
				ticket.setOldClient(old_client);
				ticketDAO.saveTicket(ticket);

				// TM Message for old client
				if (old_client) {
					System.out.println(
							"\"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.\"");
				}
				System.out.println("Generated Ticket and saved in DB");
				System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
				System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	/**
	 * Getting the vehicle number
	 * 
	 * @return : the vehicle number
	 * @throws Exception
	 *             : bad user input
	 * 
	 */
	private String getVehicleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	/**
	 * Getting the next available parking number
	 * 
	 * @return : the next available parking number
	 */
	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehicleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	/**
	 * Getting the vehicle type
	 * 
	 * @return : the vehicle type
	 */
	private ParkingType getVehicleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
			case 1 : {
				return ParkingType.CAR;
			}
			case 2 : {
				return ParkingType.BIKE;
			}
			default : {
				System.out.println("Incorrect input provided");
				throw new IllegalArgumentException("Entered input is invalid");
			}
		}
	}

	/**
	 * Exiting process
	 */
	public void processExitingVehicle() {
		try {
			String vehicleRegNumber = getVehicleRegNumber();
			// TM 28/10/22 Search for the last ticket and its associated ParkingSpot for the vehicle in the database
			Ticket ticket = getLastTicketAndParkingSpotFromVehicleNumber(vehicleRegNumber);
			LocalDateTime outTime = LocalDateTime.now();
			ticket.setOutTime(outTime);
			fareCalculatorService.calculateFare(ticket);
			if (ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);

				System.out.println("Please pay the parking fare:" + ticket.getPrice());
				System.out.println(
						"Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
			} else {
				System.out.println("Unable to update ticket information. Error occurred");
			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}

	/**
	 * Method to get a ticket and its associated parkingSpot from a vehicleNumber
	 * 
	 * @param vehicleRegNumber
	 *            : the vehicle number
	 * @return : the last ticket with this vehicle number
	 */
	// TM 26/10/22 Method to get the first ticket (with the associated ParkingSpot) from the vehicle number
	public Ticket getLastTicketAndParkingSpotFromVehicleNumber(String vehicleRegNumber) {
		Ticket ticket = null;
		try {
			// TM 28/10/22 Get the most recent ticket corresponding to the vehicle
			ticket = ticketDAO.getLastTicket(vehicleRegNumber);

			// Get the parkingSpot from the database
			if (ticket != null) {
				ParkingSpot parkingSpotSkeleton = ticket.getParkingSpot();
				if (parkingSpotSkeleton != null) {
					int parkingNumber = parkingSpotSkeleton.getId();
					ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(parkingNumber);

					// Make the link with the ticket
					ticket.setParkingSpot(parkingSpot);
				}
			}

			return ticket;
		} catch (Exception e) {
			logger.error("Unable to get first ticket from vehicle", e);
		}
		return ticket;
	}
}
