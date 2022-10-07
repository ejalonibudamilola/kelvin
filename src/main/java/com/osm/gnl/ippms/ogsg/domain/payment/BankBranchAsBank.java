package com.osm.gnl.ippms.ogsg.domain.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_branch_as_bank")
@SequenceGenerator(name = "branchAsBankSeq", sequenceName = "ippms_branch_as_bank_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class BankBranchAsBank {
    @Id
    @GeneratedValue(generator = "branchAsBankSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "branch_as_bank_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "branch_inst_id", nullable = false)
    private Long branchInstId;


    public BankBranchAsBank(Long id, Long businessClientId, Long branchInstId) {
        this.id = id;
        this.businessClientId = businessClientId;
        this.branchInstId = branchInstId;
    }

    public BankBranchAsBank(Long pId) {
        this.id = pId;
    }

    public boolean isNewEntity(){
        return this.id == null;
    }
}
