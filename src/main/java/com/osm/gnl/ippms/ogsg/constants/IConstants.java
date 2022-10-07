package com.osm.gnl.ippms.ogsg.constants;

/**
 * Do Not ALTER/REFRACTOR Application Wide Constants
 * If there is need, consultations and Impact Analysis
 * must be done thoroughly
 */

public interface IConstants {
    //Approval Object Type Codes
    String BASE_NAME_PH = "Name";
    String NOK_NAME_PH = "NOK NAME";
    int ALLOWANCE_RULE_CODE = 1;
    int STAFF_APPROVAL_CODE = 2;
    int STEP_INC_APPROVAL_CODE = 3;
    int TRANSFER_APPROVAL_CODE = 4;
    int AM_ALIVE_APPROVAL_CODE = 5;
    int SALARY_STRUCTURE_APPROVAL_CODE = 6;
    int PAY_GROUP_APPROVAL_CODE = 7;

    //DO NOT EDIT THE FOLLOWING >>>
    String RENT = "Rent";
    String TRANSPORT = "Transport";
    String MOTOR_VEHICLE = "Motor Vehicle";
    String CALL_DUTY = "Call Duty";
    String SHIFT_DUTY = "Shift Duty";
    String SHOW_MSG = "show_message";
    //--DO NOT EDIT END <<<

    String BIOMETRIC_PHOTO_TYPE = "image/jpeg";
    String BANK_BRANCH_TABLE = "ippms_bank_branches";
    String BANK_BRANCH_AKA = "bb";
    String PAY_INFO_TABLE = "ippms_payment_method_info";
    String PAY_INFO_AKA = "pmi";
    String BANK_INFO_TABLE = "ippms_banks";
    String BANK_INFO_AKA = "b";
    String HIRE_INFO_TABLE = "ippms_hire_info";
    String HIRE_INFO_AKA = "h";
    String SAL_INFO_TABLE = "ippms_salary_info";
    String EMP_INFO_TABLE = "ippms_employee";
    long ONE_HOUR = 3600000L;


    String SELECTED_IDS_KEY = "_selected_ids_key";
    String SELECTED_DEM_IDS_KEY = "_selected_DEM_ids_key";
    String SELECT_ALL_DEM_ID_KEY = "_select_all_DEM_ids";
    String SELECT_ALL_ID_KEY = "_select_all_ids";
    int AUTO_STEP_INCREASE_MONTH = 0;

    double DEVELOPMENT_LEVY = 100.0D;

    int TIN_LENGTH = 10;
    String naira = "\u20A6";
    String SHOW_ROW = "display:''";
    String HIDE_ROW = "display:none";
    String EMPTY_STR = "";

    double LTG_INCREASE = 1.2D;
    String FEMALE = "F";
    String MALE = "M";

    int PROMOTION_LIMIT = 16;
    int CHECKED = 1;




    int SPEC_ALLOW_IND = 5;
    double MAX_ARREARS_PERCENTAGE = 100.0D;


    int ON = 1;
    int OFF = 0;

    int PROMOTION = 1;
    int LOAN = 2;
    int DEDUCTION = 3;


    String TPS_HIRE_DATE_STR = "31/12/2007";
    String TPS_EXP_RETIRE_DATE_STR = "01/07/2025";

    String APPROVED = "Approved for Payroll";
    String UNAPPROVED = "Unapproved for Payroll";
    String APPROVED_BIOMETRIC = "Verified";
    String UNAPPROVED_BIOMETRIC = "Unverified";
   // String YES_IND = "Y";
    //String NO_IND = "N";
    String ALHAJA = "Alhaja";
    String DEACONESS = "Deaconess";
    String DR_MRS = "Dr./Mrs.";
    String MISS = "Miss.";
    String MS = "Ms.";
    String MRS = "Mrs.";
    /*public static final String TPS_AGE_START = "31/12/2007";
    public static final String TPS_AGE_END = "01/07/2025";*/
    String HTML_COPYRIGHT = "&copy;";
    String REDIRECT_TO_DASHBOARD = "redirect:determineDashBoard.do";
    String REDIRECT_TO_DELETE_PAYROLL = "redirect:deletePendingPayroll.do";
    //String REDIRECT_TO_HR_FXN_DASHBOARD = "redirect:hrAdminHomeForm.do";
    String REDIRECT_TO_DEPT_FXN_DASHBOARD = "redirect:departmentFunctionalities.do";
    String REDIRECT_FUT_SIM_DASHBOARD = "redirect:preFutureSimReportForm.do";
    int TPS_IND = 1;
    int CPS_IND = 2;
    int ERROR_PAYROLL_IND = 3;
    int AWAITING_APPROVAL_IND = 2;
    int CANCEL_PAYROLL_IND = 4;
    Integer POL_APP_ID = 18;
    Integer HOPS_ID = 19;
    String NAMED_ENTITY = "ne";

