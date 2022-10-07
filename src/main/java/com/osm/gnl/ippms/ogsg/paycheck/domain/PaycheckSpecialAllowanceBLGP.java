package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfoLG;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfoPensions;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_paycheck_spec_allow_blgp")
@SequenceGenerator(name = "paycheckSpecAllowBlgpSeq", sequenceName = "ippms_paycheck_spec_allow_blgp_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckSpecialAllowanceBLGP extends AbstractPaycheckSpecAllowEntity {

    @Id
    @GeneratedValue(generator = "paycheckSpecAllowBlgpSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_spec_allow_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanBLGP employeePayBean;

    @ManyToOne
    @JoinColumn(name = "spec_allow_info_inst_id", nullable = false)
    private SpecialAllowanceInfoPensions specialAllowanceInfo;


    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Pensioner employee;


    @Transient
    private String employeeId;
    @Transient
    private String employeeName;


    public PaycheckSpecialAllowanceBLGP(Long pId) {
        this.id = pId;
    }

    public int compareTo(PaycheckSpecialAllowanceBLGP pIncoming) {
        if (pIncoming != null)
            return getPayPeriodEnd().compareTo(pIncoming.getPayPeriodEnd());
        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

    @Override
    public Object getParentObject() {
        return null;
    }

    @Override
    public int compareTo(AbstractPaycheckSpecAllowEntity abstractPaycheckSpecAllowEntity) {
        return 0;
    }

    @Override
    public void setSpecialAllowanceInfo(AbstractSpecialAllowanceEntity abstractPaycheckSpecAllowEntity) {
        this.specialAllowanceInfo = (SpecialAllowanceInfoPensions) abstractPaycheckSpecAllowEntity;
    }
}

