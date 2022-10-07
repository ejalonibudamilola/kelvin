<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
	<title>Ogun I.P.P.M.S Login</title>
<link href="css/newLogin_ossg.css" rel="stylesheet" type="text/css"></link>
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
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

<body  onload="document.formLogin.uName.focus()">

<div class="register1">&nbsp;</div>

 <form name="formLogin" action="<c:url value='login'/>" method="POST" id="login">
	<div class="placeGreeBorder">
    	<!-- div to hold the image header -->
   <div class="headerImage"></div>
   
   
    
    <c:if test="${(not empty param.login_error) and (SPRING_SECURITY_LAST_EXCEPTION != null) }">
							<tr>
								<td align="center">
									<div class="errorDisp">
							            <font color="red"> Your login attempt was not successful, try again.
											<br />
											<br />
											Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />.
										</font>
									</div>
								</td>
							</tr>
						</c:if>
                        
    <div class="tableForm">
    	<table width="100%">
    	

  <tr>
    <td width="23%"><label for="login_username">Username:</label></td>
    <td width="77%"><input id="uName" name="username" title="Please provide your username"
    value="<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>" /></td>
  </tr>
</table>


    </div>			

    <div class="tableForm">
    	<table width="100%">
  <tr id="hideText" style="display:''">
    <td width="23%"><label for="login_password">Password:</label></td>
    <td width="77%">
    <input type="password" name="password" title="Password is required"  id="login_password" />
												<br /><font color="red"><i class="mini">case sensitive</i></font>
    </td>
  </tr>

  <tr>
  	<td width="23%"><label for="">&nbsp;</label></td>
    
    <td width="77%"><input type="checkbox" id="checkbox" title="Show Password" />Show Password
    <script type="text/javascript" src="scripts/jquery.min.js" charset="utf-8"></script>
     <script type="text/javascript" src="scripts/jquery.showpassword-1.0.js" charset="utf-8"></script>
      <script type="text/javascript">
      	$(document).ready(function() {
      		$('#login_password').showPassword('#checkbox');
      	});
      </script>
    </td>
  </tr>

</table>

    </div>			
      			
    <div class="submit">
        <button type="submit"></button>      
    </div>
    <div style="padding-left:31%">
           <c:if test="${(not empty param.login_error) and (SPRING_SECURITY_LAST_EXCEPTION != null) }">
             <c:if test="${SPRING_SECURITY_LAST_EXCEPTION.message eq 'Bad credentials'}">
    			<a href="${appContext}/forgetPassword.gnl"><b><i><font color="blue">Forgot Password?</font></i></b></a>
             </c:if>
      </c:if>
    </div>
    <p class="back">Please login with your OGSG IPPMS Account details</p>
  
   <h4>
	<gnl:copyright startYear="2020" />
</h4>
   </div>
     <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>	
</body>
</html>