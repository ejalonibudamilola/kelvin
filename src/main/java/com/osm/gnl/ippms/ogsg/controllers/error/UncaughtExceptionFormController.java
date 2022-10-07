package com.osm.gnl.ippms.ogsg.controllers.error;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ErrorLogBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;




@Controller
@RequestMapping({"/globalError.do"})
@SessionAttributes(types={ErrorLogBean.class})
public class UncaughtExceptionFormController extends BaseController {


    private final static String VIEW = "customErrorForm";


	public UncaughtExceptionFormController(){
	}
	

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) 
		throws Exception {
		
		SessionManagerService.manageSession(request, model);
		
		
		 BusinessCertificate bc = super.getBusinessCertificate(request);
		StringBuffer wErrorMsg = new StringBuffer();
		String wCause = "java.lang.NullPointerException";
		Throwable exception = (Throwable) getSessionAttribute(request, IPPMS_EXCEPTION);
		
		if (exception != null) {
			
				 wCause = exception.toString();
				 wErrorMsg.append(wCause);
			 
			 wErrorMsg.append(" ");
			 StackTraceElement[] wSTE = exception.getStackTrace();
			 int count = 0;
			 for(StackTraceElement e :wSTE){
			 	 if(e.toString().indexOf("com.osm.gnl.ippms") != -1) {
					 wErrorMsg.append(e.toString());
					 wErrorMsg.append(" ");
					 count++;
					 break;
				 }else{
					 if (count < 70){
						 wErrorMsg.append(e.toString());
						 wErrorMsg.append(" ");
						 count++;
					 }else{
						 break;
					 }

				 }
				  
			 }
			
		}else{
			wErrorMsg.append(wCause);
		}
		ErrorLogBean wELB = new ErrorLogBean();
		wELB.setThrowable(exception);
		wELB.setUser(new User(bc.getLoginId()));
		wELB.setErrorCause(wCause);
		String wErrorStr = wErrorMsg.toString();
		if(wErrorStr.length() >= 40000)
		  wELB.setErrorMsg(wErrorStr.substring(40000));
		else
			wELB.setErrorMsg(wErrorStr);
		wELB.setLastModBy(bc.getUserName());
		addRoleBeanToModel(model, request);
		model.addAttribute("elbean", wELB);
		return VIEW;
		
	}
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value="_ok", required=false) String pSave,@ModelAttribute ("elbean")
			ErrorLogBean pBc, 
			BindingResult result, SessionStatus status, Model model,HttpServletRequest request) throws Exception {
	  	
		SessionManagerService.manageSession(request, model);

		//Save the Error First then Return 
		pBc.setLastModTs(LocalDate.now());
		pBc.setBusinessClientId(getBusinessCertificate(request).getBusinessClientInstId());
		pBc.setAuditTimeStamp(PayrollBeanUtils.getCurrentTime(false));
		this.genericService.storeObject(pBc);

	    return DETERMINE_DASHBOARD_URL;
	  
	}
	


}
