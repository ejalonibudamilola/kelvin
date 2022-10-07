/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.customreports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

@Entity
@Table(name = "ippms_report_obj_attr")
@SequenceGenerator(name = "reportObjAttrSeq", sequenceName = "ippms_report_obj_attr_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class CustomReportObjectAttr implements Comparable<CustomReportObjectAttr>{
    @Id
    @GeneratedValue(generator = "reportObjAttrSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rep_obj_attr_inst_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rep_obj_inst_id",nullable = false)
    private CustomReportObject customReportObject;

    @Column(name = "def_display_name", nullable = false, length = 40)
    private String defDisplayName;

    @Column(name = "attr_name", nullable = false, length = 30)
    private String attrName;

    @Column(name = "name_type_column", nullable = false, length = 30)
    private String nameTypeColumn;


    @Column(name = "pref_display_name", nullable = false, length = 40)
    private String prefDisplayName;

    @Column(name = "col_type", nullable = false, length = 40)
    private String columnType;

    @Column(name = "pension_restricted", nullable = false, columnDefinition = "integer (1) default '0'")
    private int pensionerRestricted;

    @Column(name = "non_pension_restricted", nullable = false, columnDefinition = "integer (1) default '0'")
    private int nonPensionerRestricted;

    @Column(name = "sum_candidate", nullable = false, columnDefinition = "integer (1) default '0'")
    private int sumInd;

    @Column(name = "order_position", nullable = false, columnDefinition = "numeric (1) default '0'")
    private int orderPosition;

    @Column(name = "mda_override_ind", nullable = false, columnDefinition = "integer (1) default '0'")
    private int mdaOverrideInd;

    @Column(name = "name_type_ind", nullable = false, columnDefinition = "integer (1) default '0'")
    private int nameTypeInd;

    @Column(name = "name_type_group_ind", nullable = false, columnDefinition = "integer (1) default '0'")
    private int nameTypeGroupInd;

    @Column(name = "inactive_ind", nullable = false, columnDefinition = "integer (1) default '0'")
    private int inActiveInd;

    @Transient private boolean appliesToAll;
    @Transient private boolean orderable;
    @Transient private boolean pensionRestricted;
    @Transient private boolean activeRestricted;
    @Transient private List<String> conditions;
    @Transient private HashMap<String,String> addendum;
    @Transient private int column;
    @Transient private boolean sumCandidate;
    @Transient private boolean usesBetween;
    @Transient private boolean usesGreaterOrLessThan;
    @Transient private boolean mustSumDoubleFields;
    @Transient private boolean usesIn;
    @Transient private boolean mdaOverride;
    @Transient private boolean nameType;
    @Transient private boolean baseNameType;
    @Transient private ReportFilterObj reportFilterObj;
    @Transient private boolean usingDef;
    @Transient private String splitNameTypeColumns;
    @Transient private String headerName;

    public boolean isNameType(){
        nameType = this.nameTypeInd == 1;
        return nameType;
    }

    public String getHeaderName() {
        if(this.isUsingDef())
            headerName = this.getDefDisplayName();
        else
            headerName = this.getPrefDisplayName();
        return headerName;
    }

    public String getSplitNameTypeColumns() {
        if (nameTypeColumn != null) {
            StringTokenizer stringTokenizers = new StringTokenizer(nameTypeColumn, "||'%'||");
            StringBuffer strBuff = new StringBuffer();
            while (stringTokenizers.hasMoreTokens())
                strBuff.append(stringTokenizers.nextToken()).append(",");

            splitNameTypeColumns = strBuff.toString().substring(0, strBuff.toString().lastIndexOf(",")).trim();

        }
        return splitNameTypeColumns;
    }

    public boolean isBaseNameType() {
        baseNameType = this.nameTypeGroupInd == 1;
        return baseNameType;
    }

    public boolean isSumCandidate() {
        sumCandidate = this.sumInd == 1;
        return sumCandidate;
    }

    public boolean isMdaOverride() {
        mdaOverride = mdaOverrideInd == 1;
        return mdaOverride;
    }

    public boolean isActiveRestricted() {
        activeRestricted = this.getNonPensionerRestricted() == 0 && this.getPensionerRestricted() == 1;
        return activeRestricted;
    }

    public boolean isAppliesToAll() {
        appliesToAll = this.getNonPensionerRestricted() == 0 && this.getPensionerRestricted() == 0;
        return appliesToAll;
    }

    public boolean isPensionRestricted() {
        pensionRestricted = this.getNonPensionerRestricted() == 1 && this.getPensionerRestricted() == 0;
        return pensionRestricted;
    }

    public boolean isOrderable() {
        orderable = this.orderPosition > 0;
        return orderable;
    }

    @Override
    public int compareTo(CustomReportObjectAttr customReportObjectAttr) {
        return 0;
    }
}
