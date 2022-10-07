/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ippms_mass_reassign_master")
@SequenceGenerator(name = "massReassignMasterSeq", sequenceName = "ippms_mass_reassign_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MassReassignMasterBean extends AbstractNamedEntity {

    @Id
    @GeneratedValue(generator = "massReassignMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mass_reassign_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id",nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "from_salary_type_inst_id",nullable = false)
    private SalaryType fromSalaryType;

    @ManyToOne
    @JoinColumn(name = "to_salary_type_inst_id",nullable = false)
    private SalaryType toSalaryType;

    @OneToMany(mappedBy = "massReassignMasterBean", fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
    private List<MassReassignDetailsBean> massReassignDetailsBeans;

    @Column(name = "approval_status", columnDefinition = "integer default '0'", nullable = false)
    protected int approvalStatus;

    @Column(name = "monthly_implication", columnDefinition = "varchar(20)")
    protected String monthlyImplication;

    @Transient
    private boolean approved;

    @Transient
    private boolean rejected;
    @Transient
    private int totalEmp;
    @Transient
    private String sumFrom;
    @Transient
    private String sumTo;
    @Transient
    private String netDiff;
    @Transient
    protected String enteredCaptcha;
    @Transient
    protected String generatedCaptcha;
    @Transient
    protected boolean confirmation;

    public int getTotalEmp() {
        if(this.getMassReassignDetailsBeans() != null)
            totalEmp = this.getMassReassignDetailsBeans().size();
        return totalEmp;
    }

    public boolean isApproved() {
        approved = this.approvalStatus == 1;
        return approved;
    }
    public boolean isRejected(){
        rejected = this.approvalStatus == 2;
        return rejected;
    }
    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
