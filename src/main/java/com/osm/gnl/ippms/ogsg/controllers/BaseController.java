package com.osm.gnl.ippms.ogsg.controllers;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.I18nMessageProvider;
import com.osm.gnl.ippms.ogsg.auth.domain.IppmsEncoder;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.util.Base64Util;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.exception.NoBusinessCertificationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Base64;


@Slf4j
public abstract class BaseController extends I18nMessageProvider
        implements IConstants {
    @Autowired
    protected GenericService genericService;
    protected String viewName;
    protected  int pageLength = 25;



    /**
     * Determine if a session id has been created for a
     * particular <code>HttpSession</code>.
     *
     * @param pRequest
     * @return
     */
    protected final boolean hasSessionId(HttpServletRequest pRequest) {
        return getSession(pRequest).getAttribute(IppmsEncoder.getSessionKey()) != null;
    }

    /**
     * Thread safe access to the <code>HttpSession</code>.
     *
     * @param pRequest
     * @return
     */
    protected final synchronized static HttpSession getSession(HttpServletRequest pRequest) {
        return pRequest.getSession();
    }
    protected final synchronized  String employeeText(HttpServletRequest pRequest){
        if(this.getBusinessCertificate(pRequest).isPensioner())
            return "Pensioner";
        return "Employee";
    }
    protected final void addRoleBeanToModel(Model model, HttpServletRequest request){
        model.addAttribute("roleBean",getBusinessCertificate(request));
    }

    /**
     *  Use for Objects that that have  'private Long businessClientId' defined.
     *
     * @param pRequest
     * @return
     */
    protected final synchronized CustomPredicate getBusinessClientIdPredicate(HttpServletRequest pRequest) {

         return  CustomPredicate.procurePredicate("businessClientId",getBusinessCertificate(pRequest).getBusinessClientInstId());
    }
    /**
     * This class creates a session id for the user via
     * Spring Security's {@link PasswordEncoder}.
     *
     * @param pLogin
     * @return a unique session id
     */
    protected final Object createSessionId(User pLogin, HttpServletRequest pRequest, PasswordEncoder passwordEncoder) {
        String wSessionId = passwordEncoder.encode(new StringBuilder(pLogin.getUsername())
                .append(IppmsEncoder.getFirstEncoder())
                .append(IppmsEncoder.getSecondEncoder())
                .toString());

        //put the session id in the HttpSession
        addSessionAttribute(pRequest, IppmsEncoder.getSessionKey(), wSessionId);

        return wSessionId;
    }


    /**
     * Add an attribute to the <code>HttpSession</code>.
     *
     * @param pRequest
     * @param pAttributeName name of the attribute
     * @param pValue         the value of the attribute
     */
    protected final static void addSessionAttribute(HttpServletRequest pRequest, String pAttributeName, Object pValue) {
        getSession(pRequest).setAttribute(pAttributeName, pValue);
    }

    /**
     * Remove an attribute from the <code>HttpSession</code>.
     *
     * @param pRequest
     * @param pAttributeName
     */
    protected final static void removeSessionAttribute(HttpServletRequest pRequest, String pAttributeName) {
        getSession(pRequest).setAttribute(pAttributeName, null);
    }


    protected static final Object getSessionId(HttpServletRequest pRequest) {
        return getSessionAttribute(pRequest, IppmsEncoder.getSessionKey());
    }

    /**
     * Thread safe access to the attributes in the <code>HttpSession</code>.
     *
     * @param pRequest
     * @param pAttributeName
     * @return an object stored in the {@link HttpSession} of the {@link HttpServletRequest}.
     */
    protected final static Object getSessionAttribute(HttpServletRequest pRequest, String pAttributeName) {
        Object wRetVal = getSession(pRequest).getAttribute(pAttributeName);

        return wRetVal;
    }

    /**
     * Spring Security Logout Method
     *
     * @param pRequest
     */
    protected final void logoutUser(HttpServletRequest pRequest) {
        getSession(pRequest).invalidate();
        SecurityContextHolder.clearContext();
    }

    /**
     * Get the business certificate of the currently logged in user.
     *
     * @param pRequest
     * @return
     * @throws Exception
     */
    protected final BusinessCertificate getBusinessCertificate(User pLogin, HttpServletRequest pRequest)
            throws HttpSessionRequiredException, EpmAuthenticationException, NoBusinessCertificationException {

        Object wObj = getSessionAttribute(pRequest, IppmsEncoder.getCertificateKey());
        if (wObj == null) {
            log.error("User '" + pLogin.getUsername() + "' has no Business Certificate in the Session.");
            throw new EpmAuthenticationException(new Throwable("User '" + pLogin.getUsername() + "' has no Business Certificate in the Session."));
        }

        return (BusinessCertificate) wObj;
    }

    protected final BusinessCertificate getBusinessCertificate(HttpServletRequest pRequest) {
        Object wObj = getSessionAttribute(pRequest, IppmsEncoder.getCertificateKey());
        return (BusinessCertificate) wObj;
    }

    protected void handleProcessOutcomeMessage(HttpServletRequest pRequest, Model pModel) {
        //get the message
        String msg = getOutcomeMessage(pRequest);
        //remove message form session.
        clearOutcomeMessage(pRequest);
        handleGetAfterSaveOperation(pRequest, msg, pModel);
    }

    protected String getOutcomeMessage(HttpServletRequest pRequest) {
        return (String) getSessionAttribute(pRequest, OUTCOME_MSG_KEY);
    }

    protected void clearOutcomeMessage(HttpServletRequest pRequest) {
        removeSessionAttribute(pRequest, OUTCOME_MSG_KEY);
    }

    protected void handleGetAfterSaveOperation(HttpServletRequest pRequest, String pMessage, Model pModel) {
        Object wObj = getSessionAttribute(pRequest, SAVE_PARAM);
        if (wObj != null) {
            pModel.addAttribute("saved", true);
            pModel.addAttribute("savedMsg", pMessage);
            //remove the save parameter from the session
            removeSessionAttribute(pRequest, SAVE_PARAM);
        }
    }

    /**
     * Determine if a POST request is a cancel action.
     *
     * @param pRequest
     * @param pReqParam
     * @return {@code true} or {@code false}
     * @since April 19th, 2013.
     */
    protected final boolean isCancelRequest(HttpServletRequest pRequest, String pReqParam) {
        boolean isCancel = false;
        if (pReqParam == null) {
            pReqParam = ServletRequestUtils.getStringParameter(pRequest, REQUEST_PARAM_CANCEL + ".x", "");

            if ((null != pReqParam) && (pReqParam.length() > 0)) {
                pReqParam = REQUEST_PARAM_CANCEL_VALUE;
            }
        }

        if (REQUEST_PARAM_CANCEL_VALUE.equalsIgnoreCase(pReqParam)) {
            isCancel = true;
        }

        return isCancel;
    }

    /**
     * Determine if a POST request is an update action.
     *
     * @param pRequest
     * @param pReqParam
     * @return {@code true} or {@code false}
     * @since May 3rd 2013
     */
    protected final boolean isUpdateReportRequest(HttpServletRequest pRequest, String pReqParam) {
        boolean isUpdate = false;
        if (pReqParam == null) {
            pReqParam = ServletRequestUtils.getStringParameter(pRequest, "_updateReport.x", "");

            if ((null != pReqParam) && (pReqParam.length() > 0)) {
                pReqParam = REQUEST_PARAM_UPDATE_REPORT_VALUE;
            }
        }

        if (REQUEST_PARAM_UPDATE_REPORT_VALUE.equalsIgnoreCase(pReqParam))
            isUpdate = true;

        return isUpdate;
    }

    protected final boolean isPrintRequest(HttpServletRequest pRequest, String pReqParam) {
        boolean isPrint = false;
        if (pReqParam == null) {
            pReqParam = ServletRequestUtils.getStringParameter(pRequest, REQUEST_PARAM_PRINT + ".x", "");

            if ((null != pReqParam) && (pReqParam.length() > 0)) {
                pReqParam = REQUEST_PARAM_PRINT_VALUE;
            }
        }

        if (REQUEST_PARAM_PRINT_VALUE.equalsIgnoreCase(pReqParam))
            isPrint = true;

        return isPrint;
    }

    protected void putSaveOperationParamInSession(HttpServletRequest pRequest) {
        addSessionAttribute(pRequest, SAVE_PARAM, SAVE_PARAM_VALUE);
    }

    protected static boolean isButtonTypeClick(HttpServletRequest request, final String requestParam) {
        if (StringUtils.isNotBlank(requestParam)) {
            String valueInRequest = ServletRequestUtils.getStringParameter(request, requestParam + ".x", "");
            return StringUtils.isNotBlank(valueInRequest);
        }

        return false;
    }


    protected static void addPageTitle(Model model, String pageTitle) {
        model.addAttribute("pageTitle", pageTitle);
    }

    protected static void addMainHeader(Model model, String mainHeader) {
        model.addAttribute("mainHeader", mainHeader);
    }

    protected static void addTableHeader(Model model, String tableHeader) {
        model.addAttribute("tableHeader", tableHeader);
    }

    protected static void addSaveMsgToFlashMap(RedirectAttributes redirectAttr, String msg) {
        redirectAttr.addFlashAttribute(SAVED_INDICATOR_KEY, true);
        redirectAttr.addFlashAttribute(SAVED_MSG, msg);
    }


    protected static void addSaveMsgToSession(HttpServletRequest request, String msg, String url) {
        addSessionAttribute(request, SAVED_INDICATOR_KEY, true);
        addSessionAttribute(request, SAVED_MSG, msg);
        addSessionAttribute(request, SAVED_URL, url);
    }


    protected static void removeSaveMsgFromSession(HttpServletRequest request) {
        removeSessionAttribute(request, SAVED_INDICATOR_KEY);
        removeSessionAttribute(request, SAVED_MSG);
        removeSessionAttribute(request, SAVED_URL);

    }

    /**
     * @param request
     * @param model
     * @since 22nd May 2016
     * This adds any saved message notification that is currently
     * on the session to the request model attribute and then
     * deletes it from the session.
     */
    public static void addSaveMsgOnSessionToModel(HttpServletRequest request, Model model) {
        //if saved message exists and the destination url is for this request
        if (IppmsUtils.isNotNull(getSessionAttribute(request, IConstants.SAVED_INDICATOR_KEY))) {

            if (((String) getSessionAttribute(request, IConstants.SAVED_URL)).equalsIgnoreCase(getFromFormInRequest(request))
                    || getFromFormInRequest(request).equalsIgnoreCase(request.getContextPath() + "/determineDashBoard.do")) {
                //add message to the model attributes
                model.addAttribute(IConstants.SAVED_INDICATOR_KEY, true);
                model.addAttribute(IConstants.SAVED_MSG, getSessionAttribute(request, IConstants.SAVED_MSG));

                //remove message from session
                removeSaveMsgFromSession(request);
            }

        }
    }

    /**
     * @param request
     * @param model
     * @author Mustola
     * @since 26th May 2016
     * This adds the save message from the controllers to the model
     */
    protected static void addSaveMsgToModel(HttpServletRequest request, Model model, String saveMsg) {
        //add message to the model attributes
        model.addAttribute(IConstants.SAVED_INDICATOR_KEY, true);
        model.addAttribute(IConstants.SAVED_MSG, saveMsg);

    }

    /**
     * @param request
     * @author Mustola
     * @since 22nd May 2016
     * This adds the request url to the Navigator From Form.
     * The URL includes the Context Path and any Parameters
     */
    public static void addFromFormToNavigator(HttpServletRequest request) {
        Navigator.getInstance(getSessionId(request)).setFromSessionForm(getFromFormInRequest(request));
    }

    /**
     * @param request
     * @return
     * @author Mustola
     * @since 22nd May 2016
     * This returns the full url with the context path and parameters
     */
    protected static String getFromFormInRequest(HttpServletRequest request) {
        String URL = request.getContextPath() + request.getServletPath();

        if (request.getQueryString() != null) {
            URL += ("?" + request.getQueryString());
        }
        return URL;
    }
    protected boolean isShowNotificationMsg(HttpServletRequest request, Class<?> clazz ){
        return Navigator.getInstance(getSessionId(request)).getFromClass() != null &&
                Navigator.getInstance(getSessionId(request)).getFromClass().isAssignableFrom(clazz);
    }
    protected PaginationBean getPaginationInfo(HttpServletRequest request) {
        PaginationBean paginationBean = new PaginationBean();
        paginationBean.setPageNumber(ServletRequestUtils.getIntParameter(request, "page", 1));
        paginationBean.setSortOrder(ServletRequestUtils.getStringParameter(request, "dir", "asc"));
        paginationBean.setSortCriterion(ServletRequestUtils.getStringParameter(request, "sort", null));

        return paginationBean;
    }

    protected  void addDisplayErrorsToModel(Model model, HttpServletRequest request){
        model.addAttribute(DISPLAY_ERRORS,BLOCK);
    }
    protected String treatOgNumber(String pInValue, BusinessCertificate businessCertificate){
        if(!pInValue.startsWith(businessCertificate.getEmpIdStartVal()))
            return businessCertificate.getEmpIdStartVal()+pInValue;
        else
            return pInValue;
    }
    protected HiringInfo loadHiringInfoByEmpId(HttpServletRequest request, BusinessCertificate bc, Long pEmpId) throws IllegalAccessException, InstantiationException {
         return genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(),pEmpId)));
    }

    protected HiringInfo loadHiringInfoById(HttpServletRequest request, BusinessCertificate bc, Long pId) throws IllegalAccessException, InstantiationException {
        return genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id",pId)));
    }
    protected ConfigurationBean loadConfigurationBean(HttpServletRequest request) throws InstantiationException, IllegalAccessException {
        return genericService.loadObjectWithSingleCondition(ConfigurationBean.class,getBusinessClientIdPredicate(request));
    }

    protected  void resetNavigatorFromClass(HttpServletRequest request){
        Navigator.getInstance(getSessionId(request)).setFromClass(null);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public class PaginationBean {
        private int pageNumber;
        private String sortOrder;
        private String sortCriterion;
    }
    public static MultipartFile base64ToMultipart(String base64) {

        byte[] b = Base64.getDecoder().decode(base64);

        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }

        return new Base64Util(b, base64);
    }
    public static Object treatAsTextOrNumber(Object value, int semaphore){
        Object retVal;

        if(semaphore == 1)
            retVal = IConstants.naira+PayrollHRUtils.getDecimalFormat().format(value);
        else
            retVal = PayrollHRUtils.removeCommas(PayrollHRUtils.getDecimalFormat().format(value));

        return retVal;
    }
    public static Object setReportValueType(Object value, int val1, int val2){

        if(val1 == 1)
            if(value.getClass().isAssignableFrom(Double.class))
                value = new java.math.BigDecimal((Double)value / 12.0D).setScale(2, RoundingMode.HALF_EVEN);

        return treatAsTextOrNumber(value,val2);
    }
}