package com.osm.gnl.ippms.ogsg.domain.subvention;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ippms_subvention_history")
@SequenceGenerator(name = "subventionHistSeq", sequenceName = "ippms_subvention_history_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class SubventionHistory  implements Serializable {
	private static final long serialVersionUID = -5729089512722527068L;

	@Id
	@GeneratedValue(generator = "subventionHistSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "subvention_history_inst_id", nullable = false, unique = true)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "subvention_inst_id")
	private Subvention subvention;
	@Column(name = "run_month", columnDefinition = "numeric")
	private int runMonth;
	@Column(name = "run_year", columnDefinition = "numeric")
	private int runYear;
	@Column(name = "amount", columnDefinition = "numeric(15,2)")
	private double amount;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;


	public SubventionHistory(Long pId) {
		this.id = pId;
	}


	public boolean isNewEntity() {

		return this.id == null;
	}
}