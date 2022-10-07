<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="icon" type="image/png" href="/ogsg_ippms/images/coatOfArms.png">
        <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <title>Salary Structure</title>
    </head>
    <style>
            .scrollTable{
                max-width: 1200px;
                height:500px;
                overflow: auto;
                margin:1%;
            }
            .salaryReport tr:nth-child(even){
                background-color: #ffffff; font-size:8pt
            }
            .salaryReport tr:nth-child(odd){
                background-color: #f9f9f9;; font-size:8pt
            }
            .salaryReport{
               border-collapse: collapse;
            }
            .salaryReport tr{
                height:31px;
            }
            thead th{
                font-size:8pt;
                height:82px;
                background-color: #f1f1f1;
                padding: 10px 18px;
                position: sticky;
                top: 0;
                z-index:0;
            }
            thead tr th{
                border-top: red solid;
                border-bottom: red solid;
            }
            .salaryReport tbody td{
                padding: 10px 15px;
            }

            .fixedOne{
                left:0;
                position:sticky;
                background-color: #f1f1f1;
            }
            .fixedOneC{
                background-color: #f1f1f1;
            }

            .fixedTwo{
               left:90;
               position:sticky;
               background-color: #f1f1f1;
            }
            .fixedTwoC{
               background-color: #f1f1f1;
            }

            .fixedThree{
               left:180;
               position:sticky;
               background-color: #f1f1f1;
            }

            .fixedThreeC{
               background-color: #f1f1f1;
            }

            .fixedHeader{
                 z-index:1;
            }


    </style>
    <body>
    <%--<form:form modelAttribute="salStrName">--%>
        <table class="main" width="90%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
        	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
        	<tr>
        		<td>
        			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                       <tr>
                          <td>
                             <div class="title">View Salary Structure </div>
                          </td>
                       </tr>

                       <tr>
                            <td>
                                <table width="30%"  style="margin-left:1%; margin-top:1%">
                                   <tr align="left">
                                      <td class="activeTH" style="padding:1%">Salary Structure Details</td>
                                   </tr>
                                   <tr>
                                      <td class="activeTD">
                                         <p style="font-size:8pt">Salary Structure Name: ${salStrName.name}</p>

                                      </td>
                                   </tr>
                                </table>
                                <div class="scrollTable">
                                    <table id="" class="salaryReport">
                                        <thead>
                                            <tr role="row">
                                                <th class="fixedOne fixedHeader">Level/Step</th>
                                                <th class="fixedTwo fixedHeader">Basic Salary</th>
                                                <th class="fixedThree fixedHeader">Monthly Gross</th>
                                                <th>Admin Allowance</th>
                                                <th>Call Duty</th>
                                                <th>Domestic Servant</th>
                                                <th>Driver Allowance</th>
                                                <th>Entertainment</th>
                                                <th>Furniture</th>
                                                <th>Hazard</th>
                                                <th>Inducement</th>
                                                <th>Journal</th>
                                                <th>Meal</th>
                                                <th>Nurses and Other Allowances</th>
                                                <th>Rent</th>
                                                <th>Rural Posting</th>
                                                <th>Security Allowance</th>
                                                <th>Transport</th>
                                                <th>Utility</th>
                                                <th>Cons Allowance</th>
                                                <th>Exam Allowance</th>
                                                <th>LCons Allowance</th>
                                                <th>Medical Allowance</th>
                                                <th>Motor Vehicle</th>
                                                <th>Outfit Allowance</th>
                                                <th>Overtime Allowance</th>
                                                <th>Quarters Allowance</th>
                                                <th>Research Allowance</th>
                                                <th>Responsibility Allowance</th>
                                                <th>Shift Duty Allowance</th>
                                                <th>Siting Allowance</th>
                                                <th>Spa Allowance</th>
                                                <th>Special Health Allowance</th>
                                                <th>Special Allowance</th>
                                                <th>Swes Allowance</th>
                                                <th>Teaching Allowance</th>
                                                <th>Tools/Torchlight</th>
                                                <th>Totor Allowance</th>
                                                <th>Uniform/Allowance</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="list" items="${salary}">
                                                <tr role="row">
                                                   <td class="fixedOne fixedOneC"><c:out value="${list.levelStepStr}"/></td>
                                                   <td class="fixedTwo fixedTwoC"><c:out value="${list.monthlyBasicSalaryStr}"/></td>
                                                   <td class="fixedThree fixedThreeC"><c:out value="${list.monthlyGrossPayWivNairaStr}"/></td>
                                                   <td><c:out value="${list.adminAllowanceStr}"/></td>
                                                   <td><c:out value="${list.callDutyStr}"/></td>
                                                   <td><c:out value="${list.domesticServantStr}"/></td>
                                                   <td><c:out value="${list.driversAllowanceStr}"/></td>
                                                   <td><c:out value="${list.entertainmentStr}"/></td>
                                                   <td><c:out value="${list.furnitureStr}"/></td>
                                                   <td><c:out value="${list.hazardStr}"/></td>
                                                   <td><c:out value="${list.inducementStr}"/></td>
                                                   <td><c:out value="${list.journalStr}"/></td>
                                                   <td><c:out value="${list.mealStr}"/></td>
                                                   <td><c:out value="${list.nurseOtherAllowanceStr}"/></td>
                                                   <td><c:out value="${list.rentStr}"/></td>
                                                   <td><c:out value="${list.ruralPostingStr}"/></td>
                                                   <td><c:out value="${list.securityAllowanceStr}"/></td>
                                                   <td><c:out value="${list.transportStr}"/></td>
                                                   <td><c:out value="${list.utilityStr}"/></td>
                                                   <td><c:out value="${list.consAllowanceStr}"/></td>
                                                   <td><c:out value="${list.examAllowanceStr}"/></td>
                                                   <td><c:out value="${list.lcosAllowanceStr}"/></td>
                                                   <td><c:out value="${list.medicalAllowanceStr}"/></td>
                                                   <td><c:out value="${list.motorVehicleStr}"/></td>
                                                   <td><c:out value="${list.outfitAllowanceStr}"/></td>
                                                   <td><c:out value="${list.overtimeAllowanceStr}"/></td>
                                                   <td><c:out value="${list.quartersAllowanceStr}"/></td>
                                                   <td><c:out value="${list.researchAllowanceStr}"/></td>
                                                   <td><c:out value="${list.responsibilityAllowanceStr}"/></td>
                                                   <td><c:out value="${list.shiftDutyStr}"/></td>
                                                   <td><c:out value="${list.sittingAllowanceStr}"/></td>
                                                   <td><c:out value="${list.spaAllowanceStr}"/></td>
                                                   <td><c:out value="${list.specialHealthAllowanceStr}"/></td>
                                                   <td><c:out value="${list.specialistAllowanceStr}"/></td>
                                                   <td><c:out value="${list.swesAllowanceStr}"/></td>
                                                   <td><c:out value="${list.teachingAllowanceStr}"/></td>
                                                   <td><c:out value="${list.toolsTorchLightStr}"/></td>
                                                   <td><c:out value="${list.totorAllowanceStr}"/></td>
                                                   <td><c:out value="${list.uniformAllowanceStr}"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <input style="margin:1%" type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
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
    <script>
        $(function() {
           $("#slarytbl").DataTable({
              "order" : [ [ 1, "asc" ] ]
           });
        });
    </script>
    <%--</form:form>--%>
    </body>
</html>