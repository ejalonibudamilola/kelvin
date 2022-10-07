<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<html>
    <head>
        <title> Custom Report Finalization Page  </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
        <link rel="stylesheet" href="css/screen.css" type="text/css"/>

    </head>


<body class="main">
 	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">


					<tr>
						<td colspan="2">
								<div class="title">Finalize Custom Report </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						<font color="red"><b>* = required</b></font><br/><br/>
				<form:form modelAttribute="custRepBean">
				<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="custRepBean">
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
						<td class="activeTH">Set Report Generating Formats</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="2">

                            <tr>
									<td align="right" width="25%">
										<span class="required">Report File Name*</span></td>
									<td width="25%">
										 <form:input path="fileName" size="20" maxlength="30"/>
									</td>

					         </tr>

                            <tr>
									<td align="right" width="25%">
										<span class="required">Main Report Header*</span></td>
									<td width="25%">
										 <form:input path="mainHeader" size="40" maxlength="100"/>
									</td>

					         </tr>
                            <tr>
									<td align="right" width="25%">
										<span class="required">Sub-Header 1 </span></td>
									<td width="25%">
										 <form:input path="header1" size="40" maxlength="100"/>
									</td>

					         </tr>
                            <tr>
									<td align="right" width="25%">
										<span class="required">Sub-Header 2</span></td>
									<td width="25%">
										 <form:input path="header2" size="40" maxlength="100"/>
									</td>

					         </tr>
                            <tr>
									<td align="right" width="25%">
										<span class="required">Sub-Header 3</span></td>
									<td width="25%">
										 <form:input path="header3" size="40" maxlength="100"/>
									</td>

					         </tr>
					         <c:if test="${custRepBean.activeInactive}">
                                 <tr>
                                       <td align="right" width="25%" nowrap>
                                           <span class="required"><c:out value="${roleBean.staffTypeName}"/> Status</span>
                                        </td>
                                       <td width="25%">
                                          <form:radiobutton path="statusInd" value="0" />Active Only <form:radiobutton path="statusInd" value="1"/> Inactive Only <form:radiobutton path="statusInd" value="2"/> All
                                       </td>
                                  </tr>
                              </c:if>
                            <!--
                             <tr>
                                     <td align="right" width="25%" nowrap>
                                     <span class="required">Add Count*</span>
                                      </td>
                                       <td width="25%">
                                       <form:checkbox path="countInd" title="Select this box to count a unique field and add to report generation" disabled="true"/>
                                        </td>
                                </tr>
                               -->
                             <tr>
                                   <td align="right" width="25%" nowrap>
                                   <span class="required">Use My Values*</span>
                                    </td>
                                     <td width="25%">
                                     <form:checkbox path="useDefInd" title="Select this box to use values in the Current Display Name"/>
                                      </td>
                              </tr>
                                 <tr>
                                    <td align="right" width="25%" nowrap>
                                    <span class="required"><b><font color="grey">Default Display Name</font></b></span>
                                     </td>
                                      <td align="left" width="25%" nowrap>
                                         <span class="required" title="You may wish to edit the values as desired."><b>Current Display Name</b></span>
                                       </td>
                               </tr>
                             <c:forEach items="${custRepBean.headerObjects}" var="custBean" varStatus="gridRow">
                            <tr>
                                <td class="tableCell" valign="center" width="97px" align="right"><c:out value="${custBean.prefDisplayName}"/></td>
                                <td class="tableCell" valign="center" width="97px" align="left">
							        <spring:bind path="custRepBean.headerObjects[${gridRow.index}].defDisplayName">
							        <input type="text" name="<c:out value="${status.expression}"/>"
    							        id="<c:out value="${status.expression}"/>"
   								        value="<c:out value="${status.value}"/>" size="15" maxlength="25"/>
							        </spring:bind>
						         </td>
					         </tr>
					         </c:forEach>


							</table>
						</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
							<input type="image" name="submit" value="ok" title="Submit" class="" src="images/ok_h.png">
							<input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
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

