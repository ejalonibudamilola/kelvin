/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.controllers.auth;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.forensic.domain.Materiality;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping("/setMateriality.do")
@SessionAttributes(types = {MaterialityDisplayBean.class})
public class MaterialityController extends BaseController {

    private final String VIEW = "configcontrol/setResetMaterialityForm";

    public MaterialityController() {
    }

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam(value = "noe", required = false) Integer pNoOfEdits, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = super.getBusinessCertificate(request);

        List<Materiality> materiality = this.genericService.loadAllObjectsWithSingleCondition(Materiality.class, getBusinessClientIdPredicate(request), null);
        if (IppmsUtils.isNullOrLessThanOne(pNoOfEdits)) {
            List<User> users = this.genericService.loadAllObjectsUsingRestrictions(User.class, Arrays.asList(CustomPredicate.procurePredicate("role.businessClient.id", bc.getBusinessClientInstId()), CustomPredicate.procurePredicate("accountExpired", OFF),
                    CustomPredicate.procurePredicate("accountEnabled", OFF), CustomPredicate.procurePredicate("accountLocked", OFF), CustomPredicate.procurePredicate("deactivatedInd", OFF),
                    CustomPredicate.procurePredicate("passwordExpired", OFF)), "username");
            Map<Long, Long> longMap = makeUserHashMap(materiality);
            for (User user : users) {
                if (!longMap.containsKey(user.getId())) {
                    Materiality materiality1 = new Materiality();
                    materiality1.setUser(user);
                    materiality.add(materiality1);
                }
            }
        } else {
            model.addAttribute("saved", true);
            addSaveMsgToModel(request, model, "Materiality Updated Successfully.");
            bc.setCanApproveLeaveBonus(true); //Used to Disable on Save.
        }
        bc.setCanApproveLeaveBonus(!bc.isSuperAdmin());

        for (Materiality m : materiality) {
            if (m.getValue() > 0.0D)
                m.setOldValue(m.getValue());
            else
                m.setOldValue(0.0D);

            m.setValueAsStr(PayrollHRUtils.getDecimalFormat().format(m.getValue()));

        }
        MaterialityDisplayBean materialityDisplayBean = new MaterialityDisplayBean(materiality);
        addRoleBeanToModel(model, request);
        model.addAttribute("viewBean", materialityDisplayBean);

        return VIEW;


    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value = "_cancel", required = false) String cancel,
                                @ModelAttribute("viewBean") MaterialityDisplayBean pLPB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request)
            throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = getBusinessCertificate(request);

        if (isButtonTypeClick(request, REQUEST_PARAM_CANCEL) || !bc.isSuperAdmin())
            return REDIRECT_TO_DASHBOARD;


        List<Materiality> saveList = new ArrayList<>();
        for (Materiality materiality : (List<Materiality>) pLPB.getObjectList()) {
            if (materiality.isNewEntity()) {
                materiality.setBusinessClientId(bc.getBusinessClientInstId());
                materiality.setCreatedBy(new User(bc.getLoginId()));
                materiality.setLastModBy(new User(bc.getLoginId()));
                materiality.setLastModTs(Timestamp.from(Instant.now()));
                saveList.add(materiality);
            } else {
                if (!StringUtils.trimToEmpty(materiality.getValueAsStr()).equals("")) {
                    materiality.setValue(Double.parseDouble(PayrollHRUtils.removeCommas(materiality.getValueAsStr())));
                } else {
                    materiality.setValue(0.0D);
                }
                if (materiality.getOldValue() != materiality.getValue()) {
                    materiality.setLastModBy(new User(bc.getLoginId()));
                    materiality.setLastModTs(Timestamp.from(Instant.now()));
                    saveList.add(materiality);
                }
            }

        }
        Integer noe = saveList.size();
        if (noe > 0) {
            this.genericService.storeObjectBatch(saveList);
        }
        return "redirect:setMateriality.do?noe=" + noe;
    }

    private Map<Long, Long> makeUserHashMap(List<Materiality> materiality) {

        Map<Long, Long> wRetVal = new HashMap<>();
        for (Materiality m : materiality)
            wRetVal.put(m.getUser().getId(), m.getUser().getId());

        return wRetVal;
    }


}
