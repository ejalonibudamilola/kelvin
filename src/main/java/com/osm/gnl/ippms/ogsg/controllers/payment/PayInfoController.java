package com.osm.gnl.ippms.ogsg.controllers.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.EmployeeGeneralOverviewForm;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payment.AbstractPaymentInfoEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.PayFrequency;
import com.osm.gnl.ippms.ogsg.domain.payment.PayInfoType;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.validators.payment.PayInfoFormValidator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping({"/payInfoForm.do","/penPayInfoForm.do"})
@SessionAttributes(types={PaymentInfo.class})
public class PayInfoController extends BaseController
{
  @Autowired
  private PayInfoFormValidator validator;
  
  private final String VIEW_NAME = "payment/payInfoForm";
  
  public PayInfoController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws Exception
  {
      SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = getBusinessCertificate(request);
    NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
     if(bc.isPensioner())
       return "redirect:penPayInfoForm.do?oid="+ne.getId();
    return "redirect:payInfoForm.do?oid="+ne.getId();
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"oid"})
  public String setupForm(@RequestParam("oid") Long eid, Model model, HttpServletRequest request) throws Exception
  {
      SessionManagerService.manageSession(request, model);

    AbstractPaymentInfoEntity paymentInfo =  this.getPaymentInfo(eid, request);

    if (paymentInfo.isNewEntity())
    {
      paymentInfo.setSalaryRef("1");
      paymentInfo.setParentId(eid);

    }
     
      makePaymentInfo(paymentInfo ,this.getBusinessCertificate(request));
     
    paymentInfo = setPayTypes(paymentInfo);
    addRoleBeanToModel(model, request);
    model.addAttribute("payFreq", populatePayFrequency());
    model.addAttribute("paymentInfo", paymentInfo);
    return VIEW_NAME;
  }



  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("paymentInfo") PaymentInfo payInfo, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
      SessionManagerService.manageSession(request, model);
		Object userId = getSessionId(request);
		
    

    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
      try
      {
        if (Navigator.getInstance(userId).getFromClass().isAssignableFrom(EmployeeGeneralOverviewForm.class))
        {
          if (Navigator.getInstance(userId).getFromForm() != null) {
            Navigator.getInstance(userId).setFromClass(getClass());
            return Navigator.getInstance(userId).getFromForm();
          }
        } else if (Navigator.getInstance(userId).getFromForm().equalsIgnoreCase("redirect:busEmpOverviewForm.do"));
        {
          return "redirect:busEmpOverviewForm.do";
        }
      }
      catch (Exception ex)
      {
      }
    if (payInfo != null)
    {
      resolveInternalIssues(payInfo);

      new PayInfoFormValidator().validate(payInfo, result);
      if (result.hasErrors()) {
        addDisplayErrorsToModel(model, request);
        addRoleBeanToModel(model, request);
        model.addAttribute("status", result);
        model.addAttribute("payFreq", populatePayFrequency());
        model.addAttribute("paymentInfo", result.getTarget());

        return VIEW_NAME;
      }
      this.genericService.storeObject(payInfo);
    }

    NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
    if ((!ne.isNewEntity()) && (!ne.getMode().equalsIgnoreCase("create"))) {
      try {
        if (Navigator.getInstance(userId).getFromClass().isAssignableFrom(EmployeeGeneralOverviewForm.class))
        {
          if (Navigator.getInstance(userId).getFromForm() != null) {
            Navigator.getInstance(userId).setFromClass(getClass());
            return Navigator.getInstance(userId).getFromForm();
          }
        }
      }
      catch (Exception ex)
      {
      }

      return "redirect:paymentInfoForm.do?oid=" + payInfo.getEmployee().getId();
    }
    if ((payInfo.getSickPay().equalsIgnoreCase("Y")) || (payInfo.getVacationPay().equalsIgnoreCase("Y")))
    {
      return "redirect:payPolicyViewForm.do?eid=" + payInfo.getEmployee().getId();
    }
    return "redirect:paymentInfoForm.do?oid=" + payInfo.getEmployee().getId();
  }
  
