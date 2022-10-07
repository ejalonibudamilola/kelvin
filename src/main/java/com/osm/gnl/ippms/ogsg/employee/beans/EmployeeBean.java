package com.osm.gnl.ippms.ogsg.employee.beans;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.GratuityInfo;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PaymentMethodInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeBean implements Serializable {

    private Long id;
    private AbstractEmployeeEntity employee;
    private HiringInfo hiringInfo;
    private PaymentMethodInfo paymentMethodInfo;
    private String employeeType;
    private String showSuspensionRow;
    private String contractType;
    private String showContractRow;
    private String paySchedule;
    private String payRate;
    private String buttonRowInd;
    private String hireDate;
    private String birthDate;
    private String cityStateZip;
    private String dispClass;
    private String dispPayMethod;
    private String dispName;
    private String empDed;
    private String compCont;
    private String specAllow;
    private String garnishment;
    private String payMethodType;
    private String nextPromotionDateStr;
    private boolean hasSchoolInformation;
    private boolean hasPayInformation;
    private String schoolName;
    private AbstractPaycheckEntity lastPayCheck;
    private String bankName;
    private String bankBranchName;
    private String accountNumber;
    private String nextOfKinMessage;
    private String floatMessage;
    private boolean hasNextOfKin;
    private String terminatedBy;
    private String legacyEmployeeId;
    private String suspensionReasonStr;
    private String suspendedBy;
    private boolean hasPromotionHistory;
    private boolean readOnly;
    private String bvnNo;
    private String bvnNoMask;
    private String nin;
    private String ninMask;
    private String refNumber;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String mdaName;
    private String titleField;
    private String salaryScaleName;

    private boolean hasGratuityPayments;
    private GratuityInfo gratuityInfo;

    public boolean isNewEntity(){
        return this.id == null;
    }

    public String getBvnNoMask() {
        if(StringUtils.trimToEmpty(this.bvnNo).length() == 11) {
            this.bvnNoMask = this.bvnNo.substring(1,bvnNo.length() - 3);
            this.bvnNoMask += "XXXX";
        }
        return bvnNoMask;
    }
}