<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            View Bank Branches Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
		<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
    <body class="main">
    <form:form modelAttribute="bankBranchBean">
	<c:set value="${bankBranchList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Bank Branch List<br>
                                   
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
                                <div id="topOfPageBoxedErrorMessage" style="display:${bankBranchBean.displayErrors}">
									<spring:hasBindErrors name="bankBranchBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
												<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
									</spring:hasBindErrors>
								</div>  
								<table>
								<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    <tr>
                                      
                                       <td class="activeTD">
                                             Parent Bank :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="bankId">
												<option value="0">&lt;&nbsp;Select&nbsp;&gt;</option>
													<c:forEach items="${banksList}" var="banks">
														<option value="${banks.id}" title="${banks.sortCode}">${banks.name}</option>
													</c:forEach>

											</form:select>
                                        </td>
                                    </tr>

                                    <tr>

                                       <td class="activeTD">
                                             Branch Name :
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="name" size="20" maxlength="30"/>
                                        </td>
                                    </tr>

                                    <tr>
                                       <td class="activeTD">
                                            Branch Sort Code :
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="sortCode" size="5" maxlength="10"/>
                                        </td>
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" id="updateReport" title="Update Report" src="images/Update_Report_h.png">

                                    	</td>
                                    </tr>
								</table>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td class="">
                                            &nbsp;
                                        </td>

                                    </tr>



                                    <tr>
                                        <td valign="top">
											<p class="label">Bank Branch List</p>
											<display:table name="dispBean" id="bankBranchTbl" class="display table" export="" sort="page" defaultsort="1" requestURI="${appContext}/viewAllBankBranches.do">
											<display:setProperty name="export.rtf.filename" value="BankBranchList.rtf"/>
											<display:setProperty name="export.excel.filename" value="BankBranchList.xls"/>
											<display:column property="name" title="Name" media="html" href="${appContext}/editBankBranch.do" paramId="bbid" paramProperty="id"></display:column>
											<display:column property="name" title="Name" media="excel"></display:column>
											<display:column property="branchSortCode" title="Sort Code"></display:column>
											<display:column property="bankInfo.name" title="Parent Bank"></display:column>
										    <display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
										    <display:column property="lastModTs" title="Last Modified Date"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>

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
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
        </table>
        </form:form>
        <div class="spin"></div>
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript">
           $(function() {
              $("#bankBranchTbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
              });
           });
           $("#updateReport").click(function(e){
              $(".spin").show();
           });

           var id = document.getElementById('bankId');
           id.value=${bankBranchBean.bankId};
        </script>
    </body>
</html>