package com.parkit.parkingsystem.service;

import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		// TM 26/10/22 add a second to outTime, in case inTime = outTime in tests
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			System.out.println("In time" + ticket.getInTime());
			System.out.println("Out time" + ticket.getOutTime());
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// TM 25/10/22 Difference in Hours using LocalDateTime
		double differenceInHours = ChronoUnit.SECONDS.between(ticket.getInTime(), ticket.getOutTime()) / (3600.0);

		switch (ticket.getParkingSpot().getParkingType()) {
			case CAR : {
				ticket.setPrice(differenceInHours * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE : {
				ticket.setPrice(differenceInHours * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default :
				throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}