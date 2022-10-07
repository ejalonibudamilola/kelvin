package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.SinglePaySlipPdfGenerator;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.simulation.FuturePaycheckBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.utils.ThrashOldPdfs;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class CalculateBulkPaySlip extends BaseController {

    private Long parentId;

    private LocalDate fPayPeriodStart;
    private PayrollService payrollService;
    private String payPeriodStr;
    private String zipFileName;
    private ConfigurationBean configurationBean;
    private String filePath;




    public void generatePaySlip(BusinessCertificate bc, GenericService gServ,
                                  PayPeriodDaysMiniBean pPDMB, HttpServletResponse response, HttpServletRequest request, int totalListSize,
                                  ConfigurationBean configurationBean,PaySlipService paySlipService, String filePath) throws Exception {

        this.filePath = filePath;
        this.configurationBean = configurationBean;

        List<ReportGeneratorBean> beanList = new ArrayList<>();



                int runMonth = pPDMB.getStartLocalDate().getMonthValue();
                int runYear = pPDMB.getStartLocalDate().getYear();

        IPaycheckGenerator paycheckGenerator;
                if(bc.isPensioner())
                    paycheckGenerator = new PensionPaycheckGenerator();
                else
                    paycheckGenerator = new PaycheckGenerator();
                for(NamedEntityBean m : pPDMB.getNamedListBean()){
                    for(EmployeePayBean e : m.getEmpPayBeanList()){
                        ReportGeneratorBean rt = new ReportGeneratorBean();

                        EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
                        empPayMiniBean.setParentInstId(e.getEmployee().getId());
                        empPayMiniBean.setAdmin(bc.isSuperAdmin());

                        AbstractPaycheckEntity employeePayBean = (AbstractPaycheckEntity) gServ.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
                                CustomPredicate.procurePredicate("employee.id", e.getEmployee().getId()), CustomPredicate.procurePredicate("runMonth", runMonth), CustomPredicate.procurePredicate("runYear",
                                        runYear), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));


                        employeePayBean.setHiringInfo((gServ.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
                                CustomPredicate.procurePredicate("employee.id", e.getEmployee().getId()), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())))));

                        empPayMiniBean = (EmployeePayMiniBean) paycheckGenerator.generatePaySlipModel(empPayMiniBean, employeePayBean, gServ, bc,null,this.configurationBean, paySlipService);

                        rt.setReportTitle(e.getEmployee().getLastName()+"_"+e.getEmployee().getEmployeeId()+"_paySlip");
                        rt.setBusinessCertificate(bc);
                        rt.setEmployeePayMiniBean(empPayMiniBean);
                        rt.setWatermark(bc.getBusinessName()+" PaySlip");
                        rt.setOutputInd(false);
                        beanList.add(rt);

                    }
                }

                List<String> files = new ArrayList<>();
                File currDir;
                String fileLocation;
                String filename;
                for(int f=0; f<beanList.size(); f++){

                    SinglePaySlipPdfGenerator.getInstance().getPdf(beanList.get(f), filePath, response);

//                    currDir = new File(".");
//                    path = currDir.getAbsolutePath();
//                    fileLocation = path.substring(0, path.length() - 1) +beanList.get(f).getReportTitle()+".pdf";
                      filename = filePath+beanList.get(f).getReportTitle()+".pdf";
                      currDir = new File(filename);
                      fileLocation = currDir.getPath();
                      files.add(fileLocation);

                }

                zipPdfReports(files, response, bc);

    }

    private void zipPdfReports(List<String> files, HttpServletResponse response, BusinessCertificate bc) {
        FileOutputStream fos = null;
        ZipOutputStream zipOut;
        FileInputStream fis;
        String fileLocation = null;
        try {
            this.zipFileName = "BulkPaySlips"+bc.getOrgFileNameDiff()+PayrollBeanUtils.getDateAsStringWidoutSeparators(LocalDate.now())+PayrollBeanUtils.getCurrentTime(true)+".zip";


            File currDir = new File(filePath+zipFileName);

            fileLocation = currDir.getPath();



            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename="+"bulkPaySlip.zip");



            fos = new FileOutputStream(fileLocation);
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
            File input;
            ZipEntry ze;
            for(String filePath:files){
                 input = new File(filePath);
                fis = new FileInputStream(input);
                 ze = new ZipEntry(input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fis.read(tmp)) != -1){
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();
                fis.close();
            }
            zipOut.close(); 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(fos != null){
                    FileInputStream baos = new FileInputStream(fileLocation);
                    OutputStream os = response.getOutputStream();
                    byte[] buffer = new byte[8192];
                    int bytesRead;

                    while ((bytesRead = baos.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.close();
                    fos.close();
                }
                //initiate delete of all files
                files.add(fileLocation);
                ThrashOldPdfs.checkToDeleteOldPdfs(files);
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    public String getZipFileName(){
        return zipFileName;
    }

    private synchronized void saveCalculatedPayroll(List<FuturePaycheckBean> pEmpPayBeanList) {
        if (!pEmpPayBeanList.isEmpty()) {
            payrollService.saveCollection(pEmpPayBeanList);
        }
    }

    public String getPayPeriodStr() {
        if(fPayPeriodStart != null){
            payPeriodStr = " Period : "+fPayPeriodStart.getMonth().name()+" : "+fPayPeriodStart.getYear();
        }

        return payPeriodStr;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long pParentId) {
        this.parentId = pParentId;
    }
}
