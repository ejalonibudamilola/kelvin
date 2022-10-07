<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> e-Payroll Login  </title>
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>


<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="80%" border="0" align="center">
										
					<p><p/>
					<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
					</tr>
						<tr>
							<td><h4> Your session has expired. <a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a></td>
							
						</tr>
						<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
						</tr>
					<p/>
					
				</table>
			  </td>
			  </tr>
				<tr>
					<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
				</tr>
		
	</table>
</body>
</html>