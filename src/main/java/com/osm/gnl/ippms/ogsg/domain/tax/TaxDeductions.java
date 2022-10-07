package com.osm.gnl.ippms.ogsg.domain.tax;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;

@Getter
@Setter
public class TaxDeductions extends DependentEntity
{
  private static final long serialVersionUID = 1767735158495855115L;
  private EmployeePayBean employeePayBean;
  private double amount;
  private String amountStr;
  private String payDateStr;
  private LocalDate payDate;
  private Employee employee;
  private int businessClientInstId;
  private LocalDate payPeriodStart;
  private LocalDate payPeriodEnd;
  private int runMonth;
  private int runYear;
  private String runMonthStr;
private String employeeId;
private double taxPaid;
private String taxPaidStr;
private String payPeriodStr;

  public int compareTo(TaxDeductions pIncoming)
  {
    if (pIncoming != null)
      return getPayPeriodEnd().compareTo(pIncoming.getPayPeriodEnd());
    return 0;
  }


  public String getAmountStr()
  {
    this.amountStr = (IConstants.naira + new DecimalFormat("#,##0.00##").format(getAmount()));
    return this.amountStr;
  }

  public void setAmountStr(String pAmountStr)
  {
    this.amountStr = pAmountStr;
  }

  public String getPayDateStr()
  {
      this.payDateStr = PayrollHRUtils.getDisplayDateFormat().format(getPayPeriodEnd());
    return this.payDateStr;
  }

public String getTaxPaidStr()
{
	this.taxPaidStr = (IConstants.naira + new DecimalFormat("#,##0.00##").format(this.getTaxPaid()));
	return taxPaidStr;
}

public void setTaxPaidStr(String pTaxPaidStr)
{
	taxPaidStr = pTaxPaidStr;
}

public String getPayPeriodStr()
{
	payPeriodStr = PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(runMonth, runYear);
	return payPeriodStr;
}

public void setPayPeriodStr(String pPayPeriodStr)
{
	payPeriodStr = pPayPeriodStr;
}
}