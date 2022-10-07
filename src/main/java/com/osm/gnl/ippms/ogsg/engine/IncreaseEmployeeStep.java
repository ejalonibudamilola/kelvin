package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepJobBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
public class IncreaseEmployeeStep
  implements Runnable
{


  private final GenericService genericService;

  private HashMap<Long, HashMap<Integer, Integer>> fSalaryTypeToLevelAndStepMap;
  private List<StepIncreaseBean> fStepIncreaseBean;
  private final int fListSize;
  private boolean fBatch;
  private final int fBatchSize;
  private boolean fStop;
  private ArrayList<StepIncreaseBean> fSaveList;
  private final HashMap<String, Long> fSalaryInfoMap;
  private int fCurrentPercentage;

  private final String fUserName;
  private final StepJobBean fStepJobBean;

  public IncreaseEmployeeStep(GenericService genericService,String pUserName, StepJobBean pStepJobBean, HashMap<Long, HashMap<Integer, Integer>> pSalaryTypeToLevelAndStepMap, List<StepIncreaseBean> pStepIncreaseBean, HashMap<String, Long> pSLSTSMap)
  {
    this.genericService = genericService;
    this.fSalaryTypeToLevelAndStepMap = pSalaryTypeToLevelAndStepMap;
    this.fSalaryInfoMap = pSLSTSMap;
    this.fStepIncreaseBean = pStepIncreaseBean;
    this.fUserName = pUserName;
    this.fStepJobBean = pStepJobBean;
    this.fBatchSize = 500;

    this.fStop = false;
    this.fSaveList = new ArrayList();
    this.fListSize = this.fStepIncreaseBean.size();
  }

  public void run()
  {
    if (this.fListSize > 200)
      this.fBatch = true;
    try {
      for (int i = 0; i < this.fListSize; i++)
      {
        StepIncreaseBean s = this.fStepIncreaseBean.get(i);

        if (this.fSalaryTypeToLevelAndStepMap.containsKey(s.getSalaryTypeInstId()))
        {
          HashMap sMap = this.fSalaryTypeToLevelAndStepMap.get(s.getSalaryTypeInstId());
          if ((sMap != null) && (!sMap.isEmpty())) {
            Integer wBarValue = (Integer)sMap.get(Integer.valueOf(s.getLevel()));
            if (wBarValue == null)
            {
              throw new Exception("Bar Value could not be determined for Salary Level " + s.getLevel() + " of Salary Scale " + s.getSalaryScaleName());
            }

            if (s.getStep() < wBarValue.intValue()) {
              s.setNewStep(s.getStep() + 1);

              String wKey = s.getSalaryTypeInstId() + ":" + s.getLevel() + ":" + s.getNewStep();
              if (!this.fSalaryInfoMap.containsKey(wKey))
              {
                throw new Exception("Could not get Salary Information for Salary Type " + s.getSalaryTypeInstId() + " Level " + s.getLevel() + " Step " + s.getNewStep());
              }
              s.setNewSalaryInfoInstId(this.fSalaryInfoMap.get(wKey));

              this.fSaveList.add(s);
            }
          }

        }

        if (this.fBatch) {
          if ((this.fSaveList.size() >= this.fBatchSize) || (i == this.fListSize - 1)) {
            storeSalaryIncreaseTemp(this.fSaveList);
            this.fSaveList = new ArrayList();
          }
        }
        else storeSingleSalaryIncreaseTemp(s);

        this.fCurrentPercentage += 1;
      }
    }
    catch (Exception wEx) {
      this.fCurrentPercentage = -1;
      log.error("Critical Exception thrown from " + getClass().getSimpleName() + " " + wEx.getMessage());
      wEx.printStackTrace();
    }

    if (!getState())
      updateStepJobBean();
  }

  public boolean isFinished()
  {
    return (this.fCurrentPercentage == this.fListSize) || (this.fCurrentPercentage == -1);
  }

  public int getCurrentRecord()
  {
    return this.fCurrentPercentage;
  }

  public int getTotalRecords() {
    return this.fListSize;
  }

  private void updateStepJobBean() {
    this.fStepJobBean.setLastRunBy(this.fUserName);
    this.fStepJobBean.setEndDate(LocalDate.now());
    this.fStepJobBean.setEndTime(PayrollBeanUtils.getCurrentTime(false));
    this.fStepJobBean.setRunning(0);

    this.genericService.storeObject(this.fStepJobBean);
  }

  private boolean getState()
  {
    return this.fStop;
  }

  public void stop(boolean pStop) {
    this.fStop = pStop;
  }

  private void storeSingleSalaryIncreaseTemp(StepIncreaseBean pS) {
    if (pS.getStep() < pS.getNewStep())
      this.genericService.storeObject(pS);
  }

  private void storeSalaryIncreaseTemp(ArrayList<StepIncreaseBean> pFSaveList)
  {
    this.genericService.storeObjectBatch(pFSaveList);
  }

  public void setSalaryTypeToLevelAndStepMap(HashMap<Long, HashMap<Integer, Integer>> pSalaryTypeToLevelAndStepMap)
  {
    this.fSalaryTypeToLevelAndStepMap = pSalaryTypeToLevelAndStepMap;
  }

  public void setStepIncreaseBean(List<StepIncreaseBean> pStepIncreaseBean) {
    this.fStepIncreaseBean = pStepIncreaseBean;
  }
}