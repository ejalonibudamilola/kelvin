package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.BiometricInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryType;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class EmployeeHrBean extends NamedEntity {
    private static final long serialVersionUID = 5846927448180006358L;
    private List<Employee> empList = LazyList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(Employee.class));
    private List<Pensioner> penList = LazyList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(Pensioner.class));

    private List<Department> deptList;

    private List<SalaryInfo> gradeLevelList;
    private List<SalaryInfo> salaryStructureList;
    private List<SalaryType> salaryTypeList;

    //LGA Stuffs
    private Long cadreInstId;
    private String fileNo;
    private Long rankInstId;
    private Long oldRankInstId;

    private AbstractEmployeeEntity employee;
    private Pensioner pensioner;
    private int gradeLevelInstId;
    private String residenceId;
    private String residenceIdStr;
    private String ninStr;
    private String nin;

    private List<?> parentObjectList;
    private LocalDate refDate;
    private String refNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String employeeId;
    private int currentPolicyId;
    private String parentObjectName;
    private String parentObjectParentName;
    private int parentObjectId;
    private Long deptId;
    private int medicalId;
    private int gradeLevelId;
    private int institutionId;
    private int respAllowInd;
    private String hrStatus;
    private String showMedical;
    private String showMedicalDr;

    private String showRespAllow;
    private String paymentTypeRef;
    private String showInstitution;

    private Long salaryStructureId;

    private Long salaryTypeId;

    private String showNormalPay;
    private boolean normalPay;
    private boolean medical;
    private boolean institution;
    private String showForConfirm;
    private String showGradeLevelDesignation;
    private String showLevelAndSteps;
    private String showDesignation;
    private String salaryScaleName;
    private boolean confirm;
    private boolean warningIssued;
    private boolean delete;
    private boolean mapUsed;
    private boolean salaryStructureSelectable;
    private Long mdaId;
    private String schoolName;
    private String mdaName;
    private Long parentClientId;
    private boolean fail;
    private boolean response;
    private String photo;
    private String signature;
    private Long biometricId;
    private Long oldDesignationId;
    private BiometricInfo biometricInfoBean;


    private BusinessCertificate roleBean;


}