<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> LTG Details  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>

</head>
<body class="main">
<form:form modelAttribute="miniBean">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>

			<table  width="100%" align="center" border="0" cellpadding="0" cellspacing="0" height="550px">
			
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${miniBean.name}"></c:out> &nbsp; Details</div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			<table width="650" cellspacing="0" cellpadding="0">
				<tr>
					<td>
							&nbsp;
					</td>
				</tr>
			</table>
			
				
 					<c:set value="${miniBean}" var="dispBean" scope="request"/>
				<table width="100%" cellspacing="1" cellpadding="3">
				<tr>
							<td><a href='${appContext}/viewLtgSimulations.do'><i>Back to LTG Simulation(s) view</i></a></td>

						</tr>
						<tr>
							<td>&nbsp;</td>

						</tr>
						<tr>
						 <td valign="top">
						<display:table name="dispBean" class="register3" export="true" requestURI="${appContext}/viewLtgDetails.do" pagesize="${miniBean.pageSize}">
						<display:caption><c:out value="${miniBean.name}"></c:out> &nbsp; Details</display:caption>
						<display:setProperty name="export.rtf.filename" value="LtgInstructionDetails.rtf"/>
						<display:setProperty name="export.excel.filename" value="LtgInstructionDetails.xls"/>
						<display:column property="name" title="MDA" />
						<display:column property="noOfEmp" title="No. Of Employees" ></display:column>
						<display:column property="basicSalaryStr" title="Basic Salary" ></display:column>	
						<display:column property="ltgCostStr" title="LTG Cost" ></display:column>	
						<display:column property="netIncreaseStr" title="Net Increase" ></display:column>				
				</display:table>
				</td>
				</tr>
				<tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                   <td>
                      <a href='${appContext}/viewLtgSimulations.do'><i>Back to LTG Simulation(s) view</i></a>                                          
                  </td>
                </tr>
				
			</table>					
		
		<br/>
		<br/>
		</td>
		
		
		<!-- Here to put space beween this and any following divs -->
	</tr>
	
	
	</table>
	</td>
	</tr>
	<tr><%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
	</table>
	
</form:form>
</body>

</html>

