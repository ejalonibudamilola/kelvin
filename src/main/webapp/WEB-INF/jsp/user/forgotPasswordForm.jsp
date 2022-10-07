<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
	<title>I.P.P.M.S Forgot Password</title>
<link href="css/newLogin_ossg.css" rel="stylesheet" type="text/css"></link>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
</head>
<body>
<script src="<c:url value="/scripts/jquery-1.12.0.min.js"/>"></script>
<script type="text/javascript">
     	var APP_CONTEXT = '${pageContext.request.contextPath}';

     	//function displaying success/failure messages after certain actions
     	function displayPageNotification(msgContent) {
     		if (msgContent) {
     			$(document).ready(function(){
     				var notifDiv = $("#notification-msg");
     				notifDiv.html(msgContent);
     				notifDiv.hide();
     				notifDiv.slideDown('slow');
     			    setTimeout(function(){
     			    	notifDiv.slideUp('slow', function(){
     			             $(this).remove();
     			        });
     			    }, 10000);//10 seconds for message to disappear
     			});
     		}
     	}
     </script>

   <div id="parent-msg">
   	  <!-- Notification Message -->
   	  <div id="notification-msg" class="undisplay"></div>
   </div>
    <div class="register1">&nbsp;</div>
    <form:form modelAttribute="epmUser" id="login">

    	<div class="placeGreeBorder">
            <div class="headerImage"></div>
            <spring:hasBindErrors name="epmUser">
            	<div class="errorDisp">
            		<ul>
            			<c:forEach var="errMsgObj" items="${errors.allErrors}">
            			   <li>
            			     <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
            		       </li>
            		    </c:forEach>
            	    </ul>
            	</div>
            </spring:hasBindErrors>
            <div class="tableForm">
        	    <table width="100%">
                    <tbody>
                        <tr>
                            <td width="23%"><label for="login_username">Username:</label></td>
                            <td width="77%"><input id="uName" name="username" title="Please provide your username" value=""></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="forgotPassword">
               <button id="resetPassword" type="submit"></button>
            </div>
            <div style="padding-left:38%">
               <a href="${pageContext.request.contextPath}/securedLoginForm">Back to Log in</a>
            </div>
            <c:if test="${saved}">
                <script type="text/javascript">displayPageNotification('${savedMsg}');</script>
            </c:if>
            <h4>
            	©2020-2021 GNL Systems Ltd.
            </h4>
        </div>
    </form:form>
    <div class="spin"></div>
<script>
     $("#resetPassword").click(function(e){
        $(".spin").show();
     });
</script>
</body>
</html>