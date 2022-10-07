<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Assign Departments to <c:out value="${roleBean.mdaTitle}"/>s  </TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<script language="JavaScript">
 


function go(which,destUrl) {
  n = which.value;
  if (n == 0) {
    location.href = destUrl;
  }else if(n > 0){
  	var url = destUrl+n;
  	location.href = url;
  }
}
</script>

</head>

<body class="main">
    <form:form modelAttribute="miniBean">			
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
                    <tr>
                        <td colspan="2">
                            <div class="title"> Assign Department(s) to <c:out value="${roleBean.mdaTitle}"/> </div>
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" class="mainbody" id="mainbody">

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
                            <div id="topOfPageBoxedErrorMessage" style="display:${messageString}">
                                    <ul>
                                        <li>
                                            <c:out value="${displayMessage}"/>
                                        </li>

                                    </ul>

                            </div><br/><br/>
                                 Assign department(s)
                            <br/>
                            <br/>


                            <table class="formtable" border="0" cellspacing="0" cellpadding="3" width="75%" align="left" >
                                    <tr align="left">
                                        <td class="activeTH">Assign Departments to <c:out value="${roleBean.mdaTitle}"/></td>
                                    </tr>
                                    <tr>
                                        <td class="activeTD">
                                            <table width="75%" border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td width="2%" align="left" valign=bottom nowrap>Select <c:out value="${roleBean.mdaTitle}"/></td>
                                                    <td width="25%" align="left" >
                                                    <form:select path="mdaInfo.id" onchange="go(this,'${appContext}/assignDeptToMin.do?mid=')">
                                                            <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                                <c:forEach items="${mdaList}" var="mList">
                                                                    <form:option value="${mList.id}">${mList.name}</form:option>
                                                                </c:forEach>

                                                    </form:select>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td width="2%" align="left" valign="bottom" nowrap>Select Department</td>
                                                    <td width="25%" align="left" >
                                                        <form:select path="department.id" id="department-control" cssClass="branchControls">
                                                            <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                            <c:forEach items="${unAssignedDepts}" var="dList">
                                                               <form:option value="${dList.id}">${dList.name}</form:option>
                                                            </c:forEach>
                                                        </form:select>
                                                    </td>
                                                </tr>
                                                <!--
                                                <tr id="hiderow" style="display:none">
                                                    <td width="2%" align="left">Head of Department*</td>
                                                    <td width="25%" align="left"><form:input path="deptDirector" size="20" maxlength="100"/>
                                                </tr>
                                                -->
                                                <tr>
                                                    <td>&nbsp;</td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                            </table>
                        </td>
                    </tr>
		        </table>
			    <table class="formtable" border="0" cellspacing="0" cellpadding="3" width="75%" align="left" >
						<tr align="left">
							<td class="activeTH">Assigned Departments</td>
							
						</tr>
						<tr>
							<td id="firsttd_editdeduction_form" class="activeTD">
								<table width="95%" border="0" cellspacing="0" cellpadding="0">
		
								    <c:forEach items="${assignedDepts}" var="assignedDept">
									    <tr>
                                            <td width="33%">
                                                <c:out value="${assignedDept.name}"/>
                                            </td>
                                            <td align="left" nowrap>
                                                <a href="${appContext}/assignDeptToMin.do?pid=${assignedDept.mdaDeptMapId}&did=${assignedDept.id}" >Remove</a>&nbsp;
                                                <a href="${appContext}/deptEmpDetails.do?mid=${assignedDept.mdaDeptMapId}" target="_blank" onclick="popup(this.href, '${assignedDept.name}');return false;">Details....</a>&nbsp;
                                            </td>
								        </tr>
								    </c:forEach>
								</table>
							</td>
						</tr>
						<tr>
							<td class="buttonRow" align="right" >
									 
								<input type="image" name="submit" value="ok" title="Add Department" class="" src="images/add.png">
								<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							</td>
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
