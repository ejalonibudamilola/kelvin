<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title>Promotion By Pay Group</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
<script language="JavaScript">
        	 		$(document).ready(function() {

        		    //bind a click event to the select all checkbox
        		    jQuery('#selectall').click(function(event) {  //on click
        		    	console.log("Select ALL");
        		    	//var checkboxes = [];
        		    	//var checked = false;
        		        var isChecked = this.checked; //this.checked ? true : false;
        		        var url = 'massPromoByPayGroup.do?checkboxState='+(isChecked?"true":"false");

        		    	jQuery('.selectableCheckbox').each(function(index, element) { //loop through each checkbox
        	                this.checked = isChecked;
        	                //checkboxes.push(this.id);
        	                //url+='&reqList='+this.id;
        	                
        	            });

        		    	//if(checkboxes.length > 0) {
        		    		sendValueToServer(url);
        		    	//}
        		    });


        		    jQuery(".selectableCheckbox").click(function(e ){
        		    	console.log("Select Individually");
        		    	var url = 'massPromoByPayGroup.do?checkboxState='+(this.checked?"true":"false") //use can use true or false also
        				+"&reqList="+this.id;
        		    	console.log("URL is "+url);
        		    	sendValueToServer(url);
        		    });

        		    function sendValueToServer(pUrl) {
        		    	jQuery.ajax({
        	    			url: pUrl,
        	    			type: "POST"
        	    		});
        		    }
        		});
        	</script>
</head>

<style>
.tableFixHead {
	margin-top: 3%;
	overflow-y: auto;
	height: 400px;
}
.tableFixHead thead th{
	position: sticky;
	top: 0;
}
th{
 background: #E2E2E2 !important;
}
</style>
<body class="main">
	<form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">
					<div class="title">Promotion By Pay Group</div>
				</td>
			</tr>
			<tr>
					<td valign="top" class="mainBody" id="mainBody">
					<div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
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
					<table>
									<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    
                                    <tr>
                                      
                                        <td class="activeTD">
                                            From Pay Group :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                           
										   <form:select path="fromSalaryTypeId" id="salary-type-control" cssClass="salaryTypeControls" onchange="loadSalaryLevelAndStepBySalaryTypeId(this);" disabled="${miniBean.hasRecords}">
											<form:option value="-1">&lt;Select&gt;</form:option>
											<c:forEach items="${salaryTypeList}" var="ssList">
												<form:option value="${ssList.id}">${ssList.name}</form:option>
											</c:forEach>
										    </form:select>
											 
							 
                                        </td>
                                       
                                        
                                    </tr>
                                    
                                    <tr>
                                       <td class="activeTD">
                                            From Level/Step*&nbsp;:                                  
                                        </td>
                                        <td class="activeTD">
                                          <form:select path="fromSalaryStructureId" id="levelStep-control" cssClass="salaryTypeControls" disabled="${miniBean.hasRecords}"> 
												 <form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												 <c:forEach items="${fromSalaryStructureList}" var="fLevelList">
												<form:option value="${fLevelList.id}">${fLevelList.levelStepStr}</form:option>
												 </c:forEach>
											</form:select> 
                                        </td>
                                    </tr>
                                    <c:if test="${miniBean.hasRecords}">
                                    <tr>
                                       <td class="activeTD">
                                           To Level/Step*&nbsp;:                                
                                        </td>
                                        <td class="activeTD">
			                                <form:select path="toSalaryStructureId">
											<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
										    <c:forEach items="${toSalaryStructureList}" var="tList">
												 <form:option value="${tList.id}">${tList.levelStepStr}</form:option>
												 </c:forEach>
											</form:select> 
                                        </td>
                                    </tr>
                                   
                                     
									<tr>
                                    	<td class="buttonRow" align="right">

					  						<input type="image" name="_promote" value="promote" title="Promote ${roleBean.staffTypeName}s" class="updateReportSubmit" src="images/promote_h.png">
					                     </td>
                                    </tr>
                                     </c:if>
								</table>  
								</td>
								</tr>
							                              
					
					 
					<c:if test="${miniBean.hasRecords}">
					<div>
						 <br/>	
						<br/>	
					</div>
					
					<tr>
						<td>
						 
					<div class="tableFixHead">
						<table class="report" cellspacing="0" cellpadding="0">
						   <thead>
						   <tr class="reportOdd header">
							    <th class="tableCell" valign="top" width="5px">
							     &nbsp;&nbsp;<input id="selectall" type="checkbox" >
								</th>
                                <th class="tableCell" valign="top" width="25px"><c:out value="${roleBean.staffTitle}"/></th>
								<th class="tableCell" valign="top" width="100px"><c:out value="${roleBean.staffTypeName}"/> Name</th>
								<th class="tableCell" valign="top" width="100px"><c:out value="${roleBean.mdaTitle}"/></th>
							</tr>
							</thead>
							<c:forEach items="${miniBean.employeeList}" var="beans" varStatus="gridRow">
							<c:choose>
							    <c:when test="${beans.add}">
                                    <tr class="${beans.displayStyle}" style="outline:medium solid red">
							             <td class="tableCell" valign="top" width="5px">&nbsp;
			                  	             <spring:bind path="miniBean.employeeList[${gridRow.index}].payEmployee">
			                 	                 <input type="hidden" name="_<c:out value="${status.expression}"/>">
			                 	                 <input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" class="selectableCheckbox" id="${beans.id}" <c:if test="${status.value}">checked</c:if>  title="Check this box to Promote ${roleBean.staffTypeName}s" />
			     				             </spring:bind>
			     			  	         </td>
								         <td class="tableCell" valign="top" width="25px"><c:out value="${beans.employeeId}"/></td>
								         <td class="tableCell" align="left" valign="top" width="100px" ><c:out value="${beans.displayName}"/></td>
								         <td class="tableCell" align="left" valign="top" width="100px" ><c:out value="${beans.mdaName}"/></td>
							        </tr>
							    </c:when>
							    <c:otherwise>
                                    <tr class="${beans.displayStyle}">
							             <td class="tableCell" valign="top" width="5px">&nbsp;
			                  	             <spring:bind path="miniBean.employeeList[${gridRow.index}].payEmployee">
			                 	                 <input type="hidden" name="_<c:out value="${status.expression}"/>">
			                 	                 <input type="checkbox" name="<c:out value="${status.expression}"/>" value="true" class="selectableCheckbox" id="${beans.id}" <c:if test="${status.value}">checked</c:if>  title="Check this box to Promote ${roleBean.staffTypeName}s" />
			     				             </spring:bind>
			     			  	         </td>
								         <td class="tableCell" valign="top" width="25px"><c:out value="${beans.employeeId}"/></td>
								         <td class="tableCell" align="left" valign="top" width="100px" ><c:out value="${beans.displayName}"/></td>
								         <td class="tableCell" align="left" valign="top" width="100px" ><c:out value="${beans.mdaName}"/></td>
							        </tr>
							    </c:otherwise>
							</c:choose>

							</c:forEach>
						</table>
						</div>
						</td>
						</tr>
						
					</c:if>
					
					<tr>
					<td class="buttonRow" align="right">
					   <c:if test="${not miniBean.hasRecords}">
					   <input type="image" name="_updateReport" value="updateReport" title="Load ${roleBean.staffTypeName}s" class="updateReportSubmit" src="images/Update_Report_h.png">
					   </c:if>
					    <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
					</td>
				</tr>
				
		</table>
		
		</td>
		</tr>
		
		
		</table>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
		</td>
		</tr>
		
	</table>
	</form:form>
</body>
</html>
