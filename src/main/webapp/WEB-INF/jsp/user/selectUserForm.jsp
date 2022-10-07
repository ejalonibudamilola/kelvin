<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
    <head>
        <title>
            <c:out value="${title}"/>
            Select User For Deactivation Form
        </title>
		<link rel="stylesheet" href=<c:url value="styles/omg.css"/> type="text/css" />
		<link rel="Stylesheet" href=<c:url value="styles/skye.css"/> type="text/css" media ="screen">
        <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
    </head>
    <body class="main">
        <table class="main" width="80%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                        
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Select User</div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainbody" id="mainbody">
                                <form:form modelAttribute="miniBean">
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
                                    <table border="0" cellspacing="0" cellpadding="0" width="100%" id="pcform0">
                                        <tr>
                                            <td>
                                                <table border="0" cellspacing="0" cellpadding="3" width="100%" align="left">
                                                   <!-- <tr>
                                                       <tr align="left">
                                                       <td class="activeTH"> Select User </td>
                                                          
                                                   </tr> -->
                                                   <!--<tr>
                                                        <td class="activeTD">
                                                            <br/><br/>
                                                            <table width="100%" cellspacing="0" border="0" cellpadding="3">
                                                                <tr>
                                                                    <td>
                                                                        <table width="100%" cellpadding="0" cellspacing="0">
                                                                            <tr>
                                                                            	<td width="20%" class="required">
                                                                            		<span class="required">Select User*</span>&nbsp;
                                                                            	</td>
                                                                                <td width="80%" nowrap>
                                                                                	<form:select path="id">
                                                                                		<form:option value="0">&lt; Please Select &gt;</form:option>
																						<c:forEach items="${miniBean.loginList}" var="uList">
																						<form:option value="${uList.id}">${uList.userName}</form:option>
																						</c:forEach>
																					</form:select>
                                                                              </td>
                                                                              </tr>
                                                                        </table>
                                                                    </td>
																	<td>&nbsp;
																	<br/><br/><br/><br/></td>
                                                                </tr>
                                                            </table>
                                                        </td>
                                                   </tr> -->

                                                    <tr>
                                                       <td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">

                                                          <div>
                                                           <div class="panel panel-danger">
                                                              <div class="panel-heading">
                                                                 <p style="font-size:12px" class="panel-title lead">
                                                                     <c:out value="${title}"/>
                                                                  </p>
                                                              </div>
                                                              <div class="panel-body">
                                                                 <table id="dtable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                                                    <thead>
                                                                        <tr>
                                                                           <td class="twentyPercentWidth">USERNAME</td>
                                                                           <td class="twentyPercentWidth">NAME</td>
                                                                           <td class="tenPercentWidth">ROLE</td>
                                                                           <td class="twentyPercentWidth">LAST LOGIN DATE</td>
                                                                         </tr>
                                                                    </thead>
                                                                     <tbody>
                                                                       <c:forEach items="${miniBean.loginList}" var="uList" varStatus="currIndex">
                                                                          <tr>
                                                                            <td>
                                                                               <!--<a href="${appContext}/${url}=${uList.id}">-->
                                                                               <a href="#" class="fixed" data-id="${uList.id}">
                                                                                  <c:out value="${uList.userName}" />
                                                                               </a>
                                                                            </td>
                                                                            <td>
                                                                              <c:out value="${uList.lastName}"/>
                                                                              <span class="spacerThreePix"></span>
                                                                              <c:out value="${uList.firstName}"/>
                                                                            </td>
                                                                            <td><c:out value="${uList.role.name}"/></td>
                                                                            <td><fmt:formatDate value="${uList.lastModTs}" pattern="MMM dd, yyyy"/></td>
                                                                          </tr>
                                                                       </c:forEach>
                                                                       </tbody>
                                                                   </table>
                                                                  </div>
                                                                </div>

                                                          </div>
                                                       </td>
                                                    </tr>
                                                </table>
                                                <br/>
                                                <br/>
                                            </td>
                                        </tr>
                                       <!-- <tr>
                                            <td class="buttonRow" align="right">
                                                <input type="image" name='submit' value="Select"  alt='Select' class='' src='images/go_h.png'>
												<input type="image" name='_cancel' value="Cancel" alt='Cancel' class='' src='images/cancel_h.png'>
                                            </td>
                                        </tr> -->
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

        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript">
        		$(function() {

        			$("#dtable").DataTable({
        				"order" : [ [ 1, "asc" ] ],
        				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
        				//also properties higher up, take precedence over those below
        				"columnDefs":[
        					//{"targets": 0, "orderable" : false},
        					//{"targets": 5, "searchable" : false }
        					//{"targets": [0, 1], "orderable" : false }
        				]
        			});
        		});

                $(".fixed").click(function(e) {
                    var id = $(this).data('id');
                    console.log("Id is "+id);
                    $.ajax({
                          type: "GET",
                          success: function (response) {
                             // do something ...
                             window.location.href ="${appContext}/${url}="+id
                          },
                          error: function (e) {
                             alert('Error: ' + e);
                          }
                    });
                });
        </script>
    </body>
</html>
