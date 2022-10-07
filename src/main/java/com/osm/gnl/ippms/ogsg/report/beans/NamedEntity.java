/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.report.beans;

import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NamedEntity implements Comparable<Object>{
    private Long id;
    private Long parentInstId;
    protected String name;
    private String mode;
    private String description;
    private Integer currentOtherId;
    private int pageSize;
    private int objectInd;
    private int runMonth;
    private int runYear;
    private String staffId;
    private boolean admin;
    private int reportType;
    private LocalDate allowanceStartDate;
    private LocalDate allowanceEndDate;
    private LocalDate creationDate;
    private List<PromotionTracker> promotionTracker;
    private Long mdaInstId;
    private boolean confirmation;
    private BusinessCertificate roleBean;
    private boolean user;
    private boolean editMode;
    private String goToLink;
    //--Pension Stuffs...
    private Long paycheckTypeInstId;
    private double deductionAmount;
    private String payPercentageStr;
    private int noOfEmployees;
    private String payGratuity;
    private double allowanceAmount;
    private boolean errorRecord;
    private String showRow;
    private String errorMsg;
    private String amountInWords;
    private String displayErrors;
    private String objectCode;
    private String startDateString;
    private String endDateString;
    private LocalDate startDate;
    private LocalDate endDate;
    private double loanBalance;
    private double tenor;
    private String payTypeName;
    private String startAndEndPeriod;
    private String organization;
    private String levelAndStep;
    private String displayStyle;
    private String allowanceStartDateStr;
    private String allowanceEndDateStr;
    private String deductionAmountStr;
    private String ltgYear;
    private String bankSortCode;
    private String bankBranchSortCode;
    private Long branchInstId;
    private String titleField;
    private boolean doNotShowButton;
    private String startMonthStr;
    private String startYearStr;
    private String endMonthStr;
    private String endYearStr;
    private boolean deletable;
    private boolean extendable;
    private boolean deleteOperation;
    private boolean updateOperation;
    private String generatedCaptcha;
    private String enteredCaptcha;
    private boolean captchaError;
    private boolean forSuccessDisplay;
    private Long salaryTypeId;
    private int level;
    private int step;
    private Long hiringInfoId;
    private Long oldSalaryId;
    private String startDateStr;
    private String endDateStr;



    /**
     * Used to drive Notifications...
     * @param pId
     * @param pName
     * @param pDesc
     */
    public NamedEntity(Integer pId, String pName, String pDesc) {
        this.id = Long.valueOf(pId);
        this.name = pName;
        this.description = pDesc;
    }
    public NamedEntity(Integer pId){
        this.currentOtherId = pId;
    }

    /**
     * Do not change or modify. Used by JavaScript.
     * @param pName
     * @param pGotToLink
     */
    public NamedEntity(String pName, String pGotToLink ){
        this.name = pName;
        this.goToLink = pGotToLink;
    }

    public NamedEntity(String s) {
        this.name = s;
    }

    public NamedEntity(Integer integer, String name) {
        this.currentOtherId = integer;
        this.name = name;
    }

    public boolean isNewEntity(){
        return this.id == null;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }


}
