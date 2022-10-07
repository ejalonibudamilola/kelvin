/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.employee.beans;

import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.control.entities.Religion;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.NextOfKin;
import com.osm.gnl.ippms.ogsg.nextofkin.domain.RelationshipType;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.BankBranch;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewPensionerBean extends NamedEntity {


    private static final long serialVersionUID = 6488554680486128454L;
    private Long parentBusinessClientId;
    private boolean lockResId;
    private boolean lockNin;
    private Long nokStateId;
    private Long nokCityId;
    private Employee employee;
    private Pensioner pensioner;
    private HiringInfo hiringInfo;
    private String accountNumber;
    private String activeEmployeeId;
    private String bvnNo;
    private PaymentMethodInfo paymentMethodInfo;
    private boolean noMdaFlag;
    private boolean bankNotFound;
    private Long mdaId;
    private int mapId;
    private int objectInd;
    private int useDefPension;
    private int useDefGratuity;
    private boolean belongsToASchool;
    private boolean schoolNotFound;
    private Long schoolInstId;
    private SalaryInfo salaryInfo;
    private List<NamedEntity> schoolInfoList;
    private String mdaName;
    private Long lgaId;
    private List<NamedEntity> lgaList;
    private List<MdaInfo> mdaList;
    private List<BankInfo> bankList;
    private Long bankId;
    private List<BankBranch> bankBranchList;
    private Long bankBranchId;
    private List<?> salaryTypeList;
    private List<SalaryInfo> levelAndStepList;
    private boolean stateNotFoundFlag;
    private Long stateId;
    private List<LGAInfo> lgaInfoList;
    private List<State> stateInfoList;
    private Long salaryTypeId;
    private String levelAndStepStr;
    private boolean noNextOfKinState;
    private boolean noNextOfKin;
    private NextOfKin nextOfKin;
    private boolean hasNextOfKin;
    private String noPensionerMsg;
    private List<MaritalStatus> maritalStatusList;
    private List<Religion> religionList;
    private int totalLengthOfService;
    private int totalLengthOfServiceInYears;
    private double totalEmoluments;
    private String totalEmolumentsStr;
    private String totalPayStr;
    private List<PfaInfo> pfaInfoList;
    private Long pfaId;
    private Long relationTypeId;
    private List<RelationshipType> relationshipTypeList;
    private int createLaterInd;
    private String employeeId;
    private String mdaTypeName;
    private Long levelAndStepInd;
    private int calculateGratuityInd;
    private boolean calculateGratuity;
    private boolean consolidatedIndBind;
    private List<Title> epmTitleList;
    private LocalDate gratuityEffectiveDate;
    private boolean recalculation;
    private int calculatePensionInd;
    private boolean calculatePensions;
    private String consolidatedIndStr;
    private boolean showConsolidatedIndRow;
    private String totalLengthOfServiceStr;
    private int finalLengthOfService;
    private boolean apportionment;
    private boolean lockAll;
    private Long salaryInfoId;
    private Long stateOfOriginId;
    private List<City> cityList;
    private Long cityId;
    private Long tcoId;
    private Long religionId;
    private Long maritalStatusId;
    private Long titleId;
    private Long pensionerTypeId;

    public boolean isCalculatePensions() {
        this.calculatePensions = this.calculatePensionInd == 1;
        return calculatePensions;
    }

    public boolean isCalculateGratuity() {
        this.calculateGratuity = this.calculateGratuityInd == 1;
        return calculateGratuity;
    }
}