    String EMP_SKEL = "emp_skel";
    String PS_BEAN = "ps_bean";

    //Objects Moved from Base Controller.
    int pageLength = 20;
    String BLOCK = "block";

    String IPPMS_EXCEPTION = "uncaughtException";
    String NONE = "none";
    String TRUE = "true";
    String FALSE = "false";
    String REQUEST_PARAM_ADD = "_add";
    String REQUEST_PARAM_UPD = "_update";
    String REQUEST_PARAM_CANCEL = "_cancel";
    String REQUEST_PARAM_MERGE = "_merge";
    String REQUEST_PARAM_CANCEL_VALUE = "cancel";
    String REQUEST_PARAM_CLOSE = "_close";
    String REQUEST_PARAM_CONFIRM = "_confirm";
    String REQUEST_PARAM_CREATE = "_create";
    String REQUEST_PARAM_OK = "_ok";
    String REQUEST_PARAM_GO = "_go";
    String REQUEST_PARAM_DEACTIVATE = "_deactivate";
    String REQUEST_PARAM_SEARCH = "_search";
    String REQUEST_PARAM_APPROVE = "_approve";
    String REQUEST_PARAM_REJECT = "_reject";
    String REQUEST_PARAM_PROMOTE = "_promote";
    String REQUEST_PARAM_ADD_LOAN = "_addloan";
    String REQUEST_PARAM_VERIFY = "_verify";
    String REQUEST_PARAM_ADD_DEDUCTION = "_addDeduction";
    String REQUEST_PARAM_TRANSFER = "_transfer";
    String REQUEST_PARAM_TERMINATE = "_terminate";
    String REQUEST_PARAM_EXTEND = "_extend";
    String REQUEST_PARAM_ADD_SPEC_ALLOW = "_addSpecAllow";
    String REQUEST_PARAM_EDIT = "_edit";
    String CONFIG_HOME_URL = "redirect:configurationHome.do";
    String REQUEST_PARAM_UPDATE = "_update";
    String REQUEST_PARAM_DELETE = "_delete";
    String REQUEST_PARAM_REPLACE = "_replace";
    String REQUEST_PARAM_SIMULATE = "_simulate";
    String REQUEST_PARAM_LOAD = "_load";
    String REQUEST_PARAM_DONE = "_done";
    String REQUEST_PARAM_NEXT = "_next";
    String REQUEST_PARAM_FINISH = "_finish";
    String REQUEST_PARAM_SCHED = "_schedule";
    String REQUEST_PARAM_UPDATE_REPORT = "_updateReport";
    String REQUEST_PARAM_SEND_EMAIL = "_sendEmail";
    String REQUEST_PARAM_UPDATE_REPORT_VALUE = "updateReport";
    String REQUEST_PARAM_CLOSE_ALL_TICKETS = "_closeOpenTickets";
    String SELECTED_MONTHLY_VARAIATION_OBJECT = "schoolSelected";
    String SCHOOL_SELECTED_MONTHLY_VARIATION = "selected";

    //-- Spring Security stuffs...
    String DETERMINE_DASHBOARD_URL = "redirect:/determineDashBoard.do";
    String CHANGE_PASSWORD_URL = "redirect:/changePassword.do";

