package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.DataTableBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/viewTransferLog.do"})
@SessionAttributes(types = {DataTableBean.class})
public class TransferAuditLogsFormController extends BaseController {


    private final int pageLength = 20;

    private final String VIEW_NAME = "transfer/transferAuditLogForm";

    @Autowired
    public TransferAuditLogsFormController() {
    }

    @ModelAttribute("userList")
    public List<User> populateUsersList(HttpServletRequest request) {

        return this.genericService.loadAllObjectsWithSingleCondition(User.class,CustomPredicate.procurePredicate("role.businessClient.id",getBusinessCertificate(request).getBusinessClientInstId()),"username");
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        DataTableBean wBEOB = new DataTableBean(new ArrayList<TransferLog>());

        wBEOB.setShowLink(false);
        wBEOB.setShowRow(HIDE_ROW);
        return makeAndReturnView(model,getBusinessCertificate(request),wBEOB,null);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long pEid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

//        PaginationBean paginationBean = getPaginationInfo(request);

        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        String empJoin = "employee.id";
        if (bc.isPensioner())
            empJoin = "pensioner.id";
        predicates.add(CustomPredicate.procurePredicate(empJoin, pEid));

//        List<TransferLog> empList = this.genericService.loadPaginatedObjects(TransferLog.class, predicates, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

        List<TransferLog> empList = this.genericService.loadAllObjectsUsingRestrictions(TransferLog.class,predicates,null);

        if(bc.isPensioner()){

        }

//        int wGLNoOfElements = this.genericService.getTotalPaginatedObjects(TransferLog.class, predicates).intValue();

//        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        DataTableBean wPELB = new DataTableBean(empList);
        wPELB.setShowLink(empList.size() > 0);
        wPELB.setShowRow(SHOW_ROW);

        wPELB.setFromDateStr("");
        wPELB.setToDateStr("");
        wPELB.setId(0L);
        return makeAndReturnView(model,bc,wPELB,null);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"fd", "td", "uid"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd,
                            @RequestParam("uid") Long pUid, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

//        PaginationBean paginationBean = getPaginationInfo(request);
        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        predicates.add(CustomPredicate.procurePredicate("transferDate", PayrollBeanUtils.getNextORPreviousDay(fDate, false), Operation.GREATER));
        predicates.add(CustomPredicate.procurePredicate("transferDate", PayrollBeanUtils.getNextORPreviousDay(tDate, true), Operation.LESS));

        if (IppmsUtils.isNotNullAndGreaterThanZero(pUid))
            predicates.add(CustomPredicate.procurePredicate("user.id", pUid));
        else
            pUid = 0L;

        List<TransferLog> empList = this.genericService.loadAllObjectsUsingRestrictions(TransferLog.class,predicates,null);

//        List<TransferLog> empList = this.genericService.loadPaginatedObjects(TransferLog.class, predicates, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
//
//        int wGLNoOfElements = this.genericService.getTotalPaginatedObjects(TransferLog.class, predicates).intValue();
//        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        DataTableBean wPELB = new DataTableBean(empList);

        wPELB.setShowLink(empList.size() > 0);
        wPELB.setShowRow(SHOW_ROW);

        wPELB.setFromDate(fDate);
        wPELB.setToDate(tDate);
        wPELB.setFromDateStr(pFd);
        wPELB.setToDateStr(pTd);
        wPELB.setId(pUid);
        return makeAndReturnView(model,bc,wPELB,null);
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"fd", "td", "uid", "eid"})
    public String setupForm(@RequestParam("fd") String pFd, @RequestParam("td") String pTd, @RequestParam("uid") Long pUid,
                            @RequestParam("eid") Long pEmpId, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);
        LocalDate fDate = PayrollBeanUtils.setDateFromString(pFd);
        LocalDate tDate = PayrollBeanUtils.setDateFromString(pTd);

