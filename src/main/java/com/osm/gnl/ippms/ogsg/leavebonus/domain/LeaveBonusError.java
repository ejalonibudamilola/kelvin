package com.osm.gnl.ippms.ogsg.leavebonus.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_leave_bonus_error")
@SequenceGenerator(name = "leaveBonusErrorSeq", sequenceName = "ippms_leave_bonus_error_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class LeaveBonusError implements Comparable<LeaveBonusError> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4533949339260501017L;

	@Id
	@GeneratedValue(generator = "leaveBonusErrorSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "leave_bonus_error_inst_id", unique = true, nullable = false)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "leave_bonus_err_master_inst_id")
	private LeaveBonusErrorBean leaveBonusErrorBean;
	
	@Column(name = "employee_id", nullable = false, length = 10)
	private String employeeId;
	
	@Column(name = "error_msg", nullable = false, length = 2000)
	private String errorField;

	@Column(name = "employee_name")
	private String employeeName;
	 
	@Column(name = "leave_bonus", length = 20)
	private String leaveBonusAmount;
	 
	@Transient private String name;
	@Transient private String ltgYear;


	public boolean isNewEntity() {
		 
		return this.id == null;
	}


	@Override
	public int compareTo(LeaveBonusError leaveBonusError) {
		return 0;
	}
}
