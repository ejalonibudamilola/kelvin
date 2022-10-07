package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractTriggerAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_hire_audit")
@SequenceGenerator(name = "hireInfoTriggAuditSeq", sequenceName = "ippms_hire_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class HiringInfoAudit extends AbstractTriggerAuditEntity
{

private static final long serialVersionUID = -3724183879515359961L;
  
  @Id
  @GeneratedValue(generator = "hireInfoTriggAuditSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "audit_inst_id", unique = true, nullable = false)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "hire_info_inst_id", nullable = false)
  private HiringInfo hireInfo;

  public HiringInfoAudit(Long pId) { this.id = pId;}

    public String getAuditTime() {
        if(this.lastModTs != null)
            if(this.auditTimeStamp != null)
                this.auditTime = dtf.format(this.lastModTs)+" "+this.auditTimeStamp;
            else
                this.auditTime = dtf.format(this.lastModTs);
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public boolean isNewEntity() {
	 
	return this.id == null;
}

@Override
public int compareTo(Object o) {
	 
	return 0;
}


}