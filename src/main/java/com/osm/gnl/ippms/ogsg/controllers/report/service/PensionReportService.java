package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.report.PensionBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/*
Taiwo Kasumu
04-07-2021
 */
@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class PensionReportService {


    private final SessionFactory sessionFactory;

    public PensionReportService(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<PensionBean> pensionListing(BusinessCertificate bc, int wRunMonth, int wRunYear)
            throws Exception {
        PensionBean wBSS;
        List<PensionBean> wCol = new ArrayList<>();
        NativeQuery wQuery = null;

        String wSql = "select t.range as r,count(monthly_pension) as totalStaff, sum(monthly_pension) as pension " +
                " from " +
                "  (select monthly_pension, case" +
                " when monthly_pension = 0.00 then '0'" +
                " when monthly_pension between 1 and 4999.99 then '1-4999'" +
                " when monthly_pension between 5000.00 and 10000.00 then '5000-10000'" +
                " when monthly_pension between 10001.00 and 20000.00 then '10001-20000'" +
                " when monthly_pension between 20001.00 and 30000.00 then '20001-30000'" +
                " when monthly_pension between 30001.00 and 40000.00 then '30001-40000'" +
                " when monthly_pension between 40001.00 and 50000.00 then '40001-50000'" +
                " when monthly_pension between 50001.00 and 60000.00 then '50001-60000'" +
                " when monthly_pension between 60001.00 and 70000.00 then '60001-70000'" +
                " when monthly_pension between 70001.00 and 80000.00 then '70001-80000'" +
                " when monthly_pension between 80001.00 and 100000.00 then '80001-100000'" +
                " when monthly_pension between 100001.00 and 120000.00 then '100001-120000'" +
                " when monthly_pension between 120001.00 and 150000.00 then '120001-150000'" +
                " when monthly_pension between 150001.00 and 200000.00 then '150001-200000'" +
                " when monthly_pension between 200001.00 and 300000.00 then '200001-300000'" +
                " when monthly_pension between 300001.00 and 500000.00 then '300001-500000'" +
                " when monthly_pension between 500001.00 and 800000.00 then '500001-800000'" +
                "  end as range" +
                "  from " + IppmsUtils.getNativePaycheckTableName(bc) + "  where run_month = " + wRunMonth + "" +
                "             and run_year = " + wRunYear + ") t " +
                "  group by t.range";

        wQuery = this.sessionFactory.getCurrentSession().createNativeQuery(wSql)
                .addScalar("r", StandardBasicTypes.STRING)
                .addScalar("totalStaff", StandardBasicTypes.LONG)
                .addScalar("pension", StandardBasicTypes.DOUBLE);


        ArrayList<Object[]> wRetVal = (ArrayList) wQuery.list();
        if (wRetVal.size() > 0) {
            Long cumulativeTotalPen = 0L;
            Double cumulativeTotalAmount = 0.0;
            for (Object[] o : wRetVal) {
                String category = ((String) o[0]);

                Long totalPensioners = ((Long) o[1]);
                Double amount = ((Double) o[2]);
                cumulativeTotalPen += totalPensioners;
                cumulativeTotalAmount += amount;
                wBSS = new PensionBean(category, totalPensioners, amount, cumulativeTotalPen, cumulativeTotalAmount);
                wCol.add(wBSS);
            }
        }
        return wCol;
    }


    public List<PensionBean> deceasedPensioners(BusinessCertificate bc, int pRunMonth, int pRunYear) {

        List<PensionBean> list = new ArrayList<PensionBean>();

        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();

        String hqlQuery = "select emp.id, emp.firstName,emp.lastName,emp.employeeId, emp.initials, p.monthlyPension, l.name "
                + " from " + IppmsUtils.getPaycheckTableName(bc) + " p, "
                + "" + IppmsUtils.getEmployeeTableName(bc) + " emp, LGAInfo l,TerminationLog t, TerminateReason r "
                + "where t.pensioner.id = emp.id and t.terminateReason.id = r.id and p.employee.id = emp.id "
                + "and emp.lgaInfo.id = l.id "
                + "and p.runMonth = :pRunMonth "
                + "and p.runYear = :pRunYear and r.name = 'Death'";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);

        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        wRetVal = (ArrayList) query.list();

        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                PensionBean p = null;
                Employee e = new Employee((Long) o[0], (String) o[1], (String) o[2]);
                e.setEmployeeId((String) o[3]);
                e.setFirstName((String) o[1]);
                e.setLastName((String) o[2]);
                e.setInitials((String) o[4]);
                p.setAmount((Double) o[5]);
                p.setLgaName((String) o[6]);
                p.setEmployee(e);
                list.add(p);
            }

        }

        return list;

    }
}
