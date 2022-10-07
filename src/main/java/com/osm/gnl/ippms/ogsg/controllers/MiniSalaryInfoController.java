/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.chart.MiniSalaryInfoDao;
import com.osm.gnl.ippms.ogsg.engine.PersistMiniSalaryInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/createEditMiniSalary.do"})
@SessionAttributes("miniBean")
public class MiniSalaryInfoController extends BaseController{

    private MiniSalaryInfoDao miniSalaryInfoDao;

    private final String VIEW = "configcontrol/miniSalaryForm";

    public MiniSalaryInfoController()
    {}


    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);
        List<SalaryInfo> salaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId() ),null);
        List<MiniSalaryInfoDao> miniSalaryInfoDaos = this.genericService.loadAllObjectsWithSingleCondition(MiniSalaryInfoDao.class,CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId() ),null);
        Map<Long,MiniSalaryInfoDao> wCheckMap = this.breakMiniSalaryInfos(miniSalaryInfoDaos);
        List<MiniSalaryInfoDao> persistList = new ArrayList<>();
        Map<String,NamedEntity> salaryTypeNameList = new HashMap<>();
        for(SalaryInfo s : salaryInfoList){
            if(!wCheckMap.containsKey(s.getId())) {
                persistList.add(this.makeMiniSalaryInfo(s, null));
            }else{
                persistList.add(this.makeMiniSalaryInfo(s,wCheckMap.get(s.getId())));
             }
                if(!salaryTypeNameList.containsKey(s.getSalaryType().getName())) {
                    NamedEntity namedEntity = new NamedEntity();
                    namedEntity.setName(s.getSalaryType().getName());
                    namedEntity.setNoOfEmployees(1);
                    salaryTypeNameList.put(namedEntity.getName(),namedEntity);
                }else{
                    NamedEntity n = salaryTypeNameList.get(s.getSalaryType().getName());
                    n.setNoOfEmployees(n.getNoOfEmployees() + 1);
                }
            }

        miniSalaryInfoDao = new MiniSalaryInfoDao();
        miniSalaryInfoDao.setMiniSalaryInfoDaoList(persistList);
        miniSalaryInfoDao.setSalaryTypeNameList((new ArrayList<>(salaryTypeNameList.values())));
        addRoleBeanToModel(model,request);
        model.addAttribute("miniBean",miniSalaryInfoDao);
        return VIEW;
    }

    @RequestMapping(method={RequestMethod.GET}, params={"maid"})
    public String setupForm(@RequestParam("maid") Integer mid,Model model, HttpServletRequest request)
            throws Exception
    {
        SessionManagerService.manageSession(request, model);

        Object o = getSessionAttribute(request, IConstants.MINI_SAL_KEY);
        if(mid == null || o == null)
            return "redirect:createEditMiniSalary.do";

        miniSalaryInfoDao = ((PersistMiniSalaryInfo)o).getMiniSalaryInfoDao();
        removeSessionAttribute(request,IConstants.MINI_SAL_KEY);
        addRoleBeanToModel(model,request);
        model.addAttribute("actionCompleted", "Mini Salary Information created successfully.");
        model.addAttribute("saved",true);
        model.addAttribute("miniBean",miniSalaryInfoDao);
        return VIEW;
    }
    @RequestMapping(method={RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") MiniSalaryInfoDao miniSalaryInfoDao, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
           return CONFIG_HOME_URL;

        PersistMiniSalaryInfo persistSalaryInfo = new PersistMiniSalaryInfo(genericService, miniSalaryInfoDao, bc);
        addSessionAttribute(request, IConstants.MINI_SAL_KEY, persistSalaryInfo);

        Thread thread = new Thread(persistSalaryInfo);
        thread.start();

        return "redirect:miniSalaryInfoStatus.do";
    }

    private Map<Long, MiniSalaryInfoDao> breakMiniSalaryInfos(List<MiniSalaryInfoDao> miniSalaryInfoDaos) {
        Map<Long,MiniSalaryInfoDao> wRetMap = new HashMap<>();
        for(MiniSalaryInfoDao m : miniSalaryInfoDaos)
            wRetMap.put(m.getSalaryInfoId(),m);
        return wRetMap;
    }

    private MiniSalaryInfoDao makeMiniSalaryInfo(SalaryInfo s, MiniSalaryInfoDao m) {
         if(m == null) {
             miniSalaryInfoDao = new MiniSalaryInfoDao();
             miniSalaryInfoDao.setSalaryInfoId(s.getId());
         }else{
             miniSalaryInfoDao = m;
         }
         miniSalaryInfoDao.setAnnualAllowance(s.getConsolidatedAllowance());
         miniSalaryInfoDao.setAnnualBasic(s.getMonthlyBasicSalary());
         miniSalaryInfoDao.setAnnualGross(s.getAnnualSalary());
         miniSalaryInfoDao.setMonthlyAllowance(s.getConsolidatedMonthlyAllowance());
         miniSalaryInfoDao.setMonthlyBasic(s.getBasicMonthlySalary());
         miniSalaryInfoDao.setMonthlyGross(s.getMonthlyGrossSalary());
         miniSalaryInfoDao.setBusinessClientId(s.getBusinessClientId());
         miniSalaryInfoDao.setLevel(s.getLevel());
         miniSalaryInfoDao.setStep(s.getStep());
         miniSalaryInfoDao.setSalaryTypeName(s.getSalaryType().getName());
         return miniSalaryInfoDao;
    }


}
