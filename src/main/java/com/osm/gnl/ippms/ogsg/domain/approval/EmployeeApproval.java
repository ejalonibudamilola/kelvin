/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "ippms_employee_approval")
@SequenceGenerator(name = "empPayApprSeq", sequenceName = "ippms_employee_approval_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmployeeApproval extends AbstractApprovalEntity {

    /**
     *
     */
    private static final long serialVersionUID = -6022051471105338491L;

    @Id
    @GeneratedValue(generator = "empPayApprSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "pay_approval_inst_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    @Transient private AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient private AbstractEmployeeEntity childObject;

    public EmployeeApproval(Long pId) {
        this.id = pId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeApproval)) return false;
        EmployeeApproval that = (EmployeeApproval) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(this.getBusinessClientId(), that.getBusinessClientId());
    }

    public AbstractEmployeeEntity getChildObject() {
        if(this.getEmployee() != null && !this.getEmployee().isNewEntity())
            childObject = this.getEmployee();
        else
            if(this.getPensioner() != null && !this.getPensioner().isNewEntity())
                childObject = this.getPensioner();
        return childObject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getBusinessClientId());
    }
    public boolean isNewEntity() {
        return this.id == null;
    }


}
