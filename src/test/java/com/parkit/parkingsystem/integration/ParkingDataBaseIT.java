package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * Integration Tests
 * 
 * @author trimok
 *
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	/**
	 * Database de test
	 */
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	/**
	 * Parking spot DAO
	 */
	private static ParkingSpotDAO parkingSpotDAO;
	/**
	 * Ticket DAO
	 */
	private static TicketDAO ticketDAO;
	/**
	 * Utility class
	 */
	private static DataBasePrepareService dataBasePrepareService;

	/**
	 * Test vehicle number
	 */
	private static final String VEHICLE_NUMBER = "ABCDEF";
	private static final String BAD_VEHICLE_NUMBER = "AAAAAA";

	/**
	 * The mocking interactive shell
	 */
	@Mock
	private static InputReaderUtil inputReaderUtil;

	/**
	 * Init method for all tests
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@BeforeAll
	public static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	/**
	 * Init method for each test
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@BeforeEach
	public void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
	}

	/**
	 * Method launched after all tests
	 */
	@AfterAll
	public static void tearDown() {

	}

	/**
	 * Test for the incoming process
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@Test
	@DisplayName("IT :Incoming process, should write ticket in the database, with inTime filled")
	public void parking_whenIncomingProcess_shouldWriteCorrectTicketInDatabase() throws Exception {
		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_NUMBER);
		when(inputReaderUtil.readSelection()).thenReturn(ParkingType.CAR.ordinal() + 1);
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// TM 26/10/222 Get the ticket from the database
		Ticket ticket = parkingService.getLastTicketAndParkingSpotFromVehicleNumber(VEHICLE_NUMBER);

		// THEN
		// Verifying that the ticket exists in the database and has the correct vehicle number
		// inTime must exists, outTime and Price must not exist
		assertNotNull(ticket);
		assertEquals(ticket.getVehicleRegNumber(), inputReaderUtil.readVehicleRegistrationNumber());
		assertNotNull(ticket.getInTime());
		assertNull(ticket.getOutTime());
		assertEquals(ticket.getPrice(), 0.0);
		assertFalse(ticket.isOldClient());

		// Verifying that the Parking Spot exists in the database, and has the
		// available attribute to false
		assertNotNull(ticket.getParkingSpot());
		assertFalse(ticket.getParkingSpot().isAvailable());
	}

	/**
	 * Test exception for the incoming process : Bad Vehicle Type should throws Exception
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@Test
	@DisplayName("IT Exception :Incoming process, should raise Exception if bad vehicle type")
	public void parking_whenIncomingProcessAndBadVehickeType_shouldRaiseException() throws Exception {
		// GIVEN
		// Bad vehicle type
		when(inputReaderUtil.readSelection()).thenReturn(3);
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		Executable action = () -> parkingService.processIncomingVehicle();

		// THEN
		assertThrows(IllegalArgumentException.class, action);
	}

	/**
	 * Test for the simple incoming + exiting process
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@Test
	@DisplayName("IT : Simple incoming + exiting process, should write/update ticket in the database, with inTime/outTime filled")
	public void parking_whenSimpleIncomingPlusExitingProcess_shouldWriteCorrectTicketInDatabase() throws Exception {
		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_NUMBER);
		when(inputReaderUtil.readSelection()).thenReturn(ParkingType.CAR.ordinal() + 1);

		// WHEN
		// Simulation of simple incoming + exiting process
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		// TM 26/10/222 Get the ticket (and the associated ParkingSpot) from the database
		Ticket ticket = parkingService.getLastTicketAndParkingSpotFromVehicleNumber(VEHICLE_NUMBER);

		// THEN
		// Verifying that the ticket has a correct outTime attribute
		// the database
		assertNotNull(ticket);
		assertNotNull(ticket.getOutTime());
		assertFalse(ticket.isOldClient());

		// Verifying that the Parking Spot exist and is correctly updated (available = true)
		ParkingSpot parkingSpot = ticket.getParkingSpot();
		assertNotNull(parkingSpot);
		assertTrue(parkingSpot.isAvailable());
	}

	/**
	 * Test Exception for the simple incoming + exiting process, bad vehicle number
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@Test
	@DisplayName("IT Exception : Simple incoming + Bad vehicle Number, should raise exception ")
	public void parking_whenSimpleIncomingPlusExitingProcessBadVehicleNumber_shouldRaiseException() throws Exception {
		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_NUMBER);
		when(inputReaderUtil.readSelection()).thenReturn(ParkingType.CAR.ordinal() + 1);

		// WHEN
		// Simulation of simple incoming + exiting process
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// TM bad vehicle number in exiting process
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(BAD_VEHICLE_NUMBER);

		// WHEN
		Executable action = () -> parkingService.processExitingVehicle();

		// THEN
		assertThrows(Exception.class, action);
	}

	/**
	 * Test for the multiple incoming + exiting process
	 * 
	 * @throws Exception
	 *             : exception
	 */
	@Test
	@DisplayName("IT : Multiple incoming + exiting process, should write/update ticket in the database, with inTime/outTime/oldClient filled")
	public void parking_whenMultipleIncomingPlusExitingProcess_shouldWriteCorrectTicketInDatabase() throws Exception {
		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_NUMBER);
		when(inputReaderUtil.readSelection()).thenReturn(ParkingType.CAR.ordinal() + 1);

		// WHEN
		// Simulation of multiple incoming + exiting process
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		// TM 26/10/222 Get the ticket (and the associated ParkingSpot) from the database
		Ticket ticket = parkingService.getLastTicketAndParkingSpotFromVehicleNumber(VEHICLE_NUMBER);

		// THEN
		// Verifying that the ticket has a correct outTime attribute
		// the database
		assertNotNull(ticket);
		assertNotNull(ticket.getOutTime());
		assertTrue(ticket.isOldClient());

		// Verifying that the Parking Spot exist and is correctly updated (available = true)
		ParkingSpot parkingSpot = ticket.getParkingSpot();
		assertNotNull(parkingSpot);
		assertTrue(parkingSpot.isAvailable());
	}
}
