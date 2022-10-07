package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.base.services.DeletePaycheckService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.RerunPayrollBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.DeletePayrollBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

@Slf4j
public class DeletePendingPayroll implements Runnable {
	
	 
	private final DeletePaycheckService deletePayrollService;
	
	 
	 
	private boolean fObjectSaved;
	private int fListSize;
	private int fCurrentPercentage;
	
	private boolean wBatch;
	private boolean fStop;
	private boolean finished;
	 
	private final int wBatchSize;
	private final BusinessCertificate businessCertificate;
	private final Vector<AbstractPaycheckEntity> wListToSave;
	private final TransactionTemplate myTransactionTemplate;
	private DeletePayrollBean deletePayrollBean;
	private RerunPayrollBean rerunPayrollBean;
	private String payPeriod;
	private final PayrollRunMasterBean payrollRunMasterBean;

	private String displayMessage;
	
	private long startTime;
	//private long endTime;
	private long timePerBatch;
	private int fRemainingRecords;
	
	
	public DeletePendingPayroll(DeletePaycheckService pDeletePayrollService, Vector<AbstractPaycheckEntity> wLTS,
								TransactionTemplate pTransactionTemplate, DeletePayrollBean pDeletePayrollBean, RerunPayrollBean pRerunBean, PayrollRunMasterBean pPRMB,
								BusinessCertificate pBusinessCertificate) {

		businessCertificate = pBusinessCertificate;
		this.fListSize = wLTS.size();
		this.wBatchSize = 2000;
		fStop = false;
		this.deletePayrollService = pDeletePayrollService;
		 
		this.wListToSave = wLTS;
		 
		this.myTransactionTemplate = pTransactionTemplate;
		this.setDeletePayrollBean(pDeletePayrollBean);
		this.payrollRunMasterBean = pPRMB;
		this.rerunPayrollBean = pRerunBean;
	}
	

	@Override
	public void run() {
		
		//Session wSession = mySessionFactory.openSession();
		//Transaction wTransaction = wSession.beginTransaction();
		myTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				 
				try {
					
					ArrayList <Long> wSaveList = new ArrayList<Long>();
						
					        setDisplayMessage("deleted");
							wBatch = true;

							startTime = System.currentTimeMillis();
							boolean firstBatch = false;
							
							for (int i = 0; i < fListSize; i++) {
							
							if (getState()){
								//wTransaction.rollback();
								throw new RuntimeException();
							}
							

							Long wInt  = wListToSave.get(i).getId();
							if(wInt == null || wInt.equals(0L))
								break;//Do not know how this will ever happen....
							
							 
								
								wSaveList.add(wInt);
								
								if (wSaveList.size() >= wBatchSize || i == fListSize - 1) {
									deletePayrollService.deletePendingPaychecks(wSaveList,businessCertificate);
									wSaveList = new ArrayList<>();
									
									if(!firstBatch)
										timePerBatch = System.currentTimeMillis() - startTime;
									//this.startTime = System.currentTimeMillis();
									firstBatch = true;
									
								}
								
							 
							
							fCurrentPercentage++;
							// Thread.sleep(2000);
							
						}
						wBatch = false;
						
						if(wSaveList != null && !wSaveList.isEmpty()){
							deletePayrollService.deletePendingPaychecks(wSaveList,businessCertificate);
						}
						//here we need to finalize...
						do{
							setDisplayMessage("Finalizing Payroll Deletion....");
							Thread.currentThread();
							Thread.sleep(5100);
							
						}while(deletePayrollService.doesPendingPayrollExist(businessCertificate));
						
				
				fCurrentPercentage = fListSize;
				
				//wTransaction.commit();
				
			} catch (Exception e) {
				//wTransaction.rollback();
				setfCurrentPercentage(-1);
				log.error("Critical Exception thrown from Delete Pending Payroll "+e.getMessage());
				e.printStackTrace();
				throw new RuntimeException();

			}
			
				
			}

			
		});
		
		deletePayrollService.deleteCasacadingObject(payrollRunMasterBean,this.rerunPayrollBean);
		
	}
	private boolean getState(){
		return this.fStop;
	}
    public String getDisplayMessage(){
    	return this.displayMessage;
    }
    public void setDisplayMessage(String pMessage){
    	this.displayMessage = pMessage;
    }
	/**
	 * Gets the current percentage that is done.
	 * @return a percentage or 1111 if something went wrong.
	 */
	/*public int getPercentage() {
		if(fCurrentPercentage == -1)
			return 100;
		if(fCurrentPercentage == 0)
			return fCurrentPercentage;
		int wRetVal = Math.round(((fCurrentPercentage/fListSize) * 100));
		return wRetVal;
	}*/
    
    
    public int getPercentage()
	  {
			if(fCurrentPercentage == -1)
				return 100;
			if(fCurrentPercentage == 0)
				return fCurrentPercentage;
			double wPercent = (((double)fCurrentPercentage/fListSize)) * 100;
			int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
			return wRetVal;
		}
	  
	  /**
		 * Gets the remaining time for payroll to run fully.
		 * @return Time in years, months, days, hours, minutes, seconds
		 */
		public String getTimeToElapse() {
			String wRetVal = "";
			if(fCurrentPercentage == -1)
				return wRetVal;
			if(fCurrentPercentage == 0)
				return wRetVal;
			
			Date currDate = new Date();
			long timeRemainingMillis = (this.getfRemainingRecords() / this.wBatchSize) * this.getTimePerBatch();
			//System.out.println("Time Per Batch: " + this.getTimePerBatch() + " Remaining Records: " + this.getfRemainingRecords() + " Time Remaining Millis: " + timeRemainingMillis);
			Date endDate = new Date(currDate.getTime() + timeRemainingMillis);
			
			wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);
			
			
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
        finished = fCurrentPercentage == fListSize || fCurrentPercentage == -1;
		return finished;
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


	public void setDeletePayrollBean(DeletePayrollBean deletePayrollBean)
	{
		this.deletePayrollBean = deletePayrollBean;
	}


	public DeletePayrollBean getDeletePayrollBean()
	{
		return deletePayrollBean;
	}


	public void setPayPeriod(String payPeriod)
	{
		this.payPeriod = payPeriod;
	}


	public String getPayPeriod()
	{
		return payPeriod;
	}

	public void setTimePerBatch(long timePerBatch) {
		this.timePerBatch = timePerBatch;
	}


	public long getTimePerBatch() {
		return timePerBatch;
	}


	public void setfRemainingRecords(int fRemainingRecords) {
		this.fRemainingRecords = fRemainingRecords;
	}


	public int getfRemainingRecords() {
		fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
		return fRemainingRecords;
	}
	
}
