package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static FareCalculatorService fareCalculatorService;
	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	private Ticket ticket;
	private ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	private final static String VEHICLE_NUMBER = "ABCDEF";

	@BeforeEach
	public void setUpPerTest() {
		// GIVEN
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_NUMBER);
			when(inputReaderUtil.readSelection()).thenReturn(1);

			ticket = new Ticket();
			ticket.setVehicleRegNumber(VEHICLE_NUMBER);
			LocalDateTime outTime = LocalDateTime.now();
			LocalDateTime inTime = outTime.minusHours(1);
			ticket.setInTime(inTime);
			ticket.setOutTime(outTime);
			ticket.setParkingSpot(parkingSpot);

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	/**
	 * Test for the exiting process for vehicle
	 */
	@Test
	@DisplayName("Unitary Test for the incoming process for vehicle")
	public void processIncomingVehicleTest() {

		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
		verify(fareCalculatorService, times(0)).calculateFare(any(Ticket.class));
	}

	/**
	 * Test for the simple incoming + exiting process for vehicle
	 */
	@Test
	@DisplayName("Test New Client = simple incoming + exiting")
	public void processSimpleIncomingPlusExitingVehicleTest() {

		// Capture des modèles de calcul
		final ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);

		// GIVEN
		when(ticketDAO.getLastTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.getParkingSpot(any(Integer.class))).thenReturn(parkingSpot);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);

		// WHEN
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();

		// THEN
		verify(fareCalculatorService, times(1)).calculateFare(ticketCaptor.capture());
		final List<Ticket> ticketList = ticketCaptor.getAllValues();
		assertNotEquals(ticketList.size(), 0);
		assertFalse(ticketList.get(0).isOldClient());

		verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
		verify(parkingSpotDAO, Mockito.times(2)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
	}
}
