<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="../cdg_header.jsp" />
<script>
	function jsonconstruct(val) {
	
		
		var connection_name = document.getElementById("connection_name").value;
		var connection_type = document.getElementById("connection_type").value;
		var api_url = document.getElementById("api_url").value;
		var api_user_name = document.getElementById("api_user_name").value;
		var api_user_password = document.getElementById("api_user_password").value;
		var system = document.getElementById("system").value;		
		
		var auth_drive_path = document.getElementById("auth_drive_path").value;
		var auth_file_path = document.getElementById("auth_file_path").value;
		var auth_file_name = document.getElementById("auth_file_name").value;
		
		var errors = [];

		if (!checkLength(connection_name)) {
			errors[errors.length] = "Connection Name";
		}
		
		if (!checkLength(system)) {
			errors[errors.length] = " Source System";
		}
		
		/*if (!checkLength(api_url)) { 
			errors[errors.length] = "Api Url";
		}*/
					
		if (!checkLength(api_user_name)) {
			errors[errors.length] = "Api User Name";
		}
		
		if (!checkLength(api_user_password)) {
			errors[errors.length] = "Api Password";
		}
		
		if (!checkLength(auth_file_path)) {
			errors[errors.length] = "Data Path";
		}
		
		if (!checkLength(auth_drive_path)) {
			errors[errors.length] = "Drive Path";
		}
		
		/*if (!checkLength(auth_file_name)) {
			errors[errors.length] = "Authorization File Name";
		}*/

		if (errors.length > 0) {
			reportErrors(errors);			
			return false;
		}
		/*if(checkLength(api_url) && !checkUrl(api_url)){
			alert("Api Url is incorrect. Please enter valid Url");
			return false;
		}*/
		
		$("#loading").show();
		
		var data = {};
		document.getElementById('button_type').value = val;
		$(".form-control").serializeArray().map(function(x) {
			data[x.name] = x.value;
		});
		var x = '{"header":{},"body":{"data":' + JSON.stringify(data) + '}}';
		document.getElementById('x').value = x;
		//console.log(x);
		//alert(x);
		document.getElementById('ConnectionDetails').submit();
	}
	$(document)
			.ready(
					function() {
						$("#conn")
								.change(
										function() {
											$("#loading").show();
											var conn = $(this).val();
											var src_val = document.getElementById("src_val").value;
											$.post('${pageContext.request.contextPath}/extraction/ConnectionDetailsEditUnix', {
												conn : conn,
												src_val : src_val
											}, function(data) {
												$("#loading").hide();
												$('#cud').html(data)
											});
										});
						$("#success-alert").hide();
			              $("#success-alert").fadeTo(10000,10).slideUp(2000, function(){
			              });   
			       $("#error-alert").hide();
			              $("#error-alert").fadeTo(10000,10).slideUp(2000, function(){
			               });
					});

	function funccheck(val) {
		if (val == 'create') {
			//window.location.reload();
			window.location.href="${pageContext.request.contextPath}/extraction/ConnectionDetailsUnix";
		} else {
			document.getElementById('connfunc').style.display = "block";
			document.getElementById('cud').innerHTML = "";
		}
	}
