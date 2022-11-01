package com.parkit.parkingsystem.constants;

/**
 * Constants for database queries
 * 
 * @author trimok
 *
 */
public class DBConstants {

	/**
	 * SQL Query for getting the next available spot
	 */
	public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
	/**
	 * SQL Query for updating a parking spot
	 */
	public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";
	// TM 26/10/22
	/**
	 * SQL Query for getting a parking spot
	 */
	public static final String GET_PARKING_SPOT = "SELECT AVAILABLE, TYPE FROM PARKING WHERE PARKING_NUMBER = ?";

	/**
	 * SQL Query for saving a ticket
	 */
	public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, OLD_CLIENT) values(?,?,?,?,?,?)";
	/**
	 * SQL Query for updating a ticket
	 */
	public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
	/**
	 * SQL Query for getting the first ticket
	 */
	public static final String GET_FIRST_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, t.OLD_CLIENT, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
	/**
	 * SQL Query for getting the last ticket
	 */
	public static final String GET_LAST_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, t.OLD_CLIENT, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME DESC  limit 1";

	/**
	 * DRIVER_CLASS
	 */
	// private static final String URL = "jdbc:mysql://localhost:3306/prod";
	public static final String DRIVER_CLASS = "DRIVER_CLASS";
	/**
	 * URL
	 */
	// private static final String URL = "jdbc:mysql://localhost:3306/prod";
	public static final String URL = "URL";
	/**
	 * PORT
	 */
	public static final String PORT = "PORT";
	/**
	 * DATABASE
	 */
	public static final String DATABASE = "DATABASE";
	/**
	 * USERr
	 */
	public static final String USER = "USER";
	/**
	 * PASSWORD
	 */
	public static final String PASSWORD = "PASSWORD";
	/**
	 * SEPARATOR
	 */
	public static final String SEPARATOR = "/";
	/**
	 * SEPARATOR_PORT
	 */
	public static final String SEPARATOR_PORT = ":";

}
