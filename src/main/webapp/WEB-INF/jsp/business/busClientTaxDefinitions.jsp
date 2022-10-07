<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Business Client Tax Policy
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
                            <td colspan="2" class="navTop">
                                <div class="navTopLinks">
                                    <div class="navTopBannerAndSignOffLink">
                                        <span class="navTopSignOff">
											<a href='${appContext}/signOut.do' title='Sign Off' id="topNavSearchButton1" onclick="" name="topNavSearchButton1">
										Sign Out</a>
										</span>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${miniBean.modeStr}"/> Tax Policy</div>
                            </td>
                        </tr>
                       
									<tr>
										<td>&nbsp;</td>
									</tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                                <form:form modelAttribute="miniBean">
									<div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
										<spring:hasBindErrors name="miniBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}" />
											</li>
											</c:forEach>
										</ul>
										</spring:hasBindErrors>
									</div>
									
                                    <table border="0" cellspacing="0" cellpadding="0" width="90%" id="pcform0">
                                        <tr>
                                            <td>
                                                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                    <tr>
                                                       <tr align="left">
                                                       <td class="activeTH"><c:out value="${miniBean.headerMessage}"/> </td><br/><br/>
                                                          
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTD">
                                                            
                                                            <table width="100%" cellspacing="0" border="0" cellpadding="3">
                                                              <tr>
                                                                    <td>
                                                                        <table width="100%" cellpadding="0" cellspacing="0">
                                                                            <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Description*</span>&nbsp;
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:input path="description" />
																				</td>
                                                                              </tr>
																			  <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Salary Range*</span>&nbsp;
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:input path="lowerBoundStr"/>&nbsp;-&nbsp;<form:input path="upperBoundStr"/>(commas allowed)
																				</td>
                                                                              </tr>
                                                                              <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Taxable Percentage</span>
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:input path="taxablePercentageStr"/>&nbsp;(0.0 - 100)
																				</td>
                                                                              </tr> 
																			 <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Tax Rate*</span>&nbsp;
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:input path="taxRate" maxlength="5"/>
																				</td>
                                                                              </tr>
																			  <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Tax Amount* (Absolute)</span>&nbsp;
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:input path="taxAmount" />
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
                                            	   <c:when test="${miniBean.editMode}">
                                                	<input type="image" name='create' value="Create"  title='Save' class='' src='images/ok_h.png' onclick="">
                                                	<input type="image" name='_expire' value="Expire" title='Expire Policy' class='' src='images/delete_h.png' onclick="">
                                                	<input type="image" name='_done' value="Done" title='Cancel' class='' src='images/cancel_h.png' onclick="">
                                                	</c:when>
                                                	<c:otherwise>
                                                	<input type="image" name='create' value="Create"  title='Create' class='' src='images/create_h.png' onclick="">
                                                	<input type="image" name='_done' value="Done" title='Done' class='' src='images/done_h.png' onclick="">
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
