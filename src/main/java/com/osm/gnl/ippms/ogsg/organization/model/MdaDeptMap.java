package com.osm.gnl.ippms.ogsg.organization.model;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_mda_dept_map")
@SequenceGenerator(name = "mdaDeptMapSeq", sequenceName = "ippms_mda_dept_map_seq", allocationSize = 1)
@Setter
@Getter
@EqualsAndHashCode
public class MdaDeptMap extends AbstractControlEntity {

    @Id
    @GeneratedValue(generator = "mdaDeptMapSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mda_dept_map_inst_id")
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mda_inst_id")
    private MdaInfo mdaInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_inst_id")
    private Department department;

    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    @Column(name = "preferred_name", length = 150)
    private String preferredName;

    @Column(name = "preferred_desc", length = 200)
    private String preferredDesc;

    @Column(name = "dept_director", length = 200)
    private String deptDirector;

    @Column(name = "employee_inst_id")
    private Long employeeId;
    @Transient
    private String headOfDept;



    public MdaDeptMap() {}


    public MdaDeptMap(Long id,Long pChildId) {
        this.id = id;
        this.setMdaInfo(new MdaInfo(pChildId));
    }

    public MdaDeptMap(Long id, Long pMdaInfoId, String pMdaName) {
        this.id = id;
        this.setMdaInfo(new MdaInfo(pMdaInfoId,pMdaName ));
    }
    public MdaDeptMap(Long long1) {
        this.id = long1;
    }


    public MdaDeptMap(Long pBusClientId, Long pId, Long pDeptId) {
        this.businessClientId = pBusClientId;
        this.setMdaInfo(new MdaInfo(pId));
        this.setDepartment(Department.build(pDeptId));
    }
    public MdaDeptMap(Long pBusClientId, Long pId, Long pDeptId, Long pEmployeeId) {
        this.businessClientId = pBusClientId;
        this.setMdaInfo(new MdaInfo(pId));
        this.setDepartment(Department.build(pDeptId));
        this.setEmployeeId(pEmployeeId);
    }


    public String getPreferredName() {
        if(this.preferredName == null && this.getDepartment() != null)
            this.preferredName = this.getDepartment().getName();
        return preferredName;
    }



    public String getPreferredDesc() {
        if(this.preferredDesc == null && this.getDepartment() != null)
            this.preferredDesc = this.getDepartment().getDescription();
        return preferredDesc;
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
