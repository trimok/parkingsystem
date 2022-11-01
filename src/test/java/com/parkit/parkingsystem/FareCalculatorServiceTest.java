package com.parkit.parkingsystem;

import static com.parkit.parkingsystem.util.MathUtil.PRECISION;
import static com.parkit.parkingsystem.util.MathUtil.SIXTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import com.parkit.parkingsystem.util.MathUtil;

/**
 * Unitary Tests for FareCalculator Service
 * 
 * @author trimok
 *
 */
public class FareCalculatorServiceTest {

	/**
	 * The calculator service
	 */
	private static FareCalculatorService fareCalculatorService;
	/**
	 * The ticket
	 */
	private Ticket ticket;

	/**
	 * Init method before all tests
	 */
	@BeforeAll
	public static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	/**
	 * Init method before each test
	 */

	@BeforeEach
	public void setUpPerTest() {
		ticket = new Ticket();
	}

	// TM 25/10/22 Refactoring : Utilitary method to simplify the test code
	/**
	 * 
	 * Refactoring : utilitary method for calculating fare of a ticket, from parkingDuration and parkingType
	 * 
	 * @param parkingDurationInMinutes
	 *            : the parking duration in minutes
	 * @param parkingType
	 *            : the parking type
	 * @param clientType
	 *            : the client type
	 */
	public void calculateFareForClientTypeParkingTypeParkingDuration(Long parkingDurationInMinutes,
			ParkingType parkingType, ClientType clientType) {
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
	 * 
	 * Generic Method for calculating fare
	 * 
	 * @see fareParametersProvider
	 * 
	 * @param clientType
	 *            : the client type
	 * 
	 * @param parkingType
	 *            : the parking type
	 * @param rate_per_hour
	 *            : the rate per hour
	 * @param parkingDurationInMinutes
	 *            : the parking duration in mintues
	 * @param expectedFare
	 *            : the expected fare
	 */
	@DisplayName("Standard : ")
	@ParameterizedTest(name = "Fare for client {0}, Type vehicle {1}, parking duration of {3} minutes should be equals to {4}")
	@MethodSource("fareParametersProvider")
	public void calculateFare_ForClientTypeParkingTypeParkingDuration_shouldBeEqualsTo(ClientType clientType,
			ParkingType parkingType, double rate_per_hour, long parkingDurationInMinutes, double expectedFare) {

		// WHEN
		calculateFareForClientTypeParkingTypeParkingDuration(parkingDurationInMinutes, parkingType, clientType);

		// THEN
		assertThat(ticket.getPrice()).isCloseTo(expectedFare, Assertions.offset(PRECISION));
	}

	/**
	 * Arguments Provider for the method calculateFare
	 * 
	 * @return : the list of arguments
	 */
	public static Stream<Arguments> fareParametersProvider() {
		// GIVEN
		return Stream.of(
				// VEHICLE TYPE, RATE_PER_HOUR, PARKING DURATION IN MINUTES, EXPECTED FARE, OLD_CLIENT

				// NEW CLIENT (CAR)
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 60,
						MathUtil.round((60 / SIXTY) * Fare.CAR_RATE_PER_HOUR)),
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 45,
						MathUtil.round((45 / SIXTY) * Fare.CAR_RATE_PER_HOUR)),
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 24 * 60,
						MathUtil.round((24 * 60 / SIXTY) * Fare.CAR_RATE_PER_HOUR)),

				// NEW CLIENT (BIKE)
				Arguments.arguments(ClientType.NEW, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 60,
						MathUtil.round((60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR)),
				Arguments.arguments(ClientType.NEW, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 45,
						MathUtil.round((45 / SIXTY) * Fare.BIKE_RATE_PER_HOUR)),
				Arguments.arguments(ClientType.NEW, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 24 * 60,
						MathUtil.round((24 * 60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR)),

				// OLD CLIENT (5 % reduction) CAR
				Arguments.arguments(ClientType.OLD, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 60,
						MathUtil.round((60.0 / SIXTY) * Fare.CAR_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR)),
				Arguments.arguments(ClientType.OLD, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 45,
						MathUtil.round((45 / SIXTY) * Fare.CAR_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR)),
				Arguments.arguments(ClientType.OLD, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 24 * 60,
						MathUtil.round((24 * 60 / SIXTY) * Fare.CAR_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR)),

				// OLD CLIENT (5 % reduction) BIKE
				Arguments.arguments(ClientType.OLD, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 60,
						MathUtil.round((60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR)),
				Arguments.arguments(ClientType.OLD, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 45,
						MathUtil.round((45 / SIXTY) * Fare.BIKE_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR)),
				Arguments.arguments(ClientType.OLD, ParkingType.BIKE, Fare.BIKE_RATE_PER_HOUR, 24 * 60,
						MathUtil.round((24 * 60 / SIXTY) * Fare.BIKE_RATE_PER_HOUR * Fare.OLD_CLIENT_RATE_FACTOR)),

				// LESS THAN 30 minutes = 0 fare
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 29, 0.0),
				Arguments.arguments(ClientType.NEW, ParkingType.CAR, Fare.CAR_RATE_PER_HOUR, 30,
						MathUtil.round((30 / SIXTY) * Fare.CAR_RATE_PER_HOUR)));
	}

	/**
	 * Generic Method to check exception throws for unknown vehicle type or negative parking duration
	 * 
	 * @see fareExceptionParametersProvider
	 * 
	 * @param parkingType
	 *            : the parking type
	 * @param parkingDurationInMinutes
	 *            : the parking duration in minutes
	 * @param expectedExceptionClass
	 *            : the expected exception class
	 */
	@DisplayName("Exception : ")
	@ParameterizedTest(name = "Fare calculus : an exception ot type {2} should be thrown for vehicle Type = {0} and parking duration of {1} ")
	@MethodSource("fareExceptionParametersProvider")
	public void calculateFare_ForParkingTypeParkingDuration_shouldThrowsAssertion(ParkingType parkingType,
			long parkingDurationInMinutes, Class<Exception> expectedExceptionClass) {
		// WHEN
		Executable action = () -> calculateFareForClientTypeParkingTypeParkingDuration(parkingDurationInMinutes,
				parkingType, ClientType.OLD);

		// THEN
		assertThrows(expectedExceptionClass, action);
	}

	/**
	 * Arguments Provider for the method calculateFareException
	 * 
	 * @return : the list of the arguments
	 */
	public static Stream<Arguments> fareExceptionParametersProvider() {
		// GIVEN
		return Stream.of(
				// VEHICLE TYPE, PARKING DURATION IN MINUTES, EXCEPTION CLASS

				// ParkingType is null
				Arguments.arguments(null, 60, NullPointerException.class),

				// Parking duration is < 0
				Arguments.arguments(ParkingType.CAR, -60, IllegalArgumentException.class),
				Arguments.arguments(ParkingType.BIKE, -60, IllegalArgumentException.class));
	}
}
