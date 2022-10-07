<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Upload Form  </title>
<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
</head>

<body class="main">

<form:form modelAttribute="fileUploadBean" action="uploadSubmit.do" method="post" enctype="multipart/form-data">
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
			
			<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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

								<c:if test="${fileUploadBean.leaveBonus}">
								 
					            <tr>
					              <td align="right"><span class="required"><c:out value="${roleBean.mdaTitle}"/>*</span></td>
					              <td>   
					                <form:select path="mdaInstId">
									<form:option value="0">&lt;Please select &gt;</form:option>
								    <c:forEach items="${fileUploadBean.mdaInfoList}" var="mList">
										<form:option value="${mList.id}" >${mList.name}</form:option>
									</c:forEach>
								   </form:select>
					              </td>
					               
					            </tr>
					            </c:if>
						          <tr>
										<td align="right" valign="top" nowrap>
										<span class="text2" style="font-weight: bold;">Upload Excel File* :</span>
										<img src="images/pixel.png" border="0" alt="" width="1" height="1" hspace="0" vspace="0"></td>
										<td nowrap><input type="file" name="file" size="60"/></td>
									</tr>
									<!--
						          <c:if test="${fileUploadBean.bankBranch}">
						           <tr>
                                  	 <td align="right"><span class="required">Active Banks*</span></td>
                                  	 <td>
                                  	 <form:select path="bankId">
                                  	 <form:option value="0">&lt;Please select &gt;</form:option>
                                  	  <c:forEach items="${fileUploadBean.bankInfoList}" var="bList">
                                  		 <form:option value="${bList.id}">${bList.name}</form:option>
                                  	 </c:forEach>
                                  	 </form:select>
                                  	 </td>

                                  	 </tr>
						          <tr>
					                <td align="right" valign="top">Mode*</td>
									<td>
									  <form:radiobutton path="bankUpdateInd" value="1"/>Update Account No. Only<br>
									  <form:radiobutton path="bankUpdateInd" value="2"/>Require Bank Branch<br>
									  </td>
								</tr>
						          
						          </c:if>
						          -->

						             <c:if test="${fileUploadBean.salaryInfo}">
                                  		<tr>
                                          <td align="right" width="35%" nowrap>
                                           <span class="required">Pay Group</span></td>
                                           <td width="25%">
                                           <form:select path="salaryTypeId">
                                           <option value="0">&lt;&nbsp;Select&nbsp;&gt;</option>
                                           <c:forEach items="${salaryTypeList}" var="salaryTypes">
                                           <form:option value="${salaryTypes.id}" title="${salaryTypes.description}">${salaryTypes.name}</form:option>
                                           </c:forEach>
                                             </form:select>
                                           </td>

                                          </tr>

                                  		</c:if>
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
				
			</td>
	        </tr>
	        </table>
	       		 <br>
			    <br>
	        <div class="reportBottomPrintLink">
	        		 
	        		   <a href="${appContext}/downloadObjectTemplate.do?ot=${fileUploadBean.objectTypeInd}">
						Download Template </a>
						<c:if test="${not fileUploadBean.leaveBonus && not fileUploadBean.salaryInfo && not fileUploadBean.stepIncrement}">
							<span class="tabseparator">|</span>
							<a href="${appContext}/downloadTypeTemplate.do?ot=${fileUploadBean.objectTypeInd}">
							Download Model Data Types</a>
						</c:if>
						<br />	
	        		
	        		
							
			    </div>
			    <br>
			    <br>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	</table>
	</form:form>
</body>
</html>
