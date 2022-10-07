<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Edit IPPMS User Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
      
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="${appContext}/dataTables/ext/scroller/css/scroller.dataTables.min.css" type="text/css">

<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>

</head>

<body class="main">
	<table class="main" width="74%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

					
					<tr>
						<td>
								<div class="title">Edit User: <c:out value="${epmUser.firstName}" />&nbsp;<c:out value="${epmUser.lastName}" /></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="epmUser">
				<spring:hasBindErrors name="epmUser">
				<div id="topOfPageBoxedErrorMessage" style="display:block;">
								
					 <ul>
						<c:forEach var="errMsgObj" items="${errors.allErrors}">
  								<li>
     							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
  								</li>
						</c:forEach>
					</ul>
     							 
				</div>
				</spring:hasBindErrors>
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">User Information</td>
					</tr>
					<tr>
						<td class="activeTD" align="center">
						   <div class="panel-group" id="accordion">
					            	 
		            	 	 <div class="panel panel-danger">
							    <div class="panel-heading">
							      <h3 class="panel-title">
							        <a data-toggle="collapse" data-parent="#accordion" href="#collapse1" style="font-size: 16px;">
							        Configure User's Basic Information*</a>
							      </h3>
							    </div>
							    <div id="collapse1" class="panel-collapse collapse in">
							      <div class="panel-body">
										      
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align=right width="35%" nowrap>
									<span class="required"><b>User Name :</b></span></td>
									<td width="25%">
										&nbsp;<c:out value="${epmUser.userName}" />
									</td>
					            </tr>
					           	<tr>
									<td align=right nowrap><span class="required">
									<b>First Name :</b></span></td>
									<td width="25%">
										&nbsp;<c:out value="${epmUser.firstName}" />
									</td>
								</tr>
					            <tr>
					              <td align=right nowrap><span class="required"><b>Last Name :</b></span></td>
					              <td width="25%">
										&nbsp;<c:out value="${epmUser.lastName}" />
								   </td>
					              
					            </tr>
					            <c:choose>
					            <c:when test="${roleBean.privilegedUser}">
					             <tr>
                                  <td align=right nowrap><span class="required"><b>Email :</b></span></td>
                                  <td width="25%"><form:input path="email" size="35" maxlength="50"/> </td>
                                 </tr>
					            </c:when>
					            <c:otherwise>
					             <tr>
                                  <td align=right nowrap><span class="required"><b>Email :</b></span></td>
                                  <td width="25%"> &nbsp;<c:out value="${epmUser.email}" /> </td>
                                 </tr>
					            </c:otherwise>
					            </c:choose>

					            <tr>
					              <td align=right width="35%" nowrap><span class="required"><b>Old Role :</b></span></td>
					              <td>   
					                &nbsp;<c:out value="${epmUser.user.oldRoleName}" /></td>
					              
					            </tr>
					            
					             <tr>
					              <td align=right width="15%" nowrap><span class="required"><b>New Role* :</b></span></td>
					              <td align=left width="85%">   
					                <form:select path="roleId" id="role">
					                <form:option value="0">&lt;Assign Role&gt;</form:option>
					                <c:forEach items="${roles}" var="role">
					                	<form:option value="${role.id}" >${role.name}</form:option>
					                </c:forEach>
					                </form:select>
					              </td>
					              
					            </tr>
                                <tr>
										<td align=right nowrap><b>Allow Weekend Login</b></td>
										<td width="25%"><form:checkbox path="allowWeekendLogin" /></td>
									</tr>
					            <c:if test="${roleBean.sysUser}">
									<tr>
										<td align=right nowrap><b>System User</b></td>
										<td width="25%"><form:checkbox path="sysUser" /></td>
									</tr>
								</c:if>

					        </table>

					         </div>
							    </div>
							  </div>
							  
								  <div class="panel panel-danger">
								    <div class="panel-heading">
								      <h3 class="panel-title">
								        
								        Configure User's Accessible Links*
								      </h3>
								    </div>
								    
								      <div class="panel-body">
								      
							            	<table id="menuTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
												<thead>
													<tr class="role">
														<th class="fivePercentWidth"><input type="checkbox" id="selectAll"/></th>
														<th class="twentyPercentWidth"><spring:message code="userForm.permission.table.header.name"/></th>
														<th class="thirtyFivePercentWidth"><spring:message code="userForm.permission.table.header.desc"/></th>
														<th class="twentyPercentWidth"><spring:message code="userForm.permission.table.header.category"/></th>
														<th class="twentyPercentWidth"><spring:message code="userForm.permission.table.header.parent"/></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${epmUser.userMenu.menuLinks}" var="menuLink" varStatus="currIndex">
														<tr>
															<td>
																<form:checkbox path="userMenu.menuLinks[${currIndex.index}].selected" 
																		cssClass="menuChkBx" id="_${menuLink.id}" onchange="checkParentMenuLink(this, '${menuLink.id}');"/>
																	<input type="hidden" value="${menuLink.parentMenuLink.id}" class="menu-link-hidden-input-${menuLink.parentMenuLink.id}"  id="${menuLink.id}"/>
															</td>
															<td><c:out value="${menuLink.name}" /></td>
															<td><c:out value="${menuLink.description}" /></td>
															<td><c:out value="${menuLink.menuLinkCategory.name}" /></td>
															<td>
																<c:if test="${menuLink.hasParentLink}">
                                                                    <c:out value="${menuLink.parentMenuLink.name}" />
                                                                </c:if>
															</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
							            </div>
								    
								  </div>
								  
							</div> 
							          					
						</td>
					</tr>
					<tr>
						
						   <c:choose>
						   	<c:when test="${saved}">
						   		<td class="buttonRow" align="right" >
						   		<input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
						   		</td>
						   	</c:when>
						   	<c:otherwise>
						   		<td class="formButtonRow alignLeft">
									<input type="submit" name="_save" value="<spring:message code="form.button.save"/>" class="form-btn"/>
                  					<input type="submit" name="_cancel" value="<spring:message code="form.button.cancel"/>" class="form-btn"/>
								</td>
						   	</c:otherwise>
						   </c:choose>
							
						
					</tr>
				</table>
				</form:form>
			</td>
		</tr>
		</table>
		</td>
		</tr>
		
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script src="${appContext}/dataTables/ext/scroller/js/dataTables.scroller.min.js"></script>
	
	<script type="text/javascript">
		$(function() {
			$("#selectAll").change(function(e) {
				$(".menuChkBx").prop("checked", (this.checked));
			});

			$("#menuTable").DataTable({
				"order" : [ [ 1, "asc" ] ],
				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
				//also properties higher up, take precedence over those below
				"columnDefs":[
					{"targets": 0, "orderable" : false, "searchable" : false }
					//{"targets": [0, 1], "orderable" : false }
				],
				"scroller" : true,
				"scrollY" : 800,
				"bLengthChange": false
				
			});

			$("#role").change(function(e) {
				$("input.menuChkBx").prop("checked", false);
				
				if (this.value != '0') {
					var value = this.value;
					var userId = '${_userId}';
					$.ajax({
						url: APP_CONTEXT + "/menu/getMenuLinkIdsForRoleAndOrUser.do?roleId=" + value + "&userId="+ userId,
						type : 'GET',
						dataType : 'json',
						async : false,
						statusCode : {
							200 : function(data, status, jqxhr) {
								if (data && data.length > 0) {
									for (var i = 0; i < data.length; i++) {
										if(document.getElementById("_" + data[i]) != null)
											document.getElementById("_" + data[i]).checked = true;
									}
								}
							}
						}
					});
				}
			});
			
		});

		function checkParentMenuLink(element, hiddenInputFieldId) {
		    var parentMenuLinkId = jQuery("#"+hiddenInputFieldId).val();
		 
		  //check if any child link is checked, if not uncheck the parent link
		  if( parentMenuLinkId != "" ){
			  document.getElementById("_" + parentMenuLinkId).checked = false;
		    var parentMenuLinks = jQuery( '.menu-link-hidden-input-'+parentMenuLinkId ).get();
		    for( var i = 0; i < parentMenuLinks.length; i++ ) {
		    	var currMenuLink = document.getElementById("_" + parentMenuLinks[i].id).checked;
		    	if(currMenuLink){
		    		document.getElementById("_" + parentMenuLinkId).checked = true;
		    	}
			}
		  }

		  //if current child link is checked, please ensure parent link is checked
		  if( element.checked && parentMenuLinkId != "" ){
			  document.getElementById("_" + parentMenuLinkId).checked = true;
		  }
		    
		}
		
	</script>
	
	<script type="text/javascript">
		function handleUserTypeSelection(element) {
			var checked = element.checked;
			var iPPMSElements = jQuery( '.ippms-user-element' ).get();
			var iPPMSElements1 = jQuery( '.ippms-user-element1' ).get();
			
			var mdaElements = jQuery( '.mda-user-element' ).get();
			var disableMda = true;
			var disableIPPMS = true;
			
			if( element.id == 'mda-user-radio' ) {
				if(checked) disableMda = false;
			}
			else if( element.id == 'ippms-user-radio' ) {
				if(checked) disableIPPMS = false;
			}

			for( var i = 0; i < mdaElements.length; i++ ) {
				mdaElements[i].disabled = disableMda;
			}
			//IPPMS elements
			for( var i = 0; i < iPPMSElements.length; i++ ) {
				iPPMSElements[i].disabled = disableIPPMS;
			}
			for( var i = 0; i < iPPMSElements1.length; i++ ) {
				iPPMSElements1[i].disabled = disableIPPMS;
			}
		}


		function updateTextArea(element, hiddenInputFieldId) {
		    var mdaTypeName = jQuery("#"+hiddenInputFieldId).val();

		    if( element.checked ) {
		    	mdaTypeName =  $('#mdaTextArea').val() + mdaTypeName + "\n";
			    $('#mdaTextArea').val( mdaTypeName );
		    }else {
                jQuery('#mdaTextArea').val('');
			    jQuery( ".ippms-user-element:checked" ).each(function(){ jQuery(this).change(); })
		    }
		}
			

		function handleAllMdaTypeSelection(allMdaTypeCheckbox, className) {
			var checkboxes = jQuery("."+className).get();
			var checkBoxChecked = allMdaTypeCheckbox.checked;

			for(var i = 0; i < checkboxes.length; i++) {
				currentCheckBox = checkboxes[ i ];

				//if the check box is checked....funny english
				if( checkBoxChecked ) {
					currentCheckBox.checked = true;
				}else {
					currentCheckBox.checked = false;
				}
			}

			jQuery('#mdaTextArea').val('');
			jQuery( ".ippms-user-element:checked" ).each(function(){ jQuery(this).change(); })
		}

				
	</script>
	
</body>
</html>