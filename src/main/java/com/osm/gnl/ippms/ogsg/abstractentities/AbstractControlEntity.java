package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import com.osm.gnl.ippms.ogsg.report.beans.ReportViewAttributes;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;


@SuppressWarnings("serial")
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractControlEntity extends ReportViewAttributes implements Serializable, Comparable<Object> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    protected User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "last_mod_by", nullable = false)
    protected User lastModBy;

    @Column(name = "creation_date", nullable = false)
    protected Timestamp creationDate = Timestamp.from(Instant.now());

    @Column(name = "last_mod_ts", nullable = false)
    protected Timestamp lastModTs;



    public String getLastModTsForDisplay(){
        return PayrollUtils.formatTimeStamp(this.lastModTs);
    }
    public String getCreatedDateStrForDisplay(){
        return PayrollUtils.formatTimeStamp(this.creationDate);
    }
	public abstract int compareTo(Object o) ;
	
	public abstract boolean isNewEntity() ;


    @Transient
    protected String lastModTsForDisplay;
    @Transient
    protected String createdDateStrForDisplay;
    @Transient
    protected boolean editMode;
    @Transient
    protected String displayStyle;
    @Transient
    protected String expirationDateStr;
    @Transient
    protected String createdDateStr;
    @Transient
    protected String amountStr;
    @Transient
    protected String amountAsStr;
    @Transient
    protected int serialNum;
    @Transient
    protected boolean expired;
    @Transient
    protected boolean canEdit;
    @Transient
    protected String creator;
    @Transient
    protected String lastModifier;
    @Transient
    protected String displayName;
    @Transient
    protected boolean confirmation;
    @Transient
    private BusinessCertificate roleBean;
    @Transient
    private LocalDate fromDate;
    @Transient
    private LocalDate toDate;
    @Transient
    private String fromDateStr;
    @Transient
    private String toDateStr;
    @Transient
    private String remove = "Remove";
    @Transient
    protected int lastEdited;


 }
