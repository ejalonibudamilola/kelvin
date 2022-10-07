<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New User Setup Form  </title>
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
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
					<tr>
						<td>
								<div class="title">Setup New User Profile for IPPMS</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				    <form:form modelAttribute="epmUser">
                        <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
                                     <spring:hasBindErrors name="epmUser">
                                         <ul>
                                            <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                <li>
                                                <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
                                                </li>
                                            </c:forEach>
                                            </ul>
                                     </spring:hasBindErrors>
                        </div>
                        <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
                            <tr align="left">
                                <td class="activeTH">New User Information</td>
                            </tr>
                            <tr>
                                <td class="activeTD">

                                    <table>
                                               <tr>
                                                <td>

                                                 <div class="panel-group" id="accordion">

                                                     <div class="panel panel-danger">
                                                        <div class="panel-heading">
                                                          <h4 class="panel-title">
                                                            <a data-toggle="collapse" data-parent="#accordion" href="#collapse1" style="font-size: 16px;">
                                                            Configure User's Basic Information*</a>
                                                          </h4>
                                                        </div>
                                                        <div id="collapse1" class="panel-collapse collapse in">
                                                          <div class="panel-body">
                                                            <table border="0" cellspacing="0" cellpadding="2">
                                                                <tr>
                                                                    <td align=right width="35%" nowrap>
                                                                    <span class="required">User Name*</span></td>
                                                                    <td width="25%">
                                                                        <form:input path="userName" />
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td align=right nowrap><span class="required">
                                                                    First Name*</span></td>
                                                                    <td width="25%">
                                                                        <form:input path="firstName"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                  <td align=right nowrap><span class="required">Last Name*</span></td>
                                                                  <td width="25%">
                                                                        <form:input path="lastName"/>
                                                                   </td>

                                                                </tr>
                                                                 <tr>
                                                                    <td align=right nowrap><span class="required">Email Address*</span></td>
                                                                     <td width="25%">
                                                                     <form:input path="email"/>
                                                                 </td>

                                                                 </tr>
                                                                <tr>
                                                                  <td align=right width="35%" nowrap><span class="required">Designation*</span></td>
                                                                  <td>
                                                                    <form:select path="roleId" id="role">
                                                                    <form:option value="0">&lt;Assign Role&gt;</form:option>
                                                                    <c:forEach items="${roles}" var="role">
                                                                        <form:option value="${role.id}" >${role.name}</form:option>
                                                                    </c:forEach>
                                                                    </form:select>
                                                                  </td>

                                                                </tr>

                                                                <c:if test="${roleBean.sysUser}">
                                                                    <tr>
                                                                        <td align=right nowrap><b>System User</b></td>
                                                                        <td width="25%"><form:checkbox path="sysUser" /></td>
                                                                    </tr>
                                                                </c:if>
                                                                <br></br>
                                                            <tr>
                                                                 <td align="right" ></td>
                                                                 <td>
                                                                 <c:choose>
                                                                 <c:when test="${saved}">
                                                                 <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                                                 </c:when>
                                                                 <c:otherwise>
                                                                   <input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">&nbsp;
                                                                     <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
                                                                 </c:otherwise>
                                                                 </c:choose>
                                                                 </td>
                                                             </tr>

                                                                </table>
                                                          </div>
                                                        </div>
                                                      </div>


                                                      <div class="panel panel-danger">
                                                        <div class="panel-heading">
                                                          <h4 class="panel-title">
                                                            Configure User's Accessible Links*
                                                          </h4>
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
                                    </table>
						        </td>
					        </tr>
                            <tr>
                                <td class="buttonRow" align="right" >
                                    <c:choose>
                                    <c:when test="${saved}">
                                        <input type="image" name="_cancel" value="cancel" title="Close" src="images/close.png">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">&nbsp;
                                        <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
                                    </c:otherwise>
                                </c:choose>
                                </td>
                            </tr>
				        </table>
				    </form:form>
				</table>
			</td>
		</tr>
        <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
        </tr>
	</table>
	
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script src="${appContext}/dataTables/ext/scroller/js/dataTables.scroller.min.js"></script>
	
	<script type="text/javascript"><!--
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
	
	
</body>
</html>