package com.osm.gnl.ippms.ogsg.controllers.error;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PassPhrase;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.EmployeeMiniBean;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;




@Controller
@RequestMapping({"/setUpEmpErrorForm.do","/setUpPenErrorForm.do"})
@SessionAttributes(types = PaginatedPaycheckGarnDedBeanHolder.class)
public class SetupEmployeeErrorFormController extends BaseController {


	  private final int pageLength = 10;
	  private final String VIEW_NAME = "employee/setupEmpErrorViewForm";

	@Autowired
	public SetupEmployeeErrorFormController() {}



 	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity) getSessionAttribute(request, EMP_SKEL);

		List<EmployeeMiniBean> wErrorList = (List<EmployeeMiniBean> ) getSessionAttribute(request, EMP_SKEL_ERR);

		if(wEmp == null || wErrorList == null){
		    if(bc.isPensioner())
		        return "redirect:createNewPensioner.do";
			return "redirect:createNewEmployee.do";
		}

		//-- If we get here...set MDA Information for all Employees in EmployeeMiniBean....
		SetupEmployeeMaster wSEM = this.createSetupEmployeeMaster(wEmp, bc);
		List<SetupEmployeeDetails> wAllList = this.createSetupEmployeeDetails(wSEM.getId(),wErrorList, bc);
		List<SetupEmployeeDetails> wNewList;
		PaginationBean paginationBean = this.getPaginationInfo(request);
		if(wAllList.size() > 10){

			wNewList = (List<SetupEmployeeDetails>) PayrollUtils.paginateList(paginationBean.getPageNumber(),this.pageLength,wAllList);
		}else{
			wNewList = wAllList;
		}

		for (SetupEmployeeDetails e : wNewList) {
			//Employee emp = (Employee) this.genericService.loadObjectByClassAndId(Employee.class, e.getId());
			AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,e.getParentId(),bc, e.getBusinessClientId());
			if (!emp.isNewEntity())
				e.setMdaName(emp.getMdaDeptMap().getMdaInfo().getName());
			if (emp.isSchoolStaff())
				e.setSchoolName(emp.getSchoolInfo().getName());
			else
				e.setSchoolName("");
			e.setPayGroup(emp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
		}

		PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), this.pageLength, wErrorList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());

		if(bc.isPensioner())
		    wPGBDH.setPageUrl("setUpPenErrorForm.do");
		else
		    wPGBDH.setPageUrl("setUpEmpErrorForm.do");

		wPGBDH.setId(bc.getLoginId());
		wPGBDH.setSomeObject(wSEM);
		wPGBDH.setEmployeeId(wSEM.getEmployeeId());
		wPGBDH.setBeanList(wNewList);
		wPGBDH.setPaginationListHolder(wErrorList);
		model.addAttribute("roleBean", bc);
		model.addAttribute("miniBean", wPGBDH);
		model.addAttribute("employee", wSEM);
		removeSessionAttribute(request, EMP_SKEL);
		removeSessionAttribute(request, EMP_SKEL_ERR);
		return VIEW_NAME;


	}

	
	@RequestMapping(method = RequestMethod.GET,params={"mid"})
	public String setupForm(@RequestParam("mid") Long pMid,Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		PaginationBean paginationBean = this.getPaginationInfo(request);

		SetupEmployeeMaster wSEM = this.genericService.loadObjectUsingRestriction(SetupEmployeeMaster.class,
				Arrays.asList(CustomPredicate.procurePredicate("id",pMid),CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));

		List<SetupEmployeeDetails> wAllList = this.genericService.loadAllObjectsWithSingleCondition(SetupEmployeeDetails.class,
				CustomPredicate.procurePredicate("setupEmployeeMaster.id", pMid),null);
		Collections.sort(wAllList,Comparator.comparing(SetupEmployeeDetails::getLastName).thenComparing(SetupEmployeeDetails::getFirstName));
//		List<SetupEmployeeDetails> wNewList = null;
//		if(wAllList.size() > 10){
//			wNewList = (List<SetupEmployeeDetails>) PayrollUtils.paginateList(paginationBean.getPageNumber(),this.pageLength,
//					wAllList);
//		}else{
//			wNewList = wAllList;
//		}
		for(SetupEmployeeDetails e : wAllList){
			AbstractEmployeeEntity emp = IppmsUtils.loadEmployee(genericService,e.getParentId(),bc, e.getBusinessClientId());
			if (!emp.isNewEntity()) {
				e.setMdaName(emp.getMdaDeptMap().getMdaInfo().getName());
				e.setPayGroup(emp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
			}
			if (emp.isSchoolStaff())
				e.setSchoolName(emp.getSchoolInfo().getName());
			else
				e.setSchoolName("");
		}
		PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wAllList, paginationBean.getPageNumber(), this.pageLength, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());

		wPGBDH.setId(bc.getLoginId());
		wPGBDH.setSomeObject(wSEM);
		wPGBDH.setEmployeeId(wSEM.getEmployeeId());
		wPGBDH.setBeanList(wAllList);
		wPGBDH.setUser(true);
		wPGBDH.setPaginationListHolder(wAllList);
		model.addAttribute("roleBean", bc);
		model.addAttribute("miniBean", wPGBDH);
		model.addAttribute("employee", wSEM);

		return VIEW_NAME;


	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
			@RequestParam(value = REQUEST_PARAM_APPROVE, required = false) String approve,
			@RequestParam(value = REQUEST_PARAM_REJECT, required = false) String reject,
			@RequestParam(value = REQUEST_PARAM_DELETE, required = false) String delete,
			@RequestParam(value = REQUEST_PARAM_CONFIRM, required = false) String confirm,
			@ModelAttribute ("miniBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result,
			SessionStatus status, Model model,HttpServletRequest request) throws Exception {


		SessionManagerService.manageSession(request, model);
	    BusinessCertificate bc = super.getBusinessCertificate(request);


		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			 return DETERMINE_DASHBOARD_URL;
		}

		if(isButtonTypeClick(request,REQUEST_PARAM_CONFIRM)){
			if(!pEHB.getEnteredCaptcha().equalsIgnoreCase(pEHB.getGeneratedCaptcha())){
				  result.rejectValue("", "No.Employees", "Entered Captcha does not match Generated Captcha..");

			         model.addAttribute(DISPLAY_ERRORS, BLOCK);
			         model.addAttribute("pageErrors", result);
			         model.addAttribute("miniBean", pEHB);
			         model.addAttribute("employee", pEHB.getSomeObject());
			         model.addAttribute("roleBean", bc);
			         return VIEW_NAME;
			  }
			if(pEHB.isDeleteWarningIssued()){

				SetupEmployeeMaster wSEM = (SetupEmployeeMaster)pEHB.getSomeObject();
				wSEM.setDeletedBy(bc.getUserName());
				this.genericService.storeObject(wSEM);
				this.genericService.deleteObject(wSEM);
			}
			if(pEHB.isEditMode()){

				 SetupEmployeeMaster wSEM = (SetupEmployeeMaster)pEHB.getSomeObject();
				 wSEM.setApprovedBy(bc.getUserName());
				 wSEM.setApprovedInd(ON);
				 wSEM.setApprovedDate(LocalDate.now());
				 this.genericService.storeObject(wSEM);

			}
			if(pEHB.isRejectWarningIssued()){
				SetupEmployeeMaster wSEM = (SetupEmployeeMaster)pEHB.getSomeObject();
				 wSEM.setApprovedBy(bc.getUserName());
				 wSEM.setApprovedDate(LocalDate.now());
				 wSEM.setRejectedStatus(ON);
				 wSEM.setRejectedInd(ON);
				 this.genericService.storeObject(wSEM);
			}
		}

		if(isButtonTypeClick(request,REQUEST_PARAM_DELETE)){


				 pEHB.setDeleteWarningIssued(true);
				 pEHB.setConfirmation(true);
				 pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
			    	 result.rejectValue("", "No.Employees", "Click on the 'Confirm' button to delete record.");

			    	 model.addAttribute(DISPLAY_ERRORS, BLOCK);
			         model.addAttribute("pageErrors", result);
			         model.addAttribute("miniBean", pEHB);
			         model.addAttribute("employee", pEHB.getSomeObject());
			         model.addAttribute("roleBean", bc);
			         return VIEW_NAME;




		}

		if(isButtonTypeClick(request,REQUEST_PARAM_APPROVE)){


				 pEHB.setConfirmation(true);
				 pEHB.setEditMode(true);
				 pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
			    	 result.rejectValue("", "No.Employees", "Please Enter the Generated Captcha for Approval..");

			    	 model.addAttribute(DISPLAY_ERRORS, BLOCK);
			         model.addAttribute("pageErrors", result);
			         model.addAttribute("miniBean", pEHB);
			         model.addAttribute("employee", pEHB.getSomeObject());
			         model.addAttribute("roleBean", bc);
			         return VIEW_NAME;






		}

		if(isButtonTypeClick(request,REQUEST_PARAM_REJECT)){

				pEHB.setConfirmation(true);
				 pEHB.setRejectWarningIssued(true);
				 pEHB.setGeneratedCaptcha(PassPhrase.generateCapcha(6));
			    	 result.rejectValue("", "No.Employees", "Please Enter the Generated Captcha for Rejection.");

			    	 model.addAttribute(DISPLAY_ERRORS, BLOCK);
			         model.addAttribute("pageErrors", result);
			         model.addAttribute("miniBean", pEHB);
			         model.addAttribute("employee", pEHB.getSomeObject());
			         model.addAttribute("roleBean", bc);
			         return VIEW_NAME;




		}

		  if(bc.isSuperAdmin()){
			  PredicateBuilder predicateBuilder = new PredicateBuilder()
					  .addPredicate(CustomPredicate.procurePredicate("rejectedInd", IConstants.OFF))
					  .addPredicate(CustomPredicate.procurePredicate("approvedInd", IConstants.OFF))
					  .addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));

			  PredicateBuilder predicateBuilder2 = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));


			  if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, SetupEmployeeMaster.class) > 0)
				  return "redirect:viewPendingConflicts.do";
			  else if(this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder2,SetupEmployeeMaster.class) > 0
					  && ! IppmsUtils.treatNull(Navigator.getInstance(getSessionId(request)).getFromForm()).equals(EMPTY_STR))
				  return Navigator.getInstance(getSessionId(request)).getFromForm();
			  else
				  return DETERMINE_DASHBOARD_URL;
		  }
		  return "redirect:createNewEmployee.do";

	}


	private SetupEmployeeMaster createSetupEmployeeMaster(AbstractEmployeeEntity pEmp, BusinessCertificate pBc) throws Exception{

			SetupEmployeeMaster wSEM = new SetupEmployeeMaster();

			wSEM.setEmployeeId(pEmp.getEmployeeId());
			wSEM.setMdaName(pEmp.getMdaDeptMap().getMdaInfo().getName());
			if(pEmp.isSchoolStaff()){
				wSEM.setSchoolName(pEmp.getSchoolInfo().getName());
			}else{
				wSEM.setSchoolName(null);
			}
			wSEM.setPayGroup(pEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
			wSEM.setFirstName(pEmp.getFirstName());
			wSEM.setLastName(pEmp.getLastName());
			wSEM.setInitials(pEmp.getInitials());
			wSEM.setPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, pBc));
			wSEM.setLastModBy(pBc.getUserName());
			wSEM.setLastModTs(LocalDate.now());
			wSEM.setBusinessClientId(pBc.getBusinessClientInstId());


			this.genericService.saveObject(wSEM);

			return wSEM;


	}

	private List<SetupEmployeeDetails> createSetupEmployeeDetails(Long pSetupEmpMasterId, List<EmployeeMiniBean> pErrorList, BusinessCertificate bc)
	{
		List<SetupEmployeeDetails> wRetList = new ArrayList<SetupEmployeeDetails>();

		for(EmployeeMiniBean wEMB : pErrorList){
			SetupEmployeeDetails s = new SetupEmployeeDetails();
			s.setSetupEmployeeMaster(new SetupEmployeeMaster(pSetupEmpMasterId));
			if(bc.isPensioner())
				s.setPensioner(new Pensioner(wEMB.getId()));
			 else
			 	s.setEmployee(new Employee(wEMB.getId()));
			s.setEmployeeId(wEMB.getEmployeeId());
			s.setFirstName(wEMB.getFirstName());
			s.setLastName(wEMB.getLastName());
			s.setInitials(wEMB.getInitials());
			s.setGsmNumber(wEMB.getAccountNumber());
			s.setBusinessClientName(wEMB.getName());
			s.setBusinessClientId(wEMB.getPaycheckId());
			this.genericService.saveObject(s);
			wRetList.add(s);
		}
		return wRetList;
	}

}
