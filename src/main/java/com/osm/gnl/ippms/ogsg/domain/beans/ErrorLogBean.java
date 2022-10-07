package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;



@Entity
@Table(name = "ippms_error_log")
@SequenceGenerator(name = "errorLogSeq", sequenceName = "ippms_error_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ErrorLogBean  implements Serializable {
	@Id
	@GeneratedValue(generator = "errorLogSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "error_inst_id")
	private Long id;

	@Column(name = "error_cause", nullable = false, length = 40000)
	private String errorCause;
	@Column(name = "stack_trace", nullable = false, length = 40000)
	private String errorMsg;
	
	@ManyToOne
	@JoinColumn(name = "user_inst_id",nullable = false)
	private User user;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 32)
	private String lastModBy;
	
	@Column(name = "audit_time", nullable = false, length = 20)
	private String auditTimeStamp;
	
	@Transient private Throwable throwable;

	@Transient private String details;

	@Transient private String errorLogTime;
	@Transient private BusinessClient businessClient;

	@Transient private String bcert;

	public String getErrorLogTime() {
		errorLogTime = PayrollHRUtils.getDisplayDateFormat().format(this.lastModTs)+" "+this.auditTimeStamp;
		return errorLogTime;
	}

}
