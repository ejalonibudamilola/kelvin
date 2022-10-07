function loadSalaryLevelByCadreId(pElem){

	 if(pElem.value == "0"){
	        $("#from-level-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="0">&lt;Select&gt;</option>').appendTo("#from-level-control");
	          $("#to-level-control option").remove(); //empty the dropdown
             //<Select>
             $('<option value="0">&lt;Select&gt;</option>').appendTo("#to-level-control");
	    }
	     //load the departments
	        else{

	             $.get("getLevelsForCadre.do", { cadreInstId: pElem.value}, function( returnedHTML ) {

	                $("#from-level-control option").remove();
	                //display the initial value
	                $('<option value="0">&lt;Select&gt;</option>').appendTo("#from-level-control");
	                //add the returned html
	                $("#from-level-control").append(returnedHTML);
	                 $("#to-level-control option").remove();
                     //display the initial value
                    $('<option value="0">&lt;Select&gt;</option>').appendTo("#to-level-control");
                     //add the returned html
                     $("#to-level-control").append(returnedHTML);
                   }, "html");
	        }



}
function loadSalaryStepByCadreId(pElem){

	 if(pElem.value == "0"){
	        $("#from-step-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="0">&lt;Select&gt;</option>').appendTo("#from-step-control");
            $("#to-step-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="0">&lt;Select&gt;</option>').appendTo("#to-step-control");	         
	    }
	     //load the departments
	        else{

	             $.get("getStepsForCadre.do", { cadreInstId: pElem.value}, function( returnedHTML ) {

	                $("#from-step-control option").remove();
	                //display the initial value
	                $('<option value="0">&lt;Select&gt;</option>').appendTo("#from-step-control");
	                //add the returned html
	                $("#from-step-control").append(returnedHTML);
                    $("#to-step-control option").remove();
	                //display the initial value
	                $('<option value="0">&lt;Select&gt;</option>').appendTo("#to-step-control");
	                //add the returned html
	                $("#to-step-control").append(returnedHTML);	                
	            }, "html");
	        }



}
function loadCadreBySalaryTypeId(pElem){

	 if(pElem.value == "-1"){
	        $("#cadre-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#cadre-control");
	    }
	     //load the Cadres
	        else{

	             $.get("getCadresForSalaryType.do", { salaryTypeId: pElem.value}, function( returnedHTML ) {

	                $("#cadre-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#cadre-control");
	                //add the returned html
	                $("#cadre-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadRanksByParentClientId(pElem){

	 if(pElem.value == "-1"){
	        $("#pen-rank-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#pen-rank-control");
	    }
	     //load the departments
	        else{

	             $.get("getRanksByBusinessClient.do", { parentClientId: pElem.value}, function( returnedHTML ) {

	                $("#pen-rank-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#pen-rank-control");
	                //add the returned html
	                $("#pen-rank-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadStateByCity(pElem){

	 if(pElem.value == "-1"){
	        $("#state-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#state-control");
	    }
	     //load the departments
	        else{

	             $.get("getStatesForCity.do", { cityId: pElem.value}, function( returnedHTML ) {

	                $("#state-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#state-control");
	                //add the returned html
	                $("#state-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadPayTypes(pElem){

	 if(pElem.value == "-1"){
	        $("#pay-type-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#pay-type-control");
	    }
	     //load the departments
	        else{

	             $.get("getSpecAllowTypePayType.do", { satId: pElem.value}, function( returnedHTML ) {

	                $("#pay-type-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#pay-type-control");
	                //add the returned html
	                $("#pay-type-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadNOKStateByCityId(pElem){

	 if(pElem.value == "-1"){
	        $("#nok-state-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#nok-state-control");
	    }
	     //load the departments
	        else{

	             $.get("getStatesForCity.do", { cityId: pElem.value}, function( returnedHTML ) {

	                $("#nok-state-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#nok-state-control");
	                //add the returned html
	                $("#nok-state-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadRanksByCadreId(pElem){

	 if(pElem.value == "-1"){
	        $("#rank-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#rank-control");
	    }
	     //load the departments
	        else{

	             $.get("getRanksForCadre.do", { cadreInstId: pElem.value}, function( returnedHTML ) {

	                $("#rank-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#rank-control");
	                //add the returned html
	                $("#rank-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadUnassignedDepartments(pElem){

	 if(pElem.value == "0"){
	        $("#department-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#department-control");
	    }
	     //load the departments
	        else{
	        	
	             $.get("getUnassignedDepartmentsByMda.do", { mdaId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#department-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#department-control");
	                //add the returned html
	                $("#department-control").append(returnedHTML);
	            }, "html");
	        }
	
}
function loadDepartments(pElem){
	
	 if(pElem.value == "0"){
	        $("#department-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#department-control");
	    }
	     //load the departments
	        else{
	        	
	             $.get("getDepartmentsByMda.do", { mdaId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#department-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#department-control");
	                //add the returned html
	                $("#department-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadSchoolsByMdaId(pElem){
	
	 if(pElem.value == "0"){
	        $("#mda-school-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#mda-school-control");
	    }
	     //load the schools
	        else{
	        	
	             $.get("getSchoolByMdaId.do", { mdaId: pElem.value}, function( returnedHTML ) {
	            	if(returnedHTML != ""){
	            		$("#school-control").show();
	            		$("#school-ind-control").show();
	            	}else{
	            		$("#school-control").hide();
	            		$("#school-ind-control").hide();
	            	}
	                $("#mda-school-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#mda-school-control");
	                //add the returned html
	                $("#mda-school-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function enableDisableSchool(pElem){
	
	 if(pElem.value == "-1"){
	        
	        $("#schoolrow-control").css("display", "none"); 
	    }
	    
	        else{
	        	  
	             $.get("mdaIsShoolEnabled.do", { mdaId: pElem.value}, function( returnedHTML ) {
	            	
	            	  
	            	 if(returnedHTML.value == "Y"){
	            		 $("#schoolrow-control").show(); 
	            	 }else{
	            		 $("#schoolrow-control").hide(); 
	            	 }
	                 
	            }, "html");
	        }
	
	
	
}

function loadSalaryType(pElem){
	
	 if(pElem.value <= "0"){
	        $("#salary-type-control").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#salary-type-control");
	    }
	     //load the salary Types
	        else{
	        	
	             $.get("getSalaryTypes.do", { mdaId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#salary-type-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#salary-type-control");
	                //add the returned html
	                $("#salary-type-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadBankBranchesByBankId(pElem){
	
	 if(pElem.value == "-1"){
	        $("#bank-branch-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#bank-branch-control");
	    }
	     //load the sectors
	        else{
	        	
	             $.get("getBranchesByBank.do", { bankId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#bank-branch-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#bank-branch-control");
	                //add the returned html
	                $("#bank-branch-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadMonthsForPayrollRunYear(pElem){
	
	 if(pElem.value == "-1"){
	        $("#year-month-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#year-month-control");
	    }
	     //load the sectors
	        else{
	        	
	             $.get("getMonthsForPayrollRunYear.do", { pRunMonth: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#year-month-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#year-month-control");
	                //add the returned html
	                $("#year-month-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadBankBranchByBankId(pElem){
	
	 if(pElem.value == "-1"){
	        $("#bank-branch2-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#bank-branch2-control");
	    }
	     //load the sectors
	        else{
	        	
	             $.get("getBranchesByBank.do", { bankId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#bank-branch2-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#bank-branch2-control");
	                //add the returned html
	                $("#bank-branch2-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadMdaByTypeId(pElem){
	
	 if(pElem.value == "0"){
	        $("#mda-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="0">&lt;Select&gt;</option>').appendTo("#mda-control");
	    }
	     //load the sectors
	        else{
	        	
	             $.get("getMdasByMdaInd.do", { typeInd: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#mda-control option").remove();
	                //display the initial value
	                $('<option value="0">&lt;Select&gt;</option>').appendTo("#mda-control");
	                //add the returned html
	                $("#mda-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}



function loadSalaryLevelAndStepBySalaryTypeId(pElem){
	
	 if(pElem.value == "0"){
	        $("#levelStep-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#levelStep-control");
	    }
	      
	        else{
	        	
	             $.get("getLevelAndSteps.do", { salaryTypeId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#levelStep-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#levelStep-control");
	                //add the returned html
	                $("#levelStep-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadSalaryTypeLevelAndStepByRankId(pElem){

	 if(pElem.value == "-1"){
	        $("#levelStep-lga-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#levelStep-lga-control");
	    }

	        else{

	             $.get("getSalaryInfoByRankId.do", { rankInstId: pElem.value}, function( returnedHTML ) {

	                $("#levelStep-lga-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#levelStep-lga-control");
	                //add the returned html
	                $("#levelStep-lga-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadSalaryTypeByRankId(pElem){

	 if(pElem.value == "-1"){
	        $("#salary-type-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#salary-type-control");
	    }

	        else{

	             $.get("getSalaryTypesByRankId.do", { rankInstId: pElem.value}, function( returnedHTML ) {

	                $("#salary-type-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#salary-type-control");
	                //add the returned html
	                $("#salary-type-control").append(returnedHTML);
	            }, "html");
	        }



}
function loadGradeLevels(pElem){
	
	 if(pElem.value == "0"){
	        $("#grade-level-control option").remove(); //empty the dropdown
	        $("#grade-to-level-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="0">&lt;From Level&gt;</option>').appendTo("#grade-level-control");
	         $('<option value="0">&lt;To Level&gt;</option>').appendTo("#grade-to-level-control");
	    }
	     
	        else{
	        	
	             $.get("getLevels.do", { salaryTypeId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#grade-level-control option").remove();
	                //display the initial value
	                $('<option value="0">&lt;From Level&gt;</option>').appendTo("#grade-level-control");
	                //add the returned html
	                $("#grade-level-control").append(returnedHTML);
	                $("#grade-to-level-control option").remove();
	                //display the initial value
	                $('<option value="0">&lt;To Level&gt;</option>').appendTo("#grade-to-level-control");
	                //add the returned html
	                $("#grade-to-level-control").append(returnedHTML);
	            }, "html");
	             
	        }
	
	
	
}
function loadLGAByStateId(pElem){
	
	 if(pElem.value == "-1"){
	        $("#lga-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#lga-control");
	    }
	     //load the sectors
	        else{

	             $.get("getLGAsByState.do", { stateId: pElem.value}, function( returnedHTML ) {
	            	 	            	 
	                $("#lga-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#lga-control");
	                //add the returned html
	                $("#lga-control").append(returnedHTML);
	            }, "html");
	        }
	
	
	
}
function loadCityByStateId(pElem){

	 if(pElem.value == "-1"){
	        $("#city-control option").remove(); //empty the dropdown
	         //<Select>
	         $('<option value="-1">&lt;Select&gt;</option>').appendTo("#city-control");
	    }
	     //load the sectors
	        else{

	             $.get("getCitiesByStateId.do", { stateId: pElem.value}, function( returnedHTML ) {

	                $("#city-control option").remove();
	                //display the initial value
	                $('<option value="-1">&lt;Select&gt;</option>').appendTo("#city-control");
	                //add the returned html
	                $("#city-control").append(returnedHTML);
	            }, "html");
	        }



}