package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;


@Controller
@RequestMapping({"/viewEmpTransferHistory.do"})
public class TransferHistoryController extends BaseController {

      private final String VIEW_NAME = "history/promotionHistoryForm";
	  public TransferHistoryController()
	  {}

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
	  public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);
 		  PaginationBean paginationBean = getPaginationInfo(request);

	    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder().addPredicate(getBusinessClientIdPredicate(request)),TransferLog.class);

	    List wPromoHist;
	    if (wNoOfElements > 0)
	    {
	      wPromoHist = this.genericService.loadPaginatedObjects(TransferLog.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), pEmpId)),(paginationBean.getPageNumber() - 1) * pageLength, pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
	      Collections.sort(wPromoHist, Comparator.comparing(TransferLog::getAuditTimeStamp));
	    } else {
	      wPromoHist = new ArrayList();
	    }
	    HiringInfo wHireInfo = loadHiringInfoByEmpId(request,bc,pEmpId);

	    PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), pageLength, wNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
	    wPGBDH.setEmployeeId(wHireInfo.getAbstractEmployeeEntity().getEmployeeId());
	    wPGBDH.setId(pEmpId);
	     
	    
	    wPGBDH.setName(wHireInfo.getAbstractEmployeeEntity().getDisplayNameWivTitlePrefixed());
	    wPGBDH.setMode(wHireInfo.getAbstractEmployeeEntity().getMdaDeptMap().getMdaInfo().getName());
	    wPGBDH.setCurrentLevelAndStep(wHireInfo.getAbstractEmployeeEntity().getGradeLevelAndStep());
	    wPGBDH.setConfirmation(wHireInfo.getAbstractEmployeeEntity().isSchoolStaff());
	    wPGBDH.setDisplayTitle(wHireInfo.getAbstractEmployeeEntity().getSchoolName());
	    wPGBDH.setBirthDate(wHireInfo.getBirthDateStr());
	    wPGBDH.setHireDate(wHireInfo.getHireDateStr());
	    wPGBDH.setConfirmDate(wHireInfo.getConfirmationDateAsStr());

	    if (wHireInfo.getAbstractEmployeeEntity().isTerminated()) {
			wPGBDH.setNoOfYearsInService(wHireInfo.getTerminateDate().getYear() - wHireInfo.getHireDate().getYear());
	    	if(bc.isPensioner()){
              wPGBDH.setNoOfYearsAsPensioner(wHireInfo.getPensionEndDate().getYear() - wHireInfo.getPensionStartDate().getYear());
			}
	      wPGBDH.setTerminatedEmployee(true);
	      wPGBDH.setTerminationReason(wHireInfo.getTerminateReason().getName());
	      wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getTerminateDate()));
	    } else {
	    	if(bc.isPensioner())
	              wPGBDH.setNoOfYearsInService(LocalDate.now().getYear() - wHireInfo.getPensionStartDate().getYear());
	    	else
				wPGBDH.setNoOfYearsInService(LocalDate.now().getYear() -  wHireInfo.getHireDate().getYear());
	    }
	    addRoleBeanToModel(model, request);
	    model.addAttribute("transfer", true);
	    model.addAttribute("pageTitle", bc.getStaffTypeName()+" Transfer History");
	    model.addAttribute("pageSubTitle", "Transfer History");
	    model.addAttribute("promoHist", wPGBDH);

	    return VIEW_NAME;
	  }
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
 								@ModelAttribute("promoHist") PaginatedPaycheckGarnDedBeanHolder pHADB, BindingResult result, SessionStatus
										status, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);


		return "redirect:searchEmpForTransferHistory.do";
	}


}
