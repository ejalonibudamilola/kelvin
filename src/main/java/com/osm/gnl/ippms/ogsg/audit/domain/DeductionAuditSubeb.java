package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_deduction_audit_subeb")
@SequenceGenerator(name = "deductAuditSubebSeq", sequenceName = "ippms_deduction_audit_subeb_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class DeductionAuditSubeb extends AbstractDeductionAuditEntity{

    @Id
    @GeneratedValue(generator = "deductAuditSubebSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id", nullable = false)
    private Employee employee;


    public DeductionAuditSubeb(Long pId) {
        this.id = pId;
    }



    @Override
    public int compareTo(Object o) {

        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }
}