    String SIGN_OUT_URL = "secure/securedLogoutForm";
    String PERMISSION_DENIED_URL = "redirect:/permissionDenied.do";
    String SAVE_PARAM = "save_op";
    Object SAVE_PARAM_VALUE = "_save";
    String OUTCOME_MSG_KEY = "outcomeMsg";
    String REQUEST_PARAM_PRINT = "_print";
    String REQUEST_PARAM_PRINT_VALUE = "print";
   // String ORIG_FORM = "orig_form";
    String LTG_ATTR_NAME = "ltg_holder";
    String CHECK_REG_ATTR_NAME = "check_reg";
  //  String GEN_PAY_ATTR = "gen_pay";
    String MASS_DED_ATTR_NAME = "mass_ded";
    String MASS_PAYSLIP_EMAIL_NAME = "mass_email_payslip";
    String MASS_LOAN_ATTR_NAME = "mass_loan";
    String MASS_PROMO_ATTR_NAME = "mass_promo";
    String MASS_SA_ATTR_NAME = "mass_sa";
    String MASS_TRANS_ATTR_NAME = "mass_trans";
   // String MOVE_EMP_PAYGROUP = "move_emp";
    String SIM_ATTR = "simul";
    String EMP_SKEL_ERR = "emp_skel_error";
   // String CREATE_PG = "make_pg";
    int CURRENT_BVN_LENGTH = 11;
    String CONFIG_BEAN = "er_config";
    String BVN_CONFIG_BEAN = "bvn_config";
    String CUSTOM_REPORT = "x_c_r";


    String SAVED_INDICATOR_KEY = "saved";
    String SAVED_MSG = "savedMsg";
    String SAVED_URL = "savedUrl";
    String DISPLAY_ERRORS = "displayErrors";

    //String BULK_PAY_SLIPS = "xb_p_s";
   // String RURAL_SPEC_ALLOW_TYPE = "RURAL";
    String FU_OBJ_KEY = "_fuobj";

    String MINI_SAL_KEY = "_miBin$t76_";


    int TWO = 2;
    String REQUEST_PARAM_CREATE_CONTRACT = "_createContract";

    String FILE_UPLOAD_OBJ_IND = "_objInd";
    String FILE_UPLOAD_UUID = "_fuUID";
    /**
     * Stored Procedures or PostgreSQL Fxns DO NOT ALTER.
     */
    String DEL_STEP_INC = "ippms_del_step_increment";
    String DEL_SALARY_TEMP_SP = "ippms_del_salary_temp";
    String UPD_PAY_GROUP_CODE = "ippms_update_pay_group_code";
    String DEL_SING_RERUN_PAYCHECKS = "ippms_del_rerun_paychecks";
    String CLOSE_OPEN_TICKETS = "ippms_close_open_tickets";
    String DEL_PAYROLL_RERUN_PROC = "ippms_del_payroll_rerun";
    String UPD_RERUN_IND_PROC = "ippms_update_rerun_paychecks";
    String RESET_IPPMS_PAYCHECK_SEQUENCES = "ippms_reset_paychecks_sequences";
    String UPD_SPEC_ALLOW_PAY_TYPES = "ippms_upd_spec_allow_info_pt";

    /**
     * Do not alter this values.
     * See com.osm.gnl.ippms.ogsg.web.ui.filter.MenuFilter
     */
    String DASHBOARD_URL = "/determineDashBoard.do";

    String ABSTRACT_PAYCHECK = "abstract_paycheck_info" ;
    int ALLOWANCE_RULE_APPROVAL_URL_IND = 1;
    int ALLOWANCE_RULE_INIT_URL_IND = 2;
    int TRANSFER_APPROVAL_URL_IND = 3;
    int TRANSFER_REQUEST_URL_IND = 4;
    int STEP_INC_INIT_URL_IND = 5 ;
    int STEP_INC_APPROVAL_URL_IND = 6 ;
    int PAY_GROUP_INIT_URL_IND = 7;
    int AM_ALIVE_INIT_URL_IND = 8;
    int STAFF_APPROVAL_INIT_URL_IND = 9;
    int TREATED_STATUS = 4;
    int DELETED_STATUS = 3 ;
    int REJECTED_STATUS = 2;
    int PENDING_STATUS = 0;
    int APPROVED_STATUS = 1 ;
    int STEP_INCREMENT = 7 ;//For File Upload


}