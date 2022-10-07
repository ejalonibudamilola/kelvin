package com.osm.gnl.ippms.ogsg.controllers.hr.ext;

import java.time.LocalDate;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.MdaService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.service.SchoolService;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PayrollSummaryBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.WageBeanContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PayrollSummaryReportByMDAPandTotalsController extends BaseController
{
  @Autowired
  private PayrollService payrollService;

  @Autowired
  private SchoolService schoolService;

  @Autowired
  private MdaService mdaService;

  @Autowired
  private PaycheckService paycheckService;
 
  public PayrollSummaryReportByMDAPandTotalsController(){}
  

	  
	  @RequestMapping({"/payrollSumDetailsByGLandPayGroupExcel.do"})
	  public void setupForm(@RequestParam("rm") int pRunMonth,
			  @RequestParam("ry") int pRunYear,@RequestParam("tc") String pTypeCode,@RequestParam("mda") String pMdaCode,@RequestParam("stid") Long pStid,
			  Model model, HttpServletRequest request) throws Exception {
	      SessionManagerService.manageSession(request, model);
	      WageBeanContainer wBEOB = new WageBeanContainer();
		  wBEOB.setRunMonth(pRunMonth);
		  wBEOB.setRunYear(pRunYear);

		  BusinessCertificate bc = getBusinessCertificate(request);
		    
	        int wTypeCode = 0;
		    int wLevel = 0;
		    List<Long> wList = null;
		    int wObjectInd = 0;
		    int wMapId = 0;

		    if(pTypeCode != null && !pTypeCode.isEmpty()){
			    StringTokenizer wStrToTokenize = new StringTokenizer(pTypeCode,":");
			    int count = 1;
			    while(wStrToTokenize.hasMoreTokens()){
			    	if(count == 1){
			    		try{
			    			wTypeCode = Integer.parseInt(wStrToTokenize.nextToken());
			    		}catch(Exception wEx){
			    			wEx.printStackTrace();
			    		}
			    	}
			    	if(count == 2){
			    		try{
			    			wLevel = Integer.parseInt(wStrToTokenize.nextToken());
			    		}catch(Exception wEx){
			    			wEx.printStackTrace();
			    		}
			    	}
			    	count++;
			    }
			    
			  

		    }
		    if(wTypeCode > 1)
		    	wLevel = 0;
		    
		    
		    if(pMdaCode != null && !pMdaCode.isEmpty()){
		    	StringTokenizer wStrToTokenize = new StringTokenizer(pMdaCode,":");
			    int count = 1;
			    while(wStrToTokenize.hasMoreTokens()){
			    	if(count == 1){
			    		try{
			    			wMapId = Integer.parseInt(wStrToTokenize.nextToken());
			    		}catch(Exception wEx){
			    			wEx.printStackTrace();
			    		}
			    	}
			    	if(count == 2){
			    		try{
			    			wObjectInd = Integer.parseInt(wStrToTokenize.nextToken());
			    		}catch(Exception wEx){
			    			wEx.printStackTrace();
			    		}
			    	}
			    	  count++;
			    }
			    
		    }
		    
			List<EmployeeType> wETList = this.genericService.loadAllObjectsUsingRestrictions(EmployeeType.class, Arrays.asList(
					CustomPredicate.procurePredicate("employeeTypeCode", wTypeCode), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), null);

		    //This is necessary should in case they make a different Employee Type.
		    if(wETList.size() > 1)
		       wBEOB.setName(PayrollUtils.makeCode(wTypeCode, wLevel, null));
		    else
		    	wBEOB.setName(PayrollUtils.makeCode(wTypeCode, wLevel, wETList.get(0).getName()));
		    
		    
		    if(wMapId > 0){
		    	MdaInfo wMdaInfo = this.genericService.loadObjectWithSingleCondition(MdaInfo.class, CustomPredicate.procurePredicate("id", wMapId));
		    	
		    	wBEOB.setMode(wMdaInfo.getName());
		    	 
		    	wBEOB.setShowMda(true);
		    	if(wMapId > 0){
		    		wList =  mdaService.loadMappedIdsByInnerClassId(wMdaInfo.getId());
				  }
		    	 
		    }
		    if(pStid > 0){
		    	wBEOB.setStaffId((this.genericService.loadObjectById(SalaryType.class, pStid)).getName());
		    	wBEOB.setUsingSalaryType(true);
		    }
		    
		    wBEOB.setEmployeePayBeanList(this.payrollService.loadEmployeePayBeanByParentIdAndLastPayPeriod(pRunMonth,pRunYear,wTypeCode, wList, wObjectInd, pStid, wLevel, bc));
		    wBEOB.setAllowanceStartDateStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));

//	    return new ModelAndView("paySummaryDetailsByGLnPayGroup", "paySumDetByGLnPayGrpBean", wBEOB);
	  }

  @RequestMapping({"/paySumWithTotals.do"})
  public ModelAndView setupForm(@RequestParam("fd") String fD, @RequestParam("td") String tD, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
      SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);
    PayrollSummaryBean p = new PayrollSummaryBean();
    LocalDate fromDate;
    LocalDate toDate;
    fromDate = setDateFromString(fD);
    toDate = setDateFromString(tD);
    List<EmployeePayBean> empBeanList;

    empBeanList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(), fromDate.getMonthValue(), fromDate.getMonthValue(), bc,null,false);
   
    HashMap<Long, SalaryInfo> wSIMap = new HashMap<Long,SalaryInfo>();
    List<SalaryInfo> wList = this.schoolService.loadBasicSalaryInfo();
    wSIMap = setSalaryInfoHashMap(wList);
    p.setSalaryInfoMap(wSIMap);
  

    p.setPayDate(toDate);

      p.setShowUnionDues(PayrollBeanUtils.isUnionDuesDeducted(toDate));

    p.setGenerateBankPvs(false);

    p.setEmployeePayBean(empBeanList);

    return new ModelAndView("paySummaryWithTotals", "mdapSummaryWithTotals", p);
  }

  private LocalDate setDateFromString(String pDateAsString)
  {
    LocalDate cal = LocalDate.now();
    String[] retVal = new String[3];
    if (pDateAsString != null) {
      retVal = StringUtils.tokenizeToStringArray(pDateAsString, "/", true, true);
    }
    if ((retVal != null) && (retVal.length == 3))
      try
      {
        LocalDate.of(Integer.parseInt(retVal[2]), Integer.parseInt(retVal[1]) - 1, Integer.parseInt(retVal[0]));
      }
      catch (Exception ex)
      {
    	  ex.printStackTrace();
      }
    return cal;
  }

  private HashMap<Long, SalaryInfo> setSalaryInfoHashMap(List<SalaryInfo> list)
  {
	  HashMap<Long, SalaryInfo> wRetMap = new HashMap<Long, SalaryInfo>();
    for (SalaryInfo s : list) {
      wRetMap.put(s.getId(), s);
    }
    return wRetMap;
  }
}