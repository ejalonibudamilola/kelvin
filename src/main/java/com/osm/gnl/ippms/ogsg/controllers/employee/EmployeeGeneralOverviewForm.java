package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.service.EmpGenOverviewService;
import com.osm.gnl.ippms.ogsg.domain.employee.HrPassportInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.AbstractHiringInfoEntity;
import com.osm.gnl.ippms.ogsg.employee.beans.EmployeeBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/employeeOverviewForm.do","/pensionerOverviewForm.do"})
@SessionAttributes(types = {EmployeeBean.class})
public class EmployeeGeneralOverviewForm extends BaseController {


    private final IMenuService menuService;
    private final PromotionService promotionService;
    private final PaycheckService paycheckService;
    private final String VIEW_NAME = "employee/employeeOverviewForm";
    private final String ENQ_VIEW = "employee/employeeEnquiryViewForm";

    @Autowired
    public EmployeeGeneralOverviewForm(IMenuService menuService, PromotionService promotionService, PaycheckService paycheckService) {
        this.menuService = menuService;
        this.promotionService = promotionService;
        this.paycheckService = paycheckService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        NamedEntity ne = (NamedEntity) getSessionAttribute(request, NAMED_ENTITY);
        if (!ne.isNewEntity()) {
            return "redirect:employeeOverviewForm.do?eid=" + ne.getId();
        }

        return "redirect:sessionExpiredForm.do";
    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid"})
    public String setupForm(@RequestParam("eid") Long empId, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = this.getBusinessCertificate(request);
        Object userId = getSessionId(request);
        NamedEntity ne = new NamedEntity();
        ne.setId(empId);

        EmployeeBean pEB = new EmployeeBean();
        ne.setMode("edit");
        pEB.setEmployee(IppmsUtils.loadEmployee(genericService, empId, bc));
        ne.setName(pEB.getEmployee().getDisplayNameWivTitlePrefixed());

        Object wObj = EmpGenOverviewService.makeModeAndReturnView(pEB, ne, bc, genericService, paycheckService, promotionService,loadConfigurationBean(request));
        if (!(wObj instanceof EmployeeBean))
            return (String.valueOf(wObj));

        pEB = (EmployeeBean) wObj;

        addSessionAttribute(request, IConstants.NAMED_ENTITY, ne);
        model.addAttribute("namedEntity", ne);
        model.addAttribute("employeeBean", pEB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(userId).setFromClass(getClass());
        if(bc.isPensioner())
            Navigator.getInstance(userId).setFromForm("redirect:pensionerOverviewForm.do?eid=" + ne.getId());
        else
        Navigator.getInstance(userId).setFromForm("redirect:employeeOverviewForm.do?eid=" + ne.getId());


        HrPassportInfo pPhoto = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), ne.getId()));

        if (pPhoto.getId() == null) {
            model.addAttribute("photo", "");
        } else {
            addSessionAttribute(request, EmployeeEnquiryOverviewForm.PASSPORT_KEY, pPhoto);
            model.addAttribute("photo", pPhoto);
        }

        //Set Context Menu Items On Right Click Event
        if (!pEB.getHiringInfo().isNewEntity())
            model.addAttribute("contextMenuItems", getContextMenuItems(request, pEB.getHiringInfo(), bc));


