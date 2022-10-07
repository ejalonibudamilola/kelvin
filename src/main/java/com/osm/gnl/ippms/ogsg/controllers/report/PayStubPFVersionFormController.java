package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PaycheckGenerator;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PensionPaycheckGenerator;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping({"/printSinglePayStub.do"})
public class PayStubPFVersionFormController extends BaseController
{

   private final PaySlipService paySlipService;
    @Autowired
  public PayStubPFVersionFormController(PaySlipService paySlipService)
  {
      this.paySlipService = paySlipService;
  }

  
@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"pid"})
  public String setupForm(@RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception
  {
      SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

    EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
    empPayMiniBean.setAdmin(bc.isSuperAdmin());
    
    AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectById(IppmsUtils.getPaycheckClass(bc), pid);
      HiringInfo wHI = this.genericService.loadObjectWithSingleCondition(HiringInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), empPayBean.getParentObject().getId()));
      empPayBean.setHiringInfo(wHI);
    if(empPayBean.getNetPay() == 0) {

    	LocalDate retireDate = PayrollBeanUtils.calculateExpDateOfRetirement(wHI.getBirthDate(), wHI.getHireDate(),loadConfigurationBean(request), bc);
    	wHI.setExpectedDateOfRetirement(retireDate);

    }
     if(empPayBean.getSuspendedInd() == 1) {
    	LocalDate wStartDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), false),false);
        LocalDate wEndDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), true),true);
    	
    	List<SuspensionLog> wSL =  new ArrayList<>();//this.genericService.loadAllObjectsUsingRestrictions(SuspensionLog.class,Arrays.asList(CustomPredicate.procurePredicate("suspensionDate",wStartDate,wEndDate,"employee.id",empPayBean.getParentObject().getId());
        if(wSL.size() > 1) {
        	Comparator<SuspensionLog> wComp = Comparator.comparing(SuspensionLog::getSuspensionDate).reversed();
            Collections.sort(wSL,wComp);
        }
    	// empPayBean.setSuspendedLog(wSL.get(0));
       
    }
    
    if(empPayBean.getContractIndicator() == ON && empPayBean.getNetPay() < 1) {
   	 //Look for a Contract that ended before the beginning of that Month & Year...
   	 LocalDate wStartDate = PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(),empPayBean.getRunYear(), false);
    	
    	 
		List<ContractHistory> wSL = new ArrayList<>() ; //this.genericService.loadAllObjectsUsingRestrictions(ContractHistory.class,"contractEndDate",wStartDate,null,"employee.id",empPayBean.getParentObject().getId());
        if(wSL.size() > 1) {
        	Comparator<ContractHistory> wComp = Comparator.comparing(ContractHistory::getContractEndDate).reversed();
            Collections.sort(wSL,wComp);
        }
    	 empPayBean.getHiringInfo().setContractEndDate((wSL.get(0).getContractEndDate()));
    }
    if(bc.isPensioner()){
        model = (Model)new PensionPaycheckGenerator().generatePaySlipModel(empPayMiniBean, empPayBean, this.genericService,bc, model, loadConfigurationBean(request), paySlipService);
    }else{
        model = (Model)new PaycheckGenerator().generatePaySlipModel(empPayMiniBean, empPayBean, this.genericService,bc, model,loadConfigurationBean(request), paySlipService);
    }


    addRoleBeanToModel(model,request);
    return "payment/paystubPFVForm";
  }

}