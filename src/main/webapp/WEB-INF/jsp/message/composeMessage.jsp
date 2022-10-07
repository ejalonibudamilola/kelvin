<%--
    Document   : messageDisplay
    Created on : Apr 26, 2021, 8:27:46 AM
    Author     : damilola-ejalonibu
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
    <head>
        <title>
            Compose Message
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
        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
           <tr>
                <td>
                    <table style="margin-left:4px; margin-top:5px; margin-bottom:5px; margin-right:-5px" width="99%" align="center" border="0" cellpadding="0" cellspacing="0">
                       <tr>
                            <td>
                                <div class="title">Compose Message</div>
                            </td>
                       </tr>

                       <tr>
                            <td>
                                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
                                   ${sent}
                                </div>
                                <div style="margin-left:1%; margin-top:1%">
                                    <p style="font-size:12px; color:red" id="errorDiv"></p>
                                </div>
                                <div style="margin:10px; border:1px solid #f2dede; border-top-left-radius:10px; border-top-right-radius:10px">
                                   <p style="background-color:#f2dede; padding:1%; border-top-left-radius:10px; border-top-right-radius:10px;font-size:10pt">New Message</p>
                                   <p style="display:flex">
                                        <b style="padding-top:5px; padding-left:10px; font-size:10pt">To:</b>
                                        <select name="msg"  id="to" multiple="multiple" data-placeholder="select recipient" class="browser-default custom-select ms">
                                            <c:forEach items="${emp}" var="names">
                                                <option value="<c:out value="${names.id}"/>" title="<c:out value="${names.email}"/>">&nbsp;<c:out value="${names.lastName}"/> <c:out value="${names.firstName}"/></option>
                                            </c:forEach>
                                        </select>
                                   </p>
                                    <hr style="margin-top:-5px">
                                    <div style="display:flex">
                                        <p style="padding-left:1%; color:#212529;"><b style="font-size:10pt">Subject:</b></p>
                                        <input style="margin-top:-10px; border: none; outline:none; font-size:14px; width:730px" id="subject" type="text">
                                    </div>
                                   <hr style="margin-top:0">
                                   <div class="form-group" style="padding:1%">
                                      <label style="font-size:12px" for="exampleFormControlTextarea1">Message Body</label>
                                      <textarea class="form-control" id="body" rows="3"></textarea>
                                   </div>

                                   <div style="margin-bottom: 1%; margin-left: 1%">
                                      <img id="sendMsg" src="images/done_h.png">
                                   </div>
                                </div>
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
        <script>
            $('#to').multiselect({
               includeSelectAllOption: true,
               maxHeight: 350,
               buttonWidth : '100%',
               numberDisplayed: 7
            });

            $('#sendMsg').click(function(e) {
                var to= $('#to').val();
                var subject= $('#subject').val();
                var body= $('#body').val();
                console.log("to is "+to+" subject is "+subject+" body is "+body);
                if((subject=="") || (body=="")){
                    $('#errorDiv').html('One or more field is empty, Please fill all the fields to send message');
                }
                else if(to==null){
                    $('#errorDiv').html('Please select at least one Recipient');
                }
                else{
                    $.ajax({
                       type: "GET",
                       success: function (response) {
                            // do something ...
                            window.location.href ="${pageContext.request.contextPath}/messaging.do?to=" + to+ "&subject=" + subject+ "&body=" + body
                       },
                       error: function (e) {
                           alert('Error: ' + e);
                       }
                    });
                }
            });
        </script>
    </body>
</html>