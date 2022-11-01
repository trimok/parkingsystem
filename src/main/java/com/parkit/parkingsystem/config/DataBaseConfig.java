package com.parkit.parkingsystem.config;

import static com.parkit.parkingsystem.constants.DBConstants.DATABASE;
import static com.parkit.parkingsystem.constants.DBConstants.DRIVER_CLASS;
import static com.parkit.parkingsystem.constants.DBConstants.PASSWORD;
import static com.parkit.parkingsystem.constants.DBConstants.PORT;
import static com.parkit.parkingsystem.constants.DBConstants.SEPARATOR;
import static com.parkit.parkingsystem.constants.DBConstants.SEPARATOR_PORT;
import static com.parkit.parkingsystem.constants.DBConstants.URL;
import static com.parkit.parkingsystem.constants.DBConstants.USER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.util.PropertiesUtil;

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
	protected Logger logger = LogManager.getLogger("DataBaseConfig");

	/**
	 * The properties file where to read the parameters of the driver and the database access
	 */
	protected String databasePropertiesFile = "database_prod.properties";

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

		// TM 01/11/22 Get the parameters from the properties file
		Properties properties = PropertiesUtil.getDatabaseProperties(this, databasePropertiesFile);
		String driverClass = (String) properties.get(DRIVER_CLASS);
		String urlConnection = (String) properties.get(URL) + SEPARATOR_PORT + properties.get(PORT) + SEPARATOR
				+ properties.get(DATABASE);
		String user = (String) properties.get(USER);
		String password = (String) properties.get(PASSWORD);

		Class.forName(driverClass);
		return DriverManager.getConnection(urlConnection, user, password);
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