private Collection<PayFrequency> populatePayFrequency() {
	  Collection<PayFrequency> retVal = new ArrayList<>();
	  Collection<PayFrequency> allPayFreq = this.genericService.loadControlEntity(PayFrequency.class);
    for (PayFrequency p : allPayFreq) {
      if (p.getName().equalsIgnoreCase("month")) {
        retVal.add(p);
        break;
      }

    }

    return retVal;
  }

  
private AbstractPaymentInfoEntity setPayTypes(AbstractPaymentInfoEntity pPaymentInfo) {
    try {
      List<PayInfoType> list =  this.genericService.loadControlEntity(PayInfoType.class);

      if ((list != null) && (!list.isEmpty()))
      {
        for (PayInfoType p : list)
        {
          if (p.getName().equalsIgnoreCase("salary"))
          {
            pPaymentInfo.setSalaryPayType(p.getId().intValue());
            continue;
          }if (p.getName().equalsIgnoreCase("hourly"))
          {
            pPaymentInfo.setHourlyPayType(p.getId().intValue());
            continue;
          }if (p.getName().equalsIgnoreCase("commission"))
          {
            pPaymentInfo.setCommissionPayType(p.getId().intValue());
            continue;
          }
        }
      }
    }
    catch (Exception ex)
    {
    }

    return pPaymentInfo;
  }

  private PaymentInfo resolveInternalIssues(PaymentInfo pPaymentInfo)
  {
    switch (Integer.parseInt(pPaymentInfo.getSalaryRef())) {
    case 0:
      pPaymentInfo.setPayInfoType(new PayInfoType(new Long(pPaymentInfo.getHourlyPayType())));
      pPaymentInfo.setPayFrequency(new PayFrequency(new Long(getFrequencyIdUsingCode("hourly"))));
      pPaymentInfo.setSalary(0.0D);
      pPaymentInfo.setDaysWorkedPerWeek(0.0D);
      pPaymentInfo.setHoursWorkedPerDay(0.0D);
      break;
    case 1:
      pPaymentInfo.setPayInfoType(new PayInfoType(new Long(pPaymentInfo.getSalaryPayType())));
      pPaymentInfo.setRate(0.0D);
      break;
    case 2:
      pPaymentInfo.setPayInfoType(new PayInfoType(new Long(pPaymentInfo.getCommissionPayType())));
      pPaymentInfo.setPayFrequency(new PayFrequency(new Long(getFrequencyIdUsingCode("commission"))));
      pPaymentInfo.setSalary(0.0D);
      pPaymentInfo.setDaysWorkedPerWeek(0.0D);
      pPaymentInfo.setHoursWorkedPerDay(0.0D);
    }

    if (Boolean.valueOf(pPaymentInfo.getOvertime()).booleanValue())
      pPaymentInfo.setOvertime("Y");
    else {
      pPaymentInfo.setOvertime("N");
    }
    if (Boolean.valueOf(pPaymentInfo.getDoubleOvertime()).booleanValue())
      pPaymentInfo.setDoubleOvertime("Y");
    else {
      pPaymentInfo.setDoubleOvertime("N");
    }
    if (Boolean.valueOf(pPaymentInfo.getSickPay()).booleanValue())
      pPaymentInfo.setSickPay("Y");
    else {
      pPaymentInfo.setSickPay("N");
    }
    if (Boolean.valueOf(pPaymentInfo.getVacationPay()).booleanValue())
      pPaymentInfo.setVacationPay("Y");
    else {
      pPaymentInfo.setVacationPay("N");
    }
    if (Boolean.valueOf(pPaymentInfo.getHolidayPay()).booleanValue())
      pPaymentInfo.setHolidayPay("Y");
    else {
      pPaymentInfo.setHolidayPay("N");
    }
    if (Boolean.valueOf(pPaymentInfo.getBonus()).booleanValue())
      pPaymentInfo.setBonus("Y");
    else {
      pPaymentInfo.setBonus("N");
    }
    if (Boolean.valueOf(pPaymentInfo.getCommission()).booleanValue())
      pPaymentInfo.setCommission("Y");
    else {
      pPaymentInfo.setCommission("N");
    }
    return pPaymentInfo;
  }

  
