package com.osm.gnl.ippms.ogsg.leavebonus.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "ippms_leave_bonus_1")
@SequenceGenerator(name = "leaveBonus1Seq", sequenceName = "ippms_leave_bonus_1_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class FirstLeaveBonus {


    @Id
    @GeneratedValue(generator = "leaveBonus1Seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "leave_bonus_1_inst_id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;
    @ManyToOne()
    @JoinColumn(name = "user_inst_id")
    private User login;
    @Column(name = "created_month")
    private int createdMonth;
    @Column(name = "created_year")
    private int createdYear;
    @ManyToOne()
    @JoinColumn(name = "salary_info_inst_id")
    private SalaryInfo salaryInfo;

    @Column(name = "last_mod_ts", nullable = false)
    private LocalDate lastModTs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FirstLeaveBonus)) return false;
        FirstLeaveBonus that = (FirstLeaveBonus) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isNewEntity() {

        return this.id == null;
    }


}
