package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.approval.AbstractApprovalEntity;
import com.osm.gnl.ippms.ogsg.domain.notifications.NotificationObject;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;

import java.util.Arrays;

public abstract class NotificationService {

    public static void storeNotification(BusinessCertificate bc, GenericService genericService, AbstractApprovalEntity abstractApprovalEntity,
                                         String pUrl, String msg, int pObjectTypeCode) throws IllegalAccessException, InstantiationException {
        NotificationObject notificationObject = genericService.loadObjectUsingRestriction(NotificationObject.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()),
                        CustomPredicate.procurePredicate("ticketId", abstractApprovalEntity.getTicketId())));


        if(notificationObject.isNewEntity()){

            notificationObject.setInitiator(abstractApprovalEntity.getInitiator());
            notificationObject.setEntityId(abstractApprovalEntity.getEntityId());
            notificationObject.setEntityName(abstractApprovalEntity.getEntityName());
            notificationObject.setEmployeeId(abstractApprovalEntity.getEmployeeId());
            if(IppmsUtils.isNullOrEmpty(abstractApprovalEntity.getApprovalMemo()))
                notificationObject.setApprovalMemo(msg);
            else
                notificationObject.setApprovalMemo(abstractApprovalEntity.getApprovalMemo());
            notificationObject.setUrl(pUrl);
            notificationObject.setSubject(msg);
            notificationObject.setSender(bc.getLoginId());
            notificationObject.setSenderName(bc.getLoggedOnUserNames());
            notificationObject.setTicketId(abstractApprovalEntity.getTicketId());
            notificationObject.setLastModTs(abstractApprovalEntity.getLastModTs());
            notificationObject.setBusinessClientId(bc.getBusinessClientInstId());
            notificationObject.setApprovalObjectType(pObjectTypeCode);
        }else{
                notificationObject.setApprover(new User(bc.getLoginId()));
                notificationObject.setApprovalStatusInd(abstractApprovalEntity.getApprovalStatusInd());
                notificationObject.setApprovedDate(abstractApprovalEntity.getApprovedDate());
                notificationObject.setApprovalMemo(abstractApprovalEntity.getApprovalMemo());
                notificationObject.setLastModTs(abstractApprovalEntity.getLastModTs());
                notificationObject.setApprovalAuditTime(abstractApprovalEntity.getApprovalAuditTime());
            /**
             * Mustola 18th August 2022.
             * CASP Request.
             * Close Tickets after Updates.
             *
             */
              notificationObject.setTicketOpen(IConstants.ON);
              notificationObject.setResponseInd(IConstants.ON);
        }


        genericService.saveObject(notificationObject);

    }

}
