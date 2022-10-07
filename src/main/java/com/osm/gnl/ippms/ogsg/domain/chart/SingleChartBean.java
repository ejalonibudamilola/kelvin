package com.osm.gnl.ippms.ogsg.domain.chart;

import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SingleChartBean {
    private String chartTitle;
    private String labelForCD;
    private String verticalLabel;
    private String url;
    private List<String> barXAxis;
    private int barXAxisSize;
    private List<String> barYAxis;
    private int barYAxisSize;
    private List<NamedEntity> monthList;
    private List<NamedEntity> yearList;
    private int runMonth;
    private int runYear;
}

