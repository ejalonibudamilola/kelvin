<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Permission Denied </title>
<link rel="stylesheet" href="<c:url value="/styles/omg.css"/>" type="text/css" />
<link rel="stylesheet" href="<c:url value="/styles/skye.css"/>" type="text/css" />
</head>


<body class="main">
    <form:form>
	<table class="main" width="74%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center">
					
					
						<tr>
								<td align="right">&nbsp;<br/></td>
								<td>&nbsp;</td>
							
						</tr>
						<tr>
							<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
							
						</tr>
						<tr>
							<td align="center">
								<font color="red">
									<b>PERMISSION DENIED!!!</b>
									<br />
									<br />
								</font>
							</td>
						</tr>
						<tr>
							<td align="center">
								<font color="red">
									<b>Some of the domain objects selected for the operation, are outside<br/>
									   your <b><i><font color="black">jurisdiction</font></i></b>.
									Please see your administrator for more information.</b>
								</font>
							</td>
						</tr>
						<tr>
							<td align="center" valign="top" >
								<br/>
								<br/>
								<br/>
								<a href='<c:url value="/determineDashBoard.do"/>' title='Home page'>
									Home Page
								</a>&nbsp;
								<br/>
							</td>
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