package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "ippms_paycheck_garnishments")
@SequenceGenerator(name = "paycheckGarnSeq", sequenceName = "ippms_paycheck_garnishments_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckGarnishment extends AbstractPaycheckGarnishmentEntity
{

    @Id
    @GeneratedValue(generator = "paycheckGarnSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_garn_inst_id", nullable = false, updatable = false,unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBean employeePayBean;

    @ManyToOne
    @JoinColumn(name = "garn_info_inst_id", nullable = false)
    private EmpGarnishmentInfo empGarnInfo;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;


    public PaycheckGarnishment(Long pId) {
        this.id = pId;
    }


    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

    @Override
    public void setEmpGarnInfo(AbstractGarnishmentEntity abstractGarnishmentEntity) {
       this.empGarnInfo = (EmpGarnishmentInfo) abstractGarnishmentEntity;
    }

    @Override
    public void setEmployee(AbstractEmployeeEntity abstractEmployeeEntity) {
        this.employee = (Employee) abstractEmployeeEntity;
    }

    @Override
    public String getEmployeeName() {
        if(this.getEmployee() != null && !this.getEmployee().isNewEntity())
            return this.getEmployee().getDisplayName();
        return null;
    }
}