package com.osm.gnl.ippms.ogsg.domain.suspension;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_reinstatement_tracker")
@SequenceGenerator(name = "reinstateTrackerSeq", sequenceName = "ippms_reinstatement_tracker_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class PayrollReinstatementTracker implements Serializable {
    private static final long serialVersionUID = 4398698446054799904L;

    @Id
    @GeneratedValue(generator = "reinstateTrackerSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "reinstate_tracker_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_inst_id" )
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "pensioner_inst_id" )
    private Pensioner pensioner;

    @Column(name = "month_ind", length = 2)
    private int monthInd;
    @Column(name = "year_ind", length = 4)
    private int yearInd;
    @Column(name = "no_of_days", length = 5)
    private int noOfDays;
    @Column(name = "arrears_percentage", columnDefinition = "numeric(12,2) default '0.00'")
    private double percentage;
    @ManyToOne
    @JoinColumn(name = "reinstatement_inst_id")
    private ReinstatementLog reinstatementLog;
    @ManyToOne
    @JoinColumn(name = "salary_info_inst_id")
    private SalaryInfo salaryInfo;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Column(name = "last_mod_by", nullable = false, length = 50)
    private String lastModBy;


    public PayrollReinstatementTracker(Long pId) {
        this.id = pId;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }

    private boolean isPensioner(){
        return pensioner != null && !pensioner.isNewEntity();
    }

}