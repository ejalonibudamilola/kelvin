package com.osm.gnl.ippms.ogsg.controllers.nextofkin;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.NextOfKinValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/addNextOfKin.do"})
@SessionAttributes({"nokBean"})
public class AddNextOfKinFormController extends BaseController {

	   @Autowired
	   private NextOfKinValidator nextOfKinValidator;

	  private final String VIEW = "employee/addNextOfKinForm";
	  
	  public AddNextOfKinFormController()
	  {}
	  
	@ModelAttribute("cities")
	  public List<City> populateStates() {
		  return this.genericService.loadAllObjectsWithoutRestrictions(City.class,"name");
	  }
	 


	@ModelAttribute("relationshipTypes")
	  public List<RelationshipType> populateRelationshipType() {
		return this.genericService.loadAllObjectsWithoutRestrictions(RelationshipType.class,"name");
	  }
	  
	  
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"eid"})
	  public String setupForm(@RequestParam("eid") Long pEmpId,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	     return  prepareAndReturnView(pEmpId,null,0, model,request,bc, null);
	  }
		@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"eid","dest"})
		  public String setupForm(@RequestParam("eid") Long pEmpId,@RequestParam("dest") String pDest,Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

			return  prepareAndReturnView(pEmpId,pDest,0, model,request,bc, null);
		  }
	  
		@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"nokId","s"})
		  public String setupForm(@RequestParam("nokId") Long pNextOfKinId,@RequestParam("s") int pSaved,Model model, HttpServletRequest request) throws Exception {
			SessionManagerService.manageSession(request, model);
		      BusinessCertificate bc = super.getBusinessCertificate(request);

			return  prepareAndReturnView(pNextOfKinId,null,pSaved, model,request,bc, null);
		  }
		@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params={"eid","dest","sec"})
		  public String setupForm(@RequestParam("eid") Long pEmpId,
				  @RequestParam("dest") String pDest,@RequestParam("sec") String pSec,Model model, HttpServletRequest request) throws Exception {
			SessionManagerService.manageSession(request, model);
		      BusinessCertificate bc = super.getBusinessCertificate(request);
			return  prepareAndReturnView(pEmpId,pDest,0, model,request,bc, pSec);

		  }
	 
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @ModelAttribute("nokBean") NextOfKin pNextOfKin, BindingResult result, 
			  SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	   

	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	    {
	    	//Now Check if this guy has 2 Next Of Kins Yet...
			if (!StringUtils.isBlank(pNextOfKin.getDisplayName())) {
				if (pNextOfKin.getDisplayName().equalsIgnoreCase("enq")) {
					return "redirect:employeeEnquiryForm.do?eid=" + pNextOfKin.getParentId();
				}
			}
			return "redirect:employeeOverviewForm.do?eid="+pNextOfKin.getParentId();


		}

		nextOfKinValidator.validate(pNextOfKin, result,bc);

	    if (result.hasErrors()) {
	      
	      model.addAttribute(DISPLAY_ERRORS, BLOCK);
	      model.addAttribute("status", result);
	      model.addAttribute("roleBean", bc);
 		  model.addAttribute("nokBean",pNextOfKin);
		  return VIEW;
	    }

	   pNextOfKin.setCreatedBy(new User(bc.getLoginId()));
	   pNextOfKin.setLastModBy(pNextOfKin.getCreatedBy());
	   pNextOfKin.setLastModTs(Timestamp.from(Instant.now()));
	   pNextOfKin.setBusinessClientId(bc.getBusinessClientInstId());
	   if(bc.isPensioner())
	   	pNextOfKin.setPensioner(new Pensioner(pNextOfKin.getParentId()));
	   else
	   	pNextOfKin.setEmployee(new Employee(pNextOfKin.getParentId()));
	   
	   this.genericService.saveObject(pNextOfKin);


	 return "redirect:addNextOfKin.do?nokId="+pNextOfKin.getId()+"&s=1" ;


	  }

	  private String prepareAndReturnView(Long pEmpId,String pDest, int pSaved,Model model, HttpServletRequest request,
										  BusinessCertificate bc, String pSec) throws InstantiationException, IllegalAccessException {


	  	  if(pSaved == 1){

			  NextOfKin wNextOfKin = genericService.loadObjectById(NextOfKin.class, pEmpId);

			  model.addAttribute(IConstants.SAVED_MSG,"Next Of Kin added successfully.");

			  model.addAttribute("confirmation", true);
			  model.addAttribute("saved", true);
			  model.addAttribute("roleBean", bc);
			  model.addAttribute("nokBean",wNextOfKin);
			  return VIEW;
		  }
		  AbstractEmployeeEntity employee = IppmsUtils.loadEmployee(genericService,pEmpId,bc);

		  if ((employee == null) || (employee.isNewEntity())) {
			  if(bc.isPensioner())
				  return "redirect:searchPensionerToCreate.do";
			  return "redirect:setUpNewEmployee.do";
		  }

		  //Now check if this Employee has 2 active Next Of Kins...
		  List<NextOfKin> wNextOfKins =   this.genericService.loadAllObjectsUsingRestrictions(NextOfKin.class,
				  Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), employee.getId())), null);

		  if(wNextOfKins != null && wNextOfKins.size() == 2) {

			  return "redirect:editNextOfKin.do?eid=" + employee.getId();

		  }

		  NextOfKin wNextOfKin = new NextOfKin();
		  wNextOfKin.setRelationshipType(new RelationshipType());
		  wNextOfKin.setCity(new City());
		  wNextOfKin.setAbstractEmployeeEntity(employee);
		  if(IppmsUtils.isNullOrEmpty(pDest))
		  	wNextOfKin.setDisplayName(pDest);

		  model.addAttribute("roleBean", bc);
		  if(IppmsUtils.isNullOrEmpty(pSec))
		      model.addAttribute("confirmation", false);
		  model.addAttribute("nokBean",wNextOfKin);


	  	return VIEW;
	  }
	  
}
