package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_garnishment_audit_subeb")
@SequenceGenerator(name = "garnishmentAuditSubebSeq", sequenceName = "ippms_garnishment_audit_subeb_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GarnishmentAuditSubeb extends AbstractGarnishmentAuditEntity{
    @Id
    @GeneratedValue(generator = "garnishmentAuditSubebSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;


    public GarnishmentAuditSubeb(Long pId) {
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
