package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Getter
@Setter
public class GrossSummary implements Comparable<GrossSummary>{


    public String lgaName;
    public Double grossPay;
    public String grossPayStr;
    public Long totalStaff;
    protected DecimalFormat df = new DecimalFormat("#,##0.00");

    public GrossSummary(String lgaName, Double grossPay, Long totalStaff) {
        this.lgaName = lgaName;
        this.grossPay = grossPay;
        this.totalStaff = totalStaff;
    }

    public String getGrossPayStr() {
        this.grossPayStr = IConstants.naira + this.df.format(getGrossPay());
        return this.grossPayStr;
    }

    @Override
    public int compareTo(GrossSummary pO)
    {
        return this.getLgaName().compareToIgnoreCase(pO.getLgaName());
    }

}
