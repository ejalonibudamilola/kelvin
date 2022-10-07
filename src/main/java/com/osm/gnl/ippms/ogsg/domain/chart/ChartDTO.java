package com.osm.gnl.ippms.ogsg.domain.chart;


import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class ChartDTO extends AbstractEmployeeEntity {

    private int level;
    private int level2;
    private int step;
    private int step2;
    private String salaryTypeName2;
    private double pay;
    private double specialPay;
    private double totalAnnualSalary;
    private String totalAnnualSalaryStr;


    @Override
    public SchoolInfo getSchoolInfo() {
        return null;
    }

    @Override
    public String getParentObjectName() {
        return null;
    }

    @Override
    public boolean isSchoolStaff() {
        return false;
    }

    @Override
    public boolean isPensioner() {
        return false;
    }

    @Override
    protected Object getChildEntity() {
        return null;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long pId) {

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return false;
    }
}
