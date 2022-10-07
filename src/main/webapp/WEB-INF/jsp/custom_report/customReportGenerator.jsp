<%-- 
    Document   : customReport6
    Created on : Feb 15, 2021, 1:55:46 PM
    Author     : damilola-ejalonibu
--%>

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
        
        <style>
         #rul{ list-style-type: none; margin: 0; 
            padding: 0;}
         #rul li { margin: 0 0 3px 0; padding: 0.4em; 
            padding-left: 1.5em; font-size: 12px;}
         .default {
            background: #cedc98;
            border: 1px solid #DDDDDD;
            color: #333333;
         }
         
         #mdaListDiv2 select{
             font-size: 6px;
         }
         .multiselect {
             font-size: 6px !important;
             height: 28px;
             margin-top: 3px;
         }
         /**.multiselect div{
             max-height: 80px !important;
             overflow: scroll !important;
         }**/

         .multiselect-container {
            width: 200%;
            overflow: auto;
         }

         button.multiselect {
            background-color: initial;
            border: 1px solid #ced4da;
            border-radius: 2px;
         }

         .multiselect-option {
            width:100%;
            text-align: left;
         }

         .multiselect-option label{
            font-size: 11px !important;
            font-weight: normal !important;
         }
      </style>
        <title>Custom Report</title>
    </head>
    <body>
        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
                <tr>
                    <td style="border-bottom:none">
                        <div class="title">Custom Report Generator</div>
                    </td>
                </tr>

                <tr style="height:250px" id="top">
                    <td style="border-top:none">
                        <table style="margin-top: 2%; margin-left: 1%;">
                            <tr>
                                <td style="width:1%">
                                    <select style="height:250px; width:230px; font-size:13px" multiple="multiple" id='lstBox1' class="form-control">
                                       <c:forEach items="${rList}" var="names">
                                           <option value="<c:out value="${names.id}"/>"><c:out value="${names.prefDisplayName}"/></option>
                                        </c:forEach>

                                    </select>
                                </td>
                                <td style='width:5%; font-size:13px'>
                                    <input style ="width: 80%; margin-bottom: 5px" type='button' id='btnRight' value ='>'/> <br/>
                                   <!-- <input style ="width: 80%; margin-bottom: 5px" type='button' id='btnAllRight' value ='>>'/> <br/> -->
                                    <input style ="width: 80%; margin-bottom: 5px" type='button' id='btnLeft' value ='<'/>
                                    <!-- <input style ="width: 80%; margin-bottom: 5px" type='button' id='btnAllLeft' value ='<<'/> -->
                                </td>
                                <td style="width:45%">
                                    <select style="height:250px; width:230px; font-size:13px; margin-left: -8px" multiple="multiple" id='lstBox2' class="form-control">

                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <img id="foo" style='margin-top: 5%' src="images/continue_h.png">
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>

                <tr>
                    <td style="border-top:none">
                    <div id="all">
                        <table width="100%">
                            <tr>
                                <td>
                                    <div style="margin-left: 1%" class="row ml-1 mt-1">
                                        <div style="font-size: 12px; text-decoration: underline;" class="col-md-3" id="back">
                                            <a href="">&lt;&lt;--Go Back</a>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <!--<div style="border: 1px solid grey; border-radius:5px; background-color: #E3E0DF; margin-left: 3.6%" class="col-md-3 mt-2 mb-2 pt-2 pl-2" id="udiv">-->
                                         <div style="margin-left: 2%; margin-top:1%;" class="col-md-3" id="udiv">
                                            <p style="font-size:14px">Selected Fields</p>
                                            <ul id="rul"></ul>
                                        </div>
                                    </div>
                                </td>
                            </tr>

                            <tr id="twhole">
                                <td>
                                    <div style="font-size:14px; margin: 2px 0 2px 1px" class="row">
                                        <div class="col-md-4" id="sdiv">
                                            <div class="row">
                                                <div style="margin-top:1%" class="col-md-5">
                                                    <p> Filter Options</p>
                                                </div>
                                                <div style="margin-left: -6%" class="col-md-7">
                                                    <select name="searchBy" style="font-size:12px; width:110%" id="type"  class="browser-default custom-select">
                                                        <option value="-1">Select</option>
                                                        <c:forEach items="${rList}" var="names">
                                                           <option value="<c:out value="${names.columnType}"/>" data-id="<c:out value="${names.id}"/>"><c:out value="${names.prefDisplayName}"/></option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3" id="string">
                                            <select name="searchStrOp" style="font-size:12px; width:100%" id="strOp"  class="browser-default custom-select">
                                                <option value="" disabled>Select Operation</option>
                                                <option id="sl" value="Like">Like</option>
                                                <option id="se" value="Equals">Equals</option>
                                            </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-2 " id="number">
                                            <select name="searchNumOp" style="font-size:12px; width:110%" id="numOp"  class="browser-default custom-select" placeholder"select">
                                                <option value="" disabled>Select Operation</option>
                                                <option id="ne" value="Equals">Equals</option>
                                                <option id="ng" value="Greater Than">Greater Than</option>
                                                <option id="nl" value="Less Than">Less Than</option>
                                                <option id="nb" value="Between">Between</option>
                                            </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-2 " id="date">
                                            <select name="searchDateOp" style="font-size:12px; width:110%" id="dOp"  class="browser-default custom-select" placeholder"select">
                                                <option value="" disabled>Select Operation</option>
                                                <option id="de" value="Equals">Equals</option>
                                                <option id="dg" value="Greater Than">Greater Than</option>
                                                <option id="dl" value="Less Than">Less Than</option>
                                                <option id="db" value="Between">Between</option>
                                            </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="mda">
                                            <select name="searchOp" style="font-size:12px; width:90%" id="mdaOp"  class="browser-default custom-select" placeholder"select">
                                                <option value="" disabled>Select Operation</option>
                                                <option id="me" value="Equals">Equals</option>
                                                <option id="mi" value="In">In</option>
                                            </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="empt">
                                            <select name="searchOp" style="font-size:12px; width:90%" id="emptOp"  class="browser-default custom-select" placeholder"select">
                                                <option value="" disabled>Select Operation</option>
                                                <option id="le" value="Equals">Equals</option>
                                                <option id="li" value="In">In</option>
                                            </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="bank">
                                           <select name="searchOp" style="font-size:12px; width:90%" id="bankOp"  class="browser-default custom-select">
                                              <option value="" disabled>Select Operation</option>
                                              <option id="be" value="Equals">Equals</option>
                                              <option id="bi" value="In">In</option>
                                           </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="state">
                                           <select name="searchOp" style="font-size:12px; width:90%" id="stateOp"  class="browser-default custom-select">
                                              <option value="" disabled>Select Operation</option>
                                              <option id="ste" value="Equals">Equals</option>
                                              <option id="sti" value="In">In</option>
                                           </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="marital">
                                           <select name="searchOp" style="font-size:12px; width:90%" id="maritalOp"  class="browser-default custom-select">
                                              <option value="" disabled>Select Operation</option>
                                              <option id="mse" value="Equals">Equals</option>
                                              <option id="msi" value="In">In</option>
                                           </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="loan">
                                           <select name="searchOp" style="font-size:12px; width:90%" id="loanOp"  class="browser-default custom-select">
                                               <option value="" disabled>Select Operation</option>
                                               <option id="ge" value="Equals">Equals</option>
                                               <option id="gi" value="In">In</option>
                                           </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="salary">
                                           <select name="searchOp" style="font-size:12px; width:90%" id="salaryOp"  class="browser-default custom-select">
                                                <option value="" disabled>Select Operation</option>
                                                <option id="pe" value="Equals">Equals</option>
                                                <option id="pi" value="In">In</option>
                                           </select>
                                        </div>

                                        <div style="margin-top:3px" class="col-md-3 " id="pfa">
                                           <select name="searchOp" style="font-size:12px; width:90%" id="pfaOp"  class="browser-default custom-select">
                                              <option value="" disabled>Select Operation</option>
                                              <option id="pfe" value="Equals">Equals</option>
                                              <option id="pfi" value="In">In</option>
                                           </select>
                                        </div>

                                        <div style="padding-right: 7%; margin-top:4px" class="col-md-3 mb-1" id="oneInput">
                                            <input name="searchStr" style="border-radius:5px; height: 27px; width:200px; border:1px solid #ced4da; font-size:12px; color:#495057" id='searchStr' type='text' value=''/>
                                        </div>

                                        <div style="margin-top:4px" class="col-md-4" id="twoInput">
                                           <input name="search1" style="border-radius:5px; height: 27px; width:130px; border:1px solid #ced4da; font-size:12px; color:#495057" id='searchNum1'  value=''/>
                                           <input name="search2" style="border-radius:5px; height: 27px; width:130px; border:1px solid #ced4da; font-size:12px; color:#495057" id='searchNum2'  value=''/>
                                        </div>

                                        <div style="padding-right: 7%; margin-top:4px" class="col-md-3 mb-1" id="oneDateInput">
                                            <input name="searchDate" style="border-radius:5px; height: 27px; width:200px;  border:1px solid #ced4da; font-size:12px; color:#495057" id='searchStrd' type='date' value=''/>
                                        </div>

                                        <div style="margin-top:4px" class="col-md-4" id="twoDateInput">
                                            <input name="searchDate1" style="border-radius:5px; height: 27px; width:130px; border:1px solid #ced4da; font-size:12px; color:#495057" id='searchStr1' type='date' value=''/>
                                            <input name="searchDate2" style="border-radius:5px; height: 27px; width:130px; border:1px solid #ced4da;font-size:12px; color:#495057" id='searchStr2' type='date' value=''/>
                                        </div>

                                        <div style="margin-top:4px" class="col-md-3 " id="mdaListDiv">
                                            <select name="searchMda" style="font-size:12px; width:100%" id="mdaList"  class="browser-default custom-select">
                                                <option value="" disabled>Select</option>
                                                <c:forEach items="${mList}" var="names">
                                                   <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="col-md-3 " id="mdaListDiv2">
                                            <select name="searchMda2" id="mdaList2" multiple="multiple" data-placeholder="Select MDA..">
                                                <c:forEach items="${mList}" var="names">
                                                   <option style="text-align:center" value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div style="margin-top:4px" class="col-md-3 " id="emptListDiv">
                                           <select name="searchEmpt" style="font-size:12px; width:100%" id="emptList"  class="browser-default custom-select">
                                              <option value="" disabled>Select</option>
                                              <c:forEach items="${eList}" var="names">
                                                <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                              </c:forEach>
                                           </select>
                                        </div>

                                        <div class="col-md-3 " id="emptListDiv2">
                                           <select name="searchEmpt2"  id="emptList2" multiple="multiple" data-placeholder="Select Employee Type.." class="browser-default custom-select ms">
                                              <c:forEach items="${eList}" var="names">
                                                <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                              </c:forEach>
                                           </select>
                                        </div>

                                        <div style="margin-top:4px" class="col-md-3" id="bankListDiv">
                                            <select name="searchBank" style="font-size:12px; width:100%" id="bankList"  class="browser-default custom-select" placeholder"select">
                                                <option value="" disabled>Select</option>
                                                <c:forEach items="${bList}" var="names">
                                                    <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <div class="col-md-3 " id="bankListDiv2">
                                           <select name="searchBank2"  id="bankList2" multiple="multiple" data-placeholder="Select Bank.." class="browser-default custom-select ms">
                                             <c:forEach items="${bList}" var="names">
                                               <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                             </c:forEach>
                                           </select>
                                        </div>

                                         <div style="margin-top:4px" class="col-md-3" id="stateListDiv">
                                            <select name="searchState" style="font-size:12px; width:100%" id="stateList"  class="browser-default custom-select">
                                               <option value="" disabled>Select</option>
                                               <c:forEach items="${stList}" var="names">
                                                  <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div class="col-md-3 " id="stateListDiv2">
                                            <select name="searchState2"  id="stateList2" multiple="multiple" data-placeholder="Select State.." class="browser-default custom-select ms">
                                               <c:forEach items="${stList}" var="names">
                                                  <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>

                                         <div style="margin-top:4px" class="col-md-3" id="maritalListDiv">
                                            <select name="searchMarital" style="font-size:12px; width:100%" id="maritalList"  class="browser-default custom-select">
                                               <option value="" disabled>Select</option>
                                               <c:forEach items="${msList}" var="names">
                                                  <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div class="col-md-3 " id="maritalListDiv2">
                                            <select name="searchMarital2"  id="maritalList2" multiple="multiple" data-placeholder="Select Status.." class="browser-default custom-select ms">
                                               <c:forEach items="${msList}" var="names">
                                                  <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div style="margin-top:4px" class="col-md-3" id="loanListDiv">
                                            <select name="searchLoan" style="font-size:12px; width:100%" id="loanList"  class="browser-default custom-select">
                                               <option value="" disabled>Select</option>
                                               <c:forEach items="${gList}" var="names">
                                                  <option value="<c:out value="${names.description}"/>"><c:out value="${names.description}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div class="col-md-3 " id="loanListDiv2">
                                            <select name="searchLoan2"  id="loanList2" multiple="multiple" data-placeholder="Select Loan Type.." class="browser-default custom-select ms">
                                               <c:forEach items="${gList}" var="names">
                                                  <option value="<c:out value="${names.id}"/>"><c:out value="${names.description}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div style="margin-top:4px" class="col-md-3" id="salaryListDiv">
                                            <select name="searchSalary" style="font-size:12px; width:100%" id="salaryList"  class="browser-default custom-select">
                                               <option value="" disabled>Select</option>
                                               <c:forEach items="${sList}" var="names">
                                                 <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div class="col-md-3 " id="salaryListDiv2">
                                            <select name="searchSalary2"  id="salaryList2" multiple="multiple" data-placeholder="Select Pay Group.." class="browser-default custom-select ms">
                                               <c:forEach items="${sList}" var="names">
                                                  <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div style="margin-top:4px" class="col-md-3" id="pfaListDiv">
                                            <select name="searchPfa" style="font-size:12px; width:100%" id="pfaList"  class="browser-default custom-select">
                                               <option value="" disabled>Select</option>
                                               <c:forEach items="${pList}" var="names">
                                                 <option value="<c:out value="${names.name}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                         <div class="col-md-3 " id="pfaListDiv2">
                                            <select name="searchPfa2"  id="pfaList2" multiple="multiple" data-placeholder="Select PFA.." class="browser-default custom-select ms">
                                               <c:forEach items="${pList}" var="names">
                                                 <option value="<c:out value="${names.id}"/>"><c:out value="${names.name}"/></option>
                                               </c:forEach>
                                            </select>
                                         </div>
                                        <div class="col-md-2" id="strOpAdd">
                                            <input  type="image"  value="add" title="Add" class="" id='add1' src="images/add.png">
                                           <!-- <input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px" type='button' id='add1' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="numOpAdd">
                                            <input  type="image"  value="add" title="Add" class="" id='add2' src="images/add.png">
                                            <!--<input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px" type='button' id='add2' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="dOpAdd">
                                            <input  type="image" value="add" title="Add" class="" id='add3' src="images/add.png">
                                            <!--<input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px;" type='button' id='add3' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="mdaOpAdd">
                                            <input  type="image"  value="add" title="Add" class="" id='add4' src="images/add.png">
                                            <!--<input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px;" type='button' id='add4' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="eOpAdd">
                                            <input type="image"  value="add" title="Add" class="" id='add5' src="images/add.png">
                                            <!-- <input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px;" type='button' id='add5' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="bOpAdd">
                                           <input  type="image"  value="add" title="Add" class="" id='addB' src="images/add.png">
                                           <!-- <input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px;" type='button' id='add6' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="stOpAdd">
                                           <input type="image"  value="add" title="Add" class="" id='addSt' src="images/add.png">
                                           <!-- <input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px;" type='button' id='add7' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="msOpAdd">
                                           <input type="image"  value="add" title="Add" class="" id='addMs' src="images/add.png">
                                           <!-- <input style ="width:60px; border: 1px solid #ced4da; border-radius: 5px; height: 30px; font-size:12px;" type='button' id='add8' value ='Add'/>-->
                                        </div>

                                        <div class="col-md-2" id="ltOpAdd">
                                           <input  type="image"  value="add" title="Add" class="" id='addLt' src="images/add.png">
                                        </div>

                                        <div class="col-md-2" id="satOpAdd">
                                           <input  type="image"  value="add" title="Add" class="" id='addSat' src="images/add.png">
                                        </div>

                                        <div class="col-md-2" id="pfaOpAdd">
                                           <input type="image"  value="add" title="Add" class="" id='addPfa' src="images/add.png">
                                        </div>

                                    </div>
                                </td>
                            </tr>
                            <tr id="tfilter">
                                <td>
                                    <div id="fdiv">
                                        <div style="margin: 2% 4% 0 2%" class="row ml-3 mr-4 mt-2">
                                            <table width="75%" id ='ftable' class="table table-bordered">
                                                <tr class="thead-light" style="height: 30px; font-size:12px;">
                                                    <th scope="col"></th>
                                                    <th width="100px" scope="col">Search By</th>
                                                    <th width="200px" scope="col">Search Operation</th>
                                                    <th scope="col">Search String</th>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="col-md-3 mt-2">
                                            <img id="delete" style='margin-top: 5%' src="images/delete_h.png">
                                            <img id="go" style='margin-top: 5%' src="images/proceed.png">
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div class="row ml-3 mr-4 mt-2" id="rdiv">
                                        <table id ='rtable' class="table table-hover">
                                            <thead class="thead-light">
                                                <tr style="background-color: #e9ecef; border-color: #dee2e6;" id="rtr"></tr>
                                            </thead>
                                            <tbody>

                                            </tbody>
                                        </table>
                                    </div>
                                </td>
                            </tr>
                        </table>
                        <div class="modal" id="myModal" tabindex="-1" role="dialog">
                          <div class="modal-dialog" role="document">
                            <div class="modal-content">
                              <div class="modal-body">
                                <p style="font-size:12px;"><b>Your query is unbalanced, you need to filter by Pay Period</b></p>
                              </div>
                              <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                              </div>
                            </div>
                          </div>
                        </div>
                        </div>
                    </td>
                </tr>
                <!-- <tr>
                    <td style="background-color: #33C0C8; font-size: 12px; text-align: center">
                        ©2020-2021 GNL Systems Ltd.
                    </td>
                </tr> -->
                <tr>
                   <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
                </tr>
            </table>
        <script src="scripts/jquery-3.4.1.min.js"></script>
        <script src="scripts/bootstrap-multiselect.js"></script>
        <script src="scripts/jquery-ui.js"></script>


        <script>
             $(document).ready(function() {
                $('#mdaList2').multiselect({
                    //includeSelectAllOption: true,
                    //enableFiltering: true,
                    maxHeight: 350,
                    buttonWidth : '100%',
                    dropUp: true
                });
                $('#bankList2').multiselect({
                  //includeSelectAllOption: true,
                  maxHeight: 350,
                  buttonWidth : '100%',
                  dropUp: true
                });
                $('#stateList2').multiselect({
                   //includeSelectAllOption: true,
                   maxHeight: 350,
                   buttonWidth : '100%',
                   dropUp: true
                });
                $('#emptList2').multiselect({
                   //includeSelectAllOption: true,
                   maxHeight: 350,
                   buttonWidth : '100%',
                   dropUp: true
                });
                $('#maritalList2').multiselect({
                   //includeSelectAllOption: true,
                   maxHeight: 350,
                   buttonWidth : '100%',
                   dropUp: true
                });
                $('#loanList2').multiselect({
                   //includeSelectAllOption: true,
                   maxHeight: 350,
                   buttonWidth : '100%',
                   dropUp: true
                });
                $('#salaryList2').multiselect({
                   //includeSelectAllOption: true,
                   maxHeight: 350,
                   buttonWidth : '100%',
                   dropUp: true
                });
                $('#pfaList2').multiselect({
                   //includeSelectAllOption: true,
                   maxHeight: 350,
                   buttonWidth : '100%',
                   dropUp: true
                });
             });
        </script>

        <script>
         $(function() {
            $( "#rul").sortable();
         });
      </script>

