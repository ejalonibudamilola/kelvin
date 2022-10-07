package com.osm.gnl.ippms.ogsg.organization.model;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_mda_info")
@SequenceGenerator(name = "mdaInfoSeq", sequenceName = "ippms_mda_info_seq", allocationSize = 1)
@Setter
@Getter
@EqualsAndHashCode
public class MdaInfo extends AbstractControlEntity {


    @Id
    @GeneratedValue(generator = "mdaInfoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mda_inst_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    private String name;

    @Column(name = "description", length = 60, nullable = false)
    private String description;

    @Column(name = "code_name", length = 40, nullable = false)
    private String codeName;
    /**
     * Relationship to the Entity defining MDA Types:
     * be it Ministry, Agency, LGEA, LGA etc
     */
    @ManyToOne
    @JoinColumn(name = "mda_type_inst_id")
    private MdaType mdaType;

    /**
     * This is only for ease of retrieving MDA_INFO and MDA_DEPT_MAP_INFO
     */
    @Column(name = "business_client_inst_id")
    private Long businessClientId;

    /*
     * @Column(name = "CODE_NAME_OGS", length = 10) private String codeNameOgs;
     */
    @Column(name = "deactivated", columnDefinition = "integer default '0'")
    private int deactivatedIndicator;


    @Column(name = "deactivated_date")
    private LocalDate deactivatedDate;

    @Column(name = "school_ind", columnDefinition = "integer default '0'")
    private int schoolIndicator;

    @Column(name = "mapable", columnDefinition = "integer default '0'")
    private int mappableInd;

    @Column(name = "email_address", length = 40)
    private String emailAddress;

    @Transient
    private boolean isDeactivated;
    @Transient
    private String codeNameOgs;
    @Transient
    private boolean schoolAttached;
    @Transient
    private  long totalNoOfEmployees;
    @Transient
    private  double totalTaxesPaid;
    @Transient
    private  double totalGrossPay;
    @Transient
    private  String totalGrossPayStr;
    @Transient
    private  double totalMonthlyBasic;
    @Transient
    private  String totalMonthlyBasicStr;
    @Transient
    private  double totalNetPay;
    @Transient
    private  String totalNetPayStr;
    @Transient
    private double netIncrease;

    @Transient
    private int noOfDept;
    @Transient
    private boolean canEdit;




    public MdaInfo() {
    }

    public MdaInfo(Long id) {
        this.id = id;
    }

    public MdaInfo(Long id,String pMdaName) {
        this.name = pMdaName;
        this.id = id;
    }
    public MdaInfo(Long pId, String pMdaName, String pCodeName) {
        this.id = pId;
        this.name = pMdaName;
        this.codeName = pCodeName;
    }


    public boolean isDeactivated() {
        return this.deactivatedIndicator > 0;
    }


    public boolean isSchoolAttached() {
        return this.schoolIndicator == 1;
    }



    public String getTotalGrossPayStr() {
        this.totalGrossPayStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.totalGrossPay));
        return totalGrossPayStr;
    }

    public String getTotalMonthlyBasicStr() {
        this.totalMonthlyBasicStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.totalMonthlyBasic));

        return totalMonthlyBasicStr;
    }

    public String getTotalNetPayStr() {
        this.totalNetPayStr = (IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.totalNetPay));

        return totalNetPayStr;
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
