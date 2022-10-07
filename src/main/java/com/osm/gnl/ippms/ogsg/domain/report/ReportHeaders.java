package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;


@Getter
@Setter
public class ReportHeaders {

    private String headerName;
    private int totalInd;

    public ReportHeaders(String  headerName, int totalInd) {
        this.headerName = headerName;
        this.totalInd = totalInd;
    }
}
