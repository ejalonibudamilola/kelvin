package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PensionBean implements Comparable<PensionBean>{

    private Integer id;
    private String category;
    private Long totalPensioners;
    private Double amount;
    private Long cumulativeTotalPensioners;
    private Double cumulativeTotalAmount;
    private String firstName;
    private String lastName;
    private String initials;
    private String employeeId;
    private String lgaName;
    private Employee employee;

    public PensionBean(String category, Long totalPensioners, Double amount, Long cumulativeTotalPensioners, Double cumulativeTotalAmount) {
        this.category = category;
        this.totalPensioners = totalPensioners;
        this.amount = amount;
        this.cumulativeTotalPensioners = cumulativeTotalPensioners;
        this.cumulativeTotalAmount = cumulativeTotalAmount;
        setId(category);
    }

    public void setId(String idStr){
        /**
         * This is assuming the String will come as '1-12' etc.
         */
        if(idStr.equals("0")){
            this.id = Integer.parseInt(idStr);
        }
        else if(idStr.equals("1-4999")){
            this.id = Integer.parseInt(idStr.substring(0,idStr.indexOf("-"))) + Integer.parseInt(idStr.substring(idStr.lastIndexOf("-")+ 1));
        }
        else{
            this.id = Integer.parseInt(idStr.substring(0,idStr.indexOf("-"))) + Integer.parseInt(idStr.substring(idStr.lastIndexOf("-")+ 1));
        }
 }
    @Override
    public int compareTo(PensionBean pO)
    {
        return this.getCategory().compareToIgnoreCase(pO.getCategory());
    }
}
