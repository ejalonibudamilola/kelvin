package com.osm.gnl.ippms.ogsg.payroll.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckDeductionEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckSpecAllowEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.audit.domain.*;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.domain.allowance.*;
import com.osm.gnl.ippms.ogsg.domain.deduction.*;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.garnishment.*;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import com.osm.gnl.ippms.ogsg.paycheck.domain.*;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PensionerPaymentInfo;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class IppmsUtils {
	
	private static Date parseDateWithLeniency(
            String str, String[] parsePatterns, boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        
        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(lenient);
        ParsePosition pos = new ParsePosition(0);
        for (String parsePattern : parsePatterns) {

            String pattern = parsePattern;

            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (parsePattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            
            parser.applyPattern(pattern);
            pos.setIndex(0);

            String str2 = str;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (parsePattern.endsWith("ZZ")) {
                str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2"); 
            }

            Date date = parser.parse(str2, pos);
            if (date != null && pos.getIndex() == str2.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }
	
	public static Date parseDateStrictly(String str, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, parsePatterns, false);
    }
	
	public static String formatDate( Date pDateToBeFormated, String formatPattern ) {
		if( pDateToBeFormated != null && StringUtils.isNotEmpty( formatPattern ) ) {
			DateFormat formatter = new SimpleDateFormat( formatPattern );
			return formatter.format( pDateToBeFormated );
		}
		return "";
	}
	
	public static boolean isValidDate( String dateString, String datePattern ) {
		boolean result = true;
		try{
			parseDateStrictly(dateString, datePattern);
		}catch( ParseException pex ) { result = false;}
		
		return result;
	}
	
	public static String[] getClassFieldNames( Class<?> clazz ) {
		if( clazz != null ) {
			Field[] fields = clazz.getFields();
			if( fields != null ) {
				String[] retVal = new String[ fields.length ];
				int ii = 0;
				for( Field field : fields ) {
					retVal[ ii ] = field.getName();
					ii++;
				}
				
				return retVal;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param pObjToCopy
	 * @param classTypeOfNewObject
	 * @param fieldsToExclude i.e fields that shouldn't be transfered
	 * @return
	 * @throws BeansException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	 public static final Object transferFieldsToAnotherEntity( Object pObjToCopy, Class<?> classTypeOfNewObject, String[] fieldsToExclude ) throws BeansException, 
	 	InstantiationException, IllegalAccessException {
		 
		 if( pObjToCopy != null && classTypeOfNewObject != null ) {
			 Object objectToBeCopiedInto = classTypeOfNewObject.newInstance();
			 org.springframework.beans.BeanUtils.copyProperties( pObjToCopy, objectToBeCopiedInto, fieldsToExclude );
			 
			 return objectToBeCopiedInto;
		 }
		 
		 return null;
	 }
	 
	 public static boolean isNullOrEmpty( String string ) {
		 return ( string == null || string.trim().isEmpty() );
	 }
	 
	 public static boolean isNullOrEmpty( Collection<?> collection ) {
		 return ( collection == null || collection.isEmpty() );
	 }
	 
	 public static boolean isNotNullOrEmpty(Collection<?> col) {
		 return !isNullOrEmpty(col);
	 }
	 
	 
     public static boolean isNotNullOrEmpty(String str) {
		 return !isNullOrEmpty(str);
	 }

	public static String treatNull(String str) {
		 if(str == null)
		 	str = "";
		return  str.trim();
	}
	public static String treatNull(Object str) {
		if(str == null)
			str = "";
		if(str.getClass().isAssignableFrom(String.class))
		  return ((String) str).trim();
		return "";
	}
	 
	 public static boolean isNotNull(Object obj) {
		 return obj != null;
	 }
	 
	 public static boolean isNull(Object obj) {
		 return obj == null;
	 }
	 
	 /**
	  *  Check if a {@link Number Number} isn't 
	  * {@code null} and it is greater than or equal to (>=) zero(0).
	  * 
	  * @param number
	  * @return
	  */
	 public static boolean isNotNullAndGreaterThanOrEqualToZero(Number number) {
		 return isNotNull(number) && number.intValue() >= 0;
	 }
	 
	 /**
	  * Check if a {@link Number Number} isn't 
	  * {@code null} and it is greater than zero(0).
	  * 
	  * @param number The number to check
	  * @return {@code true} if it is and {@code false} otherwise.
	  */
	 public static boolean isNotNullAndGreaterThanZero(Number number) {
		 return isNotNull(number) && number.intValue() > 0;
	 }
	 
	 /**
	  * Check if a {@link Number Number} is 
	  * {@code null} or it is less than one(1).
	  * 
	  * @param number The number to check
	  * @return {@code true} if it is and {@code false} otherwise.
	  */
	 public static boolean isNullOrLessThanOne(Number number) {
		 return isNull(number) || number.intValue() < 1;
	 }
	 
	/* public static ArrayList<Integer> getIntKeySetFromMap(Map<Integer, List<?>> wParamMap)
	    {
	      ArrayList<Integer> wRetVal = new ArrayList<>();

	      Set<Map.Entry<Integer, List<?>>> set = wParamMap.entrySet();
	      Iterator<Map.Entry<Integer, List<?>>> i = set.iterator();

	      while (i.hasNext()) {
	        Map.Entry<Integer, List<?>> me = i.next();

	        wRetVal.add(me.getKey());
	      }

	      return wRetVal;
	    }*/

    public static HashMap<Long, SalaryInfo> makeHasMapFromList(List<SalaryInfo> wList) {
	 	HashMap<Long, SalaryInfo> wRetMap = new HashMap<>();

	 	for(SalaryInfo obj : wList) {
			wRetMap.put(obj.getId(), obj);
		}

	 	return wRetMap;

    }
      public static  Class<?> getPaycheckClass(BusinessCertificate businessCertificate){

	 	if(businessCertificate.isCivilService()){
	 		return EmployeePayBean.class;
		}else if(businessCertificate.isLocalGovt()){
	 		return EmployeePayBeanLG.class;
		}else if(businessCertificate.isLocalGovtPension()){
	 		return EmployeePayBeanBLGP.class;
		}else if(businessCertificate.isSubeb()){
	 		return EmployeePayBeanSubeb.class;
		}else{
	 		return EmployeePayBeanPension.class;
		}
	}
	public static  Class<?> getPaycheckDeductionClass(BusinessCertificate businessCertificate){

		if(businessCertificate.isCivilService()){
			return PaycheckDeduction.class;
		}else if(businessCertificate.isLocalGovt()){
			return PaycheckDeductionLG.class;
		}else if(businessCertificate.isLocalGovtPension()){
			return PaycheckDeductionBLGP.class;
		}else if(businessCertificate.isSubeb()){
			return PaycheckDeductionSubeb.class;
		}else{
			return PaycheckDeductionPension.class;
		}
	}
	public static  Class<?> makePaycheckSpecAllowClass(BusinessCertificate businessCertificate){

		if(businessCertificate.isCivilService()){
			return PaycheckSpecialAllowance.class;
		}else if(businessCertificate.isLocalGovt()){
			return PaycheckSpecialAllowanceLG.class;
		}else if(businessCertificate.isLocalGovtPension()){
			return PaycheckSpecialAllowanceBLGP.class;
		}else if(businessCertificate.isSubeb()){
			return PaycheckSpecialAllowanceSubeb.class;
		}else{
			return PaycheckSpecialAllowancePension.class;
		}
	}
	public static  Class<?> getEmployeeClass(BusinessCertificate businessCertificate){

		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return Pensioner.class;
		}else  {
			return Employee.class;
		}
	}
	public static Class<?> getEmployeeClass(BusinessClient businessClient){
		if(businessClient.isLocalGovtPension() || businessClient.isStatePension()){
			return Pensioner.class;
		}else  {
			return Employee.class;
		}
	}
	public static  String getEmployeeTableName(BusinessCertificate businessCertificate){

		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return "Pensioner";
		}else  {
			return "Employee";
		}
	}

	public static  String getEmployeePGTableName(BusinessCertificate businessCertificate){

		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return "ippms_pensioner";
		}else  {
			return "ippms_employee";
		}
	}
	public static AbstractPaycheckEntity makePaycheckObject(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return new EmployeePayBean();
		}else if(businessCertificate.isLocalGovt()){
			return new EmployeePayBeanLG();
		}else if(businessCertificate.isLocalGovtPension()){
			return new EmployeePayBeanBLGP();
		}else if(businessCertificate.isSubeb()){
			return new EmployeePayBeanSubeb();
		}else{
			return new EmployeePayBeanPension();
		}
	}

	public static String getPaycheckTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return  "EmployeePayBean";
		}else if(businessCertificate.isLocalGovt()){
			return  "EmployeePayBeanLG";
		}else if(businessCertificate.isLocalGovtPension()){
			return  "EmployeePayBeanBLGP";
		}else if(businessCertificate.isSubeb()){
			return  "EmployeePayBeanSubeb" ;
		}else{
			return "EmployeePayBeanPension";
		}
	}

	public static String getNativePaycheckTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return  "ippms_paychecks_info";
		}else if(businessCertificate.isLocalGovt()){
			return  "ippms_paychecks_lg_info";
		}else if(businessCertificate.isLocalGovtPension()){
			return  "ippms_paychecks_blgp_info";
		}else if(businessCertificate.isSubeb()){
			return  "ippms_paychecks_subeb_info" ;
		}else{
			return "ippms_paychecks_pension_info";
		}
	}


	public static String getPaycheckDeductionTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return  "PaycheckDeduction";
		}else if(businessCertificate.isLocalGovt()){
			return  "PaycheckDeductionLG";
		}else if(businessCertificate.isLocalGovtPension()){
			return  "PaycheckDeductionBLGP";
		}else if(businessCertificate.isSubeb()){
			return  "PaycheckDeductionSubeb" ;
		}else{
			return "PaycheckDeductionPension";
		}
	}
