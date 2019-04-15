package com.iig.gcp.extraction.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKey;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.iig.gcp.constants.OracleConstants;
import com.iig.gcp.extraction.dto.ConnectionDTO;

import com.iig.gcp.utils.ConnectionUtils;
import com.iig.gcp.utils.EncryptionUtil;

@Component
public class APIConnectionRepositoryImpl implements APIConnectionRepository {

	@Autowired
	private ConnectionUtils ConnectionUtils;

	private static String master_key_path;

	/**
	 * @param value
	 */
	@SuppressWarnings("static-access")
	@Value("${master.key.path}")
	public void setMasterKeyPath(String value) {
		this.master_key_path = value;
	}

	/**
	 * @return String
	 */
	@Override
	public String addAPIConnectionDetails(ConnectionDTO connDto) {

		Connection conn = null;
		try {

			conn = ConnectionUtils.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to connect to Metadata database";
		}

		int system_sequence = 0;
		int project_sequence = 0;

		try {
			system_sequence = getSystemSequence(conn, connDto.getSource_System());
			project_sequence = getProjectSequence(conn, connDto.getProject_name());

			System.out.println("system sequence is " + system_sequence + " project sequence is " + project_sequence);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error retrieving system and project details";
		}
		String sequence = "";
		String connectionId = "";
		byte[] encrypted_key = null;
		byte[] encrypted_password = null;
		if (system_sequence != 0 && project_sequence != 0) {

			try {
				encrypted_key = getEncryptedKey(conn, system_sequence, project_sequence);
			} catch (Exception e) {
				e.printStackTrace();
				return "Error occured while fetching encryption key";
			}
			try {
				encrypted_password = encryptPassword(encrypted_key, connDto.getApi_password());
			} catch (Exception e) {
				e.printStackTrace();
				return "Error occurred while encrypting password";
			}

			PreparedStatement pstm = null;

			try {

				String insert_sql = "INSERT INTO JUNIPER_EXT_API_CONN_MASTER"
						+ "(API_CONN_NAME,API_CONN_TYPE, AUTH_API_URL,AUTH_USERNAME,AUTH_PASSWORD, AUTH_DRIVE_PATH, AUTH_FILE_PATH, AUTH_FILENAME, ENCRYPTED_ENCR_KEY, SOURCE_SYSTEM, SYSTEM_SEQUENCE, PROJECT_SEQUENCE, CREATED_BY, CREATED_DATE) VALUES"
						+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstm = conn.prepareStatement(insert_sql);

				// FEED_SEQUENCE
				pstm.setString(1, connDto.getConnection_name());
				// TABLE_NAME
				pstm.setString(2, "Api");
				// COLUMNS
				pstm.setString(3, connDto.getApi_Url());
				// WHERE_CLAUSE
				pstm.setString(4, connDto.getApi_user_name());
				// FETCH_TYPE
				pstm.setBytes(5, encrypted_password);
				pstm.setString(6, connDto.getAuth_drive_path());
				pstm.setString(7, connDto.getAuth_file_path());
				pstm.setString(8, connDto.getAuth_file_name());
				pstm.setBytes(9, encrypted_key);
				pstm.setString(10, connDto.getSource_System());
				pstm.setInt(11, system_sequence);
				pstm.setInt(12, project_sequence);
				pstm.setString(13, connDto.getCreated_By());
				Date dDate = new Date(connDto.getCreated_Date().getTime());
				pstm.setDate(14, dDate);
				pstm.executeUpdate();
				pstm.close();
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}

			try {
				Statement statement = conn.createStatement();
				String query = OracleConstants.GETSEQUENCEID.replace("${tableName}", OracleConstants.CONNECTIONTABLE)
						.replace("${columnName}", OracleConstants.CONNECTIONTABLEKEY);
				ResultSet rs = statement.executeQuery(query);
				if (rs.isBeforeFirst()) {
					rs.next();
					sequence = rs.getString(1).split("\\.")[1];
					rs = statement.executeQuery(OracleConstants.GETLASTROWID.replace("${id}", sequence));
					if (rs.isBeforeFirst()) {
						rs.next();
						connectionId = rs.getString(1);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return e.getMessage();
			} finally {
				try {
					pstm.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return "Connection added successfully:" + connectionId;
		} else {
			return "System/Project not found";
		}
	}

	/**
	 * @param conn
	 * @param system_name
	 * @return integer
	 * @throws SQLException
	 */
	private int getSystemSequence(Connection conn, String system_name) throws SQLException {
		// TODO Auto-generated method stub
		String query = "select system_sequence from " + OracleConstants.SYSTEMTABLE + " where system_name='"
				+ system_name + "'";
		int sys_seq = 0;
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.isBeforeFirst()) {

			rs.next();
			sys_seq = rs.getInt(1);

		}

		return sys_seq;

	}

	/**
	 * @param conn
	 * @param project
	 * @return integer
	 * @throws SQLException
	 */
	private int getProjectSequence(Connection conn, String project) throws SQLException {
		// TODO Auto-generated method stub
		String query = "select project_sequence from " + OracleConstants.PROJECTTABLE + " where project_id='" + project
				+ "'";
		int proj_seq = 0;
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.isBeforeFirst()) {

			rs.next();
			proj_seq = rs.getInt(1);

		}

		return proj_seq;
	}

	/**
	 * @return String
	 */
	@Override
	public String updateAPIConnectionDetails(ConnectionDTO connDto) {

		Connection conn = null;
		try {
			conn = ConnectionUtils.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to connect to Metadata database";
		}

//		String updateConnectionMaster = "";
		PreparedStatement pstm = null;
		int system_sequence = 0;
		int project_sequence = 0;
		byte[] encrypted_key = null;
		byte[] encrypted_password = null;

		try {
			system_sequence = getSystemSequence(conn, connDto.getSource_System());
			project_sequence = getProjectSequence(conn, connDto.getProject_name());
		} catch (SQLException e) {
			e.printStackTrace();
			return "Error while retrieving system or project Details";
		}

		if (system_sequence != 0 && project_sequence != 0) {

			if (!(connDto.getApi_password() == null || connDto.getApi_password().isEmpty())) {
				try {
					encrypted_key = getEncryptedKey(conn, system_sequence, project_sequence);
				} catch (Exception e) {
					e.printStackTrace();
					return "Error occured while fetching encryption key";
				}
				try {
					encrypted_password = encryptPassword(encrypted_key, connDto.getApi_password());
				} catch (Exception e) {
					e.printStackTrace();
					return "Error occurred while encrypting password";
				}
			} else {
				return "Password can not be Null";
			}

			try {
				String connection_name = connDto.getConnection_name();
				int connection_id = connDto.getConnection_id();
				String projectid = connDto.getProject_name();
				String update_sql = "UPDATE JUNIPER_EXT_API_CONN_MASTER SET AUTH_API_URL=?, AUTH_USERNAME=?, AUTH_PASSWORD=?,  AUTH_FILE_PATH=?, AUTH_FILENAME=?, ENCRYPTED_ENCR_KEY = ?, SYSTEM_SEQUENCE=?, UPDATED_BY=?, UPDATED_DATE=? "
						+ " WHERE API_CONN_TYPE='Api' AND API_CONN_NAME= '" + connection_name
						+ "' AND API_CONN_SEQUENCE=" + connection_id
						+ " AND PROJECT_SEQUENCE = (select PROJECT_SEQUENCE from juniper_project_master where project_id='"
						+ projectid + "')";

				pstm = conn.prepareStatement(update_sql);
				pstm.setString(1, connDto.getApi_Url());
				pstm.setString(2, connDto.getApi_user_name());
				pstm.setBytes(3, encrypted_password);
				// pstm.setString(3, connDto.getApi_password());
				pstm.setString(4, connDto.getAuth_file_path());
				pstm.setString(5, connDto.getAuth_file_name());
				pstm.setBytes(6, encrypted_key);
				pstm.setInt(7, system_sequence);
				// pstm.setInt(8, project_sequence);
				pstm.setString(8, connDto.getUpdated_By());
				Date dDate = new Date(connDto.getUpdated_Date().getTime());
				pstm.setDate(9, dDate);
				pstm.executeUpdate();
				pstm.close();
				return "Connection updated successfully: " + connection_id;

			} catch (SQLException e) {
				e.printStackTrace();
				return e.getMessage();

			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "Execption ocurred while closing connection";
				}
			}

		} else {

			return "System or Project does not Exist";
		}

	}

	/**
	 * @param json
	 * @param url
	 * @return String
	 * @throws UnsupportedOperationException
	 * @throws Exception
	 */
	private String invokeEncryption(JSONObject json, String url) throws UnsupportedOperationException, Exception {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Content-Type", "application/json");
		StringEntity input = new StringEntity(json.toString());
		postRequest.setEntity(input);
		HttpResponse response = httpClient.execute(postRequest);
		HttpEntity respEntity = response.getEntity();
		return EntityUtils.toString(respEntity);
	}

	/**
	 * @param conn
	 * @param system_sequence
	 * @param project_sequence
	 * @return byte[]
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private byte[] getEncryptedKey(Connection conn, int system_sequence, int project_sequence) throws Exception {

		JSONObject json = new JSONObject();
		json.put("system", Integer.toString(system_sequence));
		json.put("project", Integer.toString(project_sequence));

		String query = "select key_value from " + OracleConstants.KEYTABLE + " where system_sequence=" + system_sequence
				+ " and project_sequence=" + project_sequence;
		byte[] encrypted_key = null;
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.isBeforeFirst()) {

			rs.next();
			encrypted_key = rs.getBytes(1);
			return encrypted_key;

		} else {
			throw new Exception("Key not Found");
		}

	}

	/**
	 * @param encrypted_key
	 * @param password
	 * @return byte[]
	 * @throws Exception
	 */
	private byte[] encryptPassword(byte[] encrypted_key, String password) throws Exception {

		String content = EncryptionUtil.readFile(master_key_path);
		SecretKey secKey = EncryptionUtil.decodeKeyFromString(content);
		String decrypted_key = EncryptionUtil.decryptText(encrypted_key, secKey);
		byte[] encrypted_password = EncryptionUtil.encryptText(password, decrypted_key);
		return encrypted_password;

	}

	/**
	 * @return String
	 */
	public String decyptPassword(byte[] encrypted_key, byte[] encrypted_password) throws Exception {

		String content = EncryptionUtil.readFile(master_key_path);
		SecretKey secKey = EncryptionUtil.decodeKeyFromString(content);
		String decrypted_key = EncryptionUtil.decryptText(encrypted_key, secKey);
		SecretKey secKey2 = EncryptionUtil.decodeKeyFromString(decrypted_key);
		String password = EncryptionUtil.decryptText(encrypted_password, secKey2);
		return password;

	}

	/**
	 * @return String
	 */
	@Override
	public String deleteAPIConnectionDetails(String connection_name) throws Exception {
		/*
		 * PreparedStatement pstm=null; Connection conn=null; try {
		 * conn=ConnectionUtils.getConnection();
		 * 
		 * }catch(Exception e) { e.printStackTrace(); return
		 * "Failed to connect to Metadata database"; }
		 * 
		 * String delete_sql =
		 * "DELETE FROM JUNIPER_EXT_API_CONN_MASTER WHERE API_CONN_TYPE='Api' AND API_CONN_NAME= '"
		 * +connection_name+"'";
		 * 
		 * try { pstm = conn.prepareStatement(delete_sql);
		 * 
		 * pstm.executeUpdate(); pstm.close(); return
		 * "Connection deleted successfully: ";
		 * 
		 * }catch (SQLException e) { e.printStackTrace(); return e.getMessage();
		 * 
		 * 
		 * }finally { try { conn.close(); } catch (SQLException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); return
		 * "Execption ocurred while closing connection"; } }
		 */
		return null;
	}

}
