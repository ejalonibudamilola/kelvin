package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@NoArgsConstructor
@Entity
@Table(name = "ippms_paycheck_deductions")
@SequenceGenerator(name = "paycheckDedSeq", sequenceName = "ippms_paycheck_deductions_seq", allocationSize = 1)
@Getter
@Setter
public class PaycheckDeduction extends AbstractPaycheckDeductionEntity {

    @Id
    @GeneratedValue(generator = "paycheckDedSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_ded_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBean employeePayBean;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "deduct_info_inst_id")
    private EmpDeductionInfo empDedInfo;






    public PaycheckDeduction(Long pId) {
        this.id = pId;
    }



    public int compareTo(PaycheckDeduction pIncoming) {
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
        this.empDedInfo = (EmpDeductionInfo) abstractDeductionEntity;
    }


}