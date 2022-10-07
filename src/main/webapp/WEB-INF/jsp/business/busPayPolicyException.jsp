<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Business Client Pay Policy Msg
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
                                    Create Business Pay Policy</div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                                <form:form>
									<table border="0" cellspacing="0" cellpadding="0" width="90%">
                                        <tr>
                                            <td>
                                                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                     <tr align="left">
                                                       <td class="activeTH"> No Business Pay Policy Defined</td><br/><br/>
                                                    </tr>
                                                     <tr>
                                                        <td class="activeTD">
                                                            <input type="hidden" name="schedule3" value="0">
                                                            <table width="100%" cellspacing="0" border="0" cellpadding="3">
                                                              <tr>
                                                                    <td>
                                                                        <table width="100%" cellpadding="0" cellspacing="0">
                                                                            <tr>
                                                                            	<td width="20%" class="required">
																				<h5> Business Pay Policy has not been defined for this company. All employees of a company<br>
																					inherit default pay policy/policies which can subsequently be modified for each employee<br>
                                                                            	    Please <A HREF='${appContext}/createBusPayPolicyForm.do?mode=e' title='Setup' id="topNavSearchButton1" onclick="" target="_top" >click here to create a new Business Pay Policy</A>
                                                                            	 </h5>   	
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
