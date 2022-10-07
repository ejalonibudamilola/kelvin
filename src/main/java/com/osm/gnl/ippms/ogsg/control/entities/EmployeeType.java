package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_employee_type")
@SequenceGenerator(name = "empTypeSeq", sequenceName = "ippms_employee_type_seq", allocationSize = 1)
@Getter
@Setter
@EqualsAndHashCode
public class EmployeeType  extends AbstractControlEntity {

    @Id
    @GeneratedValue(generator = "empTypeSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "employee_type_inst_id")
    private Long id;

    @Column(name = "employee_type", length = 40, nullable = false)
    private String name;
    @Column(name = "employee_type_code", columnDefinition = "integer default '1'")
    private int employeeTypeCode;
    @Column(name = "contract_enabled" , columnDefinition = "integer default '0'")
    private int contractStatusInd;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "renewable" , columnDefinition = "integer default '0'")
    private int renewableInd;

    @Column(name = "political_ind", columnDefinition = "integer default '0'")
    private int politicalInd;

    @Column(name = "description", length = 2000, nullable = false)
    private String description;

    @Transient
    private boolean contractStaff;

    @Transient private boolean renewable;
    @Transient private boolean politicalOfficeHolder;
    @Transient private boolean politicalOfficeHolderBind;
    @Transient private boolean contractBind;
    @Transient private boolean contractRenewableBind;

    public EmployeeType() {
    }

    public EmployeeType(Long pId, Integer pPoliticalInd, Integer pEmployeeTypeCode, Integer pContractStatInd) {
        this.id = pId;
        this.employeeTypeCode = pEmployeeTypeCode;
        this.politicalInd = pPoliticalInd;
        this.contractStatusInd = pContractStatInd;
     }

    public EmployeeType(Long pId ) {
        this.id = pId;

    }


    public boolean isContractStaff()
    {
        return this.employeeTypeCode == 1 && this.contractStatusInd == 1;
    }


    public boolean isRenewable()
    {
        this.renewable = this.renewableInd == 1;

        return this.renewable;
    }


    public boolean isPoliticalOfficeHolderType()
    {
        return this.politicalInd == 1;
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
