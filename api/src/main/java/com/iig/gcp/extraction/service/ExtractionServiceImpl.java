package com.iig.gcp.extraction.service;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;

import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFRow;

import org.json.JSONArray;
import org.json.JSONException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iig.gcp.constants.OracleConstants;

import com.iig.gcp.extraction.dto.ConnectionDTO;

import com.iig.gcp.utils.ConnectionUtils;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.iig.gcp.extraction.repository.APIConnectionRepository;

@Service
public class ExtractionServiceImpl implements ExtractionService {

	@Autowired
	private ConnectionUtils ConnectionUtils;

	@Autowired
	APIConnectionRepository APIRepository;

//private static String SCHEDULER_MASTER_TABLE = "JUNIPER_SCH_MASTER_JOB_DETAIL";

	/**
	 * @return String
	 */
	@Override
	public String invokeRest(String json, String url) throws UnsupportedOperationException, Exception {
		String resp = null;
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(/* OracleConstants.EXTRACTION_COMPUTE_URL + */ url);
		System.out.println(/* OracleConstants.EXTRACTION_COMPUTE_URL + */ url);
		postRequest.setHeader("Content-Type", "application/json");
		StringEntity input = new StringEntity(json);
		postRequest.setEntity(input);

		HttpResponse response = httpClient.execute(postRequest);
		String response_string = EntityUtils.toString(response.getEntity(), "UTF-8");
		if (response.getStatusLine().getStatusCode() != 200) {
			resp = "Error" + response_string;
			throw new Exception("Error" + response_string);
		} else {
			resp = response_string;
		}
		return resp;
	}