</script>
<div class="main-panel">
	<div class="content-wrapper">
		<div class="row">
			<div class="col-12 grid-margin stretch-card">
				<div class="card">
					<div class="card-body">
						<h4 class="card-title">API Connection</h4>
						<p class="card-description">Connection Details</p>
						<%
               if(request.getAttribute("successString") != null) {
               %>
            <div class="alert alert-success" id="success-alert">
               <button type="button" class="close" data-dismiss="alert">x</button>
               ${successString}
            </div>
            <%
               }
               %>
            <%
               if(request.getAttribute("errorString") != null) {
               %>
            <div class="alert alert-danger" id="error-alert">
               <button type="button" class="close" data-dismiss="alert">x</button>
               ${errorString}
            </div>
            <%
               }
               %>
						<form class="forms-sample" id="ConnectionDetails"
							name="ConnectionDetails" method="POST"
							action="${pageContext.request.contextPath}/extraction/ConnectionDetails1"
							enctype="application/json">
							<input type="hidden" name="x" id="x" value=""> <input
								type="hidden" name="button_type" id="button_type" value="">
							<input type="hidden" name="src_val" id="src_val"
								value="${src_val}"> <input type="hidden" name="path"
								id="path" class="form-control" value=""> <input
								type="hidden" name="project" id="project" class="form-control"
								value="${project}"> <input type="hidden" name="user"
								id="user" class="form-control" value="${user}">

							<div class="form-group row">
								<label class="col-sm-3 col-form-label">Connection</label>
								<div class="col-sm-4">
									<div class="form-check form-check-info">
										<label class="form-check-label"> <input type="radio"
											class="form-check-input" name="radio" id="radio1"
											checked="checked" value="create"
											onclick="funccheck(this.value)"> Create
										</label>
									</div>
								</div>
								<div class="col-sm-4">
									<div class="form-check form-check-info">
										<label class="form-check-label"> <input type="radio"
											class="form-check-input" name="radio" id="radio2"
											value="edit" onclick="funccheck(this.value)">
											Edit/View
										</label>
									</div>
								</div>
							</div>

							<div class="form-group" id="connfunc" style="display: none;">
								<label>Select Connection</label> <select name="conn" id="conn"
									class="form-control">
									<option value="" selected disabled>Select Connection
										...</option>
									<c:forEach items="${conn_val}" var="conn_val">
										<option value="${conn_val.connection_id}">${conn_val.connection_name}</option>
									</c:forEach>
								</select>
							</div>
							<div id="cud">
								<fieldset class="fs">
									<div class="form-group row">
										<div class="col-sm-6">
											<label>Connection Name *</label> <input type="text"
												class="form-control" id="connection_name"
												name="connection_name" placeholder="Connection Name">
										</div>
										<div class="col-sm-6">
											<label>Connection Type *</label> <input type="text"
												class="form-control" id="connection_type"
												name="connection_type" value="API"
												readonly="readonly">
										</div>
									</div>
							
											
										<div class="form-group">
										
											<label>Api Url </label>
									<input type="text" 
												class="form-control" id="api_url"
												name="api_url" placeholder="API URL">
										</div>
										
									
									
									<div class="form-group row">
										<div class="col-sm-6">
											<label>Api User Name  *</label>
									<input type="text" 
												class="form-control" id="api_user_name"
												name="api_user_name" placeholder="API ClientID / UserName">
										</div>
									
									
											<div class="col-sm-6">
											<label>Api Password  *</label>
									<input type="text" 
												class="form-control" id="api_user_password"
												name="api_user_password" placeholder="API ClientSecret / Password">
										</div>
										
										</div>				
										
											<div class="form-group row">
										<div class="col-sm-6">
											<label>Drive Path  *</label>
									<input type="text" 
												class="form-control" id="auth_drive_path"
												name="auth_drive_path" value="/data/juniper/in" readonly="readonly">
										</div>
									
											<div class="col-sm-6">
											<label>Data Path  *</label>
									<input type="text" 
												class="form-control" id="auth_file_path"
												name="auth_file_path" placeholder="Authorization File Path">
										</div>
										
										</div>	
										
										
											<div class="form-group row">
										<div class="col-sm-6">
											<label>Authorization File Name </label>
									<input type="text" 
												class="form-control" id="auth_file_name"
												name="auth_file_name" placeholder="File Name">
										</div>
									
										
										<div class="col-sm-6">
											<label>Source System *</label> <select name="system"
												id="system" class="form-control">
												<option value="" selected disabled>Select System...</option>
												<c:forEach items="${system}" var="system">
													<option value="${system}">${system}</option>
												</c:forEach>
											</select>
										</div>
										
										</div>						
									
								
									
									
									
											
								</fieldset>
							
										<%-- <button onclick="return jsonconstruct('test');"
									class="btn btn-rounded btn-gradient-info mr-2">
									Test Connection</button>--%>
									
									<button onclick="return jsonconstruct('add');"
									class="btn btn-rounded btn-gradient-info mr-2">
									Save & Submit</button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../cdg_footer.jsp" />