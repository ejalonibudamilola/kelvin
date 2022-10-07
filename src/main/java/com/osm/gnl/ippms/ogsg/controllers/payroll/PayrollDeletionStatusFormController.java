package com.osm.gnl.ippms.ogsg.controllers.payroll;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.DeletePendingPayroll;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;



@Controller
@RequestMapping({"/displayDeletePayroll.do"})
public class PayrollDeletionStatusFormController extends BaseController {

 
	 
	  public PayrollDeletionStatusFormController()
	  {}

 	@RequestMapping(method={RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);


	    ProgressBean wPB = new ProgressBean();
	    Object o = getSessionAttribute(request, "delPay");
	    if (o == null) {
	    	 return "redirect:deletePendingPayroll.do?pp=n&s=1";
	    }
	    DeletePendingPayroll wCP = (DeletePendingPayroll)o;
	     if (wCP.isFinished()) {
	    	      
	    	 	  String wPayPeriod = wCP.getPayPeriod();
	    	      removeSessionAttribute(request, "delPay");
	    	      //savePayrollInfomation(this.getSession(request), bc);
	    	      return "redirect:deletePendingPayroll.do?pp="+wPayPeriod+"&s=1";
	     }
	    	    wPB.setCurrentCount(wCP.getCurrentRecord());
	    	    wPB.setTotalElements(wCP.getTotalRecords());
	    	    wPB.setPercentage(wCP.getPercentage());
				wPB.setTimeRemaining(wCP.getTimeToElapse());

				addRoleBeanToModel(model, request);
	    model.addAttribute("progressBean", wPB);
	    return "payroll/deletePayrollProgressBarForm";
	  }


	 
	


}
