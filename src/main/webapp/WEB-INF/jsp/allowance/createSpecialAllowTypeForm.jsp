<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> New Special Allowance Type Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>

</head>

<body class="main">

	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">Create a New Special Allowance Type</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody"> * = required<br/><br/>
                            <form:form modelAttribute="specAllowTypeBean">
                                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
                                                 <spring:hasBindErrors name="specAllowTypeBean">
                                                     <ul>
                                                        <c:forEach var="errMsgObj" items="${errors.allErrors}">
                                                            <li>
                                                            <spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
                                                            </li>
                                                        </c:forEach>
                                                        </ul>
                                                 </spring:hasBindErrors>
                                </div>
                                <table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
                                    <tr align="left">
                                        <td class="activeTH">New Special Allowance Type Information</td>
                                    </tr>
                                    <tr>
                                        <td class="activeTD">
                                            <table border="0" cellspacing="0" cellpadding="2">
                                                <tr>
                                                    <td align="right" width="35%" nowrap>
                                                        <span class="required">Special Allowance Type Code*</span></td>
                                                    <td width="25%">
                                                        <form:input path="name" size="12" maxlength="12"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="right" width="35%" nowrap>
                                                        <span class="required">Description*</span></td>
                                                    <td width="25%">
                                                        <form:input path="description" size="30" maxlength="30"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="right" width="35%" nowrap>
                                                        <span class="required">Tax Deductible*</span></td>
                                                    <td width="25%" nowrap>
                                                        <form:radiobutton path="taxExemptInd" value="0" />Deduct <form:radiobutton path="taxExemptInd" value="1"/> Exempt

                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="right" width="35%" nowrap>
                                                        <span class="required" title="Indicate whether editing of Special Allowance of this type should be restricted to super admin and admin roles">Restrict Editing*</span></td>
                                                    <td width="25%" nowrap>
                                                        <form:radiobutton path="editRestrictionInd" value="0" title="Edit of Special Allowance of this Type not restricted to super admin and admin roles only"/>No <form:radiobutton path="editRestrictionInd" value="1" title="Only Super Admin and Administrator Role can edit Special Allowance of this type"/> Yes

                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="right" width="35%" nowrap>
                                                        <span class="required" title="Designates Special Allowance as a Type of Arrears (This ensures it is NOT Taxable, irrespective of the value for 'Tax Deductible'"> Arrears Type*</span></td>
                                                    <td width="25%" nowrap>
                                                        <form:radiobutton path="arrearsInd" value="0" />Yes <form:radiobutton path="arrearsInd" value="1"/> No

                                                    </td>
                                                </tr>
                                                <tr>
                                                   <td align="right" width="30%" nowrap><span class="required">Apply As*&nbsp;</span></td>
                                                   <td align="left" nowrap>
                                                        <form:select path="payTypes.id">
                                                        <form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
                                                          <c:forEach items="${payType}" var="ptypes">
                                                             <form:option value="${ptypes.id}">${ptypes.name}</form:option>
                                                          </c:forEach>
                                                        </form:select>
                                                   </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <c:choose>
                                        <c:when test= "${saved}">
                                            <tr>
                                                <td>
                                                    <input type="image" name="_cancel" value="cancel" alt="Cancel" class="" src="images/close.png">
                                                </td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td class="buttonRow" align="right" >
                                                    <input type="image" name="submit" value="ok" title="Ok" class="" src="images/ok_h.png">&nbsp;
                                                    <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png">
                                                </td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
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