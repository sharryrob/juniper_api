package com.iig.gcp.constants;

public class OracleConstants {

	// Oracle Driver Details
	public final static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";

	public final static String ORACLE_IP_PORT_SID = "35.227.48.30:1521:ORCL";

	public final static String ORACLE_DB_NAME = "juniper_admin";
	public final static String ORACLE_JDBC_URL = "jdbc:oracle:thin:@" + ORACLE_IP_PORT_SID + "";
	public static final String ORACLE_PASSWORD = "Infy123##";

	public static final String masterKeyLocation = "master_key.txt";

	public static final String GETSEQUENCEID = "Select  DATA_DEFAULT from USER_TAB_COLUMNS where TABLE_NAME = '${tableName}' and COLUMN_NAME='${columnName}'";
	// API
	public static final String CONNECTIONTABLE = "JUNIPER_EXT_API_CONN_MASTER";
	public static final String CONNECTIONTABLEKEY = "API_CONN_SEQUENCE";
	public static final String GETLASTROWID = "SELECT ${id}.currval from dual";
	public static final String PROJECTTABLE = "JUNIPER_PROJECT_MASTER";
	public static final String SYSTEMTABLE = "JUNIPER_SYSTEM_MASTER";
	public static final String KEYTABLE = "JUNIPER_EXT_KEY_MASTER";

}
