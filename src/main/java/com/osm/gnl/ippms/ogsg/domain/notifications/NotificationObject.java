package com.osm.gnl.ippms.ogsg.domain.notifications;

import com.osm.gnl.ippms.ogsg.domain.approval.AbstractApprovalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ippms_notification_obj")
@SequenceGenerator(name = "notificationObjSeq", sequenceName = "ippms_notification_obj_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class NotificationObject extends AbstractApprovalEntity implements Serializable, Comparable<NotificationObject> {

    @Id
    @GeneratedValue(generator = "notificationObjSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "not_obj_inst_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name= "ticket_time")
    protected Timestamp ticketTime = Timestamp.from(Instant.now());

    @Column(name = "ticket_open", columnDefinition = "integer default '0'")
    private int ticketOpen;

    @Column(name = "approval_object_type_ind", columnDefinition="integer default '1'", nullable = false)
    protected int approvalObjectType;

    @Column(name = "sender_inst_id", nullable = false)
    private Long sender;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "response_ind", columnDefinition = "integer default '0'" )
    private int responseInd;

    @Transient private String response;
    @Transient private boolean messageRead;
    @Transient private String ticketOpenStr;
    @Transient private String memoSubString;
    @Transient private String username;
    @Transient private Integer filterInd;
    @Transient private List<NotificationObject> objectList;



    public String getUsername(){
        return  this.initiator.getActualUserName();
    }


    public String getTicketOpenStr(){
        switch(this.ticketOpen) {
            case 0:
                ticketOpenStr = "Open";
                break;
            case 1:
                ticketOpenStr = "Closed";
                break;
        }
        return ticketOpenStr;
    }

    public String getMemoSubString(){
        if (approvalMemo.length() < 9){
            return approvalMemo;
        }
        else {
            memoSubString = approvalMemo.substring(0, 9) + "...";
            return memoSubString;
        }
    }

    public NotificationObject(Long pId) {
        this.id = pId;
    }
    public boolean isNewEntity(){
        return this.id == null;
    }
    public boolean isTicketIdNull(){return ticketId == null;}

    @Override
    public int compareTo(NotificationObject notificationObject) {
        return 0;
    }
}
