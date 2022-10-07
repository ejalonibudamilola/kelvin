<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
         <!-- Bootstrap core CSS -->
        <link rel="stylesheet" href="css/bootstrap-multiselect.css">
        <link rel="stylesheet" href="css/jquery-ui.css">
        <link rel="icon" type="image/png" href="/ogsg_ippms/images/coatOfArms.png">
        <title>Help</title>
    </head>
    <style>

        .container {
            margin-bottom: 5px
        }

        .panel-title {
            font-size: 10pt !important;
            color: green !important;
        }

        .level1{
            margin-top:10px;
        }

        .level2{
            margin-left: 2%;
            margin-top: 7px;
        }
        .level2H a{
            /*color: #ffbf00 !important;*/
            color: #f00 !important;
            font-size: 9pt;
        }

        .level2H{
            /*color: #ffbf00 !important;*/
            color: #f00 !important;
           font-size: 9pt;
        }

        .level3{
            margin-left:2%;
        }

        .level3 p{
            color:#0000ffd1;
            font-size: 9pt;
        }

        .level4{
            margin-left:2%;
        }

        .level4H{
          color: #ffb300 !important;
          font-size: 9pt;
        }

        .level4H a{
           color: #ffb300 !important;
           font-size: 9pt;
        }

        .level4 p{
            color:#ffb300 !important;
            font-size:9pt
        }
    </style>
    <body class="main">
        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
           <tr>
              <td>
                 <table>
                   <tr>
                        <td>
                            <div class="title">
                                Content
                            </div>
                        </td>
                   </tr>

                   <tr>
                     <td>
                        <div class="container">
                                <div class="level1">
                                    <div class="">
                                        <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#login2" aria-expanded="false" aria-controls="login2"><span class="glyphicon glyphicon-plus"></span></a>Log In</h4>
                                    </div>

                                    <div class="collapse " id="login2">
                                       <img src="manual/login/login.png">
                                    </div>
                                </div>
                                <div class="level1">
                                    <div class="">
                                        <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#adminTask2" aria-expanded="false" aria-controls="adminTask"><span class="glyphicon glyphicon-plus"></span></a>Admin Task</h4>
                                    </div>

                                    <div class="collapse level2" id="adminTask2">
                                       <div class="">
                                          <p class="level2H"> <a data-toggle="collapse" href="#auditLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Audit Logs</p>
                                           <div class="collapse level3" id="auditLogs">
                                              <p class=""> <a data-toggle="collapse" href="#bank" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Bank Account Log</p>
                                              <div class="collapse level4" id="bank">
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#empBankLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Banks Logs
                                                    <div class="collapse" id="empBankLogs">
                                                        <img src="manual/adminTask/auditLog/bankAccountLog/paymentInfoLog.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#userAcctLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>User Account Logs
                                                    <div class="collapse" id="userAcctLogs">
                                                         <img src="manual/adminTask/auditLog/bankAccountLog/userAccountLog.png">
                                                    </div>
                                                 </p>
                                              </div>
                                              <p class=""><a data-toggle="collapse" href="#deduction" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Deduction Allowance Log
                                              <div class="collapse level4" id="deduction">
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#empDeductLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Deduction Log
                                                    <div class="collapse" id="empDeductLogs">
                                                        <img src="manual/adminTask/auditLog/deductionLog/empDeductLog.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#empLoanLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Loan Log
                                                    <div class="collapse" id="empLoanLogs">
                                                         <img src="manual/adminTask/auditLog/deductionLog/empLoanLog.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#empSpecAllowLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Special Allowance Log
                                                    <div class="collapse" id="empSpecAllowLogs">
                                                         <img src="manual/adminTask/auditLog/deductionLog/empSpecAllow.png">
                                                    </div>
                                                 </p>
                                              </div>
                                              </p>
                                              <p class=""><a data-toggle="collapse" href="#empRelatedLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Related Log
                                                 <div class="collapse level4" id="empRelatedLogs">
                                                     <p class="level4H">
                                                        <a data-toggle="collapse" href="#empBasicInfo" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Basic Information Changes
                                                        <div class="collapse" id="empBasicInfo">
                                                            <img src="manual/adminTask/auditLog/empRelatedLog/basicInfoChanges.png">
                                                        </div>
                                                     </p>
                                                     <p class="level4H">
                                                        <a data-toggle="collapse" href="#empHiringInfo" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Hiring Information Changes
                                                        <div class="collapse" id="empHiringInfo">
                                                             <img src="manual/adminTask/auditLog/empRelatedLog/hireInfoChanges.png">
                                                        </div>
                                                     </p>
                                                     <p class="level4H">
                                                        <a data-toggle="collapse" href="#empPromotionLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Promotion Logs
                                                        <div class="collapse" id="empPromotionLogs">
                                                             <img src="manual/adminTask/auditLog/empRelatedLog/promotionLogs.png">
                                                        </div>
                                                     </p>
                                                     <p class="level4H">
                                                        <a data-toggle="collapse" href="#empReassignLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Reassignment Logs
                                                        <div class="collapse" id="empReassignLogs">
                                                             <img src="manual/adminTask/auditLog/empRelatedLog/reassignmentLog.png">
                                                        </div>
                                                     </p>
                                                     <p class="level4H">
                                                        <a data-toggle="collapse" href="#empReinstateLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Reinstatement Logs
                                                        <div class="collapse" id="empReinstateLogs">
                                                             <img src="manual/adminTask/auditLog/empRelatedLog/reinstatementLog.png">
                                                        </div>
                                                     </p>
                                                     <p class="level4H">
                                                         <a data-toggle="collapse" href="#empTransferLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Transfer Logs
                                                         <div class="collapse" id="empTransferLogs">
                                                              <img src="manual/adminTask/auditLog/empRelatedLog/transferLog.png">
                                                         </div>
                                                     </p>
                                                 </div>
                                              </p>
                                              <p class=""><a data-toggle="collapse" href="#otherLogs" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Other Logs
                                                <div class="collapse" id="otherLogs">
                                                   <img src="manual/adminTask/auditLog/otherLogs/subvention.png">
                                                </div>
                                              </p>
                                           </div>
                                       </div>
                                       <div class="">
                                         <p class="level2H"> <a data-toggle="collapse" href="#configAndControl" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Configuration And Control</p>
                                         <div class="collapse level3" id="configAndControl">
                                            <p class=""> <a data-toggle="collapse" href="#deductionLoanCat" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Deduction Loan Configuration</p>
                                            <div class="collapse level4" id="deductionLoanCat">
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#newDedCat" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>New Deduction Category
                                                    <div class="collapse" id="newDedCat">
                                                        <img src="manual/adminTask/config/deduction/newDeductionCat.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#newDedType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>New Deduction Type
                                                    <div class="collapse" id="newDedType">
                                                         <img src="manual/adminTask/config/deduction/newDeductionType.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#newLoanType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>New Loan Type
                                                    <div class="collapse" id="newLoanType">
                                                         <img src="manual/adminTask/config/deduction/newLoanType.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#newSpecAllowType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>New Special Allowance Type
                                                    <div class="collapse" id="newSpecAllowType">
                                                         <img src="manual/adminTask/config/deduction/newSpecAllowType.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#viewEditDedType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit Deduction Type
                                                    <div class="collapse" id="viewEditDedType">
                                                         <img src="manual/adminTask/config/deduction/VEDedType.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#viewEditLoanType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit Loan Type
                                                    <div class="collapse" id="viewEditLoanType">
                                                         <img src="manual/adminTask/config/deduction/VELoanType.png">
                                                    </div>
                                                 </p>
                                                 <p class="level4H">
                                                    <a data-toggle="collapse" href="#viewEditSpecAllowType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit Special Allowance Type
                                                    <div class="collapse" id="viewEditSpecAllowType">
                                                         <img src="manual/adminTask/config/deduction/VESpecAllow.png">
                                                    </div>
                                                 </p>
                                            </div>
                                            <p class=""> <a data-toggle="collapse" href="#modelConfig" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Model Configuration</p>
                                            <div class="collapse level4" id="modelConfig">
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#cadre" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add/Edit Cadres
                                                  <div class="collapse" id="cadre">
                                                     <img src="manual/adminTask/config/model/cadre.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#rank" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add/Edit Ranks
                                                  <div class="collapse" id="rank">
                                                     <img src="manual/adminTask/config/model/rank.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#title" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add/Edit Title
                                                  <div class="collapse" id="title">
                                                     <img src="manual/adminTask/config/model/title.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#addBank" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add New Bank
                                                  <div class="collapse" id="addBank">
                                                     <img src="manual/adminTask/config/model/addBank.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#addBBranch" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add New Bank Branch
                                                  <div class="collapse" id="addBBranch">
                                                     <img src="manual/adminTask/config/model/addBBranch.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#addCity" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add New City
                                                  <div class="collapse" id="addCity">
                                                     <img src="manual/adminTask/config/model/addCity.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#addLGA" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add New L.G.A
                                                  <div class="collapse" id="addLGA">
                                                     <img src="manual/adminTask/config/model/addLGA.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#addRelType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Add New Relationship Type
                                                  <div class="collapse" id="addRelType">
                                                     <img src="manual/adminTask/config/model/addRelType.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#rbaValue" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Alter RBA Value
                                                  <div class="collapse" id="rbaValue">
                                                     <img src="manual/adminTask/config/model/rba.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#absorptionReasons" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Create/Edit Absorption Reasons
                                                  <div class="collapse" id="absorptionReasons">
                                                     <img src="manual/adminTask/config/model/absorption.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#empType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Create/Edit Employee Type
                                                  <div class="collapse" id="empType">
                                                     <img src="manual/adminTask/config/model/empType.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#eduType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Create/Edit Qualification School Type
                                                  <div class="collapse" id="eduType">
                                                     <img src="manual/adminTask/config/model/eduType.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#suspType" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Create/Edit Suspension Type
                                                  <div class="collapse" id="suspType">
                                                     <img src="manual/adminTask/config/model/suspensionType.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#mbb" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Merge Bank Branches
                                                  <div class="collapse" id="mbb">
                                                     <img src="manual/adminTask/config/model/mbb.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                   <a data-toggle="collapse" href="#VEBank" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit Bank
                                                   <div class="collapse" id="VEBank">
                                                      <img src="manual/adminTask/config/model/VEBank.png">
                                                   </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#VEBBranch" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit Bank Branches
                                                  <div class="collapse" id="VEBBranch">
                                                     <img src="manual/adminTask/config/model/VEBBranch.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#VECity" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit City
                                                  <div class="collapse" id="VECity">
                                                     <img src="manual/adminTask/config/model/VECity.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#VELga" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit LGA
                                                  <div class="collapse" id="VELga">
                                                     <img src="manual/adminTask/config/model/VELga.png">
                                                  </div>
                                               </p>
                                               <p class="level4H">
                                                  <a data-toggle="collapse" href="#VERel" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit Relationship Type
                                                  <div class="collapse" id="VERel">
                                                     <img src="manual/adminTask/config/model/VERel.png">
                                                  </div>
                                               </p>
                                            </div>
                                            <p class=""> <a data-toggle="collapse" href="#pensionConfig" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Pension Model Configuration</p>
                                            <div class="collapse level4" id="pensionConfig">
                                                <p class="level4H">
                                                   <a data-toggle="collapse" href="#newPFA" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Create New PFA
                                                   <div class="collapse" id="newPFA">
                                                      <img src="manual/adminTask/config/model/VERel.png">
                                                   </div>
                                                </p>
                                                <p class="level4H">
                                                   <a data-toggle="collapse" href="#newPFC" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Create New PFC
                                                   <div class="collapse" id="newPFC">
                                                      <img src="manual/adminTask/config/model/VERel.png">
                                                   </div>
                                                </p>
                                                <p class="level4H">
                                                   <a data-toggle="collapse" href="#vePFA" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit PFA
                                                   <div class="collapse" id="vePFA">
                                                      <img src="manual/adminTask/config/model/VERel.png">
                                                   </div>
                                                </p>
                                                <p class="level4H">
                                                   <a data-toggle="collapse" href="#vePFC" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>View/Edit PFC
                                                   <div class="collapse" id="vePFC">
                                                      <img src="manual/adminTask/config/model/VERel.png">
                                                   </div>
                                                </p>
                                            </div>
                                         </div>
                                       </div>
                                       <div class="">
                                         <p class="level2H"> <a data-toggle="collapse" href="#" aria-expanded="false" aria-controls=""><span class="glyphicon glyphicon-plus"></span></a>Employee Functions</p>
                                       </div>
                                    </div>
                                </div>
                                <div class="level1">
                                    <div class="">
                                        <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#hrFunctions2" aria-expanded="false" aria-controls="hrFunctions2"><span class="glyphicon glyphicon-plus"></span></a>HR Function</h4>
                                    </div>

                                    <div class="collapse level2" id="hrFunctions2">
                                        <div class="">
                                            <p class="level2H"> <a data-toggle="collapse" href="#dept2" aria-expanded="false" aria-controls="dept"><span class="glyphicon glyphicon-plus"></span></a>Department Functionalities</p>

                                            <div class="collapse level3" id="dept2">
                                                <p class="">
                                                    <a data-toggle="collapse" href="#createDept2" aria-expanded="false" aria-controls="dept"><span class="glyphicon glyphicon-plus"></span></a>Create/Edit Department
                                                    <div class="collapse" id="createDept2">
                                                        <img src="manual/hrFunc/dept/createDept/createDept.png">
                                                    </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#assignDept2" aria-expanded="false" aria-controls="dept"><span class="glyphicon glyphicon-plus"></span></a>Assign Department
                                                    <div class="collapse" id="assignDept2">
                                                        <img src="manual/hrFunc/dept/assignDept/assignDept.png">
                                                    </div>
                                                </p>
                                            </div>
                                        </div>
                                        <div class="">
                                            <h4 class="level2H"> <a data-toggle="collapse" href="#emp2" aria-expanded="false" aria-controls="emp2"><span class="glyphicon glyphicon-plus"></span></a>HR Employees Functionalities</h4>

                                            <div class="collapse level3" id="emp2">
                                                <p class="">
                                                    <a data-toggle="collapse" href="#demotion" aria-expanded="false" aria-controls="demotion"><span class="glyphicon glyphicon-plus"></span></a>Demotion/Pay Group Change
                                                    <div class="collapse" id="demotion">
                                                       <img src="manual/hrFunc/hrEmpFunc/demotion/demotion2.png">
                                                    </div>
                                                </p>
                                                <c:if test="${not roleBean.pensioner}">
                                                <p class="">
                                                    <a data-toggle="collapse" href="#promotion" aria-expanded="false" aria-controls="promotion"><span class="glyphicon glyphicon-plus"></span></a>Promote Employee
                                                    <div class="collapse" id="promotion">
                                                       <img src="manual/hrFunc/hrEmpFunc/promotion/promotion2.png">
                                                    </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#reabsorb" aria-expanded="false" aria-controls="reabsorb"><span class="glyphicon glyphicon-plus"></span></a>Reabsorb Employee
                                                    <div class="collapse" id="reabsorb">
                                                       <img src="manual/hrFunc/hrEmpFunc/reabsorption/reabsorption2.png">
                                                    </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#reinstate" aria-expanded="false" aria-controls="reinstate"><span class="glyphicon glyphicon-plus"></span></a>Reinstate Employee
                                                    <div class="collapse" id="reinstate">
                                                       <img src="manual/hrFunc/hrEmpFunc/reinstatement/reinstatement2.png">
                                                    </div>
                                                </p>
                                                </c:if>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#suspension" aria-expanded="false" aria-controls="suspension"><span class="glyphicon glyphicon-plus"></span></a>Suspend Employee
                                                    <div class="collapse" id="suspension">
                                                       <img src="manual/hrFunc/hrEmpFunc/suspension/suspension2.png">
                                                    </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#terminate" aria-expanded="false" aria-controls="terminate"><span class="glyphicon glyphicon-plus"></span></a>Terminate Employee
                                                    <div class="collapse" id="terminate">
                                                       <img src="manual/hrFunc/hrEmpFunc/termination/termination2.png">
                                                    </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#transfer" aria-expanded="false" aria-controls="transfer"><span class="glyphicon glyphicon-plus"></span></a>Transfer Employee
                                                    <div class="collapse" id="transfer">
                                                       <img src="manual/hrFunc/hrEmpFunc/transfer/transfer.png">
                                                    </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#stepIncreament" aria-expanded="false" aria-controls="stepIncreament"><span class="glyphicon glyphicon-plus"></span></a>Yearly Step Increment
                                                    <div class="collapse" id="stepIncreament">
                                                       <img src="manual/hrFunc/hrEmpFunc/stepIncrement/step_increment.png">
                                                    </div>
                                                </p>
                                            </div>
                                        </div>

                                       <div class="">
                                            <h4 class="level2H"> <a data-toggle="collapse" href="#mda2" aria-expanded="false" aria-controls="mda"><span class="glyphicon glyphicon-plus"></span></a>MDA/School Functionalities</h4>
                                            <div class="collapse level3" id="mda2">
                                                <p class="">
                                                   <a data-toggle="collapse" href="#newm" aria-expanded="false" aria-controls="newm"><span class="glyphicon glyphicon-plus"></span></a>New MDA
                                                   <div class="collapse" id="newm">
                                                      <img src="manual/hrFunc/MDAFunc/addNewMda.png">
                                                   </div>
                                                </p>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#editm" aria-expanded="false" aria-controls="editm"><span class="glyphicon glyphicon-plus"></span></a>Edit MDA
                                                    <div class="collapse" id="editm">
                                                      <img src="manual/hrFunc/MDAFunc/editMda.png">
                                                    </div>
                                                </p>
                                                <c:if test="${roleBean.civilService or roleBean.subeb}">
                                                <p class="">
                                                    <a data-toggle="collapse" href="#news" aria-expanded="false" aria-controls="news"><span class="glyphicon glyphicon-plus"></span></a>New School
                                                    <div class="collapse" id="news">
                                                      <img src="manual/hrFunc/MDAFunc/newSchool.png">
                                                    </div>
                                                </p>
                                                </c:if>
                                                <p class="">
                                                    <a data-toggle="collapse" href="#edits" aria-expanded="false" aria-controls="edits"><span class="glyphicon glyphicon-plus"></span></a>View/Edit School
                                                    <div class="collapse" id="edits">
                                                      <img src="manual/hrFunc/MDAFunc/viewEditSch.png">
                                                    </div>
                                                </p>
                                            </div>
                                       </div>
                                    </div>
                                </div>
                                <div class="level1">
                                    <div class="">
                                       <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#hrReport" aria-expanded="false" aria-controls="hrReport"><span class="glyphicon glyphicon-plus"></span></a>HR Report</h4>
                                    </div>
                                </div>
                                <div class="level1">
                                    <div class="">
                                       <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#massEntry" aria-expanded="false" aria-controls="massEntry"><span class="glyphicon glyphicon-plus"></span></a>Mass Entry/File Upload</h4>
                                    </div>
                                </div>
                                <div class="level1">
                                    <div class="">
                                       <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#report" aria-expanded="false" aria-controls="report"><span class="glyphicon glyphicon-plus"></span></a>Report</h4>
                                    </div>
                                </div>
                                <div class="level1">
                                    <div class="">
                                       <h4 class="panel-title"> <a class="level1" data-toggle="collapse" href="#logOut" aria-expanded="false" aria-controls="logOut"><span class="glyphicon glyphicon-plus"></span></a>Log Out</h4>
                                    </div>
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
    <script>
        $(function () {
            $('.glyphicon').on('click', function () {
                $(this).toggleClass("glyphicon-plus glyphicon-minus");
            });
        });
    </script>
    </body>
</html>
