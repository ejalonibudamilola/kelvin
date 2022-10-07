package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_garnishment_audit_pension")
@SequenceGenerator(name = "garnishmentAuditPenSeq", sequenceName = "ippms_garnishment_audit_pen_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GarnishmentAuditPension extends AbstractGarnishmentAuditEntity {

    @Id
    @GeneratedValue(generator = "garnishmentAuditPenSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Pensioner employee;


    public GarnishmentAuditPension(Long pId) {
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
