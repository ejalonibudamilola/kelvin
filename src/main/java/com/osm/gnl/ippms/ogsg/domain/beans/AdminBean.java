package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AdminBean extends NamedEntity {
    private static final long serialVersionUID = -8093467702079346422L;
    private boolean approvedPaychecks;
    private boolean empToBePromoted;
    private boolean empToBeRetired;
    private boolean superAdmin;
    private boolean runManualPromotion;
    private String showManualPromotionRow;
    private boolean hasPendingTransfers;
    private int noOfPendingTransfers;
    private boolean hasPendingEmployeeApprovals;
    private int noOfPendingEmployeeApprovals;
    private int noOfPendingLeaveBonuses;
    private boolean hasPendingLeaveBonus;
    private boolean leaveBonusModuleOpen = false;
    private boolean canRerunPayroll;
    private int noOfNegativePayRecords;
    private int noOfPendingNameConflicts;
    private boolean hasPendingNameConflicts;
    private int noOfPendingAllowanceRules;
    private boolean hasPendingAllowanceApprovals;
    //--Protect Mass Entry Dashboard
    private boolean canDoEmail;
    private boolean canDoPromotions;
    private boolean canDoLoans;
    private boolean canDoSpecAllow;
    private boolean canDoTransfers;
    private boolean canDoDeductions;

}