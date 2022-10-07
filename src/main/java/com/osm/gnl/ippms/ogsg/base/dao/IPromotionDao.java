/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.base.dao;

import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.domain.promotion.StepIncreaseBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface IPromotionDao {

    boolean employeeHasPromotionHistory(BusinessCertificate bc,final Long pEmpId);

    int getToBePromotedEmpCount(final Long pBizId);

    List<PromotionTracker> getToBePromotedEmployeeList(BusinessCertificate businessCertificate);

    HashMap<Long, HashMap<Integer, Integer>> makeSalaryTypeToLevelAndStepMap(Long bizClientId);


    List<StepIncreaseBean> getStepIncreasableEmployees(BusinessCertificate businessCertificate, Long MdaId);

    HashMap<String, Long> makeSalaryTypeLevelStepToSalaryInfoMap(Long pBizId);

    List<SalaryInfo> loadSalaryInfoBySalaryTypeAndRank(Long pSalaryTypeId, int pStep, Rank pRankInfo, Long businessClientInstId);

    List<SalaryInfo> loadSalaryInfoByForPromotion(Long pSalaryTypeId,int pLevel, int pStep, Long businessClientInstId);

    void promoteSingleEmployee(Long pEmpId, Long pSalaryInfoId, BusinessCertificate bc, Rank rank);

    void updateHiringInfoForPromotion(Long hiringInfoId, LocalDate wNextPromotionDate, BusinessCertificate bc);

    @Transactional()
    void updateEmployeesSalarySteps(List<StepIncreaseBean> pSIBList, BusinessCertificate bc);

    int countNoOfActiveFlaggedPromotions(Long businessClientInstId);
}
