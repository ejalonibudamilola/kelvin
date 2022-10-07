/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.engine;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.base.services.HrServiceHelper;
import com.osm.gnl.ippms.ogsg.domain.employee.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.utils.BiometricsUtil;
import com.osm.gnl.ippms.ogsg.utils.RestCall;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Slf4j
public class VerifyAllBiometrics implements Runnable{
     private final GenericService genericService;
    private final HrServiceHelper hrServiceHelper;
    private final String apiUserName;
    private final String apiPassword;
    private final RestCall restCall;

    private boolean objectSaved;
    private int listSize;
    private int currentPercentage;
    private final BusinessCertificate businessCertificate;
    private int runMonth;
    private int runYear;
    private boolean stop;
    private int batchSize;
    private int firstTime = -1;
    private List<BiometricInfo> wSaveList;
    private boolean rerun;
    private boolean deleted;
    private Long mdaId;
    private long startTime;
    //private long endTime;
    private long timePerBatch;
    private int fRemainingRecords;
    private MdaInfo mdaInfo;



    public VerifyAllBiometrics(GenericService genericService, HrServiceHelper hrServiceHelper, String apiUserName, String apiPassword, RestCall restCall, BusinessCertificate businessCertificate, int pFullListSize, MdaInfo mdaInfo) {
        this.genericService = genericService;
        this.hrServiceHelper = hrServiceHelper;
        this.apiUserName = apiUserName;
        this.apiPassword = apiPassword;
        this.restCall = restCall;

        this.businessCertificate = businessCertificate;
        this.listSize = pFullListSize;
        this.batchSize = 10;
        this.stop = false;
        this.wSaveList = new ArrayList<>();


        this.mdaInfo = mdaInfo;



    }

    @SneakyThrows
    public void run() {
        try {
                if(this.batchSize > this.listSize)
                    this.batchSize = this.listSize;
                startTime = System.currentTimeMillis();
                boolean firstBatch = false;


                    List<BiometricInfo> wList = findAllEmployees(this.mdaInfo);

                    int wListSize = wList.size();

                    for (int i = 0; (i < wList.size()) &&
                            (!getState()); i++) {
                        if(this.getState())
                            break;
                        BiometricInfo b = wList.get(i);
                        b = BiometricsUtil.validateEmployee(b,apiUserName,apiPassword,restCall);

                        if(b.isResponse()) {
                            b.setBusinessClientId(this.businessCertificate.getBusinessClientInstId());
                            this.wSaveList.add(b);
                            if ((this.wSaveList.size() >= this.batchSize) || (i == wListSize - 1)) {
                                saveBiometricInfo(this.wSaveList);
                                this.wSaveList = new ArrayList<>();

                                if (!firstBatch)
                                    this.timePerBatch = System.currentTimeMillis() - this.startTime;
                                this.startTime = System.currentTimeMillis();
                                firstBatch = true;

                            }
                        }else{
                            this.writeErrorRecord(b);
                        }

                        this.currentPercentage += 1;
                    }



            this.currentPercentage = this.listSize;
        } catch (Exception e) {
            this.currentPercentage = -1;
            e.printStackTrace();

        }
    }

    private void writeErrorRecord(BiometricInfo b) {

        BiometricInfoError biometricInfoError = new BiometricInfoError();
        biometricInfoError.setEmployeeInstId(b.getParentId());
        biometricInfoError.setEmployeeId(b.getEmployeeId());
        biometricInfoError.setFirstName(b.getFirstName());
        biometricInfoError.setLastName(b.getLastName());
        biometricInfoError.setBusinessClientId(getBusinessCertificate().getBusinessClientInstId());
        this.genericService.storeObject(b);
    }

    private void saveBiometricInfo(List<BiometricInfo> wSaveList) throws InstantiationException, IllegalAccessException {
        Long parentId;
        Long biometricId;
        Long bizId = getBusinessCertificate().getBusinessClientInstId();
        User user = new User(getBusinessCertificate().getLoginId());
        for(BiometricInfo b : wSaveList) {
            parentId = b.getParentId();
            b.setBusinessClientId(bizId);
            b.setVerifiedBy(user);
            b.setLastModTs(Timestamp.from(Instant.now()));
            //First Save the Picture
            try{
                savePicture(parentId,b);
                biometricId = this.genericService.storeObject(b);
                updateEmployee(parentId, biometricId);
            }catch(Exception wEx){
                //Eat it for now
                wEx.printStackTrace();
            }



        }

    }

