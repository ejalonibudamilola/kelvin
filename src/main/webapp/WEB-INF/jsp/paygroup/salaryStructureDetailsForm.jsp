<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>Salary Structure Details Page</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">

<link rel="stylesheet" href="${appContext}/dataTables/css/fixedColumns.dataTables.min.css" type="text/css">
</head>

<style>
    .tableDiv{
        max-width: 1200px;
        overflow-x: auto;
    }

    thead th{
        background-color: #f1f1f1;
    }

    #result1{
        display:none
    }

    #showDownload{
        padding-left:2%
    }

    .formTable{
        margin: 2%;
        width: 50%;
    }
</style>

<body class="main">

	<form:form modelAttribute="miniBean">
		<table class="main" width="70%" border="1" bordercolor="#33c0c8"
			cellspacing="0" cellpadding="0" align="center">

			<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

			<tr>
				<td>
					<table width="100%" align="center" border="0" cellpadding="0"
						cellspacing="0">

						<tr>
							<td colspan="2">
								<div class="title">
									<c:out value="${miniBean.name}"/> Details
								</div>
							</td>
						</tr>
						<tr>
						    <td valign="top"  class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
						      <div class="panel-body">
                              	<div class="tableDiv">
                              	    <table id="userTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                            <h2 style="font-size: 15px">
											    Salary Type: <c:out value="${miniBean.name}"/>
											</h2>
											<thead>
												<tr><th class="tenPercentWidth">Level And Step</th>
													<th class="twentyPercentWidth">Annual Gross</th>
													<th class="twentyPercentWidth">Monthly Gross</th>
													<th class="twentyPercentWidth">Basic Salary</th>
													<th class="twentyPercentWidth">Rent</th>
													<th class="twentyPercentWidth">Transport</th>
													<th class="twentyPercentWidth">Utility</th>
													<th class="twentyPercentWidth">Meal</th>
													<th class="twentyPercentWidth">Motor Vehicle</th>
													<th class="twentyPercentWidth">Exam Allowance</th>
													<th class="twentyPercentWidth">LCons. Allowance</th>
													<th class="twentyPercentWidth">Cons. Allowance</th>
													<th class="twentyPercentWidth">Outfit Allowance</th>
													<th class="twentyPercentWidth">Quarters Allowance</th>
													<th class="twentyPercentWidth">Spa Allowance</th>
													<th class="twentyPercentWidth">Responsibility Allowance</th>
													<th class="twentyPercentWidth">Security Allowance</th>
													<th class="twentyPercentWidth">Swes Allowance</th>
													<th class="twentyPercentWidth">Research Allowance</th>
													<th class="twentyPercentWidth">Totor Allowance</th>
													<th class="twentyPercentWidth">Medical Allowance</th>
													<th class="twentyPercentWidth">Sitting Allowance</th>
													<th class="twentyPercentWidth">Overtime Allowance</th>
													<th class="twentyPercentWidth">Tools Torch Light Allowance</th>
													<th class="twentyPercentWidth">Uniform Allowance</th>
													<th class="twentyPercentWidth">Teaching Allowance</th>
													<th class="twentyPercentWidth">Enhanced Health Allowance</th>
                                                    <th class="twentyPercentWidth">Special Health Allowance</th>
                                                    <th class="twentyPercentWidth">Shift Duty Allowance</th>
                                                    <th class="twentyPercentWidth">Special Allowance</th>
                                                    <th class="twentyPercentWidth">Admin Allowance</th>
                                                    <th class="twentyPercentWidth">Call Duty</th>
                                                    <th class="twentyPercentWidth">Domestic Servant</th>
 													<th class="twentyPercentWidth">Drivers Allowance</th>
 													<th class="twentyPercentWidth">Entertainment</th>
                                                    <th class="twentyPercentWidth">Furniture</th>
                                                    <th class="twentyPercentWidth">Hazard</th>
                                                    <th class="twentyPercentWidth">Inducement</th>
                                                    <th class="twentyPercentWidth">Journal</th>
                                                    <th class="twentyPercentWidth">Nurse Other Allowance</th>
                                                    <th class="twentyPercentWidth">Rural Posting</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${displayList}" var="innerBean" varStatus="gridRow">
													<tr>
														<td class=""><c:out value="${innerBean.levelAndStepAsStr}" /></td>
														<td><c:out value="${innerBean.annualSalaryStrWivNaira}" /></td>
														<td class=""><c:out value="${innerBean.monthlyGrossPayWivNairaStr}" /></td>
														<td class=""><c:out value="${innerBean.basicSalaryStr}" /></td>
														<td><c:out value="${innerBean.rentStr}" /></td>
														<td><c:out value="${innerBean.transportStr}" /></td>
														<td><c:out value="${innerBean.utilityStr}" /></td>
														<td><c:out value="${innerBean.mealStr}" /></td>
														<td><c:out value="${innerBean.motorVehicleStr}" /></td>
														<td><c:out value="${innerBean.examAllowanceStr}" /></td>
														<td><c:out value="${innerBean.lcosAllowanceStr}" /></td>
														<td><c:out value="${innerBean.consAllowanceStr}" /></td>
														<td><c:out value="${innerBean.outfitAllowanceStr}" /></td>
														<td><c:out value="${innerBean.quartersAllowanceStr}" /></td>
														<td><c:out value="${innerBean.spaAllowanceStr}" /></td>
														<td><c:out value="${innerBean.responsibilityAllowanceStr}" /></td>
														<td><c:out value="${innerBean.securityAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.swesAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.researchAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.totorAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.medicalAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.sittingAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.overtimeAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.toolsTorchLightStr}" /></td>
													    <td><c:out value="${innerBean.uniformAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.teachingAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.enhancedHealthAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.specialHealthAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.shiftDutyStr}" /></td>
													    <td><c:out value="${innerBean.specialistAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.adminAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.callDutyStr}" /></td>
													    <td><c:out value="${innerBean.domesticServantStr}" /></td>
													    <td><c:out value="${innerBean.driversAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.entertainmentStr}" /></td>
													    <td><c:out value="${innerBean.furnitureStr}" /></td>
													    <td><c:out value="${innerBean.hazardStr}" /></td>
													    <td><c:out value="${innerBean.inducementStr}" /></td>
													    <td><c:out value="${innerBean.journalStr}" /></td>
													    <td><c:out value="${innerBean.nurseOtherAllowanceStr}" /></td>
													    <td><c:out value="${innerBean.ruralPostingStr}" /></td>
													</tr>
												</c:forEach>
											</tbody>
                              	    </table>
                              	</div>
                              </div>
						    </td>
						</tr>
					</table>
					<p id="showDownload"><a href="#" onClick="showDownload(); return false">Generate Report</a></p>

                    <div id="result1">
                        <table class="formTable" border="0" cellspacing="0" cellpadding="3"  align="left" >
                            <tr align="left">
                                <td class="activeTH">Configure Report Format </td>
                            </tr>
                            <tr>
                                <td class="activeTD">
                                   <table border="0" cellspacing="0" cellpadding="2">
                                      <fieldset>
                                         Print Options
                                         <tr>
                                            <td width="25%">
                                                <form:radiobutton name="rvalue" path="" value="1" />Monthly Value
                                                <form:radiobutton name="rvalue" path="" value="2" />Yearly Value
                                            </td>
                                         </tr>
                                         <tr>
                                            <td width="25%">
                                                <form:radiobutton path="" name="dvalue" value="1" />Text Value
                                                <form:radiobutton path="" name="dvalue" value="2" />Number Value
                                            </td>
                                         </tr>
                                      </fieldset>
                                   </table>
                                   <p style="padding-top: 3%; padding-left: 1%"><a href="#" onClick="downloadReport(${miniBean.id}); return false">Download Report</a></p>
                                </td>
							</tr>
                        </table>
				    </div>
				</td>
			</tr>
           </tr>
               <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
           </tr>
		</table>
	</form:form>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/dataTables/js/dataTables.fixedColumns.min.js"/>"></script>
    <script type="text/javascript">
          $(document).ready(function() {
            var table = $('#userTable').DataTable( {
                "order": [],

                scrollX:        true,
                scrollCollapse: true,
                paging:         true,
                fixedColumns:   {
                    leftColumns: 4
              },
            });
          });
          function showDownload(){
            $('#result1').css("display", "block");
            $('#showDownload').css("display", "none");
          };


          function downloadReport(id){
             console.log("got to download");
                var rvalue = $("input[name='rvalue']:checked").val();
                var dvalue = $("input[name='dvalue']:checked").val();
                if(rvalue && dvalue){
                    window.location.href = "${appContext}/salaryStructureExcel.do?sid="+id+"&rvalue=" + rvalue + "&dvalue= "+ dvalue;
                }
          }
    </script>
</body>
</html>