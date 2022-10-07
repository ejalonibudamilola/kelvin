package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportRedirect {

    private String url;

    public ReportRedirect(String url){
        this.url = url;
    }
}
