package com.osm.gnl.ippms.ogsg.controllers.nextofkin;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKinDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping({"/viewNextOfKin.do"})
@SessionAttributes({"nokBean"})
public class ViewNextOfKinInfoFormController extends BaseController {

	  public ViewNextOfKinInfoFormController()
	  {

	  }
	
	
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"eid","dest"})
	  public String setupForm(@RequestParam("eid") Long pEmpId,@RequestParam("dest") String pDesignation,Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	     
	    List<NextOfKin> wNextOfKinList = genericService.loadAllObjectsWithSingleCondition(NextOfKin.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId), null);

	    Collections.sort(wNextOfKinList, Comparator.comparing(NextOfKin::getLastName).thenComparing(NextOfKin::getFirstName));
	    
	    NextOfKinDTO wNextOfKinDto = new NextOfKinDTO();
	    if(wNextOfKinList.size() > 1){
	    	wNextOfKinDto.setPrimaryNextOfKin(wNextOfKinList.get(0));
	    	wNextOfKinDto.setSecondaryNextOfKin(wNextOfKinList.get(1));
	    	wNextOfKinDto.setHasSecondaryNextOfKin(true);
	    }else{
	    	wNextOfKinDto.setPrimaryNextOfKin(wNextOfKinList.get(0));
	    }

	    wNextOfKinDto.setName(pDesignation);
	    wNextOfKinDto.setParentInstId(pEmpId);
	    
	     
	    HiringInfo wHireInfo = loadHiringInfoByEmpId(request,bc,pEmpId);
	    if(wHireInfo.isTerminatedEmployee() || wHireInfo.isSuspendedEmployee())
	    	wNextOfKinDto.setCanNotEdit(true);
	    model.addAttribute("employee",wHireInfo.getAbstractEmployeeEntity());
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("nokBean",wNextOfKinDto);
	    return "employee/viewNextOfKinForm";
	  }

	  
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @RequestParam(value="_add", required=false) String pAdd, 
			  @ModelAttribute("nokBean") NextOfKinDTO pNextOfKin, BindingResult result, 
			  SessionStatus status, Model model, HttpServletRequest request) throws InstantiationException, IllegalAccessException, HttpSessionRequiredException, EpmAuthenticationException {

		  SessionManagerService.manageSession(request, model);

	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	    {
	     
	    	 if(pNextOfKin.getName().equalsIgnoreCase("over")){
	 	        return "redirect:employeeOverviewForm.do?eid="+pNextOfKin.getParentInstId();

	     	}else{
	 	        return "redirect:employeeEnquiryForm.do?eid="+pNextOfKin.getParentInstId();

	     	}
	     
	    }
	    
	   return "redirect:addNextOfKin.do?eid="+pNextOfKin.getParentInstId()+"&dest="+pNextOfKin.getName();
	    
	}

	  


}
