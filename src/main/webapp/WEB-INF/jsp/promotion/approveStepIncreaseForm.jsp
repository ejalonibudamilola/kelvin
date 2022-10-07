<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="dt"%>

<html>
<head>
<title>Approve/Delete Yearly Salary Step Increment</title>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="Stylesheet" href="styles/skye.css" type="text/css" media="screen">
 <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
</head>

<body class="main">

<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

	<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
			
				<td colspan="2">
				<div class="title" >Approve/Delete Step Increment</div>
				</td>
			
			</tr>
			<tr >
				<form:form modelAttribute="approveStepBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="approveStepBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
				</div>
				<c:set value="${approveStepBean}" var="dispBean" scope="request"/>
				<td valign="top" class="mainBody" id="mainBody">Please &quot;Approve&quot; if you wish to make this increment permanent<br>
				or &quot;Undo All&quot; if u wish to cancel the Yearly Step Increase.
				<p><br>
				</p>
				
					
					<table border="0" width="80%" cellpadding="5">
					<c:choose>
					<c:when test="${approveStepBean.listSize gt 0}"> 
						<tr>
							<td align="left">
							<table width="90%" cellspacing="1" cellpadding="3">
							   
							    <tr> 
                                    <dt:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/approveStepIncrease.do">
									<dt:column property="employeeId" title="${roleBean.staffTitle}"></dt:column>
									<dt:column property="name" title="${roleBean.staffTypeName} Name"></dt:column>
									<dt:column property="mdaInfo.name" title="${roleBean.mdaTitle}" ></dt:column>
									<dt:column property="salaryScaleName" title="Pay Group" ></dt:column>
									<dt:column property="oldLevelAndStepStr" title="From Level & Step" ></dt:column>
									<dt:column property="newLevelAndStepStr" title="To Level & Step" ></dt:column>
									<dt:setProperty name="paging.banner.placement" value="bottom" />
									</dt:table>
								</tr>
							  
							</table>
							</td>
						</tr>
						<tr>
							<td align="right">
							<div id="processingMsg" style="color: red"><!-- placeholder for the please wait message --></div>
							</td>
						</tr>
						</c:when>
						<c:otherwise>
						  <c:if test="${not approveStepBean.confirmation}">
							<tr>
							<td align="left"><H3><font color="red">No Step Increment found to Approve.</font></H3>
							  </td>
							</tr>
							</c:if>
						</c:otherwise>
						</c:choose>
						<tr>
							
							<td class="buttonRow" align="right">
							
							    <c:if test="${approveStepBean.listSize gt 0}"> 
							      <input type="image" name="submit" value="Approve Salary Increase" title="Approve Salary Increase" class='' src='images/approve.png'>
								</c:if>
								<input type="image" name="_cancel" value="close" title="Come back later"   src='images/close.png' onclick="">
								
							</td>
							
						</tr>
						
					</table>
					<c:if test="${approveStepBean.listSize gt 0}"> <p/>
					If you have created these <b>&quot;Yearly Salary Step Increments&quot;</b> in error, you can <a href='${appContext}/approveStepIncrease.do?action=undo'>delete
				all now</a><br/></c:if>
				<br/>
				</td>
				</form:form> 
				
				
			</tr>
			

            <tr>
               <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
              </tr>
			</table>

			</td>
			</tr>

		</table>
</body>

</html>

