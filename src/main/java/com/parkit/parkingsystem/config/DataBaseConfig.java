package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * 
 * @author trimok
 * 
 *         Database configuration for production
 *
 */
public class DataBaseConfig {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	/**
	 * Getting the connection
	 * 
	 * @return : the connection
	 * @throws ClassNotFoundException
	 *             : driver not found
	 * @throws SQLException
	 *             : SQL error
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/prod", "root", "rootroot");
	}

	/**
	 * Closing the connection
	 * 
	 * @param con
	 *            : the connection
	 */
	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	/**
	 * Closing a prepared statement
	 * 
	 * @param ps
	 *            : the prepared statement
	 */
	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	/**
	 * Closing a result set
	 * 
	 * @param rs
	 *            : the result set
	 */
	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}
}
