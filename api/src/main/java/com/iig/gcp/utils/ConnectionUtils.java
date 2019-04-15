package com.iig.gcp.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ConnectionUtils {

	@Autowired
	private DataSource dataSource;

	/**
	 * @return Connection
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {

		// Here I using Oracle Database.
		// (You can change to use another database.)
		// return OracleConnUtils.getOracleConnection();

		// return OracleConnUtils.getOracleConnection();
		return dataSource.getConnection();
		// return MySQLConnUtils.getMySQLConnection();
		// return SQLServerConnUtils_JTDS.getSQLServerConnection_JTDS();
		// return SQLServerConnUtils_SQLJDBC.getSQLServerConnection_SQLJDBC();
		// return PostGresConnUtils.getPostGresConnection();
	}

	/*
	 * 
	 * public static void closeQuietly(Connection conn) { try { conn.commit();
	 * conn.close(); } catch (Exception e) { } }
	 */

	public static void rollbackQuietly(Connection conn) {
		try {
			conn.rollback();
		} catch (Exception e) {
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			rs.close();
		} catch (Exception e) {
		}
	}

	public static void closePrepareStatement(PreparedStatement ps) {
		try {
			ps.close();
		} catch (Exception e) {
		}
	}

}
