package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.hr.ClientDeptValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping({"/createClientDeptForm.do"})
@SessionAttributes(types={Department.class})
public class CreateDepartmentFormController extends BaseController{

  @Autowired
  private ClientDeptValidator validator;
  
  private final String VIEW = "hr_mda/createEditDepartmentForm";
  
  @ModelAttribute("mdasList")
  private List<MdaInfo> loadMdas(HttpServletRequest request){

	  return this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
              CustomPredicate.procurePredicate("businessClientId",getBusinessCertificate(request).getBusinessClientInstId()),"name");

	  
  }


  @RequestMapping(method={RequestMethod.GET})
  public String setupForm(@RequestParam(value = "did", required = false) Long pDeptId,Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);

      Department wHCDD = new Department();
      if(IppmsUtils.isNotNullAndGreaterThanZero(pDeptId)) {
            wHCDD = this.genericService.loadObjectUsingRestriction(Department.class,
                  Arrays.asList(CustomPredicate.procurePredicate("id", pDeptId), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
          wHCDD.setEditMode(true);
          model.addAttribute("displayTitle", "Edit Department");
          if(wHCDD.getDefaultInd() == ON)
              wHCDD.setDefaultIndBind(true);
      }else{
          wHCDD.setBusinessClientId(bc.getBusinessClientInstId());
          model.addAttribute("displayTitle", "Create Department");
      }

    model.addAttribute("miniBean", wHCDD);
//    model.addAttribute("displayList", this.makePaginatedList(request, false));
    model.addAttribute("displayList", this.makeDeptList(request, false));
    model.addAttribute("roleBean",bc);
    return VIEW;
    
    
  }
  @RequestMapping(method={RequestMethod.GET}, params={"did","s"})
  public String setupForm(@RequestParam("did") Long pDeptId,
		  @RequestParam("s") int pSaved,
		  Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);

      BusinessCertificate bc = super.getBusinessCertificate(request);

      Department wHCDD = this.genericService.loadObjectUsingRestriction(Department.class,
              Arrays.asList(CustomPredicate.procurePredicate("id",pDeptId), CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId())));
      
     String wActionCompleted = "Department "+wHCDD.getName()+" created successfully.";
     if(pSaved > 1) {
    	 wActionCompleted = "Department "+wHCDD.getName()+" edited successfully.";
    	
     }
    	 
    wHCDD = new Department();
    model.addAttribute("displayTitle", "Create/Edit Department");
//    model.addAttribute("displayList", this.makePaginatedList(request, false));
    model.addAttribute("displayList", this.makeDeptList(request, false));
    model.addAttribute(IConstants.SAVED_MSG, wActionCompleted);
    model.addAttribute("saved", true);
    model.addAttribute("miniBean", wHCDD);
    model.addAttribute("roleBean",bc);
    return VIEW;
  }

  @RequestMapping(method={RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("miniBean") Department pHCDD, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
	  SessionManagerService.manageSession(request, model);
      BusinessCertificate bc = super.getBusinessCertificate(request);


    if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL)) {
      return "redirect:departmentFunctionalities.do";
    }

   validator.validate(pHCDD, result, bc);
    if (result.hasErrors()) {
      model.addAttribute(DISPLAY_ERRORS, BLOCK);
      model.addAttribute("status", result);
      model.addAttribute("busClient", pHCDD);
      model.addAttribute("roleBean",bc);
      return VIEW;
    }
    if(pHCDD.isDefaultIndBind()){
        pHCDD.setDefaultInd(ON);
    }
    int s = 1;
    if(pHCDD.isEditMode()) {
    	++s;
    	
    }else {
        pHCDD.setCreatedBy(new User(bc.getLoginId()));
        pHCDD.setCreationDate(Timestamp.from(Instant.now()));
        pHCDD.setBusinessClientId(bc.getBusinessClientInstId());

    }
    pHCDD.setDeactivate(0);
    pHCDD.setMapable(0);
    pHCDD.setDeactivatedDate(null);

    pHCDD.setLastModBy(new User(bc.getLoginId()));
    pHCDD.setLastModTs(Timestamp.from(Instant.now()));

     this.genericService.saveObject(pHCDD);

    return "redirect:createClientDeptForm.do?did="+pHCDD.getId()+"&s="+s;
  }
  
// private PaginatedPaycheckGarnDedBeanHolder makePaginatedList(HttpServletRequest request, boolean pSaved) {
//
//      BusinessCertificate bc = super.getBusinessCertificate(request);
//
//      PaginationBean paginationBean = this.getPaginationInfo(request);
//
//        List<Department> wAllList = this.genericService.loadAllObjectsWithSingleCondition(Department.class,
//                CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),null);
//		List<Department> wRetList = null;
//
//		//Do we do the list upside down now?
//
//		if(pSaved) {
//			Comparator<Department> c = Comparator.comparing(Department::getId);
//			Collections.sort(wAllList,c.reversed());
//		}else {
//			Comparator<Department> c = Comparator.comparing(Department::getName);
//			Collections.sort(wAllList,c);
//		}
//
//
//		if(wAllList.size() > 20) {
//
//			wRetList = (List<Department>) PayrollUtils.paginateList(paginationBean.getPageNumber(), 10, wAllList, Department.class);
//
//		}else {
//			 wRetList = wAllList;
//		}
//
//		PaginatedPaycheckGarnDedBeanHolder wPBO = new PaginatedPaycheckGarnDedBeanHolder(wRetList, paginationBean.getPageNumber(), 10, wAllList.size(), paginationBean.getSortOrder(), paginationBean.getSortCriterion());
//
//		return wPBO;
//	}

    private List<Department> makeDeptList (HttpServletRequest request, boolean pSaved) {

        BusinessCertificate bc = super.getBusinessCertificate(request);

        List<Department> wAllList = this.genericService.loadAllObjectsWithSingleCondition(Department.class,
                CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),null);
        List<Department> wRetList = null;

        //Do we do the list upside down now?

        if(pSaved) {
            Comparator<Department> c = Comparator.comparing(Department::getId);
            Collections.sort(wAllList,c.reversed());
        }else {
            Comparator<Department> c = Comparator.comparing(Department::getName);
            Collections.sort(wAllList,c);
        }


        wRetList = wAllList;


        return wRetList;


    }
}