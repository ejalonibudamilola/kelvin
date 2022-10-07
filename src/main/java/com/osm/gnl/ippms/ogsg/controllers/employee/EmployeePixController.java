/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.employee;


import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.HrPassportInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.employee.beans.EmployeeBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;

@Controller
@RequestMapping({"/editEmployeePassportForm.do"})
@SessionAttributes(types={EmployeeBean.class})
public class EmployeePixController extends BaseController {



        //passport key holder - by Adegbesan Oluwafemi , monday 7:38pm;
        /*
         * variable passport_key is used just to hold something for transport to
         * servlet so u can pass any default string value
         */
        public static final String PASSPORT_KEY = "_any_thing";
        private static final String INTENT = "update";

        private final String VIEW = "employee/employeePassportOverviewForm";
        /* for image conversion to megabyte value */
        private static final long K = 1024;
        private static final long M = K * K;
        private static final long G = M * K;
        private static final long T = G * K;


        private final Integer maxUploadValue;

        @Autowired
        public EmployeePixController( @Value("${maxUploadValue}") Integer maxUploadValue)
        {
            this.maxUploadValue = maxUploadValue;
        }

        @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"eid"})
        public String setupForm(@RequestParam("eid") Long empId, Model model, HttpServletRequest request) throws Exception
        {
            SessionManagerService.manageSession(request, model);
            Object userId = getSessionId(request);
            BusinessCertificate bc = this.getBusinessCertificate(request);
            NamedEntity ne = new NamedEntity();
            ne.setId(empId);

            EmployeeBean pEB = new EmployeeBean();
            ne.setMode("edit");
            pEB.setEmployee(IppmsUtils.loadEmployee(genericService,empId,bc));
            ne.setName(pEB.getEmployee().getDisplayNameWivTitlePrefixed());


            pEB.setId(bc.getBusinessClientInstId());

            addSessionAttribute(request, NAMED_ENTITY, ne);
            model.addAttribute("namedEntity", ne);
            model.addAttribute("employeeBean", pEB);
            model.addAttribute("roleBean", bc);
            Navigator.getInstance(userId).setFromClass(getClass());
            Navigator.getInstance(userId).setFromForm("redirect:employeeEnquiryForm.do?eid=" + ne.getId());

            //method to get passport pix display
            HrPassportInfo  pPhoto = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), ne.getId() ));
            if( pPhoto.getId() == null ){
                model.addAttribute("photo","");
                pPhoto.setEditMode(false);
            }else{

                addSessionAttribute(request, EmployeeEnquiryOverviewForm.PASSPORT_KEY, pPhoto );
                model.addAttribute("wModifiedBy", pPhoto.getLastModBy().getActualUserName());
                model.addAttribute("photo", pPhoto);
            }

            //end of passport display

            return VIEW;
        }


        @RequestMapping(method = RequestMethod.POST)
        public String processSubmit(@RequestParam(value = "_upload",required = false) String pUpload,
                                    @RequestParam(value="_close", required=false) String close,
                                    @RequestParam(value="file") MultipartFile file,
                                    @RequestParam(value="intent",required = false) String pAction,
                                    @ModelAttribute("informationBean") HrPassportInfo pHrPassportInfo,
                                    @ModelAttribute("employeeBean") EmployeeBean pEmployeeBean,
                                    BindingResult pResult, SessionStatus pStatus, HttpServletRequest request, Model pModel)
                throws Exception {

            //get the account session
            SessionManagerService.manageSession(request, pModel);
            BusinessCertificate bc = this.getBusinessCertificate(request);

            //retain employeeid before process
            Long EmployeeIDbeforeFlush = pEmployeeBean.getEmployee().getId();
            NamedEntity ne = new NamedEntity();
            ne.setId(EmployeeIDbeforeFlush);
            ne.setName(pEmployeeBean.getEmployee().getDisplayNameWivTitlePrefixed());

            if(file.getSize() > maxUploadValue) {
                pResult.rejectValue("", "DEF.EDIT.VOID", "File too Large to upload " + convertToStringRepresentation(file.getSize())
                        + ", Should not be more than " + convertToStringRepresentation(maxUploadValue));
                pModel.addAttribute(DISPLAY_ERRORS, BLOCK);
                pModel.addAttribute("status", pResult);
                pModel.addAttribute("namedEntity", ne);
                pModel.addAttribute("employeeBean", pEmployeeBean);
                pModel.addAttribute("roleBean", bc);
                pModel.addAttribute("informationBean", pHrPassportInfo);

                return VIEW;

            }

            if (isButtonTypeClick(request, REQUEST_PARAM_CLOSE)) {
                return "redirect:employeeOverviewForm.do";

            }

            //loads item from db first
            pHrPassportInfo = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class, CustomPredicate.procurePredicate(bc.getEmployeeIdJoinStr(), EmployeeIDbeforeFlush ));


            if(pHrPassportInfo.isNewEntity()){
                //Blob blob = Hibernate.createBlob(file.getInputStream());
                pHrPassportInfo.setPhoto(file.getBytes());
                // pHrmPassport.setPhoto(blob);
                pHrPassportInfo.setPhotoType(file.getContentType());
                if(bc.isPensioner())
                    pHrPassportInfo.setPensioner(new Pensioner(EmployeeIDbeforeFlush));
                else
                   pHrPassportInfo.setEmployee(new Employee(EmployeeIDbeforeFlush));

                pHrPassportInfo.setCreatedBy(new User(bc.getLoginId()));

            }
            else if( ( pAction != null && pAction.equalsIgnoreCase( INTENT ) )  || !pHrPassportInfo.isNewEntity() ) {

                pHrPassportInfo.setPhoto(file.getBytes());
                pHrPassportInfo.setPhotoType(file.getContentType());
                if(bc.isPensioner())
                    pHrPassportInfo.setPensioner(new Pensioner(EmployeeIDbeforeFlush));
                else
                    pHrPassportInfo.setEmployee(new Employee(EmployeeIDbeforeFlush));

            }
            pHrPassportInfo.setLastModBy(new User(bc.getLoginId()));
            pHrPassportInfo.setLastModTs(Timestamp.from(Instant.now()));
            pHrPassportInfo.setBusinessClientId(bc.getBusinessClientInstId());
            try {
                //store document
                if(pHrPassportInfo.getPhoto() == null){
                    pResult.rejectValue("", "DEF.EDIT.VOID",  "Please select a Picture to upload ");
                    pModel.addAttribute(DISPLAY_ERRORS, BLOCK);
                    pModel.addAttribute("status", pResult);
                    pModel.addAttribute("namedEntity", ne);
                    pModel.addAttribute("employeeBean", pEmployeeBean);
                    pModel.addAttribute("roleBean", bc);
                    pModel.addAttribute("informationBean", pHrPassportInfo);

                    return VIEW;
                }
                this.genericService.storeObject(pHrPassportInfo);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return "redirect:editEmployeePassportForm.do?eid="+EmployeeIDbeforeFlush ;

        }



        public static String convertToStringRepresentation(final long value){
            final long[] dividers = new long[] { T, G, M, K, 1 };
            final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
            if(value < 1)
                throw new IllegalArgumentException("Invalid file size: " + value);
            String result = null;
            for(int i = 0; i < dividers.length; i++){
                final long divider = dividers[i];
                if(value >= divider){
                    result = format(value, divider, units[i]);
                    break;
                }
            }
            return result;
        }


        private static String format(final long value,
                                     final long divider,
                                     final String unit){
            final double result =
                    divider > 1 ? (double) value / (double) divider : (double) value;
            return new DecimalFormat("#.#").format(result) + " " + unit;
        }


}



