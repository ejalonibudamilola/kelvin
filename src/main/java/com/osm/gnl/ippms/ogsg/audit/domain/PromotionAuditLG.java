package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_promotion_audit_lg")
@SequenceGenerator(name = "promotionAuditLgSeq", sequenceName = "ippms_promotion_audit_lg_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PromotionAuditLG extends AbstractPromotionAuditEntity {

    @Id
    @GeneratedValue(generator = "promotionAuditLgSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "promotion_audit_inst_id", unique = true, nullable = false)
    private Long id;

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
