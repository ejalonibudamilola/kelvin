<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Custom Report Design Form </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="scripts/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="scripts/utility_script.js"></script>

<script type="text/javascript">
	
//A dictionary of the index in the original list to the id of the entity
var hiddenFieldPrefix="sce-";
var selectedHiddenFieldPrefix="ssce-";
var removeIndex = "r";


function move_item(from, to, elem, isRemove)
	{	
	var wL = elem.id;
	var addVal = 'add1';
	  var SI;
	  if(from.options.length>0)
	  {
		  /*  this loop goes through the fromList, picks out
		   the selected Items and puts them in toList.  */
	    for(i=0;i<from.length;i++)
	    {
	      if(from.options[i].selected)
	      {
		    var selectedObj=from.options[from.selectedIndex];
	        f=selectedObj.index;

			//value is the ID
			var entityId=selectedObj.value;
			
	        //get the hidden input field for the selected option
	        var indexOfSelectedOption= document.getElementById(hiddenFieldPrefix+entityId).value;
			var selectedFieldId = selectedHiddenFieldPrefix+entityId;
			
			console.log("selected ID: " + selectedFieldId);
			
	        if (!isRemove) {
	        	jQuery("#selected-list-div").append("<input id='"+selectedFieldId+"' type='hidden' value='true' name='fieldList["+indexOfSelectedOption+"].selected' />");
	        	jQuery("#"+removeIndex+selectedFieldId).remove();
		    } else {
		    	jQuery("#removed-list-div").append("<input id='"+removeIndex+selectedFieldId+"' type='hidden' value='true' name='selectedFieldList["+indexOfSelectedOption+"].selected' />");
		    	 jQuery("#"+selectedFieldId).remove();
		    	 //document.getElementById(selectedFieldId).remove();
		     } 		
	        
	        to.options[to.length]=new Option(selectedObj.text,entityId);
	     //   from.options[i].prop('selected', false);
	        from.options[f]=null;
	        i--; 
	      }
	      
	    }
	  }

	  // displayButton(from,to,wL, addVal);
	  } 

	function move_all_items(from, to, elem, isRemove)
	{	
		var wL = elem.id;
		var addVal = 'addAll';	 
	  if(from.length>0)
	  {
		  /*  this loop goes through the fromList, and puts all Items in the toList.  */
	    for(i=0;i<from.length;i++)
	    {
			var currObj = from.options[i];
			var entityID = currObj.value;
			var indexOfSelectedOption = document.getElementById(hiddenFieldPrefix+entityID).value;
			var selectedFieldId = selectedHiddenFieldPrefix+entityID;

			console.log("selected ID: " + selectedFieldId);
		    
	    	if (!isRemove) {
	        	jQuery("#selected-list-div").append("<input id='"+selectedFieldId+"' type='hidden' value='true' name='fieldList["+indexOfSelectedOption+"].selected' />");
	        	jQuery("#"+removeIndex+selectedFieldId).remove();
		    } else {
		    	jQuery("#removed-list-div").append("<input id='"+removeIndex+selectedFieldId+"' type='hidden' value='true' name='selectedFieldList["+indexOfSelectedOption+"].selected' />");
			    jQuery("#"+selectedFieldId).remove();
			 }
		    
	        f=currObj.index;
	        to.options[to.length]=new Option(currObj.text,entityID);
	       // from.options[i].prop('selected', false);
	        from.options[f]=null;
			i--;
	    }
		    
	 // displayButton(from,to ,wL, addVal);
	  }
	  
	}

	function displayButton(from,to, wL, addVal)
	{
		if(wL == addVal && to.length > 0)
		  {	
		  	document.getElementById("buttons").style.display="";
		  }
	  else{
		   if(from.length < 1)
			  {
			  document.getElementById("buttons").style.display="none";
			  }
	  }  
		}
	</script>

<script type="text/javascript">
</script>
</head>

<body class="main">
<form:form modelAttribute="miniBean">
<script type="text/javascript" src="scripts/jacs.js"></script>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td>
								<div class="title">Create Custom Report</div>
						</td>
					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>
				
				
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
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="center" >
					<tr align="center">
						<td class="activeTH">Custom Report Configuration</td>
					</tr>
			          
			          <tr>
			          	<td colspan="3">
			          	<fieldset>
			          	<legend>Select Fields </legend>
			          	
			          <table>
			          <tr>
							<td> Fields <br>
							
										<form:select id="typeList" style="width: 240px;" path="fieldList" multiple="true" size="24">
											 <c:forEach items="${miniBean.reportAttributes}" var="yList">
											         <form:option value="${yList.id}">${yList.defDisplayName}</form:option>
											      </c:forEach>
									</form:select> 
						</td>
						<td>
								&nbsp;&nbsp;&nbsp;&nbsp;<form:input id="add1" path="" value=" Add >  " type="button" onclick="move_item(document.getElementById('typeList'), document.getElementById('selectList'), this, false)"/><br>
								<br>
								&nbsp;&nbsp;&nbsp;&nbsp;<form:input id="addAll" path="" value=" Add All >>  " type="button" onclick="move_all_items(document.getElementById('typeList'), document.getElementById('selectList'), this, false)" /><br>
								<br>
								&nbsp;&nbsp;&nbsp;&nbsp;<form:input id="rem1" path="" value=" < Remove  " type="button" onclick="move_item(document.getElementById('selectList'), document.getElementById('typeList'), this, true)"/> <br>
								<br>
								&nbsp;&nbsp;&nbsp;&nbsp;<form:input id="remAll" path="" value=" << Remove All  " type="button" onclick="move_all_items(document.getElementById('selectList'), document.getElementById('typeList'), this, true)"/>
								<br>
						</td>
						<td> Selected Fields<br>
								<form:select id="selectList" style="width: 230px;" path="selectedFieldList" multiple="true" size="24">
											 <c:forEach items="${miniBean.selectedFieldList}" var="yList">
											         <form:option value="${yList.id}">${yList.defDisplayName}</form:option>
											      </c:forEach>
									</form:select> 
						</td>
				</tr>
				
					</table>
					</fieldset>
					</td>
					</tr>
				
					        </table>							
						</td>
					</tr>
					
					<tr><td>
							<!--
						This div should contain the a map of the index in the original list to the id of the entity  
				-->
				
				<div style="display:hidden;" id="original-list-div">
					<c:forEach items="${miniBean.reportAttributes}" var="pbl" varStatus="currStatus">
						<input type="hidden" id="sce-${pbl.id}" value="${currStatus.index}" />
					</c:forEach>
				</div>
				
				<div style="display:hidden;" id="original-selected-list-div">
					<c:forEach items="${miniBean.selectedFieldList}" var="sll" varStatus="currStatus">
						<input type="hidden" id="sce-${sll.id}" value="${currStatus.index}" />
					</c:forEach>
				</div>
				
				<div style="display:hidden;" id="selected-list-div">
							
				</div>
					<div style="display:hidden;" id="removed-list-div">
							
				</div>
					</td>
					</tr>
					<tr>
						<td class="buttonRow" align="right" >
						   
						 <input type="image" name="_submit" value="submit" title="Continue" class="" src="images/ok_h.png">&nbsp;
						 <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
							          
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
</body>
</html>