<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Setup <c:out value="${roleBean.staffTypeName}"/> Form  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon"
      type="image/png"
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script src="<c:url value="/scripts/jquery-1.7.1.min.js"/>"></script>
<script src="<c:url value="/scripts/utility_script.js"/>"></script>
</head>


<body class="main">
<form:form modelAttribute="miniBean">
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">



					<tr>
						<td colspan="2">
								<div class="title">Setup <c:out value="${roleBean.staffTypeName}"/> </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>

				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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

				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" >
					<tr align="left">
						<td class="activeTH">Setup <c:out value="${roleBean.staffTypeName}"/></td>
					</tr>
					<tr>
						<td class="activeTD">
							<table width="504" border="0" cellpadding="2" cellspacing="0">
                        <c:choose>
                        <c:when test="${miniBean.response eq false}">
								<tr>
									<td align=right><span class="required"><c:out value="${roleBean.staffTitle}"/>*</span></td>
									<td width="25%">
										<form:input path="employeeId"/>
									</td>
					            </tr>
					    </c:when>
					    <c:otherwise>
					            <tr>
                                		 <td>
                                			<div style=" margin-left:55%">
                                				<div style="border: 2px solid black; padding:2%; width:165px; border-radius:7px">
                                					   <img  src="data:image/jpeg;base64,${miniBean.photo}" width="150" height="200" >
                                				</div>
                                				 <div style="margin-top:2%">
                                					    <h3><b><c:out value="${roleBean.staffTypeName}"/>:</b> ${miniBean.biometricInfoBean.lastName} ${miniBean.biometricInfoBean.firstName} ${miniBean.biometricInfoBean.middleName} </h3>
                                					     <h3><b><c:out value="${roleBean.staffTitle}"/>:</b> ${miniBean.biometricInfoBean.employeeId}</h3>
                                					     <h3><b>Signature:</b> <img src="data:image/jpeg;base64,${miniBean.signature}" width="100" height="50" /></h3>
                                				</div>
                                			</div>
                                		</td>
                                </tr>
                        </c:otherwise>
                        </c:choose>

								 </table>
					    </td>
								</tr>
						  </table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right">
						  <c:choose>
                             <c:when test="${miniBean.response eq false}">
						    <input type="image" name="submit" value="ok" title="Proceed" class="" src="images/ok_h.png">
						     </c:when>
						     <c:otherwise>
							<input type="image" name="_go" value="go" title="Proceed" class="" src="images/ok_h.png">
							</c:otherwise>
						   </c:choose>
						   <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
						</td>
					</tr>
				</table>



			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>
</body>
</html>

