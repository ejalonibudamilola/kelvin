/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.simulation;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.ltg.domain.AbmpBean;
import com.osm.gnl.ippms.ogsg.ltg.domain.LtgMasterBean;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping({"/viewLtgDetails.do"})
@SessionAttributes(types={PaginatedBean.class})
public class LtgSimDetReportController extends BaseController
{
  @Autowired
  HRService hrService;
   
  public LtgSimDetReportController()
  {}

  
@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"lid"})
  public String setupForm(@RequestParam("lid") Long pLid, Model model, HttpServletRequest request)
    throws Exception
  {
	  SessionManagerService.manageSession(request, model);

	  BusinessCertificate bc = this.getBusinessCertificate(request);

    LtgMasterBean l = this.genericService.loadObjectById(LtgMasterBean.class,pLid);

    
	Collection<AbmpBean> empList = this.genericService.loadAllObjectsUsingRestrictions(AbmpBean.class,
            Arrays.asList(CustomPredicate.procurePredicate("ltgMasterBean.id", pLid)), null);

    HashMap<Long, List<Long>> wAbmpBeanIds = getRelatedAbmpBean(empList);

    empList = null;

    List<MdaType> wMdaTypeList = this.genericService.loadControlEntity(MdaType.class);
    ArrayList<AbmpBean> wEmpList = new ArrayList<AbmpBean>();
    
    List<AbmpBean> agencyList = null;
    for(MdaType m : wMdaTypeList) {
    	agencyList = getLtgDetailsByCode(wAbmpBeanIds, m.getId(), bc);
    	wEmpList = getAbmpFromList(agencyList, wEmpList);
    }
     
   
    Collections.sort(wEmpList);

    PaginatedBean wPHDB = new PaginatedBean(wEmpList, wEmpList.size());

    wPHDB.setName(l.getName());

    wPHDB.setId(l.getId());

    model.addAttribute("miniBean", wPHDB);
    addRoleBeanToModel(model, request);

    return "viewLtgSimulationDetailsForm";
  }

  private ArrayList<AbmpBean> getAbmpFromList(List<AbmpBean> pAgencyList, ArrayList<AbmpBean> pEmpList)
  {
    for (AbmpBean a : pAgencyList) {
      pEmpList.add(a);
    }
    return pEmpList;
  }

  private List<AbmpBean> getLtgDetailsByCode(HashMap<Long, List<Long>> pAbmpBeanIds, Long pKey, BusinessCertificate bc)
  {
    List<AbmpBean> wRetList = new ArrayList<AbmpBean>();
    if (pAbmpBeanIds.containsKey(pKey)) {
      wRetList = this.hrService.createMBAPMiniBeanForLTGAllowance(pKey, (ArrayList<Long>)pAbmpBeanIds.get(pKey), bc);
    }
    return wRetList;
  }

  private HashMap<Long, List<Long>> getRelatedAbmpBean(Collection<AbmpBean> pEmpList)
  {
    HashMap<Long, List<Long>> wRetVal = new HashMap<Long, List<Long>>();

    for (AbmpBean a : pEmpList) {
      if (wRetVal.containsKey(a.getMdaInfo().getMdaType().getId())) {
        wRetVal.get(a.getMdaInfo().getMdaType().getId()).add(a.getMdaInfo().getMdaType().getId());
      } else {
        ArrayList<Long> wInnerList = new ArrayList<Long>();
        wInnerList.add(a.getMdaInfo().getId());
        wRetVal.put(a.getMdaInfo().getMdaType().getId(), wInnerList);
      }
    }

    return wRetVal;
  }
}