//        PaginationBean paginationBean = getPaginationInfo(request);
        List<CustomPredicate> predicates = new ArrayList<>();
        predicates.add(getBusinessClientIdPredicate(request));
        predicates.add(CustomPredicate.procurePredicate("transferDate", PayrollBeanUtils.getNextORPreviousDay(fDate, false), Operation.GREATER));
        predicates.add(CustomPredicate.procurePredicate("transferDate", PayrollBeanUtils.getNextORPreviousDay(tDate, true), Operation.LESS));

        boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUid);


        if (!IppmsUtils.isNotNullAndGreaterThanZero(pEmpId)) {
            pEmpId = 0L;
        } else {
            String empJoin = "employee.id";
            if (bc.isPensioner())
                empJoin = "pensioner.id";
            predicates.add(CustomPredicate.procurePredicate(empJoin, pEmpId));
        }
        List<TransferLog> empList = this.genericService.loadAllObjectsUsingRestrictions(TransferLog.class,predicates,null);

//        List<TransferLog> empList = this.genericService.loadPaginatedObjects(TransferLog.class, predicates, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
//
//        int wGLNoOfElements = this.genericService.getTotalPaginatedObjects(TransferLog.class, predicates).intValue();
//        PaginatedBean wPELB = new PaginatedBean(empList, paginationBean.getPageNumber(), this.pageLength, wGLNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());

        DataTableBean wPELB = new DataTableBean(empList);

        wPELB.setShowRow(SHOW_ROW);

        if (pEmpId != null) {
            AbstractEmployeeEntity entity = IppmsUtils.loadEmployee(genericService, pEmpId, bc);
            wPELB.setOgNumber(entity.getEmployeeId());
        }
        wPELB.setShowLink(empList.size() > 0);
        wPELB.setFromDate(fDate);
        wPELB.setToDate(tDate);
        wPELB.setFromDateStr(pFd);
        wPELB.setToDateStr(pTd);
        wPELB.setId(pUid);
        wPELB.setEmpInstId(pEmpId);
        return makeAndReturnView(model,bc,wPELB,null);
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_updateReport", required = false) String updRep,
                                @RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("miniBean") DataTableBean pLPB, BindingResult result,
                                SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL)) {
            return "redirect:auditPageHomeForm.do";
        }


        if (isButtonTypeClick(request, REQUEST_PARAM_UPDATE_REPORT)) {
            if ((pLPB.getFromDate() == null) && (pLPB.getToDate() == null)) {
                result.rejectValue("", "InvalidValue", "Please select valid Dates");
               addDisplayErrorsToModel(model, request);
               addRoleBeanToModel(model, request);

                return makeAndReturnView(model,bc,pLPB,result);
            }


            if (pLPB.getFromDate().isAfter(pLPB.getToDate())) {
                result.rejectValue("", "InvalidValue", "'Between' Date can not be greater than 'And' Date ");

                addDisplayErrorsToModel(model, request);
                addRoleBeanToModel(model, request);
                return makeAndReturnView(model,bc,pLPB,result);
            }

            String sDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getFromDate());
            String eDate = PayrollBeanUtils.getJavaDateAsString(pLPB.getToDate());

            if (!StringUtils.trimToEmpty(pLPB.getOgNumber()).equals("")) {

                AbstractEmployeeEntity wEmp = (AbstractEmployeeEntity)this.genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(bc),
                        Arrays.asList(getBusinessClientIdPredicate(request),CustomPredicate.procurePredicate("employeeId",pLPB.getOgNumber())));
                pLPB.setEmpInstId(wEmp.getId());
            } else {
                //Reset the Emp ID to Zero
                pLPB.setEmpInstId(0L);
            }
            if (pLPB.getEmpInstId().intValue() > 0) {
                return "redirect:viewTransferLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId() + "&eid=" + pLPB.getEmpInstId();
            }
            return "redirect:viewTransferLog.do?fd=" + sDate + "&td=" + eDate + "&uid=" + pLPB.getId();
        }

        return "redirect:viewTransferLog.do";
    }
    private String makeAndReturnView(Model model,BusinessCertificate bc,DataTableBean dtb, BindingResult result){

        model.addAttribute("displayList", dtb.getObjectList());
        model.addAttribute("roleBean", bc);
        if(result != null)
            model.addAttribute("status", result);
        model.addAttribute("miniBean", dtb);

        return this.VIEW_NAME;
    }

}
