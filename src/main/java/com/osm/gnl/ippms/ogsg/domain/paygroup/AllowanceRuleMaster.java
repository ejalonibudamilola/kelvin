/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.paygroup;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.utils.annotation.AnnotationProcessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "ippms_allow_rule_master" )
@SequenceGenerator(name = "allowRuleMasterSeq", sequenceName = "ippms_allow_rule_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class AllowanceRuleMaster extends AbstractControlEntity {

    @Id
    @GeneratedValue(generator = "allowRuleMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rule_master_inst_id")
    private Long id;


    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "hire_info_inst_id", nullable = false)
    private HiringInfo hiringInfo;

    @OneToMany(mappedBy = "allowanceRuleMaster", fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
    private List<AllowanceRuleDetails>  allowanceRuleDetailsList;

    @Column(name = "rule_start_date")
    private LocalDate ruleStartDate;

    @Column(name = "rule_end_date")
    private LocalDate ruleEndDate;
    /**
     * Do not Edit this Semaphore. If not, Allowance Rules will not work properly.
     */
    @Column(name = "active_ind", columnDefinition = "integer default '0'", nullable = false)
    private int activeInd;

    @ManyToOne
    @JoinColumn(name = "user_inst_id", nullable = false)
    private User lastModBy;

    @Transient private String status;

    public AllowanceRuleMaster(Long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllowanceRuleMaster)) return false;
        AllowanceRuleMaster that = (AllowanceRuleMaster) o;
        return getActiveInd() == that.getActiveInd() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getBusinessClientId(), that.getBusinessClientId()) &&
                Objects.equals(getHiringInfo(), that.getHiringInfo());
    }

    public void build() throws IllegalAccessException {

        if(IppmsUtils.isNullOrEmpty(this.getAllowanceRuleDetailsList()))
        {
            List<AllowanceRuleDetails> allowanceRuleDetailsList = new ArrayList<>();
            Map<String,Double> noZeroAllowances = AnnotationProcessor.getNonZeroAllowanceAnnotations(this.getHiringInfo().getAbstractEmployeeEntity().getSalaryInfo(),
                    Double.class);
            AllowanceRuleDetails allowanceRuleDetails;
            for(String string :  noZeroAllowances.keySet()){
                allowanceRuleDetails = new AllowanceRuleDetails();
                allowanceRuleDetails.setBeanFieldName(string);
                allowanceRuleDetails.setYearlyValue(noZeroAllowances.get(string));
                allowanceRuleDetails.setMonthlyValue(EntityUtils.divideAsBigDecimal(allowanceRuleDetails.getYearlyValue(),12.0D));
                allowanceRuleDetailsList.add(allowanceRuleDetails);
            }
            Collections.sort(allowanceRuleDetailsList,Comparator.comparing(AllowanceRuleDetails::getBeanFieldName));
            this.setAllowanceRuleDetailsList(allowanceRuleDetailsList);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBusinessClientId(), getHiringInfo(), getActiveInd());
    }

    private boolean isActive(){
        return this.activeInd == 1;
    }
    private boolean isAwaitingApproval(){return  this.activeInd == 2;}

    public String getStatus() {
        if(isActive())
            status = "Active";
        if(isAwaitingApproval())
            status = "Awaiting Approval";
        if(this.activeInd == 0)
            status = "Expired";
        return status;
    }
    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public int compareTo(Object allowanceRuleMaster) {
        return 0;
    }
}
