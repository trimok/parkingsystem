package com.parkit.parkingsystem.constants;

public class MathUtil {
	public static final double SIXTY = 60.0;
	public static final double TWENTY_FOUR = 24.0;
	public static final double THOUSAND = 1000.0;
	public static final double HOUR_IN_MICRO = THOUSAND * THOUSAND * SIXTY * SIXTY;
	public static final double HUNDRED = 100.0;
	public static final double PRECISION = 1E-15;
	public static final double HALF_HOUR = 0.5;

	public static final double round(double price) {
		return Math.round(HUNDRED * price) / HUNDRED;
	}
}
