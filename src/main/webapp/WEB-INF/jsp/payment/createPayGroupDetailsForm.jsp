<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Pay Group File Upload Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

</head>

<body class="main">

<form:form modelAttribute="fileUploadBean" action="uploadPayGroupDetails.do" method="post" enctype="multipart/form-data">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
								<div class="title">Upload Excel Schedule for <c:out value="${fileUploadBean.name}"/></div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
			
			<div id="topOfPageBoxedErrorMessage" style="display:${fileUploadBean.displayErrors}">
								 <spring:hasBindErrors name="fileUploadBean">
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
						<td class="activeTH">Upload Excel Schedule for: <b><c:out value="${fileUploadBean.name}"/></b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td width="25%" align="left">Pay Group Name<font color="red"><b>*</b></font></td>
										<td><c:out value="${fileUploadBean.salaryType.name}"></c:out></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pay Group Description<font color="red"><b>*</b></font></td>
										<td><c:out value="${fileUploadBean.salaryType.description}"></c:out></td>
									</tr>
									<tr>
										<td width="25%" align="left">Pension Rule<font color="red"><b>*</b></font></td>
										<td><c:out value="${fileUploadBean.salaryType.pensionRule}"></c:out></td>
									</tr>
						          <tr>
										<td align="right" valign="top" nowrap>
										<span class="text2" style="font-weight: bold;">Upload Pay Group Details Excel File* :</span>
										<img src="images/pixel.png" border="0" alt="" width="1" height="1" hspace="0" vspace="0"></td>
										<td nowrap><input type="file" name="file" size="60"/></td>
									</tr>
						           
							</table>							
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						  	 <input type="image" name="submit" value="ok" title="Create Pay Group" class="" src="images/ok_h.png">
						     <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
							    
							</td>
					</tr>
				</table>
				
			</td>
	        </tr>
	        </table>
	       		 <br>
			    <br>
	        <div class="reportBottomPrintLink">
	        		 
	        		   <a href="${appContext}/downloadObjectTemplate.do?ot=${fileUploadBean.objectTypeInd}">
						Download Pay Group Details Template </a>
						<br />	
	        		
	        		
							
			    </div>
			    <br>
			    <br>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>
</body>
</html>