<script>
    $('#all').css("display", "none");

     $('#foo').click(function(e) {
            var ol = $('#lstBox2 option').length;
            console.log("ol is "+ol);
            if(ol > 12){
                alert("You can only select a maximum of 12 fields per time");
            }
            else if(ol < 3){
                alert("You can only select a minimum of 3 fields per time");
            }
            else if(ol == 0){
                alert("You have not made any selection");
            }
            else{
                $('#all').css("display", "block");
                $('#top').css("display", "none");
                var values = $.map($('#lstBox2 option'), function(e) { return e.text; });
                    // as a comma separated string
                values.join(',');
                console.log("values are "+values);

                console.log("values length is "+values.length);
                console.log("value is "+values[0]);
                for (let i = 0; i < values.length; i++){
                    $('#rul').append(
                        '<li class="default">'+values[i]+'</li>'
                    );
                }
                $('#udiv').css("display", "block");
            }
            $('#fdiv').css("display", "none");
            if(!$('#fdiv').is(':visible')){
                console.log("Hello if");
               $('#delete').css("display", "none");
            }
     });
 </script>

  <script>
    $(document).ready(function() {
        $('#btnRight').click(function(e) {
            var selectedOpts = $('#lstBox1 option:selected');
            if (selectedOpts.length == 0) {
                alert("Nothing to move.");
                e.preventDefault();
            }
            else if(selectedOpts.length > 12){
            alert("You can only select a maximum of 12 fields per time");
            e.preventDefault();
        }
        else{
        $('#lstBox2').append($(selectedOpts).clone());
        $(selectedOpts).remove();
        e.preventDefault();
        }
    });
    $('#btnLeft').click(function(e) {
        var selectedOpts = $('#lstBox2 option:selected');
        if (selectedOpts.length == 0) {
            alert("Nothing to move.");
            e.preventDefault();
        }
        $('#lstBox1').append($(selectedOpts).clone());
        $(selectedOpts).remove();
        e.preventDefault();
    });

  /**  $("#btnAllRight").click(function (e) {
    var selectedOpts = $("#lstBox1 option");
    if (selectedOpts.length == 0) {
      alert("Nothing to move.");
      e.preventDefault();
    }
    $("#lstBox2").append($(selectedOpts).clone());
    $(selectedOpts).remove();
    e.preventDefault();
  });

  $("#btnAllLeft").click(function (e) {
    var selectedOpts = $("#lstBox2 option");
    if (selectedOpts.length == 0) {
      alert("Nothing to move.");
      e.preventDefault();
    }
    $("#lstBox1").append($(selectedOpts).clone());
    $(selectedOpts).remove();
    e.preventDefault();
  });  **/

});

