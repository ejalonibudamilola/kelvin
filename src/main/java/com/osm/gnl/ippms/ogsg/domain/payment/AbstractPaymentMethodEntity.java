/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPaymentMethodEntity implements Serializable {


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_type_inst_id")
    private PaymentMethodTypes paymentMethodTypes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_inst_id")
    private BankBranch bankBranches;

    @Column(name = "account_number", length = 20, nullable = false)
    private String accountNumber;

    @Column(name = "bvn", length = 11)
    private String bvnNo;

    @Column(name = "account_number_2", columnDefinition = "varchar(20) default NULL")
    private String accountNumber2;

    @Column(name = "account_type", columnDefinition = "varchar(1) default 'C'", nullable = false)
    private String accountType;

    @Column(name = "cash_card_number", length = 19, nullable = true)
    private String cashCardNumber;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    protected User createdBy;


    @Column(name = "creation_date", nullable = false)
    protected Timestamp creationDate = Timestamp.from(Instant.now());

    @Column(name = "last_mod_ts", nullable = false)
    protected Timestamp lastModTs;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_inst_id")
    private User lastModBy;

    @Column(name = "shares_acct_with", length = 10)
    private String inheritOgNumber;

    @ManyToOne
    @JoinColumn(name = "share_acct_creator")
    protected User shareAccountCreator;


    @Transient
    private String routingNumber;
    @Transient
    private String routingNumber2;
    @Transient
    private String accountTypeStr;
    @Transient
    private String paymentTypeRef;
    @Transient
    private String showRow;
    @Transient
    private Long bankId;
    @Transient
    private String cashCardShowRow;
    @Transient
    private Long cashCardBankId;
    @Transient
    private Long directDepositBankId;
    @Transient
    private Long branchId;
    @Transient
    private boolean terminated;
    @Transient
    private String displayErrors;
    @Transient
    private boolean needsOgNumber;
    @Transient
    private boolean confirmation;
    @Transient
    protected boolean ogNumberNotEmpty;


    public String getAccountTypeStr() {
        if (this.accountType != null) {
            if (this.accountType.equalsIgnoreCase("c"))
                this.accountTypeStr = "Checking";
            else if (this.accountType.equalsIgnoreCase("s"))
                this.accountTypeStr = "Savings";
            else
                this.accountTypeStr = "Undefined";
        } else this.accountTypeStr = "";

        return this.accountTypeStr;
    }

    public abstract boolean isNewEntity();

}
