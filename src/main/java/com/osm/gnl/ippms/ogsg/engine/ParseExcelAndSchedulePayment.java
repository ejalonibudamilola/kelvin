package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryTemp;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncrementTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.annotation.AnnotationProcessor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class ParseExcelAndSchedulePayment
        implements Runnable {
    private String fileName;
    //private String confirmationNumber;
    private BusinessCertificate businessCertificate;
    private boolean fStop;
    private boolean finished;
    private boolean failed;
    private Integer transactionId;
    private int runMonth;
    private int runYear;
    private XSSFWorkbook fWb;
    private volatile FileParseBean fileParseBean;
    private HashMap<String, NamedEntity> wEmployeeMap;
    private HashMap<String, ?> wObjectMap;
    private HashMap<Long,HashMap<String,SalaryInfo>> salaryTypeLevelStepMap;
    private HashMap<Long,StepIncrementTracker> stepIncrementMap;
    private Vector<NamedEntity> wListToSave;
    private Vector<SalaryTemp> wSalInfoList;
    private Vector<NamedEntity> wErrorList;
    private volatile String uniqueUploadId;
    private HashMap<String, PayTypes> wPayTypeMap;
    private HashMap<String, NamedEntity> wInactiveEmpMap;
    private HashMap<String, NamedEntity> suspendedMap;
    //private HashMap<String, Long> fBankSortCodeMap;
   // private HashMap<String, Long> fBankBranchSortCodeMap;
    private List<Long> awaitingApproval,allowanceList;
    private HashMap<String,String> wUniqueMap;
    private ConfigurationBean configurationBean;

    public ParseExcelAndSchedulePayment(XSSFWorkbook pWorkBook, FileParseBean pFileParseBean, Object pSessionId, HashMap<String,
            ?> pObjectMap, HashMap<String, NamedEntity> pEmployeeMap,@NonNull String pUploadUniqueIdentifier, int pNextRunMonth, int pNextRunYear,
                                        HashMap<String, PayTypes> pPayTypeMap, HashMap<String, NamedEntity> pInactiveEmpMap, BusinessCertificate bc, HashMap<String,
            NamedEntity> pSuspendedMap, String fileName, ConfigurationBean configurationBean) {
        this.fileParseBean = pFileParseBean;
        this.businessCertificate = bc;
        this.wEmployeeMap = pEmployeeMap;
        this.wObjectMap = pObjectMap;
        this.wListToSave = new Vector<>();
        this.wErrorList = new Vector<>();
        this.uniqueUploadId = pUploadUniqueIdentifier;
        this.runMonth = pNextRunMonth;
        this.runYear = pNextRunYear;
        this.wPayTypeMap = pPayTypeMap;
        this.wInactiveEmpMap = pInactiveEmpMap;
        this.fWb = pWorkBook;
        this.fileName = fileName;
        this.suspendedMap = pSuspendedMap;
        this.configurationBean = configurationBean;

    }

    public ParseExcelAndSchedulePayment(XSSFWorkbook pWorkBook, FileParseBean pFileParseBean, HashMap<Long, StepIncrementTracker> pStepIncrementMap,
                                        HashMap<Long, HashMap<String, SalaryInfo>> pSalTypeLevelStepMapMap,List<Long> allowanceMap,List<Long> awaitingApprovalList,
                                         HashMap<String, NamedEntity> pEmployeeMap, @NonNull String pUploadUniqueIdentifier,
                                        HashMap<String, NamedEntity> pInactiveEmpMap, BusinessCertificate bc, HashMap<String, NamedEntity> pSuspendedMap,
                                        String fileName, ConfigurationBean configurationBean) {
        this.fileParseBean = pFileParseBean;
        this.businessCertificate = bc;
        this.wEmployeeMap = pEmployeeMap;
        this.stepIncrementMap = pStepIncrementMap;
        this.salaryTypeLevelStepMap = pSalTypeLevelStepMapMap;
        this.wListToSave = new Vector<>();
        this.wErrorList = new Vector<>();
        this.uniqueUploadId = pUploadUniqueIdentifier;
        this.wInactiveEmpMap = pInactiveEmpMap;
        this.fWb = pWorkBook;
        this.fileName = fileName;
        this.suspendedMap = pSuspendedMap;
        this.configurationBean = configurationBean;
        this.allowanceList = allowanceMap;
        this.awaitingApproval = awaitingApprovalList;
    }

    public void run() {

        try {
            switch (this.fileParseBean.getObjectTypeInd()) {
                case 2:
                    parseLoanExcelSheet();
                    break;
                case 3:
                    parseDeductionExcelSheet();
                    break;
                case 5:
                    parseSpecialAllowanceExcelSheet();
                    break;
                case 6:
                    parseSalaryStructureExcelSheet();
                    break;
                case 7:
                    parseStepIncrementExcelSheet();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.fStop = true;
            this.finished = true;

            this.fWb = null;

        }
        // }
    }

    private void parseStepIncrementExcelSheet() {



            int noOfSheets = this.fWb.getNumberOfSheets();
            XSSFCell cell;
            XSSFRow row;
            NamedEntity wNamedEntity;
            StringBuffer wErrorMsgBuffer;
            String employeeId;
            boolean errorRecord;
            HashMap<String, SalaryInfo> parentMap;
            XSSFSheet wSheet;
            Iterator<Row> it;
            SalaryInfo salaryInfo;
            String stepIncrease;
            StepIncrementTracker stepIncrementTracker;
            int x;
            wUniqueMap = new HashMap<>();
        for (int i = 0; i < noOfSheets; i++) {
                if (isStop()) {
                    break;
                }
                 wSheet = this.fWb.getSheetAt(i);
                 it = wSheet.rowIterator();
                  x = 0;
                while (it.hasNext()) {
                    if (isStop()) {
                        break;
                    }
                    row = (XSSFRow) it.next();
                    if (x == 0) {
                        x++;
                        continue;
                    }
                    cell = row.getCell(0);
                    wNamedEntity = new NamedEntity();
                    errorRecord = false;
                    wErrorMsgBuffer = new StringBuffer();

                    if ((cell == null))
                        break;

                    employeeId = String.valueOf(getValueFromCell(cell, businessCertificate.getStaffTitle()));

                    if (employeeId.indexOf("Error") != -1 || employeeId.isEmpty()) {
                        break;
                    }

                    wNamedEntity.setStaffId(employeeId);
                    //Check Active Employee Map
                    if (!this.wEmployeeMap.containsKey(employeeId.trim().toUpperCase())) {

                        if (this.wInactiveEmpMap.containsKey(employeeId.trim().toUpperCase())) {
                            wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Terminated.");
                            wNamedEntity.setName(wInactiveEmpMap.get(employeeId.trim().toUpperCase()).getName());
                            wErrorMsgBuffer.append("Terminated " + businessCertificate.getStaffTypeName());
                            errorRecord = true;
                        } else if (this.suspendedMap.containsKey(employeeId.trim().toUpperCase())) {
                            wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Suspended.");
                            wNamedEntity.setName(suspendedMap.get(employeeId.trim().toUpperCase()).getName());
                            wErrorMsgBuffer.append("Invalid  " + businessCertificate.getStaffTitle());
                            errorRecord = true;
                        } else {
                            wNamedEntity.setDisplayErrors(businessCertificate.getStaffTitle() + " not found.");
                            wNamedEntity.setName("Unknown");
                            wErrorMsgBuffer.append("Invalid  " + businessCertificate.getStaffTitle());
                            errorRecord = true;
                        }

                    } else {

                        wNamedEntity = this.wEmployeeMap.get(employeeId.trim().toUpperCase());
                    }
                    //--Here check if the Code and the ID are the map
                    if(wUniqueMap.containsKey(employeeId.trim().toUpperCase())){
                        errorRecord = true;
                        wErrorMsgBuffer.append("Duplicate "+ businessCertificate.getStaffTypeName()+ " Step Increment.");
                    }else{
                        wUniqueMap.put(employeeId.trim().toUpperCase(),employeeId.trim().toUpperCase());
                    }
                    //Find the Step Increment here....
                    if(!errorRecord){
                        parentMap = this.salaryTypeLevelStepMap.get(wNamedEntity.getSalaryTypeId());
                        if(parentMap == null){
                            wNamedEntity.setDisplayErrors("Pay Group "+wNamedEntity.getPayTypeName() + " is currently inactive.");
                            wErrorMsgBuffer.append("Invalid  Pay Group" );
                            errorRecord = true;
                        }else{
                            stepIncrease = PayrollUtils.makeLevelAndStep(wNamedEntity.getLevel(), wNamedEntity.getStep() + 1);
                            salaryInfo = parentMap.get(stepIncrease);
                            if(salaryInfo == null){
                                wNamedEntity.setDisplayErrors("Level & Step   "+wNamedEntity.getLevelAndStep() + " can not be Incremented.");
                                wErrorMsgBuffer.append(businessCertificate.getStaffTypeName()+" At Bar." );
                                errorRecord = true;
                            } else{
                                //first make sure there is no awaiting approval and no Allowance Rule
                                if(this.stepIncrementMap.containsKey(wNamedEntity.getId())){
                                    wNamedEntity.setDisplayErrors("Step Increment Approval Exists.");
                                    wErrorMsgBuffer.append("Step Increment Approval Found" );
                                    errorRecord = true;
                                }
                                if(awaitingApproval.contains(wNamedEntity.getHiringInfoId())){
                                    wNamedEntity.setDisplayErrors("Allowance Rule Exists");
                                    wErrorMsgBuffer.append("Allowance Rule exists" );
                                    errorRecord = true;
                                }
                                //--Here we need to see if this dude has been implemented once this year...
                                stepIncrementTracker = this.stepIncrementMap.get(wNamedEntity.getId());

                                if(!errorRecord) {
                                    if (stepIncrementTracker == null) {

                                    } else {
                                        if (stepIncrementTracker.getNoOfTimes() == 1) {
                                            wNamedEntity.setMdaInstId(stepIncrementTracker.getId());
                                        } else {
                                            //Error the Record out. this means we have an error record
                                            wNamedEntity.setDisplayErrors("Step Increment done two(2) times this year");
                                            wErrorMsgBuffer.append(businessCertificate.getStaffTypeName() + " has 2 Increments already this year.");
                                            errorRecord = true;
                                        }
                                    }
                                }
                                if(!errorRecord) {
                                   // NamedEntity oldNamedEntity = this.wEmployeeMap.get(employeeId.trim().toUpperCase());
                                    wNamedEntity.setParentInstId(salaryInfo.getId());
                                    wNamedEntity.setDeductionAmountStr(wNamedEntity.getLevelAndStep());
                                    wNamedEntity.setStaffId(employeeId.trim().toUpperCase());
                                    wNamedEntity.setObjectCode(stepIncrease);

                                }


                            }
                        }
                    }

                    if (errorRecord) {
                        wNamedEntity.setDisplayErrors(wErrorMsgBuffer.toString());

                        wNamedEntity.setStaffId(employeeId);

                        this.wErrorList.add(wNamedEntity);
                    } else {

                        this.wListToSave.add(wNamedEntity);
                    }

                }

                this.fileParseBean.setErrorList(this.wErrorList);
                this.fileParseBean.setEmployeeMap(this.wEmployeeMap);
                this.fileParseBean.setListToSave(this.wListToSave);
                this.fileParseBean.setUniqueUploadId(this.uniqueUploadId);
                this.fileParseBean.setObjectTypeInd(7);
                this.fileParseBean.setObjectTypeClass(StepIncrementTracker.class);
                this.fileParseBean.setInactiveEmpMap(this.wInactiveEmpMap);
                this.fileParseBean.setSuspendedMap(this.suspendedMap);
                this.fileParseBean.setConfigurationBean(configurationBean);
                this.finished = true;
            }


    }

    private void parseSalaryStructureExcelSheet() {


        int noOfSheets = this.fWb.getNumberOfSheets();
        this.wSalInfoList = new Vector<>();
        Map<String, String> wMap = AnnotationProcessor.getFieldsForExcelParsing(new SalaryInfo());
        SalaryTemp s;
        String cellValue;
        double grossPay;
        double sum = 0.0D;
        int intField;
        double doubleField;
        NamedEntity wNamedEntity;
        int currRow = 0;
        int currCol = 0;
        Map<Integer,String> headerMap;
        XSSFSheet wSheet;
        Iterator<Row> it;
        XSSFRow row;
        XSSFCell cell;
        boolean errorRecord;
        StringBuffer wErrorMsgBuffer;
        String currColumnName;
        wUniqueMap = new HashMap<>();
        for (int i = 0; i < noOfSheets; i++) {
            if (isStop()) {
                break;
            }
            wSheet = this.fWb.getSheetAt(i);

              it = wSheet.rowIterator();

            while (it.hasNext()) {
                if (isStop()) {
                    // is.close();
                    break;
                }
                 row = (XSSFRow) it.next();
                if (currRow == 0) {
                    currRow++;
                    continue;
                }


                cell = row.getCell(currCol++);
                wNamedEntity = new NamedEntity();
                errorRecord = false;
                wErrorMsgBuffer = new StringBuffer();


                if (cell == null)
                    break;
                s = new SalaryTemp();
                cellValue = String.valueOf(getValueFromCell(cell, "Grade Level"));

                if (cellValue.indexOf("Error") != -1 || cellValue.isEmpty()) {
                    break;
                } else {
                    try {
//                        intField = Integer.parseInt(cellValue);
                        intField = new BigDecimal(cellValue).setScale(0, RoundingMode.HALF_UP).intValue();

                        s.setLevel(intField);
                        wNamedEntity.setName(cellValue);
                    } catch (Exception ex) {
                        errorRecord = true;
                        wNamedEntity.setName(cellValue);
                        wErrorMsgBuffer.append("Invalid value for Grade Level " + cellValue);
                    }
                }
                cellValue = String.valueOf(getValueFromCell(row.getCell(currCol++), "Step"));
                if (cellValue.indexOf("Error") != -1 || cellValue.isEmpty()) {
                    break;
                } else {
                    try {
//                        intField = Integer.parseInt(cellValue);
                        intField = new BigDecimal(cellValue).setScale(0, RoundingMode.HALF_UP).intValue();

                        s.setStep(intField);
                        wNamedEntity.setStaffId(cellValue);
                    } catch (Exception ex) {
                        if (!errorRecord) {
                            errorRecord = true;
                        } else {
                            wErrorMsgBuffer.append(" | ");
                        }
                        wNamedEntity.setStaffId(cellValue);
                        wErrorMsgBuffer.append("Invalid value for Step " + cellValue);
                    }
                }
                if(!errorRecord){
                    if(wUniqueMap.containsKey(s.getLevel()+""+s.getStep())){
                        errorRecord = true;
                        wErrorMsgBuffer.append("Duplicate Level & Step Found");
                    }else{
                        wUniqueMap.put(s.getLevel()+""+s.getStep(),s.getLevel()+""+s.getStep());
                    }
                }

                cellValue = String.valueOf(getValueFromCell(row.getCell(currCol++), "Basic Salary"));
                if (cellValue.indexOf("Error") != -1 ) {
                    break;
                } else {
                    if(cellValue.isEmpty())
                        cellValue = "0.00";
                    try {
                        doubleField = Double.parseDouble(PayrollHRUtils.removeCommas(cellValue));
                        s.setMonthlyBasicSalary(doubleField);
                    } catch (Exception ex) {
                        if (!errorRecord) {
                            errorRecord = true;
                        } else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wErrorMsgBuffer.append("Invalid value for Basic Salary " + cellValue);
                    }
                }
               // int currColumn = 0;

                headerMap = makeHeaderMap(wSheet, currCol);
               // List<String> headers = new ArrayList<>(headerMap.values());

                for (int j = 0; j < headerMap.size(); j++) {

                    cellValue = getValueFromCell(row.getCell(j + 3));
                    currColumnName = headerMap.get(j + 4);
                    if (cellValue.indexOf("Error") != -1) {
                        if (!errorRecord)
                            errorRecord = true;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setAllowanceAmount(0.0D);
                        wErrorMsgBuffer.append("Invalid Amount for " + currColumnName);
                    } else {
                        if(cellValue.isEmpty())
                            cellValue = "0.00";
                        try {
                            doubleField = Double.parseDouble(PayrollHRUtils.removeCommas(cellValue));
                            //sum monthly gross
                            sum+=doubleField;

                            Field field = s.getClass().getSuperclass().getDeclaredField(wMap.get(currColumnName));
                            field.setAccessible(true);
                            field.set(s, doubleField);
                        } catch (Exception wEx) {

                            if (!errorRecord)
                                errorRecord = true;
                            else {
                                wErrorMsgBuffer.append(" | ");
                            }

                            wErrorMsgBuffer.append("Invalid Amount for " + currColumnName + " (" + cellValue + ")");
                        }
                    }
                }

                //add basic salary to allowances
                grossPay = s.getMonthlyBasicSalary() + sum;
                s.setMonthlyGrossPay(EntityUtils.convertDoubleToEpmStandard(grossPay/12.0D));
                s.setAnnualSalary(EntityUtils.convertDoubleToEpmStandard(grossPay));
                if (errorRecord) {
                    wNamedEntity.setDisplayErrors(wErrorMsgBuffer.toString());
                    this.wErrorList.add(wNamedEntity);
                } else {
                    this.wSalInfoList.add(s);
                }
                currCol = 0;

            }

            this.fileParseBean.setErrorList(this.wErrorList);
            this.fileParseBean.setPayGroupList(wSalInfoList);
            this.fileParseBean.setUniqueUploadId(this.uniqueUploadId);
            this.fileParseBean.setObjectTypeInd(6);
            this.fileParseBean.setObjectTypeClass(SalaryInfo.class);
            this.finished = true;

        }
    }

    private Map<Integer, String> makeHeaderMap(XSSFSheet wSheet, int currColumn) {
        Iterator<Row> it = wSheet.rowIterator();
        Map<Integer,String> wRetMap = new HashMap<>();
        int x = 0;
        XSSFRow row;
        String headerName;
        row = (XSSFRow) it.next();
        while (it.hasNext()) {

            if (x < currColumn) {
                x++;
                continue;
            }
            headerName = String.valueOf(getValueFromCell(row.getCell(x++)));
            if(headerName.indexOf("Error") ==  -1){
                wRetMap.put(x,headerName);
            }else{
                break;
            }

        }
        return  wRetMap;
    }

    private synchronized void parseDeductionExcelSheet()
            throws Exception {

        int noOfSheets = this.fWb.getNumberOfSheets();
        XSSFCell cell;
        XSSFRow row;
        NamedEntity wNamedEntity;
        StringBuffer wErrorMsgBuffer;
        String employeeId;
        boolean errorRecord;
        String deductionCode;
        String amount;
        boolean hasDedCodeError;
        double _amount;
        String startDate;
        String endDate;
        LocalDate wEndDate;
        XSSFSheet wSheet;
        Iterator<Row> it;
         wUniqueMap = new HashMap<>();
        for (int i = 0; i < noOfSheets; i++) {
            if (isStop()) {
                //  is.close();
                break;
            }
             wSheet = this.fWb.getSheetAt(i);
             it = wSheet.rowIterator();
            int x = 0;
            while (it.hasNext()) {
                if (isStop()) {
                    // is.close();
                    break;
                }
                row = (XSSFRow) it.next();
                if (x == 0) {
                    x++;
                    continue;
                }
                cell = row.getCell(0);
                wNamedEntity = new NamedEntity();
                errorRecord = false;
                wErrorMsgBuffer = new StringBuffer();

                if ((cell == null))
                    break;

                employeeId = String.valueOf(getValueFromCell(cell, businessCertificate.getStaffTitle()));

                if (employeeId.indexOf("Error") != -1 || employeeId.isEmpty()) {
                    break;
                }

                wNamedEntity.setStaffId(employeeId);
                //Check Active Employee Map
                if (!this.wEmployeeMap.containsKey(employeeId.trim().toUpperCase())) {

                    if (this.wInactiveEmpMap.containsKey(employeeId.trim().toUpperCase())) {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Terminated.");
                        wNamedEntity.setName(wInactiveEmpMap.get(employeeId.trim().toUpperCase()).getName());
                        wErrorMsgBuffer.append("Terminated " + businessCertificate.getStaffTypeName());
                        errorRecord = true;
                    } else if (this.suspendedMap.containsKey(employeeId.trim().toUpperCase())) {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Suspended.");
                        wNamedEntity.setName(suspendedMap.get(employeeId.trim().toUpperCase()).getName());
                        wErrorMsgBuffer.append("Invalid  " + businessCertificate.getStaffTitle());
                        errorRecord = true;
                    } else {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTitle() + " not found.");
                        wNamedEntity.setName("Unknown");
                        wErrorMsgBuffer.append("Invalid  " + businessCertificate.getStaffTitle());
                        errorRecord = true;
                    }

                } else {
                    wNamedEntity.setName(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getName());
                }

                hasDedCodeError = false;
                cell = row.getCell(1);
                if (cell == null) {
                    break;
                }
                deductionCode = getValueFromCell(cell, "Deduction Code");

                if (deductionCode.indexOf("Error") != -1) {
                    if (!errorRecord)
                        errorRecord = !errorRecord;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }
                    hasDedCodeError = true;
                    wNamedEntity.setObjectCode(deductionCode);
                    wErrorMsgBuffer.append("Invalid Deduction Code");
                } else if (!this.wObjectMap.containsKey(deductionCode.trim().toUpperCase())) {
                    hasDedCodeError = true;
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setObjectCode(deductionCode);
                    wErrorMsgBuffer.append("Invalid Deduction Code");
                }

                //--Here check if the Code and the ID are the map
                if(wUniqueMap.containsKey(employeeId.trim().toUpperCase()+deductionCode)){
                    errorRecord = true;
                    wErrorMsgBuffer.append("Duplicate "+ businessCertificate.getStaffTypeName()+" Deduction ["+deductionCode+"]");
                }else{
                    wUniqueMap.put(employeeId.trim().toUpperCase()+deductionCode,employeeId.trim().toUpperCase()+deductionCode);
                }
                 _amount = 0.0D;
                cell = row.getCell(2);
                amount = getValueFromCell(cell, "Amount");

                //---Begin
                if (amount.indexOf("Error") != -1) {
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setAllowanceAmount(0.0D);
                    wErrorMsgBuffer.append("Invalid Amount");
                } else {

                    try {
                        _amount = Double.parseDouble(PayrollHRUtils.removeCommas(amount));
                        //if we get here...amount is good.
                        if (!hasDedCodeError) {
                            EmpDeductionType wEDT = (EmpDeductionType) this.wObjectMap.get(deductionCode.trim().toUpperCase());
                            if (wEDT.getPayTypes().isUsingPercentage()) {
                                if (_amount > configurationBean.getMaxDeductionValue()) {
                                    wNamedEntity.setDeductionAmount(_amount);
                                    wNamedEntity.setDeductionAmountStr(PayrollHRUtils.getDecimalFormat().format(_amount));
                                    wErrorMsgBuffer.append("Amount must be less than or equal to " + configurationBean.getMaxDeductionValue());
                                    errorRecord = true;
                                }
                            }
                        }

                    } catch (Exception wEx) {
                        _amount = 0.0D;
                        if (!errorRecord)
                            errorRecord = true;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setDeductionAmount(_amount);
                        wErrorMsgBuffer.append("Invalid Amount");
                    }
                }

                //=====END

                //Parse Start Date Column

                cell = row.getCell(3);

                if (cell == null) {
                    startDate = "";
                } else {
                    startDate = getValueFromCell(cell, "Start Date (DD-MMM-YYYY)");
                }

                LocalDate wStartDate = null;
                //GregorianCalendar wGC = new GregorianCalendar();
                // wGC = (GregorianCalendar)PayrollBeanUtils.makeNextPayPeriodStart(this.runMonth, this.runYear);

                if ((startDate.indexOf("Error") != -1) || (startDate.equals(""))) {

                    if (this.wObjectMap.containsKey(deductionCode.trim().toUpperCase())) {
                        EmpDeductionType sEDT = (EmpDeductionType) this.wObjectMap.get(deductionCode.trim().toUpperCase());
                        if (sEDT.isMustEnterDate()) {
                            if (!errorRecord)
                                errorRecord = true;
                            else {
                                wErrorMsgBuffer.append(" | ");
                            }

                            wErrorMsgBuffer.append("Start Date is Required");
                        }
                    }


                    //wStartDate.setTime(wGC.getTime());

                } else if (startDate != null) {
                    try {
                        //wStartDate = PayrollHRUtils.getFullDateFormat().format(startDate);

                        wStartDate = PayrollBeanUtils.setDateFromStringExcel(startDate);
                    } catch (Exception wEx) {
                        wStartDate = null;
                        if (!errorRecord)
                            errorRecord = !errorRecord;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setStartDateString(startDate);
                        wErrorMsgBuffer.append("Start Date is Invalid");

                    }

                }


                cell = row.getCell(4);
                if (cell == null)
                    endDate = "";
                else {
                    endDate = getValueFromCell(cell, "End Date (DD-MMM-YYYY)");
                }

                wEndDate = null;
                if ((endDate.indexOf("Error") != -1) || (endDate.equals(""))) {

                    if (this.wObjectMap.containsKey(deductionCode.trim().toUpperCase())) {
                        EmpDeductionType sEDT = (EmpDeductionType) this.wObjectMap.get(deductionCode.trim().toUpperCase());
                        if (sEDT.isMustEnterDate()) {
                            if (!errorRecord)
                                errorRecord = true;
                            else {
                                wErrorMsgBuffer.append(" | ");
                            }

                            wErrorMsgBuffer.append("End Date is Required");
                        }
                    }

                } else {
                    try {

                        wEndDate = PayrollBeanUtils.setDateFromStringExcel(endDate);
                    } catch (Exception wEx) {
                        wEndDate = null;
                        if (!errorRecord)
                            errorRecord = !errorRecord;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setEndDateString(endDate);
                        wErrorMsgBuffer.append("Invalid End Date");
                    }

                }

                if (errorRecord) {
                    wNamedEntity.setDisplayErrors(wErrorMsgBuffer.toString());
                    wNamedEntity.setObjectCode(deductionCode);
                    wNamedEntity.setStaffId(employeeId);
                    wNamedEntity.setDeductionAmount(_amount);

                    wNamedEntity.setStartDate(wStartDate);
                    wNamedEntity.setEndDate(wEndDate);


                    this.wErrorList.add(wNamedEntity);
                } else {
                    wNamedEntity = new NamedEntity();
                    NamedEntity oldNamedEntity = this.wEmployeeMap.get(employeeId.trim().toUpperCase());
                    wNamedEntity.setDeductionAmount(_amount);
                    wNamedEntity.setDeductionAmountStr(PayrollHRUtils.getDecimalFormat().format(_amount));
                    wNamedEntity.setStaffId(employeeId.trim().toUpperCase());
                    wNamedEntity.setObjectCode(deductionCode);
                    wNamedEntity.setName(oldNamedEntity.getName());
                    wNamedEntity.setId(oldNamedEntity.getId());
                    wNamedEntity.setOrganization(oldNamedEntity.getOrganization());
                    wNamedEntity.setPayTypeName(oldNamedEntity.getPayTypeName() + " " + oldNamedEntity.getLevelAndStep());
                    if (wStartDate != null)
                        wNamedEntity.setStartDate(wStartDate);
                    if (wEndDate != null)
                        wNamedEntity.setEndDate(wEndDate);
                    if (wStartDate != null && wEndDate != null)
                        wNamedEntity.setStartAndEndPeriod(PayrollBeanUtils.getDateAsString(wStartDate) + " - " + PayrollBeanUtils.getDateAsString(wEndDate));
                    this.wListToSave.add(wNamedEntity);
                }

            }

            this.fileParseBean.setErrorList(this.wErrorList);
            this.fileParseBean.setEmployeeMap(this.wEmployeeMap);
            this.fileParseBean.setListToSave(this.wListToSave);
            this.fileParseBean.setUniqueUploadId(this.uniqueUploadId);
            this.fileParseBean.setObjectTypeInd(3);
            this.fileParseBean.setObjectTypeClass(EmpDeductionType.class);
            this.fileParseBean.setObjectMap(this.wObjectMap);
            this.fileParseBean.setPayTypeMap(wPayTypeMap);
            this.fileParseBean.setInactiveEmpMap(this.wInactiveEmpMap);
            this.fileParseBean.setSuspendedMap(this.suspendedMap);
            this.fileParseBean.setConfigurationBean(configurationBean);
            this.finished = true;
        }
    }

    private synchronized void parseLoanExcelSheet() {

        int noOfSheets = this.fWb.getNumberOfSheets();
        XSSFSheet wSheet;
        Iterator<Row> it;
        XSSFRow row;
        NamedEntity wNamedEntity;
        boolean errorRecord;
        StringBuffer wErrorMsgBuffer;
        XSSFCell cell;
        String employeeId;
        int x;
        String loanCode;
        String amount;
        double _amount;
        double tenor;
        String _tenor;
        NamedEntity wPI;
        wUniqueMap = new HashMap<>();
        for (int i = 0; i < noOfSheets; i++) {
            if (isStop()) {
                //  is.close();
                break;
            }
              wSheet = this.fWb.getSheetAt(i);
            it = wSheet.rowIterator();
            x = 0;
            while (it.hasNext()) {
                if (isStop()) {
                    //    is.close();
                    break;
                }
                row = (XSSFRow) it.next();
                if (x == 0) {
                    x++;
                    continue;
                }

                wNamedEntity = new NamedEntity();
                 errorRecord = false;
                 wErrorMsgBuffer = new StringBuffer();
                 cell = row.getCell(0);



                if ((cell == null))
                    break;

                employeeId = String.valueOf(getValueFromCell(cell, businessCertificate.getStaffTitle()));


                if (employeeId.indexOf("Error") != -1 || employeeId.isEmpty()) {
                    break;
                }

                wNamedEntity.setStaffId(employeeId);

                //Check Active Employee Map
                if (!this.wEmployeeMap.containsKey(employeeId.trim().toUpperCase())) {
                    //Check In-Active Employee Map
                    if (this.wInactiveEmpMap.containsKey(employeeId.trim().toUpperCase())) {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Terminated.");
                        wNamedEntity.setName(wInactiveEmpMap.get(employeeId.trim().toUpperCase()).getName());
                        wErrorMsgBuffer.append("Terminated " + businessCertificate.getStaffTypeName() + ".");
                        errorRecord = true;
                    } else if (this.suspendedMap.containsKey(employeeId.trim().toUpperCase())) {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Suspended.");
                        wNamedEntity.setName(suspendedMap.get(employeeId.trim().toUpperCase()).getName());
                        wErrorMsgBuffer.append("Invalid  " + businessCertificate.getStaffTitle());
                        errorRecord = true;
                    } else {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTitle() + " not found.");
                        wNamedEntity.setName("Unknown");
                        wErrorMsgBuffer.append("Invalid " + businessCertificate.getStaffTitle());
                        errorRecord = true;
                    }

                } else {
                    wNamedEntity.setName(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getName());
                }



                cell = row.getCell(1);
                if (cell == null) {
                    break;
                }
                loanCode = getValueFromCell(cell, "Loan Code");

                if (loanCode.indexOf("Error") != -1) {
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setObjectCode(loanCode);
                    wErrorMsgBuffer.append("Invalid Loan Code");
                } else if (!this.wObjectMap.containsKey(loanCode.trim().toUpperCase())) {
                    if (!errorRecord)
                        errorRecord = true;
                    else {

                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setObjectCode(loanCode);
                    wErrorMsgBuffer.append("Invalid Loan Code");
                } else {
                    wNamedEntity.setObjectCode(loanCode);
                }
                if(wUniqueMap.containsKey(employeeId.trim().toUpperCase()+loanCode)){
                    errorRecord = true;
                    wErrorMsgBuffer.append("Duplicate "+ businessCertificate.getStaffTypeName()+" Loan ["+loanCode+"]");
                }else{
                    wUniqueMap.put(employeeId.trim().toUpperCase()+loanCode,employeeId.trim().toUpperCase()+loanCode);
                }
                _amount = 0.0D;
                cell = row.getCell(2);
                amount = getValueFromCell(cell, "Balance");
                if (amount.indexOf("Error") != -1) {
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append("<br>");
                    }

                    wNamedEntity.setLoanBalance(0.0D);
                    wErrorMsgBuffer.append("Invalid Loan Balance");
                } else {
                    try {
                        _amount = Double.parseDouble(PayrollHRUtils.removeCommas(amount));
                        wNamedEntity.setLoanBalance(_amount);
                    } catch (Exception wEx) {
                        if (!errorRecord)
                            errorRecord = true;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setLoanBalance(0.0D);
                        wErrorMsgBuffer.append("Invalid Loan Balance");
                    }
                }

                tenor = 0.0D;
                cell = row.getCell(3);
                _tenor = getValueFromCell(cell, "Tenor");

                if (amount.indexOf("Error") != -1) {
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append("<br>");
                    }

                    wNamedEntity.setTenor(0.0D);
                    wErrorMsgBuffer.append("Invalid Loan Tenor");
                } else {
                    try {
                        tenor = Double.parseDouble(PayrollHRUtils.removeCommas(_tenor));
                        wNamedEntity.setTenor(tenor);
                    } catch (Exception wEx) {
                        if (!errorRecord)
                            errorRecord = true;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setTenor(0.0D);
                        wErrorMsgBuffer.append("Invalid Loan Tenor");
                    }
                }
                if (errorRecord) {
                    wNamedEntity.setLoanBalance(_amount);
                    wNamedEntity.setDisplayErrors(wErrorMsgBuffer.toString());
                    this.wErrorList.add(wNamedEntity);
                } else {
                    wPI = new NamedEntity();
                    wPI.setLoanBalance(_amount);
                    wPI.setTenor(tenor);
                    wPI.setStaffId(employeeId.toUpperCase());
                    wPI.setObjectCode(loanCode);
                    wPI.setName(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getName());
                    wPI.setId(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getId());

                    this.wListToSave.add(wPI);
                }

            }

            this.fileParseBean.setErrorList(this.wErrorList);
            this.fileParseBean.setEmployeeMap(this.wEmployeeMap);
            this.fileParseBean.setListToSave(this.wListToSave);
            this.fileParseBean.setUniqueUploadId(this.uniqueUploadId);
            this.fileParseBean.setObjectTypeInd(2);
            this.fileParseBean.setObjectTypeClass(EmpGarnishmentType.class);
            this.fileParseBean.setObjectMap(this.wObjectMap);
            this.fileParseBean.setPayTypeMap(wPayTypeMap);
            this.fileParseBean.setInactiveEmpMap(this.wInactiveEmpMap);
            this.fileParseBean.setSuspendedMap(this.suspendedMap);
            this.finished = true;
        }
    }

    private synchronized void parseSpecialAllowanceExcelSheet()
            throws Exception {


        SpecialAllowanceType wSAT;
        XSSFSheet wSheet;
        Iterator<Row> it;
        XSSFRow row;
        NamedEntity wNamedEntity;
        boolean errorRecord,employeeIdError;
        StringBuffer wErrorMsgBuffer;
        int x;
        String employeeId;
        XSSFCell cell;
        String specialAllowanceCode;
        String amount;
        boolean hasAllowanceError;
        double _amount;
        LocalDate wStartDate,wEndDate;
        String startDate;
        String endDate;
        int noOfSheets = this.fWb.getNumberOfSheets();
        boolean typeError;
        wUniqueMap = new HashMap<>();
        for (int i = 0; i < noOfSheets; i++) {
            if (isStop()) {
                //  is.close();
                break;
            }
              wSheet = this.fWb.getSheetAt(i);
              it = wSheet.rowIterator();
              x = 0;
            while (it.hasNext()) {
                if (isStop()) {
                    // is.close();
                    break;
                }
                  row = (XSSFRow) it.next();
                if (x == 0) {
                    x++;
                    continue;
                }
                wNamedEntity = new NamedEntity();
                  errorRecord = false;
                  employeeIdError = false;
                 wErrorMsgBuffer = new StringBuffer();

                  cell = row.getCell(0);


                if ((cell == null))
                    break;

                employeeId = String.valueOf(getValueFromCell(cell, businessCertificate.getStaffTitle()));

                if (employeeId.indexOf("Error") != -1 || employeeId.isEmpty()) {
                    break;
                }


                wNamedEntity.setStaffId(employeeId);

                //Check Active Employee Map
                if (!this.wEmployeeMap.containsKey(employeeId.trim().toUpperCase())) {
                    //Check In-Active Employee Map
                    if (this.wInactiveEmpMap.containsKey(employeeId.trim().toUpperCase())) {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Terminated.");
                        wNamedEntity.setName(wInactiveEmpMap.get(employeeId.trim().toUpperCase()).getName());
                        wErrorMsgBuffer.append("Terminated " + businessCertificate.getStaffTypeName());
                        errorRecord = true;
                    } else if (this.suspendedMap.containsKey(employeeId.trim().toUpperCase())) {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTypeName() + " is Suspended.");
                        wNamedEntity.setName(suspendedMap.get(employeeId.trim().toUpperCase()).getName());
                        wErrorMsgBuffer.append("Invalid  " + businessCertificate.getStaffTitle());
                        errorRecord = true;
                    } else {
                        wNamedEntity.setDisplayErrors(businessCertificate.getStaffTitle() + " not found.");
                        wNamedEntity.setName("Unknown");
                        wErrorMsgBuffer.append("Invalid " + businessCertificate.getStaffTitle());
                        errorRecord = true;
                    }

                } else {
                    wNamedEntity.setName(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getName());
                }


                  hasAllowanceError = false;

                cell = row.getCell(1);
                if (cell == null) {
                    break;
                }
                specialAllowanceCode = getValueFromCell(cell, "Allowance Code");

                if (specialAllowanceCode.indexOf("Error") != -1) {
                    if (!errorRecord) {
                        errorRecord = true;

                    } else {
                        wErrorMsgBuffer.append(" | ");
                    }


                    wNamedEntity.setObjectCode(specialAllowanceCode);
                    wErrorMsgBuffer.append("Invalid Allowance Code");
                    hasAllowanceError = true;
                } else if (!this.wObjectMap.containsKey(specialAllowanceCode.trim().toUpperCase())) {
                    if (!errorRecord)
                        errorRecord = !errorRecord;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setObjectCode(specialAllowanceCode);
                    wErrorMsgBuffer.append("Invalid Allowance Code");
                    hasAllowanceError = true;
                } else {
                    if (employeeIdError) {
                        wNamedEntity.setObjectCode(specialAllowanceCode);
                    }
                    if(wUniqueMap.containsKey(employeeId.trim().toUpperCase()+specialAllowanceCode)){

                    }else{
                        errorRecord = true;
                        wErrorMsgBuffer.append("Duplicate "+ businessCertificate.getStaffTypeName()+" Spec. Allow ["+specialAllowanceCode+"]");
                        wUniqueMap.put(employeeId.trim().toUpperCase()+specialAllowanceCode,employeeId.trim().toUpperCase()+specialAllowanceCode);
                    }

                }
                wSAT = null;
                typeError = false;
                try{
                    wSAT = (SpecialAllowanceType) this.wObjectMap.get(specialAllowanceCode.trim().toUpperCase());
                    wNamedEntity.setPaycheckTypeInstId(wSAT.getPayTypes().getId());
                    wNamedEntity.setPayTypeName(wSAT.getPayTypes().getName());
                }catch(Exception wEx){
                    typeError = true;
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setPayTypeName("Unknown");
                    wErrorMsgBuffer.append("Pay Type Can Not Be Determined.");
                }


                _amount = 0.0D;
                cell = row.getCell(2);
                amount = getValueFromCell(cell, "Amount");
                if (amount.indexOf("Error") != -1) {
                    if (!errorRecord)
                        errorRecord = true;
                    else {
                        wErrorMsgBuffer.append(" | ");
                    }

                    wNamedEntity.setAllowanceAmount(0.0D);
                    wErrorMsgBuffer.append("Invalid Amount");
                } else {
                    try {
                        _amount = Double.parseDouble(PayrollHRUtils.removeCommas(amount));
                        //if we get here...amount is good.
                        if(!typeError) {
                            if (!hasAllowanceError) {

                                if (wSAT.getPayTypes().isUsingPercentage()) {
                                    if (_amount > configurationBean.getMaxSpecAllowValue()) {
                                        wNamedEntity.setAllowanceAmount(_amount);
                                        wErrorMsgBuffer.append("Amount must be less than " + configurationBean.getMaxSpecAllowValue());
                                        errorRecord = true;
                                    }
                                }
                            }
                        }

                    } catch (Exception wEx) {
                        _amount = 0.0D;
                        if (!errorRecord)
                            errorRecord = true;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setAllowanceAmount(_amount);
                        wErrorMsgBuffer.append("Invalid Amount");
                    }
                }

                cell = row.getCell(3);

                if (cell == null) {
                    startDate = "";
                } else {
                    startDate = getValueFromCell(cell, "Start Date (DD-MMM-YYYY)");
                }


                wStartDate = null;
                if ((startDate.indexOf("Error") != -1) || (startDate.equals(""))) {
                    wStartDate = PayrollBeanUtils.makeNextPayPeriodStart(this.runMonth, this.runYear);

                } else if (startDate != null) {
                    try {
                        wStartDate = PayrollBeanUtils.setDateFromStringExcel(startDate);
                    } catch (Exception wEx) {
                        wStartDate = PayrollBeanUtils.makeNextPayPeriodStart(this.runMonth, this.runYear);
                    }

                }


                cell = row.getCell(4);
                if (cell == null)
                    endDate = "";
                else {
                    endDate = getValueFromCell(cell, "End Date (DD-MMM-YYYY)");
                }

                wEndDate = null;
                if ((endDate.indexOf("Error") != -1) || (endDate.equals(""))) {

                    if (this.wObjectMap.containsKey(specialAllowanceCode.trim().toUpperCase())) {
                        SpecialAllowanceType sAT = (SpecialAllowanceType) this.wObjectMap.get(specialAllowanceCode.trim().toUpperCase());
                        if (sAT.isEndDateRequired()) {
                            if (!errorRecord)
                                errorRecord = true;
                            else {
                                wErrorMsgBuffer.append(" | ");
                            }

                            wNamedEntity.setEndDateString(endDate);
                            wErrorMsgBuffer.append("End Date is Required");
                        } else {
                            wEndDate = null;
                        }
                    }

                } else {
                    try {
                        wEndDate = PayrollBeanUtils.setDateFromStringExcel(endDate);
                    } catch (Exception wEx) {
                        wEndDate = null;
                        if (!errorRecord)
                            errorRecord = true;
                        else {
                            wErrorMsgBuffer.append(" | ");
                        }

                        wNamedEntity.setEndDateString(endDate);
                        wErrorMsgBuffer.append("Invalid End Date");
                    }

                }


                if (errorRecord) {
                    wNamedEntity.setAllowanceStartDate(wStartDate);
                    wNamedEntity.setAllowanceEndDate(wEndDate);
                    wNamedEntity.setAllowanceAmount(_amount);
                    wNamedEntity.setObjectCode(specialAllowanceCode);

                    wNamedEntity.setDisplayErrors(wErrorMsgBuffer.toString());
                    this.wErrorList.add(wNamedEntity);
                } else {
                    NamedEntity wPI = new NamedEntity();
                    wPI.setId(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getId());
                    wPI.setName(this.wEmployeeMap.get(employeeId.trim().toUpperCase()).getName());
                    wPI.setAllowanceAmount(_amount);
                    wPI.setStaffId(employeeId.toUpperCase());
                    wPI.setObjectCode(specialAllowanceCode);
                    wPI.setAllowanceStartDate(wStartDate);
                    wPI.setPayTypeName(wNamedEntity.getPayTypeName());
                    wPI.setPaycheckTypeInstId(wNamedEntity.getPaycheckTypeInstId());
                    wPI.setAllowanceEndDate(wEndDate);
                    wPI.setStartDateStr(PayrollHRUtils.getFullDateFormat().format(wStartDate));
                    wPI.setEndDateStr(PayrollHRUtils.getFullDateFormat().format(wEndDate));

                    // wPI.setPayTypeName(payType);

                    this.wListToSave.add(wPI);
                }

            }

            this.fileParseBean.setErrorList(this.wErrorList);
            this.fileParseBean.setEmployeeMap(this.wEmployeeMap);
            this.fileParseBean.setListToSave(this.wListToSave);
            this.fileParseBean.setUniqueUploadId(this.uniqueUploadId);
            this.fileParseBean.setObjectTypeInd(5);
            this.fileParseBean.setObjectTypeClass(SpecialAllowanceType.class);
            this.fileParseBean.setObjectMap(this.wObjectMap);
            this.fileParseBean.setPayTypeMap(wPayTypeMap);
            this.fileParseBean.setInactiveEmpMap(this.wInactiveEmpMap);
            this.fileParseBean.setSuspendedMap(this.suspendedMap);
            this.finished = true;
        }

    }

    private String getValueFromCell(XSSFCell pCell, String pFieldName) {
        String wRetVal = null;
        switch (pCell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(pCell)) {
                    if (pCell.getDateCellValue() == null) break;
                    DataFormatter wFormatter = new DataFormatter(Locale.UK);
                    wRetVal = wFormatter.formatCellValue(pCell);
                } else if (pCell.getDateCellValue() != null) {
                    wRetVal = Double.toString(pCell.getNumericCellValue());
                } else {
                    wRetVal = "0";
                }

                break;
            case STRING:
                if (pCell.getRichStringCellValue() != null) {
                    wRetVal = pCell.getRichStringCellValue().getString();
                } else {
                    log.error("Invalid value for '" + pFieldName + "' in Excel File " + this.fileName);
                    wRetVal = "Error - Invalid value for '" + pFieldName + "' in Excel File " + this.fileName;
                }
                break;
            default:

                log.error("Invalid value for '" + pFieldName + "' in Excel File " + this.fileName);
                wRetVal = "Error - Invalid value for '" + pFieldName + "' in Excel File " + this.fileName;
                break;

        }

        return wRetVal;
    }

    private String getValueFromCell(XSSFCell pCell) {
        String wRetVal = null;
        if(pCell == null)
            return "Error Field";
        switch (pCell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(pCell)) {
                    if (pCell.getDateCellValue() == null) break;
                    DataFormatter wFormatter = new DataFormatter(Locale.UK);
                    wRetVal = wFormatter.formatCellValue(pCell);
                } else if (pCell.getDateCellValue() != null) {
                    wRetVal = Double.toString(pCell.getNumericCellValue());
                } else {
                    wRetVal = "0";
                }

                break;
            case STRING:
                if (pCell.getRichStringCellValue() != null) {
                    wRetVal = pCell.getRichStringCellValue().getString();
                } else {
                    log.error("Error Field");
                    wRetVal = "Error - Invalid Column Value";
                }
                break;
            default:

                log.error("Error Field");
                wRetVal = "Error - Invalid Column Value";
                break;

        }

        return wRetVal;
    }
    public boolean isFinished() {
        return this.finished;
    }

   // public String getConfirmationNumber() {
     //   return this.confirmationNumber;
  //  }

    public boolean isStop() {
        return this.fStop;
    }

    public void setStop(boolean pStop) {
        this.fStop = pStop;
    }

    public Integer getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Integer pTransactionId) {
        this.transactionId = pTransactionId;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public void setFailed(boolean pFailed) {
        this.failed = pFailed;
    }

    public HashMap<String, NamedEntity> getwEmployeeMap() {
        return this.wEmployeeMap;
    }

    public HashMap<String, ?> getwObjectMap() {
        return this.wObjectMap;
    }

    public Vector<NamedEntity> getwListToSave() {
        return this.wListToSave;
    }

    public Vector<NamedEntity> getwErrorList() {
        return this.wErrorList;
    }

    public String getUniqueUploadId() {
        return this.uniqueUploadId;
    }

    public FileParseBean getFileParseBean() {
        return this.fileParseBean;
    }

    private String camelCaseIt(String fieldName) {
        return String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
    }
}