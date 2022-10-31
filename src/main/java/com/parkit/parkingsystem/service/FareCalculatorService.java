package com.parkit.parkingsystem.service;

import static com.parkit.parkingsystem.constants.MathUtil.HALF_HOUR;
import static com.parkit.parkingsystem.constants.MathUtil.HOUR_IN_MICRO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.MathUtil;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		LocalDateTime inTime = ticket.getInTime();
		LocalDateTime outTime = ticket.getOutTime();

		// TM 31/10/22 Difference in nanosecondes
		double nanoDifferenceTime = ChronoUnit.MICROS.between(inTime, outTime);

		// TM 26/10/22 add a second to outTime, in case inTime = outTime in tests
		if ((inTime == null) || nanoDifferenceTime < 0) {
			throw new IllegalArgumentException("Times provided are incorrect: InTime = " + inTime.toString()
					+ ", outTime = " + outTime.toString());
		}

		// TM 25/10/22 Difference in hours
		double differenceInHours = nanoDifferenceTime / HOUR_IN_MICRO;

		// TM 27/10/22 reduction
		double factorRate = ticket.isOldClient() ? Fare.OLD_CLIENT_RATE_FACTOR : 1.0;

		// Base rate
		ParkingType parkingType = ticket.getParkingSpot().getParkingType();
		double rate_per_hour = 0;
		switch (parkingType) {
			case CAR : {
				rate_per_hour = Fare.CAR_RATE_PER_HOUR;
				break;
			}
			case BIKE : {
				rate_per_hour = Fare.BIKE_RATE_PER_HOUR;
				break;
			}
			default :
				throw new IllegalArgumentException("Unkown Parking Type");
		}

		// Price calculus and round
		// TM 28/10/22 => If parking time is less than 30 minutes, then the fare is 0
		double price = 0.0;
		if (differenceInHours >= HALF_HOUR) {
			price = MathUtil.round(differenceInHours * rate_per_hour * factorRate);
		}
		ticket.setPrice(price);
	}
}