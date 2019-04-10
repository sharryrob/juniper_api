package com.iig.gcp.extraction.repository;

import com.iig.gcp.extraction.dto.ConnectionDTO;

public interface APIConnectionRepository {

	public String addAPIConnectionDetails(ConnectionDTO connDto);

	public String updateAPIConnectionDetails(ConnectionDTO connDto);

	public String decyptPassword(byte[] encrypted_key, byte[] encrypted_password) throws Exception;

	public String deleteAPIConnectionDetails(String connection_name) throws Exception;

}
