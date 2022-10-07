package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.employee.beans.NewPensionerBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;

import java.util.*;

public abstract class PensionerGenerator {

    public PensionerGenerator() {
    }


   
    public static NewPensionerBean getEmployeeInfoDetails(GenericService genericService, PayrollService payrollService,
                                                          Long pEmpId, Long pBusinessClientId, PensionService pensionService, BusinessCertificate businessCertificate) throws Exception {

        NewPensionerBean wNewPensionerBean = new NewPensionerBean();
        wNewPensionerBean.setPensioner(new Pensioner());
        wNewPensionerBean.setParentBusinessClientId(pBusinessClientId);

        //First Load His Employee Information....
        Employee wEmp = genericService.loadObjectUsingRestriction(Employee.class,
                Arrays.asList(CustomPredicate.procurePredicate("id", pEmpId), CustomPredicate.procurePredicate("businessClientId", pBusinessClientId)));



        wNewPensionerBean =  setEmployeeInfoDropDownValues(wNewPensionerBean, wEmp,genericService);
        //Now load the HiringInfo Details as well...
        HiringInfo wHI = genericService.loadObjectUsingRestriction(HiringInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("businessClientId", pBusinessClientId)));

        HiringInfo hiringInfo = new HiringInfo();
       // hiringInfo.setServiceHireInfo(new HiringInfo(wHI.getId()));
        hiringInfo.setServiceHireInfo(wHI.getId());
        wNewPensionerBean.setHiringInfo(hiringInfo);

        wNewPensionerBean = setHiringInfoValues(wNewPensionerBean,wHI);

