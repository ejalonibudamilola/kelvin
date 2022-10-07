package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_garnishment_audit_lg")
@SequenceGenerator(name = "garnishmentAuditLGSeq", sequenceName = "ippms_garnishment_audit_lg_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GarnishmentAuditLG extends AbstractGarnishmentAuditEntity{

    @Id
    @GeneratedValue(generator = "garnishmentAuditLGSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;


    public GarnishmentAuditLG(Long pId) {
        this.id = pId;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

    @Override
    public int compareTo(Object o) {

        return 0;
    }
}
