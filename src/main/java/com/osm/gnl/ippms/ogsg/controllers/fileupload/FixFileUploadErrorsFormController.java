package com.osm.gnl.ippms.ogsg.controllers.fileupload;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.beans.FileParseBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Vector;

 
@Controller
@RequestMapping({"/fixFileUploadError.do"})
@SessionAttributes(types={FileParseBean.class})
public class FixFileUploadErrorsFormController extends BaseController
{

  private final String VIEW = "fileupload/fileUploadErrorFixForm";
  public FixFileUploadErrorsFormController()
  { }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"uid"})
  public String setupForm(@RequestParam("uid") String pEmpId, Model model, HttpServletRequest request) throws Exception
  {

	  SessionManagerService.manageSession(request);
	  BusinessCertificate bc = super.getBusinessCertificate(request);
	  
    Object wFPB = getSessionAttribute(request, pEmpId);
    if ((wFPB == null) || (!wFPB.getClass().isAssignableFrom(FileParseBean.class))) {
      return "redirect:fileUploadFailed.do";
    }
    FileParseBean _wFPB = (FileParseBean)wFPB;

    switch (_wFPB.getObjectTypeInd()) {
    case 3:
      _wFPB.setDisplayErrors("Deduction Error Records To Fix");
      _wFPB.setDisplayTitle("Deduction Fileupload Error Summary");
      _wFPB.setName("Deductions");
      _wFPB.setDeduction(true);
      break;
    case 2:
      _wFPB.setDisplayErrors("Loan Error Records To Fix.");
      _wFPB.setDisplayTitle("Loan Fileupload Error Summary");
      _wFPB.setName("Loans");
      _wFPB.setLoan(true);
      break;
    case 5:
      _wFPB.setSpecialAllowance(true);
      _wFPB.setName("Special Allowance");
      _wFPB.setDisplayErrors("Special Allowance Error Records To Add.");
      _wFPB.setDisplayTitle("Special Allowance Fileupload Error Summary");
    case 4:
    }

    if (_wFPB.isHasErrorList()) {
      _wFPB.setErrorList(setFormDisplayStyle(_wFPB.getErrorList()));
    }
    model.addAttribute("roleBean", bc);
    model.addAttribute("failedUploadBean", _wFPB);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"uid", "s"})
  public String setupForm(@RequestParam("uid") String pEmpId, @RequestParam("s") int pSaved, Model model, HttpServletRequest request) throws Exception
  {

	  SessionManagerService.manageSession(request);
	  BusinessCertificate bc = super.getBusinessCertificate(request);
	  
    Object wFPB = getSessionAttribute(request, pEmpId);
    int objectTypeInd = 0;
    if ((wFPB == null) || (!wFPB.getClass().isAssignableFrom(FileParseBean.class))) {
      return "redirect:fileUploadFailed.do";
    }
    objectTypeInd = ((FileParseBean)wFPB).getObjectTypeInd();
    removeSessionAttribute(request, pEmpId);

    FileParseBean _wFPB = new FileParseBean();

    switch (objectTypeInd) {
    case 3:
      _wFPB.setDisplayErrors("Deductions Added Successfully.");
      _wFPB.setDisplayTitle("Deduction Summary");
      break;
    case 2:
      _wFPB.setDisplayErrors("Loans Added Successfully.");
      _wFPB.setDisplayTitle("Loan Summary");
      break;
    case 5:
      _wFPB.setDisplayErrors("Special Added Successfully.");
      _wFPB.setDisplayTitle("Special Allowance Summary");
    case 4:
    }

    model.addAttribute("saved", Boolean.valueOf(pSaved == 1));
    model.addAttribute("roleBean", bc);
    model.addAttribute("failedUploadBean", _wFPB);

    return VIEW;
  }

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String processSubmit(@RequestParam(value="_cancel", required=false) String cancel, @ModelAttribute("failedUploadBean") FileParseBean pEHB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
    throws Exception
  {

	  SessionManagerService.manageSession(request);
	  BusinessCertificate bc = super.getBusinessCertificate(request);
	  
    if(super.isCancelRequest(request, cancel))
    {
      return "redirect:fileUploadReport.do?uid=" + pEHB.getUniqueUploadId();
    }

    Vector<NamedEntity> wNewErrorList = new Vector<>();
    PayrollFlag wPF;
    switch (pEHB.getObjectTypeInd()) {
    case 3:
      for (NamedEntity n : pEHB.getErrorList()) {
        boolean errorRecord = false;
          boolean codeError = false;
        StringBuffer wErrorMsgBuffer = new StringBuffer();
        if (n.getStaffId() == null) {
          wErrorMsgBuffer.append("Invalid "+bc.getStaffTypeName());
          errorRecord = true;
        }
        else if (!pEHB.getEmployeeMap().containsKey(n.getStaffId().trim().toUpperCase())) {
        	
        	if(pEHB.getInactiveEmpMap().containsKey(n.getStaffId().trim().toUpperCase())){
        		wErrorMsgBuffer.append(bc.getStaffTypeName()+" is Terminated");
                errorRecord = true;
        	}else if(pEHB.getSuspendedMap().containsKey(n.getStaffId().trim().toUpperCase())){
                wErrorMsgBuffer.append(bc.getStaffTypeName()+" is Suspended");
                errorRecord = true;
            }
        	else{
        		 wErrorMsgBuffer.append("Invalid "+bc.getStaffTitle());
                 errorRecord = true;
        	}
         
        } 
        else {
            NamedEntity namedEntity = pEHB.getEmployeeMap().get(n.getStaffId().trim().toUpperCase());
          n.setName(namedEntity.getName());
          n.setId(namedEntity.getId());
          n.setOrganization(namedEntity.getOrganization());
          n.setPayTypeName(namedEntity.getPayTypeName());
          n.setLevelAndStep(namedEntity.getLevelAndStep());
        }

        if (n.getObjectCode() == null) {
          if (!errorRecord)
            errorRecord = !errorRecord;
          else {
            wErrorMsgBuffer.append(" | ");
          }
          n.setObjectCode("");
          wErrorMsgBuffer.append("Invalid Deduction Code");
        }
        else if (!pEHB.getObjectMap().containsKey(n.getObjectCode().trim().toUpperCase())) {
          wErrorMsgBuffer.append("Invalid Deduction Code");
          codeError = true;
          errorRecord = true;
        }

        if(!errorRecord && n.getObjectCode() != null){
        	if(pEHB.getObjectMap().containsKey(n.getObjectCode().trim().toUpperCase())){
        		EmpDeductionType eDT = (EmpDeductionType) pEHB.getObjectMap().get(n.getObjectCode().trim().toUpperCase());
                if(eDT != null && eDT.getPayTypes().isUsingPercentage()){
                    if(n.getDeductionAmount() > pEHB.getConfigurationBean().getMaxDeductionValue()){

                        wErrorMsgBuffer.append("Amount must be less than "+pEHB.getConfigurationBean().getMaxDeductionValue());
                        errorRecord = true;
                    }
                }
        		if(eDT.isMustEnterDate()){
        			if(IppmsUtils.isNullOrEmpty(n.getStartDateString())){
        				if (!errorRecord)
        		            errorRecord = true;
        		          else {
        		            wErrorMsgBuffer.append(" | ");
        		          }
        		          n.setStartDateString("");
        		          wErrorMsgBuffer.append("Invalid Start Date");
        			}
        			else if(IppmsUtils.isNullOrEmpty(n.getEndDateString())){
        				if (!errorRecord)
        		            errorRecord = true;
        		          else {
        		            wErrorMsgBuffer.append(" | ");
        		          }
        		          n.setEndDateString("");
        		          wErrorMsgBuffer.append("Invalid End Date");
        			}
        			else{
        				
        				LocalDate wStartDate = null;
                        LocalDate wEndDate = null;
        				
        				try{
        					wStartDate = PayrollBeanUtils.setDateFromStringExcel(n.getStartDateString());
            		        
            		        n.setStartDate(wStartDate);
            		        
        				}
        				catch(Exception wEx){
                            errorRecord = true;
            		          n.setStartDateString("");
            		          wErrorMsgBuffer.append("Invalid Start Date");
        				}
        				
        				try{
            		        wEndDate = PayrollBeanUtils.setDateFromStringExcel(n.getEndDateString());
            		        n.setEndDate(wEndDate);
        				}
        				catch(Exception wEx){
        					if (!errorRecord)
            		            errorRecord = true;
            		          else {
            		            wErrorMsgBuffer.append(" | ");
            		          }
            		          n.setEndDateString("");
            		          wErrorMsgBuffer.append("Invalid End Date");
        				}
        				
        				
        		        
        		        PayrollFlag wPayFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
    		        	LocalDate wLastApproveDate  = LocalDate.of(wPayFlag.getApprovedYearInd(), wPayFlag.getApprovedMonthInd(),1);
        		        if(wStartDate.compareTo(wEndDate) >= 0){
        		        	if (!errorRecord)
            		            errorRecord = true;
            		          else {
            		            wErrorMsgBuffer.append(" | ");
            		          }
            		          //n.setStartDateString("");
            		          wErrorMsgBuffer.append("Deduction Start Date must be before Deduction End Date");
	   		        		 
	   		        	}
	   		        	//Now check if the Start Date is after the last Payroll Run
	   		        	if(wStartDate.getMonthValue() < wLastApproveDate.getMonthValue()
	   		        			&& wStartDate.getYear() <= wLastApproveDate.getYear() ){
	   		        		if (!errorRecord)
            		            errorRecord = true;
            		          else {
            		            wErrorMsgBuffer.append(" | ");
            		          }
            		          //n.setDeductionCode("");
            		          wErrorMsgBuffer.append("Deduction Start Date must be after last payroll approval period");
	   		        		 
	   		        	}
	   		        	//Now check if there is a pending paycheck for the start date chosen...
	   		        	if(n.getId() > 0){
		   		        	if(IppmsUtilsExt.paycheckExistsForEmployee(genericService,bc,wStartDate.getMonthValue(),wStartDate.getYear(),n.getId())){
		   		        		if (!errorRecord)
	            		            errorRecord = true;
	            		          else {
	            		            wErrorMsgBuffer.append(" | ");
	            		          }
	            		          //n.setDeductionCode("");
	            		          wErrorMsgBuffer.append("Paychecks exist for this deduction start date. Please change dates.");
		   		        		
		   		        	}
	   		        	}
   		        	
        			}
        		}
        	}
        }
        

        if (errorRecord) {
          n.setDisplayErrors(wErrorMsgBuffer.toString());
          wNewErrorList.add(n);
        } else {

          n.setDisplayErrors(null);
          n.setDeductionAmountStr(PayrollHRUtils.getDecimalFormat().format(n.getDeductionAmount()));
            if(n.getStartDate() != null && n.getEndDate() != null)
                n.setStartAndEndPeriod(PayrollBeanUtils.getDateAsString(n.getStartDate())+" - "+PayrollBeanUtils.getDateAsString(n.getEndDate()));
          pEHB.getListToSave().add(n);
          
          
        }

      }

      break;
    case 2:
      for (NamedEntity n : pEHB.getErrorList()) {
        boolean errorRecord = false;
        StringBuffer wErrorMsgBuffer = new StringBuffer();
        if (n.getStaffId() == null) {
          wErrorMsgBuffer.append("Invalid "+bc.getStaffTitle());
          errorRecord = true;
        }
        else if (!pEHB.getEmployeeMap().containsKey(n.getStaffId().trim().toUpperCase())) {
        	if(pEHB.getInactiveEmpMap().containsKey(n.getStaffId().trim().toUpperCase())){
        		wErrorMsgBuffer.append(bc.getStaffTypeName()+" is Terminated");
                errorRecord = true;
        	}else if(pEHB.getSuspendedMap().containsKey(n.getStaffId().trim().toUpperCase())){
                wErrorMsgBuffer.append(bc.getStaffTypeName()+" is Suspended");
                errorRecord = true;
            }
        	else{
        		 wErrorMsgBuffer.append("Invalid "+bc.getStaffTitle());
                 errorRecord = true;
        	}
        } else {
          n.setName((pEHB.getEmployeeMap().get(n.getStaffId().trim().toUpperCase())).getName());
          n.setId((pEHB.getEmployeeMap().get(n.getStaffId().trim().toUpperCase())).getId());
        }

        if (n.getObjectCode() == null) {
          if (!errorRecord)
            errorRecord = !errorRecord;
          else {
            wErrorMsgBuffer.append(" | ");
          }
          n.setObjectCode("");
          wErrorMsgBuffer.append("Invalid Loan Code");
        }
        else if (!pEHB.getObjectMap().containsKey(n.getObjectCode().trim().toUpperCase())) {
          wErrorMsgBuffer.append("Invalid Loan Code");
          errorRecord = true;
        }
        try
        {
          Double.parseDouble(String.valueOf(n.getLoanBalance()));
        } catch (Exception wEx) {
          if (!errorRecord)
            errorRecord = true;
          else {
            wErrorMsgBuffer.append(" | ");
          }
          n.setLoanBalance(0.0D);
          wErrorMsgBuffer.append("Invalid Loan Balance");
        }
        try {
          Double.parseDouble(String.valueOf(n.getTenor()));
        } catch (Exception wEx) {
          if (!errorRecord)
            errorRecord = true;
          else {
            wErrorMsgBuffer.append(" | ");
          }
          n.setTenor(0.0D);
          wErrorMsgBuffer.append("Invalid Loan Tenor");
        }
        if (errorRecord) {
          n.setDisplayErrors(wErrorMsgBuffer.toString());
          wNewErrorList.add(n);
        } else {
          n.setDisplayErrors(null);
          pEHB.getListToSave().add(n);
        }

      }

      break;
    case 5:
      wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);
        SpecialAllowanceType wSAT = null;
      boolean codeError = false;
      for (NamedEntity n : pEHB.getErrorList()) {
        boolean errorRecord = false;
        StringBuffer wErrorMsgBuffer = new StringBuffer();
        if (n.getStaffId() == null) {
          wErrorMsgBuffer.append("Invalid "+bc.getStaffTitle());
          errorRecord = true;
        }
        else if (!pEHB.getEmployeeMap().containsKey(n.getStaffId().trim().toUpperCase())) {
        	if(pEHB.getInactiveEmpMap().containsKey(n.getStaffId().trim().toUpperCase())){
        		wErrorMsgBuffer.append(bc.getStaffTypeName()+" is Terminated");
                errorRecord = true;
        	}else if(pEHB.getSuspendedMap().containsKey(n.getStaffId().trim().toUpperCase())){
                wErrorMsgBuffer.append(bc.getStaffTypeName()+" is Suspended");
                errorRecord = true;
            }
        	else{
        		 wErrorMsgBuffer.append("Invalid "+bc.getStaffTitle());
                 errorRecord = true;
        	}
        } else {
          n.setName((pEHB.getEmployeeMap().get(n.getStaffId().trim().toUpperCase())).getName());
          n.setId((pEHB.getEmployeeMap().get(n.getStaffId().trim().toUpperCase())).getId());
        }

        if (n.getObjectCode() == null) {
            codeError = true;
          if (!errorRecord)
            errorRecord = true;
          else {
            wErrorMsgBuffer.append(" | ");
          }
          n.setObjectCode("");
          wErrorMsgBuffer.append("Invalid Allowance Code");
        }
        else if (!pEHB.getObjectMap().containsKey(n.getObjectCode().trim().toUpperCase())) {
          wErrorMsgBuffer.append("Invalid Allowance Code");
          errorRecord = true;
            codeError = true;
        }
        try
        {
          double amount = Double.parseDouble(String.valueOf(n.getAllowanceAmount()));
          //--If we get here.. check if the value should be less...
            if(!codeError){
                wSAT = (SpecialAllowanceType) pEHB.getObjectMap().get(n.getObjectCode().trim().toUpperCase());
                if(wSAT != null && wSAT.getPayTypes().isUsingPercentage()){
                    if(amount > pEHB.getConfigurationBean().getMaxSpecAllowValue()){
                        n.setAllowanceAmount(amount);
                        wErrorMsgBuffer.append("Amount must be less than "+pEHB.getConfigurationBean().getMaxSpecAllowValue());
                        errorRecord = true;
                    }
                }
            }
        } catch (Exception wEx) {
          if (!errorRecord)
            errorRecord = true;
          else {
            wErrorMsgBuffer.append(" | ");
          }
          n.setLoanBalance(0.0D);
          wErrorMsgBuffer.append("Invalid Amount");
        }
        LocalDate wStartDate = null;
        LocalDate wGC = null;
        try
        {
          wGC =  PayrollBeanUtils.makeNextPayPeriodStart(wPF.getApprovedMonthInd(), wPF.getApprovedYearInd());

          wStartDate = PayrollBeanUtils.setDateFromStringExcel(n.getAllowanceStartDateStr());

          if ((wStartDate.getMonthValue() <= wPF.getApprovedMonthInd()) || (wStartDate.getYear() < wPF.getApprovedYearInd()))
          {
            wStartDate = wGC;
          }
        }
        catch (Exception wEx) {
          wStartDate = wGC;
        }
        LocalDate wEndDate = null;
        wGC = null;
        try
        {
          wGC =  PayrollBeanUtils.makeNextPayPeriodStart(wPF.getApprovedMonthInd(), wPF.getApprovedYearInd());

          wEndDate = PayrollBeanUtils.setDateFromStringExcel(n.getAllowanceEndDateStr());
        }
        catch (Exception wEx)
        {
          wEndDate = null;
            if(!codeError) {
                wSAT = (SpecialAllowanceType) pEHB.getObjectMap().get(n.getObjectCode().trim().toUpperCase());
                if(wSAT.isArrearsType()){
                    wEndDate = wGC;
                }
            }

        }
        
        if (n.getPayTypeName() == null) {
            if (!errorRecord)
              errorRecord = !errorRecord;
            else {
              wErrorMsgBuffer.append(" | ");
            }
            n.setPayTypeName("");
            wErrorMsgBuffer.append("Invalid Pay Type");
          }
          else if (!pEHB.getPayTypeMap().containsKey(n.getPayTypeName().trim().toUpperCase())) {
            wErrorMsgBuffer.append("Invalid Pay Type");
            errorRecord = true;
          }
        
        if (errorRecord) {
          n.setDisplayErrors(wErrorMsgBuffer.toString());
          wNewErrorList.add(n);
        } else {

            n.setAllowanceStartDate(wStartDate);

            n.setAllowanceEndDate(wEndDate);

          pEHB.getListToSave().add(n);
        }
      }
    case 4:
    }

    pEHB.setErrorList(wNewErrorList);

    addSessionAttribute(request, pEHB.getUniqueUploadId(), pEHB);
    return "redirect:fileUploadReport.do?uid=" + pEHB.getUniqueUploadId();
  }

  private Vector<NamedEntity> setFormDisplayStyle(Vector<NamedEntity> pEmpList)
  {
    int i = 1;
    for (NamedEntity e : pEmpList) {
      if (i % 2 == 1)
        e.setDisplayStyle("reportEven");
      else {
        e.setDisplayStyle("reportOdd");
      }
      i++;
    }
    return pEmpList;
  }
}