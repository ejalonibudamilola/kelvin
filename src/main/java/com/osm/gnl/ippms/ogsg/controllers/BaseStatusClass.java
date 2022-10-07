/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;

import java.util.Date;

public abstract class BaseStatusClass {
    protected long startTime;
    protected long timePerBatch;
    protected int fRemainingRecords;
    protected boolean stop;
    protected int batchSize;
    protected int listSize;
    protected int currentPercentage;
    protected boolean firstBatch;

 
    public void stop(boolean pStop) {
        this.stop = pStop;
    }

    
    public int getCurrentRecord() {
        return this.currentPercentage;
    }

    
    public int getTotalRecords() {
        return this.listSize;
    }

    
    public boolean isFinished() {
        return (this.currentPercentage == this.listSize) || (this.currentPercentage == -1);
    }

    
    public int getfRemainingRecords() {
        fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
        return fRemainingRecords;
    }

    
    
    public boolean getState() {
        return this.stop;
    }

    
    public int getPercentage() {
        if (currentPercentage == -1)
            return 100;
        if (currentPercentage == 0)
            return currentPercentage;
        double wPercent = (((double) currentPercentage / listSize)) * 100;
        int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
        return wRetVal;
    }

    
    public String getTimeToElapse() {
        String wRetVal = "";
        if (currentPercentage == -1)
            return wRetVal;
        if (currentPercentage == 0)
            return wRetVal;

        Date currDate = new Date();
        long timeRemainingMillis = (this.getfRemainingRecords() / this.batchSize) * this.timePerBatch;
        //System.out.println("Time Per Batch: " + this.getTimePerBatch() + " Remaining Records: " + this.getfRemainingRecords() + " Time Remaining Millis: " + timeRemainingMillis);
        Date endDate = new Date(currDate.getTime() + timeRemainingMillis);

        wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);


        return wRetVal;
    }
    public void setTimePerBatch(long timePerBatch) {
        this.timePerBatch = timePerBatch;
    }


    public void setfRemainingRecords(int fRemainingRecords) {
        this.fRemainingRecords = fRemainingRecords;
    }

 
}
