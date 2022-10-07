package com.osm.gnl.ippms.ogsg.controllers.ajax;

import com.osm.gnl.ippms.ogsg.base.services.SearchService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.employee.beans.EmployeeBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
public class MultiActionFilterController extends BaseController {
	
	 
	private static final int MAX_RETURN_ROWS = 10;
	@Autowired
	private SearchService searchService;


	public MultiActionFilterController()
	  {
	   
	  }

	
	  @RequestMapping( value = "/getEmployeeIds.do" )
		public @ResponseBody List<EmployeeBean> searchForEmployeeId(
				@RequestParam( "employee_id" )String employeeId, 
				@RequestParam( value = "maxRows", required = false )Integer maxRowsToReturn,
				@RequestParam( "emp_param" )String wParam,
				@RequestParam( "activeInd" )Integer activeInd,
				HttpServletRequest pRequest )  throws Exception {
			
		  BusinessCertificate bc = super.getBusinessCertificate(pRequest);

			final List<EmployeeBean> retVal = new ArrayList<EmployeeBean>();
			
				if( maxRowsToReturn == null || maxRowsToReturn == 0 ) {
					maxRowsToReturn = MAX_RETURN_ROWS;
				}
				
				List<AbstractEmployeeEntity> employeeList = this.searchService.searchForEmployeeId( bc, employeeId, 0, maxRowsToReturn ,"employeeId", activeInd);
				//List<Employee> employeeList = this.payrollService.searchForEmployeeByParam( employeeId, wParam, 0, maxRowsToReturn, bc.getBusinessClientInstId() );


				for( AbstractEmployeeEntity employee : employeeList ) {
					EmployeeBean empDTO = new EmployeeBean();
					empDTO.setRefNumber( employee.getId().toString() );
					empDTO.setEmployeeId( employee.getEmployeeId() );
					empDTO.setFirstName( employee.getFirstName() );
					empDTO.setLastName( employee.getLastName() );
					empDTO.setMiddleName( employee.getInitials() );
					empDTO.setMdaName(employee.getMdaName());
					empDTO.setTitleField(employee.isTerminated() == true ? "InActive" : "Active");
					empDTO.setSchoolName(employee.getSchoolName());
					empDTO.setSalaryScaleName(employee.getLevelStepStr());
					retVal.add( empDTO );
				}

				return retVal;
		}
	@RequestMapping( value = "/getLegacyEmployeeIds.do" )
	public @ResponseBody List<EmployeeBean> searchForLegacyEmployeeId(
			@RequestParam( "employee_id" )String employeeId,
			@RequestParam( value = "maxRows", required = false )Integer maxRowsToReturn,
			@RequestParam( "emp_param" )String wParam,
			HttpServletRequest pRequest )  throws Exception {

		BusinessCertificate bc = super.getBusinessCertificate(pRequest);

		final List<EmployeeBean> retVal = new ArrayList<>();

		if( maxRowsToReturn == null || maxRowsToReturn == 0 ) {
			maxRowsToReturn = MAX_RETURN_ROWS;
		}

		List<AbstractEmployeeEntity> employeeList = this.searchService.searchForEmployeeId( bc, employeeId, 0, maxRowsToReturn, "legacyEmployeeId", 0);
		//List<Employee> employeeList = this.payrollService.searchForEmployeeByParam( employeeId, wParam, 0, maxRowsToReturn, bc.getBusinessClientInstId() );


		for( AbstractEmployeeEntity employee : employeeList ) {
			EmployeeBean empDTO = new EmployeeBean();
			empDTO.setRefNumber( employee.getId().toString() );
			empDTO.setEmployeeId( employee.getEmployeeId() );
			empDTO.setFirstName( employee.getFirstName() );
			empDTO.setLastName( employee.getLastName() );
			empDTO.setMiddleName( employee.getInitials() );
			empDTO.setMdaName(employee.getMdaName());
			empDTO.setTitleField(employee.isTerminated() == true ? "InActive" : "Active");
			empDTO.setSchoolName(employee.getSchoolName());
			empDTO.setSalaryScaleName(employee.getLevelStepStr());
			empDTO.setLegacyEmployeeId(employee.getLegacyEmployeeId());
			retVal.add( empDTO );
		}

		return retVal;
	}

	@RequestMapping( value = "/getEmployeeUniqueNames.do" )
		public @ResponseBody List<String> searchForEmployeeUniqueNames( 
				@RequestParam( "employee_id" )String employeeId, 
				@RequestParam( value = "maxRows", required = false )Integer maxRowsToReturn,
				@RequestParam( "emp_param" )String wParam, 
				HttpServletRequest pRequest )  throws Exception {
			
		  BusinessCertificate bc = super.getBusinessCertificate(pRequest);
			
				if( maxRowsToReturn == null || maxRowsToReturn == 0 ) {
					maxRowsToReturn = MAX_RETURN_ROWS;
				}
				
				List<String> employeeList = this.searchService.searchForEmployeeByNames( employeeId, wParam, 0, maxRowsToReturn, bc );
				
				return employeeList;
		}
	  
	
}
