/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.pagination.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.domain.beans.MPBAMiniBean;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DataTableBean extends NamedEntityLong {

    private List<?> objectList;
    private List<MPBAMiniBean> mpbaMiniBeanList;
    private int pageNumber;
    private int pageLength;
    private int listSize;
    private String sortCriterion;
    private String sortOrder;
    private String objectName;
    private String createdBy;
    private String createdDateStr;
    private int noOfEmployees;
    private boolean objectsExist;
    private String showRow;
    private String yearStr;
    private String monthAndYearStr;
    private int monthInd;
    private Long mdaInd;
    private Long schoolId;
    private String employeeName;
    private String employeeId;
    private String empId;
    private double totPensionCont;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDate fromHireDate;
    private LocalDate toHireDate;
    private LocalDate fromBirthDate;
    private LocalDate toBirthDate;
    private LocalDate fromRetireDate;
    private LocalDate toRetireDate;
    private String fromDateStr;
    private String toDateStr;
    private String fromHireDateStr;
    private String toHireDateStr;
    private String fromBirthDateStr;
    private String toBirthDateStr;
    private String fromRetireDateStr;
    private String toRetireDateStr;
    private boolean addWarningIssued;
    private boolean showOverride;
    private boolean replaceWarningIssued;
    private boolean showForConfirm;
    private boolean individual;
    private int objectId;
    private String details;
    private Long empInstId;
    private String mapObj;
    private int pfaId;
    private Long idLong;
    private String ogNumber;
    private String lastName;
    private String useTpsCpsRule;
    private String includeTerminated;
    private int tpsOrCps;
    private String tpsOrCpsName;
    private boolean notUsingDates;
    private int userId;
    // -- PaginatedAbmpBean
    private double totalBasicSalary;
    private String totalBasicSalaryStr;
    private double totBasicSalaryPlusLtg;
    private String totBasicSalaryPlusLtgStr;
    private double netIncrease;
    private String netIncreaseStr;
    // -- PaginatedBankInfoBean
    private String sortCode;
    private Long bankId;
    private String typeCode;
    private boolean canSendToExcel;
    private String organization;
    private String birthDateStr;
    private String hireDateStr;
    private String salaryScaleLevelAndStep;
    private String salaryTypeName;
    private int noOfYearsInService;
    private Long deductionTypeId;
    private boolean filteredBySchool;
    private String payPeriodStr;
    private boolean showLink;
    private String actionCompleted;
    private boolean captchaError;
    private boolean errorRecord;
    private String typeName;
    private boolean filteredByType;
    private boolean usingDates;
    private String schoolName;
    private boolean filteredByMda;
    private String payPeriod;
    private boolean usingPayPeriod;
    private String departmentName;
    private String mbapName;
    private String ministryName;
    private String approvalMemo;
    private int overrideInd;
    private String memoType;
    private boolean rejection;
    private int noOfFemales;
    private int noOfMales;
    private String femalePercentageStr;
    private String malePercentageStr;
    private boolean hasErrors;
    private boolean readOnly;
    private String urlName;
    private String searchAgainUrl;

    public DataTableBean(List<?> objectList) {
        this.objectList = objectList;
    }

}
