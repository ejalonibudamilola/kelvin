package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentInfoPensions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_paycheck_garnishments_pen")
@SequenceGenerator(name = "paycheckPenGarnSeq", sequenceName = "ippms_paycheck_garnishments_pen_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckGarnishmentPension extends AbstractPaycheckGarnishmentEntity {


    @Id
    @GeneratedValue(generator = "paycheckPenGarnSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_garn_inst_id", nullable = false, updatable = false,unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanPension employeePayBean;


    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Pensioner employee;

    @ManyToOne
    @JoinColumn(name = "garn_info_inst_id", nullable = false)
    private EmpGarnishmentInfoPensions empGarnInfo;

    public PaycheckGarnishmentPension(Long pId) {
        this.id = pId;
    }


    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public void setEmpGarnInfo(AbstractGarnishmentEntity abstractGarnishmentEntity) {
        this.empGarnInfo = (EmpGarnishmentInfoPensions) abstractGarnishmentEntity;
    }

    @Override
    public void setEmployee(AbstractEmployeeEntity abstractEmployeeEntity) {
        this.employee = (Pensioner) abstractEmployeeEntity;
    }
    @Override
    public String getEmployeeName() {
        if(this.getEmployee() != null && !this.getEmployee().isNewEntity())
            return this.getEmployee().getDisplayName();
        return null;
    }
}
