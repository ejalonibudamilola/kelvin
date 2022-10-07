package com.osm.gnl.ippms.ogsg.domain.message;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ippms_message_obj")
@SequenceGenerator(name = "messageObjSeq", sequenceName = "ippms_message_obj_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MessageObject implements Serializable,Comparable<MessageObject> {

    @Id
    @GeneratedValue(generator = "messageObjSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "msg_obj_inst_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "msg_body",  length = 40000,nullable = false)
    private String msgBody;

    @Column(name = "time_sent", nullable = false)
    private Timestamp timeSent;

    @Column(name = "data_status", columnDefinition = "integer default '0'")
    private int dataStatus;

    @Transient
    private boolean messageRead;

    public boolean isMessageRead() {
        messageRead = dataStatus == 1;
        return messageRead;
    }

    public MessageObject(Long pId) {
        this.id = pId;
    }
    public boolean isNewEntity(){
        return this.id == null;
    }


    @Override
    public int compareTo(MessageObject messageObject) {
        return 0;
    }
}

