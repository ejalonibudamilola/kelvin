package com.osm.gnl.ippms.ogsg.controllers.ajax;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.MdaService;
import com.osm.gnl.ippms.ogsg.base.services.PayrollService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.Cadre;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.*;


@Controller
public class MultiActionAjaxController extends BaseController
{

    @Autowired
    private MdaService mdaService;
    @Autowired
    private PayrollService payrollService;




  public MultiActionAjaxController()
  {}

    @RequestMapping({ "/getRanksByBusinessClient.do" })
    public void getRanksBusinessClientId(@RequestParam("parentClientId") Long pBusClientId, OutputStream pOutputStream,
                                HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<?> wReturnList = null;
            StringBuilder wRetList = new StringBuilder();
            wReturnList = this.genericService.loadAllObjectsWithSingleCondition(Rank.class,CustomPredicate.procurePredicate("businessClientId", pBusClientId), "name");
            if (wReturnList != null)
                for (Object h : wReturnList)
                    wRetList.append("<option value=\"").append(((Rank) h).getId()).append("\" ")
                            .append("title=\"").append(((Rank) h).getDescription()).append("\">")
                            .append(((Rank) h).getName().trim()).append("</option>");
            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        } catch (Exception wEx) {
            wEx.printStackTrace();
            System.out.println("Exception Thrown from getRanksBusinessClientId.do() " + wEx.getMessage());
            System.out.println(wEx.getCause());
        }
    }
    @RequestMapping({ "/getStatesForCity.do" })
    public void getStatesForCity(@RequestParam("cityId") Long pCityId, OutputStream pOutputStream,
                                HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<City> wReturnList = new ArrayList<>();
            StringBuilder wRetList = new StringBuilder();
            City city = this.genericService.loadObjectById(City.class,pCityId);
            if(!city.isNewEntity())
                wReturnList.add(city);

            if (!wReturnList.isEmpty())
                for (Object h : wReturnList)
                    wRetList.append("<option value=\"").append(((City) h).getState().getId()).append("\" ")
                            .append("title=\"").append(((City) h).getState().getFullName()).append("\">")
                            .append(((City) h).getState().getName().trim()).append("</option>");
            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        } catch (Exception wEx) {
            wEx.printStackTrace();
            System.out.println("Exception Thrown from getStatesForCity.do() " + wEx.getMessage());
            System.out.println(wEx.getCause());
        }
    }
    @RequestMapping({ "/getSpecAllowTypePayType.do" })
    public void loadPayTypeForSpecAllow(@RequestParam("satId") Long pSatId, OutputStream pOutputStream,
                                 HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<PayTypes> wReturnList = new ArrayList<>();
            StringBuilder wRetList = new StringBuilder();
            SpecialAllowanceType specialAllowanceType = this.genericService.loadObjectById(SpecialAllowanceType.class,pSatId);
            if(specialAllowanceType.isNewEntity()) {
                wReturnList.addAll(genericService.loadAllObjectsWithSingleCondition(PayTypes.class, CustomPredicate.procurePredicate("selectableInd", 0), null));
            }else{
                wReturnList.add(specialAllowanceType.getPayTypes());
            }
            if (!wReturnList.isEmpty())
                for (PayTypes h : wReturnList)
                    wRetList.append("<option value=\"").append(h.getId()).append("\" ")
                            .append("title=\"").append(h.getCodeName()).append("\">")
                            .append(h.getName()).append("</option>");
            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        } catch (Exception wEx) {
            wEx.printStackTrace();
            System.out.println("Exception Thrown from loadPayTypeForSpecAllow.do() " + wEx.getMessage());
            System.out.println(wEx.getCause());
        }
    }

    @RequestMapping({ "/getSalaryInfoByRankId.do" })
    public void getSalaryInfoByRankId(@RequestParam("rankInstId") Long pRankInstId, OutputStream pOutputStream,
                                      HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<?> wReturnList = null;
            StringBuilder wRetList = new StringBuilder();
            wReturnList = this.payrollService.loadSalaryInfoByRankInfo(new Rank(pRankInstId),getBusinessCertificate(pRequest).getBusinessClientInstId(), false);
            if (wReturnList != null)
                for (Object h : wReturnList)
                    wRetList.append("<option value=\"").append(((SalaryInfo) h).getId()).append("\" ")
                            .append("title=\"").append(((SalaryInfo) h).getDescription()).append("\">")
                            .append(((SalaryInfo) h).getLevelAndStepAsStr().trim()).append("</option>");
            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        } catch (Exception wEx) {
            wEx.printStackTrace();
            System.out.println("Exception Thrown from getRanksForCadre.do() " + wEx.getMessage());
            System.out.println(wEx.getCause());
        }
    }
    @RequestMapping({ "/getRanksForCadre.do" })
    public void getRanksByCadre(@RequestParam("cadreInstId") Long pCadreInstId, OutputStream pOutputStream,
                                HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<?> wReturnList = null;
            StringBuilder wRetList = new StringBuilder();
             wReturnList = this.genericService.loadAllObjectsWithSingleCondition(Rank.class,CustomPredicate.procurePredicate("cadre.id", pCadreInstId), "name");
             if (wReturnList != null)
               for (Object h : wReturnList)
                 wRetList.append("<option value=\"").append(((Rank) h).getId()).append("\" ")
                            .append("title=\"").append(((Rank) h).getDescription()).append("\">")
                            .append(((Rank) h).getName().trim()).append("</option>");
             pOutputStream.write(wRetList.toString().getBytes());
              pOutputStream.flush();
              } catch (Exception wEx) {
              wEx.printStackTrace();
              System.out.println("Exception Thrown from getRanksForCadre.do() " + wEx.getMessage());
             System.out.println(wEx.getCause());
        }
    }
    @RequestMapping({ "/getLevelsForCadre.do" })
    public void getLevelsByCadre(@RequestParam("cadreInstId") Long pCadreInstId, OutputStream pOutputStream,
                                HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<?> wReturnList = null;
            StringBuilder wRetList = new StringBuilder();
            Cadre cadre = this.genericService.loadObjectById(Cadre.class,pCadreInstId);
             wReturnList = this.payrollService.makeLevelOrStepList(cadre.getSalaryType().getId(),true);

            if (!wReturnList.isEmpty())
                for (Object h : wReturnList)
                    wRetList.append("<option value=\"").append(((NamedEntity) h).getObjectInd()).append("\" >")
                            .append(((NamedEntity) h).getName().trim()).append("</option>");

            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        } catch (Exception wEx) {
            wEx.printStackTrace();
            System.out.println("Exception Thrown from getLevelsByCadre.do() " + wEx.getMessage());
            System.out.println(wEx.getCause());
        }
    }
    @RequestMapping({ "/getStepsForCadre.do" })
    public void getStepsByCadre(@RequestParam("cadreInstId") Long pCadreInstId, OutputStream pOutputStream,
                                 HttpServletRequest pRequest) throws Exception {
        try {
            SessionManagerService.manageSession(pRequest);
            List<?> wReturnList = null;
            StringBuilder wRetList = new StringBuilder();
            Cadre cadre = this.genericService.loadObjectById(Cadre.class,pCadreInstId);
            wReturnList = this.payrollService.makeLevelOrStepList(cadre.getSalaryType().getId(),false);

            if (!wReturnList.isEmpty())
                for (Object h : wReturnList)
                    wRetList.append("<option value=\"").append(((NamedEntity) h).getObjectInd()).append("\" >")
                            .append(((NamedEntity) h).getName().trim()).append("</option>");

            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        } catch (Exception wEx) {
            wEx.printStackTrace();
            System.out.println("Exception Thrown from getStepsForCadre.do() " + wEx.getMessage());
            System.out.println(wEx.getCause());
        }
    }
    @RequestMapping({"/mdaIsShoolEnabled.do"})
  public String isMdaSchoolEnabled(@RequestParam("mdaId") Long  pMdaId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
	  
	  MdaInfo wMdaInfo = this.genericService.loadObjectById(MdaInfo.class, pMdaId);
	  
	  return  wMdaInfo.isSchoolAttached() ? "Y" : "N";
  }
  /**
   * 
   * @param pMdaId - This is a concatenation of the ObjectInd+MDA_INST_ID e.g 11001
   * @param pOutputStream
   * @param pRequest
   * @throws Exception
   */
  @RequestMapping({"/getDepartmentsByMda.do"})
  public void getDepartmentsByMdaId(@RequestParam("mdaId") Long  pMdaId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);
    	
    	 
    	List<Department> wDeptList = this.mdaService.getDepartmentsByMdaId(pMdaId, false);

    	 // Assuming we are moving to Java 8+
	     Comparator<Department>  departmentComparator = Comparator.comparing(Department::getName);
	     Collections.sort(wDeptList,departmentComparator);
     
	     StringBuilder wRetList = new StringBuilder();

      if (wDeptList != null) {
        for (Department h : wDeptList) {
          wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("\">").append(h.getName().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from getDepartmentsByMdaId() ").append(wEx.getMessage()).toString());
    }
  }
  @RequestMapping({"/getUnassignedDepartmentsByMda.do"})
  public void loadUnassignedDepartmentsByMdaId(@RequestParam("mdaId") Long  pMdaId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);
    	
    	 
    	List<Department> wDeptList = this.mdaService.getDepartmentsByMdaId(pMdaId, true);

    	 // Assuming we are moving to Java 8+
	     Comparator<Department>  departmentComparator = Comparator.comparing(Department::getName);
	     Collections.sort(wDeptList,departmentComparator);
     
	     StringBuilder wRetList = new StringBuilder();

      if (wDeptList != null) {
        for (Department h : wDeptList) {
          wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("\">").append(h.getName().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from loadUnassignedDepartmentsByMdaId() ").append(wEx.getMessage()).toString());
    }
  }
  @RequestMapping({"/getSalaryTypes.do"})
  public void loadAllActiveSalaryTypes(@RequestParam("mdaId") String pBankId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);


      List<SalaryType> wSalaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(
              CustomPredicate.procurePredicate("selectable", IConstants.ON),
              this.getBusinessClientIdPredicate(pRequest)), "name");

      StringBuilder wRetList = new StringBuilder();

      if (wSalaryTypeList != null) {
        for (SalaryType s : wSalaryTypeList) {
          wRetList.append("<option value=\"").append(s.getId()).append("\" ").append("\">").append(s.getName().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from getSalaryTypes.do() ").append(wEx.getMessage()).toString());
    }
  }

    @RequestMapping({"/getCadresForSalaryType.do"})
    public void loadCadresBySalaryTypeId(@RequestParam("salaryTypeId") Long pSalaryTypeId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model) {
        try
        {
            SessionManagerService.manageSession(pRequest, model);
            List<Cadre> wCadreList = this.genericService.loadAllObjectsUsingRestrictions(Cadre.class,Arrays.asList(getBusinessClientIdPredicate(pRequest),CustomPredicate.procurePredicate("salaryType.id",pSalaryTypeId)),"name");

            StringBuilder wRetList = new StringBuilder();

            if (wCadreList != null) {
                for (Cadre s : wCadreList) {
                    wRetList.append("<option value=\"").append(s.getId()).append("\" ").append("title=\"").append(s.getDescription()).append("\">").append(s.getName().trim()).append("</option>");
                }

            }

            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        }
        catch (Exception wEx) {
            System.out.println(new StringBuilder().append("Exception Thrown from getCadresForSalaryType.do() ").append(wEx.getMessage()).toString());
        }
    }
    @RequestMapping({"/getSalaryTypesByRankId.do"})
    public void loadAllSalaryTypesByRank(@RequestParam("rankInstId") Long pRankId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
            throws Exception
    {
        try
        {
            SessionManagerService.manageSession(pRequest, model);
            //--Load the Rank First.
            Rank rank = this.genericService.loadObjectById(Rank.class,pRankId);
            List<SalaryType> wSalaryTypeList;
            if(rank.getCadre() == null || rank.getCadre().getSalaryType() == null){
                wSalaryTypeList = this.genericService.loadAllObjectsUsingRestrictions(SalaryType.class, Arrays.asList(
                        CustomPredicate.procurePredicate("selectableInd", IConstants.ON),CustomPredicate.procurePredicate("businessClientId", rank.getBusinessClientId())),"name");
            }else{
                wSalaryTypeList =  new ArrayList<>();
                wSalaryTypeList.add(rank.getCadre().getSalaryType());
            }


            StringBuilder wRetList = new StringBuilder();

            if (wSalaryTypeList != null) {
                for (SalaryType s : wSalaryTypeList) {
                    wRetList.append("<option value=\"").append(s.getId()).append("\" ").append("\">").append(s.getName().trim()).append("</option>");
                }

            }

            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();
        }
        catch (Exception wEx) {
            System.out.println(new StringBuilder().append("Exception Thrown from getSalaryTypesByRankId.do() ").append(wEx.getMessage()).toString());
        }
    }
  @RequestMapping({"/getBranchesByBank.do"})
  public void getBranchesByBankId(@RequestParam("bankId") Long pBankId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);
	     
     // BankInfo wBank = (BankInfo)this.payrollService.loadObjectByClassAndId(BankInfo.class, Integer.valueOf(pBankId));
      
	List<BankBranch> wBranchList = this.genericService.loadAllObjectsWithSingleCondition(BankBranch.class, CustomPredicate.procurePredicate("bankInfo.id", pBankId),"name");
      //Collections.sort(wBranchList);
      StringBuilder wRetList = new StringBuilder();

      if (wBranchList != null) {
        for (BankBranch h : wBranchList) {
          wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("title=\"").append(h.getBranchSortCode()).append("\">").append(h.getName().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from getBranchesByBank.do() ").append(wEx.getMessage()).toString());
    }
  }
  @RequestMapping({"/getMonthsForPayrollRunYear.do"})
  public void getMonthsByPayrollYYear(@RequestParam("runYear") int pRunYear, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);
	     
	List<NamedEntityBean> wMonthList = this.payrollService.loadPayrollMonthsByYear(this.getBusinessCertificate(pRequest),pRunYear);
      StringBuilder wRetList = new StringBuilder();

      if (wMonthList != null) {
        for (NamedEntityBean h : wMonthList) {
          wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("title=\"").append(h.getName()).append("\">").append(h.getName().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from getMonthsForPayrollRunYear.do() ").append(wEx.getMessage()).toString());
    }
  }
  
@RequestMapping({"/getLevelAndSteps.do"})
  public void getSalaryInfoBySalaryTypeId(@RequestParam("salaryTypeId") Long pSalaryTypeId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model)
    throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);
    	//SalaryType salaryType = genericService.loadObjectById(SalaryType.class,pSalaryTypeId);
        List<SalaryInfo> wSalaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class,
              CustomPredicate.procurePredicate("salaryType.id", pSalaryTypeId), null);

        Collections.sort(wSalaryInfoList,Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep));
      StringBuilder wRetList = new StringBuilder();

      if (wSalaryInfoList != null) {
        for (SalaryInfo h : wSalaryInfoList) {
          wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("title=\"").append(h.getSalaryType().getName()).append("\">").append(h.getLevelAndStepAsStr().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from getSalaryInfoBySalaryTypeId() ").append(wEx.getMessage()).toString());
    }
  }

 
@RequestMapping({"/getLevels.do"})
  public void getSalaryTypeLevelsById(@RequestParam("salaryTypeId") Long pSalaryTypeId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model) throws Exception
  {
    try
    {
    	SessionManagerService.manageSession(pRequest, model);

        List<SalaryInfo> wSalaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("salaryType.id", pSalaryTypeId),null);
      Comparator<SalaryInfo> c = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);
      
      Collections.sort(wSalaryInfoList,c);
      StringBuilder wRetList = new StringBuilder();

      if (wSalaryInfoList != null) {
        for (SalaryInfo h : wSalaryInfoList) {
          wRetList.append("<option value=\"").append(h.getLevel()).append("\" ").append("\">").append(h.getLevelAndStepAsStr().trim()).append("</option>");
        }

      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();
    }
    catch (Exception wEx) {
      System.out.println(new StringBuilder().append("Exception Thrown from getSalaryInfoBySalaryTypeId() ").append(wEx.getMessage()).toString());
    }
  }

 
  
  @RequestMapping({"/getSchoolsForMda.do"})
  public void getSchoolEnabledMdaByTypeId(@RequestParam("typeInd") Long pMdaInfoId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model) throws Exception
  {
    try {
    	SessionManagerService.manageSession(pRequest, model);

      List<MdaInfo> wReturnList = null;
 	   StringBuilder  wRetList = new StringBuilder();
 	   
 		 wReturnList =  this.genericService.loadAllObjectsUsingRestrictions(MdaInfo.class, Arrays.asList(
 		         CustomPredicate.procurePredicate("mdaType.id", pMdaInfoId),
                 CustomPredicate.procurePredicate("schoolIndicator", 1)), "name");


        if (wReturnList != null) {
            for (MdaInfo h : wReturnList) {
            	wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("title=\"").append(h.getCodeName()).append("\">").append(
            			h.getName().trim()).append("</option>");
            }
        }
 		 
       pOutputStream.write(wRetList.toString().getBytes());
       pOutputStream.flush();
       
       }catch(Exception wEx){
    	   wEx.printStackTrace();
       	System.out.println("Exception Thrown from getSchoolEnabledMdasByMdaInd() "+wEx.getMessage());
       	System.out.println(wEx.getCause());
       }
       
   }
  
@RequestMapping({"/getSchoolByMdaId.do"})
  public void getSchoolsForMda(@RequestParam("mdaId") Long pMdaInfoId, OutputStream pOutputStream, HttpServletRequest pRequest, Model model) throws Exception
  {
    try {
    	SessionManagerService.manageSession(pRequest, model);

      List<?> wReturnList = null;
 	   StringBuilder  wRetList = new StringBuilder();
 	   
 	     
 		 wReturnList =  this.genericService.loadAllObjectsWithSingleCondition(SchoolInfo.class,CustomPredicate.procurePredicate("mdaInfo.id",pMdaInfoId), "codeName");
 		 
 		 if(wReturnList != null && !wReturnList.isEmpty()) {

	
	        if (wReturnList != null) {
	            for (Object h : wReturnList) {
	            	wRetList.append("<option value=\"").append(((SchoolInfo) h).getId()).append("\" ").append("title=\"").append(((SchoolInfo) h).getCodeName()).append("\">").append(
	            			((SchoolInfo) h).getName().trim()).append("</option>");
	            }
	        }
 		 }
       pOutputStream.write(wRetList.toString().getBytes());
       pOutputStream.flush();
       
       }catch(Exception wEx){
    	   wEx.printStackTrace();
       	System.out.println("Exception Thrown from getSchoolEnabledMdasByMdaInd() "+wEx.getMessage());
       	System.out.println(wEx.getCause());
       }
       
   }

  
	@RequestMapping("/getLGAsByState.do")
  public void getLGAsByStateId(@RequestParam("stateId") Long pStateId, OutputStream pOutputStream,
                                  HttpServletRequest pRequest, Model model)
          throws Exception {
    try{
    	SessionManagerService.manageSession(pRequest, model);


     List<LGAInfo> wLgaList  =  this.genericService.loadAllObjectsWithSingleCondition(LGAInfo.class, CustomPredicate.procurePredicate("state.id", pStateId), "name");

     StringBuilder  wRetList = new StringBuilder();

      if (wLgaList != null) {
          for (LGAInfo h : wLgaList) {
          	wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("title=\"").append(h.getName()).append("\">").append(h.getName().trim()).append("</option>");
          }
      }

      pOutputStream.write(wRetList.toString().getBytes());
      pOutputStream.flush();

      }catch(Exception wEx){
      	System.out.println("Exception thrown from getLGAsByStateId() "+wEx.getMessage());
      }

  }

    @RequestMapping("/getCitiesByStateId.do")
    public void getCitiesByStateId(@RequestParam("stateId") Long pStateId, OutputStream pOutputStream,
                                 HttpServletRequest pRequest, Model model)
            throws Exception {
        try{
            SessionManagerService.manageSession(pRequest, model);

            List<City> wCityList  =  this.genericService.loadAllObjectsWithSingleCondition(City.class, CustomPredicate.procurePredicate("state.id", pStateId), "name");

            StringBuilder  wRetList = new StringBuilder();

            if (wCityList != null) {
                for (City h : wCityList) {
                    wRetList.append("<option value=\"").append(h.getId()).append("\" ").append("title=\"").append(h.getName())
                            .append("\">").append(h.getName().trim()).append("</option>");
                }
            }

            pOutputStream.write(wRetList.toString().getBytes());
            pOutputStream.flush();

        }catch(Exception wEx){
            System.out.println("Exception thrown from getCitiesByStateId() "+wEx.getMessage());
        }

    }

}
