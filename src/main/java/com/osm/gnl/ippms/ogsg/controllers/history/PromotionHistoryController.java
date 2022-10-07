package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/viewEmpPromoHistory.do"})
public class PromotionHistoryController extends BaseController
{

  private final int pageLength = 24;

  private final String VIEW_NAME = "history/promotionHistoryForm";
  private final String ERR_VIEW_NAME = "promotion/promoteEmployeeErrorForm";

 
  public PromotionHistoryController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
  public String setupForm(@RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException
  {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);


    Long serviceId = pEmpId;
    Long bizId = bc.getBusinessClientInstId();

    HiringInfo wHireInfo = loadHiringInfoByEmpId(request,bc,pEmpId);
    AbstractEmployeeEntity wEmp  = wHireInfo.getAbstractEmployeeEntity();
    if(wHireInfo.isPensionerType()){
      //--Look for if he has a Parent Business Client.
      if(wHireInfo.getPensioner().getParentBusinessClientId() != null){

        if(wHireInfo.getPensioner().getEmployee() == null){
          HrMiniBean empHrBean = new HrMiniBean();
          empHrBean.setName(wEmp.getDisplayName());
          empHrBean.setDisplayTitle(bc.getStaffTypeName()+" "+wEmp.getDisplayNameWivTitlePrefixed()+ " Has No Promotion History.");
          addRoleBeanToModel(model, request);
          model.addAttribute("miniBean", empHrBean);
          return ERR_VIEW_NAME;
        }
        serviceId = wHireInfo.getPensioner().getEmployee().getId();
        bizId = wHireInfo.getPensioner().getParentBusinessClientId();

      }
    }
    PaginationBean paginationBean = getPaginationInfo(request);

    PredicateBuilder predicateBuilder = new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("businessClientId", bizId));
    predicateBuilder.addPredicate(CustomPredicate.procurePredicate("employee.id",serviceId));
    int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPromotionAuditClass(bc));

    List wPromoHist;
    if (wNoOfElements > 0)
    {
      wPromoHist = this.genericService.loadPaginatedObjects(IppmsUtils.getPromotionAuditClass(bc),predicateBuilder.getPredicates(),(paginationBean.getPageNumber() - 1) * 24, 24, paginationBean.getSortOrder()
              , paginationBean.getSortCriterion());
      Collections.sort(wPromoHist, Comparator.comparing(AbstractPromotionAuditEntity::getPromotionDate).reversed());
    } else {
      wPromoHist = new ArrayList();
    }

    PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), 24, wNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
    wPGBDH.setEmployeeId(wEmp.getEmployeeId());
    wPGBDH.setId(wEmp.getId());
     

    wPGBDH.setName(wEmp.getDisplayNameWivTitlePrefixed());
    wPGBDH.setMode(wEmp.getMdaDeptMap().getMdaInfo().getName());
    if(!wEmp.isPensioner())
       wPGBDH.setCurrentLevelAndStep(wEmp.getSalaryInfo().getSalaryType().getName() + " " + wEmp.getSalaryInfo().getLevelAndStepAsStr());
    else
       wPGBDH.setMonthlyPensionStr(wHireInfo.getMonthlyPensionAmountStr());

    wPGBDH.setConfirmation(wEmp.isSchoolStaff());
    wPGBDH.setDisplayTitle(wEmp.getSchoolName());
    wPGBDH.setBirthDate(wHireInfo.getBirthDateStr());
    wPGBDH.setHireDate(wHireInfo.getHireDateStr());
    wPGBDH.setConfirmDate(wHireInfo.getConfirmationDateAsStr());
    if (wEmp.isTerminated()) {
      wPGBDH.setTerminatedEmployee(true);
      wPGBDH.setTerminationReason(wHireInfo.getTerminateReason().getName());

        wPGBDH.setNoOfYearsInService(wHireInfo.getNoOfYearsInService());
        wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getTerminateDate()));


    } else {
      wPGBDH.setNoOfYearsInService(LocalDate.now().getYear() -  wHireInfo.getHireDate().getYear());
    }
    model.addAttribute("promotion", true);
    model.addAttribute("pageTitle", bc.getStaffTypeName()+" Promotion/Demotion History");
    model.addAttribute("pageSubTitle", "Promotion/Demotion History");
    addRoleBeanToModel(model, request);
    model.addAttribute("promoHist", wPGBDH);

    return VIEW_NAME;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
                              @ModelAttribute("promoHist") PaginatedPaycheckGarnDedBeanHolder pHADB, BindingResult result, SessionStatus
                                      status, Model model, HttpServletRequest request) throws Exception {
    SessionManagerService.manageSession(request, model);


    return "redirect:searchEmpForPromoHistory.do";
  }
}