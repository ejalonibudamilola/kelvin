/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.ErrorLogBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({"/viewErrorAuditLog.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ErrorLogController extends BaseController {

	  
	  private final int pageLength = 20;
	   
	  private final String VIEW = "audit/errorAuditLogForm";
	  
	  
	  public ErrorLogController()
	  {}
	  
	  @ModelAttribute("userList")
	  public List<User> populateUsersList() {

	  	return genericService.loadAllObjectsWithoutRestrictions(User.class,"username");
	  }

	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
	  public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
		  SessionManagerService.manageSession(request, model);

	       LocalDate localDate = LocalDate.now();

		  String sDate = PayrollBeanUtils.getJavaDateAsString(localDate);
		  String eDate = sDate;


		  return "redirect:viewErrorAuditLog.do?fd=" + sDate + "&td=" + eDate + "&uid=0";


	  }
	  
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"fd", "td", "uid"})
	  public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("uid") Long pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException
	  {
		  SessionManagerService.manageSession(request, model);
		     
	    LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
		  LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

	   PaginationBean paginationBean = getPaginationInfo(request);

		  PredicateBuilder predicateBuilder = new PredicateBuilder();
		  if(IppmsUtils.isNotNullAndGreaterThanZero(pUid))
		  	predicateBuilder.addPredicate(CustomPredicate.procurePredicate("user.id", pUid));
		  predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lastModTs",fDate, Operation.GREATER_OR_EQUAL));
		  predicateBuilder.addPredicate(CustomPredicate.procurePredicate("lastModTs",tDate, Operation.LESS_OR_EQUAL));

	    List<ErrorLogBean> empList = this.genericService.loadPaginatedObjects(ErrorLogBean.class,predicateBuilder.getPredicates(),(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

		  Collections.sort(empList, Comparator.comparing(ErrorLogBean::getErrorLogTime).reversed());
	    int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder,ErrorLogBean.class);

	    PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

	    wPELB.setShowRow(SHOW_ROW);

	    wPELB.setFromDate(fDate);
	    wPELB.setToDate(tDate);
	    wPELB.setFromDateStr(pFd);
	    wPELB.setToDateStr(pTd);
	    wPELB.setId(pUid);
		  addRoleBeanToModel(model, request);
	    model.addAttribute("miniBean", wPELB);
	    model.addAttribute("empList", empList);
	    model.addAttribute("size", empList.size());

	    return VIEW;
	  }

	@RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid", "fd", "td", "uid"})
	public String setupForm(@RequestParam("eid") Long eId, @RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("uid") Long pUid,
							Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
		SessionManagerService.manageSession(request, model);


			ErrorLogBean wErrLogBean = this.genericService.loadObjectById(ErrorLogBean.class, eId);
			this.genericService.deleteObject(wErrLogBean);

		return "redirect:viewErrorAuditLog.do?fd=" + pFd + "&td=" + pTd + "&uid=" + pUid;
	}
	  
	  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep, 
			  @RequestParam(value="_cancel", required=false) String cancel, @RequestParam(value="_delete", required = false) String delete,
								  @ModelAttribute("miniBean") PaginatedBean pLPB, @ModelAttribute("empList") ErrorLogBean errLB,
			  BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
		  SessionManagerService.manageSession(request, model);
	  
	    if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
	      return "redirect:auditPageHomeForm.do";
	    }
	   

	    if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
	      if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null))
	      {
	        result.rejectValue("", "InvalidValue", "Please select valid Dates");
	        addDisplayErrorsToModel(model, request);
	        addRoleBeanToModel(model, request);
			  addRoleBeanToModel(model, request);
	        model.addAttribute("status", result);
	        model.addAttribute("miniBean", pLPB);

	        return VIEW;
	      }


	      if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
	        result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");
			  addDisplayErrorsToModel(model, request);
			  addRoleBeanToModel(model, request);
			  addRoleBeanToModel(model, request);
			  model.addAttribute("status", result);
			  model.addAttribute("miniBean", pLPB);

	        return VIEW;
	      }

	  
	      String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
	      String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());


	      return "redirect:viewErrorAuditLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId();
	    }

	    return "redirect:viewErrorAuditLog.do";
	  }

	@RequestMapping(method={RequestMethod.GET}, params={"pid"})
	public String setupForm2(@RequestParam("pid") String pId, Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException {
		SessionManagerService.manageSession(request, model);
//		BusinessCertificate bc = this.getBusinessCertificate(request);
		ErrorLogBean errorLogBean = this.genericService.loadObjectWithSingleCondition(ErrorLogBean.class, CustomPredicate.procurePredicate("id",pId));
        errorLogBean.setBusinessClient(genericService.loadObjectById(BusinessClient.class, errorLogBean.getBusinessClientId()));
		model.addAttribute("eBean",errorLogBean);
		addRoleBeanToModel(model, request);
		return "audit/errorLogModal";

	}
	}
