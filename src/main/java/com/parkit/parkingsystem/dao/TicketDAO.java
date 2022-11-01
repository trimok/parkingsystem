package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * A DAO for the Ticket object
 * 
 * @author trimok
 *
 */
public class TicketDAO {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("TicketDAO");

	/**
	 * The Database
	 */
	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * Saving a ticket object in the database
	 * 
	 * @param ticket
	 *            : the ticket
	 * @return : a boolean indicating that the saving operation is a success
	 */
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, OLD_CLIENT)
			// ps.setInt(1,ticket.getId());
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : Timestamp.valueOf(ticket.getOutTime()));
			ps.setBoolean(6, ticket.isOldClient());
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			if (ps != null) {
				dataBaseConfig.closePreparedStatement(ps);
			}
			if (con != null) {
				dataBaseConfig.closeConnection(con);
			}
		}
		return false;
	}

	/**
	 * Getting the last object saved in the database from a vehicle number
	 * 
	 * @param vehicleRegNumber
	 *            : the vehicle number
	 * @return : the last ticket in the database with the corresponding vehicle number
	 */
	public Ticket getLastTicket(String vehicleRegNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_LAST_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				// TM 31/10/22 Minimal (Skeleton) Parking Spot, only the key (parking number) is correct
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1));
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				// TM 25/10/22 Take in account null values
				Timestamp inTimestamp = rs.getTimestamp(4);
				if (inTimestamp != null) {
					ticket.setInTime(inTimestamp.toLocalDateTime());
				}
				Timestamp outTimestamp = rs.getTimestamp(5);
				if (outTimestamp != null) {
					ticket.setOutTime(outTimestamp.toLocalDateTime());
				}
				ticket.setOldClient(rs.getBoolean(6));
			}
			return ticket;

		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			if (rs != null) {
				dataBaseConfig.closeResultSet(rs);
			}
			if (ps != null) {
				dataBaseConfig.closePreparedStatement(ps);
			}
			if (con != null) {
				dataBaseConfig.closeConnection(con);
			}
		}
		return null;
	}

	/**
	 * Updating a ticket
	 * 
	 * @param ticket
	 *            : the ticket to update
	 * @return : a boolean indicating that the update operation is a success
	 */
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			if (ps != null) {
				dataBaseConfig.closePreparedStatement(ps);
			}
			if (con != null) {
				dataBaseConfig.closeConnection(con);
			}

		}
		return false;
	}
}
