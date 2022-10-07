package com.osm.gnl.ippms.ogsg.audit.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ippms_rba_config_audit")
@SequenceGenerator(name = "rbaConfigAuditSeq", sequenceName = "ippms_rba_config_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RbaConfigAuditBean implements Serializable,Comparable<RbaConfigAuditBean> {

	@Id
	@GeneratedValue(generator = "rbaConfigAuditSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "rba_config_audit_inst_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "new_rba_percentage", columnDefinition = "numeric(5,2) default '0.00'", nullable = false)
	private double newRbaPercentage;
	@Column(name = "old_rba_percentage", columnDefinition = "numeric(5,2) default '0.00'", nullable = false)
	private double oldRbaPercentage;


	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;
	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;
	@Column(name = "audit_time", nullable = false, length = 30)
	private String auditTimeStamp;

	public RbaConfigAuditBean(Long pId) {
		this.id = pId;
	}

    public boolean isNewEntity() {
    	return this.id == null;
    }

	@Override
	public int compareTo(RbaConfigAuditBean rbaConfigAuditBean) {
		return 0;
	}
}
