package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfoLG;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_special_allow_audit_lg")
@SequenceGenerator(name = "specAllowAuditLGSeq", sequenceName = "ippms_special_allow_audit_lg_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SpecialAllowanceAuditLG extends AbstractSpecAllowAuditEntity{
    @Id
    @GeneratedValue(generator = "specAllowAuditLGSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "special_allow_audit_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "special_allow_info_inst_id", nullable = false)
    private SpecialAllowanceInfoLG specialAllowanceInfo;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @Transient
    private String allowanceType;

    public SpecialAllowanceAuditLG(Long id) {

        this.id = id;
    }


    @Override
    public int compareTo(Object o) {

        return 0;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

    public boolean isPensioner(){
        return false;
    }
}
