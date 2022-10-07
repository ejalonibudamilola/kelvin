package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class Dashboard extends BaseController {

    public Dashboard() {}

    @RequestMapping({ "/determineDashBoard.do" })
    public String determineDashBoardFormHandler(Model model,
                                                HttpServletRequest request,
                                                @RequestParam(value = "s", required = false) boolean pSave)
            throws Exception {
       User user = SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if(bc == null){
            if(user != null && !user.isNewEntity()){
               bc = BusinessCertificateCreator.createBusinessCertificate(request,user,genericService);
            }

        }
        final String roleDisplayName = bc.getRoleDisplayName();

        addRoleBeanToModel(model,request);
        addMainHeader(model, roleDisplayName);
        addPageTitle(model, roleDisplayName);

        return "menu/userDashboard";
    }



    @RequestMapping(value="/", method=GET)
    public String dashboard(Model model) {
        return "redirect:/determineDashBoard.do";
    }
}
