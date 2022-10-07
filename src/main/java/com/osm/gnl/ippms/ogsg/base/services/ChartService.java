package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.chart.ChartDTO;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("chartService")
@Repository
@Transactional(readOnly = true)
public class ChartService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;


    public List<MdaInfo> countPromotedStaffByMda(BusinessClient bc, String month) {

        List wRetList = new ArrayList();

        String sql = "SELECT m.codeName, count(p.mdaInfo.id) from MdaInfo m, "+IppmsUtils.getPromotionAuditTableByBusinessClient(bc)+" p where " +
                "p.businessClientId = :pBizClient and p.auditPayPeriod = :pMonth and p.mdaInfo.id = " +
                "m.id GROUP BY m.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizClient", bc.getId());
        wQuery.setParameter("pMonth", month);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaInfo m = new MdaInfo();
                m.setCodeName((String) o[0]);
                m.setTotalNoOfEmployees((long) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;
    }

    public List<MdaInfo> countNewStaffByMda(long clientCode, String month) {

        List wRetList = new ArrayList();

        String sql = "SELECT m.codeName, count(e.mdaInfo.id) from MdaInfo m, EmployeeAudit e where " +
                "e.businessClientId = :pBizClient and e.auditActionType = :pAudit and e.auditPayPeriod " +
                "= :pMonth and e.mdaInfo.id = m.id GROUP BY m.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizClient", clientCode);
        wQuery.setParameter("pMonth", month);
        wQuery.setParameter("pAudit", "I");

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaInfo m = new MdaInfo();
                m.setCodeName((String) o[0]);
                m.setTotalNoOfEmployees((long) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;

    }

    public List<MdaType> countStaffByMda(long clientCode) {

        List wRetList = new ArrayList();

        String sql = "SELECT m.name, count(e.id) from MdaType m, Employee e where" +
                " e.businessClientId = :pBizClient and e.mdaDeptMap.mdaInfo.mdaType.id = m.id" +
                " GROUP BY m.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizClient", clientCode);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaType m = new MdaType();
                m.setName((String) o[0]);
                m.setId((long) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;


    }

    public List<MdaInfo> countAbsorbedStaffByMda(long clientCode, String month) {

        List wRetList = new ArrayList();

        String sql = "SELECT m.codeName, count(a.mdaInfo.id) from MdaInfo m, AbsorptionLog a where " +
                "a.businessClientId = :pBizClient and a.auditPayPeriod = :pMonth and a.mdaInfo.id = " +
                "m.id GROUP BY m.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizClient", clientCode);
        wQuery.setParameter("pMonth", month);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaInfo m = new MdaInfo();
                m.setCodeName((String) o[0]);
                m.setTotalNoOfEmployees((long) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;

    }

    public List<MdaInfo> countReinstatedStaffByMda(long clientCode, String month) {

        List wRetList = new ArrayList();

        String sql = "SELECT m.codeName, count(r.mdaInfo.id) from MdaInfo m, ReinstatementLog r where " +
                "r.businessClientId = :pBizClient and r.auditPayPeriod = :pMonth and r.mdaInfo.id = " +
                "m.id GROUP BY m.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizClient", clientCode);
        wQuery.setParameter("pMonth", month);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaInfo m = new MdaInfo();
                m.setCodeName((String) o[0]);
                m.setTotalNoOfEmployees((long) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;

    }

    public Double sumPaycheckTotalPay(long i, BusinessClient bc, LocalDate payCheckDate) throws Exception {
        String sql = "select sum(b.totalPay) from " + IppmsUtils.getPaycheckTableName(BusinessCertificateCreator.makeBusinessClient(bc)) + " b" +
                " where b.businessClientId = :pBizClient and b.payDate = :pPayDate";

       // System.out.println("sql is " + sql);

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("pBizClient", i);
        query.setParameter("pPayDate", payCheckDate);
        Double sumPaycheck = (Double) query.uniqueResult();

        if (sumPaycheck == null)
            sumPaycheck = 0.0D;

        return sumPaycheck;
    }

    public List<MdaInfo> sumTotalPayByMda(BusinessClient bc, LocalDate payCheckDate) throws Exception {

        List wRetList = new ArrayList();

        String sql = "select a.codeName, sum(b.totalPay) from MdaInfo a, " + IppmsUtils.getPaycheckTableName(BusinessCertificateCreator.makeBusinessClient(bc)) + " b" +
                " where b.payDate = :pPayDate and b.mdaDeptMap.mdaInfo.id = a.id group by a.id order by a.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pPayDate", payCheckDate);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaInfo m = new MdaInfo();

                m.setCodeName((String) o[0]);
                m.setTotalGrossPay((Double) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;
    }

    public List<ChartDTO> getEmployeeTotalPayDetails(BusinessClient bc, LocalDate payCheckDate, long mdaId) throws Exception {
        List wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId," +
                " b.netPay, b.totalPay from Employee a, " + IppmsUtils.getPaycheckTableName(BusinessCertificateCreator.makeBusinessClient(bc)) + " b" +
                " where b.payDate = :pPayDate and b.mdaDeptMap.mdaInfo.id = :pMid and" +
                " b.employee.id = a.id group by a.lastName, a.firstName, a.initials, a.gsmNumber, a.employeeId," +
                " b.netPay, b.totalPay";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pPayDate", payCheckDate);
        wQuery.setParameter("pMid", mdaId);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                ChartDTO p = new ChartDTO();

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String)o[2]);
                p.setGsmNumber((String)o[3]);
                p.setEmployeeId((String)o[4]);
                p.setPay((Double) o[5]);
                p.setSpecialPay((Double)o[6]);
                wRetList.add(p);

               // System.out.println("In chart Service......." + p.getLastName() + " " + p.getFirstName() + " " + p.getSpecialPay());
            }
        }

        return wRetList;
    }

    public Double sumSpecAllowAmount(long i, BusinessClient bc, LocalDate specAllowDate) throws Exception {
        String sql = "select sum(b.amount) from " + IppmsUtils.getPaycheckSpecAllowTableName(BusinessCertificateCreator.makeBusinessClient(bc)) + " b" +
                " where b.businessClientId = :pBizClient and b.payDate = :pPayDate ";

        //System.out.println("sql is " + sql);

        Query query = this.sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("pBizClient", i);
        query.setParameter("pPayDate", specAllowDate);
        Double sumPaycheck = (Double) query.uniqueResult();
        if (sumPaycheck == null)
            sumPaycheck = 0.0D;

        return sumPaycheck;

    }

    public List<MdaInfo> sumSpecAllowByMda(BusinessClient bc, LocalDate payCheckDate) throws Exception {
        List wRetList = new ArrayList();

        String sql = "select a.codeName, sum(b.amount) from MdaInfo a, " + IppmsUtils.getPaycheckSpecAllowTableName(BusinessCertificateCreator.makeBusinessClient(bc)) + " b" +
                " where b.payDate = :pPayDate and b.employeePayBean.mdaDeptMap.mdaInfo.id = a.id group by a.id order by a.id";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pPayDate", payCheckDate);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                MdaInfo m = new MdaInfo();

                m.setCodeName((String) o[0]);
                m.setMonthlyBasic((Double) o[1]);
                wRetList.add(m);
            }
        }

        return wRetList;
    }

    public List<ChartDTO> getEmployeeSpecAllowDetails(BusinessClient bc, LocalDate payCheckDate, long mdaId) throws Exception {
        List wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId," +
                " b.amount, b.employeePayBean.totalPay from Employee a, " + IppmsUtils.getPaycheckSpecAllowTableName(BusinessCertificateCreator.makeBusinessClient(bc)) + " b" +
                " where b.payDate = :pPayDate and b.employeePayBean.mdaDeptMap.mdaInfo.id = :pMid and" +
                " b.employee.id = a.id group by a.lastName, a.firstName, a.initials, a.gsmNumber, a.employeeId," +
                " b.amount, b.employeePayBean.totalPay";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pPayDate", payCheckDate);
        wQuery.setParameter("pMid", mdaId);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                ChartDTO p = new ChartDTO();

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String)o[2]);
                p.setGsmNumber((String)o[3]);
                p.setEmployeeId((String)o[4]);
                p.setSpecialPay((Double) o[5]);
                p.setPay((Double)o[6]);
                wRetList.add(p);

                System.out.println("In chart Service......." + p.getLastName() + " " + p.getFirstName() + " " + p.getSpecialPay());
            }
        }

        return wRetList;
    }

    public List<ChartDTO> loadPromotionDetailsForModalWindow(BusinessClient bc,MdaInfo mdaInfo, String month) {
        List<ChartDTO> wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId, si.level, si.salaryType.name, si.step, si2.level," +
                " si2.salaryType.name, si2.step from Employee a, SalaryInfo si, SalaryInfo si2, "+IppmsUtils.getPromotionAuditTableByBusinessClient(bc)+" pa where" +
                " pa.mdaInfo.id = :pMid and pa.auditPayPeriod = :pPayPeriod and pa.businessClientId = :pBizId and" +
                " pa.employee.id = a.id and pa.oldSalaryInfo.id = si.id and pa.salaryInfo.id = si2.id ORDER BY a.lastName";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pMid", mdaInfo.getId());
        wQuery.setParameter("pBizId", mdaInfo.getBusinessClientId());
        wQuery.setParameter("pPayPeriod", month);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                ChartDTO p = new ChartDTO();

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String) o[2]);
                p.setGsmNumber((String) o[3]);
                p.setEmployeeId((String) o[4]);
                p.setLevel((int) o[5]);
                p.setSalaryTypeName((String) o[6]);
                p.setStep((int) o[7]);
                p.setLevel2((int) o[8]);
                p.setSalaryTypeName2((String) o[9]);
                p.setStep2((int) o[10]);
                wRetList.add(p);

                //System.out.println("In chart Service......." + p.getLastName() + " " + p.getFirstName() + " " + p.getLevel() + " " + p.getLevel2() + " " + p.getStep() + " " + p.getStep2());
               // System.out.println("Salary Type name is " + p.getSalaryTypeName() + " " + p.getSalaryTypeName2());
            }
        }

        return wRetList;
    }


    public List<ChartDTO> loadNewEmployeeDetailsForModalWindow(MdaInfo mdaInfo, String month) {

        List<ChartDTO> wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId, si.level," +
                " si.salaryType.name, si.step from Employee a, SalaryInfo si, EmployeeAudit ea where" +
                " ea.mdaInfo.id = :pMid and ea.auditPayPeriod = :pPayPeriod and ea.businessClientId = :pBizId and" +
                " ea.employee.id = a.id and ea.salaryInfo.id = si.id and ea.auditActionType = :pAudit" +
                " ORDER BY a.lastName";

        System.out.println("sql is "+sql);

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pMid", mdaInfo.getId());
        wQuery.setParameter("pBizId", mdaInfo.getBusinessClientId());
        wQuery.setParameter("pPayPeriod", month);
        wQuery.setParameter("pAudit", "I");

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                ChartDTO p = new ChartDTO();

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String) o[2]);
                p.setGsmNumber((String)o[3]);
                p.setEmployeeId((String) o[4]);
                p.setLevel((int) o[5]);
                p.setSalaryTypeName((String) o[6]);
                p.setStep((int) o[7]);
                wRetList.add(p);
            }
        }
        return wRetList;
    }

    public List<ChartDTO> loadAbsorbedEmployeeDetailsForModalWindow(MdaInfo mdaInfo, String month) {
        List<ChartDTO> wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId, si.level," +
                " si.salaryType.name, si.step, al.suspensionDate, al.absorptionDate from Employee a, SalaryInfo si," +
                " AbsorptionLog al where al.mdaInfo.id = :pMid and al.auditPayPeriod = :pPayPeriod and " +
                " al.businessClientId = :pBizId and al.employee.id = a.id and al.salaryInfo.id = si.id ORDER BY a.lastName";

        System.out.println("sql is " + sql);

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pMid", mdaInfo.getId());
        wQuery.setParameter("pBizId", mdaInfo.getBusinessClientId());
        wQuery.setParameter("pPayPeriod", month);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                ChartDTO p = new ChartDTO();

                String dateStr = o[8].toString();
                System.out.println("dateStr is "+dateStr);
                String dateStr1 = o[9].toString();
                System.out.println("dateStr1 is "+dateStr1);

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String) o[2]);
                p.setGsmNumber((String) o[3]);
                p.setEmployeeId((String) o[4]);
                p.setLevel((int) o[5]);
                p.setSalaryTypeName((String) o[6]);
                p.setStep((int) o[7]);
                p.setSuspensionDateStr(dateStr);
                p.setConfirmationDateAsStr(dateStr1);
                wRetList.add(p);
            }
        }
        return wRetList;
    }

    public List<ChartDTO> loadReinstatedEmployeeDetailsForModalWindow(MdaInfo mdaInfo, String month) {
        List<ChartDTO> wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId, si.level," +
                " si.salaryType.name, si.step, rl.terminationDate, rl.reinstatementDate from Employee a," +
                " SalaryInfo si, ReinstatementLog rl where rl.mdaInfo.id = :pMid and rl.auditPayPeriod = :pPayPeriod" +
                " and rl.businessClientId = :pBizId and rl.employee.id = a.id and rl.salaryInfo.id = si.id ORDER BY a.lastName";

        System.out.println("sql is " + sql);

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pMid", mdaInfo.getId());
        wQuery.setParameter("pBizId", mdaInfo.getBusinessClientId());
        wQuery.setParameter("pPayPeriod", month);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                ChartDTO p = new ChartDTO();

                String dateStr = o[8].toString();
                System.out.println("dateStr is "+dateStr);
                String dateStr1 = o[9].toString();
                System.out.println("dateStr1 is "+dateStr1);

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String) o[2]);
                p.setGsmNumber((String) o[3]);
                p.setEmployeeId((String) o[4]);
                p.setLevel((int) o[5]);
                p.setSalaryTypeName((String) o[6]);
                p.setStep((int) o[7]);
                p.setTerminatedDate(dateStr);
                p.setConfirmationDateAsStr(dateStr1);
                wRetList.add(p);
            }
        }
        return wRetList;
    }

    public List<Employee> loadStaffDetailsForModalWindow(String label) {

        List<Employee> wRetList = new ArrayList();

        String sql = "select a.lastName, a.firstName, coalesce(a.initials,''), a.gsmNumber, a.employeeId," +
                " b.codeName from Employee a, MdaInfo b where a.mdaDeptMap.mdaInfo.mdaType.name = :pLabel" +
                " and a.mdaDeptMap.mdaInfo.id = b.id ORDER BY a.lastName";

        System.out.println("sql is " + sql);

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pLabel", label);

        List<Object[]> wRetVal = wQuery.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                Employee p = new Employee();

                p.setLastName((String) o[0]);
                p.setFirstName((String) o[1]);
                p.setInitials((String) o[2]);
                p.setGsmNumber((String)o[3]);
                p.setEmployeeId((String) o[4]);
                p.setMdaName((String)o[5]);
                wRetList.add(p);
            }
        }
        return wRetList;

    }
}