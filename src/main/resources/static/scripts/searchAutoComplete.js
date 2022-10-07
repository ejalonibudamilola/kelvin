jQuery(function(){
		makeEmployeeIdFieldAutocomplete( "empId", APP_CONTEXT + "/getEmployeeIds.do", "employeeId", 20, "employee_names_div", "staff_name_display_span", "activeInd");
	});

	jQuery(function(){
    		makeEmployeeIdFieldAutocomplete( "legEmpId", APP_CONTEXT + "/getLegacyEmployeeIds.do", "legacyEmployeeId", 20, "employee_leg_names_div", "staff_name_legacy_display_span", "legActiveInd");
    	});
	jQuery(function(){
		makeEmployeeIdFieldAutocomplete( "fName", APP_CONTEXT + "/getEmployeeUniqueNames.do", "firstName", 20, "employee_names_div", "staff_name_display_span" );
	});
	
	jQuery(function(){
		makeEmployeeIdFieldAutocomplete( "lName", APP_CONTEXT + "/getEmployeeUniqueNames.do", "lastName", 20, "employee_names_div", "staff_name_display_span" );
	});
	
	