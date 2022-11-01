package com.parkit.parkingsystem.integration.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.parkit.parkingsystem.config.DataBaseConfig;

/**
 * Configuration for test database
 * 
 * @author trimok
 *
 */
public class DataBaseTestConfig extends DataBaseConfig {

	/**
	 * The properties file where to read the parameters of the driver and the database access
	 */
	{
		databasePropertiesFile = "database_test.properties";
	}

	@Override
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

	@Override
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
