package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionInfoPensions;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_paycheck_deductions_pension")
@SequenceGenerator(name = "paycheckDedPensionSeq", sequenceName = "ippms_paycheck_deductions_pen_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckDeductionPension extends AbstractPaycheckDeductionEntity{

    @Id
    @GeneratedValue(generator = "paycheckDedPensionSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_ded_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanPension employeePayBean;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Pensioner employee;

    @ManyToOne
    @JoinColumn(name = "deduct_info_inst_id")
    private EmpDeductionInfoPensions empDedInfo;



    public PaycheckDeductionPension(Long pId) {
        this.id = pId;
    }


    public int compareTo(PaycheckDeductionPension pIncoming) {
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
        this.empDedInfo = (EmpDeductionInfoPensions) abstractDeductionEntity;
    }
}

