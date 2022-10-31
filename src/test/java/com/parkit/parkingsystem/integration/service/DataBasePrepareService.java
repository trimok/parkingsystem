package com.parkit.parkingsystem.integration.service;

import java.sql.Connection;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

/**
 * Preparation for Test Database
 * 
 * @author trimok
 *
 */
public class DataBasePrepareService {

	/**
	 * Test database config
	 */
	DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	/**
	 * Suppress all test database entries
	 */
	public void clearDataBaseEntries() {
		Connection connection = null;
		try {
			connection = dataBaseTestConfig.getConnection();

			// set parking entries to available
			connection.prepareStatement("update parking set available = true").execute();

			// clear ticket entries;
			connection.prepareStatement("truncate table ticket").execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataBaseTestConfig.closeConnection(connection);
		}
	}
}