private Long getFrequencyIdUsingCode(String pCode) {
    Long pFreqInstId = 0L;
    Collection<PayFrequency> pList = this.genericService.loadControlEntity(PayFrequency.class);
    for (PayFrequency p : pList) {
    	 if (p.getName().equalsIgnoreCase(pCode))
              pFreqInstId = p.getId() ;
        break;
      }
   
    return pFreqInstId;
   }
  

  private AbstractPaymentInfoEntity makePaymentInfo(AbstractPaymentInfoEntity pPayInfo, BusinessCertificate pBizId) throws Exception
  {


    AbstractEmployeeEntity employee = IppmsUtils.loadEmployee(this.genericService,pPayInfo.getParentId(),pBizId);
	//Return more values...
	if(employee.getSalaryInfo() == null || employee.getSalaryInfo().isNewEntity()) {
	        pPayInfo.setAbstractEmployee(employee);
	}else

    pPayInfo.setSalaryRef("1");
    pPayInfo.setSalary(employee.getSalaryInfo().getMonthlyBasicSalary());

    if (StringUtils.trimToEmpty(pPayInfo.getOvertime()).equalsIgnoreCase("Y"))
      pPayInfo.setOvertime(TRUE);
    else {
      pPayInfo.setOvertime(FALSE);
    }
    if (StringUtils.trimToEmpty(pPayInfo.getDoubleOvertime()).equalsIgnoreCase("Y"))
      pPayInfo.setDoubleOvertime(TRUE);
    else {
      pPayInfo.setDoubleOvertime(FALSE);
    }
    if (StringUtils.trimToEmpty(pPayInfo.getSickPay()).equalsIgnoreCase("Y"))
      pPayInfo.setSickPay(TRUE);
    else {
      pPayInfo.setSickPay(FALSE);
    }
    if (StringUtils.trimToEmpty(pPayInfo.getVacationPay()).equalsIgnoreCase("Y"))
      pPayInfo.setVacationPay(TRUE);
    else {
      pPayInfo.setVacationPay(FALSE);
    }
    if (StringUtils.trimToEmpty(pPayInfo.getHolidayPay()).equalsIgnoreCase("Y"))
      pPayInfo.setHolidayPay(TRUE);
    else {
      pPayInfo.setHolidayPay(FALSE);
    }
    if (StringUtils.trimToEmpty(pPayInfo.getBonus()).equalsIgnoreCase("Y"))
      pPayInfo.setBonus(TRUE);
    else {
      pPayInfo.setBonus(FALSE);
    }
    if (StringUtils.trimToEmpty(pPayInfo.getCommission()).equalsIgnoreCase("Y"))
      pPayInfo.setCommission(TRUE);
    else {
      pPayInfo.setCommission(FALSE);
    }

    return pPayInfo;
  }
  private AbstractPaymentInfoEntity getPaymentInfo(Long eid, HttpServletRequest request) throws InstantiationException, IllegalAccessException {

    BusinessCertificate businessCertificate = super.getBusinessCertificate(request);

    String ormRelationship = "employee.id";
    if(businessCertificate.isPensioner()){
      ormRelationship = "pensioner.id";
    }

     return (AbstractPaymentInfoEntity) this.genericService.loadObjectWithSingleCondition(IppmsUtils.getPaymentInfoClass(businessCertificate),
     CustomPredicate.procurePredicate(ormRelationship, eid));


  }
}