        //Next We need to Get payment Information....
        PaymentMethodInfo wPMI = genericService.loadObjectUsingRestriction(PaymentMethodInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("employee.id", pEmpId), CustomPredicate.procurePredicate("businessClientId", pBusinessClientId)));

        if (wPMI.isNewEntity())
            throw new EpmAuthenticationException("Payment Information Not Found for " + wEmp.getDisplayNameWivTitlePrefixed());
        Long _id = wPMI.getId();
        PaymentMethodInfo paymentMethodInfo = wPMI;
        paymentMethodInfo.setId(null);
        paymentMethodInfo.setServicePaymentMethodInfo(new PaymentMethodInfo(_id));

        if (wPMI.getPaymentMethodTypes().getPaymentMethodCode().equalsIgnoreCase("DD")) {
            //Get the Bank Branch By its Branch Code...
            wNewPensionerBean.setBankList(genericService.loadAllObjectsWithSingleCondition(BankInfo.class, CustomPredicate.procurePredicate("selectableInd", 0), "name"));

            wNewPensionerBean.setBankId(wPMI.getBankBranches().getBankInfo().getId());
            wNewPensionerBean.setBankBranchId(wPMI.getBankBranches().getId());
            wNewPensionerBean.setBankBranchList(genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id",wNewPensionerBean.getBankId()),"name"));

        }
        wNewPensionerBean.setPaymentMethodInfo(paymentMethodInfo);

        wNewPensionerBean = createSalaryInformation(wNewPensionerBean, wEmp.getSalaryInfo(), genericService, payrollService);

        //Now -- Add Next Of Kin Information....
        List<NextOfKin> wNextOfKinList = genericService.loadAllObjectsUsingRestrictions(NextOfKin.class, Arrays.asList(CustomPredicate.procurePredicate("employee.id", wEmp.getId()),
                CustomPredicate.procurePredicate("businessClientId", wEmp.getBusinessClientId())), null);

        if (!wNextOfKinList.isEmpty()) {
            wNewPensionerBean.setHasNextOfKin(true);
            Collections.sort(wNextOfKinList, Comparator.comparing(NextOfKin::getLastName).thenComparing(NextOfKin::getFirstName).thenComparing(NextOfKin::getMiddleName));
            NextOfKin wNOK = wNextOfKinList.get(0);
            wNewPensionerBean.setNextOfKin(wNOK);
        } else {
            wNewPensionerBean.setNoPensionerMsg("No Next Of Kin Information - Add later?");
        }

       // wNewPensionerBean =  setPensionAndGratuity(wNewPensionerBean,wHI);
        wNewPensionerBean = PayrollGratuityUtil.generateGatuityAndPension(wNewPensionerBean,pensionService,businessCertificate);

        //Now Set PFA Information if it exists...

        if (wHI.getPfaInfo() == null || wHI.getPfaInfo().isNewEntity()) {
            wNewPensionerBean.setPfaId(0L);//Technically should Never ever happen.
        } else {
            wNewPensionerBean.setPfaId(wHI.getPfaInfo().getId());
        }
        wNewPensionerBean.setPfaInfoList(genericService.loadAllObjectsWithoutRestrictions(PfaInfo.class, "name"));
        //-- Set the Title Information...
        wNewPensionerBean.setTitleId(wEmp.getTitle().getId());

        wNewPensionerBean.setEpmTitleList(genericService.loadAllObjectsWithoutRestrictions(Title.class, "name"));
        return wNewPensionerBean;
    }

    private static NewPensionerBean setHiringInfoValues(NewPensionerBean wNewPensionerBean, HiringInfo wHI) {

        wNewPensionerBean.getHiringInfo().setHireDate(wHI.getHireDate());
        wNewPensionerBean.getHiringInfo().setBirthDate(wHI.getBirthDate());
        wNewPensionerBean.getHiringInfo().setTerminateDate(wHI.getTerminateDate());
        wNewPensionerBean.getHiringInfo().setPensionPinCode(wHI.getPensionPinCode());
        wNewPensionerBean.getHiringInfo().setPfaInfo(wHI.getPfaInfo());
        wNewPensionerBean.getHiringInfo().setGender(wHI.getGender());
        wNewPensionerBean.setMaritalStatusId(wHI.getMaritalStatus().getId());
        int wTotalLengthOfService = (wHI.getTerminateDate().getYear() - wHI.getHireDate().getYear()) * 12;
        int wTotalLenOfServInYears = wHI.getTerminateDate().getYear() - wHI.getHireDate().getYear();
        if (wHI.getTerminateDate().getMonthValue() > wHI.getHireDate().getMonthValue()) {
            wTotalLengthOfService += ((wHI.getTerminateDate().getMonthValue() - wHI.getHireDate().getMonthValue()) + 1);
        }
        wNewPensionerBean.setTotalLengthOfService(wTotalLengthOfService);
        wNewPensionerBean.setTotalLengthOfServiceInYears(wTotalLenOfServInYears);
        wNewPensionerBean.setCalculateGratuityInd(1);
        wNewPensionerBean.setCalculatePensionInd(1);
        return  wNewPensionerBean;
    }


    /**
     * Here we set such things as
     *
     * @param pNewPensionerBean
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
   
    private static NewPensionerBean setEmployeeInfoDropDownValues(NewPensionerBean pNewPensionerBean, Employee retiringEmp, GenericService genericService) throws InstantiationException, IllegalAccessException {
        List<City> wCityList = genericService.loadAllObjectsWithoutRestrictions(City.class, "name");
        List<State> wStateInfoList = new ArrayList<>();
        //List<State> wStateOfOriginList = genericService.loadAllObjectsWithoutRestrictions(State.class,"name");
        List<LGAInfo> wLGAInfo;

        BusinessClient businessClient = genericService.loadObjectById(BusinessClient.class,retiringEmp.getBusinessClientId());

        wStateInfoList.add(retiringEmp.getCity().getState());


        //If State is not found...set a flag and load all States...
        pNewPensionerBean.setStateId(retiringEmp.getCity().getState().getId());
        pNewPensionerBean.setStateOfOriginId(retiringEmp.getStateOfOrigin().getId());
        wLGAInfo = genericService.loadAllObjectsWithSingleCondition(LGAInfo.class, CustomPredicate.procurePredicate("state.id", retiringEmp.getStateOfOrigin().getId()), "name");
        pNewPensionerBean.getPensioner().setAddress1(retiringEmp.getAddress1());
        pNewPensionerBean.setLgaId(retiringEmp.getLgaInfo().getId());
        pNewPensionerBean.setLgaInfoList(wLGAInfo);
        pNewPensionerBean.setStateInfoList(wStateInfoList);
        pNewPensionerBean.setCityList(wCityList);
        pNewPensionerBean.setCityId(retiringEmp.getCity().getId());
        pNewPensionerBean.getPensioner().setEmployee(new Employee(retiringEmp.getId()));
        pNewPensionerBean.getPensioner().setRank(retiringEmp.getRank());
        pNewPensionerBean.getPensioner().setFirstName(retiringEmp.getFirstName());
        pNewPensionerBean.getPensioner().setInitials(retiringEmp.getInitials());
        pNewPensionerBean.getPensioner().setLastName(retiringEmp.getLastName());
        pNewPensionerBean.getPensioner().setMdaName(retiringEmp.getCurrentMdaName());
        pNewPensionerBean.setActiveEmployeeId(retiringEmp.getEmployeeId());
        pNewPensionerBean.getPensioner().setEmail(retiringEmp.getEmail());
        pNewPensionerBean.getPensioner().setGsmNumber(retiringEmp.getGsmNumber());
        pNewPensionerBean.getPensioner().setReligion(retiringEmp.getReligion());
        pNewPensionerBean.setName(retiringEmp.getDisplayNameWivTitlePrefixed());
        pNewPensionerBean.setOrganization(businessClient.getName());

        if(!IppmsUtils.isNotNullOrEmpty(retiringEmp.getResidenceId())){
            pNewPensionerBean.getPensioner().setResidenceId(retiringEmp.getResidenceId());
            pNewPensionerBean.setLockResId(true);
        }
        if(!IppmsUtils.isNullOrEmpty(retiringEmp.getNin())){
            pNewPensionerBean.getPensioner().setNin(retiringEmp.getNin());
            pNewPensionerBean.setLockNin(true);
        }

        return pNewPensionerBean;
    }


    private static NewPensionerBean createSalaryInformation(NewPensionerBean pNewPensionerBean, SalaryInfo salaryInfo, GenericService genericService, PayrollService payrollService) throws Exception {

        pNewPensionerBean.setSalaryInfo(salaryInfo);
        //Now use this value to get the Salary Type ID if it exists from the Pension DB...
        pNewPensionerBean.setLevelAndStepInd(salaryInfo.getId());

        pNewPensionerBean.setSalaryTypeId(salaryInfo.getSalaryType().getId());
        List<SalaryInfo> wList = payrollService.loadLevelAndStepBySalaryTypeId(pNewPensionerBean.getSalaryTypeId());
        pNewPensionerBean.setLevelAndStepList(wList);
        pNewPensionerBean.setSalaryTypeList(Arrays.asList(salaryInfo.getSalaryType()));

        return pNewPensionerBean;
    }


}