</script>

<script>
            $('#rdiv').css("display", "none");
            $('#fdiv').css("display", "none");
            $('#string').css("display", "none");
            $('#number').css("display", "none");
            $('#date').css("display", "none");
            $('#mda').css("display", "none");
            $('#mdaListDiv').css("display", "none");
            $('#mdaListDiv2').css("display", "none");
            $('#empt').css("display", "none");
            $('#emptListDiv').css("display", "none");
            $('#emptListDiv2').css("display", "none");
            $('#bank').css("display", "none");
            $('#bankListDiv').css("display", "none");
            $('#bankListDiv2').css("display", "none");
            $('#state').css("display", "none");
            $('#stateListDiv').css("display", "none");
            $('#stateListDiv2').css("display", "none");
            $('#marital').css("display", "none");
            $('#maritalListDiv').css("display", "none");
            $('#maritalListDiv2').css("display", "none");
            $('#loan').css("display", "none");
            $('#loanListDiv').css("display", "none");
            $('#loanListDiv2').css("display", "none");
            $('#salary').css("display", "none");
            $('#salaryListDiv').css("display", "none");
            $('#salaryListDiv2').css("display", "none");
            $('#pfa').css("display", "none");
            $('#pfaListDiv').css("display", "none");
            $('#pfaListDiv2').css("display", "none");
            $('#strOpAdd').css("display", "none");
            $('#numOpAdd').css("display", "none");
            $('#dOpAdd').css("display", "none");
            $('#mdaOpAdd').css("display", "none");
            $('#eOpAdd').css("display", "none");
            $('#bOpAdd').css("display", "none");
            $('#stOpAdd').css("display", "none");
            $('#msOpAdd').css("display", "none");
            $('#ltOpAdd').css("display", "none");
            $('#satOpAdd').css("display", "none");
            $('#pfaOpAdd').css("display", "none");
            $('#oneInput').css("display", "none");
            $('#twoInput').css("display", "none");
            $('#oneDateInput').css("display", "none");
            $('#twoDateInput').css("display", "none");
</script>

<script>
            $(function() {
                $('#type').change(function(){
                    $('#strOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#numOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#dOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#mdaOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#emptOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#bankOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#stateOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#maritalOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#loanOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#salaryOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#pfaOp').prop('selected', false).find('option:first').prop('selected', true);
                    $('#mdaList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#mdaList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#emptList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#emptList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#bankList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#bankList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#stateList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#stateList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#maritalList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#maritalList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#loanList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#loanList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#salaryList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#salaryList2').prop('selected', false).find('option:first').prop('selected', false);
                    $('#pfaList').prop('selected', false).find('option:first').prop('selected', true);
                    $('#pfaList2').prop('selected', false).find('option:first').prop('selected', false);

                    var searchBy = $('#type').val();
                    var searchByText = $('#type option:selected').text();
                    console.log("searchby is "+searchBy);
                    console.log("searchByText is "+searchByText);
                    $('#oneInput').css("display", "none");
                    $('#twoInput').css("display", "none");
                    $('#oneDateInput').css("display", "none");
                    $('#twoDateInput').css("display", "none");
                    $('#mdaListDiv').css("display", "none");
                    $('#mdaListDiv2').css("display", "none");
                    $('#emptListDiv').css("display", "none");
                    $('#emptListDiv2').css("display", "none");
                    $('#bankListDiv').css("display", "none");
                    $('#bankListDiv2').css("display", "none");
                    $('#stateListDiv').css("display", "none");
                    $('#stateListDiv2').css("display", "none");
                    $('#maritalListDiv').css("display", "none");
                    $('#maritalListDiv2').css("display", "none");
                    $('#loanListDiv').css("display", "none");
                    $('#loanListDiv2').css("display", "none");
                    $('#salaryListDiv').css("display", "none");
                    $('#salaryListDiv2').css("display", "none");
                    $('#pfaListDiv').css("display", "none");
                    $('#pfaListDiv2').css("display", "none");
                    $('#rdiv').css("display", "none");
                    $('#searchStr').val('');
                    $('#searchStr1').val('');
                    $('#searchStr2').val('');

                    console.log("searchBy is "+searchBy);
                    console.log("searchByText is "+searchByText);
                    if(searchBy =='String'){
                        $('#string').css("display", "block");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy == 'Integer') || (searchBy == 'Double')){
                        $('#string').css("display", "none");
                        $('#number').css("display", "block");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if (searchBy == 'LocalDate'){
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "block");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText==="Bank Name")){
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "block");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText==="MDA Name")){
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "block");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText==="State Name")){
                       $('#string').css("display", "none");
                       $('#number').css("display", "none");
                       $('#date').css("display", "none");
                       $('#mda').css("display", "none");
                       $('#empt').css("display", "none");
                       $('#bank').css("display", "none");
                       $('#state').css("display", "block");
                       $('#marital').css("display", "none");
                       $('#loan').css("display", "none");
                       $('#salary').css("display", "none");
                       $('#pfa').css("display", "none");
                       $('#strOpAdd').css("display", "none");
                       $('#numOpAdd').css("display", "none");
                       $('#dOpAdd').css("display", "none");
                       $('#mdaOpAdd').css("display", "none");
                       $('#eOpAdd').css("display", "none");
                       $('#bOpAdd').css("display", "none");
                       $('#stOpAdd').css("display", "none");
                       $('#msOpAdd').css("display", "none");
                       $('#ltOpAdd').css("display", "none");
                       $('#satOpAdd').css("display", "none");
                       $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && ((searchByText==="Employee Type") || (searchByText==="Pensioner Type"))){
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "block");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText==="Marital Status")){
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "block");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText === "Loan Type")){
                        console.log("I am inside loan type");
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "block");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText === "Pay Group")){
                        console.log("I am inside salary type");
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "block");
                        $('#pfa').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                    else if ((searchBy === "ce") && (searchByText === "PFA Name")){
                        console.log("I am inside pfa");
                        $('#string').css("display", "none");
                        $('#number').css("display", "none");
                        $('#date').css("display", "none");
                        $('#mda').css("display", "none");
                        $('#empt').css("display", "none");
                        $('#bank').css("display", "none");
                        $('#state').css("display", "none");
                        $('#marital').css("display", "none");
                        $('#loan').css("display", "none");
                        $('#salary').css("display", "none");
                        $('#pfa').css("display", "block");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                    }
                });
            });

