package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.engine.CalculateBulkPaySlip;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({"/bulkPaySlipPdf.do"})
public class BulkPaySlipReportController extends BaseController {

    private final String VIEW = "progress/progressBarForm";
    private final PaySlipService paySlipService;

    @Autowired
    public BulkPaySlipReportController(PaySlipService paySlipService) {
        this.paySlipService = paySlipService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public void bulkPaySlip(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = this.getBusinessCertificate(request);


        PayPeriodDaysMiniBean pPDMB = (PayPeriodDaysMiniBean) getSessionAttribute(request, CHECK_REG_ATTR_NAME);
        int num = 0;
        for(NamedEntityBean m : pPDMB.getNamedListBean())
            if(IppmsUtils.isNotNullOrEmpty(m.getEmpPayBeanList()))
                num += m.getEmpPayBeanList().size();


        //ThrashOldPdfs.CheckToDeleteOldZippedFiles(request);
        //ThrashOldPdfs.checkToDeleteOldPdfs(request);

        String filePath = request.getServletContext().getRealPath("");
        new CalculateBulkPaySlip().generatePaySlip(bc, genericService, pPDMB, response, request, num,loadConfigurationBean(request),paySlipService, filePath);


//        CalculateBulkPaySlip wCP = new CalculateBulkPaySlip(bc, genericService, pPDMB, response, request, num,loadConfigurationBean(request),paySlipService, filePath);
//        addSessionAttribute(request, "myCalcPay", wCP);
//        Thread t = new Thread(wCP);
//        t.start();
//        return "redirect:displayStatus.do";

    }

}
