<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
    <head>
        <meta name="generator" content="HTML Tidy for Windows (vers 18 June 2008), see www.w3.org">
        <title>
            Setup | Tax Setup
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="styles/epayroll.css" type="text/css">
    </head>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                   
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Company General Tax Information
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                                <p>
                                    If you are planning to enroll in E-Payment, make sure that your filing name matches <b>exactly</b> with the name on record for your company with the FIRS. The FIRS will reject any enrollment for which name and EIN are mismatched.
                                </p>* = Required<br>
                                <br>
                                <form:form modelAttribute="clientGenTaxInfo">
								<div id="topOfPageBoxedErrorMessage" style="display:${clientGenTaxInfo.displayErrors}">
									<spring:hasBindErrors name="clientGenTaxInfo">
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
                                                    <tr>
                                                        <td class="activeTH" align="left" colspan="20">
                                                            <a name="CompanyType" id="CompanyType"></a>Company Type
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTD">
                                                            <table width="95%" border="0" cellspacing="0" cellpadding="2">
                                                                <tr>
                                                                    <td class="required" width="25%">
                                                                        <span class="required">Are you a Sole Proprietor,<br>
                                                                        501c3, or Other?*</span>
                                                                    </td>
                                                                    <td nowrap>
                                                                        <form:select path="compTypeRefId">
                                                                           <option value="-1">&lt; Please Select &gt;</option>
                                                                           <c:forEach items="${companyTypes}" var="compTypes" >
																		   <option value="${compTypes.id}">${compTypes.name}</option>
																		   </c:forEach>
                                                                        </form:select>
                                                                    </td>
                                                                </tr>
                                                            </table>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTH" align="left" colspan="20">
                                                            <a name="filingName" id="filingName"></a>Filing Name
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTD">
                                                            <table width="95%" border="0" cellspacing="0" cellpadding="2">
                                                                <tr>
                                                                    <td class="required" width="25%" nowrap>
                                                                        <span class="required">Filing Name*</span>
                                                                    </td>
                                                                    <td>
                                                                        <form:input path="name" size="50" maxlength="100" readonly="readonly"/>
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td></td>
                                                                    <!-- 
                                                                    <td>
                                                                        <span class="note"><b>Changing the Filing Name will disable your ability to make electronic federal tax payments until you complete re-enrollment for EFTPS.</b></span>
                                                                    </td>
                                                                     -->
                                                                </tr>
                                                            </table>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTH" align="left" colspan="20">
                                                            <a name="formUsageQtr" id="formUsageQtr"></a>Starting Date with SkyeBank 
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="activeTD">
                                                            <table width="95%" border="0" cellspacing="0" cellpadding="2">
                                                                <tr>
                                                                    <td class="required" width="25%">
                                                                        <span class="required">I will first use the service<br>
                                                                        to run a payroll*</span>
                                                                    </td>
                                                                    <td align="left" nowrap>
                                                                        before <form:input path="startDate"/>
                                                       					<img src="images/calendar.png" width="16" height="16" border="0" alt="Pick a date" onclick="JACS.show(document.getElementById('startDate'),event);">
                                                      					
                                                                    </td>
                                                                </tr>
                                                              
                                                            </table>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
										
                                        <tr>
                                            <td class="buttonRow" align="right">
                                                <input type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png"> 
												<input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/cancel_h.png">
                                            </td>
                                        </tr>
                                    </table>
                                </form:form>
                            </td>
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
