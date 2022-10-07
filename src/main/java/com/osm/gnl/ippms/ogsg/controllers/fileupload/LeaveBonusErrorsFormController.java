package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusError;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusErrorBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Vector;


@Controller
@RequestMapping({"/displayLeaveBonusError.do"})
@SessionAttributes(types={FileParseBean.class})
public class LeaveBonusErrorsFormController extends BaseController {

	  private final String VIEW = "leave/leaveBonusErrorForm";
	  @Autowired
	  public LeaveBonusErrorsFormController()
	  {
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"el"})
	  public String setupForm(@RequestParam("el") String  pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

	     
	    Object wFPB = getSessionAttribute(request, pUid);
	    if ((wFPB == null) || (!wFPB.getClass().isAssignableFrom(FileParseBean.class))) {
	      return "redirect:fileUploadFailed.do";
	    }
	    FileParseBean _wFPB = (FileParseBean)wFPB;
	    MdaInfo mdaInfo = genericService.loadObjectById(MdaInfo.class,_wFPB.getMdaInstId());
		  _wFPB.setMode(mdaInfo.getName());

        _wFPB.setErrorList(setFormDisplayStyle(_wFPB.getErrorList()));
	    
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("failedUploadBean", _wFPB);

	    return VIEW;
	  }

	  
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"el","cl"})
	  public String setupForm(@RequestParam("el") String pUid,
			  @RequestParam("cl") String pStr, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);


	     
	    Object wFPB = getSessionAttribute(request, pUid);
	    if ((wFPB == null) || (!wFPB.getClass().isAssignableFrom(ArrayList.class))) {
	      return "redirect:fileUploadFailed.do";
	    }
	    Vector<NamedEntity> wVector = new Vector<NamedEntity>((ArrayList<NamedEntity>)wFPB);
	    
	    FileParseBean _wFPB = new FileParseBean();

	    _wFPB.setName(null);

	    if(_wFPB.getMode() == null || _wFPB.getMode().equalsIgnoreCase(EMPTY_STR)){
	    	_wFPB.setMode((String) getSessionAttribute(request, "mdaName"));
	    }
        _wFPB.setErrorList(setFormDisplayStyle(wVector));
	    
	    model.addAttribute("roleBean", bc);
	    model.addAttribute("failedUploadBean", _wFPB);

	    return VIEW;
	  }

	  

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, 
			  @ModelAttribute("failedUploadBean") FileParseBean pEHB, 
			  BindingResult result, SessionStatus status, 
			  Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);



	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
	    {
	       if(!pEHB.isDeleteWarningIssued()){
	    	   pEHB.setDeleteWarningIssued(true);
		    	 result.rejectValue("", "", "Note. All Data associated with this file upload will be lost!");
		    	 addDisplayErrorsToModel(model, request);
		         model.addAttribute("roleBean",bc);
		         model.addAttribute("failedUploadBean", pEHB);
		 	    return VIEW;
	       }
	      return "redirect:fileUploadDashboard.do";
	    }

	    //If we get here it is save. Time
	    if(!pEHB.isSaveMode()){
	    	pEHB.setSaveMode(true);
	    	 
	    	 result.rejectValue("", "", "Enter the save values....");
	        addDisplayErrorsToModel(model, request);
	         model.addAttribute("roleBean",bc);
	         model.addAttribute("failedUploadBean", pEHB);
	 	    return VIEW;
	    }else{
	    	//Check to make sure the necessary values are set.
	    	if(pEHB.getName() == null || pEHB.getName().equalsIgnoreCase(EMPTY_STR)){
	    		 result.rejectValue("", "", "Please enter a value for 'Unique ID'");
		         ((FileParseBean)result.getTarget()).setDisplayErrors("block");
		          
		         model.addAttribute("roleBean",bc);
		         model.addAttribute("failedUploadBean", pEHB);
		 	    return VIEW;
	    	}else
	    	if(pEHB.getDisplayName() == null || pEHB.getDisplayName().equalsIgnoreCase(EMPTY_STR)){
	    		 result.rejectValue("", "", "Please enter a value for 'Description'");
		         ((FileParseBean)result.getTarget()).setDisplayErrors("block");
		          
		         model.addAttribute("roleBean",bc);
		         model.addAttribute("failedUploadBean", pEHB);
		 	    return VIEW;
	    	}else{
	    		//Now See if we have a LeaveBonusErrorObject with the same name...
	    		LeaveBonusErrorBean wLBEB = this.genericService.loadObjectWithSingleCondition(LeaveBonusErrorBean.class, CustomPredicate.procurePredicate("name", pEHB.getName()));
	    		if(!wLBEB.isNewEntity()){
	    			result.rejectValue("", "", "An Error file with this name already exists. Please change the name.");
			        addDisplayErrorsToModel(model, request);
			         model.addAttribute("roleBean",bc);
			         model.addAttribute("failedUploadBean", pEHB);
			 	    return VIEW;
	    		}
	    	}
	    	//If we get here...Save all.
	    	LeaveBonusErrorBean wLBEB = new LeaveBonusErrorBean();
	    	wLBEB.setName(pEHB.getName());
	    	wLBEB.setDescription(pEHB.getDisplayName());
	    	wLBEB.setLastModBy(new User(bc.getLoginId()));
	    	wLBEB.setLastModTs(Timestamp.from(Instant.now()));
	    	wLBEB.setMode(pEHB.getMode());
	    	wLBEB.setLtgYearNum(pEHB.getRunYear());
	    	wLBEB.setCreatedBy(new User(bc.getLoginId()));
	    	this.genericService.saveObject(wLBEB);
	    	storeFileErrorDetails(wLBEB,pEHB.getErrorList(), LeaveBonusError.class);
	    }
	    return "redirect:fileUploadDashboard.do";
	  }

	private void storeFileErrorDetails(LeaveBonusErrorBean wLBEB, Vector<NamedEntity> errorList, Class<LeaveBonusError> leaveBonusErrorClass) {
		for (NamedEntity n : errorList) {
			try {
				LeaveBonusError l = new LeaveBonusError();
				l.setLeaveBonusErrorBean(wLBEB);
				l.setEmployeeId(n.getStaffId());
				l.setLeaveBonusAmount(n.getDeductionAmountStr());
				l.setErrorField(n.getErrorMsg());
				l.setEmployeeName(n.getName());

				genericService.saveObject(l);

			} catch (Exception wEx) {

				wEx.printStackTrace();
			}
		}

	}

	private Vector<NamedEntity> setFormDisplayStyle(Vector<NamedEntity> pEmpList)
	  {
	    int i = 1;
	    for (NamedEntity e : pEmpList) {
	      if (i % 2 == 1)
	        e.setDisplayStyle("reportEven");
	      else {
	        e.setDisplayStyle("reportOdd");
	      }
	      i++;
	    }
	    return pEmpList;
	  }

}
