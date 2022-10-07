package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.FileUploadService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusBean;
import com.osm.gnl.ippms.ogsg.leavebonus.domain.LeaveBonusMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class ProcessLeaveBonus implements Runnable {
    private final GenericService genericService;

    private final String fCurrentUser;

    private final int fListSize;
    private int fCurrentPercentage;
    private final int runMonth;
    private final int runYear;
    private boolean fStop;
    private final int wBatchSize;
    private final Long fMdaInstId;
    private final int fObjectInd;
    private final Vector<LeaveBonusBean> wSaveList;
    private final ArrayList<NamedEntity> errorList;
    private final Vector<NamedEntity> wListToIterate;
    private final HashMap<Long, Long> pendingLtgMap;
    private final HashMap<Long, LeaveBonusBean> newEmpMap;
    private final HashMap<Long, LeaveBonusBean> promoList;
    private final BusinessCertificate businessCertificate;
    private final List<Long> mapIds;
    private boolean errorFound;
    private String mdaName;
    private double fTotalAmountPaid;
    private Long leaveBonusMasterInstId;
    private String currentTime;
    private boolean deletable;
    private final HttpSession fSession;
    private FileUploadService fileUploadService;

    public ProcessLeaveBonus(int pYear, HashMap<Long, LeaveBonusBean> pPromoList,
                             HashMap<Long, LeaveBonusBean> pNewEmployees,
                             Vector<NamedEntity> pListToIterate, List<Long> pMapIds,
                             int pObjType, GenericService genericService, String pCurrentUser, Long pMdaInstId, HashMap<Long, Long> pPendingMap
            , HttpSession pSession, String pMdaName, BusinessCertificate bc) {
        this.businessCertificate = bc;
        this.genericService = genericService;
        this.mapIds = pMapIds;
        this.fMdaInstId = pMdaInstId;
        promoList = pPromoList;
        this.newEmpMap = pNewEmployees;
        this.fCurrentUser = pCurrentUser;
        //this.fSalaryMap = pSalMap;
        this.wListToIterate = pListToIterate;
        this.fListSize = wListToIterate.size();
        this.wBatchSize = 50;
        this.fObjectInd = pObjType;
        this.fStop = false;
        this.wSaveList = new Vector<LeaveBonusBean>();
        this.errorList = new ArrayList<NamedEntity>();
        this.runMonth = Calendar.getInstance().get(Calendar.MONTH);
        this.runYear = pYear;
        pendingLtgMap = pPendingMap;
        fSession = pSession;
        setMdaName(pMdaName);
    }

    public void run() {
        try {
            //this.fCalcPayPerEmployee.setNoOfDays(PayrollBeanUtils.getNoOfDays(this.runMonth, this.runYear));
            //First Determine that this list size is greater than 50
            List<Long> wList = new ArrayList<Long>();
            List<NamedEntity> wListToIterate = new ArrayList<NamedEntity>();
            if (fListSize <= 50) {


                for (NamedEntity n : this.wListToIterate) {
                    if (getState())
                        break;

                    wList.add(n.getId());
                    wListToIterate.add(n);
                    //this.fCurrentPercentage += 1;
                }

                //Find and Save Leave Bonus....
                processLeaveBonus(wList, wListToIterate);

            } else {


                for (NamedEntity n : this.wListToIterate) {

                    if (getState())
                        break;

                    wList.add(n.getId());
                    wListToIterate.add(n);
                    if (wList.size() == this.wBatchSize) {
                        processLeaveBonus(wList, wListToIterate);
                        wList = new ArrayList<Long>();
                        wListToIterate = new ArrayList<NamedEntity>();
                    }
                }

                if (!wList.isEmpty()) {
                    processLeaveBonus(wList, wListToIterate);
                    wList = new ArrayList<Long>();
                    wListToIterate = new ArrayList<NamedEntity>();
                }

            }


            if (!getState()) {
                if (errorList == null && errorList.isEmpty()) {
                    //First Create a Leave Bonus Master Bean..
                    LeaveBonusMasterBean wLBMB = this.genericService.loadObjectUsingRestriction(LeaveBonusMasterBean.class,
                            Arrays.asList(CustomPredicate.procurePredicate("mdaInfo.id", this.fMdaInstId),
                                    CustomPredicate.procurePredicate("runMonth", this.runMonth), CustomPredicate.procurePredicate("runYear", this.runYear )));
                    if (wLBMB.isNewEntity()) {
                        wLBMB.setMdaInfo(new MdaInfo(this.fMdaInstId));
                        wLBMB.setRunYear(runYear);
                        wLBMB.setRunMonth(runMonth);
                        wLBMB.setLastModBy(fCurrentUser);
                        wLBMB.setCreatedDate(LocalDate.now());
                        wLBMB.setCreatedTime(PayrollBeanUtils.getCurrentTime(false));
                        wLBMB.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                        this.setDeletable(true);
                        wLBMB.setCreatedBy(new User(businessCertificate.getLoginId()));
                    }
                    wLBMB.setTotalAmountPaid(wLBMB.getTotalAmountPaid() + fTotalAmountPaid);
                    wLBMB.setTotalNoOfEmp(wLBMB.getTotalNoOfEmp() + wSaveList.size());
                    this.genericService.saveObject(wLBMB);
                    fSession.setAttribute(fCurrentUser, wLBMB.getId());
                    this.setCurrentTime(wLBMB.getCreatedTime());
                    this.fileUploadService.storeLeaveBonus(wSaveList, wLBMB);
                } else {

                }


            }
            this.fCurrentPercentage = this.fListSize;
        } catch (Exception e) {
            this.fCurrentPercentage = -1;
            log.error("Critical Exception thrown from ProcessLeaveBonus " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void setCurrentTime(String pCreatedTime) {
        this.currentTime = pCreatedTime;

    }

    public void setDeletable(boolean pB) {
        this.deletable = pB;

    }

    //This method will process Leave Bonus.....
    private void processLeaveBonus(List<Long> pList, List<NamedEntity> pListToIterate) throws Exception {
        List<Long> wNewEmployees = new ArrayList<Long>();
        List<Long> wOldEmployees = new ArrayList<Long>();
        for (Long wInt : pList) {
            if (this.newEmpMap.containsKey(wInt)) {
                wNewEmployees.add(wInt);
            } else {
                wOldEmployees.add(wInt);
            }
        }
        //Now Find the 'Old' Employees...
        HashMap<Long, LeaveBonusBean> wEmployees = this.fileUploadService.loadEmpFromLastDecPayroll(this.runYear - 1, this.mapIds,  wOldEmployees,businessCertificate);

        //Now check to make sure all is well....

        //We need to filter these employees by certain rules....
        //1. By Suspension...
        //2. By Termination
        //3. By Approval For Payroll...
        boolean wErrorRecord = false;
        LeaveBonusBean wCompare = null;
        for (NamedEntity n : pListToIterate) {
            wErrorRecord = false;
            n.setDeductionAmountStr(String.valueOf(n.getDeductionAmount()));
            n.setLtgYear(String.valueOf(n.getReportType()));
            Long wInt = pendingLtgMap.get(n.getId());

            if (wInt != null) {
                n.setErrorMsg(businessCertificate.getStaffTypeName()+" already scheduled to be paid Leave Bonus for " + runYear);
                if (!this.isErrorFound())
                    setErrorFound(true);
                wErrorRecord = true;
                errorList.add(n);
                continue;
            }
            if (wEmployees.containsKey(n.getId())) {
                //Now check if this employee was promoted.
                if (this.promoList.containsKey(n.getId())) {
                    wCompare = promoList.get(n.getId());
                } else {
                    wCompare = wEmployees.get(n.getId());
                }

            } else if (this.newEmpMap.containsKey(n.getId())) {
                wCompare = this.newEmpMap.get(n.getId());
            } else {
                n.setErrorMsg("Salary Information for " + n.getName() + " not found for Dec " + (runYear - 1));
                if (!this.isErrorFound())
                    setErrorFound(true);
                wErrorRecord = true;
                errorList.add(n);
                continue;
            }

            if (!wCompare.isApprovedForPayroll()) {
                n.setErrorMsg("Employee not approved for Payroll");
                if (!this.isErrorFound())
                    setErrorFound(true);
                wErrorRecord = true;
                errorList.add(n);
                continue;
            } else if (wCompare.getSuspendedInd() == 1) {
                n.setErrorMsg("Employee is Suspended");
                if (!this.isErrorFound())
                    setErrorFound(true);
                wErrorRecord = true;
                errorList.add(n);
                continue;
            } else if (wCompare.getLastLtgPaid() != null) {
                //Now find out if it is this Year...

                if (wCompare.getLastLtgPaid().getYear() == this.runYear) {
                    n.setErrorMsg(businessCertificate.getStaffTypeName()+" already paid Leave Bonus for " + runYear);
                    if (!this.isErrorFound())
                        setErrorFound(true);
                    wErrorRecord = true;
                    errorList.add(n);
                    continue;
                }
            } else {
                //Now find out if the Employee is Retiring before July of the current year...
                LocalDate wRetDate = null;
                if (wCompare.getDateTerminated() != null) {
                    wRetDate = wCompare.getDateTerminated();
                } else {
                    if (wCompare.getExpRetireDate() != null)
                        wRetDate= wCompare.getExpRetireDate();
                    else { //hopes this does not happen often....
                        ConfigurationBean configurationBean = genericService.loadObjectWithSingleCondition(ConfigurationBean.class,CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()
                        ));
                        wRetDate = (PayrollBeanUtils.calculateExpDateOfRetirement(wCompare.getDateOfBirth(), wCompare.getDateOfHire(), configurationBean,businessCertificate));
                    }
                }
                if (wRetDate.getYear() == runYear) {
                    //Now if the Month is before June...
                    if (wRetDate.getYear() < Calendar.JUNE) {
                        n.setErrorMsg(businessCertificate.getStaffTypeName()+" NOT eligible for Leave Bonus for " + runYear);
                        n.setTitleField(businessCertificate.getStaffTypeName()+" is retiring before June 1st " + runYear);
                        if (!this.isErrorFound())
                            setErrorFound(true);
                        wErrorRecord = true;
                        errorList.add(n);
                        continue;
                    }
                }
            }
            if (!wErrorRecord) {
                //Now see if the Amount is almost equal....

                BigDecimal wAmt = new BigDecimal(wCompare.getLeaveBonusAmount()).setScale(2, RoundingMode.FLOOR);
                if (Math.abs(wAmt.doubleValue() - n.getDeductionAmount()) > 0.02) {
                    //Make this into an error record.
                    if (!this.isErrorFound())
                        setErrorFound(true);
                    n.setErrorMsg("Leave Bonus Amount is not valid. IPPMS calculated amount is " + PayrollHRUtils.getDecimalFormat().format(wAmt.doubleValue()));

                    n.setTitleField("IPPMS Amount =  " + PayrollHRUtils.getDecimalFormat().format(wAmt.doubleValue()) +
                            " : Supplied Amount = " + PayrollHRUtils.getDecimalFormat().format(n.getDeductionAmount()));
                    errorList.add(n);
                } else {
                    wCompare.setLeaveBonusAmount(n.getDeductionAmount());
                    this.fTotalAmountPaid += n.getDeductionAmount();
                    wSaveList.add(wCompare);
                }
            }
            this.fCurrentPercentage += 1;
        }


    }

    public boolean isErrorFound() {

        return errorFound;
    }

    private void setErrorFound(boolean pB) {
        this.errorFound = pB;

    }

    private boolean getState() {
        return this.fStop;
    }

    public int getPercentage() {
        if (this.fCurrentPercentage == -1)
            return 100;
        if (this.fCurrentPercentage == 0)
            return this.fCurrentPercentage;
        int wRetVal = Math.round(this.fCurrentPercentage / this.fListSize * 100);
        return wRetVal;
    }

    public void stop(boolean pStop) {
        this.fStop = pStop;
    }

    public int getCurrentRecord() {
        return this.fCurrentPercentage;
    }

    public int getTotalRecords() {
        return this.fListSize;
    }

    public boolean isFinished() {
        return (this.fCurrentPercentage == this.fListSize) || (this.fCurrentPercentage == -1);
    }

    public ArrayList<NamedEntity> getErrorList() {
        return errorList;
    }

    public Long getLeaveBonusMasterInstId() {

        return this.leaveBonusMasterInstId;
    }

    public void setLeaveBonusMasterInstId(Long pLeaveBonusMasterInstId) {
        leaveBonusMasterInstId = pLeaveBonusMasterInstId;

    }

    public String getCurrentTime() {
        return currentTime;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setMdaName(String mdaName) {
        this.mdaName = mdaName;
    }

    public String getMdaName() {
        return mdaName;
    }

}
