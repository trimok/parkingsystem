package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * DAO for the ParkingSpot Object
 * 
 * @author trimok
 *
 */
public class ParkingSpotDAO {
	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	/**
	 * The database
	 */
	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * Getting the next available spot
	 * 
	 * @param parkingType
	 *            : the parking type
	 * @return the parking number
	 */
	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);;
			}
		} catch (RuntimeException re) {
			logger.error("Error fetching next available slot", re);
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
		return result;
	}

	/**
	 * Updating a ParkingSpot object
	 * 
	 * @param parkingSpot
	 *            : the parking spot
	 * @return true if update is success
	 */
	public boolean updateParking(ParkingSpot parkingSpot) {
		// update the availability fo that parking slot
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			return (updateRowCount == 1);
		} catch (RuntimeException re) {
			logger.error("Error updating parking info", re);
			return false;
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
			return false;
		} finally {
			if (ps != null) {
				dataBaseConfig.closePreparedStatement(ps);
			}
			if (con != null) {
				dataBaseConfig.closeConnection(con);
			}
		}
	}

	/**
	 * Method to get a ParkingSpot (from the database) from a parkingNumber
	 * 
	 * @param parkingNumber
	 *            : the parking number
	 * @return a ParkingSpot
	 */
	// TM 26/10/22
	public ParkingSpot getParkingSpot(int parkingNumber) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ParkingSpot parkingSpot = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_PARKING_SPOT);
			ps.setInt(1, parkingNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				boolean available = rs.getBoolean(1);
				ParkingType parkingType = ParkingType.valueOf(rs.getString(2));
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, available);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (RuntimeException re) {
			logger.error("Error fetching next available slot", re);

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
		return parkingSpot;
	}
}
