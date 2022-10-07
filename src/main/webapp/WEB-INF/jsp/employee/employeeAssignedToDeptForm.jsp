<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/> assigned to Department</title>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>
  <style>
        #assignedDeptTbl thead tr th{
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
							<div class="navTopBannerAndSignOffLink">
								<span class="navTopSignOff">
									<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
								</span>
							</div>
						</td>
					</tr>
					
			<tr>
				<td colspan="2">	
					<div class="title"> <c:out value="${roleBean.staffTypeName}"/> list for <c:out value="${miniBean.departmentName}" /> </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
     				<div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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
				<br/>
					<br/>
					     List of <c:out value="${roleBean.staffTypeName}"/>(s) assigned to Department.
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="50%"><tr><td>
						<tr align="left">
							<td class="activeTH"><c:out value="${miniBean.mbapName}" /></td>
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="50%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="25%" align="left"><c:out value="${miniBean.mbapName}" /> Name :</td>
										<td width="25%" align="left"><c:out value="${miniBean.ministryName}"/></td>
									</tr>
									<tr>
										<td width="25%" align="left">Department Name :</td>
										<td width="25%" align="left"><c:out value="${miniBean.departmentName}"/></td>
									</tr>
									
									<tr>
										<td width="25%" align="left">Staff Strength</td>
										 <td><c:out value="${miniBean.noOfEmployees}"/>										
										</td>
									</tr>
									
									<tr>
										<td width="25%" align="left">
											&nbsp;											
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									
									</table>						
								
							</td>
						</tr>
						
					</table>
					 
					   
			           <table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
			           
			           
						<tr align="left">
						 
							<td><h5><c:out value="${roleBean.staffTypeName}"/>Details</h5></td>
							
						</tr>
						<tr>
							<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<display:table name="dispBean" id="assignedDeptTbl" class="display table" export="true" sort="page" defaultsort="1" requestURI="${appContext}/deptEmpDetails.do">
									<display:column property="employee.employeeId" title="${roleBean.staffTitle} " ></display:column>
									<display:column property="employee.displayName" title="Name" ></display:column>	
									<display:column property="employee.assignedToObject" title="${roleBean.mdaTitle}"></display:column>
									<display:column property="employee.salaryScale" title="Pay Group"></display:column>
									<display:column property="employee.levelAndStep" title="Level & Step"></display:column>
									<display:column property="birthDateStr" title="Birth Date"></display:column>
									<display:column property="hireDateStr" title="Hire Date"></display:column>
									<display:column property="expDateOfRetireStr" title="Exp. Retire Date"></display:column>
									<display:column property="yearsOfService" title="Years In Service"></display:column>
									<display:setProperty name="paging.banner.placement" value="bottom" />
									</display:table>
								</table>
								
								</td>
								</tr>
								
							
			</table>
			
				</td>
			</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>			
	
		</table>
		
				
	
			</td>
			</tr>
			
			</table>
		 
			
			 
			
			<!-- 
			<tr>
				<td class="buttonRow" align="right" >
					<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
				</td>
			</tr>
			 -->
</form:form>
<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
<script>
            $(function() {
               $("#assignedDeptTbl").DataTable({
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
