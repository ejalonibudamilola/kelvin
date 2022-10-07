package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionInfoPensions;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Table(name = "ippms_paycheck_deductions_blgp")
@SequenceGenerator(name = "paycheckDedBlglSeq", sequenceName = "ippms_paycheck_deductions_blgp_seq", allocationSize = 1)
@Getter
@Setter
public class PaycheckDeductionBLGP extends AbstractPaycheckDeductionEntity {

    @Id
    @GeneratedValue(generator = "paycheckDedBlglSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_ded_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanBLGP employeePayBean;

    @ManyToOne
    @JoinColumn(name = "deduct_info_inst_id")
    private EmpDeductionInfoPensions empDedInfo;


    public PaycheckDeductionBLGP(Long pId) {
        this.id = pId;
    }

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Pensioner employee;

    public int compareTo(PaycheckDeductionBLGP pIncoming) {
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
