package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

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
	public void calculateTicketFromParkingDurationAndType(Long parkingDurationInMinutes, ParkingType parkingType) {
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusMinutes(parkingDurationInMinutes);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

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
	@ParameterizedTest(name = "The fare for a {0} and a parking duration of {2} minutes should be equals to {3}")
	@MethodSource("fareParametersProvider")
	public void calculateFare(ParkingType parkingType, double rate_per_hour, long parkingDurationInMinutes,
			double expectedFare) {
		// WHEN
		calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

		// THEN
		assertThat(ticket.getPrice()).isEqualTo(expectedFare);
	}

	/**
	 * Arguments Provider for the method calculateFare
	 * 
	 * @return
	 */
	public static Stream<Arguments> fareParametersProvider() {
		// GIVEN
		return Stream.of(
				// VEHICLE TYPE, RATE_PER_HOUR, PARKING DURATION IN MINUTES, EXPECTED FARE
				Arguments.arguments(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 60, (60 / 60.0) * Fare.CAR_RATE_PER_HOUR),
				Arguments.arguments(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 45, (45 / 60.0) * Fare.CAR_RATE_PER_HOUR),
				Arguments.arguments(ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 24 * 60,
						(24 * 60 / 60.0) * Fare.CAR_RATE_PER_HOUR),
				Arguments.arguments(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 60,
						(60 / 60.0) * Fare.BIKE_RATE_PER_HOUR),
				Arguments.arguments(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 45,
						(45 / 60.0) * Fare.BIKE_RATE_PER_HOUR),
				Arguments.arguments(ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 24 * 60,
						(24 * 60 / 60.0) * Fare.BIKE_RATE_PER_HOUR)

		);
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
		Executable action = () -> calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

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
