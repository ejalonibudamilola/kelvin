package com.osm.gnl.ippms.ogsg.paycheck.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfoSubeb;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ippms_paycheck_spec_allow_subeb")
@SequenceGenerator(name = "paycheckSpecAllowSubebSeq", sequenceName = "ippms_paycheck_spec_allow_subeb_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaycheckSpecialAllowanceSubeb extends AbstractPaycheckSpecAllowEntity{

    @Id
    @GeneratedValue(generator = "paycheckSpecAllowSubebSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "paycheck_spec_allow_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;


    @ManyToOne
    @JoinColumn(name = "paycheck_inst_id", nullable = false)
    private EmployeePayBeanSubeb employeePayBean;

    @ManyToOne
    @JoinColumn(name = "spec_allow_info_inst_id", nullable = false)
    private SpecialAllowanceInfoSubeb specialAllowanceInfo;



    @Transient
    private String employeeId;
    @Transient
    private String employeeName;


    public PaycheckSpecialAllowanceSubeb(Long pId) {
        this.id = pId;
    }


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
        this.specialAllowanceInfo = (SpecialAllowanceInfoSubeb) abstractPaycheckSpecAllowEntity;
    }
}
