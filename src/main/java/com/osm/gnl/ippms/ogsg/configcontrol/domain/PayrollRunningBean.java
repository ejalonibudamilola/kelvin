package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_payroll_running")
@SequenceGenerator(name = "payrollRunningBeanSeq", sequenceName = "ippms_payroll_running_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PayrollRunningBean {

    @Id
    @GeneratedValue(generator = "payrollRunningBeanSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "payroll_running_inst_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_inst_id")
    private User login;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "start_time", nullable = false, length = 20)
    private String startTime;
    @Column(name = "running_ind", nullable = false, columnDefinition = "integer")
    private int runningInd;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;
    @Column(name = "end_time", nullable = true, length = 20)
    private String endTime;


    @Transient
    private boolean running;

    public PayrollRunningBean(Long pId) {
        this.id = pId;
    }

    public PayrollRunningBean(Long pId, Long pBid) {
        this.id = pId;
        this.businessClientId = pBid;
    }


    public boolean isRunning() {
        return this.runningInd == 1;
    }


    public boolean isNewEntity() {
        return this.id == null;
    }
}
