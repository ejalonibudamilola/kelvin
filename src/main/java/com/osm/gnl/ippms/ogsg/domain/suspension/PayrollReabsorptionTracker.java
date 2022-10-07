package com.osm.gnl.ippms.ogsg.domain.suspension;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_pay_reabsorb_tracker")
@SequenceGenerator(name = "absorbTrackerSeq", sequenceName = "ippms_pay_reabsorb_tracker_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollReabsorptionTracker implements Comparable<PayrollReabsorptionTracker> {

	@Id
	@GeneratedValue(generator = "absorbTrackerSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "reabsorb_tracker_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "absorption_inst_id", nullable = false)
	private AbsorptionLog absorptionLog;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id" )
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id" )
	private Pensioner pensioner;

	@Column(name = "month_ind", columnDefinition = "numeric")
	private int monthInd;
	@Column(name = "year_ind", columnDefinition = "numeric")
	private int yearInd;
	@Column(name = "no_of_days", columnDefinition = "numeric")
	private int noOfDays;
	@Column(name = "arrears_percentage", columnDefinition = "numeric(15,2) default '0.00'")
	private double percentage;
	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;
	@ManyToOne
	@JoinColumn(name = "user_inst_id")
	private User user;


	public PayrollReabsorptionTracker(Long pId) {
		this.id = pId;
	}



	@Override
	public int compareTo(PayrollReabsorptionTracker o) {

		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}

}