        return this.VIEW_NAME;


    }

    @RequestMapping(method = {RequestMethod.GET}, params = {"eid", "ronly"})
    public String setupForm(@RequestParam("eid") Long empId, @RequestParam("ronly") int pReadOnly, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        Object userId = getSessionId(request);
        BusinessCertificate bc = super.getBusinessCertificate(request);
        NamedEntity ne = new NamedEntity();
        ne.setId(empId);

        EmployeeBean pEB = new EmployeeBean();
        ne.setMode("edit");
        pEB.setEmployee(IppmsUtils.loadEmployee(genericService,empId,bc));
        pEB.setReadOnly(true);

        Object wObj = EmpGenOverviewService.makeModeAndReturnView(pEB, ne, this.getBusinessCertificate(request), genericService, paycheckService, promotionService,loadConfigurationBean(request));

        pEB = (EmployeeBean) wObj;

        addSessionAttribute(request, IConstants.NAMED_ENTITY, ne);
        model.addAttribute("namedEntity", ne);
        model.addAttribute("employeeBean", pEB);
        model.addAttribute("roleBean", bc);
        Navigator.getInstance(userId).setFromClass(getClass());
        if(bc.isPensioner())
            Navigator.getInstance(userId).setFromForm("redirect:pensionerOverviewForm.do?eid=" + ne.getId()+ "&ronly=1");
        else
            Navigator.getInstance(userId).setFromForm("redirect:employeeOverviewForm.do?eid=" + ne.getId()+ "&ronly=1");


        HrPassportInfo pPhoto = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), ne.getId()));

        if (pPhoto.getId() == null) {
            model.addAttribute("photo", "");
        } else {
            addSessionAttribute(request, EmployeeEnquiryOverviewForm.PASSPORT_KEY, pPhoto);
            model.addAttribute("photo", pPhoto);
        }

        //Set Context Menu Items On Right Click Event
        if (!pEB.getHiringInfo().isNewEntity())
            model.addAttribute("contextMenuItems", getContextMenuItems(request, pEB.getHiringInfo(), bc));


        return this.ENQ_VIEW;


    }

    


    private List<NamedEntity> getContextMenuItems(HttpServletRequest request, AbstractHiringInfoEntity pHI, BusinessCertificate bc) {
        List<NamedEntity> wRetList = new ArrayList<NamedEntity>();

        if (menuService.canUserAccessURL(bc, "/paySlip.do", "/paySlip.do"))
            wRetList.add(new NamedEntity("View Last Paycheck", request.getContextPath() + "/paySlip.do?eid=" + pHI.getParentId()));
        if (menuService.canUserAccessURL(bc, "/viewPayslipHistory.do", "/viewPayslipHistory.do"))
            wRetList.add(new NamedEntity("View Payslip History", request.getContextPath() + "/viewPayslipHistory.do?eid=" + pHI.getParentId()));
        if (menuService.canUserAccessURL(bc, "/generatePRC.do", "/generatePRC.do"))
            wRetList.add(new NamedEntity("View Pay Record Card", request.getContextPath() + "/generatePRC.do?eid=" + pHI.getParentId()));
        if (menuService.canUserAccessURL(bc, "/viewEmpPromoHistory.do", "/viewEmpPromoHistory.do") && !bc.isPensioner())
            wRetList.add(new NamedEntity("View Promotion History", request.getContextPath() + "/viewEmpPromoHistory.do?eid=" + pHI.getParentId()));
        if (menuService.canUserAccessURL(bc, "/viewEmpTransferHistory.do", "/viewEmpTransferHistory.do"))
            wRetList.add(new NamedEntity("View Transfer History", request.getContextPath() + "/viewEmpTransferHistory.do?eid=" + pHI.getParentId()));

        if (pHI.isTerminatedEmployee()) {
            if (menuService.canUserAccessURL(bc, "/reinstateEmployee.do", "/reinstateEmployee.do"))
                wRetList.add(new NamedEntity("Reinstate " + bc.getStaffTypeName(), request.getContextPath() + "/reinstateEmployee.do?eid=" + pHI.getParentId()));
        } else if (pHI.isSuspendedEmployee()) {
            if (menuService.canUserAccessURL(bc, "/reabsorbEmployee.do", "/reabsorbEmployee.do"))
                wRetList.add(new NamedEntity("Reabsorb " + bc.getStaffTypeName(), request.getContextPath() + "/reabsorbEmployee.do?eid=" + pHI.getParentId()));
        } else {
            if (menuService.canUserAccessURL(bc, "/promoteEmployee.do", "/promoteEmployee.do") && !bc.isPensioner())
                wRetList.add(new NamedEntity("Promote Employee", request.getContextPath() + "/promoteEmployee.do?eid=" + pHI.getParentId()));
            if (menuService.canUserAccessURL(bc, "/transferEmployee.do", "/transferEmployee.do"))
                wRetList.add(new NamedEntity("Transfer " + bc.getStaffTypeName(), request.getContextPath() + "/transferEmployee.do?eid=" + pHI.getParentId()));
            if (menuService.canUserAccessURL(bc, "/reassignEmpDeptForm.do", "/reassignEmpDeptForm.do") && !bc.isPensioner())
                wRetList.add(new NamedEntity("Reassign Employee", request.getContextPath() + "/reassignEmpDeptForm.do?eid=" + pHI.getParentId() + "&oid=3"));
            if (menuService.canUserAccessURL(bc, "/terminateEmployee.do", "/terminateEmployee.do"))
                wRetList.add(new NamedEntity("Terminate " + bc.getStaffTypeName(), request.getContextPath() + "/terminateEmployee.do?eid=" + pHI.getParentId()));
            if (menuService.canUserAccessURL(bc, "/suspendEmployee.do", "/suspendEmployee.do"))
                wRetList.add(new NamedEntity("Suspend " + bc.getStaffTypeName(), request.getContextPath() + "/suspendEmployee.do?eid=" + pHI.getParentId()));

        }
        return wRetList;

    }


}