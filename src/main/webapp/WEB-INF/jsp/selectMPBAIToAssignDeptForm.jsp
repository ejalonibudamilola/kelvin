<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Select M D A or P To Assign Department  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">

</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title"> Select <c:out value="${miniBean.name}"></c:out> </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				<form:form modelAttribute="miniBean">
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
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">Select <c:out value="${miniBean.name}"></c:out> to assign a Department</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td>&nbsp;</td>
					            </tr>
					            
					            <tr>
					              <td align=right><span class="required"><c:out value="${miniBean.name}"></c:out></span></td>
					              <td align="left">
					              	<form:select path="id">
										<form:option value="0"> &lt;Please Select&gt;</form:option>
										 <c:forEach items="${miniBean.parentTypeList}" var="minList">
					                	 <form:option value="${minList.id}">${minList.name}</form:option>
					               		</c:forEach>
									</form:select>   
					                </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					                               
									<td>&nbsp;</td>
					            </tr>
								<tr>
									<td>&nbsp;</td>
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

