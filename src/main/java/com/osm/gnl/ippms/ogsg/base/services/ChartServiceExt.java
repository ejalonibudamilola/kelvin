package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.chart.ChartDTO;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("chartServiceExt")
@Repository
@Transactional(readOnly = true)
public class ChartServiceExt {


    private final GenericService genericService;


    private final SessionFactory sessionFactory;

    public ChartServiceExt(GenericService genericService, SessionFactory sessionFactory) {
        this.genericService = genericService;
        this.sessionFactory = sessionFactory;
    }

    public ChartDTO annualSalaryForPromotionAudit(BusinessClient bc, String month) {

        ChartDTO chartDTO = new ChartDTO();

        String sql = "SELECT count(p.id), sum(msi.annualGross) from "+ IppmsUtils.getPromotionAuditTableByBusinessClient(bc) +" p,MiniSalaryInfoDao msi " +
                "where p.businessClientId = :pBizIdVar and p.auditPayPeriod = :pPayPeriodVar " +
                "and p.salaryInfo.id = msi.salaryInfoId";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizIdVar", bc.getId());
        wQuery.setParameter("pPayPeriodVar", month);

//        Object  wRetVal = wQuery.list();
        Object[] wRetVal = (Object[]) wQuery.uniqueResult();

        if (wRetVal != null) {
            chartDTO.setNoOfYearsAtRetirement(((Long)wRetVal[0]).intValue());
            chartDTO.setTotalAnnualSalary(((Double)wRetVal[1]).doubleValue());
        }

        return chartDTO;
    }

    public ChartDTO annualSalaryForEmployeeAudit(Long clientCode, String month) {
        ChartDTO chartDTO = new ChartDTO();

        String sql = "SELECT count(p.id), sum(msi.annualGross) from EmployeeAudit p,MiniSalaryInfoDao msi " +
                "where p.businessClientId = :pBizIdVar and p.auditPayPeriod = :pPayPeriodVar  and p.auditActionType = 'I' " +
                "and p.salaryInfo.id = msi.salaryInfoId";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizIdVar", clientCode);
        wQuery.setParameter("pPayPeriodVar", month);

//        Object  wRetVal = wQuery.list();
        Object[] wRetVal = (Object[]) wQuery.uniqueResult();

        if (wRetVal != null) {
            chartDTO.setNoOfYearsAtRetirement(((Long)wRetVal[0]).intValue());
            chartDTO.setTotalAnnualSalary(((Double)wRetVal[1]).doubleValue());
        }

        return chartDTO;
    }

    public ChartDTO annualSalaryForAbsorptionLog(Long clientCode, String month) {

        ChartDTO chartDTO = new ChartDTO();

        String sql = "SELECT count(p.id), sum(msi.annualGross) from AbsorptionLog p,MiniSalaryInfoDao msi " +
                "where p.businessClientId = :pBizIdVar and p.auditPayPeriod = :pPayPeriodVar " +
                "and p.salaryInfo.id = msi.salaryInfoId";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizIdVar", clientCode);
        wQuery.setParameter("pPayPeriodVar", month);

//        Object  wRetVal = wQuery.list();
        Object[] wRetVal = (Object[]) wQuery.uniqueResult();

        if (wRetVal != null) {
            chartDTO.setNoOfYearsAtRetirement(((Long)wRetVal[0]).intValue());
            chartDTO.setTotalAnnualSalary(((Double)wRetVal[1]).doubleValue());
        }

        return chartDTO;
    }

    public ChartDTO annualSalaryForReinstatedLog(long clientCode, String month) {
        ChartDTO chartDTO = new ChartDTO();

        String sql = "SELECT count(p.id), sum(msi.annualGross) from ReinstatementLog p,MiniSalaryInfoDao msi " +
                "where p.businessClientId = :pBizIdVar and p.auditPayPeriod = :pPayPeriodVar " +
                "and p.salaryInfo.id = msi.salaryInfoId";

        Query wQuery = this.sessionFactory.getCurrentSession().createQuery(sql);

        wQuery.setParameter("pBizIdVar", clientCode);
        wQuery.setParameter("pPayPeriodVar", month);

//        Object  wRetVal = wQuery.list();
        Object[] wRetVal = (Object[]) wQuery.uniqueResult();

        if (wRetVal != null) {
            chartDTO.setNoOfYearsAtRetirement(((Long)wRetVal[0]).intValue());
            chartDTO.setTotalAnnualSalary(((Double)wRetVal[1]).doubleValue());
        }

        return chartDTO;
    }
}
