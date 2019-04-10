package com.iig.gcp.extraction.dto;

import java.util.Date;

public class ConnectionDTO {

	private int connection_id;
	private String connection_name;
	private String connection_type;
	private String api_Url;
	private String api_user_name;
	private String api_password;
	private String auth_drive_path;
	private String auth_file_path;
	private String auth_file_name;
	private String source_System;
	private String project_name;
	private String created_By;
	private String updated_By;
	private Date created_Date;
	private Date updated_Date;

	/**
	 * @return Date
	 */
	public Date getCreated_Date() {
		return created_Date;
	}

	/**
	 * @param created_Date
	 */
	public void setCreated_Date(Date created_Date) {
		this.created_Date = created_Date;
	}

	/**
	 * @return Date
	 */
	public Date getUpdated_Date() {
		return updated_Date;
	}

	/**
	 * @param updated_Date
	 */
	public void setUpdated_Date(Date updated_Date) {
		this.updated_Date = updated_Date;
	}

	/**
	 * @return String
	 */
	public String getCreated_By() {
		return created_By;
	}

	/**
	 * @param created_By
	 */
	public void setCreated_By(String created_By) {
		this.created_By = created_By;
	}

	/**
	 * @return String
	 */
	public String getUpdated_By() {
		return updated_By;
	}

	/**
	 * @param updated_By
	 */
	public void setUpdated_By(String updated_By) {
		this.updated_By = updated_By;
	}

	/**
	 * @return String
	 */
	public String getProject_name() {
		return project_name;
	}

	/**
	 * @param project_name
	 */
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	private byte[] encrypt;

	/**
	 * @return String
	 */
	public byte[] getEncrypt() {
		return encrypt;
	}

	/**
	 * @param encrypt
	 */
	public void setEncrypt(byte[] encrypt) {
		this.encrypt = encrypt;
	}

	/**
	 * @return String
	 */
	public String getSource_System() {
		return source_System;
	}

	/**
	 * @param system
	 */
	public void setSource_System(String system) {
		this.source_System = system;
	}

	/**
	 * @return String
	 */
	public int getConnection_id() {
		return connection_id;
	}

	/**
	 * @param connection_id
	 */
	public void setConnection_id(int connection_id) {
		this.connection_id = connection_id;
	}

	/**
	 * @return String
	 */
	public String getConnection_name() {
		return connection_name;
	}

	/**
	 * @param connection_name
	 */
	public void setConnection_name(String connection_name) {
		this.connection_name = connection_name;
	}

	/**
	 * @return String
	 */
	public String getConnection_type() {
		return connection_type;
	}

	/**
	 * @param connection_type
	 */
	public void setConnection_type(String connection_type) {
		this.connection_type = connection_type;
	}

	/**
	 * @return String
	 */
	public String getApi_Url() {
		return api_Url;
	}

	/**
	 * @param api_Url
	 */
	public void setApi_Url(String api_Url) {
		this.api_Url = api_Url;
	}

	/**
	 * @return String
	 */
	public String getApi_user_name() {
		return api_user_name;
	}

	/**
	 * @param api_user_name
	 */
	public void setApi_user_name(String api_user_name) {
		this.api_user_name = api_user_name;
	}

	/**
	 * @return String
	 */
	public String getApi_password() {
		return api_password;
	}

	/**
	 * @param api_password
	 */
	public void setApi_password(String api_password) {
		this.api_password = api_password;
	}

	/**
	 * @return String
	 */
	public String getAuth_drive_path() {
		return auth_drive_path;
	}

	/**
	 * @param auth_drive_path
	 */
	public void setAuth_drive_path(String auth_drive_path) {
		this.auth_drive_path = auth_drive_path;
	}

	/**
	 * @return String
	 */
	public String getAuth_file_path() {
		return auth_file_path;
	}

	/**
	 * @param auth_file_path
	 */
	public void setAuth_file_path(String auth_file_path) {
		this.auth_file_path = auth_file_path;
	}

	/**
	 * @return String
	 */
	public String getAuth_file_name() {
		return auth_file_name;
	}

	/**
	 * @param auth_file_name
	 */
	public void setAuth_file_name(String auth_file_name) {
		this.auth_file_name = auth_file_name;
	}

}
