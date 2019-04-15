package com.iig.gcp.extraction.service;

import java.util.ArrayList;

import javax.validation.Valid;

import com.iig.gcp.extraction.dto.ConnectionDTO;

public interface ExtractionService {

	public String invokeRest(String json, String url) throws UnsupportedOperationException, Exception;

	public String getExtType(int src_sys_id) throws Exception;

	public String getExtType1(String src_unique_name) throws Exception;

	public ArrayList<String> getSystem(String project) throws Exception;

	public String getSystemName(int system) throws Exception;

	public ArrayList<ConnectionDTO> getConnectionsAPI(String src_val, String project_id) throws Exception;

	public ConnectionDTO getConnections2API(@Valid String src_val, @Valid int conn, String attribute) throws Exception;

	public String addAPIConnection(@Valid String x) throws Exception;

	public String updAPIConnection(@Valid String x) throws Exception;

	public String testAPIConnection(@Valid String x) throws Exception;

	public String delAPIConnection(@Valid String x) throws Exception;
}
