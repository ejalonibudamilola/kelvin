<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Reset Client Password
        </title>
		<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
		<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
		<script language="JavaScript">
			<!--
			function go(which,destUrl) {
 				 n = which.value;
 			if(n > 0){
  				 var url = destUrl+"?pid="+n;
  				location.href = url;
  			}
		}
			// -->
		</script>
    </head>
    <body class="main">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                        
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Reset Client Password</div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                             <p>
                                    Select the Client which you wish to reset password for after confirmation,<br>
									click the 'Reset Password' button that comes up. An auto-generated password<br>
									will be sent to the email addresses available for the company.
                                </p>* = Required<br>
                              
                                <form:form modelAttribute="busCert">
									<div id="topOfPageBoxedErrorMessage" style="display:${busCert.displayErrors}">
								 		<spring:hasBindErrors name="busCert">
         									 <ul>
            									<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  								<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            								</c:forEach>
         									</ul>
     							 		</spring:hasBindErrors>
									</div>
                                    <table border="0" cellspacing="0" cellpadding="0" width="90%" id="pcform0">
                                        <tr>
                                            <td>
                                                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                     <tr align="left">
                                                       <td class="activeTH"> Reset Password</td><br/><br/>
                                                    </tr>
                                                     <tr>
                                                        <td class="activeTD">
                                                            
                                                            <table width="100%" cellspacing="0" border="0" cellpadding="3">
                                                              <tr>
                                                                    <td>
                                                                        <table width="100%" cellpadding="0" cellspacing="0">
                                                                            <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Business Client Name*</span>&nbsp;
                                                                            	</td>
                                                                                	<td width="80%" nowrap>
                                                                                		<c:choose>
                                                                                		<c:when test="${busCert.reset}">
                                                                                			<form:input path="name" disabled="true"/>
                                                                                		</c:when>
                                                                                		<c:otherwise>
                                                                                		<select name="id" onChange="go(this,'${appContext}/resetPasswordForm.do')">
                                                                                			<option value="-1">&lt; Please Select &gt;</option>
																							<c:forEach items="${busClientList}" var="busList">
																							<option value="${busList.id}">${busList.name}</option>
																							</c:forEach>
																						</select>
																						</c:otherwise>
																						</c:choose>
                                                                             	 </td>
                                                                              </tr>
																			  <tr>
																			  	<td>
																				 &nbsp;		
                                                                               	</td>
																			  </tr>
																			   <tr>
																			  	<td>
																				 &nbsp;		
                                                                               	</td>
																			  </tr>
																			   <tr>
																			  	<td>
																				 &nbsp;		
                                                                               	</td>
																			  </tr>
                                                                            </table>
                                                                    </td>
                                                                </tr>
                                                                </table>
                                                       </td>
                                                    </tr>
                                                </table>
                                                <br/>
                                                <br/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="buttonRow" align="right">
                                                <c:choose>
											<c:when test="${busCert.reset}">
												<input type="image" name="submit" value="reset" alt="reset password" class="" src="images/resetpasswd.png">
												<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
							 				</c:when>
							 				<c:otherwise>
							 					<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
											</c:otherwise>
										</c:choose>
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
