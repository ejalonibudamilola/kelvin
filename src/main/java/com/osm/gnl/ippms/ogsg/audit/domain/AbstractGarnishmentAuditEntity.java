package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractTriggerAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractGarnishmentAuditEntity extends AbstractTriggerAuditEntity {

    @ManyToOne
    @JoinColumn(name = "garn_type_inst_id", nullable = false)
    private EmpGarnishmentType garnishmentType;


    public String getGarnishType() {
        if ((garnishmentType != null) && (!garnishmentType.isNewEntity())) {
            this.garnishType = garnishmentType.getName();
        }

        return this.garnishType;
    }

    public String getAuditTime() {
        if (super.getLastModTs() != null)
            if(this.auditTime != null)
                this.auditTime = (dtf.format(super.getLastModTs()) + " " + this.auditTime);
            else
                this.auditTime = dtf.format(super.getLastModTs());
        return this.auditTime;
    }

    @Transient
    private String changedDate;
    @Transient
    private String garnishType;
    @Transient
    private String auditTime;
    public abstract void setId(Long pId);
    public abstract Long getId();
}
