package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HrMiniBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payment.beans.PayPeriodDaysMiniBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.validators.payment.PaySlipValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping({"/selectPayslipMode.do"})
@SessionAttributes("miniBean")
public class SelectPaySlipModeFormController extends BaseController {


	  private final PaySlipValidator validator;
	  private final PaycheckService paycheckService;

	  private final String VIEW = "payment/paySlipModeSelectForm";
	   @Autowired
	  public SelectPaySlipModeFormController(PaySlipValidator validator, PaycheckService paycheckService){
		   this.validator = validator;
		   this.paycheckService = paycheckService;
	   }

	  @ModelAttribute("monthList")
	  public Collection<NamedEntity> getMonthList() {
	    return PayrollBeanUtils.makeAllMonthList();
	  }

	  @ModelAttribute("yearList")
	  public Collection<NamedEntity> makeYearList(HttpServletRequest request) {

	    return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
	  }


	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);
          BusinessCertificate bc = getBusinessCertificate(request);

	    HrMiniBean wHMB = new HrMiniBean();
	    wHMB.setHideRow(HIDE_ROW);
	    wHMB.setHideRow1(HIDE_ROW);
	    wHMB.setHideRow2(HIDE_ROW);
	    //Lets Load the Objects to be chosen....
	    wHMB.setRunMonth(-1);

        wHMB.setMdaInfoList(this.paycheckService.loadObjectIdAndNameByClassAndConditions("MdaInfo",2,"where businessClientId = :pBizIdVar", bc.getBusinessClientInstId()));
        wHMB.setPayGroupList(this.paycheckService.loadObjectIdAndNameByClassAndConditions("SalaryType",1,"where selectableInd = 1 and deactivatedInd = 0 and businessClientId = :pBizIdVar", bc.getBusinessClientInstId()));
		addRoleBeanToModel(model, request);
	    model.addAttribute("miniBean", wHMB);
	    return VIEW;
	  }

		@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET},params= {"oid"})
		  public String setupForm(@RequestParam("oid") String pObjId, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
			  SessionManagerService.manageSession(request, model);


		    HrMiniBean wHMB = (HrMiniBean) super.getSessionAttribute(request, PS_BEAN);
//			HrMiniBean wHMB = (HrMiniBean) model.getAttribute(PS_BEAN);
		    if(wHMB == null)
		    	return "redirect:selectPayslipMode.do";

		    if(pObjId.equalsIgnoreCase(IConstants.REQUEST_PARAM_ADD)) {
		    	 wHMB.setMode(REQUEST_PARAM_ADD);
		    }else {
		    	 wHMB.setMode(IConstants.REQUEST_PARAM_DELETE);
		    	 String id = pObjId.substring(0,pObjId.indexOf("_"));
		    	 String pObjType = pObjId.substring(pObjId.indexOf("_") + 1);

		    	 wHMB.setId(Long.getLong(id));
		    	 wHMB.setMapId(Integer.parseInt(pObjType));
		    	 wHMB.setObjectCode(pObjId);

		    	 for(NamedEntityBean n : (List<NamedEntityBean>)wHMB.getMappedParentDeptList()){
		    	 	if(n.getPaySlipDisplayObjId().equalsIgnoreCase(pObjId)){
		    	 		if(n.isSalaryType())
							wHMB.setSalaryTypeId(n.getId());
		    	 		else if(n.isMdaType())
		    	 			wHMB.setMdaId(n.getId());
		    	 		else
		    	 			wHMB.setEmployee(new Employee<>(n.getId()));
					}
				 }
		    }
		    PaginatedPaycheckGarnDedBeanHolder p =  this.setSettables(wHMB, request,false);

			addRoleBeanToModel(model, request);
		    model.addAttribute("miniBean1", p.getSomeObject());
			model.addAttribute("miniBean", wHMB);
		    model.addAttribute("displayList", p);
		    return VIEW;
		  }

	
	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel,
			  @ModelAttribute("miniBean") HrMiniBean pHMB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception
	  {
		  SessionManagerService.manageSession(request, model);

          BusinessCertificate businessCertificate = getBusinessCertificate(request);


	    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
	      return "redirect:reportsOverview.do";
	    }
	   if(isButtonTypeClick(request, REQUEST_PARAM_ADD)) {
		   validator.validate(pHMB, result,businessCertificate);
	    	if(result.hasErrors()) {
	    		pHMB.setMode(REQUEST_PARAM_ADD);
				PaginatedPaycheckGarnDedBeanHolder p  = this.setSettables(pHMB, request, true);
				if(p.getSomeObject() == null)
					p.setSomeObject(new HrMiniBean());

	    		model.addAttribute("status", result);
	    		model.addAttribute("miniBean", pHMB);
				model.addAttribute("displayList", p);


	 		   if(pHMB.isMakeLevelStepList()) {
				   List<SalaryInfo> wSalaryInfoList = this.genericService.loadAllObjectsWithSingleCondition(SalaryInfo.class, CustomPredicate.procurePredicate("salaryType.id", pHMB.getSalaryTypeId()),null);
				   Comparator<SalaryInfo> c = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);

				   Collections.sort(wSalaryInfoList,c);
	 			   model.addAttribute("fromLevelList", wSalaryInfoList);
	 			  model.addAttribute("toLevelList", wSalaryInfoList);
	 		   }
	 		   addRoleBeanToModel(model, request);
	 		   addDisplayErrorsToModel(model, request);
	    		return VIEW;
	    	}

	    	super.addSessionAttribute(request, PS_BEAN, pHMB);
            model.addAttribute(PS_BEAN, pHMB);
	    	return "redirect:selectPayslipMode.do?oid="+REQUEST_PARAM_ADD;


	   }

	    if (isButtonTypeClick(request,REQUEST_PARAM_GO))
	    {

		    	validator.validateForPrint(pHMB, result);
		    	if(result.hasErrors())
		    	{
		    		PaginatedPaycheckGarnDedBeanHolder p =  this.setSettables(pHMB, request, true);
		    		model.addAttribute("status", result);
		    		model.addAttribute("miniBean", pHMB);
		    		 model.addAttribute("displayList", p);
			 		   if(pHMB.isMakeLevelStepList()) {
			 			   model.addAttribute("fromLevelList", pHMB.getLevelStepList());
			 			  model.addAttribute("toLevelList", pHMB.getLevelStepList());
			 		   }
			 		   addRoleBeanToModel(model, request);
			 		   addDisplayErrorsToModel(model, request);

		    		return VIEW;
		    	}
		    	 List<Long> wMdaIds = new ArrayList<>();
		         List<NamedEntityBean> myList = new ArrayList<>();
		         int wTotalElements = 0;
		    	for(NamedEntityBean n : (List<NamedEntityBean>)pHMB.getMappedParentDeptList()) {
		    		 n.getIdList().addAll(wMdaIds);
		    		 n = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDateAndFilter(super.getBusinessCertificate(request), pHMB.getRunMonth(), pHMB.getRunYear(),n);
		    		 wTotalElements += n.getEmpPayBeanList().size();
		    		 wMdaIds = new ArrayList<>(n.getIdList());
		    		 myList.add(n);
	    		}
		    	PayPeriodDaysMiniBean ppDMB = new PayPeriodDaysMiniBean();

		    	ppDMB.setStartLocalDate(PayrollBeanUtils.getDateFromMonthAndYear(pHMB.getRunMonth(), pHMB.getRunYear()));

		    	 ppDMB.setNamedListBean(myList);


		         ppDMB.setObjectList(wMdaIds);

		         ppDMB.setTotalNoOfElements(wTotalElements);

		         addSessionAttribute(request, CHECK_REG_ATTR_NAME, ppDMB);

		         return "redirect:bulkPaySlipPdf.do";
		  }
		return "redirect:selectPayslipMode.do";
	   }



	private PaginatedPaycheckGarnDedBeanHolder setSettables(HrMiniBean pHMB, HttpServletRequest request, boolean pError) throws InstantiationException, IllegalAccessException {
//		if(IppmsUtils.isNullOrEmpty(pHMB.getParentTypeList()))
//			return new PaginatedPaycheckGarnDedBeanHolder();
	  	BusinessCertificate bc = super.getBusinessCertificate(request);

		 NamedEntityBean mI = null;
		 NamedEntityBean  n = null;
		  if(pHMB.isSalaryType()) {
			   pHMB.setMakeLevelStepList(true);
			 List<NamedEntityBean> wSalTypeList = pHMB.getPayGroupList();
			 List<NamedEntityBean> wNewList = new ArrayList<>();
			 List<NamedEntityBean> wAssList = (List<NamedEntityBean>) pHMB.getParentTypeList();
			 pHMB.setHideRow(SHOW_ROW);
			 pHMB.setHideRow1(HIDE_ROW);
			 pHMB.setHideRow2(HIDE_ROW);
			if(wAssList == null)
				 wAssList = new ArrayList<>();

			 //For this types...we will need the Level (From and To)

			 if(pHMB.getMode() != null && pHMB.getMode().equals(REQUEST_PARAM_ADD)) {
				 for(NamedEntityBean m : wSalTypeList) {

					 if(m.getPaySlipDisplayObjId().equalsIgnoreCase(pHMB.getObjectCode())) {
						 mI = m;
						 wAssList.add(m);
						 continue;
					 }
					 wNewList.add(m);
				 }

				 pHMB.setPayGroupList(sortList(wNewList));
				 pHMB.setParentTypeList(wAssList);
			 }else if(pHMB.getMode() != null && pHMB.getMode().equals(REQUEST_PARAM_DELETE)) {
				 //Remove or Delete

                 for(NamedEntityBean m : (List<NamedEntityBean>) pHMB.getParentTypeList()) {

					 if(m.getPaySlipDisplayObjId().equalsIgnoreCase(pHMB.getObjectCode())) {
						 wSalTypeList.add(m);
						 continue;
					 }
					 wNewList.add(m);

				 }

                 pHMB.setPayGroupList(sortList(wSalTypeList));
                 pHMB.setParentTypeList(sortList(wNewList));
                 //--Now Remove from the Display List...
                 pHMB = this.removeFromDisplayList(pHMB);
			 }
		  }else if(pHMB.isMdaType()) {
			 //Get the MDA...
			 List<NamedEntityBean> wMdaList = pHMB.getMdaInfoList();
			 List<NamedEntityBean> wNewList = new ArrayList<NamedEntityBean>();
			 List<NamedEntityBean> wAssList = (List<NamedEntityBean>) pHMB.getParentTypeList();
			 pHMB.setHideRow1(SHOW_ROW);
			 pHMB.setHideRow2(HIDE_ROW);
			 pHMB.setHideRow(HIDE_ROW);
			  if(wAssList == null)
				  wAssList = new ArrayList<>();

			 if(pHMB.getMode() != null && pHMB.getMode().equals(REQUEST_PARAM_ADD)) {
				 for(NamedEntityBean m : wMdaList) {

					 if(m.getPaySlipDisplayObjId().equalsIgnoreCase(pHMB.getObjectCode())) {
						 mI = m;
						 wAssList.add(m);
						 continue;
					 }
					 wNewList.add(m);
				 }
				 pHMB.setMdaInfoList(sortList(wNewList));
				 pHMB.setParentTypeList(sortList(wAssList));

			 }else {
				 //Remove or Delete
				 if(IppmsUtils.isNullOrEmpty(pHMB.getParentTypeList()))
					return new PaginatedPaycheckGarnDedBeanHolder();
                 for(NamedEntityBean m : (List<NamedEntityBean>) pHMB.getParentTypeList()) {

                	 if(m.getPaySlipDisplayObjId().equalsIgnoreCase(pHMB.getObjectCode())) {
						 mI = m;
						 wMdaList.add(m);
						 continue;
					 }
					 wNewList.add(m);

				 }

                 pHMB.setParentTypeList(sortList(wNewList));
                 pHMB.setMdaInfoList((wMdaList));
                 //--Now Remove from the Display List...
                 pHMB = this.removeFromDisplayList(pHMB);
			 }
		 }
		 else if(!IppmsUtils.isNullOrEmpty(pHMB.getStaffId())){
			 //--Employee....
			 pHMB.setHideRow2(SHOW_ROW);
			 pHMB.setHideRow(HIDE_ROW);
			 pHMB.setHideRow(HIDE_ROW);
			 if(pHMB.getEmployee() == null)
				 pHMB.setEmployee((AbstractEmployeeEntity) this.genericService.loadObjectWithSingleCondition(IppmsUtils.getEmployeeClass(bc), CustomPredicate.procurePredicate("employeeId",pHMB.getStaffId())));

			 if(pHMB.getMode() != null && pHMB.getMode().equals(REQUEST_PARAM_ADD)) {
				 if(pHMB.getIdMap() == null)
					 pHMB.setIdMap(new HashMap<>());

				 if(!pHMB.getIdMap().containsKey(pHMB.getEmployee().getId()))
						 pHMB.getIdMap().put(pHMB.getEmployee().getId(), pHMB.getEmployee().getId());
				 else
					 //This is a refresh
					 pError = true;
			 }else {
				 //Remove or Delete
				 pHMB.getIdMap().remove(pHMB.getEmployee().getId());
				 pHMB = removeFromDisplayList(pHMB);

			 }

		 }
		  if(pHMB.getMode() != null && pHMB.getMode().equalsIgnoreCase(REQUEST_PARAM_ADD) && !pError)
		     n = this.paycheckService.createPaySlipDisplayBean(pHMB,bc);


	      return makePaginatedList(pHMB,mI,n,request,pError);

	}

	private List<NamedEntityBean> sortList(List<NamedEntityBean> pList) {
		 Comparator<NamedEntityBean> c = Comparator.comparing(NamedEntityBean::getName);
		 Collections.sort(pList,c);
		return pList;
	}

	private HrMiniBean removeFromDisplayList(HrMiniBean pHMB) {
		 //--Now Remove from the Display List...
        List<NamedEntityBean> wNewList = new ArrayList<NamedEntityBean>();
        if(pHMB.getMappedParentDeptList() != null)
        for(NamedEntityBean n : ((ArrayList<NamedEntityBean>)pHMB.getMappedParentDeptList())) {
        	 if(n.getPaySlipDisplayObjId().equalsIgnoreCase(pHMB.getObjectCode()))
       		 continue;
       	 wNewList.add(n);
        }
        pHMB.setMappedParentDeptList(wNewList);
        return pHMB;
	}

	private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HrMiniBean pHMB,NamedEntityBean mI, NamedEntityBean pNE, HttpServletRequest request,boolean pError) {

			PaginationBean paginationBean = getPaginationInfo(request);


			List<NamedEntityBean> wAllList =  (List<NamedEntityBean>) pHMB.getMappedParentDeptList();
			if(wAllList == null){
				wAllList = new ArrayList<>();
			}
			//Make a new NamedEntity....

			if(pHMB.getMode() != null && pHMB.getMode().equals(REQUEST_PARAM_ADD) && !pError) {

				if(pHMB.isMdaType()) {
					pNE.setId(mI.getId());
					pNE.setName(mI.getName());

				}else if(pHMB.isSalaryType()){
					pNE.setId(mI.getId());
					pNE.setName(mI.getName());
					pNE.setFromLevel(pHMB.getFromLevel());
					pNE.setToLevel(pHMB.getToLevel());

				}else {
					pNE.setId(pHMB.getEmployee().getId());
					pNE.setName(pHMB.getEmployee().getDisplayNameWivTitlePrefixed());
				}


				 pNE.setPaySlipObjTypeInd(pHMB.getMapId());
				 pNE.setObjectInd(wAllList.size() + 1);

				 pNE.setFontColor("green");
				 for(NamedEntityBean n : wAllList)n.setFontColor("black");
				 wAllList.add(pNE);
			}

			List<NamedEntityBean> wRetList = null;

			//Do we do the list upside down now?

			if(!pError) {
				Comparator<NamedEntityBean> c = Comparator.comparing(NamedEntityBean::getObjectInd);
				Collections.sort(wAllList,c.reversed());
			}else {
				Comparator<NamedEntityBean> c = Comparator.comparing(NamedEntityBean::getName);
				Collections.sort(wAllList,c);
			}


			if(wAllList.size() > 10) {

				wRetList = (List<NamedEntityBean>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList);

			}else {
				 wRetList = wAllList;
			}

			PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
			pHMB.setMappedParentDeptList(wAllList);
			if(wAllList == null || wAllList.isEmpty()) {
				pHMB.setMapId(0);
				pHMB.setHideRow(HIDE_ROW);
				pHMB.setHideRow1(HIDE_ROW);
				pHMB.setHideRow2(HIDE_ROW);
			}
			if(pHMB.isMakeLevelStepList())
				makeLevelStepList(pHMB);

				pHMB.setStaffId(null);
				pHMB.setSalaryTypeId(0L);
				pHMB.setMdaId(0L);
			    wPBO.setSomeObject(pHMB);


			return wPBO;
		}

	private void makeLevelStepList(HrMiniBean pHMB) {

		 List<SalaryInfo> wSalaryInfoList = this.paycheckService.loadSalaryInfoBySalaryTypeId(pHMB.getSalaryTypeId());
	      Comparator<SalaryInfo> c = Comparator.comparing(SalaryInfo::getLevel).thenComparing(SalaryInfo::getStep);

	      Collections.sort(wSalaryInfoList,c);
	      pHMB.setLevelStepList(wSalaryInfoList);
	}

}
