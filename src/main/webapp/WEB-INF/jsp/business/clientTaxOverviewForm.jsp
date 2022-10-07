<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
<head>
<title>Tax Setup Overview</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
</head>

<body class="main">
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
							<div class="title">Tax Setup Overview</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						To change or add information to a section, click the title of the section.
							<form:form modelAttribute="taxBean">
							 <table width="100%" border="0" cellspacing="3" cellpadding="3">
								<tr>
									<td valign="top">
										<h2>
											<A HREF='${appContext}/editGenTaxForm.do?cid=${taxBean.genTaxInfo.id}'>General Tax Information</A>
										</h2>
										<table border="0" cellspacing="0" cellpadding="2">
											<tr> 
												<td align="left" valign="top"><span class="label">Filing Name:</span></td>
												<td align="left" valign="top"><c:out value="${taxBean.genTaxInfo.name}"/></td>
											</tr>
											<tr> 
												<td valign="top" align=left><span class="label">Filing Address:</span></td>
												<td align="left" valign="top"><c:out value="${taxBean.filingAddress}"/>
												
												<br><c:out value="${taxBean.filingAddressCityStateZip}"/>
												</td>
											</tr>
										</table>
										<p>
										<h2>
											<A HREF='${appContext}/fedTaxForm.do?cid=${taxBean.companyTaxInfo.id}'>Federal Tax Information</A>
										</h2>
										<table border="0" cellspacing="0" cellpadding="2">
											<tr> 
												<td align="left" valign="top"><span class="label">EIN:</span></td>
												<td align="left" valign="top" colspan="1"><c:out value="${taxBean.companyTaxInfo.ein}"/></td>
											</tr>
											<tr> 
												<td align="left" valign="top"><span class="label">Filing &amp; Deposit Requirements:</span></td>
												<td align="left" valign="top">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" valign="top" colspan="2">
												&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${taxBean.filingDepositReq}"/>
												</td>
											</tr>
										</table>
									</td>
									<td>
										&nbsp;
									</td>
									<td valign="top">
										<h2>
										<A HREF='${appContext}/editStateTaxForm.do?cid=${taxBean.companyStateTaxInfo.id}'>State Tax Information</A>
										</h2>
										<table border="0" cellspacing="0" cellpadding="2">
											<tr>
												<td align="left" valign="top"><span class="label" nowrap>Registration/Incorporation No.</span>:</td>
												<td align="left" valign="top"><c:out value="${taxBean.companyStateTaxInfo.name}"/></td>
											</tr>
											<tr> 
												<td align="left" valign="top"><span class="label">Deposit Schedule:</span></td>
												<td align="left" valign="top">&nbsp;</td>
											</tr>	
											<tr>
												<td align="left" valign="top" colspan="2">
													&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${taxBean.stateDepositSchedule}"/>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
							</form:form>
							<P>
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
