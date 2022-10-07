package com.osm.gnl.ippms.ogsg.organization.model;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ippms_departments")
@SequenceGenerator(name = "deptSeq", sequenceName = "ippms_dept_seq", allocationSize = 1)
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Department extends AbstractNamedEntity {


    private static final long serialVersionUID = -4520212396574829013L;


    @Id
    @GeneratedValue(generator = "deptSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "dept_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @Column(name = "deactivated")
    private int deactivate;

    @Column(name = "mapable")
    private int mapable;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "deactivated_date")
    private LocalDate deactivatedDate;

    @Column(name = "default_ind",columnDefinition = "integer default '0'")
    private int defaultInd;


    @Transient
    private boolean deactivated;
    @Transient
    private boolean editMode;
    @Transient
    private Long mdaDeptMapId;

    @Transient
    private Long mdaInstId;
    @Transient
    private boolean mapIt;
    @Transient
    private String globalChange;
    @Transient
    private Long parentInd;
    @Transient
    private String parentName;
    @Transient
    private List<?> parentObjectList;
    @Transient
    private String deptHead;
    @Transient
    private String parentObjectName;
    @Transient
    private String parentObjectParentName;
    @Transient
    private boolean ministry;
    @Transient
    private boolean showDeptHead;
    @Transient
    private boolean all;
    @Transient
    private boolean defaultIndBind;

    public Department(){}

    private Department(Long pDeptId)
    {
        this.id = pDeptId;
    }

    private Department(Long pDeptId, String pName, String pDescription) {
        this.id = pDeptId;
        this.name = pName;
        this.description = pDescription;
    }


    public static Department build(Long pUserId, Long businessClientId, String pName, String pDescription,int mapable, int defaultInd){
        Department department = new Department();
        department.lastModBy = new User(pUserId);
        department.createdBy = new User(pUserId);
        department.businessClientId = businessClientId;
        department.name = pName;
        department.description = pDescription;
        department.mapable = mapable;
        department.defaultInd = defaultInd;
        department.lastModTs = Timestamp.from(Instant.now());
        return  department;
    }
    public static Department build(Long pDeptId){

        return  new Department(pDeptId);
    }
    public static Department build(Long pDeptId, String pName, String pDescription){
        Department department = new Department(pDeptId,pName,pDescription);

        return  department;
    }
    public boolean isDeactivated()
    {
        return this.deactivate == 1;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
