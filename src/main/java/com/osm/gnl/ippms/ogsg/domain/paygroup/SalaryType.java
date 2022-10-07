/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_salary_type")
@SequenceGenerator(name = "salaryTypeSeq", sequenceName = "ippms_salary_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SalaryType extends AbstractDescControlEntity {
    private static final long serialVersionUID = -6714783874241123016L;

    @Id
    @GeneratedValue(generator = "salaryTypeSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "salary_type_inst_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @Column(name = "salary_type_code", columnDefinition = "numeric(2,0) default '0'")
    private int salaryTypeCode;
    @Column(name = "salary_type_sub_code", columnDefinition = "numeric(2,0) default '0'")
    private int salaryTypeSubCode;
    @Column(name = "resp_allow_ind", columnDefinition = "integer default '0'")
    private int respAllowanceInd;
    @Column(name = "selectable", columnDefinition = "integer default '1'")
    private int selectableInd;
    @Column(name = "consolidated_ind", columnDefinition = "integer default '0'" )
    private int consolidatedInd;
    @Column(name = "deactivated_ind", columnDefinition = "integer default '0'")
    private int deactivatedInd;
    @Column(name = "pay_group_code", columnDefinition = "varchar(10)")
    private String payGroupCode;
    @Column(name = "pen_cont_type_ind", columnDefinition = "integer default '0'", nullable = false)
    private int penContTypeInd;
    @Column(name = "pension_exempt_ind", columnDefinition = "integer default '0'")
    private int pensionExemptInd;
    /**
     * Critical Semaphore. Determines the Pay Group to Use for Gratuity and Pension Calculation.
     */
    @Column(name = "pension_ind", columnDefinition = "integer default '0'")
    private int pensionInd;

    @Transient private String oldPayGroupCode;
    @Transient private int pensionRuleCode;
    @Transient private String pensionRule;
    @Transient private boolean deactivated;
    @Transient private boolean consolidated;
    @Transient private boolean penContType;
    @Transient private boolean selectable;
    @Transient private boolean create;
    @Transient private boolean consolidatedBind;
    @Transient private boolean pensionExemptIndBind;
    @Transient private String consolidatedStr;
    @Transient private String contributesToPensionStr;
    @Transient private String selectableStr;
    @Transient private boolean selectableBind;
    @Transient private boolean showDetailsRow;
    @Transient private boolean basicRentTransportType;
    @Transient private boolean exemptFromPension;


    public SalaryType(Long pId) {
        this.id = pId;
    }

    public SalaryType(Long pId, int pSalaryTypeCode) {
        this.id = pId;
        this.salaryTypeCode = pSalaryTypeCode;
    }

    public boolean isBasicRentTransportType() {
        basicRentTransportType = this.penContTypeInd == 0;
        return basicRentTransportType;
    }

    public boolean isExemptFromPension() {
        this.exemptFromPension = this.pensionExemptInd == 1;
        return exemptFromPension;
    }

    public String getConsolidatedStr() {
         consolidatedStr = (this.isConsolidated() ? "Yes" : "No");
        return consolidatedStr;
    }

    public String getSelectableStr() {
        selectableStr = (this.isSelectable() ? "Yes" : "No");
        return selectableStr;
    }

    public String getContributesToPensionStr() {
        this.contributesToPensionStr = (this.isExemptFromPension() ? "No" : "Yes");
        return contributesToPensionStr;
    }

    public boolean isDeactivated() {
        this.deactivated = this.deactivatedInd == 1;
        return deactivated;
    }

    public boolean isSelectable() {
        selectable = this.selectableInd == 1;
        return selectable;
    }

    public boolean isConsolidated() {
        consolidated = this.consolidatedInd == 1;
        return consolidated;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    public void setShowEditDetailsRow(boolean pShowDetailsRow) {
        this.showDetailsRow = pShowDetailsRow;
    }
}