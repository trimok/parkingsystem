package com.parkit.parkingsystem.util;

/**
 * Math constants
 * 
 * @author trimok
 *
 */
public class MathUtil {
	/**
	 * SIXTY
	 */
	public static final double SIXTY = 60.0;
	/**
	 * TWENTY_FOUR
	 */
	public static final double TWENTY_FOUR = 24.0;
	/**
	 * THOUSAND
	 */

	public static final double THOUSAND = 1000.0;
	/**
	 * HOUR_IN_MICRO
	 */

	public static final double HOUR_IN_MICRO = THOUSAND * THOUSAND * SIXTY * SIXTY;
	/**
	 * HUNDRED
	 */

	public static final double HUNDRED = 100.0;
	/**
	 * PRECISION : the precision in comparing doubles
	 */

	public static final double PRECISION = 1E-15;
	/**
	 * HALF_HOUR
	 */

	public static final double HALF_HOUR = 0.5;

	/**
	 * Rounding a price
	 * 
	 * @param price
	 *            : the price to be rounded
	 * @return : the rounded price
	 */
	public static final double round(double price) {
		return Math.round(HUNDRED * price) / HUNDRED;
	}
}
