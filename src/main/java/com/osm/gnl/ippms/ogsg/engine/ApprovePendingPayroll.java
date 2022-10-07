package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.dao.IApprovePayrollDao;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.statistics.domain.MdaPayrollStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ApprovePendingPayroll implements Runnable {
	
	private final IApprovePayrollDao approvePayrollService;
	 
	
	 
	@SuppressWarnings("unused")
	private boolean fObjectSaved;
	private int fListSize;
	private int fCurrentPercentage;
	
	private boolean wBatch;
	private boolean fStop;
	private final int wBatchSize;
	private final List<AbstractGarnishmentEntity> wListToSave;
	private final TransactionTemplate myTransactionTemplate;
	private final LocalDate payDate;
	private final LocalDate payPeriodEnd;
	private final LocalDate payPeriodStart;
	private final String currPayPeriod;
	private final int runMonth;
	private final int runYear;
	private final int devLevy;
	private final BusinessCertificate busCert;
	private final PayrollRunMasterBean payrollRunMasterBean;
	private final List<MdaPayrollStatistics> wStatList;
	
	
	public ApprovePendingPayroll(IApprovePayrollDao pApprovePayrollService, List<AbstractGarnishmentEntity> wLTS, TransactionTemplate pTransactionTemplate,
								 LocalDate pPayDate, LocalDate pPayPeriodEnd, LocalDate pPayPeriodStart, String pCurrPayPeriod, int pRunMonth, int pRunYear, int pDevLevyInd, BusinessCertificate pBc, PayrollRunMasterBean pPRMB, List<MdaPayrollStatistics> pStatList) throws Exception{
		
		this.fListSize = wLTS.size();
		this.wBatchSize = 200;
		fStop = false;
		this.approvePayrollService = pApprovePayrollService;
		 
		this.wListToSave = wLTS;
		 
		this.myTransactionTemplate = pTransactionTemplate;
		payDate = pPayDate;
		payPeriodEnd = pPayPeriodEnd;
		payPeriodStart = pPayPeriodStart;
		currPayPeriod = pCurrPayPeriod;
		runMonth = pRunMonth;
		runYear = pRunYear;
		devLevy = pDevLevyInd;
		busCert = pBc;
		payrollRunMasterBean = pPRMB;
		this.wStatList = pStatList;
		
	}
	

	@Override
	public void run() {
		
		//Session wSession = mySessionFactory.openSession();
		//Transaction wTransaction = wSession.beginTransaction();
		myTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				// TODO Auto-generated method stub
				try {
					
					
					
					List <AbstractGarnishmentEntity> wSaveList = new ArrayList<>();
						
						
						//if ( wList.size() > wBatchSize)
							wBatch = true;

						for (int i = 0; i < fListSize; i++) {
							
							if (getState()){
								//wTransaction.rollback();
								throw new RuntimeException();
							}
							
							
							if (wBatch) {
								
								wSaveList.add(wListToSave.get(i));
								
								if (wSaveList.size() >= wBatchSize || i == fListSize - 1) {
									approvePayrollService.savePendingPayroll(wSaveList);
									wSaveList = new ArrayList<>();
								}
								
							}else {
								approvePayrollService.savePendingPayroll(wListToSave);
							}
							
							fCurrentPercentage++;
							// Thread.sleep(2000);
						}
						wBatch = false;
						
						if(wSaveList != null && !wSaveList.isEmpty()){
							approvePayrollService.savePendingPayroll(wSaveList);
						}
						
						//Now approve all pending paychecks....
						approvePayrollService.savePayrollInformation(runMonth, runYear, payDate, payPeriodEnd, payPeriodStart,  currPayPeriod, devLevy, busCert);
						//Approve This bean as well...
						payrollRunMasterBean.setApprover(new User(busCert.getLoginId()));
						payrollRunMasterBean.setApprovedDate(LocalDate.now());
						payrollRunMasterBean.setPayrollStatus(IConstants.OFF);
						payrollRunMasterBean.setApprovedTime(PayrollBeanUtils.getCurrentTime(false));
						
						approvePayrollService.storeObject(payrollRunMasterBean);
						
						approvePayrollService.saveObjects(wStatList);
				fCurrentPercentage = fListSize;
				
				//wTransaction.commit();
				
			} catch (Exception e) {
				//wTransaction.rollback();
				setfCurrentPercentage(-1);
				log.error(this.getClass().getName()+"Critical Exception thrown from Approving Payroll "+e.getMessage());
				e.printStackTrace();
				throw new RuntimeException();

			}
			
				
			}
		});
		
		//wSession.close();
		
	}
	private boolean getState(){
		return this.fStop;
	}

	/**
	 * Gets the current percentage that is done.
	 * @return a percentage or 1111 if something went wrong.
	 */
	public int getPercentage() {
		if(fCurrentPercentage == -1)
			return 100;
		if(fCurrentPercentage == 0)
			return fCurrentPercentage;
		int wRetVal = Math.round(((fCurrentPercentage/fListSize) * 100));
		return wRetVal;
	}
	public void stop(boolean pStop){
		fStop = pStop;
	}
	public int getCurrentRecord(){
		return fCurrentPercentage;
	}

	public int getTotalRecords(){
		return fListSize;
	}
	public boolean isFinished(){
        return fCurrentPercentage == fListSize || fCurrentPercentage == -1;
    }
	
	 


	public int getfListSize() {
		return fListSize;
	}


	public void setfListSize(int fListSize) {
		this.fListSize = fListSize;
	}


	public int getfCurrentPercentage() {
		return fCurrentPercentage;
	}


	public void setfCurrentPercentage(int fCurrentPercentage) {
		this.fCurrentPercentage = fCurrentPercentage;
	}

	
}
