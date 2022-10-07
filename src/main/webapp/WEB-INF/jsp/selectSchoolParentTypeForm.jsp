<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Select MDA  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>

</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					

					<tr>
						<td colspan="2">
								<div class="title"> Select Department Parent Type </div>
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
						<td class="activeTH">Make a selection</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">
								<tr>
									<td>&nbsp;</td>
					            </tr>
					            
					            <tr>
					              <td align=right><span class="required">Parent Type*</span></td>
					              <td align="left">
					              	<form:select path="id" onchange='loadSchools(this);'>
										<form:option value="-1"> &lt;Please Select&gt;</form:option>
										<form:option value="1">Agency</form:option>
										<form:option value="2">Board</form:option>
										<form:option value="3">Ministry</form:option>
										<form:option value="4">Parastatal</form:option>
					               	</form:select>   
					                </td>
					              <td align="left"></td>
					            </tr>
					            <tr>
					              <td align=center nowrap><span class="required">MDA*</span></td>
					              <td align="left">
					              	<form:select path="parentInstId" id="mdaSchool-control">
										<form:option value="0"> &lt;Select&gt;</form:option>
										 <c:forEach items="${objectList}" var="oList">
					                	 <form:option value="${oList.id}">${oList.name}</form:option>
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

