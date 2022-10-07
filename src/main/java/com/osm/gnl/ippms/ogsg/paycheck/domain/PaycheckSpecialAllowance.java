package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_paycheck_spec_allow")
@SequenceGenerator(name = "paycheckSpecAllowSeq", sequenceName = "ippms_paycheck_spec_allow_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckSpecialAllowance extends AbstractPaycheckSpecAllowEntity{
    private static final long serialVersionUID = 1767735158495855115L;

    @Id
    @GeneratedValue(generator = "paycheckSpecAllowSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_spec_allow_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;


    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBean employeePayBean;

    @ManyToOne
    @JoinColumn(name = "spec_allow_info_inst_id", nullable = false)
    private SpecialAllowanceInfo specialAllowanceInfo;

    //public PaycheckSpecialAllowance(Long pId) {
    //    this.id = pId;
   // }


    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }


    @Override
    public Object getParentObject() {
        return this.employeePayBean;
    }


    @Override
    public int compareTo(AbstractPaycheckSpecAllowEntity abstractPaycheckSpecAllowEntity) {
        return 0;
    }

    @Override
    public void setSpecialAllowanceInfo(AbstractSpecialAllowanceEntity abstractPaycheckSpecAllowEntity) {
        this.specialAllowanceInfo = (SpecialAllowanceInfo) abstractPaycheckSpecAllowEntity;
    }
}