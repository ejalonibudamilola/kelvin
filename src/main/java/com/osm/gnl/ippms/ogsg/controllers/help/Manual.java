package com.osm.gnl.ippms.ogsg.controllers.help;


import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping({"/manual.do"})
public class Manual extends BaseController {

    @RequestMapping(method=RequestMethod.GET)
    public String help(Model model, HttpServletRequest request){
        BusinessCertificate bc = super.getBusinessCertificate(request);

        addRoleBeanToModel(model, request);

        return "help/manual";
    }

    @RequestMapping(method={RequestMethod.GET}, params = {"deploy"})
    public String deploy(Model model, HttpServletRequest request, @RequestParam("deploy") String deploy){
        BusinessCertificate bc = super.getBusinessCertificate(request);
        List<BusinessClient> org = this.genericService.loadAllObjectsWithoutRestrictions(BusinessClient.class, null);

        addRoleBeanToModel(model, request);
        model.addAttribute("org", org);

        return "paygroup/editSalaryStructure";
    }

//    @RequestMapping(method = {RequestMethod.GET}, params = {"deployId"})
//    @ResponseBody
//    public String viewSentMessages(@RequestParam("deployId") long deployId, HttpServletRequest request, Model model) throws EpmAuthenticationException, HttpSessionRequiredException, IllegalAccessException, InstantiationException {
//        SessionManagerService.manageSession(request, model);
//        BusinessCertificate bc = this.getBusinessCertificate(request);
//        MessageObject msgObj = this.genericService.loadObjectById(MessageObject.class, mid);
//        String body = EMPTY_STR;
//
//        if(IppmsUtils.isNotNull(msgObj)){
//            body = msgObj.getMsgBody();
//        }
//        return body;
//    }

}
