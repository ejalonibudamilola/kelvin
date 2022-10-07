package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class SchoolService {

    /**
     * Kasumu Taiwo
     * 12-2020
     */

    private final SessionFactory sessionFactory;

    public SchoolService(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<SalaryInfo> loadBasicSalaryInfo()
    {
        ArrayList<Object[]> wRetVal = new ArrayList<Object[]>();
        List<SalaryInfo> wRetList = new ArrayList<SalaryInfo>();

        String hqlQuery = "select s.id, s.level,s.step,s.monthlyBasicSalary from SalaryInfo s";
        Query query = this.sessionFactory.getCurrentSession().createQuery(hqlQuery);

        wRetVal = (ArrayList)query.list();
        if (wRetVal.size() > 0) {
            for (Object[] o : wRetVal) {
                SalaryInfo s = new SalaryInfo((Long)o[0]);
                s.setLevel((Integer) o[1]);
                s.setStep((Integer) o[2]);
                s.setMonthlyBasicSalary(PayrollPayUtils.convertDoubleToEpmStandard((Double)o[3] / 12.0D));

                wRetList.add(s);
            }
        }

        return wRetList;
    }

}
