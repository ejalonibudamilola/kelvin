package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.NotificationService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseStatusClass;
import com.osm.gnl.ippms.ogsg.domain.approval.StepIncrementApproval;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Vector;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.ON;
import static com.osm.gnl.ippms.ogsg.constants.IConstants.STEP_INC_APPROVAL_CODE;

@Slf4j
public class PerformStepIncrement extends BaseStatusClass implements Runnable {

    private final GenericService genericService;
    private final FileParseBean fileParseBean;
    private final BusinessCertificate businessCertificate;
    private final PayrollFlag payrollFlag;
    private final LocalDate pendingPaycheckDate;

    public PerformStepIncrement(GenericService genericService, FileParseBean fileParseBean, BusinessCertificate businessCertificate,
                                PayrollFlag payrollFlag, LocalDate pendingPaycheckDate) {
        this.genericService = genericService;
        this.fileParseBean = fileParseBean;
        this.businessCertificate = businessCertificate;
        this.payrollFlag = payrollFlag;
        this.pendingPaycheckDate = pendingPaycheckDate;

    }

    @Override
    public void run() {
        try {
            doStepIncrement();
        } catch (Exception wEx) {
            log.error(wEx.getMessage());
            wEx.printStackTrace();
        }
    }

    private void doStepIncrement() throws Exception {
        startTime = System.currentTimeMillis();
        this.listSize = fileParseBean.getListToSave().size();
        this.fileParseBean.setTotalNumberOfRecords(this.listSize);
        if (this.listSize > 50)
            this.batchSize = 20;
        else
            this.batchSize = 5;


        StepIncrementApproval stepIncrementApproval;
        StepIncrementTracker stepIncrementTracker;
        for (NamedEntity p : fileParseBean.getListToSave()) {

            if (IppmsUtils.isNotNullAndGreaterThanZero(p.getMdaInstId())) {
                //--Needs Approval...
                stepIncrementApproval = new StepIncrementApproval();
                stepIncrementApproval.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                stepIncrementApproval.setParentId(p.getId());
                stepIncrementApproval.setEntityId(p.getId());
                stepIncrementApproval.setEntityName(p.getName());
                stepIncrementApproval.setEmployeeId(p.getStaffId());
                stepIncrementApproval.setLastModTs(LocalDate.now());
                stepIncrementApproval.setInitiatedDate(LocalDate.now());
                stepIncrementApproval.setTicketId(PayrollBeanUtils.getCurrentDateTimeAsLong(businessCertificate.getLoginId()));
                stepIncrementApproval.setInitiator(new User(businessCertificate.getLoginId()));
                stepIncrementApproval.setStepIncrementTracker(new StepIncrementTracker(p.getMdaInstId()));
                //stepIncrementApproval.setApprovedDate(LocalDate.now());
                stepIncrementApproval.setSalaryInfo(new SalaryInfo(p.getOldSalaryId()));
                stepIncrementApproval.setOldSalaryInfo(new SalaryInfo(p.getParentInstId()));
                genericService.saveObject(stepIncrementApproval);
                NotificationService.storeNotification(businessCertificate, genericService, stepIncrementApproval, "requestNotification.do?arid=" + stepIncrementApproval.getId() + "&s=1&oc=" + IConstants.STEP_INC_INIT_URL_IND, "Step Increment Request", STEP_INC_APPROVAL_CODE);


            } else {
                stepIncrementTracker = new StepIncrementTracker();
                stepIncrementTracker.setEmployee(new Employee(p.getId()));

                stepIncrementTracker.setNoOfTimes(1);
                stepIncrementTracker.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                stepIncrementTracker.setYear(LocalDate.now().getYear());
                genericService.storeObject(stepIncrementTracker);
            }
            Employee employee = genericService.loadObjectById(Employee.class, p.getId());
            employee.setSalaryInfo(new SalaryInfo(p.getParentInstId()));

            this.genericService.storeObject(employee);

            AbstractPromotionAuditEntity wPA = IppmsUtils.makePromotionAuditObject(businessCertificate);
            wPA.setEmployee(new Employee(p.getId()));
            wPA.setOldSalaryInfo(new SalaryInfo(p.getOldSalaryId()));
            wPA.setUser(new User(businessCertificate.getLoginId()));
            wPA.setLastModTs(LocalDate.now());
            wPA.setAuditTime(PayrollBeanUtils.getCurrentTime(false));
            wPA.setSalaryInfo(new SalaryInfo(p.getParentInstId()));
            wPA.setMdaInfo(new MdaInfo(employee.getMdaDeptMap().getMdaInfo().getId()));//This is actually the MDA_INST_ID now. Please note..
            wPA.setPromotionDate(LocalDate.now());
            wPA.setUser(new User(businessCertificate.getLoginId()));
            wPA.setAuditPayPeriod(PayrollUtils.makePayPeriodForAuditLogs(genericService, businessCertificate));
            wPA.setBusinessClientId(businessCertificate.getBusinessClientInstId());
            if (employee.isSchoolAssigned()) {
                wPA.setSchoolInfo(employee.getSchoolInfo());
            }
            wPA.setStepIncrementInd(ON);
            this.genericService.storeObject(wPA);

            this.fileParseBean.setSuccessfulRecords(this.fileParseBean.getSuccessfulRecords() + 1);
            this.currentPercentage += 1;
        }

        this.currentPercentage = this.listSize;
    }

    public FileParseBean getFileParseBean() {
        return fileParseBean;
    }

}
