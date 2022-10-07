package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
@RequestMapping({"/paystubForm.do"})
public class AddPayStubForm extends BaseController
{


  public AddPayStubForm()
  {

  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"pid"})
  public String setupForm(@RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(CustomPredicate.procurePredicate("id", pid),getBusinessClientIdPredicate(request)));
     return "redirect:paySlip.do?pid="+empPayBean.getParentObject().getId()+"&rm="+empPayBean.getRunMonth()+"&ry="+empPayBean.getRunYear();
  }
  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "atn"})
  public String setupForm(@RequestParam("eid") Long pEmpId, @RequestParam("atn") String pAction, Model model, HttpServletRequest request) throws Exception {   SessionManagerService.manageSession(request, model);
  SessionManagerService.manageSession(request, model);

  BusinessCertificate bc = this.getBusinessCertificate(request);


    EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
    empPayMiniBean.setAdmin(bc.isSuperAdmin());
    PayrollFlag pf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);

    AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),
            Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEmpId),CustomPredicate.procurePredicate("runMonth", pf.getApprovedMonthInd()),CustomPredicate.procurePredicate("runYear", pf.getApprovedYearInd()),getBusinessClientIdPredicate(request)));


     return "redirect:paySlip.do?pid="+empPayBean.getParentObject().getId()+"&rm="+empPayBean.getRunMonth()+"&ry="+empPayBean.getRunYear();
  }


}