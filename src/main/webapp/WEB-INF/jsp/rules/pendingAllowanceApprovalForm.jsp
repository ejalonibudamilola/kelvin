<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            Approve or Reject Pending PayGroup Allowance Approvals
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
                                   Existing Pending Allowance Approvals Warning.
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainBody">
                                <form name="toDoRemove" method="post" id="toDoRemove">
                                    <input type="hidden" name="removeItem" value="">
                                </form><br>
                                <table width="100%" border="0">

                                    <tr>
                                        <td width="100%" valign="top">
                                            <form name="toDoUpdate" method="post" onsubmit="return pc_form_toDoUpdate_submit(this)" id="toDoUpdate">
                                                <table border="0" cellspacing="0" cellpadding="0" width="100%" id="pcform0">
                                                    <tr>
                                                        <td>
                                                            <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                                <tr align="left">
                                                                    <td class="activeTH">
                                                                        Pending <c:out value="${roleBean.staffTypeName}"/> PayGroup Allowance Requests found.
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
                                                                                                Please you must Approve or Reject pending <c:out value="${roleBean.staffTypeName}"/> PayGroup Allowance Rules before payroll can be run.
																							</td>
																					   </tr>
			                                                                           <tr>
                                                                                			<td>
                                                                                   			 Click <A HREF='${appContext}/viewPendingAllowanceRule.do' title='' id="" onclick="" >Here</A>&nbsp;to view pending <c:out value="${roleBean.staffTypeName}"/> PayGroup Allowance Rules.
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
