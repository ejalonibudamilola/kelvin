<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Pay Client <c:out value="${roleBean.staffTypeName}"/>s
        </title>
		<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
		<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
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
                                    Select Business Client</div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                                <form:form modelAttribute="miniBean">
									<div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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
                                    <table border="0" cellspacing="0" cellpadding="0" width="90%" id="pcform0">
                                        <tr>
                                            <td>
                                                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                     <tr align="left">
                                                       <td class="activeTH"> View/Print Approved Business Client Paychecks</td><br/><br/>
                                                    </tr>
                                                     <tr>
                                                        <td class="activeTD">
                                                            <input type="hidden" name="schedule3" value="0">
                                                            <table width="100%" cellspacing="0" border="0" cellpadding="3" height="80%">
                                                              <tr>
                                                                    <td>
                                                                        <table width="100%" cellpadding="0" cellspacing="0">
                                                                        	<tr>
                                                                              	<td>&nbsp;</td>
                                                                              </tr>
                                                                              <tr>
                                                                              	<td>&nbsp;</td>
                                                                              </tr>
                                                                            <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Select Business Client*</span>&nbsp;
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:select path="id">
                                                                                		<form:option value="0">&lt; Please Select &gt;</form:option>
																						<c:forEach items="${miniBean.busClientList}" var="busClients">
																						<form:option value="${busClients.id}">${busClients.name}</form:option>
																						</c:forEach>
																					</form:select>
                                                                              </td>
                                                                              </tr>
                                                                              <tr>
                                                                              	<td>&nbsp;</td>
                                                                              </tr>
                                                                              <tr>
                                                                              	<td>&nbsp;</td>
                                                                              </tr>
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
                                                <br/>
                                                <br/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="buttonRow" align="right">
                                                <input type="image" name='go' value="Go"  alt='Select' class='' src='images/go_h.png' onclick="">
												<input type="image" name='_done' value="Done" alt='Done' class='' src='images/done_h.png' onclick="">
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
