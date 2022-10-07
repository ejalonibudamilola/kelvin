package com.osm.gnl.ippms.ogsg.engine;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.base.services.SimulationService;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.domain.simulation.SimulationPaycheckBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@Slf4j
public class SimulatePayrollWithLtg
  implements Runnable
{
  private final HRService hrService;
  List<SimulationPaycheckBean> listToPay;
  private final CalculatePayPerABMPSimulator fCalcPayPerEmployee;
  private int fCurrentPercentage;
  private boolean finished;
  int wListSize;
  private long timePerBatch;
  private int fRemainingRecords;
  private int currentPercentage;
  private int fListSize;
  private long startTime;
  private boolean wBatch;
  private boolean fStop;
  private int batchSize;
  private LocalDate startDate;
  private final Long fLtgMasterInstId;
  List<SimulationPaycheckBean> wSaveList;
  private BusinessCertificate businessCertificate;
  private final int runMonth;
  private final int runYear;
  private SimulationService simulationService;
  private String payPeriodStr;


  public SimulatePayrollWithLtg(SimulationService simulationService1,HRService hrService, CalculatePayPerABMPSimulator pCalcPayPerEmployee,
                                Long pLtgMasterInstId, BusinessCertificate pBc, int pRunMonth, int pRunYear)
  {
    this.hrService = hrService;
    this.fCalcPayPerEmployee = pCalcPayPerEmployee;
    this.fLtgMasterInstId = pLtgMasterInstId;
    this.batchSize = 500;
    this.fStop = false;
    this.wSaveList = new ArrayList();
    this.businessCertificate = pBc;
    this.runMonth = pRunMonth;
    this.runYear = pRunYear;
    this.simulationService = simulationService1;
  }

  public void run()
  {
    this.fListSize = getTotalNumberOfActiveEmployees();
   
    this.fCalcPayPerEmployee.setNoOfDays(PayrollBeanUtils.getNoOfDays(this.runMonth, this.runYear));
    if (this.fListSize > this.batchSize) {
      this.wBatch = true;
    }
    

    //-- Convert this dude to a Map of the Values first.
    
    this.fCalcPayPerEmployee.setfAgencyMap(simulationService.getObjectsToApplyLtg(this.fLtgMasterInstId));
    
     List<MdaType> wMdaTypes = this.hrService.getGenericService().loadControlEntity(MdaType.class);
     
     LocalDate wStartDate = PayrollBeanUtils.getDateFromMonthAndYear(runMonth, runYear, false);
     LocalDate wEndDate = PayrollBeanUtils.getDateFromMonthAndYear(runMonth, runYear, true);
     this.startDate = wStartDate;
    try
    {
      boolean firstBatch = false;
      for ( MdaType m : wMdaTypes )
         
      {
    	  if(getState()) break;
        this.listToPay = simulationService.getPayableEmployeesByDesignation(m.getId(),this.fLtgMasterInstId, this.businessCertificate, wStartDate, wEndDate);

        if ((this.listToPay == null) || (this.listToPay.size() <= 0))
          continue;
        int j = 1;
        for (SimulationPaycheckBean wEPB :  this.listToPay)
        {
        	j++;
          if (getState()) {
            break;
          }
          wEPB  = this.fCalcPayPerEmployee.calculatePayroll(wEPB);

          this.fCurrentPercentage += 1;

          if (this.wBatch) {
            this.wSaveList.add(wEPB);
            if ((this.wSaveList.size() >= this.fListSize) || (j == this.fListSize - 1)) {
              saveCalculatedPayroll(this.wSaveList);
              this.wSaveList = new ArrayList();
              if (!firstBatch)
                this.timePerBatch = System.currentTimeMillis() - this.startTime;
              firstBatch = true;
            }
          } else {
            saveCalculatedPayroll(wEPB);
          }

        }

        this.listToPay = new ArrayList();
      }

      if (!this.wSaveList.isEmpty()) {
        saveCalculatedPayroll(this.wSaveList);
        this.wSaveList = null;
      }
      Thread.sleep(200L);
      this.finished = true;
    }
    catch (Exception e)
    {
      this.fCurrentPercentage = -1;
      this.finished = true;
      e.printStackTrace();
    }
  }

  private int getTotalNumberOfActiveEmployees() {
      return hrService.getGenericService().countObjectsUsingPredicateBuilder
          (new PredicateBuilder().addPredicate(CustomPredicate.procurePredicate("statusIndicator", 0)).addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId()))
                  , IppmsUtils.getEmployeeClass(businessCertificate));
  }



  private boolean getState()
  {
    return this.fStop;
  }

  public int getPercentage()
  {
    if (currentPercentage == -1)
      return 100;
    if (currentPercentage == 0)
      return currentPercentage;
    double wPercent = (((double) currentPercentage / fListSize)) * 100;
    int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
    return wRetVal;
  }
  public void stop(boolean pStop) {
    this.fStop = pStop;
  }
  public int getCurrentRecord() {
    return this.fCurrentPercentage;
  }

  public int getTotalRecords() {
    return this.fListSize;
  }
  public boolean isFinished() {
      return fCurrentPercentage == fListSize || fCurrentPercentage == -1;
  }

  private synchronized void saveCalculatedPayroll(SimulationPaycheckBean pWepb)
  {
    
    this.hrService.getGenericService().saveObject(pWepb);
  }

  private synchronized void saveCalculatedPayroll(List<SimulationPaycheckBean> pEmpPayBeanList) {
	  
    if (!pEmpPayBeanList.isEmpty())
    {
    	this.hrService.getGenericService().storeObjectBatch(pEmpPayBeanList);
    	 
    }
  }


  public String getPayPeriodStr() {
    if(this.startDate != null)
      this.payPeriodStr = startDate.getMonth().name()+" : "+startDate.getYear();
    return  this.payPeriodStr;
  }

  public String getTimeToElapse() {
    String wRetVal = "";
    if (currentPercentage == -1)
      return wRetVal;
    if (currentPercentage == 0)
      return wRetVal;

    Date currDate = new Date();
    long timeRemainingMillis = (this.getfRemainingRecords() / this.batchSize) * this.getTimePerBatch();
    //System.out.println("Time Per Batch: " + this.getTimePerBatch() + " Remaining Records: " + this.getfRemainingRecords() + " Time Remaining Millis: " + timeRemainingMillis);
    Date endDate = new Date(currDate.getTime() + timeRemainingMillis);

    wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);


    return wRetVal;


  }
  public int getfRemainingRecords() {
    fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
    return fRemainingRecords;
  }
}