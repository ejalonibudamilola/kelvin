<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Lock Application
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="css/bootstrap-multiselect.css">
        <link rel="stylesheet" href="css/jquery-ui.css">
    </head>
    <style>
            .multiselect-container {
              width:300px;
            }

            button.multiselect {
               //background-color: initial;
               text-align: left;
               height:30px;
               margin-left:5px;
               border-radius:5px;
               padding-left:10px;
               padding-right:10px;
            }

            .multiselect-option {
               width:100%;
               text-align: left;
            }

             .multiselect-option label{
                font-size: 11px !important;
                font-weight: normal !important;
             }

             .multiselect{
                border:none !important;
                focus:none !important;
                outline:none !important;
             }

             .multiselect-selected-text{
                font-size:12px;
             }
             .multiselect-all{
                width:100%;
                text-align:left;
             }
    </style>
    <body class="main">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Lock Application
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
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
								  <table class="reportMain" cellpadding="0" cellspacing="0" width="90%">
                                       <tr>
                                          <td>
                                              <div style="background-color:#E1E1E1; width:50%; padding:5px 0 0 5px; border: 1px solid white">
                                                 <p style="color: black; font-weight: bold; font-size:8pt">Lock Application</p>
                                              </div>
                                              <div style="display:block; background-color:#F0F7D2; margin:0 2% 2% 0; width:50%; padding:1%; border: 1px solid #E1E1E1" id="deploy">
                                                <p style="display:flex">
                                                   <b style="padding-top:5px; padding-left:10px;">Organization:</b>
                                                   <select name="msg"  id="to" multiple="multiple" data-placeholder="select organization" class="browser-default custom-select ms">
                                                      <c:forEach items="${org}" var="names">
                                                         <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                                      </c:forEach>
                                                   </select>
                                               </p>
                                                <!--<input id="deployId" path="" type="checkbox" title="Select this to indicate deploying"/> <label>Deploying<label>-->
                                              </div>
                                          </td>
                                          <tr>
                                            <td><input id="ok" type="image" name="submit" value="ok" alt="Ok" class="" src="images/ok_h.png"></td>
                                          </tr>
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
         <script src="scripts/bootstrap-multiselect.js"></script>
         <script src="scripts/jquery-ui.js"></script>
        <script type="text/javascript">
            $("#ok").click(function(e){
              let deployStatus = 0;
              if($('#deployId').is(':checked')){
                deployStatus = 1
                $("#showDeployStatus").css("display", "block")
              }
              else{
               $("#showDeployStatus").css("display", "none")
              }
              console.log("The value of deploy status is "+deployStatus);
            });

            $('#to').multiselect({
               includeSelectAllOption: true,
               maxHeight: 350,
               buttonWidth : '100%',
               numberDisplayed: 7
            });
        </script>
    </body>
</html>
