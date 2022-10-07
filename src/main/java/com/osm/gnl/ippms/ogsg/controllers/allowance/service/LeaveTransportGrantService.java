package com.osm.gnl.ippms.ogsg.controllers.allowance.service;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.LeaveTransportGrantHolder;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.engine.CalculatePayPerABMPSimulator;
import com.osm.gnl.ippms.ogsg.exception.ApplicationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.MdapLtgAppIndBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import com.osm.gnl.ippms.ogsg.validators.allowance.LtgValidator;
import org.hibernate.query.Query;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.*;

public abstract class LeaveTransportGrantService {
    public static void setupForm(Model model, GenericService genericService,
             LeaveTransportGrantHolder wLTGH, BusinessCertificate businessCertificate) {
        wLTGH.setName("No Selection");
        wLTGH.setShowSelectionList(SHOW_ROW);
        wLTGH.setShowMonthList(HIDE_ROW);
        wLTGH.setShowNameRow(HIDE_ROW);
        wLTGH.setShowCloseButton(true);
        wLTGH.setForPayrollRun(false);
        wLTGH.setForSimulation(false);
        wLTGH.setMdaInfo(new MdaInfo());
        wLTGH.setUnAssignedObjects(genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),"name"));
        wLTGH.setMdaList(wLTGH.getUnAssignedObjects());
        wLTGH.setTotalAssigned(0);

        model.addAttribute("ltgMiniBean", wLTGH);
    }


    public static void setupForm(String pPid, Model model, GenericService genericService,
                                   LeaveTransportGrantHolder wLTGH,BusinessCertificate businessCertificate) {
        wLTGH.setName("No Selection");
        wLTGH.setShowSelectionList(HIDE_ROW);
        wLTGH.setShowMonthList(HIDE_ROW);
        wLTGH.setMdaInfo(new MdaInfo());
        wLTGH.setMdaList(genericService.loadAllObjectsWithSingleCondition(MdaInfo.class, CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()),"name"));

        wLTGH = removeAppliedLtgs(wLTGH, genericService);

        model.addAttribute("ltgMiniBean", wLTGH);
    }


    private static LeaveTransportGrantHolder removeAppliedLtgs(LeaveTransportGrantHolder pLTGH,
                                                               GenericService genericService) {

        List<MdapLtgAppIndBean> wLMB = genericService.loadAllObjectsWithSingleCondition(MdapLtgAppIndBean.class,
                CustomPredicate.procurePredicate("ltgApplyYear", LocalDate.now().getYear()), null);

        HashMap<Long,Long> wFilterMap = makeHashMapForMdapLtgApplied(wLMB);

        List<MdaInfo> wSaveList = new ArrayList<MdaInfo>();

        for (MdaInfo a : pLTGH.getMdaList()) {
            if (wFilterMap.containsKey(a.getId()))
                continue;
            wSaveList.add(a);
        }
        return pLTGH;

    }

    public static void setupForm(Long pPid, Model model, LeaveTransportGrantHolder wLTGH, HRService hrService, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {

        MdaInfo wMdaInfo = hrService.getGenericService().loadObjectById(MdaInfo.class, pPid);
        wLTGH.setMdaInfo(wMdaInfo);
        wLTGH.setShowSelectionList(SHOW_ROW);
        wLTGH.setShowMonthList(HIDE_ROW);
        wLTGH.setShowNameRow(HIDE_ROW);
        wLTGH.setShowCloseButton(true);
        wLTGH.setForPayrollRun(false);
        wLTGH.setForSimulation(false);
        wLTGH.setName(wMdaInfo.getName());
        wLTGH.setUnAssignedObjects(wLTGH.getMdaList());
        wLTGH = setMonthList(wLTGH, bc.getBusinessClientInstId(), hrService.getGenericService());
        wLTGH.setMonthId(-1);
        Collections.sort(wLTGH.getUnAssignedObjects());

        wLTGH = addLTgDetails(wLTGH,wMdaInfo,hrService);

        model.addAttribute("ltgMiniBean", wLTGH);
        model.addAttribute("roleBean", bc);
    }

    public static LeaveTransportGrantHolder setupForm(Long pPid, String pAct, Model model, BusinessCertificate bc, LeaveTransportGrantHolder wLTGH) throws Exception {
        boolean found = false;
        for (AbmpBean a : wLTGH.getAssignedMBAPList()) {
            if (a.getId().equals( pPid)) {
                found = !found;
                break;
            }
        }
        if (!found) {
            model.addAttribute("ltgMiniBean", wLTGH);
            model.addAttribute("roleBean", bc);
            return wLTGH;
        }

        wLTGH.setShowSelectionList(SHOW_ROW);
        if (wLTGH.isShowConfirmation()) {
            wLTGH.setShowMonthList(SHOW_ROW);
            wLTGH.setShowNameRow(SHOW_ROW);

        } else {
            wLTGH.setShowMonthList(HIDE_ROW);
            wLTGH.setShowNameRow(HIDE_ROW);
        }
        wLTGH.setShowCloseButton(false);


        AbmpBean newBean = null;
        for (AbmpBean a : wLTGH.getAssignedMBAPList()) {
            if ( a.getId().equals(pPid) )
            {
                newBean  = a;
                break;
            }


        }


        wLTGH.getMdaList().add(newBean.getMdaInfo());
        wLTGH.setUnAssignedObjects(wLTGH.getMdaList());


        Collections.sort(wLTGH.getUnAssignedObjects());


        wLTGH.getAssignedMBAPList().remove(newBean);

        wLTGH.setTotalAssigned(wLTGH.getAssignedMBAPList().size());

        Collections.sort(wLTGH.getAssignedMBAPList());

        wLTGH.setTotalBasicSalary(wLTGH.getTotalBasicSalary() - newBean.getBasicSalary());
        wLTGH.setTotalLtgCost(wLTGH.getTotalLtgCost() - newBean.getLtgCost());
        wLTGH.setTotalNetIncrease(wLTGH.getTotalNetIncrease() - newBean.getNetIncrease());
        wLTGH.setTotalNoOfEmp(wLTGH.getTotalNoOfEmp() - newBean.getNoOfEmp());

        model.addAttribute("ltgMiniBean", wLTGH);
        model.addAttribute("roleBean", bc);

        return wLTGH;
    }

    public static LeaveTransportGrantHolder setMonthList(LeaveTransportGrantHolder pLTGH, Long pBusClientId, GenericService genericService)
            throws InstantiationException, IllegalAccessException {
        PayrollFlag wPF = genericService.loadObjectWithSingleCondition(PayrollFlag.class, CustomPredicate.procurePredicate("businessClientId",
                pBusClientId));

        List<NamedEntityBean> wRetList = new ArrayList<NamedEntityBean>();
        int wStartMonth = 0;
        int wStartYear = 0;
        if(wPF.isNewEntity()) {
            wStartMonth = LocalDate.now().getMonth().getValue();
            wStartYear = LocalDate.now().getYear();
        }else {
            wStartMonth = wPF.getApprovedMonthInd();
            wStartYear = wPF.getApprovedYearInd();
            if(wStartMonth == 12) {
                wStartMonth = 1;
                wStartYear += 1;
            }else{
                wStartMonth += 1;
            }
        }
        for(int wMonth = wStartMonth; wMonth <= 12; wMonth++) {
            NamedEntityBean wB = new NamedEntityBean();
            wB.setRunMonth(wMonth);
            wB.setName(Month.of(wMonth).getDisplayName(TextStyle.FULL,Locale.UK));
            wB.setRunYear(wStartYear);
            wRetList.add(wB);
        }
        pLTGH.setMonthList(wRetList);
        pLTGH.setRunYear(wStartYear);

        return pLTGH;

    }

    private static LeaveTransportGrantHolder addLTgDetails(LeaveTransportGrantHolder wLTGH, MdaInfo wMdaInfo,
                                                    HRService hrService) {
        AbmpBean wRetVal = hrService.createMBAPMiniBeanForLTGAllowance(wMdaInfo.getId(),
                wMdaInfo.getMdaType().getBusinessClientId(), false);

        //Now Remove the MDA from the Unassigned List
        for(MdaInfo m : wLTGH.getUnAssignedObjects()) {
            if(m.getId().equals(wRetVal.getId())){
                wLTGH.getUnAssignedObjects().remove(m);
                break;
            }
        }
        //Now Add AbmpBean to the Assigned List.
        wLTGH.getAssignedMBAPList().add(wRetVal);
        wLTGH.setTotalBasicSalary(wLTGH.getTotalBasicSalary() + wRetVal.getBasicSalary());
        wLTGH.setTotalLtgCost(wLTGH.getTotalLtgCost() + wRetVal.getLtgCost());
        wLTGH.setTotalNetIncrease(wLTGH.getTotalNetIncrease() + wRetVal.getNetIncrease());
        wLTGH.setTotalNoOfEmp(wLTGH.getTotalNoOfEmp() + wRetVal.getNoOfEmp());
        wLTGH.setTotalAssigned(wLTGH.getAssignedMBAPList().size());
        return wLTGH;
    }

    private static HashMap<Long, Long> makeHashMapForMdapLtgApplied(List<MdapLtgAppIndBean> pLMB) {
        HashMap<Long, Long> wRetMap = new HashMap<Long, Long>();
        for (MdapLtgAppIndBean a : pLMB)
        {
            Long wKey = a.getId();

            wRetMap.put(wKey, wKey);
        }

        return wRetMap;
    }

    public static void setModelData(Model model, BindingResult result, LeaveTransportGrantHolder pHMB,
                                    BusinessCertificate bc) {
        model.addAttribute(DISPLAY_ERRORS, BLOCK);
        model.addAttribute("status", result);
        model.addAttribute("ltgMiniBean", pHMB);
        model.addAttribute("roleBean", bc);
    }

    public static void processButtonRequestParamSimulate(Model model, BindingResult result, LeaveTransportGrantHolder pHMB,
             BusinessCertificate bc, GenericService genericService, LtgValidator validator) throws IllegalAccessException, InstantiationException {
        pHMB.setMessageString("Select Month to run simulation for");
        pHMB.setShowMonthList(SHOW_ROW);
        pHMB.setShowNameRow(SHOW_ROW);
        pHMB.setForSimulation(true);
        pHMB.setForPayrollRun(false);
        if (!pHMB.isWarningIssued()) {
            pHMB.setWarningIssued(true);
            pHMB.setShowConfirmation(true);
            if (pHMB.getTotalAssigned() == 0) {
                result.rejectValue("", "Invalid.Value", "You have not chosen any Agency. Press 'Confirm' to continue or 'Cancel' to abort  ");
            }
            else {
                result.rejectValue("", "Invalid.Value", "You have chosen to run a Payroll Simulation. Press 'Confirm' to continue or 'Cancel' to abort  ");
            }

            model.addAttribute(DISPLAY_ERRORS, BLOCK);

            pHMB = setMonthList(pHMB, bc.getBusinessClientInstId(), genericService);

            LeaveTransportGrantService.setModelData(model, result, pHMB, bc);
            return;
        }
        validator.validate(pHMB, result);
        if(result.hasErrors()) {
            model.addAttribute(DISPLAY_ERRORS, BLOCK);
            model.addAttribute("status", result);
            model.addAttribute("ltgMiniBean", pHMB);
            model.addAttribute("roleBean", bc);
        }
    }

    public static LtgMasterBean createLtgMasterBean(LeaveTransportGrantHolder pHMB, BusinessCertificate bc, HRService hrService) {
        LtgMasterBean wLMB = new LtgMasterBean();
        wLMB.setSimulationMonth(pHMB.getMonthId());
        wLMB.setSimulationYear(pHMB.getRunYear());
        wLMB.setLastModBy(bc.getUserName());
        wLMB.setLastModTs(LocalDate.now());
        wLMB.setApplicationIndicator(0);
        wLMB.setName(pHMB.getLtgInstructionName());
        wLMB.setBusinessClientId(bc.getBusinessClientInstId());
        hrService.getGenericService().saveObject(wLMB);
        hrService.storeLtgDetails(pHMB.getAssignedMBAPList(), bc.getUserName(), wLMB.getId());
        return wLMB;
    }

    public static CalculatePayPerABMPSimulator processGrantHolderOnSimulationConfirm(LeaveTransportGrantHolder pHMB,
                                                                                     LtgMasterBean wLMB, BusinessCertificate bc, HRService hrService) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        LocalDate wPayPeriodStart = PayrollBeanUtils.getDateFromMonthAndYear(wLMB.getSimulationMonth(), wLMB.getSimulationYear(), false);
        LocalDate wPayPeriodEnd = PayrollBeanUtils.getDateFromMonthAndYear(wLMB.getSimulationMonth(), wLMB.getSimulationYear(), true);

        List<SalaryInfo> wList = hrService.getGenericService().loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), null);
        Map<Long,SalaryInfo> wMap = PayrollBeanUtils.breakSalaryInfo(wList);
        List<AbstractDeductionEntity> wEmpListToSplit = hrService
                .loadToBePaidEmployeeDeductions(wPayPeriodStart, wPayPeriodEnd, bc);
        List<AbstractGarnishmentEntity> wGarnListToSplit = hrService.loadToBePaidEmployeeGarnishments(wPayPeriodEnd, bc);
        List<AbstractSpecialAllowanceEntity> wAllowListToSplit = hrService
                .loadToBePaidEmployeeSpecialAllowances(wPayPeriodStart, wPayPeriodEnd, bc);

        Map<Long, SuspensionLog> wPartPayments = hrService.loadToBePaidSuspendedEmployees(bc);
        HashMap<Long, List<AbstractDeductionEntity>> fEmpDedMap = EntityUtils.breakUpDeductionList(wEmpListToSplit,wPayPeriodStart, wPayPeriodEnd);
        HashMap<Long, List<AbstractGarnishmentEntity>> fGarnMap = EntityUtils.breakUpGarnishmentList(wGarnListToSplit);
        HashMap<Long, List<AbstractSpecialAllowanceEntity>> fAllowanceMap = EntityUtils.breakUpAllowanceList(wAllowListToSplit, wPayPeriodStart, wPayPeriodEnd);

        ConfigurationBean configurationBean = hrService.getGenericService().loadObjectWithSingleCondition(ConfigurationBean.class,CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()));
        CalculatePayPerABMPSimulator wCalcPayPerEmp = new CalculatePayPerABMPSimulator();
        wCalcPayPerEmp.setGenericService(hrService.getGenericService());
        wCalcPayPerEmp.setBusinessCertificate(bc);
        wCalcPayPerEmp.setSalaryInfoMap(wMap);
        wCalcPayPerEmp.setfEmployeeDeductions(fEmpDedMap);
        wCalcPayPerEmp.setfSpecialAllowances(fAllowanceMap);
        wCalcPayPerEmp.setfEmployeeGarnishments(fGarnMap);
        wCalcPayPerEmp.setPartPaymentMap(wPartPayments);
        wCalcPayPerEmp.setThirtyFiveYearsAgo((PayrollBeanUtils.calculate35YearsAgo(wPayPeriodEnd,configurationBean)));
        wCalcPayPerEmp.setSixtyYearsAgo(PayrollBeanUtils.calculate60yrsAgo(wPayPeriodEnd, bc.isPensioner(),configurationBean));
        wCalcPayPerEmp.setPayPeriodEnd(wPayPeriodEnd);
        return wCalcPayPerEmp;
    }

    public static void processGrantHolderOnPayrollConfirm(LeaveTransportGrantHolder pHMB,
                                                          BusinessCertificate bc, HRService hrService) {
        LtgMasterBean wLMB = new LtgMasterBean();
        wLMB.setSimulationMonth(pHMB.getMonthId());
        wLMB.setSimulationYear(Calendar.getInstance().get(1));
        wLMB.setLastModBy(bc.getUserName());
        wLMB.setLastModTs(LocalDate.now());
        wLMB.setApplicationIndicator(1);
        wLMB.setName(pHMB.getLtgInstructionName());
        hrService.getGenericService().saveObject(wLMB);
        hrService.storeLtgDetails(pHMB.getAssignedMBAPList(), bc.getUserName(), wLMB.getId());
    }

    public static void loadLTGFormData(Long pPid, Model model, HttpServletRequest request,
                                          GenericService genericService, BusinessCertificate bc) throws IllegalAccessException, InstantiationException {
        int pageNumber = ServletRequestUtils.getIntParameter(request, "page", 1);
        String sortOrder = ServletRequestUtils.getStringParameter(request, "dir", "asc");
        String sortCriterion = ServletRequestUtils.getStringParameter(request, "sort", null);

        LtgMasterBean wLMB = genericService.loadObjectById(LtgMasterBean.class, pPid);

        if (wLMB.isNewEntity()) {
            throw new ApplicationException("Invalid argument provided for Leave Transport Grant Data Access Object");
        }

        List<AbmpBean> wABList = loadObjectsToApplyLtgByParentIdAndObjectInd(wLMB.getId(), genericService, bc);


        Comparator<AbmpBean> comparator = Comparator.comparing(AbmpBean::getName);
        Collections.sort(wABList,comparator);

        int wNoOfElements = wABList.size();

        PaginatedBean wPHDB = new PaginatedBean(wABList, pageNumber, wNoOfElements, wNoOfElements, sortCriterion, sortOrder);

        wPHDB.setId(pPid);

        for (AbmpBean a : wABList) {
            wPHDB.setNoOfEmployees(wPHDB.getNoOfEmployees() + a.getNoOfEmp());
            wPHDB.setTotalBasicSalary(wPHDB.getTotalBasicSalary() + a.getBasicSalary());
            wPHDB.setTotBasicSalaryPlusLtg(wPHDB.getTotBasicSalaryPlusLtg() + a.getLtgCost());
            wPHDB.setNetIncrease(wPHDB.getNetIncrease() + a.getNetIncrease());
        }

        wPHDB.setCreatedBy(getUserNamesByUserName(wLMB.getLastModBy(), genericService));
        wPHDB.setCreatedDate(wLMB.getLastModTs());

        wPHDB.setShowForConfirm(false);
        model.addAttribute("miniBean", wPHDB);
    }

    public static List<AbmpBean> loadObjectsToApplyLtgByParentIdAndObjectInd(Long pParentId, GenericService genericService, BusinessCertificate businessCertificate)
    {
        Objects.requireNonNull(pParentId);
        String wHql = "";

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        List<AbmpBean> wRetList = new ArrayList<AbmpBean>();


        wHql = "select a.name,sum(s.monthlyBasicSalary),sum((s.monthlyBasicSalary * 1.2)),count(e.id),a.id from Employee e ,"
                + "SalaryInfo s, MdaDeptMap adm, MdaInfo a, AbmpBean d where e.mdaDeptMap.id = adm.id and "
                + "adm.mdaInfo.id = a.id and e.salaryInfo.id = s.id and e.statusIndicator = 0 "
                + "and d.ltgMasterBean.id = :pPid and e.businessClientId = :pBizIdVar order by a.name group by a.name,a.id";


        Query query = genericService.getCurrentSession().createQuery(wHql);
        query.setParameter("pPid", pParentId );
        query.setParameter("pBizIdVar", businessCertificate.getBusinessClientInstId() );

        wRetVal = (ArrayList<Object[]>)query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                AbmpBean a = new AbmpBean();
                a.setName((String)o[0]);
                a.setBasicSalary(((Double)o[1]));
                a.setLtgCost(((Double)o[2]));
                a.setNoOfEmp(((Long)o[3]).intValue());
                a.setMdaInfo(new MdaInfo((Long)o[4]));
                a.setLtgMasterBean(new LtgMasterBean(pParentId));

                wRetList.add(a);
            }

        }

        return wRetList;
    }

    private static String getUserNamesByUserName(String pUserName, GenericService genericService) {
        User user = genericService.getSingleObjectFromBuilder(
                new PredicateBuilder().addPredicate(new
                        CustomPredicate("username", pUserName, Operation.STRING_EQUALS)), User.class);
        return user.getActualUserName();
    }

    public static void processExistingLTGAddParamButton(PaginatedBean pBE, Model model, BindingResult result) {
        if (!pBE.isAddWarningIssued()) {
            pBE.setAddWarningIssued(true);
            pBE.setShowForConfirm(true);
        }
        result.rejectValue("", "Warning", "You have selected to Add to the existing Leave Transport Grant Instruction. Click 'Confirm' button to continue or 'Cancel' to abort.");

        ((PaginatedBean)result.getTarget()).setDisplayErrors("block");
        model.addAttribute("status", result);
        model.addAttribute("miniBean", pBE);
    }

    public static void processExistingLTGConfirmParamButton(LeaveTransportGrantHolder wLTGH, PaginatedBean pBE,
                    BusinessCertificate bc, GenericService genericService) throws IllegalAccessException, InstantiationException {

        if (pBE.isAddWarningIssued())
        {
            List<AbmpBean> wListToSave = new ArrayList<AbmpBean>();

            List<AbmpBean> wListToAdd = wLTGH.getAssignedMBAPList();

            for (AbmpBean a : wListToAdd) {
                boolean found = false;
                for (AbmpBean b : (List<AbmpBean>)pBE.getList())
                {
                    if (a.getId().equals(b.getId()))
                    {
                        found = !found;
                        break;
                    }
                }

                if (!found)
                {
                    a.setLastModTs(LocalDate.now());
                    wListToSave.add(a);
                }

            }

            genericService.saveObject(wLTGH);
            storeLtgDetails(wListToSave, bc.getUserName(), wLTGH.getId(), genericService);
        }
        else
        {
            LtgMasterBean wOldLMB = genericService.loadObjectById(LtgMasterBean.class, pBE.getId());
            LtgMasterBean wLMB = new LtgMasterBean();
            wLMB.setSimulationMonth(wOldLMB.getSimulationMonth());
            wLMB.setSimulationYear(wOldLMB.getSimulationYear());
            wLMB.setApplicationIndicator(1);
            wLMB.setLastModBy(bc.getUserName());
            wLMB.setLastModTs(LocalDate.now());
            wLMB.setBusinessClientId(bc.getBusinessClientInstId());
            genericService.deleteObject(wOldLMB);
            genericService.saveObject(wLMB);
            storeLtgDetails(wLTGH.getAssignedMBAPList(), bc.getUserName(), wLMB.getId(), genericService);
        }
    }

    private static void storeLtgDetails(List<AbmpBean> pAssignedList, String pUserName, Long pParentInstId,
                                        GenericService genericService) {
        for (AbmpBean a : pAssignedList) {
            a.setId(null);
            a.setLastModBy(pUserName);
            a.setLastModTs(LocalDate.now());
            a.setLtgMasterBean(new LtgMasterBean(pParentInstId));
            genericService.saveObject(a);
        }
    }
}
