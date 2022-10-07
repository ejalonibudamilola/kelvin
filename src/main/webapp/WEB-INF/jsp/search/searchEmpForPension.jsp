<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New Pensioner Search Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="styles/jquery-ui-1.8.18.custom.css" type="text/css" media ="screen">
</head>


<body class="main" onload="document.searchForm.empId.focus()" >

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">



					<tr>
						<td colspan="2">
								<div class="title"> <c:out value="${empMiniBean.displayTitle}"/> </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="empMiniBean" name="searchForm">
						<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="empMiniBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
						</div>
				<table border="0" cellspacing="0" cellpadding="3" width="45%" align="left" >
				    <div>
                      <a href="${appContext}/createNewPensioner.do">Add Pensioner Manually.</a>
                	</div>
					<tr align="left">
						<td class="activeTH">Search Criteria</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2" width="55%" align="left">
                               <c:if test="${roleBean.localGovtPension}">
							     <tr>
                                 	 <td align="left" width="35%">Service Organization*</td>
                                 	 <td width="25%" align="left">
                                 	 <form:select path="parentId">
                                 	 <form:option value="0">&lt;Select&gt;</form:option>
                                 	 <c:forEach items="${parentClientList}" var="parentList">
                                 	     <form:option value="${parentList.parentBusinessClient.id}">${parentList.parentBusinessClient.name}</form:option>
                                 	      </c:forEach>
                                 	  </form:select>
                                    </td>
                                 </tr>
                               </c:if>
								<tr>
									<td align="left" width="45%"><span class="required">Terminated Employee ID*</span></td>
									<td align="left" width="40%">
									   <form:input path="employeeId" id="empId" size="15"/>
					            </tr>

							</table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
						</td>
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

</body>
</html>

