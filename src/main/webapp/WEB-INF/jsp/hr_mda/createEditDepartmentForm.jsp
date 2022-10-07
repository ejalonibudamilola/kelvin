<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Create/Edit Department  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>
  <style>
        #deptTbl thead tr th{
            font-size:8pt !important;
        }
  </style>
<body class="main">
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">${displayTitle}</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="miniBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
				<table border="0" cellspacing="0" cellpadding="3" width="60%" align="left" >
					<tr align="left">
						<td class="activeTH">Department Information</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td align="right" width="35%">
										&nbsp;
									</td>
								</tr>
								 
								<tr>
									<td align="right" nowrap>
										<span class="required">Department Name*</span>&nbsp;
									</td>
									<td><form:input path="name" size="50" maxlength="120"/></td>								
								</tr>
								<tr>
									<td align="right" nowrap>
										<span class="required">Description*</span>&nbsp;
									</td>
									<td><form:input path="description" size="50" maxlength="120"/></td>								
								</tr>
								 <tr>
                                 	 <td align="right" nowrap><span class="required">Default Department</span>&nbsp;</td>
                                 	 <td><form:checkbox path="defaultIndBind" /></td>
                                  </tr>
								 <c:if test="${clientDept.editMode}">
								 
								 <tr> 
								     <td align="right" nowrap><span class="required">Global Change*</span></td>
                  						<td><spring:bind path="globalChange" >
                 						    <input type="hidden" name="_<c:out value="${status.expression}"/>">
                 							<input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if> title="If checked, Department Name and/or Description changes to this Department will be propagated to ALL MDA's having similar 'Department'." />
     										</spring:bind>
     			  						 </td>
                  						 
               					 </tr>
								
								 
								 </c:if>
					        </table>							
						</td>
					</tr>
					
					</table>
					 
					<tr>
						<td class="buttonRow" align="right" >
						
						   <c:if test="${miniBean.editMode}">
							  &nbsp;<input type="image" name="submit" value="ok" title="Update" class="" src="images/update.png"> 
							</c:if>
							<c:if test="${not miniBean.editMode}">
							  &nbsp; <input type="image" name="submit" value="ok" title="Update" class="" src="images/create_h.png"> 
							</c:if>
							<c:if test="${displayList.size() eq 0}">
						      &nbsp; <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
						      </c:if>
						</td>
					</tr>
					 </table>
					 
					<c:if test="${displayList.size() gt 0}">
					<table width="100%">
					 <tr>
                                        <td>
                                            &nbsp;                                    
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Click to Edit Department</p>
											<display:table name="dispBean" id="deptTbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/createClientDeptForm.do">
											    <display:column property="name" title="Department Name" media="html" href="${appContext}/createClientDeptForm.do" paramId="did" paramProperty="id"></display:column>
											    <display:column property="description" title="Description"></display:column>
											    <display:column property="createdBy.actualUserName" title="Creator"></display:column>
											    <display:column property="creationDate" title="Created Date"></display:column>
											    <display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
											    <display:setProperty name="paging.banner.placement" value="bottom" />
										    </display:table>
										</td>
                                    </tr>
                                    
                                    </table>

                                   &nbsp; <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                                       <p>
                                         </c:if>
                                      </td>
                                    </tr>
                                    
                                  
				<tr>
			      <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		     </tr>
			 
			</table>
	</form:form>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script>
                $(function() {
                   $("#deptTbl").DataTable({
                      "order" : [ [ 1, "asc" ] ],
                      //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                      //also properties higher up, take precedence over those below
                      "columnDefs":[
                         {"targets": [0], "orderable" : false}
                      ]
                   });
                });
    </script>
</body>
</html>