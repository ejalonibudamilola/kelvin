package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

@Service("payslipFileService")
public class GeneratePayslipFile extends BaseController {


    private final GenericService genericService;
    private final PaySlipService paySlipService;
     @Autowired
    public GeneratePayslipFile(GenericService genericService, PaySlipService paySlipService) {
        this.genericService = genericService;
        this.paySlipService = paySlipService;
     }

    public File mailSinglePayslip(Long empId, int runMonth, int runYear, BusinessCertificate bc, HttpServletRequest request) throws Exception {

        EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();

        ReportGeneratorBean rt = new ReportGeneratorBean();

        empPayMiniBean.setParentInstId(empId);
        empPayMiniBean.setAdmin(bc.isSuperAdmin());

        IPaycheckGenerator paycheckGenerator;
        if(bc.isPensioner())
            paycheckGenerator = new PensionPaycheckGenerator();
        else
            paycheckGenerator = new PaycheckGenerator();

        AbstractPaycheckEntity employeePayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                CustomPredicate.procurePredicate("employee.id", empId), CustomPredicate.procurePredicate("runMonth", runMonth), CustomPredicate.procurePredicate("runYear",
                        runYear), getBusinessClientIdPredicate(request)));

        employeePayBean.setHiringInfo((this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
                CustomPredicate.procurePredicate("employee.id", empId), getBusinessClientIdPredicate(request)))));


        empPayMiniBean = (EmployeePayMiniBean) paycheckGenerator.generatePaySlipModel(empPayMiniBean, employeePayBean, this.genericService, bc,null,loadConfigurationBean(request),paySlipService);

       // SinglePaySlipPdfGenerator singlePaySlipPdfGenerator = new SinglePaySlipPdfGenerator();

        rt.setReportTitle(getFileName(employeePayBean, runMonth, runYear));
        rt.setBusinessCertificate(bc);
        rt.setEmployeePayMiniBean(empPayMiniBean);
        rt.setWatermark(bc.getBusinessName()+" PaySlip");

        return SinglePaySlipPdfGenerator.getInstance().getPdfAsFile(rt,request);
    }

    private String getFileName(AbstractPaycheckEntity employeePayBean, int runMonth, int runYear) {

        String initials = "";

        String firstName = employeePayBean.getParentObject().getFirstName();
        String lastName = employeePayBean.getParentObject().getLastName();
        if(IppmsUtils.isNotNullOrEmpty(employeePayBean.getParentObject().getInitials())) {
            initials = employeePayBean.getParentObject().getInitials();
            initials = initials.charAt(0)+"";
        }
        String empId = employeePayBean.getParentObject().getEmployeeId();
        String month = PayrollBeanUtils.getMonthNameFromInteger(runMonth);

        String fileName = lastName+firstName.charAt(0)+initials+"_"+empId+"_"+month+runYear;

        return fileName;
    }
}
