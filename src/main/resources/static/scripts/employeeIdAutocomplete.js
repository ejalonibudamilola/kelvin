	/**
	 *
	 *@param inputFieldId the 'id' of the input field to add autocomplete behaviour to.
	 *@param searchUrl, the 'url' to send search values to
	 *@param maxRowsToFetch to maximum number of rows to fetch 
	 *@param namesContainerId the 'id' of the container(i.e div, span, etc) where the names to to be displayed
	 *       are kept. the styling of this element should make sure it's hidden from display e.g style="display: none;"
	 *@param nameDisplayContainerId is the 'id' of the container that displays the name of the selected value.
	 *@param activeInd the 'id' of the input field that holds the status of the employee list loaded.
	 */
	function makeEmployeeIdFieldAutocomplete( inputFieldId, searchUrl, param, maxRowsToFetch, namesContainerId, nameDisplayContainerId, activeInd) {
		var inputField = document.getElementById( inputFieldId );
		var activeId = document.getElementById(activeInd).value;
		console.log("activeind is"+activeId);
		jQuery( inputField ).autocomplete({
			source: function( request, response ) {
				jQuery.ajax({
					url: searchUrl,
					dataType: "json",
					data: { employee_id: request.term, maxRows: maxRowsToFetch, emp_param : param, activeInd : activeId},
					success: function( data ) {

						var employeeIds = jQuery.map( data, function( element, index ) {

							if(param.toLowerCase() == 'employeeid')
								return element.employeeId;
						    else if(param.toLowerCase() == 'legacyemployeeid')
						        return element.legacyEmployeeId;
							else
								return element;
						});

						//get the full employee object itself
						var employees = jQuery.map(data, function( element, index ) {
							return element;
						});
                        if(param.toLowerCase() == 'employeeid')
                        	createEmployeeNameSpansForEmployeeIds( namesContainerId, employees );
                        else
                           createLegacyEmployeeNameSpansForEmployeeIds( namesContainerId, employees );


						response( employeeIds );
					}
				});
			},//end source

			minLength : 1,

			//handle mouse over of values...display in a span
			focus : function( event, ui ){
				displayEmployeeName( event, ui, nameDisplayContainerId );
			},
			
			change : function( event, ui ){
				displayEmployeeName( event, ui, nameDisplayContainerId );
			}
			
		});
	 }

		function displayEmployeeName( event, ui, nameDisplayContainerId ) {
			var nameSpan = jQuery( "#" + nameDisplayContainerId );
			
			//get the hidden input used for the employee display name and delete it
			jQuery( nameSpan ).find( 'input' ).eq( 0 ).remove();

			//clear the <span> text...this removes everything in the span including html text.
			nameSpan.text( "" );
			if( ui.item ) {//if ui.item is not null
				//get the employee name for the selected employee id
				var field = jQuery( "#" + ui.item.value ).get();
				if( field.length > 0 ) {
					var employeeName = jQuery(field).html();
					jQuery( nameSpan ).html( "<i><b>" + employeeName + "</b></i>" );
					jQuery( "<input type='hidden' name='employeeName' value='" + employeeName +"' />" ).appendTo( nameSpan );
				}
			}
		}

		function clearNamesContainer( containerId ) {
			//clear out the previous names
			jQuery( "#" + containerId + " span" ).remove();
		}

		function createEmployeeNameSpansForEmployeeIds( containerId, employeesArray  ) {
			
			clearNamesContainer( containerId );
			
			jQuery.each( employeesArray, function( index, elem ){
				var firstName = elem.firstName == null ? "" : elem.firstName;
				var lastName = elem.lastName == null ? "" : elem.lastName;
				var initials = elem.middleName == null ? "" : elem.middleName;
				var mdaName = elem.mdaName == null ? "" : elem.mdaName;
				var status = elem.titleField == null ? "" : elem.titleField;
				var schName = elem.schoolName == null ? "" : elem.schoolName;
				var levelStep = elem.salaryScaleName == null ? "" : elem.salaryScaleName;
				
				var nameHtml = "<span id='"+ elem.employeeId + "'> <span style='color:" +((status.toLowerCase()=='active') ? "blue" : "red") + " !important;'>"
				+ lastName + " " + firstName + " " + initials +" [ " + mdaName + " - " + schName + " (" + levelStep + ")" + " ]</span></span>";
				
				jQuery(nameHtml).appendTo( "#" + containerId )
				
			} )
		}
		function createLegacyEmployeeNameSpansForEmployeeIds( containerId, employeesArray  ) {

        			clearNamesContainer( containerId );

        			jQuery.each( employeesArray, function( index, elem ){
        				var firstName = elem.firstName == null ? "" : elem.firstName;
        				var lastName = elem.lastName == null ? "" : elem.lastName;
        				var initials = elem.middleName == null ? "" : elem.middleName;
        				var mdaName = elem.mdaName == null ? "" : elem.mdaName;
        				var status = elem.titleField == null ? "" : elem.titleField;
        				var schName = elem.schoolName == null ? "" : elem.schoolName;
        				var levelStep = elem.salaryScaleName == null ? "" : elem.salaryScaleName;

        				var nameHtml = "<span id='"+ elem.legacyEmployeeId + "'> <span style='color:" +((status.toLowerCase()=='active') ? "blue" : "red") + " !important;'>"
        				+ lastName + " " + firstName + " " + initials +" [ " + mdaName + " - " + schName + " (" + levelStep + ")" + " ]</span></span>";

        				jQuery(nameHtml).appendTo( "#" + containerId )

        			} )
        		}