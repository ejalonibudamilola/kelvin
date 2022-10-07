package com.osm.gnl.ippms.ogsg.controllers.hr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
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


@Controller
@RequestMapping({"/empToBePromoted.do"})
@SessionAttributes(types={NamedEntity.class})
public class PromoNotifFormController extends BaseController
{

  @Autowired
  private PromotionService promotionService;

  public PromoNotifFormController()
  {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
  {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate businessCertificate = super.getBusinessCertificate(request);
	    
    List<PromotionTracker> wPTList = this.promotionService.getEmpToBePromotedList(businessCertificate);

    wPTList = setDisplayStyle(wPTList);

    model.addAttribute("miniBean", wPTList);

    return "promotionTrackerViewForm";
  }

  private List<PromotionTracker> setDisplayStyle(List<PromotionTracker> pList)
  {
    int i = 0;
    List<PromotionTracker> wRetList = new ArrayList<PromotionTracker>();
    for (PromotionTracker p : pList)
    {
      if (!p.getEmployee().isTerminated()) {
        p.setPromoteEmployeeRef(true);
        if ((i == 0) || (i % 2 == 0))
          p.setDisplayStyle("reportOdd");
        else {
          p.setDisplayStyle("reportEven");
        }
        i++;
        wRetList.add(p);
      }

    }

    return wRetList;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") NamedEntity pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);
	  

    return REDIRECT_TO_DASHBOARD;
  }
}