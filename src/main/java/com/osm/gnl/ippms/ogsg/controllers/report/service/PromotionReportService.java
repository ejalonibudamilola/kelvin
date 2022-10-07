package com.osm.gnl.ippms.ogsg.controllers.report.service;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.report.VariationReportBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Repository
@Transactional(readOnly = true)
@Slf4j
public class PromotionReportService {

    @Autowired
    private GenericService genericService;

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<VariationReportBean> loadFlaggedPromotionsForAllOrganization(int pRunMonth, int pRunYear, BusinessCertificate bc) {
        List wRetList = new ArrayList();

        String wHql = "SELECT e.firstName, e.lastName, e.initials, e.employeeId, bc.name, bc.id, f.promotionDate, u.firstName, u.lastName, m.name, "
                +" fs.annualSalary, ts.annualSalary, fs.monthlyGross, ts.monthlyGross, fs.consolidatedAllowance, ts.consolidatedAllowance "
                +" FROM FlaggedPromotions f, "+ IppmsUtils.getEmployeeTableName(bc)+" e, SalaryInfo fs, SalaryInfo ts, User u, BusinessClient bc, MdaInfo m WHERE "
                +" f.employee.id = e.id and f.mdaInfo.id = m.id and f.fromSalaryInfo.id = fs.id and f.toSalaryInfo.id = ts.id "
                +" and f.user.id = u.id and f.businessClientId = bc.id and f.statusInd = 1 and f.runMonth = :pRunMonth and f.runYear = :pRunYear";



        Query query = this.sessionFactory.getCurrentSession().createQuery(wHql);
        query.setParameter("pRunMonth", pRunMonth);
        query.setParameter("pRunYear", pRunYear);

        ArrayList<Object[]> wRetVal = (ArrayList<Object[]>) query.list();
        if (wRetVal.size() > 0) {

            for (Object[] o : wRetVal) {
                VariationReportBean p = new VariationReportBean();
                p.setEmployeeId((String) o[3]);
                String pFirstName = ((String) o[0]);
                String pLastName = ((String) o[1]);
                String pInitials = null;
                if (o[2] != null) {
                    pInitials = ((String) o[2]);
                }
                p.setEmployeeName(PayrollHRUtils.createDisplayName(pLastName,
                        pFirstName, pInitials));
                p.setBusinessName((String)o[4]);
                p.setBusinessClientInstId((Long)o[5]);
                p.setPromotionDate((LocalDate)o[6]);
                p.setUserFirstName((String)o[7]);
                p.setUserLastName((String)o[8]);
                p.setMda((String)o[9]);
                p.setOldSalary((Double)o[10]);
                p.setBasicSalary((Double)o[11]);
                p.setPrevMonthGross((Double)o[12]);
                p.setThisMonthGross((Double)o[13]);
                p.setOldAllowance((Double)o[14]);
                p.setCurrentAllowance((Double)o[15]);
            }
        }

        return wRetList;

    }
}
