/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.repository;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.dao.INegPayDao;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class NegPayDaoImpl implements INegPayDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public NegPayDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<AbstractPaycheckEntity> loadNegativePaycheckByMonthAndYear(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, int pStartRow,
                                                                           int pEndRow, Long pEmpInstId, String pLastName, boolean pShowAll) throws IllegalAccessException, InstantiationException {
        boolean wUsingEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpInstId);
        boolean wUsingLastName = IppmsUtils.isNotNullOrEmpty(pLastName);
        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        List wRetList = new ArrayList();
        String hqlQuery = "";
        if(pShowAll){

            hqlQuery = "select p.id,p.payPeriodStart,p.payPeriodEnd,p.totalPay," +
                    "p.netPay,p.taxesPaid,p.payDate,e.id,e.employeeId" +
                    ",e.firstName,e.lastName,e.initials, s.level,s.step,sc.name,p.nhf," +
                    "p.freePay,p.taxableIncome,p.monthlyReliefAmount,m.id,m.name,p.totalDeductions,p.totalGarnishments,p.unionDues,e.id,p.negativePayInd " +
                    "from "+businessCertificate.getPaycheckBeanName()+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e,SalaryInfo s,SalaryType sc, MdaInfo m, MdaDeptMap mdm " +
                    "where e.id = p.employee.id and p.netPay < 0 and m.id = mdm.mdaInfo.id and mdm.id = p.mdaDeptMap.id" +
                    " and p.salaryInfo.id = s.id and s.salaryType.id = sc.id and p.runMonth = :pRM and p.runYear = :pRY ";
        }else{
            hqlQuery = "select p.id,p.payPeriodStart,p.payPeriodEnd,p.totalPay," +
                    "p.netPay,p.taxesPaid,p.payDate,e.id,e.employeeId" +
                    ",e.firstName,e.lastName,e.initials, s.level,s.step,sc.name,p.nhf," +
                    "p.freePay,p.taxableIncome,p.monthlyReliefAmount,m.id,m.name,p.totalDeductions,p.totalGarnishments,p.unionDues,e.id,p.negativePayInd " +
                    "from "+businessCertificate.getPaycheckBeanName()+" p, "+IppmsUtils.getEmployeeTableName(businessCertificate)+" e,SalaryInfo s,SalaryType sc, MdaInfo m, MdaDeptMap mdm " +
                    "where e.id = p.employee.id and p.negativePayInd = :pStatus and p.netPay < 0 and m.id = mdm.mdaInfo.id and mdm.id = p.mdaDeptMap.id" +
                    " and p.salaryInfo.id = s.id and s.salaryType.id = sc.id and p.runMonth = :pRM and p.runYear = :pRY ";
        }

        if(wUsingEmpId){
            hqlQuery += "and e.id = :pEmpIdVal ";
        }
        if(wUsingLastName){
            hqlQuery += "and upper(e.lastName) like :pLastNameVal";
        }
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);
        if(!pShowAll)
            query.setParameter("pStatus", 1);
        if(wUsingLastName)
            query.setParameter("pLastNameVal", "%"+pLastName.toUpperCase()+"%");
        if(wUsingEmpId)
            query.setParameter("pEmpIdVal", pEmpInstId);

        query.setParameter("pRM", pRunMonth);
        query.setParameter("pRY", pRunYear);
        if(pStartRow > 0)
            query.setFirstResult(pStartRow);
        query.setMaxResults(pEndRow);
        wRetVal = (ArrayList)query.list();

        AbstractPaycheckEntity p;
        int i = 0;
        if (wRetVal.size() > 0)
        {
            for (Object[] o : wRetVal) {
                p = (AbstractPaycheckEntity) IppmsUtils.getPaycheckClass(businessCertificate).newInstance();
                p.setId((Long)o[i++]);
                p.setPayPeriodStart((LocalDate) o[i++]);
                p.setPayPeriodEnd((LocalDate)o[i++]);
                p.setBusinessClientId(businessCertificate.getBusinessClientInstId());
                p.setTotalPay((Double)o[i++]);
                p.setNetPay((Double)o[i++]);
                p.setTaxesPaid((Double)o[i++]);
                p.setPayDate((LocalDate)o[i++]);
                Employee e = new Employee((Long)o[i++]);
                e.setEmployeeId((String)o[i++]);
                e.setFirstName((String)o[i++]);
                e.setLastName((String)o[i++]);
                Object wInit = o[i++];
                if(wInit == null)
                    e.setInitials(IConstants.EMPTY_STR);
                else
                    e.setInitials((String)wInit);
                p.setAbstractEmployeeEntity(e);
                SalaryInfo s = new SalaryInfo();
                s.setLevel((Integer)o[i++]);
                s.setStep((Integer)o[i++]);
                SalaryType sc = new SalaryType();
                sc.setName((String)o[i++]);
                p.setNhf((Double)o[i++]);
                p.setFreePay((Double)o[i++]);
                p.setTaxableIncome((Double)o[i++]);
                p.setMonthlyReliefAmount((Double)o[i++]);
                s.setSalaryType(sc);
                p.setSalaryInfo(s);
                p.setCurrentMapId((Long)o[i++]);
                p.setMda((String)o[i++]);
                p.setTotalDeductions((Double)o[i++]);
                p.setTotalGarnishments((Double)o[i++]);
                p.setUnionDues((Double)o[i++]);

                p.getAbstractEmployeeEntity().setId((Long)o[i++]);
                p.setNegativePayInd((Integer)o[i++]);
                if(p.getNegativePayInd() == 0)
                    p.setName(p.getAbstractEmployeeEntity().getDisplayName()+"**");
                else{
                    p.setName(p.getAbstractEmployeeEntity().getDisplayName());
                }
                wRetList.add(p);
                i= 0;
            }

        }

        return wRetList;



    }

    @Override
    public int getNoOfNegPaychecksByMonthAndYear(BusinessCertificate businessCertificate, int pRunMonth, int pRunYear, Long pEmpId, String pLastNameStr, boolean pShowAll) {
        boolean wUsingEmpId = IppmsUtils.isNotNull(pEmpId );
        boolean wUsingLastName = IppmsUtils.isNotNullOrEmpty(pLastNameStr );
        String hqlQuery = "";
        if(pShowAll){
            hqlQuery = "select count(p.id) " +
                    "from "+businessCertificate.getPaycheckBeanName()+" p, Employee e,SalaryInfo s,SalaryType sc " +
                    "where e.id = p.employee.id " +
                    " and p.salaryInfo.id = s.id and s.salaryType.id = sc.id and p.netPay < 0 " +
                    " and p.runMonth = :pRM and p.runYear = :pRY ";
        }else{
            hqlQuery = "select count(p.id) " +
                    "from "+businessCertificate.getPaycheckBeanName()+" p, Employee e,SalaryInfo s,SalaryType sc " +
                    "where e.id = p.employee.id and p.negativePayInd = :pStatus" +
                    " and p.salaryInfo.id = s.id and s.salaryType.id = sc.id and p.netPay < 0 and p.runMonth = :pRM and p.runYear = :pRY ";
        }

        if(wUsingEmpId){
            hqlQuery += "and e.id = :pEmpIdVal ";
        }
        if(wUsingLastName){
            hqlQuery += "and upper(e.lastName) like :pLastNameVal";
        }

        Query query = this.sessionFactory.getCurrentSession() .createQuery(hqlQuery);
        query.setParameter("pRM", pRunMonth);
        query.setParameter("pRY", pRunYear);
        if(!pShowAll){
            query.setParameter("pStatus", 1);
        }
        if(wUsingLastName)
            query.setParameter("pLastNameVal", "%"+pLastNameStr.toUpperCase()+"%");
        if(wUsingEmpId)
            query.setParameter("pEmpIdVal", pEmpId);
        int retVal = 0;

        List list = query.list();
        if ((list == null) || (list.isEmpty())) {
            return 0;
        }
        retVal = ((Long)list.get(0)).intValue();
        return retVal;

    }

    @Override
    @Transactional()
    public void resetNegativePayInd(BusinessCertificate bc,Long pEmpInstId, int pRunMonth, int pRunYear) {

        String wHqlStr = "update "+IppmsUtils.getPaycheckTableName(bc)+" e set e.negativePayInd = 0 where e.employee.id = :pVal" +
                " and e.runMonth = :pIntVal and e.runYear = :pIntVal2 ";

        Query query = this.sessionFactory.getCurrentSession().createQuery(wHqlStr);
        query.setParameter("pVal", pEmpInstId);
        query.setParameter("pIntVal", pRunMonth);
        query.setParameter("pIntVal2", pRunYear);
        query.executeUpdate();

    }


}
