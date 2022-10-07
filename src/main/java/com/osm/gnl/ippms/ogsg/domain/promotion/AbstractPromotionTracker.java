/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.promotion;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractPromotionTracker {

    @Column(name = "last_promotion_date", nullable = false)
    private LocalDate lastPromotionDate;

    @Column(name = "next_promotion_date", nullable = false)
    private LocalDate nextPromotionDate;
    @ManyToOne
    @JoinColumn(name = "user_inst_id")
    private User user;
    @Transient
    private String lastPromoDateStr;
    @Transient
    private String nextPromoDateStr;
    @Transient
    private boolean promoteEmployeeRef;
    @Transient
    private String displayStyle;

    public abstract boolean isNewEntity();

    public String getLastPromoDateStr() {
        if (this.lastPromotionDate != null) {
            this.lastPromoDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.lastPromotionDate);
        }
        return this.lastPromoDateStr;
    }


    public String getNextPromoDateStr() {
        if (this.nextPromotionDate != null) {
            this.nextPromoDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.nextPromotionDate);
        }
        return this.nextPromoDateStr;
    }



}
