package com.osm.gnl.ippms.ogsg.controllers.audit;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.audit.domain.*;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.AuditService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.report.utility.AuditLogsReportGenerator;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceType;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.SetupEmployeeMaster;
import com.osm.gnl.ippms.ogsg.domain.garnishment.EmpGarnishmentType;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.domain.suspension.ReinstatementLog;
import com.osm.gnl.ippms.ogsg.domain.transfer.TransferLog;
import com.osm.gnl.ippms.ogsg.employee.beans.BusinessEmpOVBeanInactive;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;


@Controller
public class AuditLogExcelMultiController extends BaseController {
	   

	  private final AuditService auditService;
	  @Autowired
	  public AuditLogExcelMultiController(AuditService auditService)
	  {
		  this.auditService = auditService;
	  }

	  
	  @RequestMapping({"/exportTransferLog.do"})
	  public void generateTransferLogExcel(@RequestParam("fd") String pFromDate,
			  @RequestParam("td") String  pToDate,@RequestParam("uid") Long  pUserId,@RequestParam("eid") Long  pEmployeeId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		  SessionManagerService.manageSession(request, model);

		  BusinessCertificate bc = getBusinessCertificate(request);

		  ReportGeneratorBean rt = new ReportGeneratorBean();

		  AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

	    LocalDate fDate = null;
	    LocalDate tDate = null;
  		if(IppmsUtils.isNotNullOrEmpty(pFromDate) && IppmsUtils.isNotNullOrEmpty(pToDate)) {
			fDate = PayrollBeanUtils.setDateFromString(pFromDate);
			tDate = PayrollBeanUtils.setDateFromString(pToDate);
		}



	    List<TransferLog> wTransferLogList = this.auditService.loadTransferAuditLogsForExport(fDate,tDate,pUserId,pEmployeeId, bc);

		  PaginatedBean pList = new PaginatedBean(wTransferLogList);

		  pList.setSingleEmployee(IppmsUtils.isNotNullAndGreaterThanZero(pEmployeeId));

		  if(pList.isSingleEmployee()){
			  AbstractEmployeeEntity wEmp = IppmsUtils.loadEmployee(genericService,pEmployeeId, super.getBusinessCertificate(request));
			  pList.setEmployeeId(wEmp.getDisplayNameWivTitlePrefixed() + " [ "+wEmp.getEmployeeId() + " ] ");
		  }
		  if(!pFromDate.equals("") && !pToDate.equals("")){
			  pList.setFromDateStr(pFromDate);
			  pList.setToDateStr(pToDate);
		  }else{
			  pList.setFromDateStr("");
			  pList.setToDateStr("All Dates");
		  }



		  pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		  if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			  pList.setUserName((this.genericService.loadObjectById(User.class, pUserId)).getActualUserName());


		  List<Map<String, Object>> contPenMapped = new ArrayList<>();
		  for (TransferLog data : wTransferLogList) {
			  Map<String, Object> newData = new HashMap<>();
			  if(!pList.isSingleEmployee()) {
				  newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
				  newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
			  }
			  newData.put("Transferred From", data.getOldMda());
			  newData.put("Transferred To", data.getNewMda());
			  if(!pList.isFilteredByUserId()) {
				  newData.put("Transferred By", data.getUser().getActualUserName());
			  }
			  newData.put("Transferred Date", data.getTransferDateStr());
			  contPenMapped.add(newData);
		  }

		  List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		  if(!pList.isSingleEmployee()) {
			  tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
			  tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		  }
		  tableHeaders.add(new ReportGeneratorBean("Transferred From", 0));
		  tableHeaders.add(new ReportGeneratorBean("Transferred To", 0));
		  if(!pList.isFilteredByUserId()) {
			  tableHeaders.add(new ReportGeneratorBean("Transferred By", 0));
		  }
		  tableHeaders.add(new ReportGeneratorBean("Transferred Date", 0));

		  List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		  for(ReportGeneratorBean head : tableHeaders){
			  Map<String, Object> mappedHeader = new HashMap<>();
			  mappedHeader.put("headerName", head.getHeaderName());
			  mappedHeader.put("totalInd", head.getTotalInd());
			  OtherSpecHeaders.add(mappedHeader);
		  }

		  List<String> mainHeaders = new ArrayList<>();
		  mainHeaders.add("Transfer Logs");
		  if(pList.isSingleEmployee()){
			  mainHeaders.add("Transfers Relating To "+pList.getEmployeeId());
		  }
		  if(pList.isFilteredByUserId()){
		  	mainHeaders.add("Transfers By : "+pList.getEmployeeName());
		  }
		  if(pList.isFilteredByType()){
			  mainHeaders.add(pList.getTypeName()+" Deduction Audit Log");
		  }
		  mainHeaders.add("Audit Period "+pList.getFromDateStr()+" - "+pList.getToDateStr());

		  rt.setBusinessCertificate(bc);
		  rt.setGroupBy(null);
		  rt.setReportTitle("Transfer_Logs");
		  rt.setMainHeaders(mainHeaders);
		  rt.setSubGroupBy(null);
		  rt.setTableData(contPenMapped);
		  rt.setTableHeaders(OtherSpecHeaders);
		  rt.setTableType(0);
		  rt.setTotalInd(0);

		  auditLogsReportGenerator.getExcel(response, request, rt);
	  }
	  
