package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Entity
@Table(name = "ippms_deduction_audit_pensions")
@SequenceGenerator(name = "deductAuditPenSeq", sequenceName = "ippms_deduction_audit_pensions_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class DeductionAuditPensions extends AbstractDeductionAuditEntity{

    @Id
    @GeneratedValue(generator = "deductAuditPenSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id", nullable = false)
    private Pensioner pensioner;


    public DeductionAuditPensions(Long pId) {
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
