<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Mass Reassignment Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
</head>

<body class="main">
    <form:form modelAttribute="miniBean">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
    		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
    		<tr>
    			<td>
    				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

    					<tr>
    						<td>
    							<div class="title">Mass Reassignment</div>
    						</td>
    					</tr>
    					<tr>
    						<td valign="top" class="mainBody" id="mainbody">
    						    <font color="red"><b>* = required</b></font><br/><br/>

    				            <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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
                                <table border="0" cellspacing="0" cellpadding="3" width="60%" align="left" >
                                    <tr align="left">
                                        <td class="activeTH">Mass Reassignment</td>
                                    </tr>
                                    <tr>
                                        <td class="activeTD">
                                            <table border="0" cellspacing="1" cellpadding="2">
                                                <tr>
                                                    <td align="right" nowrap>
                                                        <span class="required">Name*</span>&nbsp;
                                                    </td>
                                                    <td align="left"><form:input path="name" size="25" maxlength="30"/></td>
                                                </tr>
                                                <tr>
                                                    <td align="right" nowrap>
                                                        <span class="required">From Pay Group</span>&nbsp;
                                                    </td>
                                                    <td align="left">
                                                        <form:select path="fromSalaryType.id" style="width:200px">
                                                            <form:option value="-1">&lt;Select&gt;</form:option>
                                                            <c:forEach items="${salaryTypeList}" var="hList">
                                                                <form:option value="${hList.id}" title="${hList.name}">${hList.name}</form:option>
                                                            </c:forEach>
                                                        </form:select>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="right" nowrap>
                                                        <span class="required">To Pay Group</span>&nbsp;
                                                    </td>
                                                    <td align="left">
                                                        <form:select path="toSalaryType.id" style="width:200px">
                                                            <form:option value="-1">&lt;Select&gt;</form:option>
                                                            <c:forEach items="${salaryTypeList}" var="hList">
                                                                <form:option value="${hList.id}" title="${hList.name}">${hList.name}</form:option>
                                                            </c:forEach>
                                                        </form:select>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
    				        </td>
    				    </tr>
    				</table>
    				&nbsp; <input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
    				&nbsp; <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
    			</td>
    		</tr>
    		<tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
    	</table>
    </form:form>
</body>