	  @RequestMapping({"/deductionAuditExcel.do"})
	  public void generateDeductionAuditLogReport(@RequestParam("uid") Long  pUserId,@RequestParam("sDate") String pFromDate,
			  @RequestParam("eDate") String pToDate,@RequestParam("dtid") Long pTypeId,@RequestParam("eid") Long pEmpId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	      SessionManagerService.manageSession(request, model);

	      AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();
	      ReportGeneratorBean rt = new ReportGeneratorBean();

	      BusinessCertificate bc = getBusinessCertificate(request);

		  LocalDate fDate = null;
		  LocalDate tDate = null;
		  boolean usingDates = false;
		  if (IppmsUtils.isNotNullOrEmpty(pFromDate) && IppmsUtils.isNotNullOrEmpty(pToDate)) {
			  fDate = PayrollBeanUtils.setDateFromString(pFromDate);
			  tDate = PayrollBeanUtils.setDateFromString(pToDate);
			  usingDates = !usingDates;

		  }

		  List<AbstractDeductionAuditEntity> wDeductionLogList = auditService.loadDeductionAuditLogsForExport(bc, fDate, tDate, pUserId, pTypeId, pEmpId);

		  Comparator<AbstractDeductionAuditEntity> comparator = Comparator.comparing(AbstractDeductionAuditEntity::getAuditTimeStamp);
		  Collections.sort(wDeductionLogList, comparator.reversed());

		  PaginatedBean pList = new PaginatedBean(wDeductionLogList);

		  if (usingDates) {
			  pList.setFromDateStr(pFromDate);
			  pList.setToDateStr(pToDate);
		  } else {
			  pList.setFromDateStr("");
			  pList.setToDateStr("All Dates");
		  }

		  if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)) {
			  pList.setTypeName((genericService.loadObjectById(EmpDeductionType.class, pTypeId)).getDescription());

		  }
		  pList.setFilteredByType(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId));
		  pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		  if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			  pList.setEmployeeName(genericService.loadObjectById(User.class, pUserId).getActualUserName());

		  List<Map<String, Object>> contPenMapped = new ArrayList<>();
		  for (AbstractDeductionAuditEntity data : wDeductionLogList) {
			  Map<String, Object> newData = new HashMap<>();
			  newData.put(bc.getStaffTitle(), data.getPlaceHolder());
			  newData.put(bc.getStaffTypeName(), data.getEmployeeName());
			  if(!pList.isFilteredByType()){
				  newData.put("Deduction Type", data.getDeductType());
			  }
			  newData.put("Old Value", data.getOldValue());
			  newData.put("New Value", data.getNewValue());
			  newData.put("Column Changed", data.getColumnChanged());
			  if(!pList.isFilteredByUserId()) {
				  newData.put("Changed By", data.getChangedBy());
			  }
			  newData.put("Changed Date", data.getAuditTimeStamp());
			  contPenMapped.add(newData);
		  }

		  List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		  tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		  tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		  if(!pList.isFilteredByType()) {
			  tableHeaders.add(new ReportGeneratorBean("Deduction Type", 0));
		  }
		  tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		  tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		  tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
		  if(!pList.isFilteredByUserId()) {
			  tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		  }
		  tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		  List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		  for(ReportGeneratorBean head : tableHeaders){
			  Map<String, Object> mappedHeader = new HashMap<>();
			  mappedHeader.put("headerName", head.getHeaderName());
			  mappedHeader.put("totalInd", head.getTotalInd());
			  OtherSpecHeaders.add(mappedHeader);
		  }

		  List<String> mainHeaders = new ArrayList<>();
		  if(pList.isFilteredByUserId()){
		  	mainHeaders.add("Deduction Changes By "+pList.getEmployeeName());
		  }
		  if(pList.isFilteredByType()){
		  	mainHeaders.add(pList.getTypeName()+" Deduction Audit Log");
		  }
		  mainHeaders.add("Audit Period "+pList.getFromDateStr()+" - "+pList.getToDateStr());

		  rt.setBusinessCertificate(bc);
		  rt.setGroupBy(null);
		  rt.setReportTitle("Deduction_Audit_Logs");
		  rt.setMainHeaders(mainHeaders);
		  rt.setSubGroupBy(null);
		  rt.setTableData(contPenMapped);
		  rt.setTableHeaders(OtherSpecHeaders);
		  rt.setTableType(0);
		  rt.setTotalInd(0);

		  auditLogsReportGenerator.getExcel(response, request, rt);
	  }


	@RequestMapping({"/deductionAuditExcelND.do"})
	public ModelAndView generateDeductionAuditLogReportWithoutDates(@RequestParam("uid") Long  pUserId,@RequestParam("dtid") 
			Long  pTypeId,@RequestParam("eid") Long pEmpId,Model model, HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);

		PaginatedBean pList = AuditLogExcelMultiControllerService.prepareDeductionAudit(genericService,auditService,this.getBusinessCertificate(request),""
				,"", pUserId,pTypeId,pEmpId);

		return new ModelAndView("deductionLogExcelView", "dedLogBean", pList);
		}
	
	 @RequestMapping({"/specAllowAuditExcel.do"})
	  public void generateSpecAllowAuditLogReport(@RequestParam("uid") Long  pUserId,@RequestParam("sDate") String pFromDate,
			  @RequestParam("eDate") String  pToDate,@RequestParam("stid") Long  pTypeId,@RequestParam("eid") Long  pEmpId,Model model, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception {
	      SessionManagerService.manageSession(request, model);

	      BusinessCertificate bc = getBusinessCertificate(request);

	      ReportGeneratorBean rt = new ReportGeneratorBean();


	      AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		 LocalDate fDate = null;
		 LocalDate tDate = null;
		 boolean usingDates = false;
		 if (IppmsUtils.isNotNullOrEmpty(pFromDate) && IppmsUtils.isNotNullOrEmpty(pToDate)) {
			 fDate = PayrollBeanUtils.setDateFromString(pFromDate);
			 tDate = PayrollBeanUtils.setDateFromString(pToDate);
			 usingDates = !usingDates;

		 }


		 List<AbstractSpecAllowAuditEntity> wSpecAllowLogList = auditService.loadSpecAllowAuditLogsForExport(bc, fDate, tDate, pUserId, pTypeId, pEmpId);

		 Comparator<AbstractSpecAllowAuditEntity> comparator = Comparator.comparing(AbstractSpecAllowAuditEntity::getAuditTimeStamp);
		 Collections.sort(wSpecAllowLogList, comparator.reversed());

		 PaginatedBean pList = new PaginatedBean(wSpecAllowLogList);

		 if (usingDates) {
			 pList.setFromDateStr(pFromDate);
			 pList.setToDateStr(pToDate);
		 } else {
			 pList.setFromDateStr("");
			 pList.setToDateStr("All Dates");
		 }

		 if (IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)) {
			 pList.setTypeName((genericService.loadObjectById(SpecialAllowanceType.class, pTypeId)).getDescription());

		 }
		 pList.setFilteredByType(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId));
		 pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		 if (IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			 pList.setEmployeeName(genericService.loadObjectById(User.class, pUserId).getActualUserName());
		 pList.setUsingDates(usingDates);


		 List<Map<String, Object>> contPenMapped = new ArrayList<>();
		 for (AbstractSpecAllowAuditEntity data : wSpecAllowLogList) {
			 Map<String, Object> newData = new HashMap<>();
			 newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
			 newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
			 if(!pList.isFilteredByType()){
				 newData.put("Special Allowance Type", data.getAllowanceType());
			 }
			 newData.put("Old Value", data.getOldValue());
			 newData.put("New Value", data.getNewValue());
			 if(!pList.isFilteredByUserId()) {
				 newData.put("Changed By", data.getChangedBy());
			 }
			 newData.put("Changed Date", data.getAuditTime());
			 newData.put(bc.getMdaTitle(), data.getEmployee().getMdaName());
			 contPenMapped.add(newData);
		 }

		 List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		 tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		 tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		 if(!pList.isFilteredByType()) {
			 tableHeaders.add(new ReportGeneratorBean("Special Allowance Type", 0));
		 }
		 tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		 tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		 if(!pList.isFilteredByUserId()) {
			 tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		 }
		 tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
		 tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

		 List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		 for(ReportGeneratorBean head : tableHeaders){
			 Map<String, Object> mappedHeader = new HashMap<>();
			 mappedHeader.put("headerName", head.getHeaderName());
			 mappedHeader.put("totalInd", head.getTotalInd());
			 OtherSpecHeaders.add(mappedHeader);
		 }

		 List<String> mainHeaders = new ArrayList<>();
		 mainHeaders.add("Special Allowance Audit Logs");
		 if(pList.isFilteredByUserId()){
			 mainHeaders.add("Special Allowance Changes By "+pList.getEmployeeName());
		 }
		 if(pList.isFilteredByType()){
			 mainHeaders.add(pList.getTypeName()+" Audit Log");
		 }
		 if(pList.isUsingDates()) {
			 mainHeaders.add("Audit Period " + pList.getFromDateStr() + " - " + pList.getToDateStr());
		 }
		 else{
			 mainHeaders.add("All Audit Periods");
		 }

		 rt.setBusinessCertificate(bc);
		 rt.setGroupBy(bc.getMdaTitle());
		 rt.setReportTitle("Spec_Allow_Audit_Logs");
		 rt.setMainHeaders(mainHeaders);
		 rt.setSubGroupBy(null);
		 rt.setTableData(contPenMapped);
		 rt.setTableHeaders(OtherSpecHeaders);
		 rt.setTableType(1);
		 rt.setTotalInd(0);


		 auditLogsReportGenerator.getExcel(response, request, rt);
	  }


	@RequestMapping({"/viewTreatedNameConflictExcelReport.do"})
	public void generateTreatedNameConflictExcelReport(Model model,
												HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = getBusinessCertificate(request);

		ReportGeneratorBean rt = new ReportGeneratorBean();


		AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		List<SetupEmployeeMaster> empList = this.genericService.loadAllObjectsUsingRestrictions(SetupEmployeeMaster.class,
				Arrays.asList(getBusinessClientIdPredicate(request)), null);


		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (SetupEmployeeMaster data : empList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getEmployeeId());
			newData.put(bc.getStaffTypeName(), PayrollHRUtils.createDisplayName(data.getLastName(),
					data.getFirstName(), data.getInitials()));
			newData.put("Pay Group", data.getPayGroup());
			newData.put("Initiated By", data.getLastModBy());
			newData.put("Created Date", data.getInitiatedDateStr());
			newData.put("Treated By", data.getApprovedBy());
			newData.put("Treated Date", data.getApprovedDateStr());
			newData.put("Status", data.getFinalStatus());
			newData.put(bc.getMdaTitle(), data.getMdaName());
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		tableHeaders.add(new ReportGeneratorBean("Pay Group", 0));
		tableHeaders.add(new ReportGeneratorBean("Initiated By", 0));
		tableHeaders.add(new ReportGeneratorBean("Created Date", 0));
		tableHeaders.add(new ReportGeneratorBean("Treated By", 0));
		tableHeaders.add(new ReportGeneratorBean("Treated Date", 0));
		tableHeaders.add(new ReportGeneratorBean("Status", 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Name Conflict Logs");



		rt.setBusinessCertificate(bc);
		rt.setGroupBy(bc.getMdaTitle());
		rt.setReportTitle("Name_Conflict_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(1);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
	}


	@RequestMapping({"/subventionAuditExcelReport.do"})
	public void generatesubventionAuditExcelReport(@RequestParam("uid") Long  pUserId,@RequestParam("sDate") String pFromDate,
												   @RequestParam("eDate") String  pToDate,Model model,
												HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = getBusinessCertificate(request);

		ReportGeneratorBean rt = new ReportGeneratorBean();


		AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		LocalDate fDate = PayrollBeanUtils.setDateFromString(pFromDate);
		LocalDate tDate = PayrollBeanUtils.setDateFromString(pToDate);

		List<CustomPredicate> predicates = new ArrayList<>();
		predicates.add(getBusinessClientIdPredicate(request));

		if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			predicates.add(CustomPredicate.procurePredicate("user.id",pUserId));

		predicates.add(CustomPredicate.procurePredicate("lastModTs",PayrollBeanUtils.getNextORPreviousDay(fDate,false), Operation.GREATER));
		predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(tDate,true), Operation.LESS));



		List<SubventionAudit> empList = this.genericService.loadAllObjectsUsingRestrictions(SubventionAudit.class,predicates, null);



		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (SubventionAudit data : empList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put("Subvention Name", data.getName());
			newData.put("Column Changed", data.getColumnChanged());
			newData.put("Old Value", data.getOldValue());
			newData.put("New Value", data.getNewValue());
			newData.put("Changed By", data.getChangedBy());
			newData.put("Changed Date", data.getAuditTime());
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean("Subvention Name", 0));
		tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Subvention Logs");



		rt.setBusinessCertificate(bc);
		rt.setGroupBy(null);
		rt.setReportTitle("Subvention_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(0);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
	}


	@RequestMapping({"/specAllowAuditExcelND.do"})
	public ModelAndView generateSpecAllowAuditLogReportWithoutDates(@RequestParam("uid") Long pUserId,@RequestParam("stid") 
			Long pTypeId,@RequestParam("eid") Long pEmpId,Model model,HttpServletRequest request,HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);

		PaginatedBean pList = AuditLogExcelMultiControllerService.prepareSpecAllowAudit(genericService,auditService,this.getBusinessCertificate(request),null,null,pUserId,pTypeId,pEmpId);

		return new ModelAndView("specAllowLogExcelView", "specAllowLogBean", pList);
		}
	
	@RequestMapping({"/garnishmentAuditExcel.do"})
	  public void generateGarnishmentAuditLogReport(@RequestParam("uid") Long  pUserId,@RequestParam("sDate") String pFromDate,
			  @RequestParam("eDate") String  pToDate,@RequestParam("gtid") Long  pTypeId,@RequestParam("eid") Long  pEmpId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	      SessionManagerService.manageSession(request, model);

	      BusinessCertificate bc = getBusinessCertificate(request);


		AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

	      ReportGeneratorBean rt = new ReportGeneratorBean();

		LocalDate fDate = null;
		LocalDate tDate = null;
		List<GarnishmentAudit> wGarnLogList;
		boolean useDates = false;
		if(!pFromDate.equals("") && !pToDate.equals("")){
			fDate = PayrollBeanUtils.setDateFromString(pFromDate);
			tDate = PayrollBeanUtils.setDateFromString(pToDate);
			useDates = true;
		}
		if(useDates){
			wGarnLogList = auditService.loadGarnishmentLogsForExport(bc,PayrollBeanUtils.getNextORPreviousDay(fDate, false)
					,PayrollBeanUtils.getNextORPreviousDay(tDate, true),pUserId,pTypeId,pEmpId);
		}else{
			wGarnLogList = auditService.loadGarnishmentLogsForExport(bc,null,null,pUserId,pTypeId,pEmpId);
		}

		Comparator<GarnishmentAudit> comparator = Comparator.comparing(GarnishmentAudit::getAuditTime);
		Collections.sort(wGarnLogList,comparator.reversed());

		PaginatedBean pList = new PaginatedBean(wGarnLogList);

		if(useDates){
			pList.setFromDateStr(pFromDate);
			pList.setToDateStr(pToDate);
		}else{
			pList.setFromDateStr("");
			pList.setToDateStr("All Dates");
		}

		if(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId)){
			String wName = genericService.loadObjectById(EmpGarnishmentType.class,  pTypeId).getName();
			pList.setTypeName(wName);

		}
		pList.setFilteredByType(IppmsUtils.isNotNullAndGreaterThanZero(pTypeId));
		pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			pList.setEmployeeName(genericService.loadObjectById(User.class, pUserId).getActualUserName());


		pList.setUsingDates(true);

		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (GarnishmentAudit data : wGarnLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getPlaceHolder());
			newData.put(bc.getStaffTypeName(), data.getName());
			if(!pList.isFilteredByType()){
				newData.put("Loan Type", data.getGarnishType());
			}
			newData.put("Old Value", data.getOldValue());
			newData.put("New Value", data.getNewValue());
			newData.put("Column Changed", data.getColumnChanged());
		if(!pList.isFilteredByUserId()) {
			newData.put("Changed By", data.getChangedBy());
		}
			newData.put("Changed Date", PayrollHRUtils.getDisplayDateFormat().format(data.getLastModTs()));
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		if(!pList.isFilteredByType()) {
			tableHeaders.add(new ReportGeneratorBean("Loan Type", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
		if(!pList.isFilteredByUserId()) {
			tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		if(pList.isFilteredByUserId()){
			mainHeaders.add(bc.getStaffTypeName()+" Loan Changes By "+pList.getEmployeeName());
		}
		if(pList.isFilteredByType()){
			mainHeaders.add(pList.getTypeName()+" Audit Log");
		}
		if(pList.isUsingDates()) {
			mainHeaders.add("Audit Period " + pList.getFromDateStr() + " - " + pList.getToDateStr());
		}
		else{
			mainHeaders.add("All Audit Periods");
		}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(null);
		rt.setReportTitle("Loan_Audit_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(0);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
	  }


	@RequestMapping({"/garnishmentAuditExcelND.do"})
	public ModelAndView generateGarnishmentAuditLogReportWithoutDates(@RequestParam("uid") Long  pUserId,@RequestParam("gtid") 
	Long  pTypeId,@RequestParam("eid") Long  pEmpId,Model model, HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);

		PaginatedBean pList = AuditLogExcelMultiControllerService.prepareGarnishmentAudit(genericService, auditService, this.getBusinessCertificate(request), null, null, pUserId, pTypeId, pEmpId);

		return new ModelAndView("loanAuditLogExcelView", "loanAuditLogBean", pList);
		}
	
	
	@RequestMapping({"/employeeAuditExcel.do"})
	public void generateEmployeeAuditLogReport(@RequestParam("sDate") String  pStartDate,
			@RequestParam("eDate") String  pEndDate,
			@RequestParam("uid") Long  pUserId,
			@RequestParam("eid") Long  pEmpId,
			@RequestParam("mid") Long  pMdaId,
			@RequestParam("sid") Long  pSchoolId,
			@RequestParam("pp") String  pPayPeriod,
			Model model, HttpServletRequest request, 
			HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);


			AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		    ReportGeneratorBean rt = new ReportGeneratorBean();

		    BusinessCertificate bc = getBusinessCertificate(request);
		 
		  LocalDate wStartDate = null;
		  LocalDate wEndDate = null;
		  boolean usingDates = true;
		  if(IppmsUtils.isNotNull(pStartDate) 
				  && IppmsUtils.isNotNull(pEndDate) ){
			  wStartDate = PayrollBeanUtils.setDateFromString(pStartDate);
			  wEndDate = PayrollBeanUtils.setDateFromString(pEndDate);
			  
		  }else{
			  usingDates = false;
		  }
		  List<AbstractEmployeeAuditEntity> wEmpLogList;
		  
		  boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUserId);
		  boolean useEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);
		  
		  if(usingDates){
			  wEmpLogList = this.auditService.loadEmployeeLogsForExport(getBusinessCertificate(request),PayrollBeanUtils.getNextORPreviousDay(wStartDate, false),
					  PayrollBeanUtils.getNextORPreviousDay(wEndDate, true),pUserId,pEmpId,useUserId,useEmpId,pMdaId,pSchoolId,pPayPeriod);
		  }else{
			  wEmpLogList = this.auditService.loadEmployeeLogsForExport(getBusinessCertificate(request),null,null,pUserId,pEmpId,useUserId,useEmpId,pMdaId,pSchoolId,pPayPeriod);
		  }

		Comparator<AbstractEmployeeAuditEntity> comparator = Comparator.comparing(AbstractEmployeeAuditEntity::getAuditTime);
		Collections.sort(wEmpLogList,comparator.reversed());
		
		  PaginatedBean pList = new PaginatedBean(wEmpLogList);
		  if(pSchoolId > 0){
			  pList.setSchoolName(this.genericService.loadObjectById(SchoolInfo.class, pSchoolId).getName());
			  pList.setFilteredBySchool(true);
		  }
		  if(IppmsUtils.isNotNullAndGreaterThanZero(pMdaId)){
			 
			  pList.setMdaName(this.genericService.loadObjectById(MdaInfo.class, pMdaId).getName());
			  pList.setFilteredByMda(true);
		  }
		  if(pPayPeriod.length() > 1){
			  pList.setPayPeriod(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(Integer.parseInt(pPayPeriod.substring(0, 2)), Integer.parseInt(pPayPeriod.substring(2))));
			  pList.setUsingPayPeriod(true);
		  }
		  pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		  if(usingDates){
			 pList.setFromDateStr(pStartDate+" "+pEndDate); 
		  }
		  if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
		      pList.setEmployeeName(this.genericService.loadObjectById(User.class, pUserId).getActualUserName());


		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (AbstractEmployeeAuditEntity data : wEmpLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getName());
			newData.put(bc.getStaffTypeName(), data.getEmployeeId());
			if(!pList.isFilteredByMda()){
				newData.put(bc.getMdaTitle()+" At Change", data.getMdaInfo().getName());
			}
			newData.put("Pay Group At Change", data.getPlaceHolder());
			newData.put("Old Value", data.getOldValue());
			newData.put("New Value", data.getNewValue());
			newData.put("Column Changed", data.getColumnChanged());
			if(!pList.isFilteredByUserId()) {
				newData.put("Changed By", data.getChangedBy());
			}
			newData.put("Changed Date", PayrollHRUtils.getDisplayDateFormat().format(data.getLastModTs()));
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		if(!pList.isFilteredByMda()) {
			tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle()+" At Change", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Pay Group At Change", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
		if(!pList.isFilteredByUserId()) {
			tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add(bc.getStaffTypeName()+" Logs");
		if(pList.isFilteredByUserId()){
			mainHeaders.add(bc.getStaffTypeName()+" Changes By "+pList.getEmployeeName());
		}
		if(pList.isUsingDates()) {
			mainHeaders.add("Audit Period " + pList.getFromDateStr() + " - " + pList.getToDateStr());
		}
		else{
			mainHeaders.add("All Audit Periods");
		}
		if(pList.isUsingPayPeriod()){
			mainHeaders.add("Pay Period: "+pList.getPayPeriod());
		}
		if(pList.isFilteredByMda()){
			mainHeaders.add(bc.getStaffTypeName()+" in "+bc.getMateriality()+": "+pList.getMdaName());
		}
		if(pList.isFilteredBySchool()){
			mainHeaders.add(bc.getStaffTypeName()+" in School: "+pList.getSchoolName());
		}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(null);
		rt.setReportTitle(bc.getStaffTypeName()+"_Audit_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(0);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
		}
	
	@RequestMapping({"/hireInfoAuditExcel.do"})
	public void generateHiringAuditLogReport(@RequestParam("sDate") String  pStartDate,
			@RequestParam("eDate") String  pEndDate,
			@RequestParam("uid") Long  pUserId,
			@RequestParam("eid") Long  pEmpId,Model model, HttpServletRequest request, 
			HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);


			AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		    ReportGeneratorBean rt = new ReportGeneratorBean();

		    BusinessCertificate bc = getBusinessCertificate(request);
		 
		  LocalDate wStartDate = null;
		  LocalDate wEndDate = null;
		boolean usingDates = true;
		if(IppmsUtils.isNotNull(pStartDate)
				&& IppmsUtils.isNotNull(pEndDate) ){
			wStartDate = PayrollBeanUtils.setDateFromString(pStartDate);
			wEndDate = PayrollBeanUtils.setDateFromString(pEndDate);

		}else{
			usingDates = false;
		}
		  List<EmployeeAudit> wEmpLogList;
		  
		  boolean useUserId = (pUserId != null && pUserId > 0);
		  boolean useEmpId = (pEmpId != null && pEmpId > 0);
		  
		  if(usingDates){
			  wEmpLogList = this.auditService.loadHiringInfoLogsForExport(getBusinessCertificate(request),PayrollBeanUtils.getNextORPreviousDay(wStartDate, false),
					  PayrollBeanUtils.getNextORPreviousDay(wEndDate, true),pUserId,pEmpId,useUserId,useEmpId);
		  }else{
			  wEmpLogList =  this.auditService.loadHiringInfoLogsForExport(getBusinessCertificate(request),null,null,pUserId,pEmpId,useUserId,useEmpId);
		  }
		  
		
		  Collections.sort(wEmpLogList);
		
		  PaginatedBean pList = new PaginatedBean(wEmpLogList);
		 
		  
		  	pList.setFromDateStr("");
			    pList.setToDateStr("All Dates");
		   
		  pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		  pList.setUsingDates(false);
		  if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
		      pList.setEmployeeName(this.genericService.loadObjectById(User.class, pUserId).getActualUserName());



		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (AbstractEmployeeAuditEntity data : wEmpLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getPlaceHolder());
			newData.put(bc.getStaffTypeName(), data.getName());
			newData.put("Column Changed", data.getColumnChanged());
			newData.put("Old Value", data.getOldValue());
			newData.put("New Value", data.getNewValue());
			if(!pList.isFilteredByUserId()) {
				newData.put("Changed By", data.getChangedBy());
			}
			newData.put("Changed Date", PayrollHRUtils.getDisplayDateFormat().format(data.getLastModTs()));
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		if(!pList.isFilteredByUserId()) {
			tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Hiring Info Audit Log Report");
		if(pList.isFilteredByUserId()){
			mainHeaders.add("Hiring Info Changes By "+pList.getEmployeeName());
		}
		if(pList.isUsingDates()) {
			mainHeaders.add("Audit Period " + pList.getFromDateStr() + " - " + pList.getToDateStr());
		}
		else{
			mainHeaders.add("All Audit Periods");
		}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(null);
		rt.setReportTitle("Hiring_Info_Audit_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(0);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
		}
	
	@RequestMapping({"/promotionAuditExcel.do"})
	public void generatePromotionAuditLogReport(@RequestParam("sDate") String  pStartDate,
			@RequestParam("eDate") String  pEndDate,
			@RequestParam("uid") Long  pUserId,
			@RequestParam("eid") Long  pEmpId,
			@RequestParam("mdaind") Long  pMdaInstId,
			Model model, HttpServletRequest request, 
			HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);

		    BusinessCertificate bc = getBusinessCertificate(request);

			AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		    ReportGeneratorBean rt = new ReportGeneratorBean();
		 
		  LocalDate wStartDate = null;
		  LocalDate wEndDate = null;
		boolean usingDates = true;
		if(IppmsUtils.isNotNull(pStartDate)
				&& IppmsUtils.isNotNull(pEndDate) ){
			wStartDate = PayrollBeanUtils.setDateFromString(pStartDate);
			wEndDate = PayrollBeanUtils.setDateFromString(pEndDate);

		}else{
			usingDates = false;
		}
		  
		  List<PromotionAudit> wEmpLogList;
		  
		  boolean useUserId = (pUserId != null && pUserId > 0);
		  boolean useEmpId = (pEmpId != null && pEmpId > 0);
		  
		  if(usingDates){
			  wEmpLogList = this.auditService.loadPromotionLogsForExport(getBusinessCertificate(request),PayrollBeanUtils.getNextORPreviousDay(wStartDate, false),
					  PayrollBeanUtils.getNextORPreviousDay(wEndDate, true),pUserId,pEmpId,pMdaInstId,useUserId,useEmpId);
		  }else{
			  wEmpLogList =  this.auditService.loadPromotionLogsForExport(getBusinessCertificate(request),null,null,pUserId,pEmpId,pMdaInstId,useUserId,useEmpId);
		  }
		  
		
		 
		  Collections.sort(wEmpLogList);
		
		  PaginatedBean pList = new PaginatedBean(wEmpLogList);
		 
		  
		  	pList.setFromDateStr("");
			    pList.setToDateStr("All Dates");
		   
		  
		   
		   
		  pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		  pList.setUsingDates(false);
		  if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
		      pList.setEmployeeName(this.genericService.loadObjectById(User.class, pUserId).getActualUserName());


		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (PromotionAudit data : wEmpLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getEmployee().getEmployeeId());
			newData.put(bc.getStaffTypeName(), data.getEmployee().getDisplayName());
			if(!pList.isFilteredByUserId()) {
				newData.put("Promoted By", data.getPromotedBy());
			}
			newData.put("Payroll Promotion Date", data.getAuditTime());
			newData.put("Old Pay Group", data.getOldSalaryInfo().getSalaryType().getName());
			newData.put("Old Level/Step", data.getOldSalaryInfo().getLevelAndStepAsStr());
			newData.put("New Pay Group", data.getSalaryInfo().getSalaryType().getName());
			newData.put("New Level/Step", data.getSalaryInfo().getLevelAndStepAsStr());
			newData.put("Old Gross Pay", data.getOldSalaryInfo().getMonthlySalary());
			newData.put("New Gross Pay", data.getSalaryInfo().getMonthlySalary());
			Double net = data.getSalaryInfo().getMonthlySalary() - data.getOldSalaryInfo().getMonthlySalary();
			newData.put("Net Difference", PayrollHRUtils.getDecimalFormat().format(net));
			newData.put(bc.getMdaTitle(), data.getMdaName());

			newData.put("Changed Date", PayrollHRUtils.getDisplayDateFormat().format(data.getLastModTs()));
			contPenMapped.add(newData);
		}

	List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		if(!pList.isFilteredByUserId()) {
			tableHeaders.add(new ReportGeneratorBean("Promoted By", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Payroll Promotion Date", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Pay Group", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Level/Step", 0));
		tableHeaders.add(new ReportGeneratorBean("New Pay Group", 0));
		tableHeaders.add(new ReportGeneratorBean("New Level/Step", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Gross Pay", 2));
		tableHeaders.add(new ReportGeneratorBean("New Gross Pay", 2));
		tableHeaders.add(new ReportGeneratorBean("Net Difference", 2));
		tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

	List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
		Map<String, Object> mappedHeader = new HashMap<>();
		mappedHeader.put("headerName", head.getHeaderName());
		mappedHeader.put("totalInd", head.getTotalInd());
		OtherSpecHeaders.add(mappedHeader);
	}

	List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Promotion Logs Report");
		if(pList.isFilteredByUserId()){
		mainHeaders.add(bc.getStaffTypeName()+" Promoted By "+pList.getEmployeeName());
	}
		if(pList.isUsingDates()) {
		mainHeaders.add("Audit Period " + pList.getFromDateStr() + " - " + pList.getToDateStr());
	}
		else{
		mainHeaders.add("All Audit Periods");
	}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(bc.getMdaTitle());
		rt.setReportTitle("Promotion_Audit_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(1);
		rt.setTotalInd(1);


		auditLogsReportGenerator.getExcel(response, request, rt);
		}
	
	@RequestMapping({"/reinstateLogExcel.do"})
	public void generateReinstatementLogReport(@RequestParam("sDate") String  pStartDate,
			@RequestParam("eDate") String  pEndDate,
			@RequestParam("uid") Long  pUserId,
			@RequestParam("eid") Long  pEmpId,
			@RequestParam("mdaind") Long  pMdaInstId,
			Model model, HttpServletRequest request, 
			HttpServletResponse response) 
		throws Exception {
		    SessionManagerService.manageSession(request, model);

		    BusinessCertificate bc = getBusinessCertificate(request);

		    ReportGeneratorBean rt = new ReportGeneratorBean();

		    AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();
		 
		  LocalDate wStartDate = null;
		  LocalDate wEndDate = null;
		boolean usingDates = true;
		if(IppmsUtils.isNotNull(pStartDate)
				&& IppmsUtils.isNotNull(pEndDate) ){
			wStartDate = PayrollBeanUtils.setDateFromString(pStartDate);
			wEndDate = PayrollBeanUtils.setDateFromString(pEndDate);

		}else{
			usingDates = false;
		}
		  
		  List<ReinstatementLog> wEmpLogList;
		  
		  boolean useUserId = IppmsUtils.isNotNullAndGreaterThanZero(pUserId);
		  boolean useEmpId = IppmsUtils.isNotNullAndGreaterThanZero(pEmpId);
		  
		  if(usingDates){
			  wEmpLogList = this.auditService.loadReinstatementLogsForExport(getBusinessCertificate(request),PayrollBeanUtils.getNextORPreviousDay(wStartDate, false),
					  PayrollBeanUtils.getNextORPreviousDay(wEndDate, true),pUserId,pEmpId,pMdaInstId,useUserId,useEmpId);
		  }else{
			  wEmpLogList =  this.auditService.loadReinstatementLogsForExport(getBusinessCertificate(request),null,null,pUserId,pEmpId,pMdaInstId,useUserId,useEmpId);
		  }
		  
		
		  
		  Collections.sort(wEmpLogList);
		
		  PaginatedBean pList = new PaginatedBean(wEmpLogList);
		  if(usingDates){
			 pList.setFromDateStr(pStartDate);
			 pList.setToDateStr(pEndDate);
		  }else{
			   
			  pList.setFromDateStr("");
			  pList.setToDateStr("All Dates");
		  }
		  pList.setUsingDates(usingDates);
		   
		  pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));

		if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			pList.setEmployeeName(this.genericService.loadObjectById(User.class, pUserId).getActualUserName());

		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (ReinstatementLog data : wEmpLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getMdaCode());
			newData.put(bc.getStaffTypeName(), data.getEmployeeName());
			if(!pList.isFilteredByUserId()) {
				newData.put("Reinstated By", data.getApprover());
			}
			newData.put("Reinstated Date", data.getReinstatedDateStr());
			newData.put("Reinstated Pay Group", data.getPayGroup());
			newData.put("Reinstated Level/Step", data.getPayGroupLevelAndStep());
			newData.put(bc.getMdaTitle(), data.getMdaName());
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		if(!pList.isFilteredByUserId()) {
			tableHeaders.add(new ReportGeneratorBean("Reinstated By", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("Reinstated Date", 0));
		tableHeaders.add(new ReportGeneratorBean("Reinstated Pay Group", 0));
		tableHeaders.add(new ReportGeneratorBean("Reinstated Level/Step", 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Reinstatement Logs");
		if(!pList.isFilteredByUserId()) {
			mainHeaders.add(bc.getStaffTypeName()+" Reinstated By " + pList.getEmployeeName());
		}
		if(pList.isUsingDates()) {
			mainHeaders.add("Audit Period: " + pList.getFromDateStr() +" - "+pList.getToDateStr());
		}
		else{
			mainHeaders.add("All Audit Periods");
		}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(bc.getMdaTitle());
		rt.setReportTitle("Reinstatement_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(1);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
		}


	@RequestMapping({"/reassignmentLogExcel.do"})
	public void generateReassignmentLogReport(@RequestParam("sDate") String  pStartDate,
											   @RequestParam("eDate") String  pEndDate,
											   @RequestParam("uid") Long  pUserId,
											   Model model, HttpServletRequest request,
											   HttpServletResponse response)
			throws Exception {
		SessionManagerService.manageSession(request, model);

		BusinessCertificate bc = getBusinessCertificate(request);

		ReportGeneratorBean rt = new ReportGeneratorBean();

		AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		boolean usingDates = IppmsUtils.isNotNull(pStartDate)
				&& IppmsUtils.isNotNull(pEndDate);

		List<ReassignEmployeeLog> wEmpLogList;

		List<CustomPredicate> predicates = new ArrayList<>();

		predicates.add(getBusinessClientIdPredicate(request));
		if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			predicates.add(CustomPredicate.procurePredicate("user.id", pUserId));
		else
			pUserId = 0L;

			wEmpLogList = this.genericService.loadAllObjectsUsingRestrictions(ReassignEmployeeLog.class, predicates, null);

		Collections.sort(wEmpLogList);

		PaginatedBean pList = new PaginatedBean(wEmpLogList);
		if(usingDates){
			pList.setFromDateStr(pStartDate);
			pList.setToDateStr(pEndDate);
		}else{

			pList.setFromDateStr("");
			pList.setToDateStr("All Dates");
		}
		pList.setUsingDates(usingDates);

		pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));

		if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			pList.setEmployeeName(this.genericService.loadObjectById(User.class, pUserId).getActualUserName());


		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (ReassignEmployeeLog data : wEmpLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getEmployeeId());
			newData.put(bc.getStaffTypeName(), data.getDisplayName());
			if(!pList.isFilteredByUserId()) {
				newData.put("Changed By", data.getUser().getActualUserName());
			}
			newData.put("From Pay Group", data.getOldSalaryInfo().getSalaryType().getName());
			newData.put("To Pay Group", data.getSalaryInfo().getSalaryType().getName());
			newData.put("From Level/Step", data.getOldSalaryInfo().getLevelStepStr());
			newData.put("To Level/Step", data.getSalaryInfo().getLevelStepStr());
			newData.put("Ref#", data.getDeptRefNumber());
			newData.put("Changed Date", data.getAuditTime());
			newData.put(bc.getMdaTitle(), data.getAssignedToObject());
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		if(!pList.isFilteredByUserId()) {
			tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		}
		tableHeaders.add(new ReportGeneratorBean("From Pay Group", 0));
		tableHeaders.add(new ReportGeneratorBean("To Pay Group", 0));
		tableHeaders.add(new ReportGeneratorBean("From Level/Step", 0));
		tableHeaders.add(new ReportGeneratorBean("To Level/Step", 0));
		tableHeaders.add(new ReportGeneratorBean("Ref#", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getMdaTitle(), 3));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("Reassignment Logs");
		if(pList.isFilteredByUserId()) {
			mainHeaders.add(bc.getStaffTypeName()+" Reassigned By " + pList.getEmployeeName());
		}
		if(pList.isUsingDates()) {
			mainHeaders.add("Audit Period: " + pList.getFromDateStr() +" - "+pList.getToDateStr());
		}
		else{
			mainHeaders.add("All Audit Periods");
		}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(bc.getMdaTitle());
		rt.setReportTitle("Reassignment_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(1);
		rt.setTotalInd(0);

		auditLogsReportGenerator.getExcel(response, request, rt);
	}



	@RequestMapping({"/employeeAccountAuditExcel.do"})
	public void generateAccountAuditLogReport(@RequestParam("uid") Long  pUserId,
													  @RequestParam("sDate") String  pStartDate,
													  @RequestParam("eDate") String  pEndDate,
													  @RequestParam("eid") Long  pEmpId,
													  Model model, HttpServletRequest request,
													  HttpServletResponse response)
			throws Exception {
		SessionManagerService.manageSession(request, model);

		AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		ReportGeneratorBean rt = new ReportGeneratorBean();

		BusinessCertificate bc = getBusinessCertificate(request);

		LocalDate wStartDate = null;
		LocalDate wEndDate = null;
		boolean usingDates = true;
		if(pStartDate != null && !IppmsUtils.treatNull(pStartDate).equals("")
				&& pEndDate != null && !IppmsUtils.treatNull(pEndDate).equals("")){
			wStartDate = PayrollBeanUtils.setDateFromString(pStartDate);
			wEndDate = PayrollBeanUtils.setDateFromString(pEndDate);

		}else{
			usingDates = false;
		}

		List<PaymentMethodInfoLog> wEmpLogList;

		boolean useUserId = (pUserId != null && pUserId > 0);
		boolean useEmpId = (pEmpId != null && pEmpId > 0);

		if(usingDates){
			wEmpLogList = this.auditService.loadEmployeeAccountLogsForExport(PayrollBeanUtils.getNextORPreviousDay(wStartDate, false),
					PayrollBeanUtils.getNextORPreviousDay(wEndDate, true),pUserId,pEmpId,useUserId,useEmpId, bc);
		}else{
			wEmpLogList =  this.auditService.loadEmployeeAccountLogsForExport(null,null,pUserId,pEmpId,useUserId,useEmpId, bc);
		}


		Collections.sort(wEmpLogList);

		BusinessEmpOVBeanInactive pList = new BusinessEmpOVBeanInactive(wEmpLogList);


		pList.setFromDateStr("");
		pList.setToDateStr("All Dates");




		pList.setFilteredByUserId(IppmsUtils.isNotNullAndGreaterThanZero(pUserId));
		pList.setUsingDates(false);
		if(IppmsUtils.isNotNullAndGreaterThanZero(pUserId))
			pList.setEmployeeName(this.genericService.loadObjectById(User.class, pUserId).getActualUserName());


		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (PaymentMethodInfoLog data : wEmpLogList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put(bc.getStaffTitle(), data.getPlaceHolder());
			newData.put(bc.getStaffTypeName(), data.getName());
			newData.put("Column Changed", data.getColumnChanged());
			newData.put("Old Value", data.getOldValue());
			newData.put("New Value", data.getNewValue());
			newData.put("Changed By", data.getChangedBy());
			newData.put("Changed Date", data.getAuditTime());
			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTitle(), 0));
		tableHeaders.add(new ReportGeneratorBean(bc.getStaffTypeName(), 0));
		tableHeaders.add(new ReportGeneratorBean("Column Changed", 0));
		tableHeaders.add(new ReportGeneratorBean("Old Value", 0));
		tableHeaders.add(new ReportGeneratorBean("New Value", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add(bc.getStaffTypeName()+" Account Logs");
		if(pList.isFilteredByUserId()) {
			mainHeaders.add(bc.getStaffTypeName()+" Account Changes By " + pList.getEmployeeName());
		}
		if(pList.isUsingDates()) {
			mainHeaders.add("Audit Period: " + pList.getFromDateStr() +" - "+pList.getToDateStr());
		}
		else{
			mainHeaders.add("All Audit Periods");
		}

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(null);
		rt.setReportTitle(bc.getStaffTypeName()+"_Account_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(0);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
	}


	@RequestMapping({"/userAccountLogsReport.do"})
	public void generateUserAccountAuditLogReport(@RequestParam("uid") Long  pUserId,
											  @RequestParam("sDate") String  pStartDate,
											  @RequestParam("eDate") String  pEndDate,
											  Model model, HttpServletRequest request,
											  HttpServletResponse response)
			throws Exception {
		SessionManagerService.manageSession(request, model);

		AuditLogsReportGenerator auditLogsReportGenerator = new AuditLogsReportGenerator();

		ReportGeneratorBean rt = new ReportGeneratorBean();

		BusinessCertificate bc = this.getBusinessCertificate(request);
		LocalDate fDate = PayrollBeanUtils.setDateFromString(pStartDate);
		LocalDate tDate = PayrollBeanUtils.setDateFromString(pEndDate);

		PaginationBean paginationBean = getPaginationInfo(request);

		boolean useUserId =  IppmsUtils.isNotNullAndGreaterThanZero(pUserId);

		List<CustomPredicate> predicates = new ArrayList<>();
		predicates.add(getBusinessClientIdPredicate(request));
		predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(fDate,false), Operation.GREATER));
		predicates.add(CustomPredicate.procurePredicate("lastModTs", PayrollBeanUtils.getNextORPreviousDay(fDate,true), Operation.LESS));
		if(useUserId)
			predicates.add(CustomPredicate.procurePredicate("login.id",pUserId));

		List<LoginAudit> empList = this.genericService.loadAllObjectsUsingRestrictions(LoginAudit.class,predicates,null);



		List<Map<String, Object>> contPenMapped = new ArrayList<>();
		for (LoginAudit data : empList) {
			Map<String, Object> newData = new HashMap<>();
			newData.put("First Name", data.getFirstName());
			newData.put("Last Name", data.getLastName());
			newData.put("Username", data.getUserName());
			newData.put("Change Description", data.getDescription());
			newData.put("Initiating IP Address", data.getRemoteIpAddress());
			newData.put("Changed By", data.getChangedBy());
			newData.put("Changed Date", data.getAuditTime());

			contPenMapped.add(newData);
		}

		List<ReportGeneratorBean> tableHeaders = new ArrayList<>();
		tableHeaders.add(new ReportGeneratorBean("First Name", 0));
		tableHeaders.add(new ReportGeneratorBean("Last Name", 0));
		tableHeaders.add(new ReportGeneratorBean("Username", 0));
		tableHeaders.add(new ReportGeneratorBean("Change Description", 0));
		tableHeaders.add(new ReportGeneratorBean("Initiating IP Address", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed By", 0));
		tableHeaders.add(new ReportGeneratorBean("Changed Date", 0));

		List<Map<String, Object>> OtherSpecHeaders = new ArrayList<>();
		for(ReportGeneratorBean head : tableHeaders){
			Map<String, Object> mappedHeader = new HashMap<>();
			mappedHeader.put("headerName", head.getHeaderName());
			mappedHeader.put("totalInd", head.getTotalInd());
			OtherSpecHeaders.add(mappedHeader);
		}

		List<String> mainHeaders = new ArrayList<>();
		mainHeaders.add("User Account Logs");

		rt.setBusinessCertificate(bc);
		rt.setGroupBy(null);
		rt.setReportTitle("User_Account_Logs");
		rt.setMainHeaders(mainHeaders);
		rt.setSubGroupBy(null);
		rt.setTableData(contPenMapped);
		rt.setTableHeaders(OtherSpecHeaders);
		rt.setTableType(0);
		rt.setTotalInd(0);


		auditLogsReportGenerator.getExcel(response, request, rt);
	}
}

