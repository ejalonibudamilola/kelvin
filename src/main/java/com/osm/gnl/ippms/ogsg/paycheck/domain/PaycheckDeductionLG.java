package com.osm.gnl.ippms.ogsg.paycheck.domain;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionInfoLG;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_paycheck_deductions_lg")
@SequenceGenerator(name = "paycheckDedLgSeq", sequenceName = "ippms_paycheck_deductions_lg_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckDeductionLG extends AbstractPaycheckDeductionEntity {

    @Id
    @GeneratedValue(generator = "paycheckDedLgSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_ded_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanLG employeePayBean;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "deduct_info_inst_id")
    private EmpDeductionInfoLG empDedInfo;


    public PaycheckDeductionLG(Long pId) {
        this.id = pId;
    }


    public int compareTo(PaycheckDeductionLG pIncoming) {
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
        this.empDedInfo = (EmpDeductionInfoLG) abstractDeductionEntity;
    }

}
