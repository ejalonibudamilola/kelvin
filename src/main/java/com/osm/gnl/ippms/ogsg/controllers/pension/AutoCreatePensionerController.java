package com.osm.gnl.ippms.ogsg.controllers.pension;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.audit.domain.PensionerAudit;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.control.entities.Religion;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.service.EmployeeControllerService;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.*;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PensionerGenerator;
import com.osm.gnl.ippms.ogsg.report.beans.HRReportBean;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.pension.SetupPensionerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/createPensioner.do")
@SessionAttributes("employeeBean")
public class AutoCreatePensionerController extends BaseController {

	private PayrollService payrollService;
	private PensionService pensionService;
	private SetupPensionerValidator validator;
	private final String VIEW = "pension/createPensionerFromEmployee";


	@ModelAttribute("employeeTypeList")
	private List<EmployeeType> makeEmployeeTypeList(HttpServletRequest request){
		return  this.genericService.loadAllObjectsWithSingleCondition(EmployeeType.class, getBusinessClientIdPredicate(request), "name");
	}
    @ModelAttribute("mdaList")
    private List<MdaInfo> makeMdaList(HttpServletRequest request){
        return  this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, getBusinessClientIdPredicate(request), "name");
    }
	@ModelAttribute("maritalStatusList")
	public List<MaritalStatus> makeMaritalStatusList() {
		return this.genericService.loadAllObjectsWithoutRestrictions(MaritalStatus.class,"name");
	}
	@ModelAttribute("religionList")
	public List<Religion> populateReligionList() {
		return this.genericService.loadAllObjectsWithoutRestrictions(Religion.class, "name");
	}
	@ModelAttribute("entrantList")
	public List<HRReportBean> populateEntrantList(HttpServletRequest request) {
		List<HRReportBean> wRetList = new ArrayList<>();
		wRetList.add(new HRReportBean(1, "New Entrant"));
		wRetList.add(new HRReportBean(2, "Existing Pensioner"));
		return wRetList;
	}
	@ModelAttribute("entryMonthList")
	public List<HRReportBean> populateEntryMonth(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
		PayrollFlag payrollFlag = genericService.loadObjectWithSingleCondition(PayrollFlag.class, getBusinessClientIdPredicate(request));
		int startMonth;
		if(!payrollFlag.isNewEntity()){
			startMonth = payrollFlag.getApprovedMonthInd();
		}else{
			startMonth = LocalDate.now().getMonthValue();
		}
		if(startMonth == 12)
			startMonth = 1;
		List<HRReportBean> wRetList = new ArrayList<>();
		wRetList.add(new HRReportBean(startMonth, PayrollBeanUtils.getMonthNameFromInteger(startMonth)));
		wRetList.add(new HRReportBean(startMonth + 1, PayrollBeanUtils.getMonthNameFromInteger(startMonth + 1)));
		return wRetList;
	}


	@Autowired
	public AutoCreatePensionerController(PayrollService payrollService, PensionService pensionService, SetupPensionerValidator validator) {
		this.payrollService = payrollService;
		this.pensionService = pensionService;
		this.validator = validator;
	}
	@RequestMapping(method = RequestMethod.GET, params={"eid","bid"})
	public String setupForm(@RequestParam("eid") Long pEmpId,@RequestParam("bid") Long pParentBizId, Model model, HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request);
		BusinessCertificate bc = super.getBusinessCertificate(request);



		NewPensionerBean wEHB = PensionerGenerator.getEmployeeInfoDetails(genericService,payrollService,pEmpId,pParentBizId,pensionService,bc);

		addRoleBeanToModel(model, request);
   		model.addAttribute("employeeBean", wEHB);

		Navigator.getInstance(IppmsEncoder.getSessionKey()).setFromClass(AutoCreatePensionerController.class);
		Navigator.getInstance(IppmsEncoder.getSessionKey()).setFromForm("createPensioner.do?eid="+pEmpId+"&bid="+pParentBizId);

		return VIEW;
		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,@ModelAttribute ("employeeBean") NewPensionerBean pEHB, 
			BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			return REDIRECT_TO_DASHBOARD;
		}
				
		  validator.validate(pEHB, result,bc);
		ConfigurationBean configurationBean = this.genericService.loadObjectWithSingleCondition(ConfigurationBean.class, getBusinessClientIdPredicate(request));
		if (result.hasErrors()) {
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("employeeBean", pEHB);
			return VIEW;
		}
		
		//Here we create the Objects one after the other....
		Long pensionerId;
		Object wObj = this.makeEmployee(pEHB,bc, request);
		if(wObj.getClass().isAssignableFrom(String.class)){
			//This Means an Error Occurred...
			result.rejectValue("", "Error.Occurred", (String) wObj);
			addDisplayErrorsToModel(model, request);
			addRoleBeanToModel(model, request);
			model.addAttribute("status", result);
			model.addAttribute("employeeBean", pEHB);
			return VIEW;
		}else{
			//This means we have created the Pensioner...
			//Create the HiringInfo...
			//first 
			Pensioner wEmp = (Pensioner) wObj;
			pensionerId =wEmp.getId();
			wObj = this.makeHiringInfo(pEHB,bc,wEmp.getId(),configurationBean);
			if(wObj.getClass().isAssignableFrom(String.class)){

				PensionerAudit wEmpAudit = this.genericService.loadObjectUsingRestriction(PensionerAudit.class, Arrays.asList(CustomPredicate.procurePredicate("employee.id", wEmp.getId()),
						getBusinessClientIdPredicate(request)));
				this.genericService.deleteObject(wEmpAudit);
				this.genericService.deleteObject(wEmp);
				result.rejectValue("", "Error.Occurred", (String) wObj);
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("employeeBean", pEHB);
				return VIEW;
			}
			HiringInfo wHInfo = (HiringInfo)wObj;
			
			this.makePaySchedule(pEHB,bc,wEmp.getId());
			
			this.makePaymentMethodInfo(pEHB,bc,wEmp.getId());
			//Now Check if it has NextOfKinInfo
			if(pEHB.isNoNextOfKin()){
				NextOfKin wNOK = pEHB.getNextOfKin();
				wNOK.setId(null);
				wNOK.setEmployee(null);
				wNOK.setPensioner(new Pensioner(pensionerId));
				wNOK.setCity(new City(pEHB.getNokCityId(),pEHB.getNokStateId()));
				wNOK.setRelationshipType(new RelationshipType(pEHB.getRelationTypeId()));
				wNOK.setLastModBy(new User(bc.getLoginId()));
				wNOK.setLastModTs(Timestamp.from(Instant.now()));
			    wNOK.setCreatedBy(wNOK.getLastModBy());
				this.genericService.saveObject(wNOK);
			}
			if(wHInfo.getGratuityAmount() > 0){
				//Now create Gratuity for this employee...
				GratuityInfo wGI = new GratuityInfo();
				wGI.setBusinessClientId(bc.getBusinessClientInstId());
				wGI.setPensioner(wEmp);
				wGI.setGratuityAmount(wHInfo.getGratuityAmount());
				wGI.setOutstandingAmount(wHInfo.getGratuityAmount());
				LocalDate wCal = LocalDate.now();
				wGI.setMonth(wCal.getMonthValue());
				wGI.setYear(wCal.getYear());
				wGI.setLastModBy(new User(bc.getLoginId()));
				wGI.setLastModTs(Timestamp.from(Instant.now()));
				wGI.setCreatedBy(wGI.getLastModBy());
				this.genericService.saveObject(wGI);
			}
			 
			if(pEHB.getCreateLaterInd() == 1){
				return "redirect:addNextOfKin.do?eid="+wEmp.getId();
			} 
				
			return "redirect:pensionerOverviewForm.do?eid="+wEmp.getId();
			 
		}
		
	}



	private Object makePaymentMethodInfo(NewPensionerBean pEHB,
			BusinessCertificate pBc, Long pId)
	{
		 PaymentMethodInfo wPaymentMethodInfo = pEHB.getPaymentMethodInfo();
		 wPaymentMethodInfo.setEmployee(null);
		 wPaymentMethodInfo.setBankBranches(new BankBranch(pEHB.getBankBranchId()));
		 //wPaymentMethodInfo.setPaymentMethodTypes(pEHB.getPaymentMethodInfo().getPaymentMethodTypes());
		 wPaymentMethodInfo.setPensioner(new Pensioner(pId));
		 wPaymentMethodInfo.setBusinessClientId(pBc.getBusinessClientInstId());
		 //wPaymentMethodInfo.setAccountType(pEHB.getPaymentMethodInfo().getAccountType());
		 wPaymentMethodInfo.setLastModBy(new User(pBc.getLoginId()));
		 wPaymentMethodInfo.setCreatedBy(wPaymentMethodInfo.getLastModBy());
		// wPaymentMethodInfo.setAccountNumber(pEHB.getAccountNumber());
		 //wPaymentMethodInfo.setBvnNo(pEHB.getBvnNo());
         this.genericService.saveObject(wPaymentMethodInfo);
		return wPaymentMethodInfo;
	}




	private Object makePaySchedule(NewPensionerBean pEHB,
			BusinessCertificate pBc, Long pId)
	{
		 try{
			 PaySchedule wPS = new PaySchedule();
			 wPS.setPensioner(new Pensioner(pId));
			 wPS.setPayPeriod(genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd",ON)));
			 wPS.setPayPeriodDays(genericService.loadObjectWithSingleCondition(PayPeriodDays.class, CustomPredicate.procurePredicate("defaultInd",ON)));

			 this.genericService.saveObject(wPS);
			 return wPS;
		 }catch(Exception wEx){
			 return "An Error Occurred while creating Pay Schedule.";
		 }
		
		
	}




	private Object makeHiringInfo(NewPensionerBean pEHB, BusinessCertificate pBc, Long pPensionerId, ConfigurationBean configBean)
	{
		try{

			HiringInfo wHiringInfo = pEHB.getHiringInfo();
			wHiringInfo.setEmployee(null);
			wHiringInfo.setPensioner(new Pensioner(pPensionerId));
			wHiringInfo.setMaritalStatus(new MaritalStatus(pEHB.getMaritalStatusId()));
			wHiringInfo.setPfaInfo(new PfaInfo(pEHB.getPfaId()));
			wHiringInfo.setPayPeriod(genericService.loadObjectWithSingleCondition(PayPeriod.class, CustomPredicate.procurePredicate("defaultInd", ON)));
			wHiringInfo.setPfaInfo(genericService.loadObjectWithSingleCondition(PfaInfo.class,CustomPredicate.procurePredicate("defaultInd",ON)));
			wHiringInfo.setBusinessClientId(pBc.getBusinessClientInstId());
			wHiringInfo.setTerminateInactive("N");
			wHiringInfo.setHireReportFiled("Y");
			wHiringInfo.setEmployeeVerifyInfo("Y");

			LocalDate localDate = LocalDate.of(wHiringInfo.getBirthDate().getYear()+configBean.getIamAlive(),wHiringInfo.getBirthDate().getMonthValue(), wHiringInfo.getBirthDate().getDayOfMonth());

             wHiringInfo.setAmAliveDate(localDate);
			if(pEHB.getUseDefGratuity() != 2)
			   wHiringInfo.setGratuityAmount(0.0D);
			if(pEHB.getUseDefPension() != 2){
				wHiringInfo.setYearlyPensionAmount(0.0D);
				wHiringInfo.setMonthlyPensionAmount(0.0D);
			}

			wHiringInfo.setLastModBy(new User(pBc.getLoginId()));
			wHiringInfo.setCreatedBy(wHiringInfo.getLastModBy());
			wHiringInfo.setNoOfYearsInOgun(pEHB.getHiringInfo().getNoOfYearsInOgun());
			wHiringInfo.setLastModTs(Timestamp.from(Instant.now()));
			wHiringInfo.setPensionStartDate(pEHB.getHiringInfo().getPensionStartDate());
			this.genericService.saveObject(wHiringInfo);
			return wHiringInfo;
		}catch(Exception wEx){
			wEx.printStackTrace();
			return "An Error Occurred during creation of Hiring Information.";
		}
		
		
	}


	private Object makeEmployee(NewPensionerBean pEHB, BusinessCertificate pBc, HttpServletRequest request) throws Exception
	{
		Pensioner pensioner = pEHB.getPensioner();
		pensioner.setSalaryInfo(pEHB.getSalaryInfo());
		Department department = genericService.loadObjectUsingRestriction(Department.class,Arrays.asList(CustomPredicate.procurePredicate("defaultInd",ON),getBusinessClientIdPredicate(request)));
		pensioner.setMdaDeptMap(genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(CustomPredicate.procurePredicate("mdaInfo.id", pEHB.getMdaId()),
				CustomPredicate.procurePredicate("department.id",department.getId() ))));

		//pensioner.setSalaryInfo(new SalaryInfo(pEHB.getSalaryInfoId()));

		//Set State And LGA
		pensioner.setEmployeeType(new EmployeeType(pEHB.getPensionerTypeId()));
		pensioner.setCity(new City(pEHB.getCityId(), pEHB.getStateId()));
		pensioner.setStateOfOrigin(new State(pEHB.getStateOfOriginId()));
    	pensioner.setLgaInfo(new LGAInfo(pEHB.getLgaId()));
        pensioner.setTitle(new Title(pEHB.getTitleId()));
		pensioner.setBusinessClientId(pBc.getBusinessClientInstId());
		pensioner.setLastModBy(new User(pBc.getLoginId()));
		pensioner.setCreatedBy(pensioner.getLastModBy());
		pensioner.setEntryMonthInd(pEHB.getObjectInd());
		pensioner.setNewEntrantInd(pEHB.getMapId());
		pensioner.setEntryYearInd(LocalDate.now().getYear());
		pensioner.setLastModTs(Timestamp.from(Instant.now()));
        pensioner.setParentBusinessClientId(pEHB.getParentBusinessClientId());
		pensioner.setPensionEndInd(OFF);

		this.genericService.saveObject(pensioner);
		//We need to Create Approval.....
		EmployeeControllerService.createEmployeeApproval(pensioner,pBc,genericService);
		return pensioner;
	}
	
	

}
