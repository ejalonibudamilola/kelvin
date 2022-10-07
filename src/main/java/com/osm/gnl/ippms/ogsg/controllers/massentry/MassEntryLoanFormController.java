package com.osm.gnl.ippms.ogsg.controllers.massentry;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.MassEntryService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.massentry.PromotionHistory;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
@Controller
@RequestMapping("/massLoanEntry.do")
@SessionAttributes(types = PaginatedPaycheckGarnDedBeanHolder.class)

public class MassEntryLoanFormController extends BaseController {

	@Autowired
	private PaycheckService paycheckService;

	@Autowired
	private MassEntryService massEntryService;

	private final int pageLength = 10;

	private final String VIEW = "massentry/massEntryLoanForm";

	
	public MassEntryLoanFormController() {

	}

	
	@ModelAttribute("LoanTypeList")
	public List<EmpGarnishmentType> getGarnishmentList(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
		BusinessCertificate bc = super.getBusinessCertificate(request);
		List<EmpGarnishmentType> wLoanTypeList = this.genericService.loadAllObjectsWithSingleCondition(EmpGarnishmentType.class,
				CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),"description");
		//Collections.sort(wLoanTypeList);
		return wLoanTypeList;
	}
	@ModelAttribute("loanTermList")
	public Collection<NamedEntity> getLoanTermList(){
		ArrayList<NamedEntity> wList = new ArrayList<NamedEntity>();
		
		for(long i = 1;i < 85; i++){
			NamedEntity n = new NamedEntity();
			n.setId(i);
			if(i == 1){
				n.setName(i +" Month");
			}else{
				n.setName(i +" Months");
			}
			wList.add(n);
		}
		
		return wList;
		
	}
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		PaginationBean paginationBean = getPaginationInfo(request);
		
		PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_LOAN_ATTR_NAME);
		
		if(wPGBDH != null &&  !wPGBDH.isNewEntity()){
			//Keep Memory low..
		   removeSessionAttribute(request, MASS_LOAN_ATTR_NAME);
			
		}
		List<PromotionHistory>	wPromoHist = new ArrayList<PromotionHistory>();
		List<Long> wEmpIdList = new ArrayList<Long>();
		wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), pageLength, 0, paginationBean.getSortCriterion(), paginationBean.getSortOrder());
		wPGBDH.setId(bc.getLoginId());
		wPGBDH.setEmployeeIdList(wEmpIdList);
		wPGBDH.setPaginationListHolder(wPromoHist);
		wPGBDH.setActiveInd(ON);
		addSessionAttribute(request, MASS_LOAN_ATTR_NAME, wPGBDH);
		addRoleBeanToModel(model, request);
		model.addAttribute("massLoanBean", wPGBDH);
		
		return VIEW;
		

	}

	
	@RequestMapping(method = RequestMethod.GET,params={"eid"})
	public String setupForm(@RequestParam("eid") Long pEmpId,Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		PaginationBean paginationBean = getPaginationInfo(request);

		
		PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_LOAN_ATTR_NAME);
		
		List<PromotionHistory> wNewList;
		List<Long> wFilter = new ArrayList<Long>();
		List<PromotionHistory> wAllList = new ArrayList<>();
		 for (PromotionHistory p : (List<PromotionHistory>)wPGBDH.getPaginationListHolder()) {
		        if (p.getId().equals(pEmpId)) {
		          continue;
		        }
		        wAllList.add(p);
		        wFilter.add(p.getId());
		      }

		 int pageNumber = paginationBean.getPageNumber();
		   
		    if(wAllList.size() > this.pageLength){
		    	Double wDouble = new Double(wAllList.size())/new Double(this.pageLength);
		    	pageNumber = Integer.parseInt(String.valueOf(wDouble).substring(0,String.valueOf(wDouble).indexOf(".")));
		    }
		    //Now Paginate.
		    wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(pageNumber,this.pageLength,wAllList);

		
		wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, pageNumber, pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
		
		wPGBDH.setEmptyList(wAllList.size() > 0);
		wPGBDH.setId(bc.getLoginId());
		wPGBDH.setPaginationListHolder(wAllList);
		wPGBDH.setBeanList(wNewList);
		wPGBDH.setEmployeeIdList(wFilter);
		wPGBDH.setActiveInd(ON);
		addSessionAttribute(request, MASS_LOAN_ATTR_NAME, wPGBDH);
		addRoleBeanToModel(model, request);
		
		model.addAttribute("massLoanBean", wPGBDH);
		
		return VIEW;
		

	}
	
	@RequestMapping(method = RequestMethod.GET,params={"lid","act"})
	public String setupForm(@RequestParam("lid") Long pLoginId,@RequestParam("act") String pSaved,Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		PaginationBean paginationBean = getPaginationInfo(request);
		
		PaginatedPaycheckGarnDedBeanHolder wPGBDH = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_LOAN_ATTR_NAME);
		
		 List<PromotionHistory> wAllList = (List<PromotionHistory>)wPGBDH.getPaginationListHolder();
		    if(wAllList == null)
		    	wAllList = new ArrayList<>();
		    List<PromotionHistory> wNewList = (List<PromotionHistory>) PayrollUtils.paginateList(paginationBean.getPageNumber(),this.pageLength,wAllList);
		List<Long> wEmpIdList = wPGBDH.getEmployeeIdList();
		 
		
		wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wNewList, paginationBean.getPageNumber(), pageLength, wAllList.size(), paginationBean.getSortCriterion(), paginationBean.getSortOrder());
		
		wPGBDH.setEmptyList(wAllList.size() > 0);
		wPGBDH.setId(pLoginId);
		wPGBDH.setEmployeeIdList(wEmpIdList);
		wPGBDH.setPaginationListHolder(wAllList);
		wPGBDH.setActiveInd(ON);
		addSessionAttribute(request, MASS_LOAN_ATTR_NAME, wPGBDH);
		addRoleBeanToModel(model, request);
		
		model.addAttribute("massLoanBean", wPGBDH);
		
		return VIEW;
		

	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value = REQUEST_PARAM_CANCEL, required = false) String cancel,
			@RequestParam(value = REQUEST_PARAM_SEARCH, required = false) String search,
			@RequestParam(value = REQUEST_PARAM_ADD_LOAN, required = false) String addLoan,
			@ModelAttribute ("massLoanBean") PaginatedPaycheckGarnDedBeanHolder pEHB, BindingResult result, 
			SessionStatus status, Model model,HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		 
		if(isButtonTypeClick(request,REQUEST_PARAM_CANCEL)){
			removeSessionAttribute(request, MASS_LOAN_ATTR_NAME);
			return "redirect:massEntryMainDashboard.do";
		}
		
	 
		if(isButtonTypeClick(request, REQUEST_PARAM_SEARCH)){
			//This is a search. First find if Employee Exists....
			String staffId = pEHB.getStaffId().toUpperCase();
			 
			Employee wEmp = this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("employeeId",staffId)));

			if(wEmp.isNewEntity()){
				result.rejectValue("", "Global.Change", "No "+bc.getStaffTypeName()+" Found with "+bc.getStaffTitle()+" '"+staffId+"'");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("massLoanBean", pEHB);
				return VIEW;
			}else{
				if(wEmp.isTerminated()){
					result.rejectValue("", "Global.Change", bc.getStaffTypeName()+" "+wEmp.getDisplayNameWivTitlePrefixed()+" [ "+wEmp.getEmployeeId()+" ] is terminated.");
					result.rejectValue("", "Global.Change", "Loans can not be added to Terminated "+bc.getStaffTypeName()+"s");

					addDisplayErrorsToModel(model, request);
					addRoleBeanToModel(model, request);
					model.addAttribute("status", result);
					model.addAttribute("massLoanBean", pEHB);
					return VIEW;
				}
				//-- Now check if this employee is Suspended
				HiringInfo wHI = loadHiringInfoByEmpId(request,bc,wEmp.getId());
				if(wHI.isNewEntity()){
					result.rejectValue("", "Global.Change", "Employee "+wEmp.getDisplayNameWivTitlePrefixed()+" [ "+wEmp.getEmployeeId()+" ], has no Hire Information.");
					result.rejectValue("", "Global.Change", "Loans can not be added to Incomplete Employees.");
					addRoleBeanToModel(model, request);
					addDisplayErrorsToModel(model, request);
					model.addAttribute("status", result);
					model.addAttribute("massLoanBean", pEHB);
					return VIEW;
				}else{
					if(wHI.isSuspendedEmployee()){
						result.rejectValue("", "Global.Change", "Employee "+wEmp.getDisplayNameWivTitlePrefixed()+" [ "+wEmp.getEmployeeId()+" ], has no Hire Information.");
						result.rejectValue("", "Global.Change", "Loans can not be added to Incomplete Employees.");
						addRoleBeanToModel(model, request);
						addDisplayErrorsToModel(model, request);
						model.addAttribute("status", result);
						model.addAttribute("massLoanBean", pEHB);
						return VIEW;
					}
				}
			}
			
			//Now check if it is already added....
			for(Object e : pEHB.getPaginationListHolder()){
				if(((PromotionHistory)e).getId().equals(wEmp.getId())){
					result.rejectValue("", "Global.Change", "Employee '"+wEmp.getDisplayName()+"' is already added");
					addDisplayErrorsToModel(model, request);
					addRoleBeanToModel(model, request);
					model.addAttribute("status", result);
					model.addAttribute("massLoanBean", pEHB);
					return VIEW;
				}
			}
			//If we get here, add the muthasucker!
			pEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_LOAN_ATTR_NAME);
			//Now find out the size of the list and add the bean...
			List<PromotionHistory> wPromoHist = (List<PromotionHistory>) pEHB.getPaginationListHolder();
			
			List<Long> wEmpIds = pEHB.getEmployeeIdList();
			
			if(wPromoHist == null){
				wPromoHist = new ArrayList<>();
			}
			if(wEmpIds == null){
				wEmpIds = new ArrayList<>();
			}
			
			
			PromotionHistory wPromHistBean = new PromotionHistory();
			wPromHistBean.setEntryIndex(wPromoHist.size() + 1);
			wPromHistBean.setEmployee(wEmp);
			wPromHistBean.setOldSalaryLevelAndStep(wEmp.getSalaryInfo().getSalaryScaleLevelAndStepStr());
			wPromHistBean.setId(wEmp.getId());
			wPromoHist.add(wPromHistBean);
			wEmpIds.add(wEmp.getId());
			pEHB.setEmployeeIdList(wEmpIds);
			
			pEHB.setPaginationListHolder(wPromoHist);
			 
			addSessionAttribute(request, MASS_LOAN_ATTR_NAME, pEHB);
			
			return "redirect:massLoanEntry.do?lid="+bc.getLoginId()+"&act=y";
		}
		 
		if(isButtonTypeClick(request, REQUEST_PARAM_ADD_LOAN)){
			//Now check if the values has been picked.
			if(pEHB.getSalaryTypeId() == -1){
				result.rejectValue("", "Global.Change", "Please select Loan Type");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("massLoanBean", pEHB);
				return VIEW;
			}
			double wOA = 0;
			 
			try{
				 
				wOA = Double.parseDouble(PayrollHRUtils.removeCommas(pEHB.getOwedAmountStr()));
				
				if(wOA < 0){
					result.rejectValue("", "Global.Change", "Please Enter a value for Loan Amount");
					addDisplayErrorsToModel(model, request);
					addRoleBeanToModel(model, request);
					model.addAttribute("status", result);
					model.addAttribute("massLoanBean", pEHB);
					return VIEW;
				}
				pEHB.setOriginalLoanAmount(wOA);
				
			}catch(NumberFormatException wNFE){
				result.rejectValue("", "Global.Change", "Please Enter a value for Loan Amount");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("massLoanBean", pEHB);
				return VIEW;
			}
			 
			if(pEHB.getLoanTerm() == 0){
				 
				result.rejectValue("", "Global.Change", "Please Select the Loan Tenor");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("massLoanBean", pEHB);
				return VIEW;
			
		    }
			
			//If We get here....
			PaginatedPaycheckGarnDedBeanHolder wPEHB = (PaginatedPaycheckGarnDedBeanHolder) getSessionAttribute(request, MASS_LOAN_ATTR_NAME);
			wPEHB.setOriginalLoanAmount(pEHB.getOriginalLoanAmount());
			wPEHB.setLoanTerm(pEHB.getLoanTerm());
			if(wPEHB == null || wPEHB.getPaginationListHolder().isEmpty()){
				result.rejectValue("", "Global.Change", "Please add "+bc.getStaffTypeName()+" to assign loan.");
				addDisplayErrorsToModel(model, request);
				addRoleBeanToModel(model, request);
				model.addAttribute("status", result);
				model.addAttribute("massLoanBean", pEHB);
				return VIEW;
			}else{
				EmpGarnishmentType wGarnType = this.genericService.loadObjectUsingRestriction(EmpGarnishmentType.class, Arrays.asList(
						CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("id",wPEHB.getSalaryTypeId())));

				wPEHB.setName(wGarnType.getDescription());
				double garnishAmount = setMonthlyLoanAmount(wPEHB.getLoanTerm(), wPEHB.getOriginalLoanAmount());

				wPEHB.setTypeInd(IConstants.LOAN);
				addSessionAttribute(request, MASS_LOAN_ATTR_NAME, wPEHB);
				HashMap<Long,Long> wUpdateMap = this.massEntryService.makeDedGarnSpecMap(IppmsUtils.getGarnishmentInfoClass(bc),"empGarnishmentType.id",wGarnType.getId(),pEHB.getEmployeeIdList(),bc);

				List<PromotionHistory> wPromoHist = (List<PromotionHistory>) wPEHB.getPaginationListHolder();
				 
				 
				List<AbstractGarnishmentEntity> wIntegerList = new ArrayList<>();
				int wAddendum = 0;
				AbstractGarnishmentEntity wEmpGarnInfo;
				for(PromotionHistory p : wPromoHist){
					wAddendum++;
					wEmpGarnInfo = IppmsUtils.makeGarnishmentInfoObject(bc);
					if(wUpdateMap.containsKey(p.getEmployee().getId())){
						wEmpGarnInfo.setId(wUpdateMap.get(p.getEmployee().getId()));
					}
					wEmpGarnInfo.setEmpGarnishmentType(wGarnType);
					wEmpGarnInfo.setOwedAmount(wOA);
					wEmpGarnInfo.setInterestAmount(0.00);
					wEmpGarnInfo.setDeductInterestSeparatelyInd(IConstants.OFF);
					wEmpGarnInfo.setGovtLoan(IConstants.OFF);
					wEmpGarnInfo.setLastModBy(new User(bc.getLoginId()));
					wEmpGarnInfo.setLastModTs(Timestamp.from(Instant.now()));
					wEmpGarnInfo.setGarnishCap(0.00);
					wEmpGarnInfo.setStartDate(LocalDate.now());
     				wEmpGarnInfo.setCreatedBy(new User(bc.getLoginId()));
     				wEmpGarnInfo.setAmount(garnishAmount);
					wEmpGarnInfo.setDescription(pEHB.getName());
					wEmpGarnInfo.setName(wEmpGarnInfo.getDescription());
					wEmpGarnInfo.setEmployee(new Employee(p.getEmployee().getId()));
					wEmpGarnInfo.setOriginalLoanAmount(wOA); 
					wEmpGarnInfo.setLoanTerm(wPEHB.getLoanTerm());
					wEmpGarnInfo.setCurrentLoanTerm(wPEHB.getLoanTerm());
					wEmpGarnInfo.setBusinessClientId(bc.getBusinessClientInstId());
					wEmpGarnInfo.setEndDate(PayrollUtils.makeEndDate(wEmpGarnInfo.getStartDate(),wEmpGarnInfo.getLoanTerm()));
					
					wIntegerList.add(wEmpGarnInfo);
					if(wIntegerList.size() == 50){
						this.genericService.storeObjectBatch(wIntegerList);
						wIntegerList = new ArrayList<>();
					}
				}
				if(!wIntegerList.isEmpty()){
					this.genericService.storeObjectBatch(wIntegerList);
				}
				 pEHB.setSuccessList(wPromoHist);
			      addSessionAttribute(request, MASS_LOAN_ATTR_NAME, pEHB);
				LocalDate _wCal = this.paycheckService.getPendingPaycheckRunMonthAndYear(bc);
			      if(_wCal != null){
					  RerunPayrollBean wRPB = IppmsUtilsExt.loadRerunPayrollBean(genericService,bc.getBusinessClientInstId(),_wCal);
					wRPB.setNoOfLoans(wRPB.getNoOfLoans() + wAddendum);
					if(wRPB.isNewEntity()){
						wRPB.setRunMonth(_wCal.getMonthValue());
						wRPB.setRunYear(_wCal.getYear());
						wRPB.setRerunInd(IConstants.ON);
						wRPB.setBusinessClientId(bc.getBusinessClientInstId());
					}
					this.genericService.saveObject(wRPB);
			      }
			}
			
		}
		
		
		return "redirect:displayMassEntryResult.do?lid="+bc.getLoginId()+"&tn="+MASS_LOAN_ATTR_NAME;
		
	}

	private double setMonthlyLoanAmount(int pLoanTerm,
			double pLoanAmount)
	{
		
			double wRetVal = 0.0;
			
			if(pLoanAmount == 0 || pLoanTerm == 0)
				return wRetVal;
			
			wRetVal = EntityUtils.convertDoubleToEpmStandard(pLoanAmount/new Double(pLoanTerm));
			
			wRetVal = Double.parseDouble(PayrollHRUtils.removeCommas(PayrollHRUtils.getDecimalFormat(true).format(wRetVal)));
			
			return wRetVal;
		
	}
	
	
	
	

}
