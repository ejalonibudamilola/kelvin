/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.allowance;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_spec_allow_type")
@SequenceGenerator(name = "specAllowTypeSeq", sequenceName = "ippms_spec_allow_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SpecialAllowanceType extends AbstractDescControlEntity {
    private static final long serialVersionUID = -463206173823657729L;

    @Id
    @GeneratedValue(generator = "specAllowTypeSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "spec_allow_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @Column(name = "taxable", columnDefinition = "integer default '0'")
    private int taxExemptInd;
    @Column(name = "allow_name_change", columnDefinition = "integer default '0'")
    private int allowNameEdit;
    @Column(name = "restrict_edit_ind", columnDefinition = "integer default '0'")
    private int editRestrictionInd;
    @Column(name = "end_date_req_ind", columnDefinition = "integer default '0'")
    private int endDateRequired;

    @Column(name = "inheritable", columnDefinition = "varchar(1) default 'N'")
    private String inherit;

    @Column(name = "amount", columnDefinition = "numeric(15,2) default '0.00'")
    protected double amount;

    /**
     * for SUBEB
     */
    @Column(name = "arrears_ind", columnDefinition = "integer default '0'")
    private int arrearsInd;

    /**
     * for SUBEB
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_types_inst_id")
    private PayTypes payTypes;

    @Transient private boolean taxable;
    @Transient private String oldName;
    @Transient private boolean inherited;
    @Transient private boolean arrearsType;
    @Transient private Long payTypeId;
    @Transient private Long oldPayTypeId;

    public boolean isArrearsType() {
        return this.arrearsInd == 1;
    }



    public SpecialAllowanceType(Long pId) {
        this.id = pId;
    }


    public boolean isEndDateRequired() {
        return this.endDateRequired == 1;
    }


    public boolean isTaxable() {
        return this.taxExemptInd == 0;
    }

    public boolean isInherited() {
        return this.inherit == "Y";
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 29;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpecialAllowanceType other = (SpecialAllowanceType) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return name == other.name;
    }

    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }


}