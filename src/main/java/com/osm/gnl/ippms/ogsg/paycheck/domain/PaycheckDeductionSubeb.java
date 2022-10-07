package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionInfoSubeb;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_paycheck_deductions_subeb")
@SequenceGenerator(name = "paycheckDedSubebSeq", sequenceName = "ippms_paycheck_deductions_subeb_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckDeductionSubeb extends AbstractPaycheckDeductionEntity{

    @Id
    @GeneratedValue(generator = "paycheckDedSubebSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_ded_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanSubeb employeePayBean;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "deduct_info_inst_id")
    private EmpDeductionInfoSubeb empDedInfo;



    public PaycheckDeductionSubeb(Long pId) {
        this.id = pId;
    }


    public int compareTo(PaycheckDeductionSubeb pIncoming) {
        if (pIncoming != null)
            return getPayPeriodEnd().compareTo(pIncoming.getPayPeriodEnd());
        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

    @Override
    public void setEmpDedInfo(AbstractDeductionEntity abstractDeductionEntity) {
        this.empDedInfo = (EmpDeductionInfoSubeb) abstractDeductionEntity;
    }

}
