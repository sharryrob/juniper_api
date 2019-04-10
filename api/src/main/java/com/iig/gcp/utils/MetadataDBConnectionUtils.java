package com.iig.gcp.utils;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.iig.gcp.constants.*;

@Component
public class MetadataDBConnectionUtils {

	private static String oracle_ip_port;

	/**
	 * @param value
	 */
	@Value("${oracle.ip.port.sid}")
	public void setSid(String value) {
		MetadataDBConnectionUtils.oracle_ip_port = value;
	}

	private static String oracle_jdbc_url;

	/**
	 * @param value
	 */
	@Value("${oracle.jdbc.url}")
	public void setJdbcUrl(String value) {
		MetadataDBConnectionUtils.oracle_jdbc_url = value;
	}

	private static String oracle_user_name;

	/**
	 * @param value
	 */
	@Value("${oracle.user.name}")
	public void setDBName(String value) {
		MetadataDBConnectionUtils.oracle_user_name = value;
	}

	private static String oracle_decrypt_pwd;

	/**
	 * @param value
	 */
	@Value("${oracle.encrypt.pwd}")
	public void setPassword(String value) {
		MetadataDBConnectionUtils.oracle_decrypt_pwd = value;
	}

	private static String master_key_path;

	/**
	 * @param value
	 */
	@Value("${master.key.path}")
	public void setMasterKeyPath(String value) {
		MetadataDBConnectionUtils.master_key_path = value;
	}

	/**
	 * @return Connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Connection getOracleConnection() throws ClassNotFoundException, SQLException, Exception {

		Class.forName(OracleConstants.ORACLE_DRIVER);
		String content = EncryptionUtil.readFile(master_key_path);
		String connectionUrl = oracle_jdbc_url.replaceAll("#orcl_ip", oracle_ip_port);
		System.out.println("connection url is " + connectionUrl);
		byte[] base_pwd = org.apache.commons.codec.binary.Base64.decodeBase64(oracle_decrypt_pwd);
		String orcl_decoded_pwd = EncryptionUtil.decryptText(base_pwd, EncryptionUtil.decodeKeyFromString(content));
		Connection conn = DriverManager.getConnection(connectionUrl, oracle_user_name, orcl_decoded_pwd);
		return conn;
	}

}
