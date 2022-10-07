package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractEmployeeAuditEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.AbstractPromotionAuditEntity;
import com.osm.gnl.ippms.ogsg.audit.domain.ReassignEmployeeLog;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({"/explainMateriality.do"})
public class MaterialityViewFormController extends BaseController
{

  public MaterialityViewFormController()
  {

  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"rm", "ry", "mid","pd"})
  public String setupForm(@RequestParam("rm") int pRunMonth, @RequestParam("ry") int pRunYear ,  @RequestParam("mid") Long pMdaDeptMapId,  @RequestParam("pd") double diff, Model model, HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, IllegalAccessException, InstantiationException {
	  SessionManagerService.manageSession(request, model);

    BusinessCertificate bc = getBusinessCertificate(request);

    MdaInfo mdaInfo = this.genericService.loadObjectById(MdaInfo.class,pMdaDeptMapId);

    String payPeriod = PayrollUtils.makeAuditPayPeriod(pRunMonth, pRunYear);

    MaterialityDisplayBean wMDB = new MaterialityDisplayBean();
    List<CustomPredicate> predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("auditPayPeriod",payPeriod));
    predicates.add(CustomPredicate.procurePredicate("auditActionType","I", Operation.EQUALS));
    predicates.add(CustomPredicate.procurePredicate("mdaInfo.id", mdaInfo.getId()));
    List<AbstractEmployeeAuditEntity> wEAList = (List<AbstractEmployeeAuditEntity>) this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getEmployeeAuditEntityClass(bc),predicates, null);

    int wTotEmpAudit = 0;
    double wNetEmpAuditSalary = 0.0D;

    for (AbstractEmployeeAuditEntity e : wEAList)
    {

        wTotEmpAudit++;
        wNetEmpAuditSalary += e.getSalaryInfo().getNetPaySansDeductions();


    }

    wMDB.setNewEmpNetEffect(wNetEmpAuditSalary);
    wMDB.setNoOfNewEmployees(wTotEmpAudit);
    wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wNetEmpAuditSalary);
    predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("auditPayPeriod",payPeriod));
    predicates.add(CustomPredicate.procurePredicate("mdaInfo.id", mdaInfo.getId()));
    predicates.add(CustomPredicate.procurePredicate("demotionInd", 0));

    List<ReassignEmployeeLog> wREALList = this.genericService.loadAllObjectsUsingRestrictions(ReassignEmployeeLog.class,predicates,null);

    int wTotEmpReassigned = 0;
    double wNetEmpReassignedSalary = 0.0D;
    for (ReassignEmployeeLog r : wREALList) {

        wTotEmpReassigned++;
        wNetEmpReassignedSalary += r.getSalaryInfo().getMonthlyGrossPay() - r.getOldSalaryInfo().getMonthlyGrossPay();
      }



    wMDB.setNoOfEmpReassigned(wTotEmpReassigned);
    wMDB.setReassignedNetEffect(wNetEmpReassignedSalary);
    wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wNetEmpReassignedSalary);
    if(!bc.isPensioner()){
      predicates = new ArrayList<>();
      predicates.add(getBusinessClientIdPredicate(request));
      predicates.add(CustomPredicate.procurePredicate("auditPayPeriod",payPeriod));
      predicates.add(CustomPredicate.procurePredicate("mdaInfo.id", mdaInfo.getId()));
      List<AbstractPromotionAuditEntity> wPAList = (List<AbstractPromotionAuditEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPromotionAuditClass(bc),predicates,null);
      int wTotEmpPromoted = 0;

      double wNetEmpPromotedSalary = 0.0D;
      for (AbstractPromotionAuditEntity p : wPAList) {
        wTotEmpPromoted++;
        wNetEmpPromotedSalary += p.getSalaryInfo().getMonthlyGrossPay() - p.getOldSalaryInfo().getMonthlyGrossPay();


      }
      wMDB.setNoOfEmpPromoted(wTotEmpPromoted);
      wMDB.setPromotedNetEffect(wNetEmpPromotedSalary);
      wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wNetEmpPromotedSalary);
    }
    predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("runMonth",pRunMonth));
    predicates.add(CustomPredicate.procurePredicate("runYear", pRunYear));
    predicates.add(CustomPredicate.procurePredicate("arrears", 0,Operation.GREATER));
    predicates.add(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", mdaInfo.getId()));
    List<AbstractPaycheckEntity> wList = (List<AbstractPaycheckEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc),predicates, null);
    int wTotWitPromotionArrears = 0;
    double wNetEmpPromotedArrears = 0.0D;
    for (AbstractPaycheckEntity p : wList) {

        wTotWitPromotionArrears++;
        wNetEmpPromotedArrears += p.getArrears();


    }

    wMDB.setNoOfEmpPromotionArrears(wTotWitPromotionArrears);
    wMDB.setPromotionArrearsNetEffect(wNetEmpPromotedArrears);
    wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wNetEmpPromotedArrears);
    predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("runMonth",pRunMonth));
    predicates.add(CustomPredicate.procurePredicate("runYear", pRunYear));
    predicates.add(CustomPredicate.procurePredicate("otherArrears", 0,Operation.GREATER));
    predicates.add(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", mdaInfo.getId()));
    List<AbstractPaycheckEntity> wList2 = (List<AbstractPaycheckEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc),predicates, null);
    int wTotWitReabsorptionArrears = 0;
    double wNetEmpReabsorptionArrears = 0.0D;
    for (AbstractPaycheckEntity p : wList2) {

        wTotWitReabsorptionArrears++;
        wNetEmpReabsorptionArrears += p.getOtherArrears();


    }

    wMDB.setNoOfEmpReabsorbArrears(wTotWitReabsorptionArrears);
    wMDB.setReabsorbedNetEffect(wNetEmpReabsorptionArrears);
    wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wNetEmpReabsorptionArrears);
    predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("runMonth",pRunMonth));
    predicates.add(CustomPredicate.procurePredicate("runYear", pRunYear));
    predicates.add(CustomPredicate.procurePredicate("leaveTransportGrant", 0,Operation.GREATER));
    predicates.add(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", mdaInfo.getId()));
     wList2 = (List<AbstractPaycheckEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc),predicates, null);
    int wTotWit13thMonthSalary = 0;
    double wNet13thMonthSalary = 0.0D;
    for (AbstractPaycheckEntity p : wList2) {

        wTotWit13thMonthSalary++;
        wNet13thMonthSalary += p.getLeaveTransportGrant(); break;

      }

    wMDB.setThirteenMonthNetEffect(wNet13thMonthSalary);
    wMDB.setNoOfEmpThirteenthMonth(wTotWit13thMonthSalary);
    wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wTotWit13thMonthSalary);
    predicates = new ArrayList<>();
    predicates.add(getBusinessClientIdPredicate(request));
    predicates.add(CustomPredicate.procurePredicate("runMonth",pRunMonth));
    predicates.add(CustomPredicate.procurePredicate("runYear", pRunYear));
    predicates.add(CustomPredicate.procurePredicate("specialAllowance", 0,Operation.GREATER));
    predicates.add(CustomPredicate.procurePredicate("mdaDeptMap.mdaInfo.id", mdaInfo.getId()));
    wList2 = (List<AbstractPaycheckEntity>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckClass(bc),predicates, null);
    int wTotWitSpecialAllowance = 0;
    double wNetSpecialAllowance = 0.0D;
    for (AbstractPaycheckEntity p : wList2) {

        wTotWitSpecialAllowance++;
        wNetSpecialAllowance += p.getSpecialAllowance();
      }

    wMDB.setSpecialAllowanceNetEffect(wNetSpecialAllowance);
    wMDB.setNoOfEmpWithSpecialAllowance(wTotWitSpecialAllowance);

    wMDB.setMaterialityTotal(wMDB.getMaterialityTotal() + wNetSpecialAllowance);
    wMDB.setName(mdaInfo.getName());
    wMDB.setAmountStr(naira+ EntityUtils.convertDoubleToEpmStandard(diff));
    wMDB.setPayPeriodStr(PayrollBeanUtils.makePayPeriod(LocalDate.of(pRunYear,pRunMonth,1)));
    addRoleBeanToModel(model, request);
    model.addAttribute("miniBean", wMDB);

    return "materialityDetailsForm";
  }
}