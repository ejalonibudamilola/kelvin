<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<meta http-equiv="Refresh" content="7">
<title> Generating Payroll  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<script type="text/javascript" src="scripts/jquery-1.4.1.min.js"></script>
<script type="text/javascript" src="scripts/ePMAjax.js"></script>
</head>


<body class="main">
    <form:form modelAttribute="progressBean">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center">
					
					
						<tr>
								<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
							
						</tr>
						<tr>
							<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
							
						</tr>
						<tr>
							<td align="center"><b><font color="blue">Generating Futuristic Payroll.....please wait!</font></b></td>
						</tr>
						<tr>
							<td align="center">processed&nbsp;&nbsp;<b><font color="blue"> <c:out value="${progressBean.currentCount}"/> of <c:out value="${progressBean.totalElements}"/></font></b>&nbsp;&nbsp;records</td>
						</tr>
						<tr>
							<td align="center" valign="top">
							<div class="progress" style="width: 300px">
							    <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="${progressBean.percentage}" aria-valuemin="0" aria-valuemax="100" style="width:${progressBean.percentage}%">
							       <c:out value="${progressBean.percentage}%"></c:out>
							    </div>
							  </div>
							 
							</td>
						</tr>
						<tr>
							<td align="center"><b><font color="green">Estimated Time to Completion.....<c:out value="${progressBean.timeRemaining}"></c:out></font></b></td>
						</tr>
						<tr>
							<td align="center" valign="top"><input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/cancel_h.png"></td>
						</tr>
						
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							
							<tr>
							<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
						</tr>						
						
						<tr>
							<td></td>
						</tr>
					
					
					
				</table>
			  </td>
			  </tr>
				<tr>
				
					<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
				</tr>
		
	</table>
	</form:form>
</body>
</html>