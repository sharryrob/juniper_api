<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fieldset class="fs">
	<div class="form-group row">
		<div class="col-sm-6">
			<label>Connection Name *</label> <input type="text"
				class="form-control" id="connection_name" name="connection_name"
				placeholder="Connection Name" value="${conn_val.connection_name}" readonly="readonly">
		</div>
		<div class="col-sm-6">
			<label>Connection Type *</label> <input type="text"
				class="form-control" id="connection_type" name="connection_type"
				value="${src_val}" readonly="readonly">
		</div>
	</div>
		<div class="form-group">
										
											<label>Api Url </label>
									<input type="text" 
												class="form-control" id="api_url"
												name="api_url" placeholder="API URL" value="${conn_val.api_Url}">
										</div>
										
									
									
									<div class="form-group row">
										<div class="col-sm-6">
											<label>Api User Name  *</label>
									<input type="text" 
												class="form-control" id="api_user_name"
												name="api_user_name" placeholder="API UserName"  value="${conn_val.api_user_name}">
										</div>
									
									
											<div class="col-sm-6">
											<label>Api Password  *</label>
									<input type="text" 
												class="form-control" id="api_user_password"
												name="api_user_password" placeholder="API Password" value="${conn_val.api_password}">
										</div>
										
										</div>				
										
											<div class="form-group row">
										<div class="col-sm-6">
											<label>Drive Path  *</label>
									<input type="text" 
												class="form-control" id="auth_drive_path"
												name="auth_drive_path" value=" Juniper Drive Path" value="${conn_val.auth_drive_path}" readonly="readonly">
										</div>
									
											<div class="col-sm-6">
											<label>Data Path  *</label>
									<input type="text" 
												class="form-control" id="auth_file_path"
												name="auth_file_path" placeholder="Authorization File Path" value="${conn_val.auth_file_path}">
										</div>
										
										</div>	
										
										
											<div class="form-group row">
										<div class="col-sm-6">
											<label>Authorization File Name </label>
									<input type="text" 
												class="form-control" id="auth_file_name"
												name="auth_file_name" placeholder="Authorization File Name" value="${conn_val.auth_file_name}">
										</div>
									
										
										<div class="col-sm-6">
											<label>System *</label> <input type="text" class="form-control"
												id="system" name="system" placeholder="System"
												value="${conn_val.source_System}" readonly="readonly">
										</div>
										
										</div>	
		
	</div>
</fieldset>
<button onclick="return jsonconstruct('upd');"
	class="btn btn-rounded btn-gradient-info mr-2">Update</button>
<button onclick="return jsonconstruct('del');"
	class="btn btn-rounded btn-gradient-info mr-2">Delete</button>