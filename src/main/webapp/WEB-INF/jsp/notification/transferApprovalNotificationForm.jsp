<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Transfer Request Notification Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="<c:url value="/styles/epayroll.css"/>" type="text/css"/>
 <script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>


<body class="main">
 	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">


					<tr>
						<td colspan="2">
								<div class="title">Transfer <c:out value="${miniBean.parentObject.displayNameWivTitlePrefixed}" />&nbsp;[ <c:out value="${miniBean.parentObject.employeeId}" /> ]</div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						 <br/><br/>
				<form:form modelAttribute="miniBean">

                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH"><b><c:out value="${miniBean.parentObject.displayName}"/>'s Transfer information</b></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="100%" border="0" cellspacing="0" cellpadding="2">
								<tr>
						            <td align="right" width="25%"><b>Transfer From :&nbsp;</b></td>
						              	<td><c:out value="${miniBean.oldMda}"/>
						               </td>
								</tr>
						          <tr>
						            <td align="right" width="25%"><b>Transfer To :&nbsp;</b></td>

									     <td width="25%">
						                   <c:out value="${miniBean.newMda}"/>
									     </td>

						            </tr>
						          <tr>
						             <td align="right" width="25%"><b>Initiated By :&nbsp;</b></td>

									      <td width="25%">
						                   <c:out value="${miniBean.initiator.actualUserName}"/>
									      </td>

						    		 <td>&nbsp;</td>
						          </tr>
						          <tr>
						            <td align="right" width="25%"><b>Transfer Initiation Date :&nbsp;</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.initiatedDateStr}"/>
									       </td>

						          </tr>
                                   <tr>
						            <td align="right" width="25%"><b>Transfer Effective Date :&nbsp;</b></td>

									       <td width="25%">
						                     <c:out value="${miniBean.transferDateStr}"/>
									       </td>

						          </tr>
                                   <c:if test="${miniBean.approval}">
						          	<tr>
						               <td align="right" width="25%">Approval Memo :&nbsp;</td>

									       <td width="25%">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" />
									       </td>

						          </tr>

						          </c:if>
						          <c:if test="${miniBean.rejection}">
						          	<tr>
						               <td align="right" width="25%">Rejection Reason* :&nbsp;</td>

									       <td width="25%">
						                     <form:textarea path="rejectionReason" rows="5" cols="35" />
									       </td>

						          </tr>

						          </c:if>

							</table>
						</td>
					</tr>
                    <tr>
						<td class="buttonRow" align="right" >
							 <input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">
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