    private void savePicture(Long parentId, BiometricInfo b) throws InstantiationException, IllegalAccessException {

        HrPassportInfo hrPassportInfo = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class, CustomPredicate.procurePredicate(getBusinessCertificate().getEmployeeIdJoinStr(), parentId ));
        if(hrPassportInfo.isNewEntity()){
            //Blob blob = Hibernate.createBlob(file.getInputStream());
            hrPassportInfo.setPhoto(b.getProfilePicture());
            // pHrmPassport.setPhoto(blob);
            hrPassportInfo.setPhotoType(b.getPhotoType());
            if(getBusinessCertificate().isPensioner())
                hrPassportInfo.setPensioner(new Pensioner(parentId));
            else
                hrPassportInfo.setEmployee(new Employee(parentId));

            hrPassportInfo.setCreatedBy(new User(getBusinessCertificate().getLoginId()));

        }
        else if(  !hrPassportInfo.isNewEntity() ) {

            hrPassportInfo.setPhoto(b.getProfilePicture());
            hrPassportInfo.setPhotoType(b.getPhotoType());
            if(getBusinessCertificate().isPensioner())
                hrPassportInfo.setPensioner(new Pensioner(parentId));
            else
                hrPassportInfo.setEmployee(new Employee(parentId));

        }
        hrPassportInfo.setLastModBy(new User(getBusinessCertificate().getLoginId()));
        hrPassportInfo.setLastModTs(Timestamp.from(Instant.now()));
        hrPassportInfo.setBusinessClientId(getBusinessCertificate().getBusinessClientInstId());
        this.genericService.saveObject(hrPassportInfo);
    }

    private void updateEmployee(Long parentId, Long biometricId) throws IllegalAccessException, InstantiationException {
        Employee e = this.genericService.loadObjectById(Employee.class,parentId);
        e.setBiometricInfo(new BiometricInfo(biometricId));
        this.genericService.storeObject(e);
    }


    private boolean getState() {
        return this.stop;
    }


    /**
     * Gets the current percentage that is done.
     *
     * @return a percentage or 100 if something went wrong.
     */
    public int getPercentage() {
        if (currentPercentage == -1)
            return 100;
        if (currentPercentage == 0)
            return currentPercentage;
        double wPercent = (((double) currentPercentage / listSize)) * 100;
        int wRetVal = EntityUtils.convertDoubleToEpmStandardZeroDecimal(wPercent);
        return wRetVal;
    }

    /**
     * Gets the remaining time for payroll to run fully.
     *
     * @return Time in years, months, days, hours, minutes, seconds
     */
    public String getTimeToElapse() {
        String wRetVal = "";
        if (currentPercentage == -1)
            return wRetVal;
        if (currentPercentage == 0)
            return wRetVal;

        Date currDate = new Date();
        long timeRemainingMillis = (this.getfRemainingRecords() / this.batchSize) * this.getTimePerBatch();
        Date endDate = new Date(currDate.getTime() + timeRemainingMillis);

        wRetVal = PayrollBeanUtils.getMeasuredTimeFromDates(currDate, endDate);


        return wRetVal;


    }



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




    private synchronized List<BiometricInfo> findAllEmployees(MdaInfo pMdaInfo ) {

        return this.hrServiceHelper.loadEmp4BiometricVerification(businessCertificate, pMdaInfo);
    }


    public void setTimePerBatch(long timePerBatch) {
        this.timePerBatch = timePerBatch;
    }


    public void setfRemainingRecords(int fRemainingRecords) {
        this.fRemainingRecords = fRemainingRecords;
    }


    public int getfRemainingRecords() {
        fRemainingRecords = this.getTotalRecords() - this.getCurrentRecord();
        return fRemainingRecords;
    }

}
