package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    // TM 25/10/22 Refactoring : Utilitary method to simplify the code
    private void calculateTicketFromParkingDurationAndType(Integer parkingDurationInMinutes, ParkingType parkingType) {
        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(parkingDurationInMinutes);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);
    }

    @Test
    public void calculateFareCar() {
        // GIVEN
        int parkingDurationInMinutes = 60;
        ParkingType parkingType = ParkingType.CAR;

        // WHEN
        calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        // GIVEN
        int parkingDurationInMinutes = 60;
        ParkingType parkingType = ParkingType.BIKE;

        // WHEN
        calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
        // GIVEN
        int parkingDurationInMinutes = 60;
        ParkingType parkingType = null;

        // WHEN
        Executable action = () -> calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertThrows(NullPointerException.class, action);
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        // GIVEN
        int parkingDurationInMinutes = -60;
        ParkingType parkingType = ParkingType.BIKE;

        // WHEN
        Executable action = () -> calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        // GIVEN
        int parkingDurationInMinutes = 45;
        ParkingType parkingType = ParkingType.BIKE;

        // WHEN
        calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertEquals(((parkingDurationInMinutes / 60.0) * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        // GIVEN
        int parkingDurationInMinutes = 45;
        ParkingType parkingType = ParkingType.CAR;

        // WHEN
        calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertEquals(((parkingDurationInMinutes / 60.0) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        // GIVEN
        int parkingDurationInMinutes = 24 * 60;
        ParkingType parkingType = ParkingType.CAR;

        // WHEN
        calculateTicketFromParkingDurationAndType(parkingDurationInMinutes, parkingType);

        // THEN
        assertEquals(((parkingDurationInMinutes / 60.0) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }
}
