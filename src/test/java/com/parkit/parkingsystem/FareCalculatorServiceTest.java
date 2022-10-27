package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.parkit.parkingsystem.constants.ClientType;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	private static final double PRECISION = 1E-15;
	private static final double SIXTY = 60.0;

	@BeforeAll
	public static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	public void setUpPerTest() {
		ticket = new Ticket();
	}

	/**
	 * Refactoring : utilitary method for calculating fare of a ticket, from parkingDuration and parkingType
	 * 
	 * @param parkingDurationInMinutes
	 * @param parkingType
	 */
	// TM 25/10/22 Refactoring : Utilitary method to simplify the code
	public void calculateTicketFromParkingDurationAndType(Long parkingDurationInMinutes, ParkingType parkingType,
			ClientType clientType) {
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusMinutes(parkingDurationInMinutes);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setOldClient(clientType == ClientType.OLD ? true : false);

		fareCalculatorService.calculateFare(ticket);
	}

	/**
	 * Generic Method to calculate correct prices for different vehicle types and parking durations
	 * 
	 * see fareParametersProvider
	 * 
	 * @param parkingType
	 * @param rate_per_hour
	 * @param parkingDurationInMinutes
	 * @param expectedFare
	 */
	@ParameterizedTest(name = "Fare for client {0}, Type vehicle {1}, parking duration of {3} minutes should be equals to {4}")
	@MethodSource("fareParametersProvider")
	public void calculateFare(ClientType clientType, ParkingType parkingType, double rate_per_hour,
			long parkingDurationInMinutes, double expectedFare) {

		// WHEN
		calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType, clientType);

		// THEN
		assertThat(ticket.getPrice()).isCloseTo(expectedFare, Assertions.offset(PRECISION));
	}

	/**
	 * Arguments Provider for the method calculateFare
	 * 
	 * @return
	 */
	public static Stream<Arguments> fareParametersProvider() {
		// GIVEN
		return Stream.of(
				// VEHICLE TYPE, RATE_PER_HOUR, PARKING DURATION IN MINUTES, EXPECTED FARE, OLD_CLIENT

				// NEW CLIENT
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 60,
						(60 / SIXTY) * Fare.CAR_RATE_PER_HOUR),
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 45,
						(45 / SIXTY) * Fare.CAR_RATE_PER_HOUR),
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 24 * 60,
						(24 * 60 / SIXTY) * Fare.CAR_RATE_PER_HOUR),
				Arguments.arguments(ClientType.NEW, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 60,
						(60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR),
				Arguments.arguments(ClientType.NEW, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 45,
						(45 / SIXTY) * Fare.BIKE_RATE_PER_HOUR),
				Arguments.arguments(ClientType.NEW, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 24 * 60,
						(24 * 60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR),

				// OLD CLIENT
				Arguments.arguments(ClientType.OLD, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 60,
						(60.0 / SIXTY) * Fare.CAR_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR),
				Arguments.arguments(ClientType.OLD, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 45,
						(45 / SIXTY) * Fare.CAR_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR),
				Arguments.arguments(ClientType.OLD, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 24 * 60,
						(24 * 60 / SIXTY) * Fare.CAR_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR),
				Arguments.arguments(ClientType.OLD, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 60,
						(60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR),
				Arguments.arguments(ClientType.OLD, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 45,
						(45 / SIXTY) * Fare.BIKE_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR),
				Arguments.arguments(ClientType.OLD, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 24 * 60,
						(24 * 60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR));

	}

	/**
	 * Generic Method to check exception throws for unknown vehicle type or negative parking duration
	 * 
	 * see fareExceptionParametersProvider
	 * 
	 * @param parkingType
	 * @param rate_per_hour
	 * @param parkingDurationInMinutes
	 * @param expectedFare
	 */
	@ParameterizedTest(name = "An exception ot type {2} should be thrown for vehicle Type = {0} and parking duration of {1} ")
	@MethodSource("fareExceptionParametersProvider")
	public void calculateFareException(ParkingType parkingType, long parkingDurationInMinutes,
			Class<Exception> exceptionClass) {
		// WHEN
		Executable action = () -> calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType,
				ClientType.OLD);

		// THEN
		assertThrows(exceptionClass, action);
	}

	/**
	 * Arguments Provider for the method calculateFareException
	 * 
	 * @return
	 */
	public static Stream<Arguments> fareExceptionParametersProvider() {
		// GIVEN
		return Stream.of(
				// VEHICLE TYPE, PARKING DURATION IN MINUTES, EXCEPTION CLASS
				Arguments.arguments(null, 60, NullPointerException.class),
				Arguments.arguments(ParkingType.CAR, -60, IllegalArgumentException.class),
				Arguments.arguments(ParkingType.BIKE, -60, IllegalArgumentException.class));
	}
}