//	public static String getGarnishmentTableName(BusinessCertificate businessCertificate) {
//		if(businessCertificate.isCivilService()){
//			return  "PaycheckGarnishment";
//		}else if(businessCertificate.isLocalGovt()){
//			return  "PaycheckGarnishmentLG";
//		}else if(businessCertificate.isLocalGovtPension()){
//			return  "PaycheckGarnishmentBLGP";
//		}else if(businessCertificate.isSubeb()){
//			return  "PaycheckGarnishmentSubeb" ;
//		}else{
//			return "PaycheckGarnishmentPension";
//		}
//	}

	public static String getGarnishmentInfoTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return  "EmpGarnishmentInfo";
		}else if(businessCertificate.isLocalGovt()){
			return  "EmpGarnishmentInfoLG";
		}else if(businessCertificate.isPensioner()){
			return  "EmpGarnishmentInfoPensions";
		}else if(businessCertificate.isSubeb()){
			return  "EmpGarnishmentInfoSubeb" ;
		}else{
			return "";
		}
	}

	public static String getSpecialAllowanceInfoTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return "SpecialAllowanceInfo";
		}else if(businessCertificate.isLocalGovt()){
			return "SpecialAllowanceInfoLG";
		}else if(businessCertificate.isSubeb()){
			return "SpecialAllowanceInfoSubeb";
		}else{
			return "SpecialAllowanceInfoPensions";
		}
	}

	public static String getDeductionInfoTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return "EmpDeductionInfo";
		}else if(businessCertificate.isLocalGovt()){
			return "EmpDeductionInfoLG";
		}else if(businessCertificate.isSubeb()){
			return "EmpDeductionInfoSubeb";
		}else{
			return "EmpDeductionInfoPensions";
		}
	}

	public static String getPaycheckSpecAllowTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return  "PaycheckSpecialAllowance";
		}else if(businessCertificate.isLocalGovt()){
			return  "PaycheckSpecialAllowanceLG";
		}else if(businessCertificate.isLocalGovtPension()){
			return  "PaycheckSpecialAllowanceBLGP";
		}else if(businessCertificate.isSubeb()){
			return  "PaycheckSpecialAllowanceSubeb" ;
		}else{
			return "PaycheckSpecialAllowancePension";
		}
	}
	public static String getPaycheckSpecAllowPGTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return  "ippms_paycheck_spec_allow";
		}else if(businessCertificate.isLocalGovt()){
			return  "ippms_paycheck_spec_allow_lg";
		}else if(businessCertificate.isLocalGovtPension()){
			return  "ippms_paycheck_spec_allow_blpg";
		}else if(businessCertificate.isSubeb()){
			return  "ippms_paycheck_spec_allow_subeb" ;
		}else{
			return "ippms_paycheck_spec_allow_pen";
		}
	}
    public static String treatOgNumber(BusinessCertificate businessCertificate,String ogNumber) {
	 	String wRetVal = ogNumber;
	 	if(businessCertificate.isCivilService()){
			if(!ogNumber.toUpperCase().startsWith("OG")){
				wRetVal = "OG"+ogNumber;
			}

		}else if (businessCertificate.isLocalGovtPension()){
			if(!ogNumber.toUpperCase().startsWith("BLGP")){
				wRetVal = "BLGP"+ogNumber;
			}

		}else if(businessCertificate.isStatePension()){
			if(!ogNumber.toUpperCase().startsWith("PEN")){
				wRetVal = "PEN"+ogNumber;
			}
		}else if(businessCertificate.isLocalGovt()){
			if(!ogNumber.toUpperCase().startsWith("LG")){
				wRetVal = "LG"+ogNumber;
			}
		}else if(businessCertificate.isSubeb()){
			if(!ogNumber.toUpperCase().startsWith("OGSB")){
				wRetVal = "OGSB"+ogNumber;
			}
		}
	 	return  wRetVal.toUpperCase().trim();
    }
    public static int validateOgNumber(BusinessCertificate bc, String ogNumber){
    	int retVal = 0;
		String bodyPart = null;
		String headerPart = null;
		ogNumber = ogNumber.trim().toUpperCase();
		if (bc.isCivilService()) {
			if(ogNumber.startsWith("OGINT"))
				return retVal;

			headerPart = ogNumber.substring(0, 2);
			bodyPart = ogNumber.substring(2);
		}

    	if(bc.isSubeb() ) {

			headerPart = ogNumber.substring(0, 4);
			bodyPart = ogNumber.substring(4);
		}
    	if(bc.isLocalGovtPension()) {
			headerPart = ogNumber.substring(0, 4);
			bodyPart = ogNumber.substring(4);
		}

    	if(bc.isStatePension()){
			headerPart = ogNumber.substring(0, 3);
			bodyPart = ogNumber.substring(3);
		}

    	if(bc.isLocalGovt()){
			headerPart = ogNumber.substring(0, 2);
			bodyPart = ogNumber.substring(2);
		}
         if(!bc.getEmpIdStartVal().equalsIgnoreCase(headerPart))
         	return 1;

         try{
         	Integer.parseInt(bodyPart);
		 }catch(Exception wEx){
         	return 2;
		 }

    	return retVal;
	}

    public static AbstractEmployeeEntity loadEmployee(GenericService genericService, Long pEmpId, BusinessCertificate businessCertificate) throws IllegalAccessException, InstantiationException {

		AbstractEmployeeEntity employee = (AbstractEmployeeEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(businessCertificate), Arrays.asList(
				CustomPredicate.procurePredicate("id",pEmpId), CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));

		return employee;
	}
	@SneakyThrows
	public static AbstractEmployeeEntity loadEmployee(GenericService genericService, Long pEmpId,BusinessCertificate bc,Long bizClientId) throws IllegalAccessException, InstantiationException {
		BusinessCertificate businessCertificate;

		if(!bc.getBusinessClientInstId().equals(bizClientId)){
			BusinessClient businessClient = genericService.loadObjectById(BusinessClient.class,bizClientId);
			businessCertificate = BusinessCertificateCreator.makeBusinessClient(businessClient);
		}else{
			businessCertificate = bc;
		}

		AbstractEmployeeEntity employee = (AbstractEmployeeEntity) genericService.loadObjectUsingRestriction(IppmsUtils.getEmployeeClass(businessCertificate), Arrays.asList(
				CustomPredicate.procurePredicate("id",pEmpId), CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId())));

		return employee;
	}
    public  static Class<?> getPaycheckGarnishmentClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return PaycheckGarnishment.class;
		}else if(businessCertificate.isLocalGovt()){
			return PaycheckGarnishmentLG.class;
		}else if(businessCertificate.isLocalGovtPension()){
			return PaycheckGarnishmentBLGP.class;
		}else if(businessCertificate.isSubeb()){
			return PaycheckGarnishmentSubeb.class;
		}else{
			return PaycheckGarnishmentPension.class;
		}
    }
    public static boolean isPendingPaychecksExisting(GenericService genericService, BusinessCertificate businessCertificate){
		PredicateBuilder predicateBuilder = new PredicateBuilder();
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("status", "P", Operation.STRING_EQUALS));
		predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId", businessCertificate.getBusinessClientInstId(), Operation.EQUALS));

		return genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(businessCertificate)) > 0;
	}

	public static String getDeductionAuditTable(BusinessCertificate businessCertificate) {

		if(businessCertificate.isPensioner()){
			return "DeductionAuditPensions";
		}else if(businessCertificate.isLocalGovt()) {
			return "DeductionAuditLG";
		}else if(businessCertificate.isCivilService()){
			return "DeductionAudit";
		}else if(businessCertificate.isSubeb()){
			return "DeductionAuditSubeb";
		}else
			return "";
	}

	public static Class<?> getDeductionAuditEntityClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isPensioner()){
			return DeductionAuditPensions.class;
		}else if(businessCertificate.isLocalGovt()) {
			return DeductionAuditLG.class;
		}else if(businessCertificate.isCivilService()){
			return DeductionAudit.class;
		}else if(businessCertificate.isSubeb()){
			return DeductionAuditSubeb.class;
		}else
			return null;
	}
	public static String getEmployeeAuditTable(BusinessCertificate businessCertificate) {

		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return "PensionerAudit";
		}else  {
			return "EmployeeAudit";
		}
	}

	public static Class<?> getEmployeeAuditEntityClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return PensionerAudit.class;
		}else  {
			return EmployeeAudit.class;
		}
	}
	public static String getSpecAllowAuditTable(BusinessCertificate businessCertificate) {

		if(businessCertificate.isPensioner()){
			return "SpecialAllowanceAuditPensions";
		}else if(businessCertificate.isSubeb())  {
			return "SpecialAllowanceAuditSubeb";
		}else if(businessCertificate.isCivilService()){
			return "SpecialAllowanceAudit";
		}else if(businessCertificate.isLocalGovt()){
			return "SpecialAllowanceAuditLG";
		}else{
			return "";
		}
	}




	public static String getHireAuditTable(BusinessCertificate businessCertificate) {
		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return "PensionerHiringInfoAudit";
		}else  {
			return "HiringInfoAudit";
		}
	}

	public static String getPromotionAuditTable(BusinessCertificate businessCertificate) {
		if(businessCertificate.isLocalGovt()){
			return "PromotionAuditLG";
		}else if(businessCertificate.isSubeb()){
			return "PromotionAuditSubeb";
		}else if(businessCertificate.isCivilService()){
			return "PromotionAudit";
		}
		return null;
	}

	public static String getPaymentMethodAuditTable(BusinessCertificate businessCertificate) {
		if(businessCertificate.isLocalGovtPension() || businessCertificate.isStatePension()){
			return "PensionerPaymentMethodInfoLog";
		}else  {
			return "PaymentMethodInfoLog";
		}
	}

    public static Class<?> getPaymentInfoClass(BusinessCertificate businessCertificate) {
	 	if(businessCertificate.isPensioner())
	 		return PensionerPaymentInfo.class;
	 	return PaymentInfo.class;
    }

	public static String getPaycheckGarnishmentTableName(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return "PaycheckGarnishment";
		}else if(businessCertificate.isLocalGovt()){
			return "PaycheckGarnishmentLG";
		}else if(businessCertificate.isLocalGovtPension()){
			return "PaycheckGarnishmentBLGP";
		}else if(businessCertificate.isSubeb()){
			return "PaycheckGarnishmentSubeb";
		}else{
			return "PaycheckGarnishmentPension";
		}
	}


	public static AbstractEmployeeEntity makeEmployeeObject(BusinessCertificate bc) {
	 	if(bc.isPensioner())
	 		return new Pensioner();
	 	else
	 		return new Employee();
	}

	public static AbstractEmployeeAuditEntity makeEmployeeAudit(BusinessCertificate businessCertificate) {
	 	if(businessCertificate.isPensioner())
		  return new PensionerAudit();
	 	else
	 		return new EmployeeAudit();
	}

	public static String getEmpPGJoinStr(BusinessCertificate bc) {
		if(bc.isPensioner())
			return "pensioner_inst_id";
		return "employee_inst_id";
	}

    public static Class<?> getGarnishmentInfoClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return EmpGarnishmentInfo.class;
		}else if(businessCertificate.isLocalGovt()){
			return EmpGarnishmentInfoLG.class;
		} else if(businessCertificate.isSubeb()){
			return EmpGarnishmentInfoSubeb.class;
		}else{
			return EmpGarnishmentInfoPensions.class;
		}
    }

	public static Class<?> getSpecialAllowanceInfoClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return SpecialAllowanceInfo.class;
		}else if(businessCertificate.isLocalGovt()){
			return SpecialAllowanceInfoLG.class;
		} else if(businessCertificate.isSubeb()){
			return SpecialAllowanceInfoSubeb.class;
		}else{
			return SpecialAllowanceInfoPensions.class;
		}
	}

	public static Class<?> getDeductionInfoClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return EmpDeductionInfo.class;
		}else if(businessCertificate.isLocalGovt()){
			return EmpDeductionInfoLG.class;
		} else if(businessCertificate.isSubeb()){
			return EmpDeductionInfoSubeb.class;
		}else{
			return EmpDeductionInfoPensions.class;
		}
	}

	public static AbstractGarnishmentEntity makeGarnishmentInfoObject(BusinessCertificate businessCertificate) {
		if(businessCertificate.isPensioner())
			return new EmpGarnishmentInfoPensions();
		else if(businessCertificate.isLocalGovt())
			return new EmpGarnishmentInfoLG();
		else if(businessCertificate.isCivilService())
			return new EmpGarnishmentInfo();
		else if(businessCertificate.isSubeb())
			return new EmpGarnishmentInfoSubeb();
		else
			return null;
	}

	public static AbstractSpecialAllowanceEntity makeSpecialAllowanceInfoObject(BusinessCertificate businessCertificate) {
		if(businessCertificate.isPensioner())
			return new SpecialAllowanceInfoPensions();
		else if(businessCertificate.isLocalGovt())
			return new SpecialAllowanceInfoLG();
		else if(businessCertificate.isCivilService())
			return new SpecialAllowanceInfo();
		else if(businessCertificate.isSubeb())
			return new SpecialAllowanceInfoSubeb();
		else
			return null;
	}

	public static AbstractDeductionEntity makeDeductionInfoObject(BusinessCertificate businessCertificate) {
		if(businessCertificate.isPensioner())
			return new EmpDeductionInfoPensions();
		else if(businessCertificate.isLocalGovt())
			return new EmpDeductionInfoLG();
		else if(businessCertificate.isCivilService())
			return new EmpDeductionInfo();
		else if(businessCertificate.isSubeb())
			return new EmpDeductionInfoSubeb();
		else
			return null;
	}

    public static String getGarnishAuditTableName(BusinessCertificate bc) {
		if(bc.isCivilService()){
			return "GarnishmentAudit";
		}else if(bc.isLocalGovt()){
			return "GarnishmentAuditLG";
		}else if(bc.isSubeb()){
			return "GarnishmentAuditSubeb";
		}else{
			return "GarnishmentAuditPension";
		}
    }
	public static Class<?> getGarnishAuditClass(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService()){
			return GarnishmentAudit.class;
		}else if(businessCertificate.isLocalGovt()){
			return GarnishmentAuditLG.class;
		} else if(businessCertificate.isSubeb()){
			return GarnishmentAuditSubeb.class;
		}else{
			return GarnishmentAuditPension.class;
		}
	}

	public static AbstractSpecAllowAuditEntity makeSpecialAllowanceAuditObject(BusinessCertificate businessCertificate) {
		if(businessCertificate.isPensioner())
			return new SpecialAllowanceAuditPensions();
		else if(businessCertificate.isLocalGovt())
			return new SpecialAllowanceAuditLG();
		else if(businessCertificate.isCivilService())
			return new SpecialAllowanceAudit();
		else if(businessCertificate.isSubeb())
			return new SpecialAllowanceAuditSubeb();
		else
			return null;
	}

	public static  Class<?> getPromotionAuditClass(BusinessCertificate bc) {
		if(bc.isCivilService()){
			return PromotionAudit.class;
		}else if(bc.isLocalGovt()){
			return PromotionAuditLG.class;
		} else if(bc.isSubeb()){
			return PromotionAuditSubeb.class;
		}else{
			return null;
		}
	}
	/**
	 * Used by ChartService
	 * @param bc
	 * @return
	 */
	public static  Class<?> getPromotionAuditClassByBusinessClient(BusinessClient bc) {
		if(bc.isCivilService()){
			return PromotionAudit.class;
		}else if(bc.isLocalGovernment()){
			return PromotionAuditLG.class;
		} else if(bc.isSubeb()){
			return PromotionAuditSubeb.class;
		}else{
			return null;
		}
	}

    public static AbstractPromotionAuditEntity makePromotionAuditObject(BusinessCertificate businessCertificate) {
		if(businessCertificate.isCivilService())
			return new PromotionAudit();
		else if(businessCertificate.isLocalGovt())
			return new PromotionAuditLG();
		else if(businessCertificate.isSubeb())
			return new PromotionAuditSubeb();

		else
			return null;
    }

	/**
	 * Used by ChartService
	 * @param bc
	 * @return
	 */
	public static String getPromotionAuditTableByBusinessClient(BusinessClient bc) {
		if(bc.isLocalGovernment()){
			return "PromotionAuditLG";
		}else if(bc.isSubeb()){
			return "PromotionAuditSubeb";
		}else if(bc.isCivilService()){
			return "PromotionAudit";
		}
		return null;
	}

    public static AbstractPaycheckDeductionEntity makePaycheckDeductionObject(BusinessCertificate bc) {
		if(bc.isSubeb()){
			return new PaycheckDeductionSubeb();
		}else if(bc.isStatePension()){
			return new PaycheckDeductionPension();
		}else if(bc.isLocalGovt()){
			return new PaycheckDeductionLG();
		}else if(bc.isCivilService()){
			return new PaycheckDeduction();
		}else if(bc.isLocalGovtPension()){
			return new PaycheckDeductionBLGP();
		}else{
			return null;
		}
    }

	public static AbstractPaycheckGarnishmentEntity makePaycheckGarnishmentObject(BusinessCertificate bc) {
		if(bc.isSubeb()){
			return new PaycheckGarnishmentSubeb();
		}else if(bc.isStatePension()){
			return new PaycheckGarnishmentPension();
		}else if(bc.isLocalGovt()){
			return new PaycheckGarnishmentLG();
		}else if(bc.isCivilService()){
			return new PaycheckGarnishment();
		}else if(bc.isLocalGovtPension()){
			return new PaycheckGarnishmentBLGP();
		}else{
			return null;
		}
	}


	public static AbstractPaycheckSpecAllowEntity makePaycheckSpecAllowObject(BusinessCertificate bc) {
		if(bc.isSubeb()){
			return new PaycheckSpecialAllowanceSubeb();
		}else if(bc.isStatePension()){
			return new PaycheckSpecialAllowancePension();
		}else if(bc.isLocalGovt()){
			return new PaycheckSpecialAllowanceLG();
		}else if(bc.isCivilService()){
			return new PaycheckSpecialAllowance();
		}else if(bc.isLocalGovtPension()){
			return new PaycheckSpecialAllowanceBLGP();
		}else{
			return null;
		}
	}
}
