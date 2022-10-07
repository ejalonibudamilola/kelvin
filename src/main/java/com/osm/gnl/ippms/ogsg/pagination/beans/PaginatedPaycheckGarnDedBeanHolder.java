package com.osm.gnl.ippms.ogsg.pagination.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.approval.TransferApproval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PaginatedPaycheckGarnDedBeanHolder extends NamedEntityLong
  implements PaginatedList
{
	private static final long serialVersionUID = -6917612382548922653L;
	private List<?> beanList;
	private List<?> salaryTypeList;
	private List<?> salaryStructureList;
	private List<?> paginationListHolder;
	private List<?> successList;
	private List<Long> employeeIdList;
	private Object someObject;
	private Long mdaId;
	private Long schoolId;
	private Long departmentId;
	private int pageNumber;
	private HashMap<String, String> bankMap;
	private int pageLength;
	private int listSize;
	private String sortCriterion;
	private String sortOrder;
	private double originalLoanAmount;
	private double paidLoanAmount;
	private double loanDifference;
	private String originalLoanAmountStr;
	private String paidLoanAmountStr;
	private String loanDifferenceStr;
	private String originalLoanAmountStrSansNaira;
	private String paidLoanAmountStrSansNaira;
	private String loanDifferenceStrSansNaira;
	private Long objectId;
	private int objectInd;
	private String garnishmentName;
	private String deductionName;
	private DecimalFormat df;
	private String birthDate;
	private String hireDate;
	private String terminationReason;
	private String terminationDate;
	private int noOfYearsInService;
	private boolean terminatedEmployee;
	private String currentLevelAndStep;
	private String confirmDate;
	private boolean emptyList;
	private String staffId;
	private Long salaryTypeId;
	private Long salaryStructureId;
	private boolean promotion;
	private boolean transfer;
	private boolean loan;
	private boolean specialAllowance;
	private boolean deduction;
	private boolean emailPayslips;
    private boolean canDoPromotion;
    private boolean canDoTransfer;
    private boolean canDoLoan;
    private boolean canDoSpecialAllow;
    private boolean canDoDeduction;
    private boolean canEmailPayslips;
	private boolean noRecords;
	private int typeInd;
	private int loanTerm;
	private String owedAmountStr;
	private Integer mappedObjectInstId;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long payTypeInstId;
	private String subLinkSelect;
	private double taxPaid;
	private String taxPaidStr;
	private LocalDate fromDate;
	private LocalDate toDate;
	private String fromDateStr;
	private String toDateStr;
	private String employeeId;
	private int empId;
	private List<TransferLog> transferLogList;
	private double currentLoanAmount;
	private String currentLoanAmountStr;
	private String currentLoanAmountStrSansNaira;
	private boolean showTotalLoanTypeRow;
	private double currentOriginalLoanAmount;
	private String currentOriginalLoanAmountStr;
	private String currentOriginalLoanAmountStrSansNaira;
	private List<TransferApproval> transferApprovalsList;
	private int recordsPerPage;
	private Integer salaryArrearsInstId;
	private int schoolTransfer;
	private String showArrearsRow;
	private String amountStr;
	private String payArrearsInd;
	private String newSalaryScaleLevelAndStepStr;
	private boolean payingArrears;
	private HashMap<Long,TransferApproval> transferApprovalMap;
	private HashMap<Long, TransferLog> transferLogMap;
	private HashMap<Long,String> oldMdaMap;
	private String massAttrName;
	private boolean rejectWarningIssued;
	private int highestLevel;
	private int highestStep;
	private int lowestLevel;
	private int lowestStep;
	private List<MdaInfo> mdaList;
	private boolean dedGarnOrSpecAllow;
	private boolean firstRecord;
	private boolean incrementWarningIssued;
    private String showRow;
    private String companyName;
    private String monthlyPensionStr;
    private int activeInd;


    public PaginatedPaycheckGarnDedBeanHolder(List<?> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion, String pSortOrder)
  {
    this.pageLength = pPageLength;
    this.pageNumber = pPageNumber;
    this.beanList = pList;
    this.listSize = pListSize;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;

    this.df = new DecimalFormat("#,##0.00");
  }
  public PaginatedPaycheckGarnDedBeanHolder(List<?> pList, int pPageNumber, int pPageLength, int pListSize, String pSortCriterion,
                                            String pSortOrder, int pHlevel, int pLLevel, int pHStep, int pLStep)
  {
    this.pageLength = pPageLength;
    this.pageNumber = pPageNumber;
    this.beanList = pList;
    this.listSize = pListSize;
    this.sortCriterion = pSortCriterion;
    this.sortOrder = pSortOrder;
    this.highestLevel = pHlevel;
    this.lowestLevel = pLLevel;
    this.highestStep = pHStep;
    this.lowestStep = pLStep;
    
    this.df = new DecimalFormat("#,##0.00");
  }

  public PaginatedPaycheckGarnDedBeanHolder(List<?> pBeanList)
  {
    this.beanList = pBeanList;
    this.df = new DecimalFormat("#,##0.00");
  }


  public int getFullListSize()
  {
    return this.listSize;
  }

  public List<?> getList()
  {
    return this.beanList;
  }

  public int getObjectsPerPage()
  {
    return this.pageLength;
  }

  public int getPageNumber()
  {
    return this.pageNumber;
  }

  public String getSearchId() {
    return null;
  }

  public String getSortCriterion()
  {
    return this.sortCriterion;
  }

  public SortOrderEnum getSortDirection()
  {
    if (this.sortOrder == null)
      return SortOrderEnum.ASCENDING;
    return this.sortOrder.equals("asc") ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
  }


  public double getLoanDifference()
  {
    this.loanDifference = (getOriginalLoanAmount() - getPaidLoanAmount());
    return this.loanDifference;
  }

  public String getOriginalLoanAmountStr()
  {
    this.originalLoanAmountStr = (naira + this.df.format(getOriginalLoanAmount()));
    return this.originalLoanAmountStr;
  }

  public String getPaidLoanAmountStr()
  {
    this.paidLoanAmountStr = (naira + this.df.format(getPaidLoanAmount()));
    return this.paidLoanAmountStr;
  }

  public String getLoanDifferenceStr()
  {
    this.loanDifferenceStr = (naira + this.df.format(currentLoanAmount));
    return this.loanDifferenceStr;
  }

  public String getOriginalLoanAmountStrSansNaira()
  {
    this.originalLoanAmountStrSansNaira = this.df.format(getOriginalLoanAmount());
    return this.originalLoanAmountStrSansNaira;
  }

  public String getPaidLoanAmountStrSansNaira()
  {
    this.paidLoanAmountStrSansNaira = this.df.format(getPaidLoanAmount());
    return this.paidLoanAmountStrSansNaira;
  }

  public String getLoanDifferenceStrSansNaira()
  {
    this.loanDifferenceStrSansNaira = this.df.format(getLoanDifference());
    return this.loanDifferenceStrSansNaira;
  }


  public boolean isEmptyList()
  {
    this.emptyList = getList().isEmpty();
    return this.emptyList;
  }


  public boolean isPromotion()
  {
    this.promotion = (1 == getTypeInd());
    return this.promotion;
  }

  public boolean isTransfer()
  {
    this.transfer = (4 == getTypeInd());

    return this.transfer;
  }

  public boolean isLoan()
  {
    this.loan = (2 == getTypeInd());
    return this.loan;
  }

  public boolean isSpecialAllowance()
  {
    this.specialAllowance = (5 == getTypeInd());
    return this.specialAllowance;
  }

  public boolean isDeduction()
  {
    this.deduction = (3 == getTypeInd());
    return this.deduction;
  }

    public boolean isEmailPayslips() {
      this.emailPayslips = (typeInd == 7);
        return emailPayslips;
    }

    public boolean isNoRecords()
  {
    this.noRecords = (getTypeInd() == 0);
    return this.noRecords;
  }

public String getTaxPaidStr()
{
	this.taxPaidStr = (naira + this.df.format(getTaxPaid()));
	return this.taxPaidStr;
}



public List<TransferLog> getTransferLogList()
{
	if(this.transferLogList == null)
		transferLogList = new ArrayList<TransferLog>();
	return transferLogList;
}


public String getCurrentLoanAmountStr()
{
	 this.currentLoanAmountStr = (naira + this.df.format(this.getCurrentLoanAmount()));
	return currentLoanAmountStr;
}

public String getCurrentLoanAmountStrSansNaira()
{
	this.currentLoanAmountStrSansNaira = (this.df.format(this.getCurrentLoanAmount()));
	return currentLoanAmountStrSansNaira;
}


public String getCurrentOriginalLoanAmountStr()
{
	currentOriginalLoanAmountStr = naira+df.format(this.currentOriginalLoanAmount);
	return currentOriginalLoanAmountStr;
}

public String getCurrentOriginalLoanAmountStrSansNaira()
{
	currentOriginalLoanAmountStrSansNaira =  df.format(this.currentOriginalLoanAmount);
	return currentOriginalLoanAmountStrSansNaira;
}

public List<TransferApproval> getTransferApprovalsList()
{
	 if(this.transferApprovalsList == null)
		 transferApprovalsList = new ArrayList<>();
	return transferApprovalsList;
}

public HashMap<Long,TransferApproval> getTransferApprovalMap()
{
	if(transferApprovalMap == null)
		transferApprovalMap = new HashMap<>();
	return transferApprovalMap;
}

public HashMap<Long, TransferLog> getTransferLogMap()
{
	if(transferLogMap == null)
		transferLogMap = new HashMap<>();
	return transferLogMap;
}


}