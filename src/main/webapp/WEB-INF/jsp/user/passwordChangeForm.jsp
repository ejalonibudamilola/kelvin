<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
	<title>Password Change Form</title>
<link href="css/newLogin_ossg.css" rel="stylesheet" type="text/css"></link>
<script type="text/javascript">
<!--
function toggleField(box,passwordId,textId) {
	//alert("got into go fxn");
	if(box.checked){
		document.getElementById(passwordId).style.display = 'none';
	 	document.getElementById(textId).style.display = '';	
	}else{
	    document.getElementById(textId).style.display = 'none';	
	    document.getElementById(passwordId).style.display = '';
	}
}
//-->


</script>
</head>

<body  onload="document.formLogin.nPass.focus()">

    <div class="register1">&nbsp;</div>

    <form:form modelAttribute="loginBean" id="login">
        <c:choose>
            <c:when test="${not caseShow}">

                <div class="placeGreeBorder">
                    <!-- div to hold the image header -->
                    <div class="headerImage"></div>

                    <spring:hasBindErrors name="loginBean">
                                    <tr>
                                        <td align="center">
                                            <div class="errorDisp">
                                                 <ul style="list-style-type:none">
                                                     <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                         <li style="font-size: 10px;">
                                                             <font color="red">
                                                                 <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
                                                             </font>
                                                         </li>
                                                     </c:forEach>
                                                 </ul>
                                             </div>
                                        </td>
                                    </tr>
                    </spring:hasBindErrors>

                    <div class="tableForm">
                        <table width="90%">
                            <tr>
                                <td width="40%"><label for="login_username">Username:</label></td>
                                <td width="60%"><form:input path="userName" disabled="true" /></td>
                            </tr>

                            <tr id="hideText" style="display:''">
                                <td width="40%" nowrap><label for="login_password">New Password:</label></td>
                                <td width="60%">
                                    <form:password path="newPassword" class="login_password" id="nPass"/>&nbsp;<font color="red"><i>case sensitive</i></font>
                                </td>
                            </tr>
                            <tr id="hideText" style="display:''">
                                <td width="60%" nowrap><label for="login_password">Confirm Password:</label></td>
                                <td width="40%">
                                    <form:password path="confirmNewPassword" class="login_password" id="cPass"/>&nbsp;<font color="red"><i>case sensitive</i></font>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div style="margin-top:-7px" class="tableForm">
                        <table width="100%">
                            <tr>
                                <td width="43%"><label for="">&nbsp;</label></td>
                                <td width="57%">
                                    <input type="checkbox" id="checkbox" title="Show Password" />
                                    Show Password
                                    <script type="text/javascript" src="scripts/jquery.min.js" charset="utf-8"></script>
                                    <script type="text/javascript" src="scripts/jquery.showpassword-1.0.js" charset="utf-8"></script>
                                    <script type="text/javascript">
                                        $(document).ready(function() {
                                            $('.login_password').showPassword('#checkbox');
                                        });
                                    </script>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div class="changePwd">
                        <button type="submit"></button>
                    </div>

                    <p class="back">Please change your Ogun <c:out value="${roleBean.businessName}"/> IPPMS Account Password</p>

                   <h4>
                        <gnl:copyright startYear="2020" />
                   </h4>
                </div>
            </c:when>
            <c:otherwise>

                <div class="placeGreeBorder">
                    <!-- div to hold the image header -->
                    <div class="headerImage"></div>

                    <spring:hasBindErrors name="loginBean">
                                    <tr>
                                        <td align="center">
                                            <div class="errorDisp">
                                                 <ul style="list-style-type:none">
                                                     <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                         <li style="font-size: 10px;">
                                                             <font color="red">
                                                                 <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
                                                             </font>
                                                         </li>
                                                     </c:forEach>
                                                 </ul>
                                             </div>
                                        </td>
                                    </tr>
                    </spring:hasBindErrors>

                    <div class="tableForm">
                        <p style="text-align:center"><a href="securedLoginForm">Click here to log in</a></p>
                        <table style="pointer-events: none; opacity: 0.4;" width="90%">
                            <tr>
                                <td width="40%"><label for="login_username">Username:</label></td>
                                <td width="60%"><form:input path="userName" disabled="true" readonly = "true"/></td>
                            </tr>

                            <tr id="hideText" style="display:''">
                                <td width="40%" nowrap><label for="login_password">New Password:</label></td>
                                <td width="60%">
                                    <form:password path="newPassword" id="nPass" readonly = "true"/>&nbsp;<font color="red"><i>case sensitive</i></font>
                                </td>
                            </tr>
                            <tr id="hideText" style="display:''">
                                <td width="60%" nowrap><label for="login_password">Confirm Password:</label></td>
                                <td width="40%">
                                    <form:password path="confirmNewPassword" id="cPass" readonly = "true"/>&nbsp;<font color="red"><i>case sensitive</i></font>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <p class="back">Please change your Ogun <c:out value="${roleBean.businessName}"/> IPPMS Account Password</p>

                   <h4>
                        <gnl:copyright startYear="2020" />
                   </h4>
                </div>
            </c:otherwise>
        </c:choose>
    </form:form>
</body>
</html>