	/**
	 * @return ArrayList<ConnectionDTO>
	 */
	@Override
	public ArrayList<ConnectionDTO> getConnectionsAPI(String src_val, String project_id) throws Exception {
		// TODO Auto-generated method stub
		Connection connection = null;
		ConnectionDTO conn = null;
		PreparedStatement pstm = null;
		ArrayList<ConnectionDTO> arrConnectionMaster = new ArrayList<ConnectionDTO>();
		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection.prepareStatement(
					"SELECT API_CONN_SEQUENCE,API_CONN_NAME,AUTH_API_URL,AUTH_USERNAME,AUTH_PASSWORD,AUTH_DRIVE_PATH, AUTH_FILE_PATH,AUTH_FILENAME,SOURCE_SYSTEM from JUNIPER_EXT_API_CONN_MASTER where API_CONN_TYPE=? and project_sequence = (select project_sequence from juniper_project_master where project_id=?)");
			pstm.setString(1, src_val);
			pstm.setString(2, project_id);
			// System.out.println("src_val>>>"+src_val);
			// System.out.println("project_id>>>"+project_id);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				// System.out.println("in rs****************");
				conn = new ConnectionDTO();
				conn.setConnection_id(rs.getInt(1));
				conn.setConnection_name(rs.getString(2));
				conn.setApi_Url(rs.getString(3));
				conn.setApi_user_name(rs.getString(4));
				conn.setApi_password(null);
				// conn.setApi_password(rs.getString(5));
				conn.setAuth_drive_path(rs.getString(6));
				conn.setAuth_file_path(rs.getString(7));
				conn.setAuth_file_name(rs.getString(8));
				conn.setSource_System(rs.getString(9));

				arrConnectionMaster.add(conn);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			pstm.close();
			connection.close();
		}
		return arrConnectionMaster;
	}

	/**
	 * @return ConnectionDTO
	 */
	@Override
	public ConnectionDTO getConnections2API(String src_val, int conn_id, String project_id) throws Exception {
		// TODO Auto-generated method stub
		Connection connection = null;
		ConnectionDTO conn = new ConnectionDTO();
		PreparedStatement pstm = null;
		byte[] encrypted_password = null;
		byte[] encrypted_key = null;

		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection.prepareStatement(
					"select api_conn_sequence,api_conn_name,api_conn_type,auth_api_url, auth_username,auth_password,auth_drive_path,auth_file_path,auth_filename, source_system, encrypted_encr_key from JUNIPER_EXT_API_CONN_MASTER where project_sequence=(select project_sequence from juniper_project_master where project_id='"
							+ project_id + "') and api_conn_sequence=" + conn_id);

			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				conn.setConnection_id(rs.getInt(1));
				conn.setConnection_name(rs.getString(2));
				conn.setConnection_type(rs.getString(3));
				conn.setApi_Url(rs.getString(4));
				conn.setApi_user_name(rs.getString(5));
				// conn.setApi_password(null);
				encrypted_password = rs.getBytes(6);
				encrypted_key = rs.getBytes(11);
				String password = null;
				password = APIRepository.decyptPassword(encrypted_key, encrypted_password);
				conn.setApi_password(password);
				// conn.setApi_password(rs.getString(5));
				conn.setAuth_drive_path(rs.getString(7));
				conn.setAuth_file_path(rs.getString(8));
				conn.setAuth_file_name(rs.getString(9));
				conn.setSource_System(rs.getString(10));
			}
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			pstm.close();
			connection.close();
		}
		return conn;
	}

	/**
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getSystem(String project) throws Exception {
		ArrayList<String> sys = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement pstm = null;
		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection.prepareStatement("select a.system_name from JUNIPER_SYSTEM_MASTER a");
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				sys.add(rs.getString(1));
			}
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			// connection.close();
		} finally {
			pstm.close();
			connection.close();
		}
		return sys;
	}

	/**
	 * @return String
	 */
	public String getSystemName(int system) throws Exception {
		String sys = null;
		Connection connection = null;
		PreparedStatement pstm = null;
		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection.prepareStatement(
					"select a.system_name from JUNIPER_SYSTEM_MASTER a where system_sequence=" + system);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				sys = rs.getString(1);
			}
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			// connection.close();
		} finally {
			if (null != pstm) {
				pstm.close();
			}
			if (null != connection) {
				connection.close();
			}

		}
		return sys;
	}

	/**
	 * @param src_val
	 * @param src_sys_id
	 * @return String
	 * @throws Exception
	 */
	public String getDatabaseData(String src_val, int src_sys_id) throws Exception {
		String sch = "";
		Connection connection = null;
		PreparedStatement pstm = null;
		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection.prepareStatement(
					"select table_name from JUNIPER_EXT_TABLE_MASTER where FEED_SEQUENCE=" + src_sys_id);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				sch = rs.getString(1).split("\\.")[0];
			}
			connection.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			// connection.close();
		}

		finally {
			pstm.close();
			connection.close();
		}
		return sch;
	}

	/**
	 * @return String
	 */
	public String addAPIConnection(String x) throws Exception {
		// Parse json to Dto Object
		String testConnStatus = "";
		String status = "";
		String message = "";
		String response = "";

		JSONObject jsonObject = new JSONObject(x);
		// String connection_id =
		// jsonObject.getJSONObject("body").getJSONObject("data").get("conn").toString();
		String connection_name = jsonObject.getJSONObject("body").getJSONObject("data").get("connection_name")
				.toString();
		String connection_type = jsonObject.getJSONObject("body").getJSONObject("data").get("connection_type")
				.toString();
		String source_System = jsonObject.getJSONObject("body").getJSONObject("data").get("system").toString();
		String api_url = jsonObject.getJSONObject("body").getJSONObject("data").get("api_url").toString();
		String api_user_name = jsonObject.getJSONObject("body").getJSONObject("data").get("api_user_name").toString();
		String api_password = jsonObject.getJSONObject("body").getJSONObject("data").get("api_user_password")
				.toString();
		String auth_drive_path = jsonObject.getJSONObject("body").getJSONObject("data").get("auth_drive_path")
				.toString();
		String auth_file_path = jsonObject.getJSONObject("body").getJSONObject("data").get("auth_file_path").toString();
		String auth_file_name = jsonObject.getJSONObject("body").getJSONObject("data").get("auth_file_name").toString();
		String project_name = jsonObject.getJSONObject("body").getJSONObject("data").get("project").toString();
		String user = jsonObject.getJSONObject("body").getJSONObject("data").get("user").toString();

		ConnectionDTO connDto = new ConnectionDTO();

		// connDto.setConnection_id(Integer.parseInt(connection_id));
		connDto.setConnection_name(connection_name);
		connDto.setConnection_type(connection_type);
		connDto.setSource_System(source_System);
		connDto.setApi_Url(api_url);
		connDto.setApi_user_name(api_user_name);
		// encrypt code
		connDto.setApi_password(api_password);
		connDto.setAuth_drive_path(auth_drive_path);
		connDto.setAuth_file_path(auth_file_path);
		connDto.setAuth_file_name(auth_file_name);
		connDto.setProject_name(project_name);
		connDto.setCreated_By(user);
		connDto.setCreated_Date(new Date());
		// connDto.setJuniper_user(requestDto.getBody().get("data").get("user"));

		response = APIRepository.addAPIConnectionDetails(connDto);
		if (response.toLowerCase().contains("success")) {
			return response;
			// status="Success";
			// message="Connection added successfully: "+connection_name;
		} else {
			status = "Connection Failed";
			message = response;
			return status + " " + message;
		}

	}

	/**
	 * @return String
	 */
	public String updAPIConnection(String x) throws Exception {
		// Parse json to Dto Object
		String testConnStatus = "";
		String status = "";
		String message = "";
		String response = "";

		JSONObject jsonObject = new JSONObject(x);
		String connection_id = jsonObject.getJSONObject("body").getJSONObject("data").get("conn").toString();
		String connection_name = jsonObject.getJSONObject("body").getJSONObject("data").get("connection_name")
				.toString();
		String connection_type = jsonObject.getJSONObject("body").getJSONObject("data").get("connection_type")
				.toString();
		String source_System = jsonObject.getJSONObject("body").getJSONObject("data").get("system").toString();
		String api_url = jsonObject.getJSONObject("body").getJSONObject("data").get("api_url").toString();
		String api_user_name = jsonObject.getJSONObject("body").getJSONObject("data").get("api_user_name").toString();
		String api_password = jsonObject.getJSONObject("body").getJSONObject("data").get("api_user_password")
				.toString();
		String auth_drive_path = jsonObject.getJSONObject("body").getJSONObject("data").get("auth_drive_path")
				.toString();
		String auth_file_path = jsonObject.getJSONObject("body").getJSONObject("data").get("auth_file_path").toString();
		String auth_file_name = jsonObject.getJSONObject("body").getJSONObject("data").get("auth_file_name").toString();
		String project_name = jsonObject.getJSONObject("body").getJSONObject("data").get("project").toString();
		String user = jsonObject.getJSONObject("body").getJSONObject("data").get("user").toString();
		ConnectionDTO connDto = new ConnectionDTO();

		connDto.setConnection_id(Integer.parseInt(connection_id));
		connDto.setConnection_name(connection_name);
		connDto.setConnection_type(connection_type);
		connDto.setSource_System(source_System);
		connDto.setApi_Url(api_url);
		connDto.setApi_user_name(api_user_name);
		// encrypt code
		connDto.setApi_password(api_password);
		connDto.setAuth_drive_path(auth_drive_path);
		connDto.setAuth_file_path(auth_file_path);
		connDto.setAuth_file_name(auth_file_name);
		connDto.setProject_name(project_name);
		connDto.setUpdated_By(user);
		connDto.setUpdated_Date(new Date());
		// connDto.setProject(requestDto.getBody().get("data").get("project"));
		// connDto.setJuniper_user(requestDto.getBody().get("data").get("user"));

		response = APIRepository.updateAPIConnectionDetails(connDto);
		if (response.toLowerCase().contains("success")) {
			return response;
			// status="Success";
			// message="Connection added successfully: "+connection_name;
		} else {
			status = "Connection Failed";
			message = response;
			return status + " " + message;
		}

	}

	@Override
	public String testAPIConnection(@Valid String x) throws Exception {
		return null;

	}

	@Override
	public String delAPIConnection(@Valid String x) throws Exception {

		/*
		 * // Parse json to Dto Object String testConnStatus=""; String status = "";
		 * String message = ""; String response="";
		 * 
		 * JSONObject jsonObject= new JSONObject(x); String connection_id =
		 * jsonObject.getJSONObject("body").getJSONObject("data").get("conn").toString()
		 * ; String connection_name =
		 * jsonObject.getJSONObject("body").getJSONObject("data").get("connection_name")
		 * .toString();
		 * 
		 * response = APIRepository.deleteAPIConnectionDetails(connection_name);
		 * if(response.toLowerCase().contains("success")){ return response;
		 * //status="Success";
		 * //message="Connection added successfully: "+connection_name; } else {
		 * status="Connection Failed"; message=response; return status+" "+message; }
		 */
		return null;
	}

	@Override
	public String getExtType(int src_sys_id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtType1(String src_unique_name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