</script>


        <script>
           $(function() {
                $('#strOp').change(function(){
                    $('#oneInput').css("display", "block");
                    $('#twoInput').css("display", "none");
                    $('#oneDateInput').css("display", "none");
                    $('#twoDateInput').css("display", "none");
                    $('#strOpAdd').css("display", "block");
                    $('#numOpAdd').css("display", "none");
                    $('#dOpAdd').css("display", "none");
                    $('#mdaOpAdd').css("display", "none");
                    $('#mdaListDiv').css("display", "none");
                    $('#mdaListDiv2').css("display", "none");
                    $('#bOpAdd').css("display", "none");
                    $('#bankListDiv').css("display", "none");
                    $('#bankListDiv2').css("display", "none");
                    $('#stOpAdd').css("display", "none");
                    $('#stateListDiv').css("display", "none");
                    $('#stateListDiv2').css("display", "none");
                    $('#eOpAdd').css("display", "none");
                    $('#emptListDiv').css("display", "none");
                    $('#emptListDiv2').css("display", "none");
                    $('#msOpAdd').css("display", "none");
                    $('#maritalListDiv').css("display", "none");
                    $('#maritalListDiv2').css("display", "none");
                    $('#ltOpAdd').css("display", "none");
                    $('#loanListDiv').css("display", "none");
                    $('#loanListDiv2').css("display", "none");
                    $('#satOpAdd').css("display", "none");
                    $('#salaryListDiv').css("display", "none");
                    $('#salaryListDiv2').css("display", "none");
                    $('#pfaOpAdd').css("display", "none");
                    $('#pfaListDiv').css("display", "none");
                    $('#pfaListDiv2').css("display", "none");
                    var sr = $('#strOp').val();
                    console.log("sr is "+sr);
                });
           });

            $(function() {
                $('#numOp').change(function(){
                   var nr = $('#numOp').val();
                   console.log("nr is "+nr);
                   if((nr=='Equals') || (nr=='Greater Than') || (nr=='Less Than')){
                        $('#oneInput').css("display", "block");
                        $('#twoInput').css("display", "none");
                        $('#oneDateInput').css("display", "none");
                        $('#twoDateInput').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "block");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "none");
                        $('#mdaListDiv').css("display", "none");
                        $('#mdaListDiv2').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#bankListDiv').css("display", "none");
                        $('#bankListDiv2').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#stateListDiv').css("display", "none");
                        $('#stateListDiv2').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#emptListDiv').css("display", "none");
                        $('#emptListDiv2').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#maritalListDiv').css("display", "none");
                        $('#maritalListDiv2').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#loanListDiv').css("display", "none");
                        $('#loanListDiv2').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#salaryListDiv').css("display", "none");
                        $('#salaryListDiv2').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                        $('#pfaListDiv').css("display", "none");
                        $('#pfaListDiv2').css("display", "none");
                   }
                   else if(nr=='Between'){
                       $('#oneInput').css("display", "none");
                       $('#twoInput').css("display", "block");
                       $('#oneDateInput').css("display", "none");
                       $('#twoDateInput').css("display", "none");
                       $('#strOpAdd').css("display", "none");
                       $('#numOpAdd').css("display", "block");
                       $('#dOpAdd').css("display", "none");
                       $('#mdaOpAdd').css("display", "none");
                       $('#mdaListDiv').css("display", "none");
                       $('#mdaListDiv2').css("display", "none");
                       $('#bOpAdd').css("display", "none");
                       $('#bankListDiv').css("display", "none");
                       $('#bankListDiv2').css("display", "none");
                       $('#stOpAdd').css("display", "none");
                       $('#stateListDiv').css("display", "none");
                       $('#stateListDiv2').css("display", "none");
                       $('#eOpAdd').css("display", "none");
                       $('#emptListDiv').css("display", "none");
                       $('#emptListDiv2').css("display", "none");
                       $('#msOpAdd').css("display", "none");
                       $('#maritalListDiv').css("display", "none");
                       $('#maritalListDiv2').css("display", "none");
                       $('#ltOpAdd').css("display", "none");
                       $('#loanListDiv').css("display", "none");
                       $('#loanListDiv2').css("display", "none");
                       $('#satOpAdd').css("display", "none");
                       $('#salaryListDiv').css("display", "none");
                       $('#salaryListDiv2').css("display", "none");
                       $('#pfaOpAdd').css("display", "none");
                       $('#pfaListDiv').css("display", "none");
                       $('#pfaListDiv2').css("display", "none");
                   }
                });
            });

            $(function() {
                $('#dOp').change(function(){
                    var dr = $('#dOp').val();
                    console.log("dr is "+dr);

                    if((dr=='Equals') || (dr=='Greater Than') || (dr=='Less Than')){
                        $('#oneInput').css("display", "none");
                        $('#twoInput').css("display", "none");
                        $('#oneDateInput').css("display", "block");
                        $('#twoDateInput').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "block");
                        $('#mdaOpAdd').css("display", "none");
                        $('#mdaListDiv').css("display", "none");
                        $('#mdaListDiv2').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#bankListDiv').css("display", "none");
                        $('#bankListDiv2').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#stateListDiv').css("display", "none");
                        $('#stateListDiv2').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#emptListDiv').css("display", "none");
                        $('#emptListDiv2').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#maritalListDiv').css("display", "none");
                        $('#maritalListDiv2').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#loanListDiv').css("display", "none");
                        $('#loanListDiv2').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#salaryListDiv').css("display", "none");
                        $('#salaryListDiv2').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                        $('#pfaListDiv').css("display", "none");
                        $('#pfaListDiv2').css("display", "none");
                    }
                    else if(dr=='Between'){
                        $('#oneInput').css("display", "none");
                        $('#twoInput').css("display", "none");
                        $('#oneDateInput').css("display", "none");
                        $('#twoDateInput').css("display", "block");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "block");
                        $('#mdaOpAdd').css("display", "none");
                        $('#mdaListDiv').css("display", "none");
                        $('#mdaListDiv2').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#bankListDiv').css("display", "none");
                        $('#bankListDiv2').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#stateListDiv').css("display", "none");
                        $('#stateListDiv2').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#emptListDiv').css("display", "none");
                        $('#emptListDiv2').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#maritalListDiv').css("display", "none");
                        $('#maritalListDiv2').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#loanListDiv').css("display", "none");
                        $('#loanListDiv2').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#salaryListDiv').css("display", "none");
                        $('#salaryListDiv2').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                        $('#pfaListDiv').css("display", "none");
                        $('#pfaListDiv2').css("display", "none");
                    }
                });
            });

            $(function() {
                $('#mdaOp').change(function(){
                    var mr = $('#mdaOp').val();
                    console.log("mr is "+mr);
                    // $('#mdaList2').prop('selected', false).find('option:first').prop('selected', true);
                    $('#mdaList').prop('selected', false).find('option:first').prop('selected', true);
                    if(mr=='Equals'){
                        $('#oneInput').css("display", "none");
                        $('#twoInput').css("display", "none");
                        $('#oneDateInput').css("display", "none");
                        $('#twoDateInput').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "block");
                        $('#mdaListDiv').css("display", "block");
                        $('#mdaListDiv2').css("display", "none");
                        $('#bOpAdd').css("display", "none");
                        $('#bankListDiv').css("display", "none");
                        $('#bankListDiv2').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#stateListDiv').css("display", "none");
                        $('#stateListDiv2').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#emptListDiv').css("display", "none");
                        $('#emptListDiv2').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#maritalListDiv').css("display", "none");
                        $('#maritalListDiv2').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#loanListDiv').css("display", "none");
                        $('#loanListDiv2').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#salaryListDiv').css("display", "none");
                        $('#salaryListDiv2').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                        $('#pfaListDiv').css("display", "none");
                        $('#pfaListDiv2').css("display", "none");
                    }

                    else if(mr=='In'){
                        $('#oneInput').css("display", "none");
                        $('#twoInput').css("display", "none");
                        $('#oneDateInput').css("display", "none");
                        $('#twoDateInput').css("display", "none");
                        $('#strOpAdd').css("display", "none");
                        $('#numOpAdd').css("display", "none");
                        $('#dOpAdd').css("display", "none");
                        $('#mdaOpAdd').css("display", "block");
                        $('#mdaListDiv').css("display", "none");
                        $('#mdaListDiv2').css("display", "block");
                        $('#bOpAdd').css("display", "none");
                        $('#bankListDiv').css("display", "none");
                        $('#bankListDiv2').css("display", "none");
                        $('#stOpAdd').css("display", "none");
                        $('#stateListDiv').css("display", "none");
                        $('#stateListDiv2').css("display", "none");
                        $('#eOpAdd').css("display", "none");
                        $('#emptListDiv').css("display", "none");
                        $('#emptListDiv2').css("display", "none");
                        $('#msOpAdd').css("display", "none");
                        $('#maritalListDiv').css("display", "none");
                        $('#maritalListDiv2').css("display", "none");
                        $('#ltOpAdd').css("display", "none");
                        $('#loanListDiv').css("display", "none");
                        $('#loanListDiv2').css("display", "none");
                        $('#satOpAdd').css("display", "none");
                        $('#salaryListDiv').css("display", "none");
                        $('#salaryListDiv2').css("display", "none");
                        $('#pfaOpAdd').css("display", "none");
                        $('#pfaListDiv').css("display", "none");
                        $('#pfaListDiv2').css("display", "none");
                    }
                });
            });

            $(function() {
                $('#emptOp').change(function(){
                    var er = $('#emptOp').val();
                    console.log("er is "+er);
                    $('#emptList').prop('selected', false).find('option:first').prop('selected', true);
                    if(er=='Equals'){
                    $('#oneInput').css("display", "none");
                    $('#twoInput').css("display", "none");
                    $('#oneDateInput').css("display", "none");
                    $('#twoDateInput').css("display", "none");
                    $('#strOpAdd').css("display", "none");
                    $('#numOpAdd').css("display", "none");
                    $('#dOpAdd').css("display", "none");
                    $('#mdaOpAdd').css("display", "none");
                    $('#mdaListDiv').css("display", "none");
                    $('#mdaListDiv2').css("display", "none");
                    $('#bOpAdd').css("display", "none");
                    $('#bankListDiv').css("display", "none");
                    $('#bankListDiv2').css("display", "none");
                    $('#stOpAdd').css("display", "none");
                    $('#stateListDiv').css("display", "none");
                    $('#stateListDiv2').css("display", "none");
                    $('#eOpAdd').css("display", "block");
                    $('#emptListDiv').css("display", "block");
                    $('#emptListDiv2').css("display", "none");
                    $('#msOpAdd').css("display", "none");
                    $('#maritalListDiv').css("display", "none");
                    $('#maritalListDiv2').css("display", "none");
                    $('#ltOpAdd').css("display", "none");
                    $('#loanListDiv').css("display", "none");
                    $('#loanListDiv2').css("display", "none");
                    $('#satOpAdd').css("display", "none");
                    $('#salaryListDiv').css("display", "none");
                    $('#salaryListDiv2').css("display", "none");
                    $('#pfaOpAdd').css("display", "none");
                    $('#pfaListDiv').css("display", "none");
                    $('#pfaListDiv2').css("display", "none");
                    }

                    else if(er=='In'){
                    $('#oneInput').css("display", "none");
                    $('#twoInput').css("display", "none");
                    $('#oneDateInput').css("display", "none");
                    $('#twoDateInput').css("display", "none");
                    $('#strOpAdd').css("display", "none");
                    $('#numOpAdd').css("display", "none");
                    $('#dOpAdd').css("display", "none");
                    $('#mdaOpAdd').css("display", "none");
                    $('#mdaListDiv').css("display", "none");
                    $('#mdaListDiv2').css("display", "none");
                    $('#bOpAdd').css("display", "none");
                    $('#bankListDiv').css("display", "none");
                    $('#bankListDiv2').css("display", "none");
                    $('#stOpAdd').css("display", "none");
                    $('#stateListDiv').css("display", "none");
                    $('#stateListDiv2').css("display", "none");
                    $('#eOpAdd').css("display", "block");
                    $('#emptListDiv').css("display", "none");
                    $('#emptListDiv2').css("display", "block");
                    $('#msOpAdd').css("display", "none");
                    $('#maritalListDiv').css("display", "none");
                    $('#maritalListDiv2').css("display", "none");
                    $('#ltOpAdd').css("display", "none");
                    $('#loanListDiv').css("display", "none");
                    $('#loanListDiv2').css("display", "none");
                    $('#satOpAdd').css("display", "none");
                    $('#salaryListDiv').css("display", "none");
                    $('#salaryListDiv2').css("display", "none");
                    $('#pfaOpAdd').css("display", "none");
                    $('#pfaListDiv').css("display", "none");
                    $('#pfaListDiv2').css("display", "none");
                }

                });
            });
             $(function() {
                $('#bankOp').change(function(){
                      var br = $('#bankOp').val();
                      console.log("br is "+br);
                      $('#bankList').prop('selected', false).find('option:first').prop('selected', true);
                      if(br=='Equals'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "block");
                         $('#bankListDiv').css("display", "block");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                      else if(br=='In'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "block");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "block");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                });
             });
             $(function() {
                $('#stateOp').change(function(){
                      var st = $('#stateOp').val();
                      console.log("st is "+st);
                      $('#stateList').prop('selected', false).find('option:first').prop('selected', true);
                      if(st=='Equals'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "block");
                         $('#stateListDiv').css("display", "block");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                      else if(st=='In'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "block");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "block");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                });
             });
             $(function() {
                $('#maritalOp').change(function(){
                      var mt = $('#maritalOp').val();
                      console.log("mt is "+mt);
                      $('#maritalList').prop('selected', false).find('option:first').prop('selected', true);
                      if(mt=='Equals'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "block");
                         $('#maritalListDiv').css("display", "block");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                      else if(mt=='In'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "block");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "block");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                });
             });
             $(function() {
                $('#loanOp').change(function(){
                      var lr = $('#loanOp').val();
                      console.log("lr is "+lr);
                      $('#loanList').prop('selected', false).find('option:first').prop('selected', true);
                      if(lr=='Equals'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "block");
                         $('#loanListDiv').css("display", "block");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                      else if(lr=='In'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "block");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "block");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                });
             });
             $(function() {
                $('#salaryOp').change(function(){
                      var sr = $('#salaryOp').val();
                      console.log("sr is "+sr);
                      $('#salaryList').prop('selected', false).find('option:first').prop('selected', true);
                      if(sr=='Equals'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "block");
                         $('#salaryListDiv').css("display", "block");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                      else if(sr=='In'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "block");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "block");
                         $('#pfaOpAdd').css("display", "none");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "none");
                      }
                });
             });
             $(function() {
                $('#pfaOp').change(function(){
                      var pr = $('#pfaOp').val();
                      console.log("pr is "+pr);
                      $('#pfaList').prop('selected', false).find('option:first').prop('selected', true);
                      if(pr=='Equals'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "block");
                         $('#pfaListDiv').css("display", "block");
                         $('#pfaListDiv2').css("display", "none");
                      }
                      else if(pr=='In'){
                         $('#oneInput').css("display", "none");
                         $('#twoInput').css("display", "none");
                         $('#oneDateInput').css("display", "none");
                         $('#twoDateInput').css("display", "none");
                         $('#strOpAdd').css("display", "none");
                         $('#numOpAdd').css("display", "none");
                         $('#dOpAdd').css("display", "none");
                         $('#mdaOpAdd').css("display", "none");
                         $('#mdaListDiv').css("display", "none");
                         $('#mdaListDiv2').css("display", "none");
                         $('#bOpAdd').css("display", "none");
                         $('#bankListDiv').css("display", "none");
                         $('#bankListDiv2').css("display", "none");
                         $('#stOpAdd').css("display", "none");
                         $('#stateListDiv').css("display", "none");
                         $('#stateListDiv2').css("display", "none");
                         $('#eOpAdd').css("display", "none");
                         $('#emptListDiv').css("display", "none");
                         $('#emptListDiv2').css("display", "none");
                         $('#msOpAdd').css("display", "none");
                         $('#maritalListDiv').css("display", "none");
                         $('#maritalListDiv2').css("display", "none");
                         $('#ltOpAdd').css("display", "none");
                         $('#loanListDiv').css("display", "none");
                         $('#loanListDiv2').css("display", "none");
                         $('#satOpAdd').css("display", "none");
                         $('#salaryListDiv').css("display", "none");
                         $('#salaryListDiv2').css("display", "none");
                         $('#pfaOpAdd').css("display", "block");
                         $('#pfaListDiv').css("display", "none");
                         $('#pfaListDiv2').css("display", "block");
                      }
                });
             });
        </script>

        <script>
            var reportArray=[];
            var reportArray1=[];
            var reportArray2=[];
            var reportArray3=[];
            var reportArray4=[];
            var reportArray5=[];
            var reportArray6=[];
            var reportArray7=[];
            var reportArray8=[];
            var reportArray9=[];
            var reportArray10=[];
            var reportArray11=[];
            var counter = 1;
             $(function() {
                $('#add1').click(function() {
                    $('#delete').css("display", "inline-block");
                    console.log("I'm in");
                    var sb = $('#type option:selected').data('id');
                    var sbt = $('#type option:selected').text();
                    var so = $('#strOp').val();
                    var ss = $('#searchStr').val();
                    if(ss==""){
                        alert("Please fill the empty field");
                    }
                    if((sb!="") && (so!="") && (ss!="")){
                         $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input class="rcc'+counter+'" id="'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td>'+ss+'</td>'
                            +'</tr>'
                         );
                         $('#fdiv').css("display", "block");
                        reportArray1.push(sb);
                        reportArray1.push(so);
                        reportArray1.push(ss);
                        reportArray.push(reportArray1.join(':'));
                        reportArray1 = [];
                        counter+=1;
                    }
                    console.log("report array in add1 is "+reportArray);
                    <%--console.log("I am trying to remove" +sbt);
                    $("#type option[value='" + sbt + "']").remove();--%>
                    $('#type').prop('selected', false).find('option:first').prop('selected', true);
                    $('#string').css("display", "none");
                    $('#strOpAdd').css("display", "none");
                    $('#oneInput').css("display", "none");
                });
            });
             $(function() {
                $('#add2').click(function() {
                   $('#delete').css("display", "inline-block");
                   var ss;
                   var ss2;
                   var sb = $('#type option:selected').data('id');
                   var sbt = $('#type option:selected').text();
                   var so = $('#numOp').val();
                   if((so=='Equals')||(so=='Greater Than')||(so=='Less Than')){
                       ss = $('#searchStr').val();
                       if(ss==""){
                          alert("Please select a valid date");
                       }
                       if((sb!="") && (so!="") & (ss!="")){
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input class="rcc'+counter+'" id="'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td>'+ss+'</td>'
                              +'</tr>'
                           );
                           $('#fdiv').css("display", "block");
                           reportArray2.push(sb);
                           reportArray2.push(so);
                           reportArray2.push(ss);
                           reportArray.push(reportArray2.join(':'));
                           reportArray2 = [];
                           counter+=1;
                       }
                   }
                   else if(so=='Between'){
                      ss = $('#searchNum1').val();
                      ss2 = $('#searchNum2').val();
                      console.log("value1 is "+ss);
                      console.log("value2 is "+ss2);
                      if((ss=="")||(ss2=="")){
                          alert("Please enter a valid date");
                      }
                      if(ss>ss2){
                         alert("Please select a valid range. \nHint: the lower value before the higher value");
                      }
                      if((sb!="") && (so!="") && (ss!="") && (ss2!="") && (ss2>ss)){
                         $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td>'+ss+' and '+ss2+'</td>'
                            +'</tr>'
                         );
                         $('#fdiv').css("display", "block");
                         reportArray2.push(sb);
                         reportArray2.push(so);
                         reportArray2.push(ss + "_" + ss2);
                         reportArray.push(reportArray2.join(':'));
                         reportArray2 = [];
                         counter+=1;
                      }
                   }
                   $('#type').prop('selected', false).find('option:first').prop('selected', true);
                   $('#number').css("display", "none");
                   $('#numOpAdd').css("display", "none");
                   $('#oneInput').css("display", "none");
                   $('#twoInput').css("display", "none");
                   console.log("report array in add2 is "+reportArray);
                });
             });

            $(function() {
                $('#add3').click(function() {
                    $('#delete').css("display", "inline-block");
                    var ss;
                    var ss2;
                    var sb = $('#type option:selected').data('id');
                    var sbt = $('#type option:selected').text();
                    var so = $('#dOp').val();

                    if((so=='Equals')||(so=='Greater Than')||(so=='Less Than')){
                        ss = $('#searchStrd').val();
                        if(ss==""){
                            alert("Please select a valid date");
                        }
                        if((sb!="") && (so!="") & (ss!="")){
                            console.log("date value is "+ss);
                            $('#ftable > tbody:last-child').append(
                                '<tr style="font-size:12px" class="radd">'
                                +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                                +'<td>'+sbt+'</td>'
                                +'<td>'+so+'</td>'
                                +'<td>'+ss+'</td>'
                                +'</tr>'
                            );
                            $('#fdiv').css("display", "block");
                           reportArray3.push(sb);
                           reportArray3.push(so);
                           reportArray3.push(ss);
                           reportArray.push(reportArray3.join(':'));
                           reportArray3 = [];
                           ss = $('#searchStrd').val("");
                           counter+=1;
                        }
                    }
                    else if(so=='Between'){
                        ss = $('#searchStr1').val();
                        ss2 = $('#searchStr2').val();
                        console.log("date value is "+ss);
                        console.log("date value2 is "+ss2);

                        if((ss=="")||(ss2=="")){
                            alert("Please enter a valid date");
                        }
                        if(ss>=ss2){
                            alert("Please select a valid date range");
                        }
                        if((sb!="") && (so!="") && (ss!="") && (ss2!="") && (ss2>ss)){
                            $('#ftable > tbody:last-child').append(
                               '<tr style="font-size:12px" class="radd">'
                               +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                               +'<td>'+sbt+'</td>'
                               +'<td>'+so+'</td>'
                               +'<td>'+ss+' and '+ss2+'</td>'
                               +'</tr>'
                            );
                            $('#fdiv').css("display", "block");
                            reportArray3.push(sb);
                            reportArray3.push(so);
                            reportArray3.push(ss + "_" + ss2);
                            reportArray.push(reportArray3.join(':'));
                            reportArray3 = [];
                            ss = $('#searchStr1').val("");
                            ss2 = $('#searchStr2').val("");
                            counter+=1;
                        }
                    }
                    console.log("report array in add3 is "+reportArray);
                    $('#type').prop('selected', false).find('option:first').prop('selected', true);
                    $('#date').css("display", "none");
                    $('#dOpAdd').css("display", "none");
                    $('#oneDateInput').css("display", "none");
                    $('#twoDateInput').css("display", "none");
                });
            });


             $(function() {
                $('#add4').click(function() {
                    $('#delete').css("display", "inline-block");
                    var sb = $('#type option:selected').data('id');
                    var sbt = $('#type option:selected').text();
                    var so = $('#mdaOp').val();
                    var dss = $('#mdaList option:selected').text();
                    var ss = $('#mdaList option:selected').val();
                    var dss2 = [];
                    $("#mdaList2 option:selected").map(
                       function(){
                         dss2.push(jQuery(this).text());
                       }
                    );
                   console.log("dss2 is "+dss2);
                   console.log("dss2 first is "+dss2[0]);
                    var ss2 = $('#mdaList2').val();
                    var ss5 = dss2[0];
                    var ssr = dss2[1];
                    console.log(ssr);
                    console.log("ssr is "+ssr);
                    //console.log("ss4 is "+ss4);
                    console.log("ss2 is "+ss2);
                    var ss3 = ss2.toString().replace(/,/g, '_');
                    console.log("ss3 is now "+ss3);
                    console.log("ss is "+ss);
                    console.log("ss2 is "+ss2);
                    if(so=='Equals'){
                        if(ss==''){
                            alert("Please select a valid Mda");
                        }
                        if((sb!="") && (so!="") && (ss!=='')){
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td>'+dss+'</td>'
                              +'</tr>'
                           );
                           $('#fdiv').css("display", "block");
                           reportArray4.push(sb);
                           reportArray4.push(so);
                           reportArray4.push(ss);
                           reportArray.push(reportArray4.join(':'));
                           reportArray4 = [];
                           counter+=1;
                        }
                    }

                    else if (so=='In'){
                        if(ss2==""){
                            alert("Please select a valid Mda");
                        }
                        if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td>'+ss5+'</td>'
                              +'</tr>'
                           );
                           $('#fdiv').css("display", "block");
                           reportArray4.push(sb);
                           reportArray4.push(so);
                           reportArray4.push(ss3);
                           reportArray.push(reportArray4.join(':'));
                           reportArray4 = [];
                           counter+=1;
                        }
                        if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                            $('#ftable > tbody:last-child').append(
                               '<tr style="font-size:12px" class="radd">'
                               +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                               +'<td>'+sbt+'</td>'
                               +'<td>'+so+'</td>'
                               +'<td class="mdaMore'+counter+'">'+ss5+' <a class="mMore'+counter+'" href="#/">read more</a></td>'
                               +'<td class="mdaLess'+counter+'" style="display:none">'+dss2+' <a class="mLess'+counter+'" href="#/">read less</a></td>'
                               +'</tr>'
                            );
                            var mClass=".mdaMore" + counter;
                            var lClass=".mdaLess" +counter;
                            $('.mMore'+counter+'').click(function(){
                                $(mClass).css("display", "none");
                                $(lClass).css("display", "block");
                            });
                            $('.mLess'+counter+'').click(function(){
                               $(lClass).css("display", "none");
                               $(mClass).css("display", "block");
                            });
                            $('#fdiv').css("display", "block");
                            reportArray4.push(sb);
                            reportArray4.push(so);
                            reportArray4.push(ss3);
                            reportArray.push(reportArray4.join(':'));
                            reportArray4 = [];
                            counter+=1;
                        }
                    }
                    $('#mdaList2 option:selected').each(function() {
                       $(this).prop('selected', false);
                    })
                    $('#mdaList2').multiselect('refresh');
                    $('#type').prop('selected', false).find('option:first').prop('selected', true);
                    $('#mda').css("display", "none");
                    $('#mdaOpAdd').css("display", "none");
                    $('#mdaListDiv').css("display", "none");
                    $('#mdaListDiv2').css("display", "none");
                    console.log("report array in add4 is "+reportArray);
                });
             });

            $(function() {
                $('#add5').click(function() {
                    $('#delete').css("display", "inline-block");
                    var sb = $('#type option:selected').data('id');
                    var sbt = $('#type option:selected').text();
                    console.log("sbt is "+sbt);
                    var so = $('#emptOp').val();
                    console.log("so is "+so);
                    var dss = $('#emptList option:selected').text();
                    console.log("dss is "+dss);
                    var ss = $('#emptList option:selected').val();
                    var dss2 = [];
                    $("#emptList2 option:selected").map(
                       function(){
                         dss2.push(jQuery(this).text());
                       }
                    );
                    console.log("dss2 is "+dss2);
                    console.log("dss2 first is "+dss2[0]);
                    var ss2 = $('#emptList2').val();
                    console.log("ss2 in empt is "+ss2);
                    var ss5 = dss2[0];
                    var ssr = dss2[1];
                    console.log(ssr);
                    console.log("ssr is "+ssr);
                    console.log("ss2 is "+ss2)
                    var ss3 = ss2.toString().replace(/,/g, '_');
                    if(so=='Equals'){
                        if(ss==""){
                            alert("Please select a valid Employee Type");
                        }
                        if((sb!="") && (so!="") && (ss!=="")){
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td>'+dss+'</td>'
                              +'</tr>'
                           );
                           $('#fdiv').css("display", "block");
                           reportArray5.push(sb);
                           reportArray5.push(so);
                           reportArray5.push(ss);
                           reportArray.push(reportArray5.join(':'));
                           reportArray5 = [];
                           counter+=1;
                        }
                    }

                    else if(so=='In'){
                        if(ss2==""){
                            alert("Please select a valid Employee Type");
                        }
                        if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                            $('#ftable > tbody:last-child').append(
                               '<tr style="font-size:12px" class="radd">'
                               +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                               +'<td>'+sbt+'</td>'
                               +'<td>'+so+'</td>'
                               +'<td>'+ss5+'</span></td>'
                               +'</tr>'
                            );
                             $('#fdiv').css("display", "block");
                             reportArray5.push(sb);
                             reportArray5.push(so);
                             reportArray5.push(ss3);
                             reportArray.push(reportArray5.join(':'));
                             reportArray5 = [];
                             counter+=1;
                        }
                        if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td class="emptMore'+counter+'">'+ss5+' <a class="eMore'+counter+'" href="#/">read more</a></td>'
                              +'<td class="emptLess'+counter+'" style="display:none">'+dss2+' <a class="eLess'+counter+'" href="#/">read less</a></td>'
                              +'</tr>'
                           );
                           var mClass=".emptMore" + counter;
                           var lClass=".emptLess" +counter;
                           $('.eMore'+counter+'').click(function(){
                              $(mClass).css("display", "none");
                              $(lClass).css("display", "block");
                           });
                           $('.eLess'+counter+'').click(function(){
                              $(lClass).css("display", "none");
                              $(mClass).css("display", "block");
                           });
                           $('#fdiv').css("display", "block");
                           reportArray5.push(sb);
                           reportArray5.push(so);
                           reportArray5.push(ss3);
                           reportArray.push(reportArray5.join(':'));
                           reportArray5 = [];
                           counter+=1;
                        }
                    }
                    console.log("report array in add5 is "+reportArray);
                    $('#emptList2 option:selected').each(function() {
                        $(this).prop('selected', false);
                    })
                    $('#emptList2').multiselect('refresh');
                    $('#type').prop('selected', false).find('option:first').prop('selected', true);
                    $('#empt').css("display", "none");
                    $('#eOpAdd').css("display", "none");
                    $('#emptListDiv').css("display", "none");
                    $('#emptListDiv2').css("display", "none");
                });
            });
            $(function() {
               $('#addB').click(function() {
                  $('#delete').css("display", "inline-block");
                  var sb = $('#type option:selected').data('id');
                  var sbt = $('#type option:selected').text();
                  var so = $('#bankOp').val();
                  var ss = $('#bankList option:selected').val();
                  var dss2 = [];
                  $("#bankList2 option:selected").map(
                     function(){
                        dss2.push(jQuery(this).text());
                     }
                  );
                  console.log("dss2 is "+dss2);
                  console.log("dss2 first is "+dss2[0]);
                  var ss2 = $('#bankList2').val();
                  var ss5 = dss2[0];
                  var ssr = dss2[1];
                  console.log("ssr is "+ssr);
                  console.log("ss2 is "+ss2)
                  var ss3 = ss2.toString().replace(/,/g, '_');
                  if(so=='Equals'){
                     if(ss==""){
                        alert("Please select a valid Bank");
                     }
                     if((sb!="") && (so!="") && (ss!=="")){
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td>'+ss+'</td>'
                           +'</tr>'
                        );
                        $('#fdiv').css("display", "block");
                        reportArray6.push(sb);
                        reportArray6.push(so);
                        reportArray6.push(ss);
                        reportArray.push(reportArray6.join(':'));
                        reportArray6 = [];
                        counter+=1;
                     }
                  }
                  else if (so=='In'){
                     if(ss2==""){
                        alert("Please select a valid Bank");
                     }
                     if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td>'+ss5+'</td>'
                           +'</tr>'
                        );
                        $('#fdiv').css("display", "block");
                        reportArray6.push(sb);
                        reportArray6.push(so);
                        reportArray6.push(ss3);
                        reportArray.push(reportArray6.join(':'));
                        reportArray6 = [];
                        counter+=1;
                     }
                     if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td class="bankMore'+counter+'">'+ss5+' <a class="bMore'+counter+'" href="#/">read more</a></td>'
                           +'<td class="bankLess'+counter+'" style="display:none">'+dss2+' <a class="bLess'+counter+'" href="#/">read less</a></td>'
                           +'</tr>'
                        );
                        var mClass=".bankMore" + counter;
                        var lClass=".bankLess" +counter;
                        $('.bMore'+counter+'').click(function(){
                           $(mClass).css("display", "none");
                           $(lClass).css("display", "block");
                        });
                        $('.bLess'+counter+'').click(function(){
                           $(lClass).css("display", "none");
                           $(mClass).css("display", "block");
                        });
                        $('#fdiv').css("display", "block");
                        reportArray5.push(sb);
                        reportArray5.push(so);
                        reportArray5.push(ss3);
                        reportArray.push(reportArray5.join(':'));
                        reportArray5 = [];
                        counter+=1;
                     }
                  }
                  console.log("report array in addB is "+reportArray);
                  $('#bankList2 option:selected').each(function() {
                    $(this).prop('selected', false);
                  })
                  $('#bankList2').multiselect('refresh');
                  $('#type').prop('selected', false).find('option:first').prop('selected', true);
                  $('#bank').css("display", "none");
                  $('#bOpAdd').css("display", "none");
                  $('#bankListDiv').css("display", "none");
                  $('#bankListDiv2').css("display", "none");
               });
            });
            $(function() {
               $('#addSt').click(function() {
                  $('#delete').css("display", "inline-block");
                  var sb = $('#type option:selected').data('id');
                  var sbt = $('#type option:selected').text();
                  var so = $('#stateOp').val();
                  var ss = $('#stateList option:selected').val();
                  var ss2 = $('#stateList2').val();
                  var dss2 = [];
                  $("#stateList2 option:selected").map(
                     function(){
                        dss2.push(jQuery(this).text());
                     }
                  );
                  console.log("dss2 is "+dss2);
                  console.log("dss2 first is "+dss2[0]);
                  var ss5 = dss2[0];
                  var ssr = dss2[1];
                  console.log(ssr);
                  console.log("ssr is "+ssr);
                  console.log("ss2 is "+ss2)
                  var ss3 = ss2.toString().replace(/,/g, '_');
                  if(so=='Equals'){
                     if(ss==""){
                        alert("Please select a valid State");
                     }
                     if((sb!="") && (so!="") && (ss!=="")){
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td>'+ss+'</td>'
                           +'</tr>'
                        );
                        $('#fdiv').css("display", "block");
                        reportArray7.push(sb);
                        reportArray7.push(so);
                        reportArray7.push(ss);
                        reportArray.push(reportArray7.join(':'));
                        reportArray7 = [];
                        counter+=1;
                     }
                  }
                  else if (so=='In'){
                     if(ss2==""){
                        alert("Please select a valid State");
                     }
                     if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td>'+ss5+'</td>'
                           +'</tr>'
                        );
                        $('#fdiv').css("display", "block");
                        reportArray7.push(sb);
                        reportArray7.push(so);
                        reportArray7.push(ss3);
                        reportArray.push(reportArray7.join(':'));
                        reportArray7 = [];
                        counter+=1;
                     }
                     if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td class="stateMore'+counter+'">'+ss5+' <a class="sMore'+counter+'" href="#/">read more</a></td>'
                           +'<td class="stateLess'+counter+'" style="display:none">'+dss2+' <a class="sLess'+counter+'" href="#/">read less</a></td>'
                           +'</tr>'
                        );
                        var mClass=".stateMore" + counter;
                        var lClass=".stateLess" +counter;
                        $('.sMore'+counter+'').click(function(){
                            $(mClass).css("display", "none");
                            $(lClass).css("display", "block");
                        });
                        $('.sLess'+counter+'').click(function(){
                            $(lClass).css("display", "none");
                            $(mClass).css("display", "block");
                        });
                        $('#fdiv').css("display", "block");
                        reportArray7.push(sb);
                        reportArray7.push(so);
                        reportArray7.push(ss3);
                        reportArray.push(reportArray7.join(':'));
                        reportArray7 = [];
                        counter+=1;
                     }
                  }
                  console.log("report array in addSt is "+reportArray);
                  $('#stateList2 option:selected').each(function() {
                     $(this).prop('selected', false);
                  })
                  $('#stateList2').multiselect('refresh');
                  $('#type').prop('selected', false).find('option:first').prop('selected', true);
                  $('#state').css("display", "none");
                  $('#stOpAdd').css("display", "none");
                  $('#stateListDiv').css("display", "none");
                  $('#stateListDiv2').css("display", "none");
               });
            });
            $(function() {
               $('#addMs').click(function() {
                  $('#delete').css("display", "inline-block");
                  var sb = $('#type option:selected').data('id');
                  var sbt = $('#type option:selected').text();
                  var so = $('#maritalOp').val();
                  var ss = $('#maritalList option:selected').val();
                  var ss2 = $('#maritalList2').val();
                  var dss2 = [];
                  $("#maritalList2 option:selected").map(
                     function(){
                        dss2.push(jQuery(this).text());
                     }
                  );
                  console.log("dss2 is "+dss2);
                  console.log("dss2 first is "+dss2[0]);
                  var ss5 = dss2[0];
                  var ssr = dss2[1];
                  console.log("ssr is "+ssr);
                  console.log("ss2 is "+ss2)
                  var ss3 = ss2.toString().replace(/,/g, '_');
                  console.log("ss in ms is "+ss);
                  console.log("ss2 in ms is "+ss2);
                  if(so=='Equals'){
                     if(ss==""){
                        alert("Please select a valid Marital Status");
                     }
                     if((sb!="") && (so!="") & (ss!=="")){
                        console.log("sb is "+sb);
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td>'+ss+'</td>'
                           +'</tr>'
                        );
                        $('#fdiv').css("display", "block");
                        reportArray8.push(sb);
                        reportArray8.push(so);
                        reportArray8.push(ss);
                        reportArray.push(reportArray8.join(':'));
                        reportArray8 = [];
                        counter+=1;
                     }
                  }
                  else if (so=='In'){
                     if(ss2==""){
                        alert("Please select a valid Marital Status");
                     }
                     if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                        console.log("sb is "+sb);
                        $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td>'+ss5+'</td>'
                            +'</tr>'
                        );
                        $('#fdiv').css("display", "block");
                        reportArray8.push(sb);
                        reportArray8.push(so);
                        reportArray8.push(ss3);
                        reportArray.push(reportArray8.join(':'));
                        reportArray8 = [];
                        counter+=1;
                     }
                     if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                        console.log("sb is "+sb);
                        $('#ftable > tbody:last-child').append(
                           '<tr style="font-size:12px" class="radd">'
                           +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                           +'<td>'+sbt+'</td>'
                           +'<td>'+so+'</td>'
                           +'<td class="maritalMore'+counter+'">'+ss5+' <a class="mtMore'+counter+'" href="#/">read more</a></td>'
                           +'<td class="maritalLess'+counter+'" style="display:none">'+dss2+' <a class="mtLess'+counter+'" href="#/">read less</a></td>'
                           +'</tr>'
                        );
                        var mClass=".maritalMore" + counter;
                        var lClass=".maritalLess" +counter;
                        $('.mtMore'+counter+'').click(function(){
                          $(mClass).css("display", "none");
                          $(lClass).css("display", "block");
                        });
                        $('.mtLess'+counter+'').click(function(){
                           $(lClass).css("display", "none");
                           $(mClass).css("display", "block");
                        });
                        $('#fdiv').css("display", "block");
                        reportArray8.push(sb);
                        reportArray8.push(so);
                        reportArray8.push(ss3);
                        reportArray.push(reportArray8.join(':'));
                        reportArray8 = [];
                        counter+=1;
                     }
                  }
                  console.log("report array in addMs is "+reportArray);
                  $('#maritalList2 option:selected').each(function() {
                     $(this).prop('selected', false);
                  })
                  $('#maritalList2').multiselect('refresh');
                  $('#type').prop('selected', false).find('option:first').prop('selected', true);
                  $('#marital').css("display", "none");
                  $('#msOpAdd').css("display", "none");
                  $('#maritalListDiv').css("display", "none");
                  $('#maritalListDiv2').css("display", "none");
               });
            });
            $(function() {
               $('#addLt').click(function() {
                   $('#delete').css("display", "inline-block");
                   var sb = $('#type option:selected').data('id');
                   var sbt = $('#type option:selected').text();
                   var so = $('#loanOp').val();
                   var ss = $('#loanList option:selected').val();
                   var ss2 = $('#loanList2').val();
                   var dss2 = [];
                   $("#loanList2 option:selected").map(
                      function(){
                         dss2.push(jQuery(this).text());
                      }
                   );
                   console.log("dss2 is "+dss2);
                   console.log("dss2 first is "+dss2[0]);
                   var ss5 = dss2[0];
                   var ssr = dss2[1];
                   console.log("ssr is "+ssr);
                   console.log("ss2 is "+ss2)
                   var ss3 = ss2.toString().replace(/,/g, '_');
                   console.log("ss in lt is "+ss);
                   console.log("ss2 in lt is "+ss2);
                   if(so=='Equals'){
                      if(ss==""){
                         alert("Please select a valid Loan Type");
                      }
                      if((sb!="") && (so!="") & (ss!=="")){
                         console.log("sb is "+sb);
                         $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td>'+ss+'</td>'
                            +'</tr>'
                         );
                         $('#fdiv').css("display", "block");
                         reportArray9.push(sb);
                         reportArray9.push(so);
                         reportArray9.push(ss);
                         reportArray.push(reportArray9.join(':'));
                         reportArray9 = [];
                         counter+=1;
                      }
                   }
                   else if (so=='In'){
                      if(ss2==""){
                         alert("Please select a valid Loan Type");
                      }
                      if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                         console.log("sb is "+sb);
                         $('#ftable > tbody:last-child').append(
                             '<tr style="font-size:12px" class="radd">'
                             +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                             +'<td>'+sbt+'</td>'
                             +'<td>'+so+'</td>'
                             +'<td>'+ss5+'</td>'
                             +'</tr>'
                         );
                         $('#fdiv').css("display", "block");
                         reportArray9.push(sb);
                         reportArray9.push(so);
                         reportArray9.push(ss3);
                         reportArray.push(reportArray9.join(':'));
                         reportArray9 = [];
                         counter+=1;
                      }
                      if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                         console.log("sb is "+sb);
                         $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td class="loanMore'+counter+'">'+ss5+' <a class="ltMore'+counter+'" href="#/">read more</a></td>'
                            +'<td class="loanLess'+counter+'" style="display:none">'+dss2+' <a class="ltLess'+counter+'" href="#/">read less</a></td>'
                            +'</tr>'
                         );
                         var mClass=".loanMore" + counter;
                         var lClass=".loanLess" +counter;
                         $('.ltMore'+counter+'').click(function(){
                           $(mClass).css("display", "none");
                           $(lClass).css("display", "block");
                         });
                         $('.ltLess'+counter+'').click(function(){
                            $(lClass).css("display", "none");
                            $(mClass).css("display", "block");
                         });
                         $('#fdiv').css("display", "block");
                         reportArray9.push(sb);
                         reportArray9.push(so);
                         reportArray9.push(ss3);
                         reportArray.push(reportArray9.join(':'));
                         reportArray9 = [];
                         counter+=1;
                      }
                   }
                   console.log("report array in addLt is "+reportArray);
                   $('#loanList2 option:selected').each(function() {
                      $(this).prop('selected', false);
                   })
                   $('#loanList2').multiselect('refresh');
                   $('#type').prop('selected', false).find('option:first').prop('selected', true);
                   $('#loan').css("display", "none");
                   $('#ltOpAdd').css("display", "none");
                   $('#loanListDiv').css("display", "none");
                   $('#loanListDiv2').css("display", "none");
               });
            });
            $(function() {
               $('#addSat').click(function() {
                   $('#delete').css("display", "inline-block");
                   var sb = $('#type option:selected').data('id');
                   var sbt = $('#type option:selected').text();
                   var so = $('#salaryOp').val();
                   var ss = $('#salaryList option:selected').val();
                   var ss2 = $('#salaryList2').val();
                   var dss2 = [];
                   $("#salaryList2 option:selected").map(
                      function(){
                         dss2.push(jQuery(this).text());
                      }
                   );
                   console.log("dss2 is "+dss2);
                   console.log("dss2 first is "+dss2[0]);
                   var ss5 = dss2[0];
                   var ssr = dss2[1];
                   console.log("ssr is "+ssr);
                   console.log("ss2 is "+ss2)
                   var ss3 = ss2.toString().replace(/,/g, '_');
                   console.log("ss in sat is "+ss);
                   console.log("ss2 in sat is "+ss2);
                   if(so=='Equals'){
                      if(ss==""){
                         alert("Please select a valid Salary Type");
                      }
                      if((sb!="") && (so!="") & (ss!=="")){
                         console.log("sb is "+sb);
                         $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td>'+ss+'</td>'
                            +'</tr>'
                         );
                         $('#fdiv').css("display", "block");
                         reportArray10.push(sb);
                         reportArray10.push(so);
                         reportArray10.push(ss);
                         reportArray.push(reportArray10.join(':'));
                         reportArray10 = [];
                         counter+=1;
                      }
                   }
                   else if (so=='In'){
                      if(ss2==""){
                         alert("Please select a valid Salary Type");
                      }
                      if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                         console.log("sb is "+sb);
                         $('#ftable > tbody:last-child').append(
                             '<tr style="font-size:12px" class="radd">'
                             +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                             +'<td>'+sbt+'</td>'
                             +'<td>'+so+'</td>'
                             +'<td>'+ss5+'</td>'
                             +'</tr>'
                         );
                         $('#fdiv').css("display", "block");
                         reportArray10.push(sb);
                         reportArray10.push(so);
                         reportArray10.push(ss3);
                         reportArray.push(reportArray10.join(':'));
                         reportArray10 = [];
                         counter+=1;
                      }
                      if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                         console.log("sb is "+sb);
                         $('#ftable > tbody:last-child').append(
                            '<tr style="font-size:12px" class="radd">'
                            +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                            +'<td>'+sbt+'</td>'
                            +'<td>'+so+'</td>'
                            +'<td class="salaryMore'+counter+'">'+ss5+' <a class="satMore'+counter+'" href="#/">read more</a></td>'
                            +'<td class="salaryLess'+counter+'" style="display:none">'+dss2+' <a class="satLess'+counter+'" href="#/">read less</a></td>'
                            +'</tr>'
                         );
                         var mClass=".salaryMore" + counter;
                         var lClass=".salaryLess" +counter;
                         $('.satMore'+counter+'').click(function(){
                           $(mClass).css("display", "none");
                           $(lClass).css("display", "block");
                         });
                         $('.satLess'+counter+'').click(function(){
                            $(lClass).css("display", "none");
                            $(mClass).css("display", "block");
                         });
                         $('#fdiv').css("display", "block");
                         reportArray10.push(sb);
                         reportArray10.push(so);
                         reportArray10.push(ss3);
                         reportArray.push(reportArray10.join(':'));
                         reportArray10 = [];
                         counter+=1;
                      }
                   }
                   console.log("report array in addSat is "+reportArray);
                   $('#salaryList2 option:selected').each(function() {
                      $(this).prop('selected', false);
                   })
                   $('#salaryList2').multiselect('refresh');
                   $('#type').prop('selected', false).find('option:first').prop('selected', true);
                   $('#salary').css("display", "none");
                   $('#satOpAdd').css("display", "none");
                   $('#salaryListDiv').css("display", "none");
                   $('#salaryListDiv2').css("display", "none");
               });
            });
            $(function() {
                 $('#addPfa').click(function() {
                     $('#delete').css("display", "inline-block");
                     var sb = $('#type option:selected').data('id');
                     var sbt = $('#type option:selected').text();
                     var so = $('#pfaOp').val();
                     var ss = $('#pfaList option:selected').val();
                     var ss2 = $('#pfaList2').val();
                     var dss2 = [];
                     $("#pfaList2 option:selected").map(
                        function(){
                           dss2.push(jQuery(this).text());
                        }
                     );
                     console.log("dss2 is "+dss2);
                     console.log("dss2 first is "+dss2[0]);
                     var ss5 = dss2[0];
                     var ssr = dss2[1];
                     console.log("ssr is "+ssr);
                     console.log("ss2 is "+ss2)
                     var ss3 = ss2.toString().replace(/,/g, '_');
                     console.log("ss in pfa is "+ss);
                     console.log("ss2 in pfa is "+ss2);
                     if(so=='Equals'){
                        if(ss==""){
                           alert("Please select a valid PFA");
                        }
                        if((sb!="") && (so!="") & (ss!=="")){
                           console.log("sb is "+sb);
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td>'+ss+'</td>'
                              +'</tr>'
                           );
                           $('#fdiv').css("display", "block");
                           reportArray9.push(sb);
                           reportArray9.push(so);
                           reportArray9.push(ss);
                           reportArray.push(reportArray9.join(':'));
                           reportArray9 = [];
                           counter+=1;
                        }
                     }
                     else if (so=='In'){
                        if(ss2==""){
                           alert("Please select a valid PFA");
                        }
                        if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr=="")){
                           console.log("sb is "+sb);
                           $('#ftable > tbody:last-child').append(
                               '<tr style="font-size:12px" class="radd">'
                               +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                               +'<td>'+sbt+'</td>'
                               +'<td>'+so+'</td>'
                               +'<td>'+ss5+'</td>'
                               +'</tr>'
                           );
                           $('#fdiv').css("display", "block");
                           reportArray11.push(sb);
                           reportArray11.push(so);
                           reportArray11.push(ss3);
                           reportArray.push(reportArray11.join(':'));
                           reportArray11 = [];
                           counter+=1;
                        }
                        if((sb!="") && (so!="") && (ss2!="") && (ss5!="") && (ssr!="")){
                           console.log("sb is "+sb);
                           $('#ftable > tbody:last-child').append(
                              '<tr style="font-size:12px" class="radd">'
                              +'<td><input id="'+counter+'" class="rcc'+counter+'" type="checkbox" name="rc"></td>'
                              +'<td>'+sbt+'</td>'
                              +'<td>'+so+'</td>'
                              +'<td class="pfaMore'+counter+'">'+ss5+' <a class="ptMore'+counter+'" href="#/">read more</a></td>'
                              +'<td class="pfaLess'+counter+'" style="display:none">'+dss2+' <a class="ptLess'+counter+'" href="#/">read less</a></td>'
                              +'</tr>'
                           );
                           var mClass=".pfaMore" + counter;
                           var lClass=".pfaLess" +counter;
                           $('.ptMore'+counter+'').click(function(){
                             $(mClass).css("display", "none");
                             $(lClass).css("display", "block");
                           });
                           $('.ptLess'+counter+'').click(function(){
                              $(lClass).css("display", "none");
                              $(mClass).css("display", "block");
                           });
                           $('#fdiv').css("display", "block");
                           reportArray11.push(sb);
                           reportArray11.push(so);
                           reportArray11.push(ss3);
                           reportArray.push(reportArray11.join(':'));
                           reportArray11 = [];
                           counter+=1;
                        }
                     }
                     console.log("report array in addPfa is "+reportArray);
                     $('#pfaList2 option:selected').each(function() {
                        $(this).prop('selected', false);
                     })
                 })
                 $('#addPfa').click(function() {
                   $('#pfaList2').multiselect('refresh');
                   $('#type').prop('selected', false).find('option:first').prop('selected', true);
                   $('#pfa').css("display", "none");
                   $('#pfaOpAdd').css("display", "none");
                   $('#pfaListDiv').css("display", "none");
                   $('#pfaListDiv2').css("display", "none");
                 });
            });
            $("#delete").on("click",function(){
                if(!($("input[type=checkbox]").is(":checked"))){
                    alert("Please check a box to delete");
                }
                else {
                   if (confirm('Are you sure you want to delete this row')) {
                      for(var i=reportArray.length; i>=1; i--){
                         console.log("i is"+ i);
                         if($(".rcc"+(i)).is(":checked")){
                            let x = i-1;
                            console.log("rcc r..."+ reportArray[x]);
                            reportArray.splice(x,1);
                            //delete reportArray[x];
                         }
                         else{
                            let x = i-1;
                            console.log("This is not checked for deletion");
                            console.log("rcc r..."+ reportArray[x]);
                         }
                      }
                      console.log("report array is "+reportArray);
                      $('input:checked').each(function() {
                        $(this).closest('tr').remove();
                        $('#sdiv').css("display", "block");
                      });
                   }
                }
            });
            $('#go').click(function(e) {
                var lis = document.getElementById("rul").getElementsByTagName("li");
                var list=[];
                let check = 0;
                let isContained = 0;
                for (let i = 0; i < lis.length; i++){
                    if(((lis[i].innerHTML) == 'Net Pay') || ((lis[i].innerHTML) == 'Total Pay') ||
                        ((lis[i].innerHTML) == 'Pay Period') || ((lis[i].innerHTML) == 'BVN') || ((lis[i].innerHTML) == 'Gratuity Amount')){
                        console.log("That's what I'm talking about");
                        isContained = 1;
                        for (let i = 0; i < reportArray.length; i++){
                           console.log("Report Array "+i+" "+reportArray[i]);
                           if(reportArray[i].startsWith(97)){
                              check++;
                           }
                        }
                    }
                   list.push([lis[i].innerHTML]);
                }
                console.log("check is "+check);
                console.log("list is "+list);
                var headers = list.toString();
                console.log("headers is "+headers);
                console.log("report array in go is "+reportArray);
                var filters = reportArray.toString();
                console.log("filters is "+filters);
                var url ="customReportGenerator.do";
                console.log("check and isContained is "+check +" "+isContained);
                if((check == 0) && (isContained == 0)){
                    console.log("If statement, check and contained are "+check+" "+isContained);
                    $.ajax({
                       type: "GET",
                       url: url,
                       success: function (response) {
                          // do something ...
                          console.log("Data here is " + headers);
                          window.location.href ="${pageContext.request.contextPath}/customReportGenerator.do?headers=" + headers+ "&filters=" + filters
                       },
                       error: function (e) {
                          alert('Error: ' + e);
                       }
                    });
                }
                else if((check > 0) && (isContained == 1)){
                    console.log("first else statement, check and contained are "+check+" "+isContained);
                   $.ajax({
                      type: "GET",
                      url: url,
                      success: function (response) {
                         // do something ...
                         console.log("Data here is " + headers);
                         window.location.href ="${pageContext.request.contextPath}/customReportGenerator.do?headers=" + headers+ "&filters=" + filters
                      },
                      error: function (e) {
                         alert('Error: ' + e);
                      }
                   });
                }
                else if((check == 0) && (isContained == 1)){
                    console.log("second else statement, check and contained are "+check+" "+isContained);
                   alert("You need to filter by Pay Period as your query will be in balanced");
                }
                console.log("Hi");
            });
        </script>
  </body>
</html>