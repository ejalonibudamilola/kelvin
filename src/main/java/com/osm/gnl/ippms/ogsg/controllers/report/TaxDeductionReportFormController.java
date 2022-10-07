package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.PaycheckDeductionService;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmpDeductMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({"/taxReport.do"})
@SessionAttributes(types={PaginatedBean.class})
public class TaxDeductionReportFormController  extends BaseController
{
	 
	  private final int pageLength = 20;
	  private final String VIEW_NAME = "deduction/taxDeductionForm";

	  @Autowired
	PaycheckDeductionService paycheckDeductionService;

	@RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);



		Long wInt =  genericService.loadMaxValueByClassAndLongColName(PayrollRunMasterBean.class, "id");
		
		PayrollRunMasterBean wPMB =  genericService.loadObjectById(PayrollRunMasterBean.class, wInt);

	    
	    LocalDate wSDate = LocalDate.now();
	    LocalDate wToDate = null;
		LocalDate wEDate = LocalDate.now();

	    if (!wPMB.isNewEntity()) {
	      
	       
	      LocalDate.of(wPMB.getRunYear(), wPMB.getRunMonth(), 1);
	      LocalDate endDate = PayrollBeanUtils.getDateFromMonthAndYear(wPMB.getRunMonth(), wPMB.getRunYear());
			LocalDate.of(wPMB.getRunYear(), wPMB.getRunMonth(), endDate.lengthOfMonth());
	       
	    }

	    return this.generateModel(wSDate, wEDate, 0L,bc, 0L, model, request);
	  }
	@RequestMapping(method={RequestMethod.GET}, params={"sDate", "eDate"})
	  public String setupForm(@RequestParam("sDate") String pSDate, 
			  @RequestParam("eDate") String pToDate,Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	     
	    LocalDate wSDate =  PayrollBeanUtils.setDateFromString(pSDate);
	    LocalDate wToDate =  PayrollBeanUtils.setDateFromString(pToDate);
	    
	    return this.generateModel(wSDate, wToDate, 0L,  bc, 0L, model, request);
	       
	   
	  }
	  @RequestMapping(method={RequestMethod.GET}, params={"sDate", "eDate","pid"})
	  public String setupForm(@RequestParam("sDate") String pSDate, 
			  @RequestParam("eDate") String pToDate, @RequestParam("pid") Long mapId,  
			  Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);
	      LocalDate wSDate = PayrollBeanUtils.setDateFromString(pSDate);
		    LocalDate wToDate = PayrollBeanUtils.setDateFromString(pToDate);
		    
		      
		   
		    return this.generateModel(wSDate, wToDate, mapId,  bc, 0L, model, request);
    }
	  
	  @RequestMapping(method={RequestMethod.GET}, params={"sDate", "eDate","eid"})
	  public String setupForm(@RequestParam("sDate") String pSDate, 
			  @RequestParam("eDate") String pToDate, @RequestParam("eid") String empId,
			  Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		    
		    LocalDate wSDate = PayrollBeanUtils.setDateFromString(pSDate);
		    LocalDate wToDate =  PayrollBeanUtils.setDateFromString(pToDate);
		     
		      
		      empId = (PayrollHRUtils.removeNonSqlXters(empId));
		      if (!empId.toUpperCase().startsWith("OG")) {
		        empId = "OG" + empId;}
		      empId = empId.toUpperCase();
		    Employee wEmp =  this.genericService.loadObjectUsingRestriction(Employee.class, Arrays.asList(CustomPredicate.procurePredicate(
			"employeeId", empId), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
		    
		    return this.generateModel(wSDate, wToDate, 0L, bc,wEmp.getId(), model, request);
		    
    }
	  

	  @RequestMapping(method={RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, 
			  @ModelAttribute("miniBean") PaginatedBean pDDB, 
			  BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
 
	    

	    if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {
	      String sDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getFromDate());
	      String eDate = PayrollBeanUtils.getJavaDateAsString(pDDB.getToDate());
	      
	      if (IppmsUtils.isNotNullAndGreaterThanZero(pDDB.getMdaInd())) {
	    	 
		      //pDDB.setId(Integer.valueOf(mapId));
	        return "redirect:taxReport.do?sDate=" + sDate + "&eDate=" + eDate +"&pid="+pDDB.getMdaInd();
	      }else if (!IppmsUtils.treatNull(pDDB.getEmpId()).equalsIgnoreCase(EMPTY_STR)) {
			        return "redirect:taxReport.do?sDate=" + sDate + "&eDate=" + eDate + "&eid=" + pDDB.getEmpId();
			      
	      }
	      return "redirect:taxReport.do?sDate=" + sDate + "&eDate=" + eDate;
	    }

	    return REDIRECT_TO_DASHBOARD;
	  }

	  private String generateModel(LocalDate pFromDate, LocalDate pToDate, Long pMdaInd, BusinessCertificate pBc,Long pEmpInstId,Model pModel,HttpServletRequest request) throws Exception{

	  		BusinessCertificate bc = getBusinessCertificate(request);
		  
		  int noOfEmpWivNegPay = 0;
		    
		  if(pFromDate.getYear() != pToDate.getYear()
		    		|| pFromDate.getMonthValue() != pToDate.getMonthValue()){

			  noOfEmpWivNegPay = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status", "P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("netPay", 0, Operation.LESS),
			  CustomPredicate.procurePredicate("runMonth", pToDate.getMonthValue()), CustomPredicate.procurePredicate("runYear", pToDate.getYear()))), IppmsUtils.getPaycheckClass(bc));
			  if (noOfEmpWivNegPay > 0)
			  {
			        return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pToDate.getMonthValue() + "&ry=" + pToDate.getYear();
			   }
		  }

		  noOfEmpWivNegPay = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(Arrays.asList(CustomPredicate.procurePredicate("status", "P"), getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("netPay", 0, Operation.LESS),
				  CustomPredicate.procurePredicate("runMonth", pToDate.getMonthValue()), CustomPredicate.procurePredicate("runYear", pToDate.getYear()))), IppmsUtils.getPaycheckClass(bc));
			  if (noOfEmpWivNegPay > 0)
			  {
			        return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pToDate.getMonthValue() + "&ry=" + pToDate.getYear();
			   }
		   
		    PaginationBean paginationBean = getPaginationInfo(request);
		  
		  int pageNumber = ServletRequestUtils.getIntParameter(request, "page", 1);
		    String sortOrder = ServletRequestUtils.getStringParameter(request, "dir", "asc");
		    String sortCriterion = ServletRequestUtils.getStringParameter(request, "sort", null);
		    
		  List<EmpDeductMiniBean> paycheckDeductions = this.paycheckDeductionService.loadTaxDeductionByRunMonthAndYear((paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
		    		paginationBean.getSortOrder(), paginationBean.getSortCriterion(),pFromDate.getMonthValue(), pFromDate.getYear(),pToDate.getMonthValue(), pToDate.getYear(),pMdaInd,pEmpInstId, bc);
		    Collections.sort(paycheckDeductions);
		  //  double sumTaxDeductions = this.payrollServiceExt.getTotalSumOfTaxDeductionsPerPayPeriod(wSDate.get(Calendar.MONTH), 
		    //		wSDate.get(Calendar.YEAR),mapId, objId);

		    int wGLNoOfElements = this.paycheckDeductionService.getTotalNoOfEmpTaxDeductionByDates(pFromDate.getMonthValue(), pFromDate.getYear(),pToDate.getMonthValue(), pToDate.getYear(),pMdaInd, pEmpInstId, bc);
		    Collections.sort(paycheckDeductions);
		    
		   
		    PaginatedBean pCList = new PaginatedBean(paycheckDeductions, pageNumber, this.pageLength, wGLNoOfElements, sortCriterion, sortOrder);
		   
            pCList.setFromDateStr(String.valueOf(pFromDate.getMonthValue())
		    			+ pFromDate.getMonthValue());
            pCList.setActionCompleted(String.valueOf(pFromDate.getMonthValue())
		    			+ pFromDate.getYear()
		    			+"."+ pToDate.getMonthValue() +
                    pToDate.getYear());
		    
		    for(EmpDeductMiniBean e : paycheckDeductions){
		    	
		    	e.setFromDateStr(pCList.getFromDateStr());
		    	e.setYearsMonthsAndIdStr(String.valueOf(pFromDate.getMonthValue())
		    			+ pFromDate.getYear()
		    			+"."+ pToDate.getMonthValue() +
                                pToDate.getYear() +"."+ e.getId()
		    			);
		    }
		    	
		    List<MdaInfo> wMdaList = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
					CustomPredicate.procurePredicate("businessClientId", getBusinessCertificate(request).getBusinessClientInstId()), "name");
		     
			
		    pCList.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pFromDate));
		    pCList.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pToDate));
		    pCList.setFromDate(pFromDate);
		    pCList.setToDate(pToDate);
		    pCList.setShowRow(SHOW_ROW);
		     
	    	pCList.setId(pEmpInstId);
	    	pCList.setMdaInd(0L);
		    if(pMdaInd == 0 && pEmpInstId.equals(0L)){
		    	 pCList.setName("All M.D.As");
		 	     
		 	     
		    }else if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaInd)){
		    	
		    	
		    	MdaInfo wMdaInfo =  this.genericService.loadObjectById(MdaInfo.class, pMdaInd);
		    	
		    	pCList.setName(wMdaInfo.getName());
		    	pCList.setMdaInd(pMdaInd);
			 
				 
		    	
		    }else if(IppmsUtils.isNotNullAndGreaterThanZero(pEmpInstId)){
		    	Employee wEmp = this.genericService.loadObjectById(Employee.class, pEmpInstId);
		    	pCList.setName(wEmp.getDisplayNameWivTitlePrefixed());
		    	pCList.setOgNumber(wEmp.getEmployeeId());
		    	pCList.setEmpId(wEmp.getEmployeeId());
		    	pCList.setCaptchaError(true);
		    	
		    }
		    pModel.addAttribute("agencyList", wMdaList);
		    pModel.addAttribute("miniBean", pCList);
		    pModel.addAttribute("roleBean", pBc);
		    
		    return VIEW_NAME;
		  
	  }
		 
			 
	}

