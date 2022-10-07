package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ProgressBean;
import com.osm.gnl.ippms.ogsg.domain.report.ReportStatus;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ReportRedirectController extends BaseController {

    @ResponseBody
    @RequestMapping({"/reportRedirect.do"})
    public String setupForm(Model model, HttpServletRequest request, HttpServletResponse response) throws HttpSessionRequiredException, EpmAuthenticationException, IOException, InterruptedException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        int ind = 0;

        Object o = getSessionAttribute(request, "reportStatus");
//        if (o == null) {
//            return "redirect:customReportGenerator.do";
//        }
//        if(o.getClass().isAssignableFrom(ReportOutputUtil.class)){
     //   CustomExcelReportGenerator wCP = new CustomExcelReportGenerator(rt, response, request,  contPenMapped.size());

       // Thread t = new Thread(wCP);
       // t.start();

        while(IppmsUtils.isNull(o)){
            o = getSessionAttribute(request, "reportStatus");
        }
        ReportStatus cERG = (ReportStatus) o;
                while(ind == 0){
                    if(cERG.isFinished()){
                        ind = 1;
                    }
                }

                if(cERG.getReportSize() < 25000){
                    Thread.sleep(5000);
                }
                else if(cERG.getReportSize() >= 25000){
                    Thread.sleep(10000);
                }
//                else if(cERG.getReportSize() >= 45000){
//                    Thread.sleep(20000);
//                }

//        Thread.sleep(5000);
        removeSessionAttribute(request, "reportStatus");
                return "success";
    }




}
