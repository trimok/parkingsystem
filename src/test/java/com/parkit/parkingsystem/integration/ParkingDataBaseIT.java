package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	private static final String VEHICLE_NUMBER = "ABCDEF";

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	public static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	public void setUpPerTest() throws Exception {
		// TM 26/10/22 1 = type CAR, 2 = type BIKE
		when(inputReaderUtil.readSelection()).thenReturn(ParkingType.CAR.ordinal() + 1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_NUMBER);
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	public static void tearDown() {

	}

	/**
	 * Test for the process parking
	 * 
	 * @throws Exception
	 */
	@Test
	@DisplayName("Integration test for incoming parking process")
	public void testParkingACar() throws Exception {
		// GIVEN
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
	 * Test for the simple incoming + exiting process
	 * 
	 * @throws Exception
	 */
	@Test
	@DisplayName("Integration test for the simple incoming + exiting parking process")
	public void testSimpleIncomingExitingParkingProcess() throws Exception {
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
	 * Test for the multiple incoming + exiting process
	 * 
	 * @throws Exception
	 */
	@Test
	@DisplayName("Integration test for the multiple incoming + parking exiting process")
	public void testMultipleIncomingExitingParkingProcess() throws Exception {
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
