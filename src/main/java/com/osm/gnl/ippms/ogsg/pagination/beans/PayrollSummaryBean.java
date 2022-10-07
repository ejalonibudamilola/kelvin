package com.osm.gnl.ippms.ogsg.pagination.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import lombok.Data;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Data
public class PayrollSummaryBean extends NamedEntityBean
        implements PaginatedList {
    private static final long serialVersionUID = -9221973238395893449L;
    private double totalNetAmount;
    private double totalTaxesWitheld;
    private double totalDeductions;
    private double totalCompanyContributions;
    private double totalPay;
    private double totalGarnishments;
    private double totalCost;
    private double totalHours;
    private double totalCommissionPay;
    private double totalBonusPay;
    private double totalOvertimePay;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String fromDateAsString;
    private String toDateAsString;
    private String destUrl;
    private String companyName;
    private Long employeeId;
    private List<AbstractPaycheckEntity> empPayBean;
    private List<EmployeePayBean> EmployeePayBean;
    private List<PensionDeductionReportBean> pensionDeductionList;
    private String totalNetAmountStr;
    private String totalTaxesWitheldStr;
    private String totalDeductionsStr;
    private String totalCompanyContributionsStr;
    private String totalPayStr;
    private List<PfaInfo> pfaInfoListMap;
    private String totalGarnishmentsStr;
    private String totalCostStr;
    private String totalCommissionPayStr;
    private String totalBonusPayStr;
    private String totalOvertimePayStr;
    private boolean generateBankPvs;
    private DecimalFormat df;
    private LocalDate payDate;
    private String showRow;
    private HashMap<Long, PaymentMethodInfo> payMethodInfoMap;
    private HashMap<Long, SalaryInfo> salaryInfoMap;
    private HashMap<Long, SchoolInfo> schoolInfoMap;


    private HashMap<Long, BankBranch> bankMap;
    private HashMap<Long, SalaryType> salaryTypeMap;
    private HashMap<String, SalaryType> salaryTypeMapStr;
    private HashMap<String, NamedEntity> allMdaMap;
    private HashMap<Long, PfaInfo> pfaMap;
    private int pageNumber;
    private int pageLength;
    private int listSize;
    private String sortCriterion;
    private String sortOrder;
    private boolean showUnionDues;
    private List<?> deductionList;
    private List<?> empDeductionBeanList;
    private boolean singleSpecialAllowance;
    private String lastName;
    private String ogNumber;
    private boolean hasErrors;
    private boolean hasHiddenRecords;
    private boolean filterByPfa;
    private boolean cpsReport;
    private boolean filterByMda;
    private boolean tpsReport;
    private boolean useRule;
    private String typeCode;
    private Long salaryTypeInstId;
    private String mdaCode;
    private List<NamedEntityBean> salaryTypeList;
    private List<NamedEntityBean> mdaList;
    private int totalNoOfEmp;
    private WageBeanContainer wageBeanContainer;
    private boolean doNotShowEmpName;
    private boolean doNotShowEmpId;
    private boolean terminatedEmployee;
    private List<MdaInfo> mdaInfoList;

    public PayrollSummaryBean() {
        this.df = new DecimalFormat("#,##0.00");
    }

    public PayrollSummaryBean(List<AbstractPaycheckEntity> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder) {
        this.df = new DecimalFormat("#,##0.00");
        this.listSize = pListSize;
        this.empPayBean = pList;
        this.pageLength = pPageLength;
        this.pageNumber = pPageNumber;
        this.sortCriterion = pSortCriterion;
        this.sortOrder = pSortOrder;
    }


    public String getTotalNetAmountStr() {
        this.totalNetAmountStr = this.df.format(this.totalNetAmount);
        return this.totalNetAmountStr;
    }

    public void setTotalNetAmountStr(String pTotalNetAmountStr) {
        this.totalNetAmountStr = pTotalNetAmountStr;
    }

    public String getTotalTaxesWitheldStr() {
        this.totalTaxesWitheldStr = this.df.format(this.totalTaxesWitheld);
        return this.totalTaxesWitheldStr;
    }

    public String getTotalDeductionsStr() {
        this.totalDeductionsStr = this.df.format(this.totalDeductions);
        return this.totalDeductionsStr;
    }

    public void setTotalDeductionsStr(String pTotalDeductionsStr) {
        this.totalDeductionsStr = pTotalDeductionsStr;
    }

    public String getTotalCompanyContributionsStr() {
        this.totalCompanyContributionsStr = this.df.format(this.totalCompanyContributions);
        return this.totalCompanyContributionsStr;
    }

    public void setTotalCompanyContributionsStr(String pTotalCompanyContributionsStr) {
        this.totalCompanyContributionsStr = pTotalCompanyContributionsStr;
    }

    public String getTotalPayStr() {
        this.totalPayStr = this.df.format(this.totalPay);
        return this.totalPayStr;
    }

    public void setTotalPayStr(String pTotalPayStr) {
        this.totalPayStr = pTotalPayStr;
    }

    public String getTotalGarnishmentsStr() {
        this.totalGarnishmentsStr = this.df.format(this.totalGarnishments);
        return this.totalGarnishmentsStr;
    }

    public void setTotalGarnishmentsStr(String pTotalGarnishmentsStr) {
        this.totalGarnishmentsStr = pTotalGarnishmentsStr;
    }

    public String getTotalCostStr() {
        this.totalCostStr = this.df.format(this.totalCost);
        return this.totalCostStr;
    }


    public String getTotalCommissionPayStr() {
        this.totalCommissionPayStr = this.df.format(getTotalCommissionPay());
        return this.totalCommissionPayStr;
    }

    public String getTotalBonusPayStr() {
        this.totalBonusPayStr = this.df.format(getTotalBonusPay());
        return this.totalBonusPayStr;
    }

    public void setTotalBonusPayStr(String pTotalBonusPayStr) {
        this.totalBonusPayStr = pTotalBonusPayStr;
    }

    public String getTotalOvertimePayStr() {
        this.totalOvertimePayStr = this.df.format(getTotalOvertimePay());
        return this.totalOvertimePayStr;
    }

    public int getFullListSize() {
        return this.listSize;
    }


    public List getList() {
        return this.empPayBean;
    }

    public int getObjectsPerPage() {
        return this.pageLength;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public String getSearchId() {
        return null;
    }

    public String getSortCriterion() {
        return this.sortCriterion;
    }

    public SortOrderEnum getSortDirection() {
        return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
    }


}