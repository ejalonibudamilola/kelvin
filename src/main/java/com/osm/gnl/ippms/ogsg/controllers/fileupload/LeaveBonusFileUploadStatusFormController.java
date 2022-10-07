package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.engine.ParseLeaveBonus;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/uploadLeaveBonusStatus.do"})
public class LeaveBonusFileUploadStatusFormController extends BaseController {

	  public LeaveBonusFileUploadStatusFormController()
	  {

	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);

	    ProgressBean wPB = new ProgressBean();

	    Object o = getSessionAttribute(request, "leaveBonusFileUpload");
	    if (o == null) {
	      return REDIRECT_TO_DASHBOARD;
	    }
	    ParseLeaveBonus wCP = (ParseLeaveBonus)o;

	    if (wCP.isFinished()) {
	      if (wCP.isFailed()) {
	        removeSessionAttribute(request, "leaveBonusFileUpload");

	        return "redirect:fileUploadFailed.do";
	      }

	      FileParseBean wPFB = wCP.getFileParseBean();
	      if ((wPFB == null) || (wPFB.getUniqueUploadId() == null)) {
	        return "redirect:fileUploadFailed.do";
	      }
	      addSessionAttribute(request, wPFB.getUniqueUploadId(), wPFB);
	      removeSessionAttribute(request, "leaveBonusFileUpload");
	      return "redirect:leaveBonusfileUploadReport.do?uid=" + wPFB.getUniqueUploadId();
	    }

	    model.addAttribute("progressBean", wPB);
	    return "fileupload/fileUploadProgressForm";
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, SessionStatus status, Model model, HttpServletRequest request)
	    throws Exception
	  {
		  SessionManagerService.manageSession(request, model);

	    Object o = getSessionAttribute(request, "leaveBonusFileUpload");
	    if (o == null) {
	      return REDIRECT_TO_DASHBOARD;
	    }
	    ParseLeaveBonus wCP = (ParseLeaveBonus)o;
	    wCP.setStop(true);
	    removeSessionAttribute(request, "leaveBonusFileUpload");

	    wCP = null;
	    Thread.sleep(200L);

	    return REDIRECT_TO_DASHBOARD;
	  }

}
