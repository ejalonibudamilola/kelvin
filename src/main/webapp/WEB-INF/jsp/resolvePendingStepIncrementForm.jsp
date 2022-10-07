<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Pending Salary Step Increment
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
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
                                   Pending Step Increment Found.
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainBody">
                               <br>
                                <table width="100%" border="0">
                                   
                                    <tr>
                                        <td width="100%" valign="top">
                                            <form>
                                                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                                    <tr>
                                                        <td>
                                                            <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                                <tr align="left">
                                                                    <td class="activeTH">
                                                                        Pending Step Increment that have not been applied found.
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td id="firstTD_toDoUpdate" class="activeTD">
                                                                        <input type="hidden" name="removeItem" value="">
                                                                        <table width="100%" border="0" cellspacing="2" cellpadding="2">
                                                                             <tr>
                                                                                <td class="toDoBullet1">
                                                                                    <img src="images/list_greybullet.png" border="0">
                                                                                </td>
                                                                                <td class="toDoBullet2">
                                                                                    <table width="100%" border="0" cellpadding="0" cellspacing="0">
                                                                                        <tr>
                                                                                            <td valign="top" nowrap>
                                                                                                There are pending yearly step increment found. Please confirm or delete this pending transactions<br>
                                                                                                before attempting to run another step increment process for the same year. This process is allowed<br>
                                                                                                only once in a year.
																							</td>
																					   </tr>
			                                                                           <tr>
                                                                                			<td>
                                                                                   			 Click <A HREF='${appContext}/approveStepIncrease.do' title='' id="" onclick="" >Here</A>&nbsp;to approve or delete pending yearly salary step increase.
                                                                               				 </td>
                                                                           				</tr>
																					   
                                                                                    </table>
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
                                            </form>
                                        </td>
                                        <td width="15" valign="top">
                                            &nbsp;
                                        </td>
                                    </tr>
                                </table>
                                
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
